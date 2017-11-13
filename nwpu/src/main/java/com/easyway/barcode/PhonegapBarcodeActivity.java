package com.easyway.barcode;

import com.phonegap.*;
import com.zyw.nwpu.AddUserInfoActivity;
import com.zyw.nwpu.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

public class PhonegapBarcodeActivity extends DroidGap {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.loadUrl("file:///android_asset/www/html/introduction.html");
		// super.loadUrl("file:///android_asset/www/html/signinAndup.html");
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, PhonegapBarcodeActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监听后退键的代码
			promptExit(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static void promptExit(final Context con) {
		LayoutInflater li = LayoutInflater.from(con);

		CustomDialog.Builder builder = new CustomDialog.Builder(con);
		builder.setMessage("是否退出项目制程序");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				System.exit(0);// 设置退出
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

}
