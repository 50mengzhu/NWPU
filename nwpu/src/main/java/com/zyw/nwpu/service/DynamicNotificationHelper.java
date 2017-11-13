package com.zyw.nwpu.service;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

/**
 * 应用中心服务类
 * 
 * @author Rocket
 *
 */
public class DynamicNotificationHelper {

	public interface OnGetDynamicNotificationCallback {
		void onGet(String notification);

		void onFailure(String errDescription);

		void onNoNotification();
	}

	public static void getNotification(final OnGetDynamicNotificationCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>("DynamicNotification");
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.whereEqualTo("enable", true);// 只加载可以显示的
		query.setLimit(1);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// 请求是否出错
				if (arg1 != null) {
					callback.onFailure("获取通知失败：" + arg1.getLocalizedMessage());
					return;
				}

				// 返回值是否为空
				if (arg0 == null || arg0.size() == 0) {
					callback.onNoNotification();
					return;
				}
				callback.onGet(arg0.get(0).getString("notification"));
			}
		});
	}
}
