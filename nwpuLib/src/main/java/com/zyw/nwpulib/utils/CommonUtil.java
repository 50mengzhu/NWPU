package com.zyw.nwpulib.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xutils.x;

import com.avos.avoscloud.AVException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class CommonUtil {

	// 计算两点距离
	public static double getDistance_km(double lat_a, double lng_a, double lat_b, double lng_b) {
		double EARTH_RADIUS = 6378137.0;
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s / 1000;
	}

	public static class VibratorUtil {

		/**
		 * final Activity activity ：调用该方法的Activity实例 long milliseconds
		 * ：震动的时长，单位是毫秒 long[] pattern ：自定义震动模式
		 * 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒 boolean isRepeat ：
		 * 是否反复震动，如果是true，反复震动，如果是false，只震动一次
		 */
		public static void Vibrate(final Activity activity, long milliseconds) {
			Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(milliseconds);
		}

		public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
			Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
			vib.vibrate(pattern, isRepeat ? 1 : -1);
		}
	}

	public static boolean filter(Context cxt, AVException arg1) {
		if (arg1 != null) {
			CommonUtil.ToastUtils.showShortToast(cxt, arg1.getLocalizedMessage());
			return true;
		}
		return false;
	}

	public static void openBrowser(Context cxt, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri uri = Uri.parse(url);
		intent.setData(uri);
		cxt.startActivity(intent);
	}

	public static class SystemUtils {
		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		public static void copy(Context cxt, String text) {
			ClipboardManager copy = (ClipboardManager) cxt.getSystemService(Context.CLIPBOARD_SERVICE);
			copy.setText(text);
			CommonUtil.ToastUtils.showShortToast(cxt, "复制成功");
		}

		public static String getModel() {
			return android.os.Build.MODEL;
		}

		public static String getSystemReleaseVersion() {
			return android.os.Build.VERSION.RELEASE;
		}

		@SuppressWarnings("deprecation")
		public static String getSystemSDKVersion() {
			return android.os.Build.VERSION.SDK;
		}

		public static String getDeviceManufacturer() {
			return android.os.Build.MANUFACTURER;
		}

		public static String getPhoneNum(Context cxt) {
			TelephonyManager phoneMgr = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
			if (TextUtils.isEmpty(phoneMgr.getLine1Number()))
				return "unknown";
			return phoneMgr.getLine1Number();
		}

		public static String getPhoneParam(Context cxt) {
			TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
			StringBuilder sb = new StringBuilder();
			sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
			sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
			sb.append("\nLine1Number = " + tm.getLine1Number());
			sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
			sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
			sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
			sb.append("\nNetworkType = " + tm.getNetworkType());
			sb.append("\nPhoneType = " + tm.getPhoneType());
			sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
			sb.append("\nSimOperator = " + tm.getSimOperator());
			sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
			sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
			sb.append("\nSimState = " + tm.getSimState());
			sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
			sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
			return sb.toString();
		}
	}

	/**
	 * 字符串检查
	 * 
	 * @author Rocket
	 * 
	 */
	public static class CheckUtils {

		/**
		 * 是否是手机号码
		 * 
		 * @param mobiles
		 * @return
		 */
		public static boolean isMobileNO(String mobiles) {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			return m.matches();
		}

		/**
		 * 是否是邮箱地址
		 * 
		 * @param email
		 * @return
		 */
		public static boolean isEmail(String email) {
			String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
			Pattern p = Pattern.compile(str);
			Matcher m = p.matcher(email);
			return m.matches();
		}
	}

	/**
	 * File相关
	 */
	public static class FileUtils {

		public static void saveImage(Bitmap bmp, String dirName, String fileName) throws IOException {
			// 判断sd卡是否存在
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(dirName);
				// 判断文件夹是否存在，不存在则创建
				if (!dir.exists()) {
					dir.mkdir();
				}
				File file = new File(dirName + fileName);
				// 判断文件是否存在，不存在则创建
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					if (fos != null) {
						// 第一参数是图片格式，第二个是图片质量，第三个是输出流
						bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						// 用完关闭
						fos.flush();
						fos.close();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public static final String getFilePathFromUri(Context act, Uri uri) {
			String[] pojo = { android.provider.MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			android.database.Cursor cursor = ((Activity) act).managedQuery(uri, pojo, null, null, null);
			if (cursor != null) {
				int colunm_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				return cursor.getString(colunm_index);
			}
			return null;
		}

		public static String getRootFilePath() {
			if (hasSDCard()) {
				return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
			} else {
				return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath:
																					// /data/data/
			}
		}

		/**
		 * 判断是否有SD卡
		 * 
		 * @return
		 */
		public static boolean hasSDCard() {
			String status = Environment.getExternalStorageState();
			if (!status.equals(Environment.MEDIA_MOUNTED)) {
				return false;
			}
			return true;
		}
	}

	/**
	 * 提示Toast类
	 * 
	 * @author Rocket
	 * 
	 */
	public static class ToastUtils {
		public static void showShortToast(Context context, String txt) {
			Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
		}

		public static void showShortToast(String txt) {
			Toast.makeText(x.app(), txt, Toast.LENGTH_SHORT).show();
		}

		public static void showShortToast(Object obj) {
			String str = String.valueOf(obj);
			Toast.makeText(x.app(), str, Toast.LENGTH_SHORT).show();
		}

	}

	public static class NetworkUtils {

		// 检查网络连接情况
		public static boolean checkNetState(Context context) {
			boolean netstate = false;
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							netstate = true;
							break;
						}
					}
				}
			}
			return netstate;
		}

		// 获取所连接的WiFi的名字
		public static String getConnectWifiSsid(Context context) {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			return wifiInfo.getSSID();
		}

		// 是否开启WiFi
		public static boolean isWiFiActive(Context inContext) {
			Context context = inContext.getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * WIFI网络开关
		 */
		public static void toggleWiFi(Context context, boolean enabled) {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wm.setWifiEnabled(enabled);
		}

	}

	/**
	 * 屏幕相关*****************************************************
	 */
	public static class ScreenUtils {
		private ScreenUtils() {
			throw new UnsupportedOperationException("cannot be instantiated");
		}

		/**
		 * 获取当前屏幕截图，并保存到本地，不包含状态栏
		 * 
		 * @param activity
		 * @return
		 */
		public static void snapShotWithoutStatusBarAndSave(Activity activity, String dir, String imgFileName) {
			View view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap bmp = view.getDrawingCache();
			Rect frame = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;

			int width = getScreenWidth(activity);
			int height = getScreenHeight(activity);
			Bitmap bp = null;
			bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
			view.destroyDrawingCache();

			// 保存到本地
			try {
				// 保存图片
				CommonUtil.FileUtils.saveImage(bp, dir, imgFileName);
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

		@SuppressLint("InlinedApi")
		public static int getActionBarHeight(Context context) {
			TypedArray actionbarSizeTypedArray = context
					.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
			float h = actionbarSizeTypedArray.getDimension(0, 0);

			return (int) h;
		}

		/**
		 * dp转px
		 * 
		 * @param context
		 * @param val
		 * @return
		 */
		public static int dp2px(Context context, float dpVal) {
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
					context.getResources().getDisplayMetrics());
		}

		/**
		 * sp转px
		 * 
		 * @param context
		 * @param val
		 * @return
		 */
		public static int sp2px(Context context, float spVal) {
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
					context.getResources().getDisplayMetrics());
		}

		/**
		 * px转dp
		 * 
		 * @param context
		 * @param pxVal
		 * @return
		 */
		public static float px2dp(Context context, float pxVal) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (pxVal / scale);
		}

		/**
		 * px转sp
		 * 
		 * @param fontScale
		 * @param pxVal
		 * @return
		 */
		public static float px2sp(Context context, float pxVal) {
			return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
		}

		/**
		 * 获得屏幕高度
		 * 
		 * @param context
		 * @return
		 */
		public static int getScreenWidth(Context context) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			return outMetrics.widthPixels;
		}

		/**
		 * 获得屏幕宽度
		 * 
		 * @param context
		 * @return
		 */
		public static int getScreenHeight(Context context) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			return outMetrics.heightPixels;
		}

		/**
		 * 获得状态栏的高度
		 * 
		 * @param context
		 * @return
		 */
		public static int getStatusHeight(Context context) {

			int statusHeight = -1;
			try {
				Class<?> clazz = Class.forName("com.android.internal.R$dimen");
				Object object = clazz.newInstance();
				int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
				statusHeight = context.getResources().getDimensionPixelSize(height);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return statusHeight;
		}

		/**
		 * 获取当前屏幕截图，包含状态栏
		 * 
		 * @param activity
		 * @return
		 */
		public static Bitmap snapShotWithStatusBar(Activity activity) {
			View view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap bmp = view.getDrawingCache();
			int width = getScreenWidth(activity);
			int height = getScreenHeight(activity);
			Bitmap bp = null;
			bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
			view.destroyDrawingCache();
			return bp;

		}

		/**
		 * 获取当前屏幕截图，不包含状态栏
		 * 
		 * @param activity
		 * @return
		 */
		public static Bitmap snapShotWithoutStatusBar(Activity activity) {
			View view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap bmp = view.getDrawingCache();
			Rect frame = new Rect();
			activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;

			int width = getScreenWidth(activity);
			int height = getScreenHeight(activity);
			Bitmap bp = null;
			bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
			view.destroyDrawingCache();
			return bp;

		}

	}

	/**
	 * APP相关
	 */
	public static class AppUtils {

		private AppUtils() {
			/* cannot be instantiated */
			throw new UnsupportedOperationException("cannot be instantiated");
		}

		/**
		 * 获取应用程序名称
		 */
		public static String getAppName(Context context) {
			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				int labelRes = packageInfo.applicationInfo.labelRes;
				return context.getResources().getString(labelRes);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return "";
		}

		/**
		 * [获取应用程序版本名称信息]
		 * 
		 * @param context
		 * @return 当前应用的版本名称
		 */
		public static String getVersionName(Context context) {
			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				return packageInfo.versionName;

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return "";
		}

		public static String getVersionCode(Context context) {
			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				return String.valueOf(packageInfo.versionCode);

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	public static class DateChangeUtils {
		private static final long ONE_MINUTE = 60;
		private static final long ONE_HOUR = 3600;
		private static final long ONE_DAY = 86400;
		private static final long ONE_MONTH = 2592000;
		private static final long ONE_YEAR = 31104000;

		public static Calendar calendar = Calendar.getInstance();

		/**
		 * 
		 * @return yyyy-mm-dd 2012-12-25
		 */

		public static String getDate() {
			return getYear() + "-" + getMonth() + "-" + getDay();
		}

		/**
		 * @param format
		 * @return yyyy年MM月dd HH:mm MM-dd HH:mm 2012-12-25
		 * 
		 */
		public static String getDate(String format) {
			SimpleDateFormat simple = new SimpleDateFormat(format);
			return simple.format(calendar.getTime());
		}

		/**
		 * 
		 * @return yyyy-MM-dd HH:mm 2012-12-29 23:47
		 */
		public static String getDateAndMinute() {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return simple.format(calendar.getTime());
		}

		/**
		 * 
		 * @return yyyy-MM-dd HH:mm:ss 2012-12-29 23:47:36
		 */
		public static String getFullDate() {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return simple.format(calendar.getTime());
		}

		/**
		 * 距离今天多久
		 * 
		 * @param date
		 * @return
		 * 
		 */
		public static String fromToday(Date date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			long time = date.getTime() / 1000;
			long now = new Date().getTime() / 1000;
			long ago = now - time;
			if (ago <= ONE_HOUR)
				return ago / ONE_MINUTE + "分钟前";
			else if (ago <= ONE_DAY)
				return ago / ONE_HOUR + "小时" + (ago % ONE_HOUR / ONE_MINUTE) + "分钟前";
			else if (ago <= ONE_DAY * 2)
				return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";
			else if (ago <= ONE_DAY * 3)
				return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";
			else if (ago <= ONE_MONTH) {
				long day = ago / ONE_DAY;
				return day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";
			} else if (ago <= ONE_YEAR) {
				long month = ago / ONE_MONTH;
				long day = ago % ONE_MONTH / ONE_DAY;
				return month + "个月" + day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
						+ calendar.get(Calendar.MINUTE) + "分";
			} else {
				long year = ago / ONE_YEAR;
				int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is
																// 0 so month+1
				return year + "年前" + month + "月" + calendar.get(Calendar.DATE) + "日";
			}

		}

		/**
		 * 距离截止日期还有多长时间
		 * 
		 * @param date
		 * @return
		 */
		public static String fromDeadline(Date date) {
			long deadline = date.getTime() / 1000;
			long now = (new Date().getTime()) / 1000;
			long remain = deadline - now;
			if (remain <= ONE_HOUR)
				return "只剩下" + remain / ONE_MINUTE + "分钟";
			else if (remain <= ONE_DAY)
				return "只剩下" + remain / ONE_HOUR + "小时" + (remain % ONE_HOUR / ONE_MINUTE) + "分钟";
			else {
				long day = remain / ONE_DAY;
				long hour = remain % ONE_DAY / ONE_HOUR;
				long minute = remain % ONE_DAY % ONE_HOUR / ONE_MINUTE;
				return "只剩下" + day + "天" + hour + "小时" + minute + "分钟";
			}

		}

		/**
		 * 距离今天的绝对时间
		 * 
		 * @param date
		 * @return
		 */
		@SuppressLint("SimpleDateFormat")
		public static String toToday(Date date) {

			long time = date.getTime() / 1000;
			long now = (new Date().getTime()) / 1000;
			long ago = now - time;
			if (ago <= ONE_HOUR)
				return ago / ONE_MINUTE + "分钟前";

			else if (ago <= ONE_DAY)
				return ago / ONE_HOUR + "小时前";

			else {
				SimpleDateFormat simple = new SimpleDateFormat("yyyy年MM月dd日");
				return simple.format(date);
			}
		}

		public static String getYear() {
			return calendar.get(Calendar.YEAR) + "";
		}

		public static String getMonth() {
			int month = calendar.get(Calendar.MONTH) + 1;
			return month + "";
		}

		public static String getDay() {
			return calendar.get(Calendar.DATE) + "";
		}

		public static String get24Hour() {
			return calendar.get(Calendar.HOUR_OF_DAY) + "";
		}

		public static String getMinute() {
			return calendar.get(Calendar.MINUTE) + "";
		}

		public static String getSecond() {
			return calendar.get(Calendar.SECOND) + "";
		}

		// 使用举例
		// String deadline = "2012-12-30 12:45:59";
		// Date date = new Date();
		// SimpleDateFormat simple = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// date = simple.parse(deadline);
		// System.out.println(DateChangeUtils.fromDeadline(date));
		//
		// String before = "2012-12-12 0:0:59";
		// date = simple.parse(before);
		// System.out.println(DateChangeUtils.fromToday(date));
		//
		// System.out.println(DateChangeUtils.getFullDate());
		// System.out.println(DateChangeUtils.getDate());
		//
		// }*/
	}

	/*
	 * 时间工具类
	 */
	@SuppressLint("SimpleDateFormat")
	public static class DateUtils {

		private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
		private final static String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
				"狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };

		public static String getConstellation(String birthday) {

			if (TextUtils.isEmpty(birthday) || birthday.length() != 11)
				return "";
			String month = birthday.substring(5, 7);
			String day = birthday.substring(8, 10);

			int int_month = Integer.parseInt(month);
			int int_day = Integer.parseInt(day);
			if (int_month == 0 || int_day == 0)
				return "";

			return int_day < dayArr[int_month - 1] ? constellationArr[int_month - 1] : constellationArr[int_month];
		}

		/**
		 * 获取年份
		 * 
		 * @return
		 */
		public static int getCurrentYear() {
			return Calendar.getInstance().get(Calendar.YEAR);
		}

		/**
		 * 获取月份 一月份为1，依次类推
		 * 
		 * @return
		 */
		public static int getCurrentMonth() {
			return Calendar.getInstance().get(Calendar.MONTH) + 1;
		}

		/**
		 * 获取日期
		 * 
		 * @return
		 */
		public static int getCurrentDayOfMonth() {
			return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		}

		/**
		 * 获取周几 周日为1，周一为2，依次类推
		 * 
		 * @return
		 */
		public static int getCurrentDayOfWeek() {
			return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		}

		/**
		 * 获取当前小时 24小时制
		 * 
		 * @return
		 */
		public static int getCurrentHour() {
			return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);// 24小时制
		}

		/**
		 * 获取当前分钟数
		 * 
		 * @return
		 */
		public static int getCurrentMin() {
			return Calendar.getInstance().get(Calendar.MINUTE);
		}

		/**
		 * 获取当前周数
		 * 
		 * @return
		 */
		public static int getCurrentWeekOfYear() {
			return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
		}

		/**
		 * 获取当前秒数
		 * 
		 * @return
		 */
		public static int getCurrentSecond() {
			return Calendar.getInstance().get(Calendar.SECOND);
		}

		public static String getDateDiff(Date date) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(date);

			// return getStrTime_ymd_hm(String.valueOf(date.getTime()));
			// String dDate = null;
			// long diff = (int) (System.currentTimeMillis() - date.getTime());
			// int dDay = (int) (diff / (1000 * 60 * 60 * 24));
			// switch (dDay) {
			// case 0:
			// int dHour = (int) (diff / (1000 * 60 * 60));
			// switch (dHour) {
			// case 0:
			// int dMin = (int) (diff / (1000 * 60));
			// switch (dMin) {
			// case 0:
			// case 1:
			// case 2:
			// dDate = "刚刚";
			// break;
			//
			// default:
			// dDate = String.valueOf(dMin) + "分钟前";
			// break;
			// }
			// break;
			// default:
			// dDate = String.valueOf(dHour) + "小时前";
			// break;
			// }
			// break;
			// case 1:
			// dDate = "昨天";
			// break;
			// case 2:
			// dDate = String.valueOf(dDay) + "前天";
			// break;
			// default:
			// dDate = String.valueOf(dDay) + "天前";
			// break;
			// }
			// return dDate;
		}

		/**
		 * 获取格式化时间
		 * 
		 * @param format
		 *            格式，例如：yyyy-MM-dd HH:mm:ss
		 * @return
		 */
		public static String getFormatTime(String format) {
			Date date = new Date();
			SimpleDateFormat sdformat = new SimpleDateFormat(format);
			return sdformat.format(date);
		}

		public static String getFormatTime_ymd_hms() {
			Date date = new Date();
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdformat.format(date);
		}

		/*
		 * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm
		 */
		public static String getStrTime_ymd_hm(String cc_time) {
			String re_StrTime = "";
			if (TextUtils.isEmpty(cc_time) || "null".equals(cc_time)) {
				return re_StrTime;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm:ss
		 */
		public static String getStrTime_ymd_hms(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;

		}

		/*
		 * 将时间戳转为字符串 ，格式：yyyy.MM.dd
		 */
		public static String getStrTime_ymd(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将时间戳转为字符串 ，格式：yyyy
		 */
		public static String getStrTime_y(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将时间戳转为字符串 ，格式：MM-dd
		 */
		public static String getStrTime_md(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将时间戳转为字符串 ，格式：HH:mm
		 */
		public static String getStrTime_hm(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将时间戳转为字符串 ，格式：HH:mm:ss
		 */
		public static String getStrTime_hms(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将时间戳转为字符串 ，格式：MM-dd HH:mm:ss
		 */
		public static String getNewsDetailsDate(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		/*
		 * 将字符串转为时间戳
		 */
		public static String getTime() {
			String re_time = "";
			long currentTime = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			Date d;
			d = new Date(currentTime);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
			return d.toString();
		}

		/*
		 * 将时间戳转为字符串 ，格式：yyyy.MM.dd 星期几
		 */
		public static String getSection(String cc_time) {
			String re_StrTime = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  EEEE");
			// 对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
			// yyyy代表年份，如“2010”；dd代表天，如“25”
			// 例如：cc_time=1291778220
			long lcc_time = Long.valueOf(cc_time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			return re_StrTime;
		}

		// public static String getTodayDate(){
		// SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		// String nowTime=format.format(new Date());
		// return
		// }
	}

}
