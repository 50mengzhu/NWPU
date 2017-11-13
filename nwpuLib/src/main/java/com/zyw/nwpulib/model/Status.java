package com.zyw.nwpulib.model;

import java.io.Serializable;

import android.text.TextUtils;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

/**
 * 2014年10月20日
 * 
 * 修改 2014.11.27
 * 
 * @author Rocket
 * 
 */

@AVClassName("announce")
public class Status extends AVObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static String CLASSNAME = "announce";

	public final static String PUBLISHER = "creator";
	public final static String CONTENT_TXT = "content";
	public final static String CONTENT_IMG = "announce_img";
	public final static String CONTENT_SND = "sound";
	public final static String LOCATION = "location";
	public final static String LOCATION_NAME = "address";
	public final static String LIKE_NUM = "num_zan";
	public final static String COMMENT_NUM = "num_comments";
	public static final String ISANONYMOUS = "isAnonymous";
	public static final String LIKESTRING = "like_user";
	public static final String CHANNEL = "channel";
	public static final String PRIZE = "prize";
	public static final String STICK_LEVEL = "stickLevel";
	public static final String TAG = "tag";
	private static final String COMMENT = "comment_relation";
	public static final String POSITION = "address";

	public Status() {
		super();
	}

	public void setPrize(String prize) {
		super.put(PRIZE, prize);
	}

	public String getPrize() {
		return (String) super.get(PRIZE);
	}

	public void setChannel(String channel) {
		super.put(CHANNEL, channel);
	}

	public String getChannel() {
		return (String) super.get(CHANNEL);
	}

	public void setPublisher(AVUser currentUser) {
		super.put(PUBLISHER, currentUser);
	}

	public AVUser getPublisher() {
		return (AVUser) super.get(PUBLISHER);
	}

	public void setAnonymous(boolean isAnonymous) {
		super.put(ISANONYMOUS, isAnonymous);
	}

	public boolean getAnonymous() {
		return super.getBoolean(ISANONYMOUS);
	}

	public void setText(String txt) {
		super.put(CONTENT_TXT, txt);
	}

	public String getText() {
		return super.getString(CONTENT_TXT);
	}

	/**
	 * 状态里添加图片
	 * 
	 * @param img
	 */
	public void setImg(AVFile img) {
		super.put(CONTENT_IMG, img);
	}

	/**
	 * 获取状态里的图片
	 * 
	 * @return
	 */
	public AVFile getImg() {
		return super.getAVFile(CONTENT_IMG);
	}

	// public void setSound(AVFile snd) {
	// super.put(CONTENT_SND, snd);
	// }
	//
	// public AVFile getSound() {
	// return super.getAVFile(CONTENT_SND);
	// }

	public void setLoc(AVGeoPoint loc) {
		super.put(LOCATION, loc);
	}

	public AVGeoPoint getLoc() {
		return super.getAVGeoPoint(LOCATION);
	}

	/**
	 * 设置发布状态时的地名
	 * 
	 * @param locname
	 */
	public void setLocName(String locname) {
		super.put(LOCATION_NAME, locname);
	}

	/**
	 * 获取发布状态时的地名
	 * 
	 * @return
	 */
	public String getLocName() {
		return super.getString(LOCATION_NAME);
	}

	/**
	 * 点赞
	 */
	public void increaseLikerNum() {
		super.put(LIKE_NUM, super.getInt(LIKE_NUM) + 1);
	}

	/**
	 * 取消点赞
	 */
	public void decreaseLikerNum() {
		super.put(LIKE_NUM, super.getInt(LIKE_NUM) - 1);
	}

	/**
	 * 获取点赞数
	 * 
	 * @return
	 */
	public int getLikeNum() {
		String str = super.getString(LIKESTRING);
		if (TextUtils.isEmpty(str))
			return 0;
		else {
			String[] likeUserList = str.split(",");
			return likeUserList.length;
		}
	}

	/**
	 * 获取点赞者的id
	 * 
	 * @return
	 */
	public String getLikeUserIds() {
		return super.getString(LIKESTRING);
	}

	/**
	 * 是否点赞
	 * 
	 * @return Boolean
	 */
	public Boolean isLiked(String currentUserId) {
		String str = super.getString(LIKESTRING);
		if (TextUtils.isEmpty(str))
			return false;

		String[] likeUserList = str.split(",");
		for (String string : likeUserList) {
			if (!TextUtils.isEmpty(string)
					&& string.compareTo(currentUserId) == 0)
				return true;
		}

		return false;
	}

	/**
	 * 评论条数加1
	 */
	public void addCommentNum() {
		super.put(COMMENT_NUM, getCommentNum() + 1);
	}

	/**
	 * 获取评论条数
	 * 
	 * @return
	 */
	public int getCommentNum() {
		return super.getInt(COMMENT_NUM);
	}

	/**
	 * 获取评论列表
	 * 
	 * @return
	 */

	public AVRelation<Comment> getCommentsByRelation() {
		AVRelation<Comment> comments = getRelation(COMMENT);// Relation列的名字为comment
		return comments;
	}

	/**
	 * 添加评论到关系表
	 * 
	 * @param cmt
	 */
	public void addCommentToRelation(Comment cmt) {
		AVRelation<Comment> comments = getCommentsByRelation();// Relation列的名字为comment
		comments.add(cmt);
	}

}