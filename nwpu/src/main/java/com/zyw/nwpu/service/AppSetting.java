package com.zyw.nwpu.service;

import com.zyw.nwpulib.utils.SPUtils;

import android.content.Context;

public class AppSetting {

	public final static int TS_SMALL = 0;
	public final static int TS_MEDIUM = 1;
	public final static int TS_BIG = 2;

	/**
	 * 是否启用无图模式
	 * 
	 * 默认为不启用
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isBlockImage(Context context) {
		return (Boolean) SPUtils.get(context, "blockImage", false);
	}

	/**
	 * 设置无图模式
	 * 
	 * @param context
	 * @param isBlockImage
	 */
	public static void setBlockImage(Context context, Boolean isBlockImage) {
		SPUtils.put(context, "blockImage", isBlockImage);
	}

	/**
	 * 是否用扬声器播放语音
	 */
	public static Boolean isSpeakerOpened(Context context) {
		return (Boolean) SPUtils.get(context, "speakeropened", false);
	}

	/**
	 * 设置是否用扬声器播放语音
	 */
	public static void setSpeakerOpened(Context context, Boolean isSpeakerOpened) {
		SPUtils.put(context, "speakeropened", isSpeakerOpened);
	}

	/**
	 * 是否启用消息提醒
	 */
	public static Boolean isMsgNotifyAllowed(Context context) {
		return (Boolean) SPUtils.get(context, "msgnotify", true);
	}

	/**
	 * 设置是否启用消息提醒
	 */
	public static void setMsgNotifyAllowed(Context context, Boolean isMsgNotifyAllowed) {
		SPUtils.put(context, "msgnotify", isMsgNotifyAllowed);
	}

	/**
	 * 是否启用消息震动提醒
	 */
	public static Boolean isMsgVibrateAllowed(Context context) {
		return (Boolean) SPUtils.get(context, "msgvibrate", true);
	}

	/**
	 * 设置是否启用消息震动提醒
	 */
	public static void setMsgVibrateAllowed(Context context, Boolean isMsgVibrateAllowed) {
		SPUtils.put(context, "msgvibrate", isMsgVibrateAllowed);
	}

	/**
	 * 是否启用消息声音提醒
	 */
	public static Boolean isMsgSoundAllowed(Context context) {
		return (Boolean) SPUtils.get(context, "msgsound", true);
	}

	/**
	 * 设置是否启用消息声音提醒
	 */
	public static void setMsgSoundAllowed(Context context, Boolean isMsgSoundAllowed) {
		SPUtils.put(context, "msgsound", isMsgSoundAllowed);
	}

	/**
	 * 获取字号
	 * 
	 * @param context
	 * @return
	 */
	public static Integer getTextSize(Context context) {
		return (Integer) SPUtils.get(context, "textSize", TS_MEDIUM);
	}

	/**
	 * 设置字号
	 * 
	 * @param context
	 * @param textSize
	 * @return
	 */
	public static void setTextSize(Context context, Integer textSize) {
		SPUtils.put(context, "textSize", textSize);
	}

	/**
	 * 是否启用全屏手势
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isUseGestureBack(Context context) {
		return (Boolean) SPUtils.get(context, "useSwipe", true);
	}

	/**
	 * 设置是否启用手势返回
	 * 
	 * @param context
	 * @param isuse
	 */
	public static void setUseGestureBack(Context context, Boolean isuse) {
		SPUtils.put(context, "useSwipe", isuse);
	}

	/**
	 * 是否接收推送消息
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isReceiveNotify(Context context) {
		return (Boolean) SPUtils.get(context, "receiveNotify", true);
	}

	/**
	 * 设置是否接收推送消息
	 * 
	 * @param context
	 * @param isreceive
	 */
	public static void setReceiveNotify(Context context, Boolean isreceive) {
		SPUtils.put(context, "receiveNotify", isreceive);
	}


}
