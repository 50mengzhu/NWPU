package com.zyw.nwpu.view;

import com.zyw.nwpu.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class CommenWaitingDialog extends Dialog {

	public CommenWaitingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public CommenWaitingDialog(Context context, int theme) {
		super(context, theme);
	}

	public CommenWaitingDialog(Context context) {
		super(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.window_waite_publish, null);
		this.setContentView(view);
	}
}
