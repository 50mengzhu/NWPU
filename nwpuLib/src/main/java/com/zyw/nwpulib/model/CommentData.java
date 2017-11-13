package com.zyw.nwpulib.model;

import com.avos.avoscloud.AVUser;

/**
 * 
 * 论坛评论数据
 * 
 * @author Rocket
 * 
 */
public class CommentData {

	// private static final long serialVersionUID = 2L;

	// 用户信息
	public AVUser creator = null;
	public String nickName = ""; // 昵称
	public String headImgUrl = "";// 头像
	public String userId = "";// ID
	public int gender = -1;// 性别
	public String schoolName = ""; // 学校
	public String degree = "";// 学历
	public String college = ""; // 学院
	public String studentId = ""; // 学号

	public String objId = "";
	public boolean creatorIsLZ = false;
	public String content = null;
	public String targetUsername = "";
	public String createTime = null;
	public boolean hasRead = false;
	public boolean isAnonymous = false;// 是否是匿名评论

	public String targetTopicId = "";
	public String targetTopicContent = "";
	public String targetTopicImageURL = "";

	public CommentData() {
	}
}
