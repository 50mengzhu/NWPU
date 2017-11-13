package com.zyw.nwpu.jifen;

import com.zyw.nwpu.Login;
import com.zyw.nwpu.R;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpulib.utils.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by 13202 on 2016/12/6.
 */
public class JfenGuizeActivity extends BaseActivity {
	WebView webView;

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		Intent intent = new Intent(cxt, JfenGuizeActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jifenguize);
		webView = (WebView) findViewById(R.id.jifenguize);
		webView.loadUrl("http://222.24.192.175/npulife_api/jifenguize.html");
	}
}
