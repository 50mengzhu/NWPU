package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.ProgressBar;

import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

	private ProgressBar progressBar;
	private WebView webView;
	private String webUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		initWebView();
		setSlideBackEnable(false);
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
			CommonUtil.openBrowser(WebViewActivity.this, webUrl);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static void startThis(Context cxt, String url, String title) {
		Intent intent = new Intent(cxt, WebViewActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("title", title);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	private void getData() {
		webUrl = getIntent().getExtras().getString("url");

		String title = getIntent().getExtras().getString("title");

		TitleBar titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle(title);
		titleBar.setMoreIconVisible(true);
		titleBar.setMoreIconClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showMenu();
			}
		});
	}

	protected void showMenu() {
		AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
		builder.setItems(new String[] { "复制链接", "在浏览器打开" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
				default:
					CommonUtil.SystemUtils.copy(getApplicationContext(), webUrl);
					break;
				case 1:
					CommonUtil.openBrowser(WebViewActivity.this, webUrl);
					break;
				}
			}
		});
		builder.show();
	}

	@Override
	public void initView() {
		progressBar = (ProgressBar) findViewById(R.id.pb_htmlprogessbar);
		webView = (WebView) findViewById(R.id.webview);

		progressBar.setMax(100);
		progressBar.setVisibility(View.VISIBLE);
		webView.setVisibility(View.VISIBLE);
	}

	@SuppressLint("SetJavaScriptEnabled")
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

			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
			settings.setDomStorageEnabled(true);

			settings.setLoadsImagesAutomatically(true); // 网络图片最后再加载

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

	@SuppressLint("SetJavaScriptEnabled")
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (parseScheme(url)) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	public boolean parseScheme(String url) {
		if (url.contains("https://mapi.alipay.com")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		// 直接返回
		// if (webView.canGoBack()) {
		// webView.goBack();
		// return;
		// }
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
	}

}
