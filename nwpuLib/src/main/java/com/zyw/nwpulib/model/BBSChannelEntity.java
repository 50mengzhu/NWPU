package com.zyw.nwpulib.model;

import java.io.Serializable;

/**
 * 
 * 频道类
 * 
 * @author Rocket
 * 
 */
public class BBSChannelEntity implements Serializable {

	private static final long serialVersionUID = 2L;

	private String title;
	private String id;
	private long topicNum = 0;
	private String firstTopicContent = "";
	private boolean isNew = false;

	public String getFirstTopicContent() {
		return firstTopicContent;
	}

	public void setFirstTopicContent(String firstTopicContent) {
		this.firstTopicContent = firstTopicContent;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public long getTopicNum() {
		return topicNum;
	}

	public void setTopicNum(long topicNum) {
		this.topicNum = topicNum;
	}

	public BBSChannelEntity() {
	}

	public BBSChannelEntity(String title, String id) {
		this.title = title;
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
