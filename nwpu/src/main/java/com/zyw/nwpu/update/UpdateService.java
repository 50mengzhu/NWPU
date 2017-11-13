package com.zyw.nwpu.update;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import com.zyw.nwpu.app.Const;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

public class UpdateService {
	private Context context;
	public static UpdateInfoEntity updateInfoEntity = null;

	private Handler handler;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 发送消息
	 * 
	 * @param result
	 */
	private void sendHandlerMsg(boolean isForceUpdate) {
		if (handler != null) {
			Message message = new Message();
			message.obj = isForceUpdate;
			handler.sendMessage(message);
		}
	}

	public UpdateService(Context context) {
		this.context = context;
	}

	/**
	 * 检查是否需要更新
	 * 
	 * @param callback
	 */
	public void isNeedUpdate() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					updateInfoEntity = getServerUpdateInfo();
					String current_version = getVersion();
					String version = updateInfoEntity.getVersion();

					if (version == null)
						return;

					if (!current_version.equals(version)) {
						if (handler != null) {
							sendHandlerMsg(true);
						}
					} else {
						if (handler != null) {
							sendHandlerMsg(false);
						}
					}

				} catch (Exception e) {
				}
			}
		}).start();

	}

	/**
	 * 主界面检查是否需要更新
	 * 
	 * @param callback
	 */
	public void isNeedUpdate_main() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					updateInfoEntity = getServerUpdateInfo();
					String current_version = getVersion();
					String version = updateInfoEntity.getVersion();

					if (version == null)
						return;

					if (!current_version.equals(version)) {
						// 有新版本
						if (handler != null) {
							Message message = new Message();
							message.obj = updateInfoEntity;
							handler.sendMessage(message);
						}
					}
				} catch (Exception e) {
				}
			}
		}).start();

	}

	public UpdateInfoEntity getUpdateInfo() {
		return updateInfoEntity;
	}

	/**
	 * 获取当前应用程序的版本号
	 * 
	 * @return
	 */
	private String getVersion() {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (Exception e) {
			return "unknown";
		}
	}

	/**
	 * 服务器路径string对应的id
	 * 
	 * @param urlid
	 * @return 更新的信息
	 */
	public UpdateInfoEntity getServerUpdateInfo() throws Exception {
		URL url = new URL(Const.updateurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return parseUpdataInfo(is);
	}

	/**
	 * 
	 * 解析的xml的inputstream
	 * 
	 * @param is
	 * @return updateinfo
	 */
	private UpdateInfoEntity parseUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		UpdateInfoEntity info = new UpdateInfoEntity();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();

		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					String version = parser.nextText();
					info.setVersion(version);
				} else if ("description".equals(parser.getName())) {
					String description = parser.nextText();
					info.setDescription(description);
				} else if ("apkurl".equals(parser.getName())) {
					String apkurl = parser.nextText();
					info.setApkurl(apkurl);
				} else if ("size".equals(parser.getName())) {
					String size = parser.nextText();
					info.setSize(size);
				} else if ("forceupdate".equals(parser.getName())) {
					String isForceUpdate = parser.nextText();
					info.setForceUpdate(Boolean.parseBoolean(isForceUpdate));
				}

				break;

			}

			type = parser.next();
		}
		return info;
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	public static void installApk(String apkFile, Context context) {
		File apkfile = new File(apkFile);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}

	// public interface CheckUpdateCallback {
	// public void OnCallback(boolean isNeedUpdate);
	// }

}
