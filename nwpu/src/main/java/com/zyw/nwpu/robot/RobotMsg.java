package com.zyw.nwpu.robot;

import java.util.List;

/**
 * @author Rocket
 */
public class RobotMsg {

	public final static int TYPENUM = 4;

	public enum MsgType {
		SEND, RECEIVE, LIST, RECEIVE_IMAGE
	}

	private String content;
	private MsgType type;
	private String avatarUrl;
	private List<String> list;
	private String keyWord;

	public RobotMsg(String content, MsgType type, String avatarUrl) {
		super();
		this.content = content;
		this.type = type;
		this.avatarUrl = avatarUrl;
	}

	public RobotMsg() {
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MsgType getType() {
		return type;
	}

	public void setType(MsgType type) {
		this.type = type;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}
