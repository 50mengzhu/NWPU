package com.zyw.nwpulib.model;

import java.io.Serializable;
import java.util.Date;

import com.avos.avoscloud.AVUser;

public class StatusData implements Serializable {

	private static final long serialVersionUID = 3L;

	// 发布者信息
	public AVUser creator = null;
	public int gender = -1;// 性别
	public String nickName = ""; // 昵称
	public String headImgUrl = "";// 头像
	public String userId = "";// ID
	public String schoolName = ""; // 学校
	public String degree = "";// 学历
	public String college = ""; // 学院
	public String studentId = ""; // 学号
	public String deviceId = ""; // 设备id，即installationId,用于推送消息

	// 帖子信息
	public String AVObjectID = "";
	public Date date = null;
	public String tag = "";
	public String content_txt = "";
	public String imgUrl = "";
	public int commentNum = 0;
	public int likeNum = 0;
	public String likeUserIds = "";// 所有点赞者的id
	public boolean isAnonymous = false;
	public boolean isSticky = false;// 是否是置顶
	public boolean isAdmin = false;// 是否是管理员
	public boolean AlreadyLiked = false;
	public String position = "";// 位置
	public double lng = -1, lat = -1;

	public StatusData() {
	}

}
