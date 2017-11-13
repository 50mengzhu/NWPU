package com.zyw.nwpulib.model;

/**
 * 
 * 推送类
 * 
 * @author Rocket
 * 
 */
public class PushEntity {

	public String id = "";// 推送id
	public String title = "";// 推送标题
	public String time = "";// 推送时间
	public String url = "";// 推送链接地址
	public String content = "";// 推送内容
	public int read_num = 0;// 已读人数

	public PushEntity() {
	}

}