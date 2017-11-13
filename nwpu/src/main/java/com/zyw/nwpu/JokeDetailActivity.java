package com.zyw.nwpu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.model.JokeEntity;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.service.NewsDetailsService;
import com.zyw.nwpu.tool.AppUtils;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.HttpUtils;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
@ContentView(R.layout.details)
public class JokeDetailActivity extends BaseActivity implements OnClickListener {
	private ProgressBar progressBar;
	private String joke_url;
	private String joke_title;
	private String joke_source;
	private String joke_date;
	private JokeEntity joke;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("笑话");

		getData();
		iniActivityView();
		initWebView();
		checkNetWork();
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
			CommonUtil.openBrowser(JokeDetailActivity.this, joke_url);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void iniActivityView() {

		progressBar = (ProgressBar) findViewById(R.id.ss_htmlprogessbar);
		progressBar.setVisibility(View.VISIBLE);

		webView = (WebView) findViewById(R.id.wv_detail);
		webView.setVisibility(View.GONE);

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
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "【"
					+ AppUtils.getAppName(getApplicationContext()) + "】"
					+ "请检查您的网络连接");
		}
	}

	/* 获取传递过来的数据 */
	private void getData() {
		joke = (JokeEntity) getIntent().getSerializableExtra("joke");
		cat_name = getIntent().getExtras().getString("cat_name");
		joke_url = joke.getUrl();
		joke_title = joke.getTitle();
		joke_date = joke.getPublishTime();
		likenum = joke.getLikeNum();
		commentnum = joke.getCommentNum();
		catid = joke.getCatId();
		newsid = joke.getId();
	}

	@SuppressWarnings("deprecation")
	private void initWebView() {
		if (!TextUtils.isEmpty(joke_url)) {
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
			settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			settings.setUseWideViewPort(false); // 打开页面时， 自适应屏幕
			settings.setLoadWithOverviewMode(true);// 打开页面时， 自适应屏幕
			settings.setSupportZoom(true);// 用于设置webview放大
			settings.setBuiltInZoomControls(false);
			settings.setBlockNetworkImage(AppSetting
					.isBlockImage(getApplicationContext()));// 是否启用无图模式

			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
			settings.setDomStorageEnabled(true);

			settings.setLoadsImagesAutomatically(false); // 网络图片最后再加载
			settings.setRenderPriority(RenderPriority.HIGH);// 提高渲染优先级

			webView.setBackgroundResource(R.color.transparent);

			// 添加js交互接口类，并起别名 imagelistner
			JavascriptInterface javascriptInterface = new JavascriptInterface(
					getApplicationContext());
			webView.addJavascriptInterface(javascriptInterface, "imagelistener");
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
			webView.loadUrl(joke_url);
			// new MyAsnycTask().execute(joke_url, joke_title, " " + joke_date);
		}
	}

	private class MyAsnycTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... urls) {
			String data;
			data = NewsDetailsService.getNewsDetails2(urls[0]);
			return data;
		}

		@Override
		protected void onPostExecute(String data) {
			webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
		}
	}

	// js通信接口
	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		// 该注解必须添加，否则安卓4.4以上版本不能调用该方法
		@android.webkit.JavascriptInterface
		public void openImage(String img, int index) {

			String[] imgs = img.split(",");
			ArrayList<String> imgsUrl = new ArrayList<String>();
			for (String s : imgs) {
				imgsUrl.add(s);
			}
			FullScreenPhotoViewActivity.startThis(context, imgsUrl.get(index));

		}
	}

	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
			// WebViewActivity.startThis(JokeDetailActivity.this, url, "");
			// return true;
		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			view.getSettings().setLoadsImagesAutomatically(true);
			progressBar.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			progressBar.setVisibility(View.GONE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress != 100) {
				progressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
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
			share("[校园门户]" + joke_title + ":" + joke_url);
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
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"点赞成功");

			// 向服务器提交
			HttpUtils.doPostAsyn(null, Const.likeurl, "news_id=" + newsid);

			isLiked = true;
		} else {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"您已经赞过");
		}
	}

	private void toViewComment() {
		Intent intent = new Intent(getApplicationContext(),
				CommentActivity.class);
		intent.putExtra("catid", catid);
		intent.putExtra("newsid", newsid);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void toLogin() {
		startActivity(new Intent(getApplicationContext(),
				Login.class));
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void share(String text) {
		Intent itt = new Intent(Intent.ACTION_SEND);
		itt.setType("text/plain");
		itt.putExtra(Intent.EXTRA_TEXT, text);
		itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(itt, "分享到："));
	}

	@Override
	protected void onResume() {
		super.onResume();
		getCmtLikeNum();
	}

	private void getCmtLikeNum() {
		HttpUtils.doPostAsyn(numHandler, Const.cmt_likenumurl, "news_id="
				+ newsid + "&category_id=" + catid);
	}

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
			String a = e.getLocalizedMessage();
		}
	}

	@Override
	public void initView() {

	}

}