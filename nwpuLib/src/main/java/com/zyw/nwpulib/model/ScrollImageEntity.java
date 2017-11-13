package com.zyw.nwpulib.model;

/**
 * 应用中心的轮播图实体类
 * 
 */
public class ScrollImageEntity {

	private String id;// id
	private String imgUrl = "";// 图片地址
	private String linkUrl = "";// 链接地址

	public ScrollImageEntity() {
	}

	public ScrollImageEntity(String id, String imgUrl, String linkUrl) {
		this.id = id;
		this.imgUrl = imgUrl;
		this.linkUrl = linkUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
}
