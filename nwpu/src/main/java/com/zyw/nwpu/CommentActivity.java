package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.avos.avoscloud.AVUser;
import com.zyw.nwpu.adapter.CommentAdapter;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.AddScoreRecordCallback;
import com.zyw.nwpulib.model.CommentEntity;
import com.zyw.nwpu.service.AvatarAndNicknameService;
import com.zyw.nwpu.service.AvatarAndNicknameService.GetUserInfoCallback;
import com.zyw.nwpu.tool.AppUtils;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpu.tool.HttpUtils;

@ContentView(R.layout.activity_comment)
public class CommentActivity extends BaseActivity implements OnClickListener {
	private ProgressBar progressBar;

	private ListView mlistview;
	private CommentAdapter mAdapter;
	private ArrayList<CommentEntity> mData;

	private String catid;
	private String newsid;

	private TextView tv_nocomment;

	private LinearLayout ll_writeComment;
	private EditText et_comment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle("评论");

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

	/* 获取传递过来的数据 */
	private void getData() {
		newsid = String.valueOf(getIntent().getExtras().getInt("newsid"));
		catid = String.valueOf(getIntent().getExtras().getInt("catid"));
		getComments();
	}

	private void getComments() {
		HttpUtils.doPostAsyn(new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message arg0) {
				if (arg0.obj != null) {
					parseResult(arg0.obj.toString());
				}
				return false;
			}
		}), Const.get_comments, "category_id=" + catid + "&news_id=" + newsid);
	}

	protected void parseResult(String text) {
		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;

		try {
			js = (JSONObject) jsonParser.nextValue();
			JSONArray ja = js.getJSONArray("news");

			ArrayList<CommentEntity> commentList = new ArrayList<CommentEntity>();

			for (int i = 0; i < ja.length(); i++) {
				String username = ja.getJSONObject(i).getString("username");
				String comment = ja.getJSONObject(i).getString(
						"commemt_content");
				String time = ja.getJSONObject(i).getString("creat_at");

				long l = Long.parseLong(time) * 1000;

				// 时间戳转换成对象
				Date date = new Date(l);

				// 将时间转换成易识别的格式
				String eazyTime = DateChangeUtils.toToday(date);

				CommentEntity commentEntity = new CommentEntity();
				commentEntity.setUsername(username);
				commentEntity.setContent(comment);
				commentEntity.setPublishTime(eazyTime);

				commentList.add(commentEntity);

				AvatarAndNicknameService.getUserInfoAndSave(username,
						new GetUserInfoCallback() {

							@Override
							public void done() {
								mAdapter.notifyDataSetChanged();
							}
						});
			}

			// 显示评论
			if (commentList == null || commentList.size() == 0) {
				// 没有评论
				progressBar.setVisibility(View.GONE);
				mlistview.setVisibility(View.GONE);
				tv_nocomment.setVisibility(View.VISIBLE);
			} else {
				mlistview.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tv_nocomment.setVisibility(View.GONE);

				mData.clear();
				for (int i = 0; i < commentList.size(); i++) {
					mData.add(commentList.get(i));
				}

				mAdapter.notifyDataSetChanged();
			}

		} catch (JSONException e) {
		}
	}

	@SuppressLint("HandlerLeak")
	public Handler likeHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// addSupport(msg.arg1);//暂时先不能点赞
		}
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_send_cmt:
			// 发送按钮
			commitCmt();
			break;

		default:
			break;
		}
	}

	private void toLogin() {
		startActivity(new Intent(getApplicationContext(),
				Login.class));
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void commitCmt() {

		if (!AccountHelper.isLogedIn(getApplicationContext())) {
			// 先判断是否登陆
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请先登陆");
			toLogin();
			return;
		}

		String comment = et_comment.getText().toString().trim();

		if (TextUtils.isEmpty(comment)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请填写评论");
			return;
		}

		// 编辑框清空
		et_comment.setText("");

		// 提交内容
		String contentId = "content_" + catid + "-" + newsid + "-1";
		String cmtUrl = Const.add_comment + "&commentid=" + contentId;

		String username = AVUser.getCurrentUser().getString(XUser.STUDENTID);
		String userid = "";

		HttpUtils.doPostAsyn(writeHandle, cmtUrl, "username=" + username
				+ "&userid=" + userid + "&content=" + comment);
	}

	@SuppressLint("HandlerLeak")
	public Handler writeHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"评论成功");
			
			// 增加积分
			ScoreHelper.addScore("评论文章", 1, new AddScoreRecordCallback() {

				@Override
				public void onSuccess() {
					ToastUtils.showShortToast("评论文章 增加1积分");
				}

				@Override
				public void onFailure(String errTip) {
					// TODO Auto-generated method stub
				}
			});

			// 列表更新
			getComments();
		}
	};

	@Override
	public void initView() {
		progressBar = (ProgressBar) findViewById(R.id.pb_cmt);
		tv_nocomment = (TextView) findViewById(R.id.tv_nocomment);

		mlistview = (ListView) findViewById(R.id.lv_cmt);// 列表
		mData = new ArrayList<CommentEntity>();
		mAdapter = new CommentAdapter(getApplicationContext(), mData,
				likeHandle);// 适配器
		mlistview.setAdapter(mAdapter);

		progressBar.setVisibility(View.VISIBLE);
		mlistview.setVisibility(View.GONE);
		tv_nocomment.setVisibility(View.GONE);

		mlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// addSupport(arg2);//暂时先不能点赞
			}
		});

		// 底部写评论相关
		ll_writeComment = (LinearLayout) findViewById(R.id.ll_foot_cmt);
		et_comment = (EditText) findViewById(R.id.et_comment);
		this.findViewById(R.id.btn_send_cmt).setOnClickListener(this);
	}

}
