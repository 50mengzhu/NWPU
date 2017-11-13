package com.zyw.nwpu.app;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.easemob.chat.EMChatManager;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.SPUtils;

import android.content.Context;
import android.webkit.CookieManager;

/**
 * 设置和获取是否首次运行
 * 
 * 设置和获取是否已经登陆
 * 
 * 设置和获取学号
 * 
 * 16年3月1日
 * 
 * @author Rocket
 * 
 */
public class AccountHelper {

	private final static String TAG = "AccountHelper";
	private final static String VERSIONCODE = "_"
			+ CommonUtil.AppUtils.getVersionName(AVOSCloud.applicationContext)
			+ TAG;

	// sp key
	private final static String KEY_ISLOGIN = "islogin" + VERSIONCODE;
	private final static String KEY_STUDENTID = "stuid" + VERSIONCODE;
	private final static String KEY_ISFIRSTLAUNCH = "isfirstlaunched"
			+ VERSIONCODE;

	/**
	 * 获取是否首次登陆
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFirstLaunch(Context context) {
		return (boolean) SPUtils.get(context, KEY_ISFIRSTLAUNCH, true);
	}

	/**
	 * 设置已经首次登陆过了
	 * 
	 * @param context
	 * @param isLogedIn
	 */
	public static void setAlreadyFirstLaunched(Context context) {
		SPUtils.put(context, KEY_ISFIRSTLAUNCH, false);
	}

	/**
	 * 获取是否登陆
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isLogedIn(Context context) {
		return (boolean) SPUtils.get(context, KEY_ISLOGIN, false);
	}

	/**
	 * 设置是否登陆
	 * 
	 * @param context
	 * @param isLogedIn
	 */
	public static void setLogedIn(Context context, boolean isLogedIn) {
		SPUtils.put(context, KEY_ISLOGIN, isLogedIn);
	}

	/**
	 * 获取学号
	 * 
	 * @param context
	 * @return
	 */
	public static String getStudentId(Context context) {
		return (String) SPUtils.get(context, KEY_STUDENTID, "");
	}

	/**
	 * 设置学号
	 * 
	 * @param context
	 * @param sid
	 */
	public static void setStudentId(Context context, String sid) {
		SPUtils.put(context, KEY_STUDENTID, sid);
	}

	/**
	 * 注销登录 需要退出环信和退出LeanCloud，并删除本地登录信息
	 */
	public static void logout(Context context) {
		EMChatManager.getInstance().logout();
		AVUser.logOut();
		SPUtils.clear(context);
		setAlreadyFirstLaunched(context);
		CookieManager.getInstance().removeAllCookie();
	}
}
