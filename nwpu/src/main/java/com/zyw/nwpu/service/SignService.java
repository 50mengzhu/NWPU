package com.zyw.nwpu.service;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.utils.CommonUtil;

import android.text.TextUtils;

/**
 * 
 * 签到服务类，有四个静态函数，用回调接口返回结果
 * 
 * 
 * 1、checkSign 检查当前用户今日是否已经签到
 * 
 * 2、sign 签到
 * 
 * 3、getCurrentUserSignStatistics 获取当前用户的签到统计，回调接口里返回三个数字，分别是总签到数，本月签到数，签到数排名
 * 
 * 4、getRankList 获取签到排名表，回调接口返回列表
 * 
 * @author Rocket
 *
 */
public class SignService {

	private static final String SIGH_TABLENAME = "QianDao";// 签到表
	private static final String SIGH_STATISTICS_TABLENAME = "QianDaoStatistics";// 签到统计表
	private static final String SIGH_RULES = "QianDaoRules";// 签到规则描述

	public interface BaseListener {
		/**
		 * 检查签到失败
		 * 
		 * @param error
		 *            错误信息
		 */
		public void onFailure(String error);
	}

	public interface OnCheckSignListener extends BaseListener {

		/**
		 * 检查签到成功
		 * 
		 * @param isSigned
		 *            是否已经签到
		 */
		public void onSuccess(boolean isSigned);
	}

	public interface OnGetSignRules extends BaseListener {
		public void onGet(String rules);
	}

	public interface OnSignListener extends BaseListener {
		public void onSuccess();
	}

	private interface OnGetSignStatisticsListener extends BaseListener {
		public void onSuccess(int total, int thisMonth);
	}

	public interface OnGetRankListListener extends BaseListener {
		public void onSuccess(List<SignBean> list);
	}

	public interface OnGetCurrentUserSignStatisticsListener extends BaseListener {
		public void onSuccess(int total, int thisMonth, int rank, String nickname, String avatar);
	}

	/**
	 * 签到实体类
	 * 
	 * @author Rocket
	 *
	 */
	public static class SignBean {
		private String nickname = "";
		private String avatar = "";
		private int signTimes = 0;
		private String studentId = "";// 学号
		private String userObjId = "";

		public SignBean() {
		}

		public SignBean(String nickname, String avatar, int signTimes, String studentId, String userObjId) {
			this.nickname = nickname;
			this.avatar = avatar;
			this.signTimes = signTimes;
			this.studentId = studentId;
			this.userObjId = userObjId;
		}

		public String getStudentId() {
			return studentId;
		}

		public void setStudentId(String studentId) {
			this.studentId = studentId;
		}

		public String getUserObjId() {
			return userObjId;
		}

		public void setUserObjId(String userObjId) {
			this.userObjId = userObjId;
		}

		/**
		 * 获取用户昵称
		 * 
		 * @return
		 */
		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		/**
		 * 获取签到者的头像
		 * 
		 * @return
		 */
		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		/**
		 * 获取签到次数
		 * 
		 * @return
		 */
		public int getSignTimes() {
			return signTimes;
		}

		public void setSignTimes(int signTimes) {
			this.signTimes = signTimes;
		}
	}

