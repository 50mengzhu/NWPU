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

public class MPopupWindow extends PopupWindow {
	public View view;

	public MPopupWindow() {

	}

	@SuppressWarnings("deprecation")
	public void showWindow(Context mContext, View parent, OnClickListener l,
			int textSize) {
		// 布局
		view = View.inflate(mContext, R.layout.popupwindow, null);

		// view.startAnimation(AnimationUtils.loadAnimation(mContext,
		// R.anim.fade_ins));
		view.findViewById(R.id.ll_menu).startAnimation(
				AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		view.findViewById(R.id.ll_small).setOnClickListener(l);
		view.findViewById(R.id.ll_medium).setOnClickListener(l);
		view.findViewById(R.id.ll_big).setOnClickListener(l);
		view.findViewById(R.id.ll_cancel).setOnClickListener(l);

		switch (textSize) {
		case 0:
			view.findViewById(R.id.iv_small).setVisibility(View.VISIBLE);
			view.findViewById(R.id.iv_medium).setVisibility(View.GONE);
			view.findViewById(R.id.iv_big).setVisibility(View.GONE);
			break;

		case 1:
			view.findViewById(R.id.iv_small).setVisibility(View.GONE);
			view.findViewById(R.id.iv_medium).setVisibility(View.VISIBLE);
			view.findViewById(R.id.iv_big).setVisibility(View.GONE);
			break;

		case 2:
			view.findViewById(R.id.iv_small).setVisibility(View.GONE);
			view.findViewById(R.id.iv_medium).setVisibility(View.GONE);
			view.findViewById(R.id.iv_big).setVisibility(View.VISIBLE);
			break;

		default:
			// 默认字号为中等字号
			view.findViewById(R.id.iv_small).setVisibility(View.GONE);
			view.findViewById(R.id.iv_medium).setVisibility(View.VISIBLE);
			view.findViewById(R.id.iv_big).setVisibility(View.GONE);
			break;

		}

		view.findViewById(R.id.root_view).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dismiss();
					}
				});

	}

}
