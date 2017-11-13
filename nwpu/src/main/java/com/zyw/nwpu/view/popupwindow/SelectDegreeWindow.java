package com.zyw.nwpu.view.popupwindow;

import com.zyw.nwpu.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

public class SelectDegreeWindow extends PopupWindow {
	public View view;

	public SelectDegreeWindow() {

	}

	public void showWindow(Context mContext, View parent, OnClickListener l) {
		// 布局
		view = View.inflate(mContext, R.layout.popupwindow_degree, null);

		view.findViewById(R.id.sv_main).startAnimation(
				AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		view.findViewById(R.id.tv_d1).setOnClickListener(l);
		view.findViewById(R.id.tv_d2).setOnClickListener(l);
		view.findViewById(R.id.tv_d3).setOnClickListener(l);
		view.findViewById(R.id.tv_d4).setOnClickListener(l);

		view.findViewById(R.id.tv_y1).setOnClickListener(l);
		view.findViewById(R.id.tv_y2).setOnClickListener(l);
		view.findViewById(R.id.tv_y3).setOnClickListener(l);

		view.findViewById(R.id.tv_b1).setOnClickListener(l);
		view.findViewById(R.id.tv_b2).setOnClickListener(l);
		view.findViewById(R.id.tv_b3).setOnClickListener(l);
		view.findViewById(R.id.tv_b4).setOnClickListener(l);
		view.findViewById(R.id.tv_b5).setOnClickListener(l);

		view.findViewById(R.id.tv_js).setOnClickListener(l);

		// view.findViewById(R.id.root_view).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// dismiss();
		// }
		// });
	}

}
