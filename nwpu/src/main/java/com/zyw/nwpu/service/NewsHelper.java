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

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpulib.model.News;
import com.zyw.nwpulib.model.NewsEntity;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.DateTools;

public class NewsHelper {

	public interface NewsDataReceiveListener {
		public void onReceived(ArrayList<NewsEntity> newslist, boolean loadmore);
	}

	public static void getNewsList(final NewsDataReceiveListener dataReceiveListener, int catid, int skip,
			final boolean isloadmore) {

		// // 检查网络链接
		// if (!CommonUtil.NetworkUtils.checkNetState(cxt)) {
		// CommonUtil.ToastUtils.showShortToast(cxt, "无法访问到网络");
		// return;
		// }

		if (dataReceiveListener == null)
			return;

		RequestParams p = new RequestParams(Const.newslisturl);
		p.addBodyParameter("cid", String.valueOf(catid));
		p.addBodyParameter("skip", String.valueOf(skip));

		x.http().post(p, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String arg0) {
				dataReceiveListener.onReceived(parseNewsEntity(arg0), isloadmore);
			}
		});
	}

	/**
	 * 解析json
	 * 
	 * @param text
	 * @return
	 */
	private static ArrayList<NewsEntity> parseNewsEntity(String text) {

		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;
		ArrayList<NewsEntity> newslist = new ArrayList<NewsEntity>();

		if (text == null)
			return newslist;

		// if (text == null || !text.startsWith("﻿{\"news\":"))// 不能通过，这里应该去掉
		// return newslist;

		if (text.equals("<h1>WARN!!! YOUR REQ IS DENY!!!</h1>"))
			return newslist;

		try {
			js = (JSONObject) jsonParser.nextValue();
			JSONArray js_array = js.getJSONArray("news");

			for (int i = 0; i < js_array.length(); i++) {
				NewsEntity entity = new NewsEntity();
				entity.setCatId(js_array.getJSONObject(i).getInt("catid"));
				entity.setTitle(js_array.getJSONObject(i).getString("title"));
				entity.setNewsAbstract(js_array.getJSONObject(i).getString("description"));
				entity.setNewsId(js_array.getJSONObject(i).getInt("id"));
				entity.setPicUrl(js_array.getJSONObject(i).getString("pic"));

				boolean o = js_array.getJSONObject(i).isNull("comment_num");

				if (!o) {
					entity.setCommentNum(js_array.getJSONObject(i).getInt("comment_num"));//
				} else {
					entity.setCommentNum(0);//
				}

				entity.setLikeNum(js_array.getJSONObject(i).getInt("like_num"));
				entity.setSource_url(js_array.getJSONObject(i).getString("url"));
				entity.setPublishTime(DateTools.getStrTime_ymd_hms(js_array.getJSONObject(i).getString("write_time")));
				entity.setReadStatus(0);

				entity.setViewnum(js_array.getJSONObject(i).getString("viewnum"));

				entity.setCopyFrom(js_array.getJSONObject(i).getString("copyfrom"));

				int isBigThumb = js_array.getJSONObject(i).getInt("isBigThumb");
				if (isBigThumb == 1) {
					entity.setShowBigImage(true);
				} else {
					entity.setShowBigImage(false);
				}

				newslist.add(entity);
			}

		} catch (JSONException e) {
		}
		// 将新闻缓存到数据库
		// NewsCacheManager.getManage(AppApplication.getApp().getSQLHelper())
		// .saveNewsCache(newslist);
		return newslist;
	}

	/** avos新闻 *******************************************************************/
	public interface BaseCallback {
		public void onFailuer(String errorTip);
	}

	public interface GetNewsCallback extends BaseCallback {
		public void onGet(ArrayList<News> newsList);
	}

	public static final void getNewsFromLeanCloud(String channel, int skip, final GetNewsCallback callback) {
		if (callback == null)
			return;
		AVQuery<AVObject> query = new AVQuery<AVObject>("News");
		query.setLimit(15);
		query.whereContains("channel", channel);
		query.addDescendingOrder("time");// 按时间降序
		query.whereEqualTo("status", 1);// 1为可见
		query.setSkip(skip);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailuer(arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == null || arg0.size() == 0) {
					callback.onFailuer("没有更多了");
					return;
				}

				ArrayList<News> newsList = new ArrayList<News>();
				for (int i = 0; i < arg0.size(); i++) {
					News news = new News();
					news.setData(arg0.get(i));
					newsList.add(news);
				}
				callback.onGet(newsList);
			}
		});
	}

	public static final void getNewsFromLeanCloud(String channel, final GetNewsCallback callback) {
		getNewsFromLeanCloud(channel, 0, callback);
	}
}
