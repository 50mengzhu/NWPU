package com.zyw.nwpu.appcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_ruiji)
public class RuijiActivity extends BaseActivity implements View.OnClickListener {

	protected static final String LOGINURL = "http://222.24.192.175/npulife_api/ruijie/ruijie_form.php";
	protected static final String POSTURL = "http://222.24.192.175/npulife_api/ruijie/ruijie_get.php";
	protected static String VERIFATIONURL = null;
	protected static String JSESSIONID;
	protected Bitmap bmVerifation;
	protected String MAINBODYHTML;
	protected String cookie;
	protected String PHPSESSID;
	private EditText et_schoolnum, etVerifation;
	private EditText et_password;
	private ImageView ivVerifation;
	private static BasicCookieStore cookieStore = null;
	private SharedPreferences sharedPreferences;
	private EncryptAndDescrypt ead;// 用于储存密码时加密解密
	private CheckBox mcb;
	private boolean isRememberPassword = true;

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, RuijiActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		ead = new EncryptAndDescrypt();

		super.onCreate(savedInstanceState);
		DoGetVerifation();
	}

	public void initView() {
		et_schoolnum = (EditText) findViewById(R.id.et_schoolnum_login);
		et_password = (EditText) findViewById(R.id.et_password_login);
		ivVerifation = (ImageView) findViewById(R.id.iv_verifation);
		etVerifation = (EditText) findViewById(R.id.etVerifation);
		mcb = (CheckBox) findViewById(R.id.et_ruiji_checkbox);
		mcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					isRememberPassword = true;
				else
					isRememberPassword = false;
			}
		});
		if (!isRememberPassword)
			mcb.setChecked(false);
		if (sharedPreferences.contains("userName") && sharedPreferences.contains("password")) {
			et_schoolnum.setText(ead.decrypt(sharedPreferences.getString("userName", "")));// DES加密
			et_password.setText(ead.decrypt(sharedPreferences.getString("password", "")));
		}
	}

	private void DoGetVerifation() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.i("info", "begin");
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet get = new HttpGet(LOGINURL);
				HttpResponse response = null;
				try {
					response = httpclient.execute(get);
					setCookieStore(response);

				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					Toast.makeText(RuijiActivity.this, "请检查网络设置", Toast.LENGTH_SHORT).show();
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				VERIFATIONURL = "http://222.24.192.175/npulife_api/ruijie/verifycode/verifyCode_" + JSESSIONID + ".jpg";
				HttpGet httpget = new HttpGet(VERIFATIONURL);
				httpget.addHeader("PHPSESSID", JSESSIONID);
				HttpClient client = new DefaultHttpClient();
				Log.i("info", "2");

				try {
					HttpResponse httpResponse = client.execute(httpget);
					Log.i("info", VERIFATIONURL);
					byte[] bytes = new byte[1024];
					bytes = EntityUtils.toByteArray(httpResponse.getEntity());
					bmVerifation = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					Log.i("info", "get bitmap");
				} catch (IOException e) {
					Toast.makeText(RuijiActivity.this, "无法读取验证码", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				if (bmVerifation == null)
					Toast.makeText(getApplicationContext(), "获取验证码失败请检查网络设置", Toast.LENGTH_SHORT).show();
				Message msg = new Message();
				msg.arg1 = 10;
				handler.sendMessage(msg);
			}
		}).start();
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

	private void login() {
		final String schoolnum = et_schoolnum.getText().toString().trim();
		final String password = et_password.getText().toString().trim();
		final String verifation = etVerifation.getText().toString().trim();

		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (isRememberPassword) {
			editor.putString("userName", ead.encrypt(schoolnum));
			editor.putString("password", ead.encrypt(password));
			editor.putBoolean("isRememberPassword", true);
		} else {
			editor.putString("userName", "");
			editor.putString("password", "");
			editor.putBoolean("isRememberPassword", false);
		}
		editor.commit();

		if (TextUtils.isEmpty(verifation)) {
			Toast.makeText(getApplicationContext(), "输入验证码", Toast.LENGTH_SHORT).show();
			return;
		}
		DoLogin(schoolnum, password, verifation);

	}

	private void DoLogin(final String user, final String password, final String verifation) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpClient defaultclient = new DefaultHttpClient();
				defaultclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				HttpPost httpPost = new HttpPost(POSTURL);
				PHPSESSID = "PHPSESSID=" + JSESSIONID;
				httpPost.addHeader("Cookie", PHPSESSID);
				httpPost.addHeader("Referer", LOGINURL);

				// 设置post参数
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", user));
				params.add(new BasicNameValuePair("password", password));
				params.add(new BasicNameValuePair("validatecode", verifation));
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
						IsLoginSuccessful(MAINBODYHTML);
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

	private void IsLoginSuccessful(String loginresult) {

		Message msg = new Message();
		if (loginresult.contains("验证码")) {
			// 刷新验证码
			DoGetVerifation();
			Log.i("info", "验证码错误");
			msg.arg1 = 0;
			handler.sendMessage(msg);
		} else if (loginresult.contains("密码")) {
			DoGetVerifation();
			Log.i("info", "密码或用户名错误");
			msg.arg1 = 1;
			handler.sendMessage(msg);
		} else {
			msg.arg1 = 2;
			handler.sendMessage(msg);
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 0:
				Toast.makeText(getApplicationContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "密码或用户名错误", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				showData();
				break;
			case 10:
				ivVerifation.setImageBitmap(bmVerifation);
				break;
			}
		}
	};

	private void showData() {
		Intent intent = new Intent(RuijiActivity.this, FlowdataActivity.class);
		intent.putExtra("Data", MAINBODYHTML);
		startActivity(intent);
		this.finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_login_login: {
			login();
			break;
		}
		}
	}
}
