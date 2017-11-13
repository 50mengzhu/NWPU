package com.zyw.nwpu.avos;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.avos.avoscloud.AVOSCloud;
import com.zyw.nwpu.R;
import com.zyw.nwpu.WebViewActivity;

/**
 * 
 * 2015年5月28日
 * 
 * @author Rocket
 * 
 */

public class MPushReceiver extends BroadcastReceiver {

	private static int notification_id = 0;

	private class UrlData {
		public String url;
		public String urlTitle;
		public String appTitle;
		public String description;
	}

	private UrlData parsePushUrlData(String push_data) {
		if (TextUtils.isEmpty(push_data))
			return null;
		JSONTokener jsonParser = new JSONTokener(push_data);
		JSONObject js;
		UrlData urlData = new UrlData();

		try {
			js = (JSONObject) jsonParser.nextValue();
			urlData.url = js.getString("url");
			urlData.urlTitle = js.getString("notification");
			urlData.description = js.getString("description");
			urlData.appTitle = js.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return urlData;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		// 推送链接
		if (intent.getAction().equals("push.url")) {

			String push_data = intent.getExtras().getString(
					"com.avoscloud.Data", "");
			UrlData urlData = parsePushUrlData(push_data);

			if (urlData == null || TextUtils.isEmpty(urlData.url)
					|| TextUtils.isEmpty(urlData.urlTitle)
					|| TextUtils.isEmpty(urlData.appTitle))
				return;

			// 显示推送消息
			Intent resultIntent = new Intent(AVOSCloud.applicationContext,
					WebViewActivity.class);// 这里应该改掉

			resultIntent.putExtra("title", urlData.appTitle);
			resultIntent.putExtra("url", urlData.url);

			PendingIntent pendingIntent = PendingIntent.getActivity(
					AVOSCloud.applicationContext, 0, resultIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					AVOSCloud.applicationContext);
			mBuilder.setSmallIcon(R.drawable.ic_launcher);// 图标

			mBuilder.setContentTitle(urlData.urlTitle);// 标题
			mBuilder.setContentText(urlData.description);// 内容
			mBuilder.setTicker(urlData.urlTitle);// 动态显示的内容

			mBuilder.setContentIntent(pendingIntent);// 点击时打开的意图
			mBuilder.setAutoCancel(false);
			Notification notification = mBuilder.build();
			notification.flags = Notification.FLAG_AUTO_CANCEL;// 通知类型
			int mNotificationId = 10086 + notification_id;
			notification_id++;
			NotificationManager mNotifyMgr = (NotificationManager) AVOSCloud.applicationContext
					.getSystemService(Context.NOTIFICATION_SERVICE);

			mNotifyMgr.notify(mNotificationId, notification);

			return;
		}
	}
}
