package com.zyw.nwpu.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.zyw.nwpulib.model.CommentLikePushData;

import android.text.TextUtils;

/**
 * 推送类
 * 
 * 2016年04月12日
 * 
 * @author Rocket
 * 
 */

public class PushHelper {

	// 点赞
	public static final String ACT_LIKE = "push.bbs_like";

	// 访问主页
	public static final String ACT_VISIT = "push.visit";

	public static class VisitData {
		public String nickname = "";
		public String userObjId = "";
		public String targetInstallationId = "";
	}

	/**
	 * 推送点赞消息
	 * 
	 * @param nickname
	 * @param statusId
	 */
	public static void pushLike(final CommentLikePushData data) {
		AVPush push = new AVPush();

		AVQuery<AVInstallation> query = AVInstallation.getQuery();
		query.whereEqualTo("installationId", data.targetInstallationId);
		push.setQuery(query);
		push.setChannel("private");// 设置频道
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", ACT_LIKE);
			jsonObject.put("friendName", data.friendName);// 用户昵称
			jsonObject.put("content", "赞");
			jsonObject.put("topicid", data.statusId);// 状态id
			jsonObject.put("targetId", data.targetInstallationId);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		push.setData(jsonObject);
		push.setPushToAndroid(true);
		push.sendInBackground(null);
	}

	/**
	 * 推送 访问主页
	 * 
	 * @param targetInstallationId
	 * @param nickname
	 * @param userObjId
	 */
	public static void pushVisit(VisitData data) {
		AVPush push = new AVPush();
		AVQuery<AVInstallation> query = AVInstallation.getQuery();
		query.whereEqualTo("installationId", data.targetInstallationId);
		push.setQuery(query);
		push.setChannel("private");// 设置频道
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", ACT_VISIT);
			jsonObject.put("nickname", data.nickname);// 用户昵称
			jsonObject.put("userObjId", data.userObjId);// 状态id
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		push.setData(jsonObject);
		push.setPushToAndroid(true);
		push.sendInBackground(null);
	}

	/**
	 * 解析访问者数据
	 * 
	 * @param text
	 * @return
	 */
	public static VisitData parseVisitData(String text) {
		if (TextUtils.isEmpty(text))
			return null;

		VisitData data = new VisitData();
		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;
		try {
			js = (JSONObject) jsonParser.nextValue();
			data.nickname = js.getString("nickname");
			data.userObjId = js.getString("userObjId");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}

}
