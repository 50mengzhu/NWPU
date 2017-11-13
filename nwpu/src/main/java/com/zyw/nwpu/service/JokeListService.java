package com.zyw.nwpu.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.zyw.nwpulib.model.JokeEntity;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.listener.JokeDataReceiveListener;
import com.zyw.nwpu.tool.DateTools;
import com.zyw.nwpu.tool.HttpUtils;

/**
 * 获取笑话数据
 * 
 * 2015年10月20日
 * 
 * @author Rocket
 * 
 */
public class JokeListService {

	protected static final int GUIUPDATEIDENTIFIER = 0x101;
	private JokeDataReceiveListener dataReceiveListener;
	private boolean isloadmore = false;

	private void getJokeListFromInternet(int catid, int skip) {
		HttpUtils.doPostAsyn(httpHandler, Const.jokelisturl,
				"skip=" + String.valueOf(skip));
	}

	/*
	 * 异步从网络获取新闻列表
	 */
	public void getJokeListFromInternetAsyn(
			JokeDataReceiveListener dataReceiveListener, final int catid,
			final int skip, boolean isloadmore) {
		this.dataReceiveListener = dataReceiveListener;
		this.isloadmore = isloadmore;

		new Thread(new Runnable() {

			@Override
			public void run() {
				getJokeListFromInternet(catid, skip);
			}
		}).start();

		return;
	}

	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler() {
		// 接受到相应消息时候，进行相应的处理
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GUIUPDATEIDENTIFIER:
				dataReceiveListener.OnJokeDataReceived(
						(ArrayList<JokeEntity>) msg.obj, isloadmore);
				break;
			}
		}
	};

	@SuppressLint("HandlerLeak")
	Handler httpHandler = new Handler() {
		// 接受到相应消息时候，进行相应的处理
		@Override
		public void handleMessage(Message msg) {
			dataReceiveListener.OnJokeDataReceived(
					parseNewsEntity(msg.obj.toString()), isloadmore);
		}

		// 解析json数据
		private ArrayList<JokeEntity> parseNewsEntity(String text) {

			JSONTokener jsonParser = new JSONTokener(text);
			JSONObject js;

			ArrayList<JokeEntity> newslist = new ArrayList<JokeEntity>();

			if (text.equals("<h1>WARN!!! YOUR REQ IS DENY!!!</h1>"))
				return newslist;

			try {
				js = (JSONObject) jsonParser.nextValue();
				JSONArray js_array = js.getJSONArray("news");

				for (int i = 0; i < js_array.length(); i++) {
					JokeEntity entity = new JokeEntity();

					String content = js_array.getJSONObject(i)
							.getString("text");

					String imgurl = js_array.getJSONObject(i).getString("img");

					entity.setCatId(js_array.getJSONObject(i).getInt("catid"));
					entity.setTitle(js_array.getJSONObject(i)
							.getString("title"));
					entity.setId(js_array.getJSONObject(i).getInt("id"));

					boolean o = js_array.getJSONObject(i).isNull("comment_num");

					if (!o) {
						entity.setCommentNum(js_array.getJSONObject(i).getInt(
								"comment_num"));//
					} else {
						entity.setCommentNum(0);//
					}

					entity.setLikeNum(js_array.getJSONObject(i).getInt(
							"like_num"));
					entity.setUrl(js_array.getJSONObject(i).getString("url"));
					entity.setPublishTime(DateTools.getStrTime_ymd_hms(js_array
							.getJSONObject(i).getString("write_time")));

					entity.setViewNum(js_array.getJSONObject(i).getString(
							"viewnum"));

					entity.setContent(content);
					entity.setImgUrl(imgurl);
					newslist.add(entity);
				}

			} catch (JSONException e) {
			}
			return newslist;
		}

	};
}
