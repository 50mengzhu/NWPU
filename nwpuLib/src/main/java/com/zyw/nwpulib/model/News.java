package com.zyw.nwpulib.model;

import java.util.Date;

import com.avos.avoscloud.AVObject;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;

/**
 * 返回手势监听接口
 * 
 * @author Rocket
 * 
 */
public class News {

	private String objId;
	private String channel;
	private String commentNum;
	private String likeNum;
	private String viewNum;

	private String title;
	private String time;
	private String thumb;
	private String thumb2;
	private String thumb3;
	private String from;
	private Boolean isBigThumb;
	private Boolean isRedirect;

	public String getViewNum() {
		return viewNum;
	}

	public void setViewNum(String viewNum) {
		this.viewNum = viewNum;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(String commentNum) {
		this.commentNum = commentNum;
	}

	public String getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(String likeNum) {
		this.likeNum = likeNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getThumb2() {
		return thumb2;
	}

	public void setThumb2(String thumb2) {
		this.thumb2 = thumb2;
	}

	public String getThumb3() {
		return thumb3;
	}

	public void setThumb3(String thumb3) {
		this.thumb3 = thumb3;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Boolean getIsBigThumb() {
		return isBigThumb;
	}

	public void setIsBigThumb(Boolean isBigThumb) {
		this.isBigThumb = isBigThumb;
	}

	public Boolean getIsRedirect() {
		return isRedirect;
	}

	public void setIsRedirect(Boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public void setData(AVObject ojb) {
		this.objId = ojb.getObjectId();
		this.channel = ojb.getString("channel");
		this.commentNum = String.valueOf(ojb.getInt("commentNum"));
		this.likeNum = String.valueOf(ojb.getInt("likeNum"));
		this.viewNum = String.valueOf(ojb.getInt("viewNum"));
		this.title = ojb.getString("title");
		this.thumb = ojb.getString("thumb");
		this.thumb2 = ojb.getString("thumb2");
		this.thumb3 = ojb.getString("thumb3");
		this.from = ojb.getString("from");
		this.isBigThumb = ojb.getBoolean("isBigThumb");
		this.isRedirect = ojb.getBoolean("isRedirect");

		Date date = ojb.getDate("time");
		this.time = DateChangeUtils.fromToday(date);
	}
}
