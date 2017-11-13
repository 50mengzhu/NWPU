package com.zyw.nwpu.appcenter;

import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

@ContentView(R.layout.activity_app_grade)
public class GradeActivity extends BaseActivity implements OnClickListener {

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, GradeActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initView() {
		findViewById(R.id.tv_yjs).setOnClickListener(this);
		findViewById(R.id.tv_bks).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_yjs:
			GraduateGrade.startThis(GradeActivity.this);
			break;

		case R.id.tv_bks:
			UndergraduateGrade.startThis(GradeActivity.this);
			break;
		}
	}
}
