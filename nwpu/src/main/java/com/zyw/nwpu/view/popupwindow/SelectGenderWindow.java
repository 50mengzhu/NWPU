package com.zyw.nwpu.view.popupwindow;

import com.zyw.nwpu.R;
import com.zyw.nwpu.SettingsActivity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;

public class SelectGenderWindow extends PopupWindow {
	public View view;

	public SelectGenderWindow() {

	}

	public void showWindow(Context mContext, View parent, OnClickListener l) {
		// 布局
		view = View.inflate(mContext, R.layout.popupwindow_gender, null);

		view.findViewById(R.id.ll_menu).startAnimation(
				AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		view.findViewById(R.id.tv_man).setOnClickListener(l);
		view.findViewById(R.id.tv_woman).setOnClickListener(l);

//		view.findViewById(R.id.root_view).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						dismiss();
//					}
//				});
	}

}
