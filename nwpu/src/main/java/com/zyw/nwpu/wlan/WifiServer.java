package com.zyw.nwpu.wlan;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xutils.x;

import com.avos.avoscloud.AVObject;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.SPUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class WifiServer {

	public static final void createShortCut(Activity activit) {
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "校园无线网");
		shortcut.putExtra("duplicate", false);// 设置是否重复创建
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(activit, ShortCutWLANLoginActivity.class);// 设置第一个页面
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activit, R.drawable.ic_wifi);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		activit.sendBroadcast(shortcut);
		ToastUtils.showShortToast(x.app(), "已创建wifi桌面快捷方式");
	}

	// 登陆状态
	public static enum WifiLoginStatus {
		LOGIN_SUCCESS, // "您已经成功登录校园wifi!"
		ARREARAGE, // "您的账户已欠费，为了不影响您正常使用网络，请尽快缴费"
		MAX_USERS, // "用户数量已达上限!"
		WRONG_PW, // "登录失败,请检查用户名和密码！"
		NO_DATA_LEFT, // "无可用流量！"
		LOGOUT_SUCCESS, // "您已经成功下线!"
		SERVICE_DENIED, // "服务器设备拒绝请求！"
		WIFI_CLOSED, // WiFi开关关闭
		NO_CONNECTION, // 没有连接到校园网
		ERROR// 出错
	}

	// 四个校园网的网络名
	private static final String WIFI_SSID_1 = "NWPU-WLAN";
	private static final String WIFI_SSID_2 = "\"NWPU-WLAN\"";
	private static final String WIFI_SSID_3 = "NWPU-LIB";
	private static final String WIFI_SSID_4 = "\"NWPU-LIB\"";

	private OnWifiLoginListner onWifiLoginListner;
	private Context context;

	private WifiLoginStatus status;

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message arg0) {
			if (onWifiLoginListner != null)
				onWifiLoginListner.onWifiLogin(status);
			return false;
		}
	});

	public WifiServer(Context context) {
		this.context = context;
	}

	public WifiServer(OnWifiLoginListner l) {
		setOnWifiLoginListner(l);
	}

	public void setOnWifiLoginListner(OnWifiLoginListner l) {
		this.onWifiLoginListner = l;
	}

	public static boolean isSchoolSSID(String currentSSID) {
		if (TextUtils.isEmpty(currentSSID)
				|| (currentSSID.compareTo(WIFI_SSID_1) != 0 && currentSSID.compareTo(WIFI_SSID_2) != 0
						&& currentSSID.compareTo(WIFI_SSID_3) != 0 && currentSSID.compareTo(WIFI_SSID_4) != 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 登陆WiFi
	 */
	public void login(String username, String pw) {
		// 是否开启WiFi？
		if (!CommonUtil.NetworkUtils.isWiFiActive(context)) {
			onWifiLoginListner.onWifiLogin(WifiLoginStatus.WIFI_CLOSED);
			return;
		}

		// 是否连接上校园网？
		String ssid = CommonUtil.NetworkUtils.getConnectWifiSsid(context);
		if (!WifiServer.isSchoolSSID(ssid)) {
			onWifiLoginListner.onWifiLogin(WifiLoginStatus.NO_CONNECTION);
			return;
		}

		byte[] pswByte = new byte[pw.length()];
		pswByte = pw.getBytes();
		pw = Base64.encodeToString(pswByte, Base64.NO_WRAP);
		String password = pw;
		String url_login = "http://202.117.80.138:8080/portal/pws?t=li&userName=" + username + "&userPwd=" + password
				+ "";
		doGetAsyn(url_login);
	}

	/**
	 * 登出WiFi
	 */
	public void logout() {

	}

	/**
	 * 执行异步GET请求
	 */
	private void doGetAsyn(final String url) {
		new Thread(new Runnable() {
			public void run() {
				doGet(url);
			}
		}).start();
	}

	/**
	 * 执行GET请求
	 */
	private void doGet(String urlname) {
		String inputstr = "";
		try {
			URL url = new URL(urlname);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept", "text/plain, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream(); // 字节流转换成字符串
				inputstr = StreamTools.streamToString(is);
				int strlen = inputstr.length();

				// 根据返回的字节长度判断不同的登录结果
				if (strlen > 800 && strlen < 1300)// 906
					status = WifiLoginStatus.LOGIN_SUCCESS;

				else if (strlen < 600 && strlen > 500)// 546
					status = WifiLoginStatus.ARREARAGE;

				else if (strlen > 421 && strlen < 499)// 462
					status = WifiLoginStatus.MAX_USERS;

				else if (strlen < 420 && strlen > 350)// 394
					status = WifiLoginStatus.WRONG_PW;

				else if (strlen == 323)// 318
					status = WifiLoginStatus.LOGOUT_SUCCESS;

				else if (strlen >= 290 && strlen <= 340 && strlen != 323)// 318
					status = WifiLoginStatus.NO_DATA_LEFT;

				else if (strlen < 100)// 44
					status = WifiLoginStatus.LOGOUT_SUCCESS;

			} else
				status = WifiLoginStatus.SERVICE_DENIED;

		} catch (Exception e) {
			e.printStackTrace();
			status = WifiLoginStatus.ERROR;
		}

		Message message = new Message();
		handler.sendMessage(message);
	}

	/**
	 * WiFi登陆状态监听接口
	 * 
	 * @author Rocket
	 * 
	 */
	public interface OnWifiLoginListner {
		public void onWifiLogin(WifiLoginStatus status);
	}

	/** 与WiFi用户名和密码本地保存相关的函数 ****************************************/
	public static final String KEY_WIFI_USERNAME = "key_wifi_username";
	public static final String KEY_WIFI_PASSWORD = "key_wifi_password";

	/**
	 * 保存WiFi用户名和密码
	 * 
	 * @param context
	 * @param username
	 * @param pw
	 */
	public static void saveWifiUserInfo(Context context, String username, String pw) {
		SPUtils.put(context, KEY_WIFI_USERNAME, username);
		SPUtils.put(context, KEY_WIFI_PASSWORD, pw);
	}

	/**
	 * 获取WiFi用户名
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiUsername(Context context) {
		return (String) SPUtils.get(context, KEY_WIFI_USERNAME, "");
	}

	/**
	 * 获取WiFi密码
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiPassword(Context context) {
		return (String) SPUtils.get(context, KEY_WIFI_PASSWORD, "");
	}
}
