package com.zyw.nwpu;

import org.xutils.common.util.MD5;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogInCallback;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.avos.AVCloudMethod;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Login extends Activity {

	private Toast mToast;

	GetResponse getResponse;
	clearEditText username;
	clearEditText password;
	EditText etverifation;
	private EncryptAndDescrypt ead;// 用于储存密码时加密解密
	ImageView ivVerifation;
	SharedPreferences sharedPreferences;
	protected String sUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd = new ProgressDialog(Login.this);

		ToastUtils.showShortToast("请用新版翱翔门户账号密码登录！");

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		// 初始化 sharedPreferences
		sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		ead = new EncryptAndDescrypt();
		initView();
		readDefaultPassword(); // 设置默认账号密码
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.arg1) {
				case 1: {
					showToast(msg.getData().getString("toast"));
					break;
				}
				case 2: {// 登录验证成功
					showToast(msg.getData().getString("status"));
					// 添加调用
					show(username.getText().toString());
					break;
				}
				case 3: {
					Log.i("info", "获取验证码成功");
					ivVerifation.setImageBitmap(getResponse.getBmVerifation());
					break;
				}
				case 4: {// 验证码或者密码出错处理
					showToast(msg.getData().getString("status"));
					break;
				}
				}
			}
		};
		getResponse = new GetResponse(handler);// 网络连接类
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, Login.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	private void show(String stuid) {
		// 保存学号
		AccountHelper.setStudentId(getApplicationContext(), stuid);

		onSSOLoginSuccess(stuid);
	}

	ProgressDialog pd = null;

	private void showWaiteDialog() {
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();
	}

	/**
	 * 
	 * 单点登录成功之后，先登录环信，然后检查是否注册LeanCloud，若没有注册则跳转到完善信息界面
	 * 
	 * @param stuid
	 *            学号/工号
	 */
	private void onSSOLoginSuccess(final String stuid) {

		showWaiteDialog();

		loginOrRegistHX(stuid, new LoginOrRegistHXCallback() {

			@Override
			public void onSuccess() {
				// 登录环信成功，下面登录Leancloud

				AVCloudMethod.checkRegistLeanCloud(getApplicationContext(), stuid, new FunctionCallback<String>() {

					@Override
					public void done(String arg0, AVException arg1) {
						if (arg1 != null) {
							CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
									"error: " + arg1.getLocalizedMessage());
							pd.dismiss();
							return;
						}

						if (arg0.trim().compareTo("yes") == 0) {
							// 已经注册过LeanCloud，直接登录
							// 登陆LeanCloud
							AVUser.logInInBackground(stuid, stuid, new LogInCallback<AVUser>() {

								@Override
								public void done(AVUser arg0, AVException arg1) {
									if (arg1 != null) {
										CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
												arg1.getLocalizedMessage());
										pd.dismiss();
										return;
									}

									// 登录成功
									onLoginCompleted(stuid);
								}
							});

						} else if (arg0.trim().compareTo("no") == 0) {
							// 未注册，跳转到完善信息界面
							AddUserInfoActivity.startThis(Login.this);
							Login.this.finish();
						}
					}
				});
			}

			@Override
			public void onFailure(String msg) {
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(), msg);
				pd.dismiss();
			}
		});
	}

	/**
	 * 登录三个平台都成功之后
	 */
	private void onLoginCompleted(String stuid) {

		// 更新环信的昵称
		EMChatManager.getInstance().updateCurrentUserNick(AVUser.getCurrentUser().getString(XUser.NICKNAME));

		AccountHelper.setLogedIn(getApplicationContext(), true);
		AccountHelper.setStudentId(getApplicationContext(), stuid);
		Login.this.onBackPressed();

		// 查询手机号是否为空
		if (AVUser.getCurrentUser() != null && !TextUtils.isEmpty(AVUser.getCurrentUser().getMobilePhoneNumber())) {
			startActivity(new Intent(Login.this, MainTabActivity.class));
		} else {
			SetPhoneNumActivity.startThis(Login.this);
		}
	}

	private interface LoginOrRegistHXCallback {
		public void onSuccess();

		public void onFailure(String msg);
	}

	/**
	 * 登录环信的流程 先直接登录，若提示用户不存在，则注册用户，注册成功之后再登录
	 * 
	 * @param stuid
	 * @param callback
	 */
	private void loginOrRegistHX(final String stuid, final LoginOrRegistHXCallback callback) {
		// 调用sdk登陆方法登陆聊天服务
		String pw = MD5.md5(stuid);
		EMChatManager.getInstance().login(stuid, pw, new EMCallBack() {

			@Override
			public void onSuccess() {
				// 加载会话
				EMChatManager.getInstance().loadAllConversations();

				if (callback != null)
					callback.onSuccess();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			// -1005 invalid user or password
			@Override
			public void onError(final int code, final String message) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (code == EMError.INVALID_PASSWORD_USERNAME || code == EMError.UNABLE_CONNECT_TO_SERVER) {
							registOnHX(stuid, callback);
						} else {
							if (callback != null)
								callback.onFailure(getString(R.string.Login_failed) + "\n" + message);
						}
					}
				});
			}
		});
	}

	private void registOnHX(final String stuid, final LoginOrRegistHXCallback callback) {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					String password = MD5.md5(stuid);
					EMChatManager.getInstance().createAccountOnServer(stuid, password);

					// 登录
					EMChatManager.getInstance().login(stuid, password, new EMCallBack() {

						@Override
						public void onError(final int code, final String message) {
							runOnUiThread(new Runnable() {
								public void run() {
									if (code == EMError.INVALID_PASSWORD_USERNAME) {
										registOnHX(stuid, callback);
									} else {
										if (callback != null)
											callback.onFailure(getString(R.string.Login_failed) + "\n" + message);
									}
								}
							});
						}

						@Override
						public void onProgress(int arg0, String arg1) {
						}

						@Override
						public void onSuccess() {
							runOnUiThread(new Runnable() {
								public void run() {
									if (!Login.this.isFinishing())
										if (callback != null)
											callback.onSuccess();
								}
							});
						}
					});

				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (callback == null)
								return;
							int errorCode = e.getErrorCode();
							if (errorCode == EMError.NONETWORK_ERROR) {
								callback.onFailure(getResources().getString(R.string.network_anomalies));
							} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
								callback.onFailure(getResources().getString(R.string.User_already_exists));
							} else if (errorCode == EMError.UNAUTHORIZED) {
								callback.onFailure(
										getResources().getString(R.string.registration_failed_without_permission));
							} else if (errorCode == EMError.ILLEGAL_USER_NAME) {
								callback.onFailure(getResources().getString(R.string.illegal_user_name));
							} else {
								callback.onFailure(
										getResources().getString(R.string.Registration_failed) + e.getMessage());
							}
						}
					});
				}
			}
		}).start();
	}

	void initView() {
		username = (clearEditText) findViewById(R.id.username);
		password = (clearEditText) findViewById(R.id.password);
		// 设置图标大小
		Drawable drawable1 = getResources().getDrawable(R.drawable.username);
		drawable1.setBounds(0, 0, 100, 100); // 设置边界
		username.setCompoundDrawables(drawable1, null, null, null);// 画在右边
		Drawable drawable2 = getResources().getDrawable(R.drawable.password);
		drawable2.setBounds(0, 0, 100, 100); // 设置边界
		password.setCompoundDrawables(drawable2, null, null, null);// 画在右边
		ivVerifation = (ImageView) findViewById(R.id.iv_verifation);
		etverifation = (EditText) findViewById(R.id.et_Verifation);
		/**
		 * 忘记密码设置 denglu_wangjimima = (TextView)
		 * findViewById(R.id.denglu_wangjimima);
		 * denglu_wangjimima.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//
		 * 给文字添加下划线 denglu_wangjimima.setOnClickListener(new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { showToast("正在开发中"); } });
		 **/
		((Button) findViewById(R.id.denglu_login)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(username.getText())) {
					// 设置晃动
					username.requestFocus();
					username.setShakeAnimation();
					// 设置提示
					showToast("学号不能为空");
					return;
				}

				if (TextUtils.isEmpty(password.getText())) {
					password.requestFocus();
					password.setShakeAnimation();
					showToast("密码不能为空");
					return;
				}
				sUsername = username.getText().toString().trim();
				String sPassword = password.getText().toString().trim();
				String sVerifation = etverifation.getText().toString().trim();
				storePassword(sUsername, sPassword);// 存储输入账号和密码
				getResponse.DoLogin(sUsername, sPassword, sVerifation);
			}
		});
	}

	/**
	 * 显示Toast消息
	 * 
	 * @param msg
	 */
	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	private void storePassword(String username, String password) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("DLuserName", ead.encrypt(username));
		editor.putString("DLpassword", ead.encrypt(password));
		editor.commit();
	}

	private void readDefaultPassword() {
		if (sharedPreferences.contains("DLuserName") && sharedPreferences.contains("DLpassword")) {
			username.setText(ead.decrypt(sharedPreferences.getString("DLuserName", "")));// DES加密
			password.setText(ead.decrypt(sharedPreferences.getString("DLpassword", "")));
			etverifation.requestFocus();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		// 点击back键时，系统默认执行的是应用程序当前Activity的finish()方法后跳出界面。
		super.finish();
		// MainTabActivity.startThis(Login.this);

	}
}
