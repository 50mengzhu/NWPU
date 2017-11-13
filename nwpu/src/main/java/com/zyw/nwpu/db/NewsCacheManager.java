package com.zyw.nwpu.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zyw.nwpulib.model.NewsEntity;
import com.zyw.nwpu.db.SQLHelper;

import android.database.SQLException;
import android.util.Log;

public class NewsCacheManager {
	public static NewsCacheManager newsCacheManager;
	private NewsDBO newsDBO;

	private NewsCacheManager(SQLHelper paramDBHelper) throws SQLException {
		if (newsDBO == null)
			newsDBO = new NewsDBO(paramDBHelper.getContext());
		return;
	}

	/**
	 * 新闻缓存管理类
	 * 
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static NewsCacheManager getManage(SQLHelper dbHelper)
			throws SQLException {
		if (newsCacheManager == null)
			newsCacheManager = new NewsCacheManager(dbHelper);
		return newsCacheManager;
	}

	/**
	 * 清除所有缓存
	 */
	public void deleteAllNewsCache() {
		newsDBO.clearFeedTable();
	}

	public List<NewsEntity> getNewsCacheList(int cat_id) {
		Object cacheList = newsDBO.listCache(SQLHelper.CATID + "= ?",
				new String[] { String.valueOf(cat_id) });

		if (cacheList != null && !((List) cacheList).isEmpty()) {
			// 数据库有新闻缓存，呈现出来
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<NewsEntity> list = new ArrayList<NewsEntity>();
			for (int i = 0; i < count; i++) {
				NewsEntity navigate = new NewsEntity();
				navigate.setNewsId(Integer.valueOf(maplist.get(i).get(
						SQLHelper.NEWSID)));
				navigate.setCatId(Integer.valueOf(maplist.get(i).get(
						SQLHelper.CATID)));
				navigate.setTitle(maplist.get(i).get(SQLHelper.TITLE));
				navigate.setNewsAbstract(maplist.get(i).get(SQLHelper.ABSTRACT));
				navigate.setCommentNum(Integer.valueOf(maplist.get(i).get(
						SQLHelper.COMMENT_NUM)));
				navigate.setLikeNum(Integer.valueOf(maplist.get(i).get(
						SQLHelper.LIKE_NUM)));
				navigate.setPicUrl(maplist.get(i).get(SQLHelper.PICURL));
				navigate.setSource_url(maplist.get(i).get(SQLHelper.SOURCEURL));
				navigate.setPublishTime(maplist.get(i).get(SQLHelper.PUBDATE));
				navigate.setReadStatus(Integer.valueOf(maplist.get(i).get(
						SQLHelper.READSTATUS)));

				list.add(navigate);
			}
			return list;
		}
		// 没有缓存，返回空
		return null;
	}

	/**
	 * 保存新闻缓存
	 * 
	 * @param userList
	 */
	@SuppressWarnings("null")
	public void saveNewsCache(List<NewsEntity> newslist) {
		if (newslist == null && newslist.size() == 0)
			return;

		for (int i = 0; i < newslist.size(); i++) {
			NewsEntity newsEntity = newslist.get(i);
			newsDBO.addCache(newsEntity);
		}
		Log.e("cache", "保存到数据库");
	}
}
