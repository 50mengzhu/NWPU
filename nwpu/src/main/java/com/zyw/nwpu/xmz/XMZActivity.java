package com.zyw.nwpu.xmz;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_xmz)
public class XMZActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btn_projects).setOnClickListener(this);
		findViewById(R.id.btn_myproject).setOnClickListener(this);
		findViewById(R.id.btn_scan).setOnClickListener(this);

	}

	@Override
	public void initView() {
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, XMZActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_projects:
			ProjectsActivity.startThis(XMZActivity.this);
			break;

		case R.id.btn_myproject:
			MyProjectsActivity.startThis(XMZActivity.this);
			break;
		case R.id.btn_scan:
			ScanActivity.startThis(XMZActivity.this);
			break;
		}
	}
}
