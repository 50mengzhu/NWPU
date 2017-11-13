package com.zyw.nwpu.wlan;

import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.wlan.WifiServer.WifiLoginStatus;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.SPUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

@ContentView(R.layout.activity_wlan_login)
public class WLANLoginActivity extends BaseActivity implements OnClickListener {

	private CheckBox cb_savepw;// 保存密码
	private EditText et_username;
	private EditText et_password;
	private Button bt_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle();
		iniView();
		// showTip();
	}

	private void showTip() {
		String tip = "1.点击记住密码后，可自动重连校园无线网，保证不掉线；\n2.保持App后台运行，不进入App也可以摇一摇连接校园wifi。";
		// 第一次进入会有提示
		if (!SPUtils.contains(getApplicationContext(), "hasShowWlanTip")) {
			new AlertDialog.Builder(WLANLoginActivity.this).setTitle("使用小提示").setMessage(tip)
					.setPositiveButton("知道了", null).show();
			SPUtils.put(getApplicationContext(), "hasShowWlanTip", true);
		}
	}

	private void iniView() {
		et_username = (EditText) findViewById(R.id.usname_edit);
		et_password = (EditText) findViewById(R.id.pass_edit);
		cb_savepw = (CheckBox) this.findViewById(R.id.Reme_chk);
		bt_login = (Button) findViewById(R.id.Log_but);

		bt_login.setOnClickListener(this);

		// 将保存在本地的账号和密码显示在控件上
		String username = WifiServer.getWifiUsername(getApplicationContext());
		String password = WifiServer.getWifiPassword(getApplicationContext());
		et_username.setText(username);
		et_password.setText(password);
	}

	private void initTitle() {
		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("校园网");
		bar.setActionTextVisible(false);
		// bar.setActionText("快捷方式");
		// bar.setActionTextClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// createShortcut();
		// }
		// });
	}

	/**
	 * 创建wifi的快捷方式
	 */
	private void createShortcut() {
		WifiServer.createShortCut(this);
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, WLANLoginActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.Log_but:
			// 登陆
			login();
			break;
		default:
			break;
		}
	}

	private void login() {
		String username = et_username.getText().toString().trim();
		String password = et_password.getText().toString().trim();

		// 判断是否输入内容
		if (TextUtils.isEmpty(username)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请输入学号");
			return;
		}

		if (TextUtils.isEmpty(password)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请输入密码");
			return;
		}

		// 是否将账号密码保存到本地
		if (cb_savepw.isChecked()) {
			WifiServer.saveWifiUserInfo(getApplicationContext(), username, password);
		}

		// 打开等待窗口
		showProgressDialog();

		// 登录
		WifiServer server = new WifiServer(this.getApplicationContext());

		server.setOnWifiLoginListner(new WifiServer.OnWifiLoginListner() {

			@Override
			public void onWifiLogin(WifiLoginStatus status) {
				switch (status) {
				case LOGIN_SUCCESS:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "登陆成功");
					WLANLoginActivity.this.onBackPressed();
					return;

				case LOGOUT_SUCCESS:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "您已经成功下线!");
					break;

				case ARREARAGE:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "您的账户已欠费，为了不影响您正常使用网络，请尽快缴费！");
					break;

				case MAX_USERS:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "用户数量已达上限!");
					break;

				case WRONG_PW:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "登录失败,请检查用户名和密码！");
					break;

				case NO_DATA_LEFT:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "无可用流量！");
					break;
				case SERVICE_DENIED:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "服务器设备拒绝请求！");
					break;

				case WIFI_CLOSED:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请打开WiFi，并连接到校园无线网");
					break;

				case NO_CONNECTION:
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请先连接到校园无线网");
					break;

				default:
					break;
				}

				// 关闭等待窗口
				dismissProgressDialog();
			}
		});

		// 登录
		server.login(username, password);
	}
}
