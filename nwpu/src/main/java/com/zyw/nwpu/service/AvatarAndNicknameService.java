package com.zyw.nwpu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.model.UserInfo;

public class AvatarAndNicknameService {

	public static Map<String, UserInfo> userInfoMap = new HashMap<String, UserInfo>();

	public interface GetUserInfoCallback {
		public void done();
	}

	public static void getUserInfoAndSave(final String stuid, final GetUserInfoCallback callback) {

		if (userInfoMap.containsKey(stuid)) {
			if (callback != null)
				callback.done();
			return;
		}

		AVQuery<AVUser> query = new AVQuery<AVUser>(XUser.CLASSNAME);
		query.whereEqualTo(XUser.STUDENTID, stuid);
		query.include(XUser.HEADIMG);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null || arg0 == null || arg0.size() != 1) {
					return;
				}
				// 获取到用户
				AVUser user = arg0.get(0);
				UserInfo info = new UserInfo();
				info.objId = user.getObjectId();
				info.college = user.getString(XUser.COLLEGE);
				info.gender = user.getInt(XUser.GENDER);
				info.studentId = stuid;
				info.nickname = user.getString(XUser.NICKNAME);
				if (user.containsKey("image") && user.getAVFile("image") != null) {
					info.avatar = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
							Const.HEADIMAGE_SIZE, 50, "jpg");
				}
				userInfoMap.put(stuid, info);

				if (callback != null)
					callback.done();
			}
		});
	}

	public static void getUserInfoAndSaveByObjId(final String objId, final GetUserInfoCallback callback) {

		if (userInfoMap.containsKey(objId)) {
			if (callback != null)
				callback.done();
			return;
		}

		AVQuery<AVUser> query = new AVQuery<AVUser>(XUser.CLASSNAME);
		query.whereEqualTo(XUser.OBJID, objId);
		query.include(XUser.HEADIMG);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null || arg0 == null || arg0.size() != 1) {
					return;
				}
				// 获取到用户
				AVUser user = arg0.get(0);
				UserInfo info = new UserInfo();
				info.objId = user.getObjectId();
				info.college = user.getString(XUser.COLLEGE);
				info.gender = user.getInt(XUser.GENDER);
				info.studentId = user.getString(XUser.STUDENTID);
				info.nickname = user.getString(XUser.NICKNAME);
				if (user.containsKey("image") && user.getAVFile("image") != null) {
					info.avatar = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE,
							Const.HEADIMAGE_SIZE, 50, "jpg");
				}
				userInfoMap.put(objId, info);

				if (callback != null)
					callback.done();
			}
		});
	}
}
