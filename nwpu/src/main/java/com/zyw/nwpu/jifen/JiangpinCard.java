package com.zyw.nwpu.jifen;

/**
 * Created by 13202 on 2016/12/6.
 */

/**
 * 每一个奖品item对应类
 */
public class JiangpinCard {
	private String mDescription;
	private String mJifen;
	private String mImgUrl;

	/**
	 * @param mDescription
	 *            奖品的名字
	 * @param mJifen
	 *            奖品需要的兑换积分
	 * @param Url
	 *            奖品的图片
	 */
	public JiangpinCard(String mDescription, String mJifen, String Url) {
		this.mDescription = mDescription;
		this.mJifen = mJifen;
		this.mImgUrl = Url;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getJifen() {
		return mJifen;
	}

	public void setJifen(String mJifen) {
		this.mJifen = mJifen;
	}

	public String getImgUrl() {
		return mImgUrl;
	}

	public void setImgUrl(String mImgUrl) {
		this.mImgUrl = mImgUrl;
	}
}
