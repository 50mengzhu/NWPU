package com.zyw.nwpu.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zyw.nwpu.dao.ChannelDao;
import com.zyw.nwpu.db.SQLHelper;
import com.zyw.nwpulib.model.ChannelItem;

import android.database.SQLException;

public class ChannelManage {
	public static ChannelManage channelManage;
	/**
	 * 默认的用户选择频道列表
	 */
	public static List<ChannelItem> defaultUserChannels;
	/**
	 * 默认的其他频道列表
	 */
	public static List<ChannelItem> defaultOtherChannels;
	private ChannelDao channelDao;
	/** 判断数据库中是否存在用户数据 */
	private boolean userExist = false;
	static {
		defaultUserChannels = new ArrayList<ChannelItem>();
		defaultOtherChannels = new ArrayList<ChannelItem>();
		int i = 1;

		// 板块名字：推荐、校园热点（代替之前的新闻）、活动预告、通知公告、微生活、翱翔学堂、（新增）、就业实习（原就业）

		// 默认显示的栏目
		defaultUserChannels.add(new ChannelItem(32, "推荐", i++, 1));// ID待定
		defaultUserChannels.add(new ChannelItem(69, "校园热点", i++, 0));
		defaultUserChannels.add(new ChannelItem(33, "通知公告", i++, 1));
		defaultUserChannels.add(new ChannelItem(70, "微生活", i++, 0));

		defaultOtherChannels.add(new ChannelItem(34, "活动预告", i++, 1));
		defaultOtherChannels.add(new ChannelItem(46, "就业实习", i++, 0));
		defaultOtherChannels.add(new ChannelItem(72, "翱翔学堂", i++, 0));
		defaultOtherChannels.add(new ChannelItem(74, "工大沙龙", i++, 0));

		// 以下为默认不显示的频道
		// defaultOtherChannels.add(new ChannelItem(46, "就业", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(51, "最美工大", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(69, "新闻", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(71, "微拓有约", i++, 0));

		// 以下是抓取的频道
		// defaultOtherChannels.add(new ChannelItem(57, "段子", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(58, "科技", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(60, "军事", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(61, "腾讯大家", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(62, "知乎精选", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(64, "今日话题", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(66, "极客公园", i++, 0));
		// defaultOtherChannels.add(new ChannelItem(68, "国内新闻", i++, 0));

	}

	private ChannelManage(SQLHelper paramDBHelper) throws SQLException {
		if (channelDao == null)
			channelDao = new ChannelDao(paramDBHelper.getContext());
		// NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
		return;
	}

	/**
	 * 初始化频道管理类
	 * 
	 * @param paramDBHelper
	 * @throws SQLException
	 */
	public static ChannelManage getManage(SQLHelper dbHelper) throws SQLException {
		if (channelManage == null)
			channelManage = new ChannelManage(dbHelper);
		return channelManage;
	}

	/**
	 * 清除所有的频道
	 */
	public void deleteAllChannel() {
		channelDao.clearFeedTable();
	}

	/**
	 * 获取其他的频道
	 * 
	 * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ChannelItem> getUserChannel() {

		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?", new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			// 数据库存在用户配置
			userExist = true;
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<ChannelItem> list = new ArrayList<ChannelItem>();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		// 不存在用户配置，初始化为默认栏目
		initDefaultChannel();
		return defaultUserChannels;
	}

	/**
	 * 获取其他的频道
	 * 
	 * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ChannelItem> getOtherChannel() {
		Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?", new String[] { "0" });
		List<ChannelItem> list = new ArrayList<ChannelItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ChannelItem navigate = new ChannelItem();
				navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		if (userExist) {
			return list;
		}
		cacheList = defaultOtherChannels;
		return (List<ChannelItem>) cacheList;
	}

	/**
	 * 保存用户频道到数据库
	 * 
	 * @param userList
	 */
	public void saveUserChannel(List<ChannelItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) userList.get(i);
			channelItem.setOrderId(i);// 顺序
			channelItem.setSelected(Integer.valueOf(1));// 已选择标志位
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 保存其他频道到数据库
	 * 
	 * @param otherList
	 */
	public void saveOtherChannel(List<ChannelItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ChannelItem channelItem = (ChannelItem) otherList.get(i);
			channelItem.setOrderId(i);
			channelItem.setSelected(Integer.valueOf(0));
			channelDao.addCache(channelItem);
		}
	}

	/**
	 * 初始化数据库内的频道数据
	 */
	public void initDefaultChannel() {
		deleteAllChannel();
		saveUserChannel(defaultUserChannels);
		saveOtherChannel(defaultOtherChannels);
	}
}
