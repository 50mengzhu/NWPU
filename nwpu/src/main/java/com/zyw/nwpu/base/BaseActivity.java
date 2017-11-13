package com.zyw.nwpu.base;

import org.xutils.x;

import com.avos.avoscloud.AVAnalytics;
import com.zyw.nwpu.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;

public abstract class BaseActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		initView();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_right);
	}

	public void initView() {

	}

	// 等待窗口
	private ProgressDialog pd = null;

	protected void showProgressDialog() {
		showProgressDialog("");
	}

	protected void showProgressDialog(String tip) {
		pd = null;
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(true);
		if (!TextUtils.isEmpty(tip)) {
			pd.setMessage(tip);
		} else {
			pd.setMessage("请稍等...");
		}
		pd.show();
	}

	protected void dismissProgressDialog() {
		if (pd != null)
			pd.dismiss();
		pd = null;
	}

	// private void checkNetWork() {
	// if (!CommonUtil.NetworkUtils.checkNetState(getApplicationContext())) {
	// CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "【"
	// + AppUtils.getAppName(getApplicationContext()) + "】"
	// + "请检查您的网络连接");
	// }
	// }

	protected void onPause() {
		super.onPause();
		AVAnalytics.onPause(this);
	}

	protected void onResume() {
		super.onResume();
		AVAnalytics.onResume(this);
	}

	@Override
	protected void onDestroy() {
		dismissProgressDialog();
		super.onDestroy();
	}
}
