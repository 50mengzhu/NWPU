package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.avos.avoscloud.AVUser;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.tool.AppUtils;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.HttpUtils;

@ContentView(R.layout.activity_write_comment)
public class WriteCommentActivity extends BaseActivity implements
		OnClickListener {

	private EditText et_cmt;
	private int newsid;
	private int catid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("评论");

		getData();
		checkNetWork();
	}

	private void checkNetWork() {
		if (!CommonUtil.NetworkUtils.checkNetState(getApplicationContext())) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "【"
					+ AppUtils.getAppName(getApplicationContext()) + "】"
					+ "请检查您的网络连接");
		}
	}

	private void getData() {
		newsid = getIntent().getExtras().getInt("newsid");
		catid = getIntent().getExtras().getInt("catid");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit_writecomment:
			commitCmt();
			break;
		}
	}

	private void commitCmt() {
		String comment = et_cmt.getText().toString().trim();

		if (TextUtils.isEmpty(comment)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请填写评论");
			return;
		}

		// 提交内容
		String contentId = "content_" + catid + "-" + newsid + "-1";
		String cmtUrl = Const.add_comment + "&commentid=" + contentId;

		String username = AVUser.getCurrentUser().getString(XUser.NICKNAME);
		String userid = AVUser.getCurrentUser().getString(XUser.STUDENTID);

		HttpUtils.doPostAsyn(mHandle, cmtUrl, "username=" + username
				+ "&userid=" + userid + "&content=" + comment);

	}

	@SuppressLint("HandlerLeak")
	public Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 从这里开始对result的操作
			if (msg.obj != null)
				// 返回
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
						"评论成功");
			WriteCommentActivity.this.onBackPressed();
		}
	};

	@Override
	public void initView() {
		findViewById(R.id.btn_commit_writecomment).setOnClickListener(this);
		et_cmt = (EditText) findViewById(R.id.et_content_writecomment);
	}

}