	public static final void getSignRules(final OnGetSignRules listener) {
		if (listener == null)
			return;
		AVQuery<AVObject> query = new AVQuery<AVObject>(SIGH_RULES);
		query.addDescendingOrder("updatedAt");
		query.whereEqualTo("isEnable", true);
		query.setLimit(1);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					listener.onFailure(arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == null || arg0.size() == 0) {
					return;
				}
			}
		});
	}

	/**
	 * 检查当天是否签到过
	 * 
	 * @param listener
	 */
	public static final void checkSign(final OnCheckSignListener listener) {
		if (listener == null)
			return;

		if (AVUser.getCurrentUser() == null) {
			listener.onFailure("用户未登录");
			return;
		}

		String currentDate = CommonUtil.DateUtils.getFormatTime("yyyy-MM-dd");
		AVQuery<AVObject> query = new AVQuery<AVObject>(SIGH_TABLENAME);
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.whereEqualTo("date", currentDate);
		query.countInBackground(new CountCallback() {

			@Override
			public void done(int arg0, AVException arg1) {
				if (arg1 != null) {
					listener.onFailure(arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == 0)
					listener.onSuccess(false);
				else
					listener.onSuccess(true);
			}
		});
	}

	/**
	 * 签到
	 * 
	 * @param listener
	 */
	public static final void sign(final OnSignListener listener) {
		if (listener == null)
			return;

		if (AVUser.getCurrentUser() == null) {
			listener.onFailure("用户未登录");
			return;
		}

		// 再次检查是否今日已经签到过
		checkSign(new OnCheckSignListener() {

			@Override
			public void onFailure(String error) {
				listener.onFailure(error);
			}

			@Override
			public void onSuccess(boolean isSigned) {
				if (isSigned) {
					listener.onSuccess();
					return;
				}

				// 签到
				String currentDate = CommonUtil.DateUtils.getFormatTime("yyyy-MM-dd");

				AVObject obj = new AVObject(SIGH_TABLENAME);
				obj.put("user", AVUser.getCurrentUser());
				obj.put("date", currentDate);
				obj.saveInBackground(new SaveCallback() {

					@Override
					public void done(AVException arg0) {
						if (arg0 != null) {
							listener.onFailure(arg0.getLocalizedMessage());
							return;
						}

						// 统计表数字加1
						// 当前月份
						final String currentMonth = CommonUtil.DateUtils.getFormatTime("yyyy-MM");

						AVQuery<AVObject> statisticsQuery = new AVQuery<AVObject>(SIGH_STATISTICS_TABLENAME);
						statisticsQuery.whereEqualTo("user", AVUser.getCurrentUser());
						statisticsQuery.whereEqualTo("month", currentMonth);
						statisticsQuery.findInBackground(new FindCallback<AVObject>() {

							@Override
							public void done(List<AVObject> arg0, AVException arg1) {
								if (arg1 != null) {
									listener.onFailure(arg1.getLocalizedMessage());
									return;
								}
								if (arg0 == null || arg0.size() == 0) {
									AVObject statistics = new AVObject(SIGH_STATISTICS_TABLENAME);
									statistics.put("user", AVUser.getCurrentUser());
									statistics.put("month", currentMonth);
									statistics.put("times", 1);
									statistics.saveInBackground(new SaveCallback() {
										@Override
										public void done(AVException arg0) {
											if (arg0 != null) {
												listener.onFailure(arg0.getLocalizedMessage());
												return;
											}
											listener.onSuccess();
										}
									});
									return;
								} else {
									AVObject statistics = arg0.get(0);
									statistics.increment("times");
									statistics.saveInBackground(new SaveCallback() {
										@Override
										public void done(AVException arg0) {
											if (arg0 != null) {
												listener.onFailure(arg0.getLocalizedMessage());
												return;
											}
											listener.onSuccess();
										}
									});
								}
							}
						});

						listener.onSuccess();
					}
				});

			}
		});
	}

	/**
	 * 获取当前用户的签到统计信息
	 * 
	 * @param listener
	 */
	private static final void getCurrentUserSignNum(final OnGetSignStatisticsListener listener) {
		if (listener == null)
			return;
		if (AVUser.getCurrentUser() == null) {
			listener.onFailure("用户未登录");
			return;
		}

		AVQuery<AVObject> query = new AVQuery<AVObject>(SIGH_STATISTICS_TABLENAME);
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.setLimit(1000);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					listener.onFailure(arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					listener.onSuccess(0, 0);
					return;
				}

				int total = 0;
				int currentMonthNum = 0;

				// 当前月份
				final String currentMonth = CommonUtil.DateUtils.getFormatTime("yyyy-MM");
				for (int i = 0; i < arg0.size(); i++) {
					AVObject obj = arg0.get(i);
					int times = obj.getInt("times");
					String month = obj.getString("month");
					total += times;
					if (TextUtils.equals(month, currentMonth)) {
						currentMonthNum = times;
					}
				}
				listener.onSuccess(total, currentMonthNum);
			}
		});
	}

	/**
	 * 获取当前用户的签到排名
	 * 
	 * @param currentUserSignNum
	 * @param listener
	 */
	public static final void getCurrentUserSignStatistics(final OnGetCurrentUserSignStatisticsListener listener) {
		if (listener == null)
			return;

		getCurrentUserSignNum(new OnGetSignStatisticsListener() {

			@Override
			public void onFailure(String error) {
				listener.onFailure(error);
			}

			@Override
			public void onSuccess(int total, int thisMonth) {
				final int totalNum = total;
				final int currentMonthNum = thisMonth;

				// 当前月份
				String currentMonth = CommonUtil.DateUtils.getFormatTime("yyyy-MM");

				AVQuery<AVObject> query = new AVQuery<AVObject>(SIGH_STATISTICS_TABLENAME);
				query.whereEqualTo("month", currentMonth);
				query.whereGreaterThan("times", currentMonthNum);
				query.countInBackground(new CountCallback() {

					@Override
					public void done(int arg0, AVException arg1) {
						if (arg1 != null) {
							listener.onFailure(arg1.getLocalizedMessage());
							return;
						}
						String nickname = AVUser.getCurrentUser().getString(XUser.NICKNAME);
						String avatar = "";
						if (AVUser.getCurrentUser().getAVFile(XUser.HEADIMG) != null) {
							avatar = AVUser.getCurrentUser().getAVFile(XUser.HEADIMG).getThumbnailUrl(false,
									Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE, 50, "jpg");
						}
						listener.onSuccess(totalNum, currentMonthNum, arg0 + 1, nickname, avatar);
					}
				});
			}
		});
	}

	/**
	 * 获取签到排名表
	 * 
	 * @param skip
	 * @param listener
	 */
	public static final void getRankList(int skip, final OnGetRankListListener listener) {
		if (listener == null)
			return;

		// 当前月份
		String currentMonth = CommonUtil.DateUtils.getFormatTime("yyyy-MM");
		AVQuery<AVObject> query = new AVQuery<AVObject>(SIGH_STATISTICS_TABLENAME);
		query.addDescendingOrder("times");
		query.addAscendingOrder("updatedAt");// 按更新时间升序排列
		// query.addDescendingOrder("updatedAt");
		query.setSkip(skip);
		query.setLimit(30);
		query.include("user");
		query.whereEqualTo("month", currentMonth);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					listener.onFailure(arg1.getLocalizedMessage());
					return;
				}
				List<SignBean> list = new ArrayList<SignBean>();
				if (arg0 == null || arg0.size() == 0)
					listener.onSuccess(list);
				else {
					for (int i = 0; i < arg0.size(); i++) {
						SignBean bean = new SignBean();
						bean.setSignTimes(arg0.get(i).getInt("times"));
						AVUser user = arg0.get(i).getAVUser("user");
						if (user != null) {
							bean.setUserObjId(user.getObjectId());
							bean.setStudentId(user.getString(XUser.STUDENTID));
							bean.setNickname(user.getString(XUser.NICKNAME));
							if (user.getAVFile(XUser.HEADIMG) != null) {

								String avatar = user.getAVFile(XUser.HEADIMG).getThumbnailUrl(false,
										Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE, 50, "jpg");
								bean.setAvatar(avatar);
							}
							list.add(bean);
						}
					}
					listener.onSuccess(list);
				}
			}
		});
	}
}
