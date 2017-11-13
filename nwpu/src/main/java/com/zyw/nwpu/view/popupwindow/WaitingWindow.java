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
import android.widget.TextView;

public class WaitingWindow extends PopupWindow {
	public View view;

	public WaitingWindow() {

	}

	public void setText(String text) {
		((TextView) (view.findViewById(R.id.tv_waiting))).setText(text);
	}

	public void showWindow(Context mContext, View parent) {
		// 布局
		view = View.inflate(mContext, R.layout.popupwindow_waiting, null);

		// view.startAnimation(AnimationUtils.loadAnimation(mContext,
		// R.anim.fade_ins));
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

		view.findViewById(R.id.root_view).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dismiss();
					}
				});

	}

}
