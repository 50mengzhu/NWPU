package com.zyw.nwpu.app;

import com.zyw.nwpu.listener.ShakeListener;
import com.zyw.nwpu.listener.ShakeListener.OnShakeListener;
import com.zyw.nwpu.wlan.WifiServer;
import com.zyw.nwpu.wlan.WifiServer.WifiLoginStatus;
import com.zyw.nwpulib.utils.CommonUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;

public class WlanShakeService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		iniShake();
	}

	// 摇一摇
	private ShakeListener mShakeListener = null;
	// 上次检测时间
	private long lastShowTime = 0;
	// 两次检测的时间间隔
	private static final int SHOW_INTERVAL_TIME = 1000;

	private void iniShake() {
		mShakeListener = null;
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake() {
				mShakeListener.stop();
				showTip();
			}
		});
	}

	private void showTip() {
		long currentUpdateTime = System.currentTimeMillis();
		long timeInterval = currentUpdateTime - lastShowTime;
		if (lastShowTime != 0 && timeInterval < SHOW_INTERVAL_TIME) {
			mShakeListener.start();
			return;
		}
		lastShowTime = System.currentTimeMillis();

		// 震动提醒
		Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(100);

		CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "正在登录校园无线网...");
		wifiLogin();
		mShakeListener.start();
	}

	private void wifiLogin() {
		// 先判断本地是否保存WiFi用户名和密码
		String username = WifiServer.getWifiUsername(getApplicationContext());
		String password = WifiServer.getWifiPassword(getApplicationContext());
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			// 若本地没有保存WiFi用户名和密码，则进入WiFi登陆界面
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请先在" + CommonUtil.AppUtils.getAppName(getApplicationContext()) + "app内登录校园无线网，并保存密码。");
			return;
		}

		WifiServer server = new WifiServer(this.getApplicationContext());
		server.setOnWifiLoginListner(new WifiServer.OnWifiLoginListner() {

			@Override
			public void onWifiLogin(WifiLoginStatus status) {
				switch (status) {
				case LOGIN_SUCCESS:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "登录成功");
					break;
				case LOGOUT_SUCCESS:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "您已经成功下线!");
					break;
				case ARREARAGE:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "您的账户已欠费，为了不影响您正常使用网络，请尽快缴费！");
					break;
				case MAX_USERS:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "用户数量已达上限!");
					break;
				case WRONG_PW:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "登录失败,请检查用户名和密码！");
					break;
				case NO_DATA_LEFT:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "无可用流量！");
					break;
				case SERVICE_DENIED:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "服务器设备拒绝请求！");
					break;
				case WIFI_CLOSED:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请打开WiFi，并连接到校园无线网");
					break;
				case NO_CONNECTION:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请先连接到校园无线网");
					break;

				default:
					break;
				}
			}
		});
		server.login(username, password);
	}
}
