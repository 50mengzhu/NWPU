package com.zyw.nwpu.jifen;

/**
 * Created by 13202 on 2016/12/2.
 */

public class DuihuanCard {
	private String mDescription;
	private String mText;
	private String mJifen;

	public DuihuanCard(String mDescription, String mText, String mJifen) {
		this.mDescription = mDescription;
		this.mText = mText;
		this.mJifen = mJifen;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

	public String getJifen() {
		return mJifen;
	}

	public void setJifen(String mJifen) {
		this.mJifen = mJifen;
	}
}
