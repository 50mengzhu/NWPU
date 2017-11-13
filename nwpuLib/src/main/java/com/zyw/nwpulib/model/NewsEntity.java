package com.zyw.nwpulib.model;

import java.io.Serializable;

import android.text.TextUtils;

/**
 * 
 * @author Rocket
 * 
 */
public class NewsEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 评论数量 */
	private Integer commentNum;
	/** 点赞数量 */
	private Integer likeNum;
	/** 新闻ID */
	private Integer newsId;
	/** 类别ID */
	private Integer catId;
	/** 标题 */
	private String title;
	/** 新闻源地址 URL */
	private String source_url;
	/** 发布时间 */
	private String publishTime;
	/** 摘要 */
	private String newsAbstract;
	/** 图片1 URL */
	private String picOne = "";
	/** 阅读状态 ，读过的话显示灰色背景 */
	private Integer readStatus = 0;
	/** 阅读量 **/
	private String viewnum = "";

	private String copyFrom = "";

	private boolean isShowBigImage = false;// 默认不显示为大图

	public void parse(News news) {
		this.title = news.getTitle();
		this.copyFrom = news.getFrom();
		this.publishTime = news.getTime();
		this.commentNum = Integer.valueOf(news.getCommentNum());
		this.likeNum = Integer.valueOf(news.getLikeNum());
		this.isShowBigImage = news.getIsBigThumb();
		this.picOne = news.getThumb();
		this.viewnum = news.getViewNum();
		this.source_url = "https://leancloud.cn/apionline/#!/classes/创建或更新对象_post_0";
	}

	public boolean isShowBigImage() {
		return isShowBigImage;
	}

	public void setShowBigImage(boolean isShowBigImage) {
		this.isShowBigImage = isShowBigImage;
	}

	public String getCopyFrom() {
		return copyFrom;
	}

	public void setCopyFrom(String copyFrom) {
		this.copyFrom = copyFrom;
	}

	public String getViewnum() {
		return viewnum;
	}

	public void setViewnum(String viewnum) {
		if (TextUtils.isEmpty(viewnum) || viewnum.compareTo("null") == 0)
			this.viewnum = "0";
		else
			this.viewnum = viewnum;
	}

	public Integer getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
	}

	public Integer getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}

	public Integer getNewsId() {
		return newsId;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}

	public Integer getCatId() {
		return catId;
	}

	public void setCatId(Integer catId) {
		this.catId = catId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getPicUrl() {
		return picOne;
	}

	public void setPicUrl(String picOne) {
		this.picOne = picOne;
	}

	// public String getPicTwo() {
	// return picTwo;
	// }
	//
	// public void setPicTwo(String picTwo) {
	// this.picTwo = picTwo;
	// }
	//
	// public String getPicThr() {
	// return picThr;
	// }
	//
	// public void setPicThr(String picThr) {
	// this.picThr = picThr;
	// }

	// public List<String> getPicList() {
	// return picList;
	// }
	//
	// public void setPicList(List<String> picList) {
	// this.picList = picList;
	// }

	/**
	 * 阅读状态
	 * 
	 * @return
	 */
	public Integer getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Integer readStatus) {
		this.readStatus = readStatus;
	}

	/**
	 * 新闻概述
	 * 
	 * @param newsAbstract
	 */
	public String getNewsAbstract() {
		return newsAbstract;
	}

	public void setNewsAbstract(String newsAbstract) {
		this.newsAbstract = newsAbstract;
	}

	/**
	 * 源网址
	 * 
	 * @param newsAbstract
	 */
	public String getSource_url() {
		return source_url;
	}

	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}

}
