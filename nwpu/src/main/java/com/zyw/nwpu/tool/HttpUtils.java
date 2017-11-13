package com.zyw.nwpu.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

//Http请求的工具类
public class HttpUtils {

	private static final int TIMEOUT_IN_MILLIONS = 5000;

	private static void sendHandlerMsg(Handler mhandler, String result) {
		if (mhandler != null) {
			Message message = new Message();
			message.obj = result;
			mhandler.sendMessage(message);
		}
	}

	/**
	 * 异步POST请求
	 * 
	 * @param mHandler
	 * @param urlStr
	 * @param params
	 */
	public static void doPostAsyn(final Handler mHandler, final String urlStr,
			final String params) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String result = HttpUtils.doPost(urlStr, params);
				sendHandlerMsg(mHandler, result);
			}
		}).start();
	}

	/**
	 * 异步POST请求，使用HttpClient
	 * 
	 * @param mHandler
	 * @param url
	 * @param param
	 */
	public static void doPostAsynByHttpClient(final Handler mHandler,
			final String url, final List<NameValuePair> param) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String result = doPostByHttpClient(url, param);
				sendHandlerMsg(mHandler, result);
			}
		}).start();
	}

	/**
	 * 同步POST请求，使用HttpClient
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String doPostByHttpClient(String url,
			List<NameValuePair> param) {

		HttpPost httpRequest = new HttpPost(url);
		try {

			HttpEntity httpentity = new UrlEncodedFormEntity(param, HTTP.UTF_8);
			httpRequest.setEntity(httpentity);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				/* 填写代码，定义收到POST消息之后如何做 */
				return EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8).trim();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 同步Get请求
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) {

		if (TextUtils.isEmpty(urlStr))
			return "";

		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else {
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
			}
			conn.disconnect();
		}
		return null;
	}

	/**
	 * 异步GET请求
	 * 
	 * @param mHandler
	 * @param urlStr
	 */
	public static void doGetAsyn(final Handler mHandler, final String urlStr) {
		new Thread() {
			public void run() {
				String result = doGet(urlStr);
				sendHandlerMsg(mHandler, result);

			};
		}.start();
	}

	/**
	 * 同步POST请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @throws Exception
	 * 
	 * @return 响应结果
	 */

	public static String doPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (param != null && !param.trim().equals("")) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
