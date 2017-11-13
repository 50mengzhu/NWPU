package com.zyw.nwpulib.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

/**
 * 2014年10月20日
 * 
 * BBS模块的评论
 * 
 * @author Rocket
 * 
 */
@AVClassName("comments")
public class Comment extends AVObject {

	public static final String CLASSNAME = "comments";
	public static final String CREATOR = "creator";

	// public final static int TYPE_TXT = 0;
	// public final static int TYPE_SND = 1;

	public Comment() {
		super();
	}

	public void setCreator(AVUser user) {
		super.put("creator", user);
	}

	public AVUser getCreator() {
		return (AVUser) super.get("creator");
	}

	public void setText(String txt) {
		super.put("content", txt);
	}

	public String getText() {
		return super.getString("content");
	}

	// /**
	// * 设置评论的类型
	// * @param type
	// */
	// public void setType(int type){
	// super.put("type", type);
	// }
	// /**
	// * 获取评论的类型
	// * @return
	// */
	// public int getType(){
	// return super.getInt("type");
	// }
	// /**
	// * 评论里添加声音
	// * @param snd
	// */
	// public void setSnd(AVFile snd){
	// super.put("content_snd", snd);
	// }
	// /**
	// * 获取评论中的声音文件
	// * @return
	// */
	// public AVFile getSnd(){
	// return super.getAVFile("content_snd");
	// }
	// /**
	// * 设置发布评论时的地理坐标
	// * @param loc
	// */
	// public void setLoc(AVGeoPoint loc){
	// super.put("location", loc);
	// }
	// /**
	// * 获取发布评论时的地理坐标
	// * @return
	// */
	// public AVGeoPoint getLoc(){
	// return super.getAVGeoPoint("location");
	// }
	// /**
	// * 设置发布评论时的地名
	// * @param locname
	// */
	// public void setLocName(String locname){
	// super.put("location_name", locname);
	// }
	// /**
	// * 获取发布评论时的地名
	// * @return
	// */
	// public String getLocName(){
	// return super.getString("location_name");
	// }
	// /**
	// * 点赞
	// */
	// public void addLikerNum(){
	// super.put("liker_num", super.getInt("liker_num")+1);
	// }
	// /**
	// * 取消点赞
	// */
	// public void removeLikerNum(){
	// super.put("liker_num", super.getInt("liker_num")-1);
	// }
}