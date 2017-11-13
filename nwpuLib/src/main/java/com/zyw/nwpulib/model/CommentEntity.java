package com.zyw.nwpulib.model;

/**
 * 
 * 评论类
 * 
 * @author Rocket
 * 
 */
public class CommentEntity {

	/** 头像 */
	private String headimg_url = "";
	/** 用户名 */
	private String username;
	/** 点赞数 **/
	private int likenum = 0;
	/** 评论内容 */
	private String content;
	/** 发布时间 */
	private String pubtime;
	/** 评论ID */
	private String id;
	/** 内容ID */
	private String contentid;
	/** 已经赞过 */
	private boolean alreadyLiked = false;

	// public CommentEntity(String headimg_url, String username, int likenum,
	// String content, String pubtime, String id, String contentid,
	// boolean alreadyLiked) {
	// this.headimg_url = headimg_url;
	// this.username = username;
	// this.likenum = likenum;
	// this.content = content;
	// this.pubtime = pubtime;
	// this.id = id;
	// this.contentid = contentid;
	// this.alreadyLiked = alreadyLiked;
	// }

	public void setLiked() {
		this.alreadyLiked = true;
	}

	public boolean isLiked() {
		return alreadyLiked;
	}

	public String getHeadImgUrl() {
		return headimg_url;
	}

	public void setHeadImgUrl(String url) {
		this.headimg_url = url;
	}

	public int getLikeNum() {
		return likenum;
	}

	public void setLikeNum(int likenum) {
		this.likenum = likenum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentId() {
		return contentid;
	}

	public void setContentId(String contentid) {
		this.contentid = contentid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPublishTime() {
		return pubtime;
	}

	public void setPublishTime(String pubtime) {
		this.pubtime = pubtime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}