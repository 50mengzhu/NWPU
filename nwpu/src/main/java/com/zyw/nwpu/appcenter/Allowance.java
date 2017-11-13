package com.zyw.nwpu.appcenter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;

@ContentView(R.layout.activity_allowance)
public class Allowance extends BaseActivity implements OnClickListener {

	private static String MAINBODYHTML;
	private static final String POSTURL = "http://222.24.192.175/npulife_api/salary/salary_json.php";
	private SharedPreferences allowanceSP;
	private EditText et_name, et_time;
	private EditText et_password;
	private String name;
	private String password;
	private String flag;

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, Allowance.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initView() {
		// 需后期加密
		allowanceSP = this.getSharedPreferences("allowance", Context.MODE_WORLD_READABLE);

		findViewById(R.id.btn_allowance_login).setOnClickListener(this);
		et_name = (EditText) findViewById(R.id.et_allowancename_login);
		et_password = (EditText) findViewById(R.id.et_allowancepwd_login);
		et_time = (EditText) findViewById(R.id.time);

		// 默认记住用户名
		et_name.setText(allowanceSP.getString("userName", ""));
		et_password.setText(allowanceSP.getString("password", ""));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_allowance_login:
			login();
			break;
		}
	}

	protected void login() {
		// TODO Auto-generated method stub
		final String name = et_name.getText().toString().trim();
		final String password = et_password.getText().toString().trim();
		final String time = et_time.getText().toString().trim();

		Editor editor = allowanceSP.edit();
		editor.putString("userName", name);
		editor.putString("password", password);
		editor.commit();

		new Thread(new Runnable() {

			@Override
			public void run() {

				DefaultHttpClient defaultclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(POSTURL);
				// 设置post编码
				httppost.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
				httppost.getParams().setParameter(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
				httppost.getParams().setParameter(HTTP.CHARSET_PARAM, HTTP.UTF_8);
				httppost.getParams().setParameter(HTTP.DEFAULT_PROTOCOL_CHARSET, HTTP.UTF_8);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("std_num", password));
				params.add(new BasicNameValuePair("time", time));

				try {
					httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse httpResponse = defaultclient.execute(httppost);
					Log.i("my", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						StringBuffer sb = new StringBuffer();
						HttpEntity entity = httpResponse.getEntity();
						MAINBODYHTML = EntityUtils.toString(entity);
						// Log.i("my", MAINBODYHTML);
						IsLoginSuccessful(MAINBODYHTML);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.arg1 = 4;
					handler.sendMessage(msg);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.arg1 = 4;
					handler.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.arg1 = 4;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	protected void IsLoginSuccessful(String loginresult) {
		// TODO Auto-generated method stub
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(loginresult);
			flag = (String) jsonObject.get("flag");

			Message msg = new Message();
			if (flag.equals("1")) {
				// 成功
				msg.arg1 = 1;
				handler.sendMessage(msg);
			} else if (flag.equals("-1")) {
				// 密码错误
				msg.arg1 = 2;
				handler.sendMessage(msg);
			} else {
				// 无数据
				msg.arg1 = 3;
				handler.sendMessage(msg);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message msg = new Message();
			msg.arg1 = 3;
			handler.sendMessage(msg);
			// Toast.makeText(getApplicationContext(), "姓名学号不匹配",
			// Toast.LENGTH_SHORT).show();
			// new
			// AlertDialog.Builder(Allowance.this).setTitle("提示").setMessage("数据错误").setPositiveButton("确定",
			// null)
			// .show();
		}
		// Message msg = new Message();
		// if (flag =="-1") {
		// // Log.i("my", "密码或用户名错误");
		// msg.arg1 = 1;
		// handler.sendMessage(msg);
		// } else {
		// msg.arg1 = 2;
		// handler.sendMessage(msg);
		// }
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 1:
				show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "姓名学号不匹配", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				// Toast.makeText(getApplicationContext(), "数据不存在",
				// Toast.LENGTH_SHORT).show();
				new AlertDialog.Builder(Allowance.this).setTitle("提示").setMessage("数据不存在").setPositiveButton("确定", null)
						.show();
				break;
			case 4:
				Toast.makeText(getApplicationContext(), "网络故障", Toast.LENGTH_SHORT).show();
				new AlertDialog.Builder(Allowance.this).setTitle("提示").setMessage("可能为姓名学号不匹配")
						.setPositiveButton("确定", null).show();
				break;
			}
		}
	};

	private void show() {
		// 生成Intent对象（包含了activity间传的Data，param）;相当于一个请求
		Intent intent = new Intent();
		// 键值对
		intent.putExtra("json", MAINBODYHTML);
		// 从此activity传到另一Activity
		intent.setClass(Allowance.this, Allowanceshow.class);
		// 启动另一个Activity
		Allowance.this.startActivity(intent);
		// TODO Auto-generated method stub
		// Context mContext = Allowance.this;
		//
		// // 下面俩种方法都可以
		// //LayoutInflater inflater = getLayoutInflater();
		// LayoutInflater inflater = (LayoutInflater)
		// mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		// View layout = inflater.inflate(R.layout.allowanceshow, null);
		// setContentView(layout);
		//
		// TableLayout table = (TableLayout) findViewById(R.id.tablelayout);
		// // 全部列自动填充空白
		//
		// table.setStretchAllColumns(true);
		// try {
		// // {"name":"android","name":"iphone"}
		// // JSONObject demoJson = new JSONObject(jsonString);
		// // String name = demoJson.getString("name");
		// // String version = demoJson.getString("version");
		// // JSONArray jsonArray = new JSONArray(MAINBODYHTML);
		// JSONObject jsonObject = new JSONObject(MAINBODYHTML);
		//
		// String name = (String) jsonObject.get("name");
		// String salary = (String) jsonObject.get("salary");
		//
		// for (int i1 = 0; i1 < 1; i1++) {
		// TableRow tablerow = new TableRow(Allowance.this);
		// tablerow.setBackgroundColor(Color.rgb(222, 220, 210));
		// for (int j = 0; j < 2; j++) {
		//
		// TextView testview = new TextView(Allowance.this);
		// testview.setBackgroundResource(R.drawable.gardeshowtableshape);
		// if (j == 0) {
		// testview.setText(name);
		// } else {
		// testview.setText(salary);
		// }
		// // testview.setTextSize(20);
		//
		// // 设置TextView的宽度为N个字符的宽度。
		// testview.setEms(7);
		// // 设置文本是否包含顶部和底部额外空白，默认为true。
		// testview.setIncludeFontPadding(false);
		// tablerow.addView(testview);
		// }
		// table.addView(tablerow, new
		// TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// }
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
