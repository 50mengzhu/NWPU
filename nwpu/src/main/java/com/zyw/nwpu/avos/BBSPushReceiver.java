package com.zyw.nwpu.avos;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.zyw.nwpu.BBSCommentActivity;
import com.zyw.nwpu.UserProfileActivity;
import com.zyw.nwpu.service.Notifier;
import com.zyw.nwpu.service.PushHelper;
import com.zyw.nwpu.service.PushHelper.VisitData;
import com.zyw.nwpulib.model.CommentLikePushData;
import com.zyw.nwpulib.utils.CommonUtil.AppUtils;

/**
 * 
 * 2014年10月31日
 * 
 * 2015年11月5日修改
 * 
 * @author Rocket
 * 
 */
public class BBSPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// 接收访问主页的推送
		if (intent.getAction().equals(PushHelper.ACT_VISIT)) {
			VisitData data = PushHelper.parseVisitData(intent.getExtras().getString("com.avoscloud.Data", ""));
			if (data == null)
				return;

			Intent clickIntent = new Intent(context, UserProfileActivity.class);
			clickIntent.putExtra("userid", data.userObjId);
			Notifier.notify(clickIntent, AppUtils.getAppName(context), data.nickname + " 访问了你的主页",
					data.nickname + " 访问了你的主页");
			return;
		}

		// 接收点赞推送
		if (intent.getAction().equals(PushHelper.ACT_LIKE)) {
			// 解析数据
			CommentLikePushData data = parseCommentLikePushData(intent.getExtras());

			// 检查是否应该推送
			if (!shouldNotify(data))
				return;

			// 通知栏显示
			Intent clickIntent = new Intent(AVOSCloud.applicationContext, BBSCommentActivity.class);
			clickIntent.putExtra("topicid", data.statusId);
			String title = AppUtils.getAppName(context);
			String content = data.friendName + " 赞了你";
			String ticker = content;
			Notifier.notify(clickIntent, title, content, ticker);
		}

		// 评论推送
		else if (intent.getAction().equals("bbs.comment")) {
			// 解析数据
			CommentLikePushData data = parseCommentLikePushData(intent.getExtras());

			// 检查是否应该推送
			if (!shouldNotify(data))
				return;

			// 显示通知
			Intent clickIntent = new Intent(AVOSCloud.applicationContext, BBSCommentActivity.class);
			clickIntent.putExtra("topicid", data.statusId);
			String title = data.friendName + " 评论了你";
			String content = data.content;
			String ticker = title;
			Notifier.notify(clickIntent, title, content, ticker);
			return;
		}
	}

	/**
	 * 解析评论he点赞的推送消息
	 * 
	 * @param push_data
	 */
	private CommentLikePushData parseCommentLikePushData(Bundle bundle) {
		if (bundle == null)
			return null;

		String push_data = bundle.getString("com.avoscloud.Data", "");
		if (TextUtils.isEmpty(push_data))
			return null;

		JSONTokener jsonParser = new JSONTokener(push_data);
		JSONObject js;
		CommentLikePushData data = new CommentLikePushData();
		try {
			js = (JSONObject) jsonParser.nextValue();
			data.content = js.getString("content");
			data.statusId = js.getString("topicid");
			data.friendName = js.getString("friendName");
			data.targetInstallationId = js.getString("targetId");
			// data.sourceInstallationId = js.getString("creatorDeviceId");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}

	/**
	 * 检查是否应该通知
	 * 
	 * @param data
	 * @return
	 */
	private boolean shouldNotify(CommentLikePushData data) {
		if (data == null)
			return false;

		// 推送目标为空，或不是自己
		if (TextUtils.isEmpty(data.targetInstallationId) || data.targetInstallationId
				.compareTo(AVInstallation.getCurrentInstallation().getInstallationId()) != 0) {
			return false;
		}

		// // 推送的来源是自己，也不通知
		// if (TextUtils.isEmpty(data.sourceInstallationId) ||
		// data.sourceInstallationId
		// .compareTo(AVInstallation.getCurrentInstallation().getInstallationId())
		// == 0) {
		// return false;
		// }

		return true;
	}
}
