package com.zyw.nwpu.xmz;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;

@ContentView(R.layout.activity_xmz_projectevaluation)
public class EvaluationActivity extends BaseActivity implements
		View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initView() {
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, EvaluationActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
