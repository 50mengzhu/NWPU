package com.zyw.nwpu;

/**
 * Created by 13202 on 2016/12/15.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * 获取用户登录数据的准确性
 */

public class GetResponse {

	protected static String VERIFATIONURL = null;
	protected static String JSESSIONID;
	protected String MAINBODYHTML;
	protected String cookie;
	protected String PHPSESSID;
	public Bitmap bmVerifation;
	private static BasicCookieStore cookieStore = null;
	private SharedPreferences sharedPreferences;
	private EncryptAndDescrypt ead;// 用于储存密码时加密解密
	Handler handler;
	Message msg;
	Bundle bundle;
	String lt;
	String jsession_id;
	String img;
	private boolean isRememberPassword = true;
	protected static final String LOGINURL = "http://222.24.192.175/npulife_api/new_aoxiang/NewAoxiang_form.php";
	protected static final String POSTURL = "http://222.24.192.175/npulife_api/new_aoxiang/NewAoxiang_get.php";

	public GetResponse(Handler handler) {
		bundle = new Bundle();
		msg = new Message();
		this.handler = handler;
		DoGetVerifation();
	}

	public Bitmap getBmVerifation() {
		return bmVerifation;
	}

	public void DoGetVerifation() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet get = new HttpGet(LOGINURL);
				HttpResponse response = null;
				try {
					response = httpclient.execute(get);
					setCookieStore(response);
					InputStream is = response.getEntity().getContent();
					String content = convertStreamToString(is);
					SetReturnValues(content);// 设置get返回的json数据进行登录
					Log.i("info", content);

				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					showToast("请检查网络设置");
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				VERIFATIONURL = "http://222.24.192.175/npulife_api/new_aoxiang/" + img;
				HttpGet httpget = new HttpGet(VERIFATIONURL);
				httpget.addHeader("PHPSESSID", JSESSIONID);// 传入session数据
				HttpClient client = new DefaultHttpClient();

				try {
					HttpResponse httpResponse = client.execute(httpget);
					Log.i("info", VERIFATIONURL);
					byte[] bytes = new byte[1024];
					bytes = EntityUtils.toByteArray(httpResponse.getEntity());
					bmVerifation = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					Log.i("info", "get bitmap");
				} catch (IOException e) {
					showToast("真不好意思，获取验证码失败！");
					e.printStackTrace();
				}
				msg.arg1 = 3;
				handler.sendMessage(msg);
			}

		}).start();
	}

	private void showToast(String string) {
		msg = new Message();
		msg.arg1 = 1;
		bundle.putString("toast", string);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * @param json
	 *            get 得到的json数据
	 */
	private void SetReturnValues(String json) {
		try {
			JSONObject js = new JSONObject(json);
			lt = js.getString("lt");
			img = js.getString("img");
			jsession_id = js.getString("jsession_id");
		} catch (JSONException je) {
			je.printStackTrace();
			showToast("后台数据出错了！我们正在抓紧修复！^_^");
		}

	}

	/**
	 * @param httpResponse
	 *            储存cookie
	 */
	public static void setCookieStore(HttpResponse httpResponse) {
		System.out.println("----setCookieStore");
		cookieStore = new BasicCookieStore();
		// JSESSIONID
		String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
		System.out.println(setCookie);
		JSESSIONID = setCookie.substring("PHPSESSID=".length(), setCookie.indexOf(";"));
		System.out.println("JSESSIONID:" + JSESSIONID);
		// 新建一个Cookie
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
		cookie.setVersion(0);
		cookie.setDomain("127.0.0.1");
		cookie.setPath("/CwlProClient");

		cookieStore.addCookie(cookie);
	}

	private void getDengluStatus(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);

			if (jsonObject.getBoolean("flag")) {
				msg = new Message();
				msg.arg1 = 2;
				bundle = new Bundle();
				bundle.putString("status", jsonObject.getString("mgs"));
				msg.setData(bundle);
				handler.sendMessage(msg);
				Log.i("info", "登录成功");
			} else {
				msg = new Message();
				msg.arg1 = 4;
				bundle = new Bundle();
				bundle.putString("status", jsonObject.getString("msg"));
				msg.setData(bundle);
				handler.sendMessage(msg);
				Log.i("info", "登陆出问题了！");
			}

		} catch (JSONException je) {
			je.printStackTrace();
			showToast("请检查您的学号是否在新版翱翔门户登陆。");
		}
	}

	public void DoLogin(final String user, final String password, final String verifation) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (TextUtils.isEmpty(verifation)) {
					showToast("请输入验证码");
					return;
				}
				HttpClient defaultclient = new DefaultHttpClient();
				defaultclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				HttpPost httpPost = new HttpPost(POSTURL);
				PHPSESSID = "PHPSESSID=" + JSESSIONID;
				httpPost.addHeader("Cookie", PHPSESSID);
				httpPost.addHeader("Referer", LOGINURL);

				// 设置post参数
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("stu_no", user));
				params.add(new BasicNameValuePair("password", password));
				params.add(new BasicNameValuePair("verifycode", verifation));
				params.add(new BasicNameValuePair("lt", lt));
				params.add(new BasicNameValuePair("jsession_id", jsession_id));
				params.add(new BasicNameValuePair("subLogin", "提交"));

				// 获得个人主界面的HTML
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse httpResponse = defaultclient.execute(httpPost);
					Log.i("info", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						StringBuffer sb = new StringBuffer();
						HttpEntity entity = httpResponse.getEntity();// 获得httppost的响应内容
						MAINBODYHTML = EntityUtils.toString(entity);// 显示httppost的响应内容肉
						Log.i("info", MAINBODYHTML);
						getDengluStatus(MAINBODYHTML);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();

				}

			}
		}).start();

	}

}
