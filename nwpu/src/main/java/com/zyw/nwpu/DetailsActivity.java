package com.zyw.nwpu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.AVCloudMethod;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.AddScoreRecordCallback;
import com.zyw.nwpulib.model.NewsEntity;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.service.ShareHelper;
import com.zyw.nwpu.tool.AppUtils;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpu.tool.HttpUtils;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
@ContentView(R.layout.details)
public class DetailsActivity extends BaseActivity implements OnClickListener {
	private ProgressBar progressBar;
	private String news_url;
	private String news_title;
	private NewsEntity news;

	private WebView webView;

	private TextView tv_cmt_count;
	private TextView tv_like_count;

	// 底部工具条
	private ImageView iv_action_like;

	private int likenum;
	private int commentnum;
	private Boolean isLiked = false;
	private int catid;
	private int newsid;
	private String cat_name;

	// webview滑动所需的参数
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		iniActivityView();
		initWebView();
		checkNetWork();

		iniTitle();

		// 增加积分
		ScoreHelper.addScore("阅读文章", 1, new AddScoreRecordCallback() {

			@Override
			public void onSuccess() {
				ToastUtils.showShortToast("阅读文章 增加1积分");
			}

			@Override
			public void onFailure(String errTip) {
				// TODO Auto-generated method stub
			}
		});

	}

	private void iniTitle() {
		titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle(cat_name);
		titleBar.setMoreIconVisible(true);
		titleBar.setMoreIconClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showMenu();
			}
		});
	}

	protected void showMenu() {
		AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);

		String[] items = new String[] { "复制链接", "在浏览器打开" };

		if (AccountHelper.isLogedIn(getApplicationContext())) {
			if (AVUser.getCurrentUser() != null) {
				if (AVUser.getCurrentUser().getBoolean(XUser.IS_ADMIN)) {
					items = null;
					items = new String[] { "复制链接", "在浏览器打开", "推送" };
				}
			}
		}

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
				default:
					CommonUtil.SystemUtils.copy(getApplicationContext(), news_url);
					break;
				case 1:
					CommonUtil.openBrowser(DetailsActivity.this, news_url);
					break;
				case 2:
					toPushThisNews();
					break;
				}
			}
		});
		builder.show();
	}

	private void toPushThisNews() {
		new AlertDialog.Builder(DetailsActivity.this).setTitle("确定要推送这条新闻吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 进行推送

						AVCloudMethod.pushUrl("瓜大生活", news_title, cat_name, news_url, new FunctionCallback<String>() {

							@Override
							public void done(String arg0, AVException arg1) {
								if (arg1 != null) {
									CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
											arg1.getLocalizedMessage());
									return;
								}

								if (!TextUtils.isEmpty(arg0) && arg0.compareTo("success") == 0) {
									CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "推送成功");
								}
							}
						});

					}
				}).setNegativeButton("取消", null).show();
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
			CommonUtil.openBrowser(DetailsActivity.this, news_url);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void iniActivityView() {
		progressBar = (ProgressBar) findViewById(R.id.ss_htmlprogessbar);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setMax(100);

		webView = (WebView) findViewById(R.id.wv_detail);

		// 底部工具条
		findViewById(R.id.action_share).setOnClickListener(this);
		findViewById(R.id.action_like).setOnClickListener(this);
		findViewById(R.id.action_view_comment).setOnClickListener(this);

		// 点赞和评论数
		iv_action_like = (ImageView) findViewById(R.id.action_like);
		tv_cmt_count = (TextView) findViewById(R.id.action_comment_count);
		tv_like_count = (TextView) findViewById(R.id.action_like_count);

		// 显示点赞和评论数
		tv_like_count.setText("赞" + String.valueOf(likenum));
		tv_cmt_count.setText("评论" + String.valueOf(commentnum));

		tv_cmt_count.setVisibility(View.VISIBLE);
		tv_like_count.setVisibility(View.VISIBLE);

	}

	private void checkNetWork() {
		if (!CommonUtil.NetworkUtils.checkNetState(getApplicationContext())) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"【" + AppUtils.getAppName(getApplicationContext()) + "】" + "请检查您的网络连接");
		}
	}

	/* 获取传递过来的数据 */
	private void getData() {
		news = (NewsEntity) getIntent().getSerializableExtra("news");

		cat_name = getIntent().getExtras().getString("cat_name");
		news_url = news.getSource_url();
		news_title = news.getTitle();
		likenum = news.getLikeNum();
		commentnum = news.getCommentNum();
		catid = news.getCatId();
		newsid = news.getNewsId();
	}

	@SuppressWarnings("deprecation")
	private void initWebView() {
		if (!TextUtils.isEmpty(news_url)) {
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
			settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			settings.setUseWideViewPort(false); // 打开页面时， 自适应屏幕
			settings.setLoadWithOverviewMode(true);// 打开页面时， 自适应屏幕
			settings.setSupportZoom(true);// 用于设置webview放大
			settings.setBuiltInZoomControls(false);
			settings.setBlockNetworkImage(false);// 是否启用无图模式
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			settings.setDomStorageEnabled(true);

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
			webView.loadUrl(news_url);
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

	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.action_like:
			addLike();
			break;

		case R.id.action_view_comment:
			toViewComment();
			break;

		case R.id.action_share:
			Toast.makeText(getApplicationContext(), "微博分享暂不可用", Toast.LENGTH_SHORT).show();
			share("[" + CommonUtil.AppUtils.getAppName(getApplicationContext()) + "]" + news_title + ":" + news_url);
			break;

		default:
			break;
		}
	}

	private void addLike() {
		if (!isLiked) {
			iv_action_like.setImageResource(R.drawable.ic_action_like_pressed);
			likenum++;
			tv_like_count.setText("赞" + String.valueOf(likenum));
			/* 这里添加一个动画更好 */
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "点赞成功");

			// 向服务器提交
			HttpUtils.doPostAsyn(null, Const.likeurl, "news_id=" + newsid);

			isLiked = true;
		} else {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "您已经赞过");
		}
	}

	private void toViewComment() {
		Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
		intent.putExtra("catid", catid);
		intent.putExtra("newsid", newsid);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	// private void toWriteComment() {
	// if (!AccountHelper.isLogedIn(getApplicationContext())) {
	// CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
	// "请先登陆");
	// toLogin();
	// return;
	// }
	//
	// Intent intent = new Intent(getApplicationContext(),
	// WriteCommentActivity.class);
	// // 这里应提交相关参数
	// intent.putExtra("newsid", newsid);
	// intent.putExtra("catid", catid);
	// startActivity(intent);
	// overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	// }

	private void toLogin() {
		startActivity(new Intent(getApplicationContext(), Login.class));
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void share(String text) {
		String path = "file:///android_asset/share.png";
		ShareHelper.showShare(this, news_title, "瓜大生活", "http://www.baidu.com", path);
		String dir = CommonUtil.FileUtils.getRootFilePath() + "com.bj/snapshot/";
		String imgFileName = System.currentTimeMillis() + ".JPEG";
		CommonUtil.ScreenUtils.snapShotWithoutStatusBarAndSave(this, dir, imgFileName);
		ShareHelper.showShare(this, news_title, "瓜大生活", news_url, dir + imgFileName);

		// Intent itt = new Intent(Intent.ACTION_SEND);
		// itt.setType("text/plain");
		// itt.putExtra(Intent.EXTRA_TEXT, text);
		// itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(Intent.createChooser(itt, "分享到："));
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
		getCmtLikeNum();
	}

	private void getCmtLikeNum() {
		HttpUtils.doPostAsyn(numHandler, Const.cmt_likenumurl, "news_id=" + newsid + "&category_id=" + catid);
	}

	@SuppressLint("HandlerLeak")
	public Handler numHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				parseResult(msg.obj.toString());
			}
		}
	};

	protected void parseResult(String text) {
		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;

		try {
			js = (JSONObject) jsonParser.nextValue();
			JSONArray ja = js.getJSONArray("news");
			int cmtnum = ja.getJSONObject(0).getInt("comment_num");
			int likenum = ja.getJSONObject(0).getInt("like_num");

			tv_cmt_count.setText("评论" + String.valueOf(cmtnum));
			tv_like_count.setText("赞" + String.valueOf(likenum));

		} catch (JSONException e) {
		}
	}

	@Override
	public void initView() {

	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
	}

}