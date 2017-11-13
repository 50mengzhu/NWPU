package com.zyw.nwpu.jifen.leancloud;

/**
 * 
 * 积分详情类
 * 
 * @author Rocket
 * 
 */
public class ScoreDetail {

	private String description;
	private int score;// 可正可负，正为挣的积分，负为兑换礼品
	private String date;// 日期

	public ScoreDetail() {
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
