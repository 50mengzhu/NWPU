package com.zyw.nwpu;

import org.xutils.common.util.MD5;
import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogInCallback;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.AVCloudMethod;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpulib.utils.CommonUtil;

@SuppressLint("SetJavaScriptEnabled")
@ContentView(R.layout.activity_webview)
public class SSOLoginActivity_bak_1219 extends BaseActivity {

	private ProgressBar progressBar;
	private WebView webView;
	private String webUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSlideBackEnable(false);
		CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "正在进入翱翔门户...");
		pd = new ProgressDialog(SSOLoginActivity_bak_1219.this);
		getData();
		initWebView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.webview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_openinbrowser:
			CommonUtil.openBrowser(SSOLoginActivity_bak_1219.this, webUrl);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, SSOLoginActivity_bak_1219.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	private void getData() {
		webUrl = Const.sso_login;

		TitleBar titleBar;
		titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle("登录");
		titleBar.setBackButtonVisible(true);
	}

	@Override
	public void initView() {
		progressBar = (ProgressBar) findViewById(R.id.pb_htmlprogessbar);
		webView = (WebView) findViewById(R.id.webview);

		progressBar.setMax(100);
		progressBar.setVisibility(View.VISIBLE);
		webView.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings("deprecation")
	private void initWebView() {
		if (!TextUtils.isEmpty(webUrl)) {
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
			settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			settings.setUseWideViewPort(false); // 打开页面时， 自适应屏幕
			settings.setLoadWithOverviewMode(true);// 打开页面时， 自适应屏幕
			settings.setSupportZoom(true);// 用于设置webview放大
			settings.setBuiltInZoomControls(false);
			settings.setBlockNetworkImage(false);// 是否启用无图模式
			settings.setLoadsImagesAutomatically(false); // 网络图片最后再加载
			settings.setRenderPriority(RenderPriority.HIGH);// 提高渲染优先级

			webView.setBackgroundResource(R.color.transparent);
			webView.setWebChromeClient(new MyWebChromeClient());
			webView.setWebViewClient(new MyWebViewClient());
			// 获取字号
			int textSize = AppSetting.getTextSize(getApplicationContext());
			switch (textSize) {
			case AppSetting.TS_SMALL:
				settings.setTextZoom(80);// 小
				break;
			case AppSetting.TS_MEDIUM:
				settings.setTextZoom(100);// 中
				break;
			case AppSetting.TS_BIG:
				settings.setTextZoom(120);// 大
				break;
			default:
				settings.setTextZoom(100);// 中
				break;
			}
			webView.loadUrl(webUrl);
		}
	}

	//
	// @SuppressLint("SetJavaScriptEnabled")
	// @SuppressWarnings("deprecation")
	// private void initWebView() {
	// if (!TextUtils.isEmpty(webUrl)) {
	// WebSettings settings = webView.getSettings();
	// settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
	// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	// settings.setUseWideViewPort(false); // 打开页面时， 自适应屏幕
	// settings.setLoadWithOverviewMode(true);// 打开页面时， 自适应屏幕
	// settings.setSupportZoom(true);// 用于设置webview放大
	// settings.setBuiltInZoomControls(false);
	// settings.setBlockNetworkImage(AppSetting.isBlockImage(getApplicationContext()));//
	// 是否启用无图模式
	//
	// settings.setCacheMode(WebSettings.LOAD_DEFAULT);
	// settings.setDomStorageEnabled(true);
	//
	// settings.setLoadsImagesAutomatically(false); // 网络图片最后再加载
	// settings.setRenderPriority(RenderPriority.HIGH);// 提高渲染优先级
	//
	// webView.setBackgroundResource(R.color.transparent);
	//
	// webView.setWebChromeClient(new MyWebChromeClient());
	// webView.setWebViewClient(new MyWebViewClient());
	//
	// // 获取字号
	// int textSize = AppSetting.getTextSize(getApplicationContext());
	// switch (textSize) {
	// case AppSetting.TS_SMALL:
	// settings.setTextZoom(80);// 小
	// break;
	// case AppSetting.TS_MEDIUM:
	// settings.setTextZoom(100);// 中
	// break;
	// case AppSetting.TS_BIG:
	// settings.setTextZoom(120);// 大
	// break;
	// default:
	// settings.setTextZoom(100);// 中
	// break;
	// }
	// webView.loadUrl(webUrl);
	// }
	// }

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress < 100) {
				progressBar.setProgress(newProgress);
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			if (url.startsWith(Const.sso_login_success)) {
				String stuid = url.substring(Const.sso_login_success.length() + 3,
						Const.sso_login_success.length() + 13);

				// 保存学号
				AccountHelper.setStudentId(getApplicationContext(), stuid);

				onSSOLoginSuccess(stuid);
			}
			return true;
		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			view.getSettings().setLoadsImagesAutomatically(true);
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			progressBar.setVisibility(View.GONE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	// private class MyWebViewClient extends WebViewClient {
	// @Override
	// public boolean shouldOverrideUrlLoading(WebView view, String url) {
	// view.loadUrl(url);
	// if (url.startsWith(Const.sso_login_success)) {
	// String stuid = url.substring(Const.sso_login_success.length() + 3,
	// Const.sso_login_success.length() + 13);
	//
	// // 保存学号
	// AccountHelper.setStudentId(getApplicationContext(), stuid);
	//
	// onSSOLoginSuccess(stuid);
	// }
	// return true;
	// }
	//
	// @SuppressLint("SetJavaScriptEnabled")
	// @Override
	// public void onPageFinished(WebView view, String url) {
	// view.getSettings().setJavaScriptEnabled(true);
	// super.onPageFinished(view, url);
	// }
	//
	// @Override
	// public void onPageStarted(WebView view, String url, Bitmap favicon) {
	// super.onPageStarted(view, url, favicon);
	// }
	//
	// @Override
	// public void onReceivedError(WebView view, int errorCode, String
	// description, String failingUrl) {
	// super.onReceivedError(view, errorCode, description, failingUrl);
	// }
	// }

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
							AddUserInfoActivity.startThis(SSOLoginActivity_bak_1219.this);
							SSOLoginActivity_bak_1219.this.finish();
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
									if (!SSOLoginActivity_bak_1219.this.isFinishing())
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
	 * 登录三个平台都成功之后
	 */
	private void onLoginCompleted(String stuid) {

		// 更新环信的昵称
		EMChatManager.getInstance().updateCurrentUserNick(AVUser.getCurrentUser().getString(XUser.NICKNAME));

		AccountHelper.setLogedIn(getApplicationContext(), true);
		AccountHelper.setStudentId(getApplicationContext(), stuid);
		SSOLoginActivity_bak_1219.this.onBackPressed();
		startActivity(new Intent(SSOLoginActivity_bak_1219.this, MainTabActivity.class));
	}
}
