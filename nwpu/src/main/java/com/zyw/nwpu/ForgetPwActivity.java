package com.zyw.nwpu;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.tool.HttpUtils;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_forgetpw)
public class ForgetPwActivity extends BaseActivity implements OnClickListener {

	private EditText et_schoolnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("找回密码");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit_forgetpw:
			getbackPw();
			break;
		}
	}

	private void getbackPw() {
		final String schoolnum = et_schoolnum.getText().toString().trim();

		if (TextUtils.isEmpty(schoolnum)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请输入学号");
			return;
		}

		if (schoolnum.length() != 10) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请输入10位学号");
			return;
		}

		showProgressDialog();

		// 找回密码
		String forgetPwUrl = Const.getbackpwurl;
		HttpUtils.doPostAsyn(mHandle, forgetPwUrl, "username=" + schoolnum);
	}

	@SuppressLint("HandlerLeak")
	public Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"密码已发送至该学号绑定的邮箱，请注意查收");
			onBackPressed();
			// if (msg.obj != null) {
			// parseResult(msg.obj.toString());
			// }

		}
	};

	private void parseResult(String text) {
		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;

		try {
			js = (JSONObject) jsonParser.nextValue();
			// JSONObject jsonObject = js.getJSONObject("back");
			String e = js.getString("back");
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), e);
			//
			// if (TextUtils.isEmpty(e)) {
			// // 登陆成功
			// CommonUtil.showShortToast(getApplicationContext(), "登陆成功");
			// finish();
			// overridePendingTransition(R.anim.slide_in_left,
			// R.anim.slide_out_right);
			// } else {
			// // 登陆失败，显示错误原因
			// }

		} catch (JSONException e) {
		}
	}

	@Override
	public void initView() {
		findViewById(R.id.btn_commit_forgetpw).setOnClickListener(this);
		et_schoolnum = (EditText) findViewById(R.id.et_schoolnum_forgetpw);
	}
}
