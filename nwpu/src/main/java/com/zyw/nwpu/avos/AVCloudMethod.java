package com.zyw.nwpu.avos;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.zyw.nwpu.BBSPublishActivity;
import com.zyw.nwpulib.utils.CommonUtil;

/**
 * 2015.11.13
 * 
 * @author Rocket
 * 
 */
public class AVCloudMethod {

	/**
	 * 检查是否已经注册了LeanCloud账号
	 * 
	 * @param cxt
	 * @param studentId
	 * @param callback
	 */
	public static void checkRegistLeanCloud(final Context cxt,
			String studentId, FunctionCallback<String> callback) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("studentId", studentId);
		AVCloud.callFunctionInBackground("checkRegist", parameters, callback);
	}

	/**
	 * 调用推送链接的云代码
	 * 
	 * @param notification
	 *            通知栏的标题
	 * @param description
	 *            通知栏的描述
	 * @param title
	 *            点击通知，打开浏览器界面的标题
	 * @param url
	 *            网址链接
	 * @param callback
	 */
	public static void pushUrl(String notification, String description,
			String title, String url, FunctionCallback<String> callback) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action", "push.url");
		parameters.put("notification", notification);
		parameters.put("description", description);
		parameters.put("title", title);
		parameters.put("url", url);
		AVCloud.callFunctionInBackground("pushUrl", parameters, callback);
	}

	public static void publishStatus(AVFile imgfile, String content,
			String address, boolean isanonymous, String tag,
			AVGeoPoint avgeopoint, FunctionCallback<String> callback) {

		if (AVUser.getCurrentUser() == null) {
			if (callback != null)
				callback.done("", new AVException(-1, "请先登录"));
			return;
		}

		// 参数
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("creator", AVUser.getCurrentUser());
		parameters.put("creatorid", AVUser.getCurrentUser().getObjectId());
		parameters.put("content", content);
		if (imgfile != null) {
			parameters.put("imgfileid", imgfile.getObjectId());
		}
		parameters.put("avgeopoint", avgeopoint);
		parameters.put("address", address);
		parameters.put("isanonymous", isanonymous);
		parameters.put("channel", "0");
		parameters.put("tag", tag);

		AVCloud.setProductionMode(true); // 调用生产环境云代码
		AVCloud.callFunctionInBackground("publishTopic", parameters, callback);
	}
}