package com.zyw.nwpu.service;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

/**
 * 启动页面帮助类
 * 
 * @author Rocket
 *
 */
public class SplashHelper {

	/**
	 * 
	 * 获取启动页图片或视频回调函数
	 * 
	 * @author Rocket
	 *
	 */
	public interface OnGetSplashCallback {
		/**
		 * 获取失败回调接口，此时显示默认启动页面即可
		 * 
		 * @param errTip
		 */
		public void onFailure(String errTip);

		/**
		 * 获取成功
		 * 
		 * @param isVideo
		 * @param url
		 */
		public void onSuccess(boolean isVideo, String url);
	}

	public static final void getSplash(final OnGetSplashCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>("SplashImage");
		query.whereEqualTo("isShow", true);
		query.setLimit(1);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure(arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == null || arg0.size() == 0) {
					callback.onFailure("没有启动页图片");
					return;
				}

				AVFile file = arg0.get(0).getAVFile("image");
				if (file == null) {
					callback.onFailure("没有启动页图片");
					return;
				}
				String url = file.getUrl();// 这里没有进行图片压缩

				boolean isVideo = arg0.get(0).getBoolean("isVideo");
				callback.onSuccess(isVideo, url);
			}
		});
	}
}
