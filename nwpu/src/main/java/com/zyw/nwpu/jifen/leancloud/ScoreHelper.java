package com.zyw.nwpu.jifen.leancloud;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.service.SignService.OnGetRankListListener;
import com.zyw.nwpu.service.SignService.SignBean;
import com.zyw.nwpulib.utils.CommonUtil;

import android.text.TextUtils;

/**
 * 积分服务类
 * 
 * @author Rocket
 *
 */
public class ScoreHelper {

	// 表名和字段名
	private static final String SCORE_DETAIL_CLASSNAME = "ScoreDetail";
	private static final String SCORE_SUMMARY_CLASSNAME = "ScoreSummary";
	private static final String SCORE_CONFIG_CLASSNAME = "ScoreConfig";

	// 商品相关
	private static final String PRODUCTS_CLASSNAME = "Products";
	private static final String PRODUCTS_NAME = "name";
	private static final String PRODUCTS_IMG = "image";
	private static final String PRODUCTS_IS_ONSALE = "isOnSale";
	private static final String PRODUCTS_ORDER = "order";
	private static final String PRODUCTS_SCORE = "score";
	private static final String PRODUCTS_DISCOUNT = "discount";
	private static final String PRODUCTS_DESCRIPTION = "description";

	private static final int PRODUCTS_IMG_MAX_SIZE = 150;// 商品图片的最大尺寸，如果觉得图片不够清晰，则将此数值调大
	private static final int AVATAR_IMG_MAX_SIZE = 60;// 用户头像图片的最大尺寸，如果觉得图片不够清晰，则将此数值调大

	/**
	 * 获取商品列表的回调接口
	 * 
	 * @author Rocket
	 *
	 */
	public interface OnGetProductsCallback {
		// 获取失败的回调函数，参数为错误提示
		public void onFailed(String errorTip);

		// 获取成功的回调函数，参数是商品列表
		public void onSuccess(List<Product> imageList);
	}

