package com.zyw.nwpu.receiver;

import com.avos.avoscloud.AVOSCloud;
import com.zyw.nwpu.R;
import com.zyw.nwpu.WebViewActivity;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.wlan.WLANLoginActivity;
import com.zyw.nwpu.wlan.WifiServer;
import com.zyw.nwpu.wlan.WifiServer.WifiLoginStatus;
import com.zyw.nwpulib.utils.CommonUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * 2016年4月4日
 * 
 * @author Rocket
 * 
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
		// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
				if (isConnected) {
					String ssid = CommonUtil.NetworkUtils
							.getConnectWifiSsid(context);
					if (WifiServer.isSchoolSSID(ssid)) {
						loginSchoolWifi(context);
					} else {
						clearWifiNotify();
					}
				} else {
					clearWifiNotify();
				}
			}
		}
	}

	private static final int WIFI_NOTIFY_ID = 10022;

	private void loginSchoolWifi(final Context context) {
		String username = WifiServer.getWifiUsername(context);
		String password = WifiServer.getWifiPassword(context);
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			// 若本地没有保存WiFi用户名和密码，则弹出通知提示，点击通知进入登录页面
			showNotify(context, "点击登录校园wifi", true);
			return;
		}

		// 登录校园网
		WifiServer server = new WifiServer(context);
		server.setOnWifiLoginListner(new WifiServer.OnWifiLoginListner() {

			@Override
			public void onWifiLogin(WifiLoginStatus status) {
				switch (status) {
				case LOGIN_SUCCESS:
					showNotify(context, "已帮您登录到校园网", false);
					break;

				default:
					break;
				}
			}
		});
		server.login(username, password);
	}

	private void showNotify(Context context, String content, boolean isClickable) {
		clearWifiNotify();

		Intent resultIntent = new Intent(AVOSCloud.applicationContext,
				WLANLoginActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				AVOSCloud.applicationContext, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				AVOSCloud.applicationContext);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);// 图标

		mBuilder.setContentTitle("校园网自动登录");// 标题
		mBuilder.setContentText(content);// 内容
		mBuilder.setTicker(content);// 动态显示的内容

		if (isClickable) {
			mBuilder.setContentIntent(pendingIntent);// 点击时打开的意图
		}
		mBuilder.setAutoCancel(true);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 通知类型
		NotificationManager mNotifyMgr = (NotificationManager) AVOSCloud.applicationContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifyMgr.notify(WIFI_NOTIFY_ID, notification);

	}

	private void clearWifiNotify() {
		NotificationManager mNotifyMgr = (NotificationManager) AVOSCloud.applicationContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.cancel(WIFI_NOTIFY_ID);
	}

}
