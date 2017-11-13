package com.zyw.nwpu.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zyw.nwpu.tool.DateTools;
import com.zyw.nwpulib.model.JokeEntity;
import com.zyw.nwpulib.model.ScrollImageEntity;

import android.text.TextUtils;

/**
 * 应用中心服务类
 * 
 * @author Rocket
 *
 */
public class AppCenterService {

	// 表名和字段名
	private static final String APPCENTER_CLASSNAME = "AppCenter";
	private static final String APPCENTER_LINK_URL = "linkUrl";
	private static final String APPCENTER_IMG_URL = "imgUrl";
	private static final String APPCENTER_CLICK_NUM = "clickNum";
	private static final String APPCENTER_IS_SHOW = "isShow";
	private static final String APPCENTER_ORDER = "order";

	/**
	 * 获取轮播图片的回调接口
	 * 
	 * @author Rocket
	 *
	 */
	public interface OnGetImagesCallback {
		// 获取失败的回调函数，参数为错误提示
		public void onFailed(String errorTip);

		// 获取成功的回调函数，参数是图片列表
		public void onSuccess(List<ScrollImageEntity> imageList);
	}

	/**
	 * 获取轮播图片列表
	 * 
	 * @param callback
	 *            回调函数
	 */
	public static void getImageList(final OnGetImagesCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>(APPCENTER_CLASSNAME);
		query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		query.addAscendingOrder(APPCENTER_ORDER);// 按order的顺序，增序排列
		query.whereEqualTo(APPCENTER_IS_SHOW, true);// 只加载可以显示的
		query.setLimit(10);// 最多显示10张图片
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// 请求是否出错
				if (arg1 != null) {
					callback.onFailed(arg1.getLocalizedMessage());
					return;
				}

				// 返回值是否为空
				if (arg0 == null || arg0.size() == 0) {
					callback.onFailed("no images");
					return;
				}

				List<ScrollImageEntity> imageList = new ArrayList<ScrollImageEntity>();
				for (int i = 0; i < arg0.size(); i++) {
					ScrollImageEntity entity = new ScrollImageEntity();
					entity.setId(arg0.get(i).getObjectId());
					entity.setImgUrl(arg0.get(i).getString(APPCENTER_IMG_URL));
					entity.setLinkUrl(arg0.get(i).getString(APPCENTER_LINK_URL));
					imageList.add(entity);
				}
				callback.onSuccess(imageList);
			}
		});
	}

	/**
	 * 获取轮播图片列表
	 * 
	 * @param callback
	 *            回调函数
	 */
	public static void getImageList2(final OnGetImagesCallback callback) {
		if (callback == null)
			return;

		RequestParams p = new RequestParams("http://s-170401.gotocdn.com/guada_app/get_pic.php");
		x.http().get(p, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				callback.onFailed("");
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				callback.onFailed("");
			}

			@Override
			public void onFinished() {
				callback.onFailed("");
			}

			@Override
			public void onSuccess(String arg0) {
				if (TextUtils.isEmpty(arg0))
					return;
				List<ScrollImageEntity> l = parseImages(arg0);
				if (l != null && l.size() != 0)
					callback.onSuccess(l);
			}
		});

	}

	// 解析json数据
	private static List<ScrollImageEntity> parseImages(String text) {

		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;

		ArrayList<ScrollImageEntity> list = new ArrayList<ScrollImageEntity>();

		if (text.equals("<h1>WARN!!! YOUR REQ IS DENY!!!</h1>"))
			return list;

		try {
			js = (JSONObject) jsonParser.nextValue();
			JSONArray js_array = js.getJSONArray("images");

			for (int i = 0; i < js_array.length(); i++) {
				String src = js_array.getJSONObject(i).getString("src");
				boolean have_href = js_array.getJSONObject(i).getBoolean("have_href");
				String href = "";

				ScrollImageEntity entity = new ScrollImageEntity();
				entity.setId("");
				entity.setImgUrl(src);
				if (have_href) {
					href = js_array.getJSONObject(i).getString("href");
					entity.setLinkUrl(href);
				} else {
					entity.setLinkUrl("");
				}

				list.add(entity);
			}

		} catch (JSONException e) {
		}
		return list;
	};

	/**
	 * 增加轮播图的点击量
	 * 
	 * @param id
	 *            轮播图的id
	 */
	public static void addImageClickNum(String id) {
		AVObject obj = AVObject.createWithoutData(APPCENTER_CLASSNAME, id);
		obj.increment(APPCENTER_CLICK_NUM);
		obj.saveInBackground();
	}
}