	/**
	 * 获取商品列表
	 * 
	 * @param callback
	 *            回调函数
	 */
	public static void getProducts(final OnGetProductsCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>(PRODUCTS_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.addAscendingOrder(PRODUCTS_SCORE);
		// query.addAscendingOrder(PRODUCTS_ORDER);// 按order的顺序，增序排列
		query.whereEqualTo(PRODUCTS_IS_ONSALE, true);// 只加载可以显示的
		query.setLimit(100);// 最多显示100件商品
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// 请求是否出错
				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				// 返回值是否为空
				if (arg0 == null || arg0.size() == 0) {
					callback.onFailed("服务器错误：商品列表为空");
					return;
				}

				List<Product> productList = new ArrayList<Product>();
				for (int i = 0; i < arg0.size(); i++) {
					Product entity = new Product();
					entity.setProductId(arg0.get(i).getObjectId());
					entity.setName(arg0.get(i).getString(PRODUCTS_NAME));
					entity.setDescription(arg0.get(i).getString(PRODUCTS_DESCRIPTION));

					double discount = arg0.get(i).getDouble(PRODUCTS_DISCOUNT);
					if (Math.abs(discount - 1) < 0.0001)
						entity.setIsDiscount(false);
					else
						entity.setIsDiscount(true);
					entity.setDiscount(discount);

					entity.setScore(arg0.get(i).getInt(PRODUCTS_SCORE));

					AVFile imageFile = arg0.get(i).getAVFile(PRODUCTS_IMG);
					if (imageFile != null) {
						String url = imageFile.getThumbnailUrl(false, PRODUCTS_IMG_MAX_SIZE, PRODUCTS_IMG_MAX_SIZE, 50,
								"jpg");
						entity.setImageUrl(url);
					}

					productList.add(entity);
				}

				callback.onSuccess(productList);
			}
		});
	}

	public interface BuyProductCallback {
		public void onSuccess();

		public void onFailure(String errTip);
	}

	/**
	 * 购买/兑换某个商品
	 * 
	 * @param productId
	 * @param callback
	 */
	public static final void buyProduct(final String productId, final BuyProductCallback callback) {
		if (callback == null)
			return;
		if (TextUtils.isEmpty(productId)) {
			callback.onFailure("所选的礼品不存在！");
			return;
		}

		getUserScoreSummary(new GetUserScoreSummaryCallback() {

			@Override
			public void onSuccess(String usrNickname, String usrStudentId, String avatarUrl, final int totalScore) {

				if (totalScore < 0) {
					callback.onFailure("积分不足！");
					return;
				}

				// 查询到商品的价格
				AVQuery<AVObject> query = new AVQuery<AVObject>(PRODUCTS_CLASSNAME);
				query.getInBackground(productId, new GetCallback<AVObject>() {
					@Override
					public void done(AVObject arg0, AVException arg1) {
						if (arg1 != null) {
							callback.onFailure("兑换礼品失败：" + arg1.getLocalizedMessage());
							return;
						}

						if (arg0 == null) {
							callback.onFailure("所选的礼品不存在！");
							return;
						}

						String productName = arg0.getString("name");
						final int productScore = arg0.getInt("score");

						// 比较积分
						if (totalScore < productScore) {
							callback.onFailure("积分不足！");
							return;
						}

						// 积分足够，可以购买
						// 添加购买记录
						addScoreRecord("兑换礼品:" + productName, -productScore, new AddScoreRecordCallback() {

							@Override
							public void onSuccess() {
								callback.onSuccess();
							}

							@Override
							public void onFailure(String errTip) {
								callback.onFailure(errTip);
							}
						});
					}
				});
			}

			@Override
			public void onFailure(String errTip) {
				callback.onFailure(errTip);
			}
		});

	}

	public interface AddScoreRecordCallback {
		public void onSuccess();

		public void onFailure(String errTip);
	}

	/**
	 * 增加积分
	 * 
	 * @param description
	 *            操作名，如 "阅读资讯" ,"发布帖子"，"评论帖子"等
	 * @param score
	 * @param callback
	 */
	public static final void addScore(final String description, final int score,
			final AddScoreRecordCallback callback) {

		if (callback == null)
			return;

		if (AVUser.getCurrentUser() == null) {
			callback.onFailure("用户未登录!");
			return;
		}
		// String currentDate = CommonUtil.DateUtils.getFormatTime("yyyyMMdd");

		// 获取日期
		x.http().get(new RequestParams("http://222.24.192.175/npulife_api/get_date.php"), new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				callback.onFailure("增加积分失败：" + arg0.getLocalizedMessage());
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(String currentDate) {
				// 查询当天是否进行了相同操作
				AVQuery<AVObject> query = new AVQuery<AVObject>(SCORE_DETAIL_CLASSNAME);
				query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
				query.whereEqualTo("description", description);
				query.whereEqualTo("user", AVUser.getCurrentUser());
				query.whereEqualTo("date", currentDate);
				query.findInBackground(new FindCallback<AVObject>() {

					@Override
					public void done(List<AVObject> arg0, AVException arg1) {
						if (arg1 != null) {
							callback.onFailure("增加积分失败：" + arg1.getLocalizedMessage());
							return;
						}

						if (arg0 == null || arg0.size() == 0) {
							// 当天没有进行相同操作，可以增加积分
							addScoreRecord(description, score, callback);
							return;
						}

						// 当天已经进行了相同操作，不可增加积分
						callback.onFailure(description + " 今日积分已经领取");
					}
				});
			}
		});
	}

	/**
	 * 增加一条积分记录，包括增加积分和兑换礼品
	 * 
	 * @param callback
	 */
	private static final void addScoreRecord(final String description, final int score,
			final AddScoreRecordCallback callback) {

		if (callback == null)
			return;

		if (AVUser.getCurrentUser() == null) {
			callback.onFailure("用户未登录!");
			return;
		}
		// String currentDate = CommonUtil.DateUtils.getFormatTime("yyyyMMdd");
		// 获取日期
		x.http().get(new RequestParams("http://222.24.192.175/npulife_api/get_date.php"), new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {

			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(String currentDate) {
				AVObject obj = new AVObject(SCORE_DETAIL_CLASSNAME);
				obj.put("user", AVUser.getCurrentUser());
				obj.put("score", score);
				obj.put("description", description);
				obj.put("date", currentDate);
				obj.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException arg0) {
						if (arg0 != null) {
							callback.onFailure("添加积分记录失败：" + arg0.getLocalizedMessage());
							return;
						}

						// 添加成功
						changeUserTatalScore(score, new ScoreHelper.GetUserScoreSummaryCallback() {

							@Override
							public void onSuccess(String usrNickname, String usrStudentId, String avatarUrl,
									int totalScore) {
								callback.onSuccess();
							}

							@Override
							public void onFailure(String errTip) {
								callback.onFailure(errTip);
							}
						});
					}
				});
			}
		});
	}

	public interface GetScoreDetailCallback {
		public void onSuccess(List<ScoreDetail> scoreDetailList);

		public void onFailure(String errTip);
	}

	/**
	 * 获取积分详情
	 * 
	 * @param callback
	 */
	public static final void getScoreDetail(int limit, int skip, final GetScoreDetailCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>(SCORE_DETAIL_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.addDescendingOrder("createdAt");

		query.setLimit(limit);
		query.setSkip(skip);

		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("获取积分详情失败：" + arg1.getLocalizedMessage());
					return;
				}

				List<ScoreDetail> scoreDetailList = new ArrayList<>();
				if (arg0 == null || arg0.size() == 0) {
					callback.onSuccess(scoreDetailList);
				}

				for (int i = 0; i < arg0.size(); i++) {
					ScoreDetail item = new ScoreDetail();
					item.setDescription(arg0.get(i).getString("description"));
					item.setScore(arg0.get(i).getInt("score"));
					item.setDate(arg0.get(i).getString("date"));
					scoreDetailList.add(item);
				}

				callback.onSuccess(scoreDetailList);
			}
		});
	}

	public interface GetPurchaseRecordCallback {
		public void onSuccess();

		public void onFailure(String errTip);
	}

	/**
	 * 获取购买/兑换记录
	 */
	public static final void getPurchaseRecord(final GetScoreDetailCallback callback) {

		AVQuery<AVObject> query = new AVQuery<AVObject>(SCORE_DETAIL_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);

		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.whereLessThan("score", 0);// 分值为负，则为购买/兑换礼品

		query.addDescendingOrder("createdAt");
		query.setLimit(1000);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("获取兑换礼品记录失败：" + arg1.getLocalizedMessage());
					return;
				}

				List<ScoreDetail> scoreDetailList = new ArrayList<>();
				if (arg0 == null || arg0.size() == 0) {
					callback.onSuccess(scoreDetailList);
				}

				for (int i = 0; i < arg0.size(); i++) {
					ScoreDetail item = new ScoreDetail();
					item.setDescription(arg0.get(i).getString("description"));
					int score = arg0.get(i).getInt("score");
					item.setScore(score);
					item.setDate(arg0.get(i).getString("date"));
					scoreDetailList.add(item);
				}

				callback.onSuccess(scoreDetailList);
			}
		});
	}

	/** 用户相关 **************************************************************************************/

	public interface GetUserScoreSummaryCallback {
		/**
		 * 获取用户积分数据汇总信息
		 * 
		 * @param usrNickname
		 *            用户昵称
		 * @param usrStudentId用户学号
		 * @param avatarUrl
		 *            用户头像url
		 * @param totalScore
		 *            总积分值
		 */
		public void onSuccess(String usrNickname, String usrStudentId, String avatarUrl, int totalScore);

		public void onFailure(String errTip);
	}

	/**
	 * 获取用户积分汇总信息
	 * 
	 * @param callback
	 */
	public static final void getUserScoreSummary(final GetUserScoreSummaryCallback callback) {
		if (callback == null)
			return;
		if (AVUser.getCurrentUser() == null) {
			callback.onFailure("用户未登录！");
			return;
		}

		// 用户个人信息
		final String nickname = AVUser.getCurrentUser().getString(XUser.NICKNAME);
		final String studentId = AVUser.getCurrentUser().getString(XUser.STUDENTID);
		String imgUrl = "";
		AVFile img = AVUser.getCurrentUser().getAVFile(XUser.HEADIMG);
		if (img != null) {
			imgUrl = img.getThumbnailUrl(false, AVATAR_IMG_MAX_SIZE, AVATAR_IMG_MAX_SIZE, 50, "jpg");
		}

		final String avatarUrl = imgUrl;

		AVQuery<AVObject> query = new AVQuery<AVObject>(SCORE_SUMMARY_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.whereEqualTo("user", AVUser.getCurrentUser());// 只加载可以显示的
		query.setLimit(1);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("获取用户积分失败：" + arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					// 向后台保存一份数据
					AVObject obj = new AVObject(SCORE_SUMMARY_CLASSNAME);
					obj.put("user", AVUser.getCurrentUser());
					obj.put("score", 0);
					obj.saveInBackground();

					callback.onSuccess(nickname, studentId, avatarUrl, 0);
					return;
				}

				AVObject obj = arg0.get(0);
				int score = obj.getInt("score");
				callback.onSuccess(nickname, studentId, avatarUrl, score);
			}
		});
	}

	/**
	 * 修改用户总积分
	 * 
	 * @param deltaScore
	 *            增加的积分值
	 * @param callback
	 */
	private static final void changeUserTatalScore(final int deltaScore, final GetUserScoreSummaryCallback callback) {

		if (callback == null)
			return;
		if (AVUser.getCurrentUser() == null) {
			callback.onFailure("用户未登录！");
			return;
		}

		// 用户个人信息
		final String nickname = AVUser.getCurrentUser().getString(XUser.NICKNAME);
		final String studentId = AVUser.getCurrentUser().getString(XUser.STUDENTID);
		String imgUrl = "";
		AVFile img = AVUser.getCurrentUser().getAVFile(XUser.HEADIMG);
		if (img != null) {
			imgUrl = img.getThumbnailUrl(false, AVATAR_IMG_MAX_SIZE, AVATAR_IMG_MAX_SIZE, 50, "jpg");
		}

		final String avatarUrl = imgUrl;

		// 先在汇总表中查询
		AVQuery<AVObject> query = new AVQuery<AVObject>(SCORE_SUMMARY_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.whereEqualTo("user", AVUser.getCurrentUser());// 只加载可以显示的
		query.setLimit(1);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("查询用户积分失败：" + arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					// 向后台保存一份数据
					AVObject obj = new AVObject(SCORE_SUMMARY_CLASSNAME);
					obj.put("user", AVUser.getCurrentUser());
					obj.put("score", deltaScore);
					obj.saveInBackground(new SaveCallback() {
						@Override
						public void done(AVException arg0) {
							if (arg0 != null) {
								callback.onFailure("修改用户总积分失败：" + arg0.getLocalizedMessage());
								return;
							}
							callback.onSuccess(nickname, studentId, avatarUrl, 0);
						}
					});

					return;
				}

				final AVObject obj = arg0.get(0);
				obj.increment("score", deltaScore);
				obj.saveInBackground(new SaveCallback() {

					@Override
					public void done(AVException arg0) {
						if (arg0 != null) {
							callback.onFailure("修改用户总积分失败：" + arg0.getLocalizedMessage());
							return;
						}

						callback.onSuccess(nickname, studentId, avatarUrl, obj.getInt("score"));
					}
				});
			}
		});
	}

	public interface GetScoreRankCallback {
		public void onFailure(String errTip);

		public void onSuccess(List<SignBean> userList);
	}

	public static final void getScoreRank(final int skip, final OnGetRankListListener callback) {
		if (callback == null)
			return;

		// 在汇总表中查询
		AVQuery<AVObject> query = new AVQuery<AVObject>(SCORE_SUMMARY_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.setLimit(20);
		query.setSkip(skip);
		query.addDescendingOrder("score");
		query.addAscendingOrder("updatedAt");// 按更新时间升序排列
		query.include("user");
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("查询积分排名失败：" + arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					callback.onFailure("未查询到积分排名");
					return;
				}

				List<SignBean> rankList = new ArrayList<SignBean>();

				for (int i = 0; i < arg0.size(); i++) {

					int signTimes = arg0.get(i).getInt("score");// 这里实际上是分数

					AVUser user = arg0.get(i).getAVUser("user");
					if (user != null) {
						SignBean bean = new SignBean();
						bean.setSignTimes(signTimes);

						bean.setUserObjId(user.getObjectId());
						bean.setStudentId(user.getString(XUser.STUDENTID));
						bean.setNickname(user.getString(XUser.NICKNAME));
						if (user.getAVFile(XUser.HEADIMG) != null) {

							String avatar = user.getAVFile(XUser.HEADIMG).getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
									Const.HEADIMAGE_SIZE, 50, "jpg");
							bean.setAvatar(avatar);
						}
						rankList.add(bean);
					}
				}

				callback.onSuccess(rankList);
			}
		});
	}
}
