package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_calendar)
public class CalendarActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_1;
	private ImageView iv_2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle("校历");

		getData();
	}

	@Override
	public void initView() {
		iv_1 = (ImageView) findViewById(R.id.iv_1);
		iv_2 = (ImageView) findViewById(R.id.iv_2);
		iv_1.setOnClickListener(this);
		iv_2.setOnClickListener(this);
	}

	private void getData() {
		int w = getIntent().getExtras().getInt("screenwidth");
		int h = w * 714 / 862;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, h);

		params.topMargin = CommonUtil.ScreenUtils.dp2px(
				getApplicationContext(), 10);
		params.bottomMargin = params.topMargin;
		params.leftMargin = CommonUtil.ScreenUtils.dp2px(
				getApplicationContext(), 20);
		params.rightMargin = params.leftMargin;

		iv_1.setLayoutParams(params);
		iv_2.setLayoutParams(params);
	}

	public static void startThis(Context cxt, int w) {
		Intent intent = new Intent(cxt, CalendarActivity.class);
		intent.putExtra("screenwidth", w);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_1:
			PhotoViewActivity.startThis(this, R.drawable.calendar1);
			break;

		case R.id.iv_2:
			PhotoViewActivity.startThis(this, R.drawable.calendar2);
			break;
		}
	}

}
