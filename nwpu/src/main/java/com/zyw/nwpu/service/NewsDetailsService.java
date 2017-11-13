package com.zyw.nwpu.service;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zyw.nwpulib.model.CommentEntity;

import android.text.TextUtils;

public class NewsDetailsService {
//	private static String iframeUrl = "";

//	public static String getIframeUrl() {
//		return iframeUrl;
//	}

	public static String getPushContent(String news_title, String news_date,
			String content) {

		// 自定义标题
		String data = "<body>";
		data = data
				+ "<h1 align='center' style='margin-left:13px,margin-right:13px'>"
				+ "<span style='font-size:20px;'>" + "<strong>" + news_title
				+ "</strong>" + "</span>" + "</h1>";

		data = data + "<span style='font-size:18px;'>" + content + "</span>";
		
		data = data + "<p align='right' >"
				+ "<span style='font-size:16px;'>" + news_date + "</span>"
				+ "</p>";
		data = data + "</body>";
		return data;
	}

	public static String getNewsDetails(String url, String news_title,
			String news_date, Boolean isSchoolNews) {
		Document document = null;
		
		// 自定义标题
		String data = "<body>";
		data = data + "<p align='left' style='margin-left:13px'>"
				+ "<span style='font-size:18px;'>" + news_title + "</span>"
				+ "</p>";
		data = data + "<p align='right' style='margin-left:13px'>"
				+ "<span style='font-size:10px;'>" + news_date + "</span>"
				+ "</p>";
		data = data + "<hr style='color:#cccccc' size='1' />";

		try {
			document = Jsoup.connect(url).timeout(9000).get();
			Element element = null;
			if (!TextUtils.isEmpty(url) && document != null) {
				// 从这里开始筛选内容
				element = document.getElementById("vsb_content_2");

				if (element != null) {
					data = data + element.toString();
				}

				// 获取评论页面url
//				iframeUrl = "";

			}

			data = data + "</body>";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	

	public static String getNewsDetails(String url, String news_title,
			String news_date) {
		Document document = null;

		// 自定义标题
		String data = "<body>";
		// + "<h2 style='font-size:18px;'>"
		// + news_title + "</h2>";
		data = data + "<p align='left' style='margin-left:13px'>"
				+ "<span style='font-size:18px;'>" + news_title + "</span>"
				+ "</p>";
		data = data + "<p align='right' style='margin-left:13px'>"
				+ "<span style='font-size:10px;'>" + news_date + "</span>"
				+ "</p>";
		data = data + "<hr style='color:#cccccc' size='1' />";

		try {
			document = Jsoup.connect(url).timeout(9000).get();
			Element element = null;
			if (!TextUtils.isEmpty(url) && document != null) {

				// 内容
				element = document.getElementsByClass("content").get(1);

				// 给所有图片添加点击事件
				/* 在这里给所有的img标签添加属性：width = 100% */
				Elements img_elements = element.getElementsByTag("img");
				String imgUrls = "";
				if (img_elements != null && img_elements.size() != 0) {
					for (Element ele : img_elements) {
						imgUrls += ele.attr("src") + ",";
					}

					for (int i = 0; i < img_elements.size(); i++) {
						Element ele = img_elements.get(i);
						ele.attr("style", "width:100%");
						String script = "javascript:window.imagelistener.openImage('"
								+ imgUrls + "'," + String.valueOf(i) + ");";
						ele.attr("onclick", script);
					}
				}

				if (element != null) {
					data = data + element.toString();
				}

				// 获取评论页面url
//				iframeUrl = document.getElementById("comment_iframe").attr(
//						"src");

			}

			data = data + "</body>";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static String getNewsDetails2(String url) {
		Document document = null;

		// 自定义标题
		String data = "";

		try {
			document = Jsoup.connect(url).timeout(9000).get();
			
			Element element = null;
			if (!TextUtils.isEmpty(url) && document != null) {

				// 内容
				element = document.getElementsByClass("content").get(1);

				// 给所有图片添加点击事件
				/* 在这里给所有的img标签添加属性：width = 100% */
				Elements img_elements = element.getElementsByTag("img");
				String imgUrls = "";
				if (img_elements != null && img_elements.size() != 0) {
					for (Element ele : img_elements) {
						imgUrls += ele.attr("src") + ",";
					}

					for (int i = 0; i < img_elements.size(); i++) {
						Element ele = img_elements.get(i);
//						ele.attr("style", "width:100%");
						String script = "javascript:window.imagelistener.openImage('"
								+ imgUrls + "'," + String.valueOf(i) + ");";
						ele.attr("onclick", script);
					}
				}
				
				data +=document.toString();

				// 获取评论页面url
//				iframeUrl = document.getElementById("comment_iframe").attr(
//						"src");

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}


	public static ArrayList<CommentEntity> getComments(String cmtUrl) {

		ArrayList<CommentEntity> commentList = new ArrayList<CommentEntity>();
		if (TextUtils.isEmpty(cmtUrl))
			return commentList;

		Document document = null;

		try {
			document = Jsoup.connect(cmtUrl).timeout(9000).get();
			if (document != null) {
				// 获取评论
				Elements elements = document.getElementsByClass("comment");
				if (elements != null && elements.size() != 0) {

					Element comment_ele = elements.get(0);
					elements = comment_ele.getElementsByClass("title");

					if (elements != null && elements.size() != 0) {
						for (int i = 0; i < elements.size(); i++) {
							CommentEntity commentEntity = new CommentEntity();
							String content = comment_ele
									.getElementsByClass("content").get(i)
									.textNodes().get(0).text();

							String temp = comment_ele.getElementsByClass("rt")
									.get(i).getElementsByTag("a").get(1)
									.attr("onclick");

							String id = temp.substring(temp.indexOf("(") + 1,
									temp.indexOf(","));

							String contentid = temp.substring(
									temp.indexOf("'") + 1,
									temp.lastIndexOf("'"));

							String pubtime = comment_ele
									.getElementsByClass("title").get(i).text()
									.substring(0, 19);

							String name = comment_ele
									.getElementsByClass("title").get(i).text()
									.substring(20);// 评论者的昵称

							name = name.substring(0, name.indexOf("("));// 去掉括号中的学号

							int likenum = Integer.valueOf(comment_ele
									.getElementsByTag("font").get(1).text());

							// int commentid = Integer.valueOf(comment_ele
							// .getElementsByTag("font").get(1).id()
							// .substring(7));

							commentEntity.setId(id);
							commentEntity.setContentId(contentid);
							commentEntity.setContent(content);
							commentEntity.setPublishTime(pubtime);
							commentEntity.setUsername(name);
							commentEntity.setLikeNum(likenum);
							commentList.add(commentEntity);
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return commentList;
	}
}
