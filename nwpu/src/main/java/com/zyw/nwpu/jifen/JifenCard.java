package com.zyw.nwpu.jifen;

/**
 * Created by 13202 on 2016/12/2.
 */

public class JifenCard {
	private String operationName;
	private String date;
	private int score;

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public JifenCard(String operationName, String date, int score) {
		super();
		this.operationName = operationName;
		this.date = date;
		this.score = score;
	}

}
