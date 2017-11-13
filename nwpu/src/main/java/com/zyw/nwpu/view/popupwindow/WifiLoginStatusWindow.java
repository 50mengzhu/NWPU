package com.zyw.nwpu.view.popupwindow;

import com.zyw.nwpu.R;
import com.zyw.nwpu.wlan.WifiServer.WifiLoginStatus;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

public class WifiLoginStatusWindow extends PopupWindow {
	private View view;

	public WifiLoginStatusWindow() {

	}

	@SuppressWarnings("deprecation")
	public void showWindow(Context mContext, View parent, WifiLoginStatus status) {

		// 布局
		view = View.inflate(mContext, R.layout.popupwindow_wifilogin_success,
				null);

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

		view.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);

		switch (status) {
		case LOGIN_SUCCESS:
			tv_title.setText("您已登录到校园无线网！");
			break;
		case LOGOUT_SUCCESS:
			tv_title.setText("您已经成功下线!");
			break;
		case ARREARAGE:
			tv_title.setText("您的账户已欠费，为了不影响您正常使用网络，请尽快缴费！");
			break;
		case MAX_USERS:
			tv_title.setText("用户数量已达上限!");
			break;
		case WRONG_PW:
			tv_title.setText("登录失败,请检查用户名和密码！");
			break;
		case NO_DATA_LEFT:
			tv_title.setText("无可用流量！");
			break;
		case SERVICE_DENIED:
			tv_title.setText("服务器设备拒绝请求");
			break;
		case WIFI_CLOSED:
			tv_title.setText("请打开WiFi，并连接到校园无线网");
			break;
		case NO_CONNECTION:
			tv_title.setText("请先连接到校园无线网");
			break;

		default:
			break;
		}
	}
}
