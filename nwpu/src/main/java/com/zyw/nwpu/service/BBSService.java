package com.zyw.nwpu.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.hardware.Camera.Area;
import android.text.TextUtils;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.AVQuery.CachePolicy;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.model.Comment;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.model.CommentLikePushData;
import com.zyw.nwpulib.model.LikeData;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.model.UserInfo;
import com.zyw.nwpu.service.BBSServiceCallback.AddCommentCallback;
import com.zyw.nwpu.service.BBSServiceCallback.DeleteStatusCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetCommentsListCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetLikeListCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetStatusCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetStatusListCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetTopicListCallback;
import com.zyw.nwpu.service.BBSServiceCallback.RemoveCommentCallback;
import com.zyw.nwpu.service.BBSServiceCallback.ToggleStatusStickCallback;
import com.zyw.nwpu.service.PushHelper.VisitData;
import com.zyw.nwpu.service.UserInfoService.QueryUsersCallback;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

public class BBSService {

	public static final void setStatusStick(final String statusObjId, final ToggleStatusStickCallback callback) {
		AVObject obj = AVObject.createWithoutData(Status.CLASSNAME, statusObjId);
		obj.put(Status.STICK_LEVEL, 1);
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				if (callback == null)
					return;

				if (arg0 != null) {
					callback.onFailed("置顶帖子失败:" + arg0.getLocalizedMessage());
					return;
				}
				callback.onSuccess(true);
			}
		});
	}

	public static final void setStatusUnstick(final String statusObjId, final ToggleStatusStickCallback callback) {
		AVObject obj = AVObject.createWithoutData(Status.CLASSNAME, statusObjId);
		obj.put(Status.STICK_LEVEL, 0);
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				if (callback == null)
					return;

				if (arg0 != null) {
					callback.onFailed("取消置顶帖子失败:" + arg0.getLocalizedMessage());
					return;
				}
				callback.onSuccess(false);
			}
		});
	}

	/**
	 * 获取置顶状态
	 * 
	 * @param cxt
	 * @param callback
	 */
	public static final void getStickStatus(final Context cxt, final String tag, final GetStatusListCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		AVQuery<AVObject> query = new AVQuery<AVObject>(Status.CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.addDescendingOrder(Status.STICK_LEVEL);// 按置顶级别降序排列
		query.include("creator");
		if (!TextUtils.isEmpty(tag))
			query.whereEqualTo(Status.TAG, tag);
		query.whereEqualTo("isShow", true);// 只加载可以显示的
		query.whereNotEqualTo(Status.STICK_LEVEL, 0);// 只筛选置顶状态
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				List<StatusData> mData = new ArrayList<StatusData>();

				if (arg0 != null && arg0.size() != 0) {
					for (int i = 0; i < arg0.size(); i++) {
						Status item = (Status) (arg0.get(i));
						mData.add(BBSService.transformStatusData(cxt, item));
					}
				}
				if (callback != null)
					callback.onSuccess(mData);
			}
		});
	}

	public static final void getStatusesByPosition(final Context cxt, final AVGeoPoint p, final int skipNum,
			final GetStatusListCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		AVQuery<AVObject> query = new AVQuery<AVObject>(Status.CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.include("creator");
		query.setSkip(skipNum);
		query.setLimit(Const.LIMIT_GET_STATUS);
		query.whereEqualTo("isShow", true);// 只加载可以显示的
		query.whereNear(Status.LOCATION, p);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				List<StatusData> mData = new ArrayList<StatusData>();

				if (arg0 != null && arg0.size() != 0) {
					for (int i = 0; i < arg0.size(); i++) {
						Status item = (Status) (arg0.get(i));
						mData.add(BBSService.transformStatusData(cxt, item));
					}
				}
				if (callback != null)
					callback.onSuccess(mData);
			}
		});
	}

	/**
	 * 刷新BBS数据
	 * 
	 * @param cxt
	 * @param callback
	 */
	public static final void getStatuses(final Context cxt, final String tag, final GetStatusListCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		AVQuery<AVObject> query = new AVQuery<AVObject>(Status.CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		if (Const.IS_UPDATE_STRATEGY) {
			// query.orderByDescending("updatedAt");
			query.addDescendingOrder("updatedAt");
		} else {
			// query.orderByDescending("createdAt");
			query.addDescendingOrder("createdAt");
		}
		query.include("creator");
		if (!TextUtils.isEmpty(tag))
			query.whereEqualTo(Status.TAG, tag);
		query.setLimit(Const.LIMIT_GET_STATUS);
		query.whereEqualTo("isShow", true);// 只加载可以显示的
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				List<StatusData> mData = new ArrayList<StatusData>();

				if (arg0 != null && arg0.size() != 0) {
					for (int i = 0; i < arg0.size(); i++) {
						Status item = (Status) (arg0.get(i));
						mData.add(BBSService.transformStatusData(cxt, item));
					}
				}
				if (callback != null)
					callback.onSuccess(mData);
			}
		});
	}

	/**
	 * BBS数据加载更多
	 * 
	 * @param cxt
	 * @param lastDate
	 * @param callback
	 */
	public static final void loadMoreStatuses(final Context cxt, Date lastDate, String tag,
			final GetStatusListCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		AVQuery<AVObject> query = new AVQuery<AVObject>(Status.CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.include("creator");
		query.setLimit(Const.LIMIT_GET_STATUS);
		query.whereEqualTo("isShow", true);
		if (Const.IS_UPDATE_STRATEGY) {
			query.orderByDescending("updatedAt");
			query.whereLessThan("updatedAt", lastDate);
		} else {
			query.orderByDescending("createdAt");
			query.whereLessThan("createdAt", lastDate);
		}

		if (!TextUtils.isEmpty(tag))
			query.whereEqualTo(Status.TAG, tag);

		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}
				List<StatusData> mData = new ArrayList<StatusData>();
				if (arg0 != null && arg0.size() != 0) {
					for (int i = 0; i < arg0.size(); i++) {
						Status item = (Status) (arg0.get(i));
						mData.add(BBSService.transformStatusData(cxt, item));
					}
				}
				if (callback != null)
					callback.onSuccess(mData);
			}
		});
	}

	public static final String TABLE_VISITRECORD = "VisitRecord";

	/**
	 * 添加访问推送
	 * 
	 * @param nickname
	 * @param userObjId
	 * @param tInstallationId
	 */
	public static final void addVisit(AVUser tUser) {
		if (tUser == null)
			return;

		if (AVUser.getCurrentUser() == null) {
			// 保存到数据库
			AVObject obj = new AVObject(TABLE_VISITRECORD);
			obj.put("visitorId", "");
			obj.put("visitorNickname", "");
			obj.put("hostId", tUser.getObjectId());
			obj.put("hostNickname", tUser.getString(XUser.NICKNAME));
			obj.saveInBackground();
			return;
		}

		String nickname = AVUser.getCurrentUser().getString(XUser.NICKNAME);
		String userObjId = AVUser.getCurrentUser().getObjectId();

		// 访问本人的页面，不记录
		if (TextUtils.isEmpty(userObjId) || userObjId.compareTo(tUser.getObjectId()) == 0)
			return;

		// 推送
		VisitData data = new VisitData();
		data.nickname = nickname;
		data.userObjId = userObjId;
		data.targetInstallationId = tUser.getString(XUser.DEVICEID);
		PushHelper.pushVisit(data);

		// 保存到数据库
		AVObject obj = new AVObject("VisitRecord");
		obj.put("visitorId", AVUser.getCurrentUser());
		obj.put("visitorNickname", nickname);
		obj.put("hostId", tUser);
		obj.put("hostNickname", tUser.getString(XUser.NICKNAME));
		obj.saveInBackground();
	}

	public interface GetVisitCountCallback {
		public void onFailed(String errorTip);

		public void onSuccess(int count);
	}

	/**
	 * 获取用户的主页访问次数
	 * 
	 * @param user
	 * @param callback
	 */
	public static final void getVisitCount(AVUser user, final GetVisitCountCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_VISITRECORD);
		query.whereEqualTo("hostId", user);
		query.countInBackground(new CountCallback() {

			@Override
			public void done(int arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
				} else {
					callback.onSuccess(arg0);
				}
			}
		});
	}

	public static final void getVisitors(AVUser user, int skip, final QueryUsersCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_VISITRECORD);
		query.whereEqualTo("hostId", user);
		query.setLimit(20);
		query.setSkip(skip);
		query.include("hostId");
		query.include("visitorId");
		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		query.addDescendingOrder("updatedAt");
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				List<UserInfo> userInfoList = new ArrayList<UserInfo>();
				for (int i = 0; i < arg0.size(); i++) {
					UserInfo info = new UserInfo();
					AVUser user = arg0.get(i).getAVUser("visitorId");
					if (user == null)
						continue;
					info.objId = user.getObjectId();
					info.nickname = user.getString(XUser.NICKNAME);
					info.college = user.getString(XUser.COLLEGE);
					info.gender = user.getInt(XUser.GENDER);
					info.studentId = user.getString(XUser.STUDENTID);
					info.hometown = user.getString(XUser.HOMETOWN);
					if (user.containsKey("image") && user.getAVFile("image") != null) {
						info.avatar = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
								Const.HEADIMAGE_SIZE, 50, "jpg");
					}
					info.lastLogin = DateChangeUtils.toToday(arg0.get(i).getCreatedAt());
					userInfoList.add(info);
				}
				callback.onSuccess(userInfoList);

			}
		});
	}

	/**
	 * 给一条状态点赞
	 * 
	 * 调用云代码实现
	 * 
	 * @param position
	 * @param likeWidget
	 */
	public static final void addLike(final Context mcontext, final AVUser creator, final String tInstallationId,
			final String statusId, final String targetUserObjId) {

		final String creatorId = creator.getObjectId();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("creatorid", creatorId);
		parameters.put("statusid", statusId);
		AVCloud.setProductionMode(true); // 调用生产环境云代码
		AVCloud.callFunctionInBackground("addLike", parameters, null);

		// 当给自己的贴子点赞时，不推送，不保存
		if (creatorId.compareTo(targetUserObjId) != 0) {

			// 推送给贴子的发布者
			CommentLikePushData data = new CommentLikePushData();
			data.targetInstallationId = tInstallationId;
			data.friendName = creator.getString(XUser.NICKNAME);
			data.statusId = statusId;
			PushHelper.pushLike(data);

			AVObject status = AVObject.createWithoutData(Status.CLASSNAME, statusId);

			// 保存到数据库
			AVObject avObject = new AVObject("Likes");
			avObject.put("creator", creator);
			avObject.put("status", status);
			avObject.put("targetUserObjId", targetUserObjId);
			avObject.put("creatorDeviceId", AVInstallation.getCurrentInstallation().getInstallationId());
			avObject.saveInBackground();
		}
	}

	/**
	 * 取消点赞
	 * 
	 * 调用云代码实现
	 * 
	 * @param currentUserId
	 * @param statusId
	 */
	public static final void removeLike(AVUser creator, String statusId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("creatorid", creator.getObjectId());
		parameters.put("statusid", statusId);
		AVCloud.setProductionMode(true); // 调用生产环境云代码
		AVCloud.callFunctionInBackground("removeLike", parameters, null);

		// 从赞的列表里删除
		AVQuery<AVObject> query = new AVQuery<>("Likes");
		query.whereEqualTo("creator", creator);
		query.whereEqualTo("statusId", statusId);
		query.deleteAllInBackground(new DeleteCallback() {

			@Override
			public void done(AVException arg0) {

			}
		});
	}

	/**
	 * 获取点赞列表
	 * 
	 * @param currentUserObjId
	 * @param skip
	 * @param callback
	 */
	public static void getLikeList(String currentUserObjId, int skip, final GetLikeListCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<>("Likes");
		query.whereEqualTo("targetUserObjId", currentUserObjId);
		query.orderByDescending("createdAt");
		query.include("creator");
		query.include("status");
		query.setLimit(Const.LIMIT_GET_STATUS);
		query.setSkip(skip);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				List<LikeData> mData = new ArrayList<LikeData>();
				if (arg0 != null && arg0.size() != 0) {
					for (int i = 0; i < arg0.size(); i++) {
						mData.add(BBSService.transformLikeData(arg0.get(i)));
					}
				}
				callback.onSuccess(mData);
			}
		});

	}

	/**
	 * 举报状态
	 * 
	 * @param statusId
	 *            被举报的帖子的ID，不可为空
	 */
	public static final void reportStatus(String statusId) {

	}

	/**
	 * 删除贴子
	 * 
	 * @param statusId
	 * @param callback
	 */
	public static final void deleteStatus(String statusId, final DeleteStatusCallback callback) {

		// 7个参数
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("statusid", statusId);

		AVCloud.setProductionMode(true); // 调用生产环境云代码
		AVCloud.callFunctionInBackground("deleteStatus", parameters, new FunctionCallback<String>() {

			@Override
			public void done(String arg0, AVException arg1) {
				if (callback == null)
					return;

				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				if (TextUtils.isEmpty(arg0) || arg0.compareTo("success") != 0) {
					callback.onFailed("删除失败");
					return;
				}
				callback.onSuccess();
			}
		});
	}

	/**
	 * 由状态ID获取其内容
	 * 
	 * @param objId
	 */
	public static final void getStatusById(Context cxt, String objId, final GetStatusCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		AVQuery<AVObject> query = new AVQuery<AVObject>(Status.CLASSNAME);
		query.setPolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.include("comment");// 存放AVRelation
		query.include("creator");
		query.getInBackground(objId, new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == null) {
					if (callback != null)
						callback.onFailed("服务器查询出错");
					return;
				}

				if (callback != null)
					callback.onSuccess((Status) arg0);
			}
		});
	}

	/**
	 * 添加评论
	 * 
	 * @param mStatus
	 * @param content
	 * @param isAnonymous
	 * @param targetUsername
	 * @param targetUser
	 * @param callback
	 */
	public static final void addComment(Context cxt, Status mStatus, final String content, boolean isAnonymous,
			String targetUsername, AVUser targetUser, final AddCommentCallback callback) {
		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		if (mStatus == null) {
			callback.onFailed("评论出错");
			return;
		}

		if (TextUtils.isEmpty(content)) {
			callback.onFailed("内容为空");
			return;
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		// 参数
		parameters.put("creator", AVUser.getCurrentUser());

		parameters.put("creatorDeviceId", AVInstallation.getCurrentInstallation().getInstallationId());

		parameters.put("content", content);
		parameters.put("isAnonymous", isAnonymous);
		if (isAnonymous) {
			parameters.put("creatorName", "某同学");
		} else {
			parameters.put("creatorName", AVUser.getCurrentUser().get("name"));
		}

		parameters.put("topicId", mStatus.getObjectId());
		parameters.put("targetTopic", mStatus);

		parameters.put("targetUsername", targetUsername);
		parameters.put("targetUseId", targetUser.getObjectId());
		parameters.put("targetUserDeviceId", targetUser.getString(XUser.DEVICEID));

		AVCloud.setProductionMode(true); // 调用生产环境云代码
		AVCloud.callFunctionInBackground("addComment", parameters, new FunctionCallback<String>() {

			@Override
			public void done(String arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}
				if (!TextUtils.isEmpty(arg0) && arg0.equals("success")) {
					if (callback != null)
						callback.onSuccess();
					return;
				}

				if (callback != null)
					callback.onFailed("评论了失败");
			}
		});
	}

	public static final void getComments(Context cxt, final Status mStatus, final GetCommentsListCallback callback) {
		BBSService.loadMoreComments(cxt, mStatus, 0, callback);
	}

	/**
	 * 加载更多评论
	 * 
	 * @param mStatus
	 * @param callback
	 */
	public static final void loadMoreComments(Context cxt, final Status mStatus, int skip,
			final GetCommentsListCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		if (mStatus == null)
			return;

		AVRelation<Comment> comments = mStatus.getCommentsByRelation();
		AVQuery<Comment> query = comments.getQuery();
		query.orderByAscending("createdAt");
		query.setLimit(Const.LIMIT_GET_COMMENTS);
		query.setSkip(skip);
		query.include("creator");
		query.whereNotEqualTo("isShow", false);
		query.findInBackground(new FindCallback<Comment>() {

			@Override
			public void done(final List<Comment> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == null || arg0.size() == 0) {
					callback.onSuccess(null);
					return;
				}

				final List<CommentData> cmtdata_set = new ArrayList<CommentData>();

				for (final Comment cmt : arg0) {
					if (null != cmt && null != cmt.getCreatedAt()) {
						cmtdata_set.add(BBSService.transformCommentData(cmt, mStatus.getPublisher().getObjectId()));
					}
				}

				if (callback != null) {
					callback.onSuccess(cmtdata_set);
				}
			}
		});
	}

	/**
	 * 删除评论
	 * 
	 * @param cmtObjId
	 */
	public static final void removeComments(final String statusObjId, String cmtObjId,
			final RemoveCommentCallback callback) {
		AVObject obj = AVObject.createWithoutData("comments", cmtObjId);
		obj.put("isShow", false);
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				if (arg0 != null) {
					if (callback != null)
						callback.onFailed(arg0.getLocalizedMessage());
				} else {
					AVObject status = AVObject.createWithoutData(Status.CLASSNAME, statusObjId);
					status.increment(Status.COMMENT_NUM, -1);
					status.saveInBackground(new SaveCallback() {
						@Override
						public void done(AVException arg0) {
							if (arg0 != null) {
								if (callback != null)
									callback.onFailed(arg0.getLocalizedMessage());
							} else {
								if (callback != null)
									callback.onSuccess();
							}
						}
					});
				}
			}
		});
	}

	/**
	 * 获取话题列表
	 * 
	 * @param cxt
	 * @param callback
	 */
	public static final void getTopics(final Context cxt, int limit, int skip, final GetTopicListCallback callback) {

		// 检查网络链接
		if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
			callback.onFailed("无法访问到网络");
			return;
		}

		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>("Topic");
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.addDescendingOrder("num");// 按置顶级别降序排列
		query.whereEqualTo("isShow", true);// 只加载可以显示的
		query.addDescendingOrder("updatedAt");
		query.setLimit(limit);
		query.setSkip(skip);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					if (callback != null)
						callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				List<String> mData = new ArrayList<String>();

				if (arg0 != null && arg0.size() != 0) {
					for (int i = 0; i < arg0.size(); i++) {
						mData.add(arg0.get(i).getString("topic"));
					}
				}
				if (callback != null)
					callback.onSuccess(mData);
			}
		});
	}

	/**
	 * 增加话题
	 * 
	 * @param topic
	 */
	public static void addTopic(final String topic) {
		if (TextUtils.isEmpty(topic))
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>("Topic");
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.setLimit(1);
		query.whereEqualTo("topic", topic);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null || arg0 == null)
					return;

				if (arg0.size() == 0) {
					// 还没有类似话题，这是一个新话题
					AVObject obj = new AVObject("Topic");
					obj.put("topic", topic);
					obj.saveInBackground();
				} else {
					AVObject obj = arg0.get(0);
					obj.increment("num");
					obj.saveInBackground();
				}
			}
		});
	}

	/**
	 * 将AVObject对象转换成实体类
	 * 
	 * @param mcontext
	 * @param item
	 * @return
	 */
	public static StatusData transformStatusData(Context mcontext, Status item) {

		StatusData statusData = new StatusData();

		if (item == null)
			return statusData;

		statusData.AVObjectID = item.getObjectId();

		if (Const.IS_UPDATE_STRATEGY) {
			statusData.date = item.getUpdatedAt();
		} else {
			statusData.date = item.getCreatedAt();
		}

		statusData.isAnonymous = item.getAnonymous();

		// 发布者相关的数据
		if ((item.getPublisher() != null)) {
			if (item.getPublisher().getAVFile("image") != null) {
				statusData.headImgUrl = item.getPublisher().getAVFile("image").getThumbnailUrl(false,
						Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE, 50, "jpg");
			}

			statusData.creator = item.getPublisher();
			statusData.nickName = item.getPublisher().getString("name");
			statusData.userId = item.getPublisher().getObjectId();
			statusData.studentId = item.getPublisher().getUsername();
			statusData.gender = item.getPublisher().getInt("gender");
			statusData.schoolName = item.getPublisher().getString("school");
			statusData.degree = item.getPublisher().getString("degree");
			statusData.college = item.getPublisher().getString(XUser.COLLEGE);
			statusData.isAdmin = item.getPublisher().getBoolean(XUser.IS_ADMIN);
			statusData.deviceId = item.getPublisher().getString(XUser.DEVICEID);
		}

		// 内容
		statusData.content_txt = item.getText();
		statusData.tag = item.getString(Status.TAG);
		statusData.commentNum = item.getCommentNum();

		// 是否置顶
		int sticklevel = item.getInt(Status.STICK_LEVEL);
		if (sticklevel == 0) {
			statusData.isSticky = false;
		} else {
			statusData.isSticky = true;
		}

		// 发布的位置
		// statusData.position = item.getString(Status.POSITION);
		statusData.position = PositionHelper.getDetailedPositionName(item.getString(Status.POSITION),
				item.getAVGeoPoint(Status.LOCATION));
		statusData.lat = item.getAVGeoPoint(Status.LOCATION).getLatitude();
		statusData.lng = item.getAVGeoPoint(Status.LOCATION).getLongitude();

		// 点赞者的id
		statusData.likeUserIds = item.getLikeUserIds();
		String currentUserId = "";
		if (AVUser.getCurrentUser() != null)
			currentUserId = AVUser.getCurrentUser().getObjectId();

		statusData.AlreadyLiked = item.isLiked(currentUserId);
		statusData.likeNum = item.getLikeNum();

		// 图片
		if (item.getImg() == null) {
			statusData.imgUrl = "";// 缩略图url为空
		} else {
			// 获取缩略图Url
			float maxWidth, picWidth, picHeight, maxHeight;

			// 屏幕宽度的一半
			maxWidth = (float) (CommonUtil.ScreenUtils.getScreenWidth(mcontext) * 0.5
					- CommonUtil.ScreenUtils.dp2px(mcontext, 40));

			picWidth = Float.parseFloat(item.getImg().getMetaData("width").toString());

			picHeight = Float.parseFloat(item.getImg().getMetaData("height").toString());

			maxHeight = (int) (maxWidth * picHeight / picWidth);

			statusData.imgUrl = item.getImg().getThumbnailUrl(false, (int) maxWidth, (int) maxHeight, 50, "jpg");// 状态里的缩略图
		}
		return statusData;
	}

	public static final CommentData transformCommentData(Comment cmt, String statusPublisherId) {
		CommentData item = new CommentData();

		if (cmt == null)
			return item;

		// 用户相关的数据
		item.creator = cmt.getCreator();
		if ((cmt.getCreator() != null)) {
			if (cmt.getCreator().getAVFile("image") != null) {
				item.headImgUrl = cmt.getCreator().getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
						Const.HEADIMAGE_SIZE, 50, "jpg");
			}

			item.nickName = cmt.getCreator().getString("name");
			item.userId = cmt.getCreator().getObjectId();
			item.studentId = cmt.getCreator().getUsername();
			item.gender = cmt.getCreator().getInt("gender");
			item.schoolName = cmt.getCreator().getString("school");
			item.degree = cmt.getCreator().getString("degree");
			item.college = cmt.getCreator().getString(XUser.COLLEGE);
		}

		if (item.creator != null && item.creator.getObjectId().equals(statusPublisherId)) {
			// 是楼主
			item.creatorIsLZ = true;
		} else {
			// 不是楼主
			item.creatorIsLZ = false;
		}

		item.objId = cmt.getObjectId();
		item.targetUsername = cmt.getString("targetUsername");// 回复目标用户名
		item.isAnonymous = cmt.getBoolean("isAnonymous");// 是否是匿名评价
		item.content = cmt.getText();
		item.createTime = DateChangeUtils.toToday(cmt.getCreatedAt());

		if (cmt.getAVObject("targetTopic") != null) {
			item.targetTopicId = cmt.getAVObject("targetTopic").getObjectId();
			item.targetTopicContent = cmt.getAVObject("targetTopic").getString("content");

			if (cmt.getAVObject("targetTopic").getAVFile("announce_img") != null) {
				item.targetTopicImageURL = cmt.getAVObject("targetTopic").getAVFile("announce_img")
						.getThumbnailUrl(false, Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE, 50, "jpg");
			} else {
				item.targetTopicImageURL = "";
			}
		}
		item.hasRead = cmt.getBoolean("hasread");
		return item;
	}

	public static final LikeData transformLikeData(AVObject obj) {
		LikeData item = new LikeData();

		if (obj == null)
			return item;

		// 用户相关的数据
		item.creator = obj.getAVUser("creator");
		if ((item.creator != null)) {
			if (item.creator.getAVFile("image") != null) {
				item.headImgUrl = item.creator.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
						Const.HEADIMAGE_SIZE, 50, "jpg");
			}

			item.nickName = item.creator.getString("name");
			item.userId = item.creator.getObjectId();
			item.studentId = item.creator.getUsername();
			item.gender = item.creator.getInt("gender");
			item.schoolName = item.creator.getString("school");
			item.degree = item.creator.getString("degree");
			item.college = item.creator.getString(XUser.COLLEGE);
		}

		item.createTime = DateChangeUtils.toToday(obj.getCreatedAt());

		if (obj.getAVObject("status") != null) {
			item.targetTopicId = obj.getAVObject("status").getObjectId();
			item.targetTopicContent = obj.getAVObject("status").getString("content");

			if (obj.getAVObject("status").getAVFile("announce_img") != null) {
				item.targetTopicImageURL = obj.getAVObject("status").getAVFile("announce_img").getThumbnailUrl(false,
						Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE, 50, "jpg");
			} else {
				item.targetTopicImageURL = "";
			}
		}
		item.hasRead = obj.getBoolean("hasread");
		return item;
	}

}
