package com.zyw.nwpu.service;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVQuery.CachePolicy;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.model.UserInfo;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;

import android.text.TextUtils;

public class UserInfoService {

	private final static int LIMIT = 10;

	public interface QueryUsersCallback {
		public void onFailed(String errorTip);

		public void onSuccess(List<UserInfo> userList);
	}

	public static class SearchCondition {
		private String key;
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public SearchCondition(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

	}

	/**
	 * 
	 * 查询特定用户用户，不包括自己
	 * 
	 * @param currentUserObjId
	 *            本人id，可为空
	 * @param key
	 * @param value
	 * @param skip
	 * @param callback
	 */
	public static void queryUsers(final String currentUserObjId, List<SearchCondition> conditions, final int skip,
			final QueryUsersCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVUser> query = new AVQuery<AVUser>(XUser.CLASSNAME);
		if (!TextUtils.isEmpty(currentUserObjId))
			query.whereNotEqualTo(XUser.OBJID, currentUserObjId);

		if (conditions != null) {
			for (int i = 0; i < conditions.size(); i++) {
				query.whereEqualTo(conditions.get(i).getKey(), conditions.get(i).getValue());
			}
		}
		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
		query.include(XUser.HEADIMG);
		query.addDescendingOrder("updatedAt");
		query.setLimit(LIMIT);
		query.setSkip(skip);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailed("查询失败:" + arg1.getLocalizedMessage());
					return;
				}

				List<UserInfo> userInfoList = new ArrayList<UserInfo>();
				for (int i = 0; i < arg0.size(); i++) {
					UserInfo info = new UserInfo();
					info.objId = arg0.get(i).getObjectId();
					info.nickname = arg0.get(i).getString(XUser.NICKNAME);
					info.college = arg0.get(i).getString(XUser.COLLEGE);
					info.gender = arg0.get(i).getInt(XUser.GENDER);
					info.studentId = arg0.get(i).getString(XUser.STUDENTID);
					info.hometown = arg0.get(i).getString(XUser.HOMETOWN);
					info.lastLogin = DateChangeUtils.toToday(arg0.get(i).getUpdatedAt());
					if (arg0.get(i).containsKey("image") && arg0.get(i).getAVFile("image") != null) {
						info.avatar = arg0.get(i).getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
								Const.HEADIMAGE_SIZE, 50, "jpg");
					}
					userInfoList.add(info);
				}
				callback.onSuccess(userInfoList);
			}
		});
	}
}
