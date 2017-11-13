package com.zyw.nwpu.view.popupwindow;

import com.zyw.nwpu.R;
import com.zyw.nwpu.SettingsActivity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;

public class ShakeWindow extends PopupWindow {
	public View view;

	public ShakeWindow() {

	}

	public void showWindow(Context mContext, View parent, OnClickListener l) {
		// 布局
		view = View.inflate(mContext, R.layout.popupwindow_shake, null);

		final RotateAnimation animation1 = new RotateAnimation(-2f, 2f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation1.setDuration(30);// 设置动画持续时间
		animation1.setRepeatMode(Animation.REVERSE);
		animation1.setRepeatCount(5);
		animation1.setFillAfter(false);// 动画执行完后是否停留在执行完的状态

		view.findViewById(R.id.ll_menu).startAnimation(animation1);

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		view.findViewById(R.id.tv_publish).setOnClickListener(l);
		view.findViewById(R.id.tv_feedback).setOnClickListener(l);
		view.findViewById(R.id.tv_sign).setOnClickListener(l);
		view.findViewById(R.id.root_view).setOnClickListener(l);
		view.findViewById(R.id.ll_menu).setClickable(false);
	}
}
