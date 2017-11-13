package com.zyw.nwpu.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.avos.avoscloud.AVOSCloud;
import com.zyw.nwpu.R;

/**
 * 2016年4月4日
 * 
 * 通知管理类
 * 
 * @author Rocket
 * 
 */
public class Notifier {

	public Notifier() {
	}

	private static int startNotifyId = 10000;

	/**
	 * 显示通知
	 * 
	 * @param intent
	 *            点击通知时打开的意图，可为空
	 * @param title
	 * @param content
	 * @param ticker
	 */
	public static void notify(Intent intent, String title, String content, String ticker) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AVOSCloud.applicationContext)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(content).setTicker(ticker);
		mBuilder.setAutoCancel(true);

		if (intent != null) {
			PendingIntent pendingIntent = PendingIntent.getActivity(AVOSCloud.applicationContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
		}

		NotificationManager mNotifyMgr = (NotificationManager) AVOSCloud.applicationContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(startNotifyId++, mBuilder.build());
	}
}