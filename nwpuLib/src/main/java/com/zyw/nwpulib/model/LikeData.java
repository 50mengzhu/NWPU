package com.zyw.nwpulib.model;

import com.avos.avoscloud.AVUser;

/**
 * 
 * 论坛评论数据
 * 
 * @author Rocket
 * 
 */
public class LikeData {

	// 点赞用户相关
	public AVUser creator = null;
	public String nickName = ""; // 昵称
	public String headImgUrl = "";// 头像
	public String userId = "";// ID
	public int gender = -1;// 性别
	public String schoolName = ""; // 学校
	public String degree = "";// 学历
	public String college = ""; // 学院
	public String studentId = ""; // 学号

	public String createTime = null;
	public boolean hasRead = false;

	// 贴子相关
	public String targetTopicId = "";
	public String targetTopicContent = "";
	public String targetTopicImageURL = "";

	public LikeData() {
	}
}
