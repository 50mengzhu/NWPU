package com.zyw.nwpu;

import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.easemob.chat.EMConversation.EMConversationType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.app.HXConst;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.tool.Options;

@ContentView(R.layout.activity_userinfo_other)
public class OtherUserInfoActivity2_bakbakbak extends BaseActivity implements OnClickListener {

	private ImageView iv_head;
	private TextView tv_nickname;
	private TextView tv_gender;
	private TextView tv_college;
	private TextView tv_birthday;
	private TextView tv_hometown;

	private RelativeLayout rl_status;
	private TextView tv_statusnum;
	private TextView tv_status;
	private ImageView iv_statuspic;

	private String headimgurl = "";
	private String nickname = "";
	private String gender = "";
	private String college = "";
	private String birthday = "";
	private String hometown = "";
	private String studentid = "";

	private Button btn_tochat;

	private String userid = "";

	// private TitleBar titleBar;

	// 下载头像所需
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();

	public static void startThis(Context cxt, String userid) {
		if (TextUtils.isEmpty(userid))
			return;

		// 若id与当前用户id相同，则跳转到本人的个人中心
		if (AVUser.getCurrentUser() != null) {
			if (userid.compareTo(AVUser.getCurrentUser().getObjectId()) == 0) {
				UserInfoActivity.startThis(cxt);
				return;
			}
		}

		Intent intent = new Intent(cxt, OtherUserInfoActivity2_bakbakbak.class);
		intent.putExtra("userid", userid);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userid = getIntent().getStringExtra("userid");

		iniView();
		// iniTitle();

		getUserInfo(userid);
	}

	private void toChat() {
		ChatActivity.startThis(OtherUserInfoActivity2_bakbakbak.this, studentid, nickname);
	}

	private void getUserInfo(String userId) {
		AVQuery<AVUser> query = new AVQuery<AVUser>(XUser.CLASSNAME);
		query.whereEqualTo(XUser.OBJID, userId);
		query.include(XUser.HEADIMG);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() != 1) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "获取个人信息失败");
					return;
				}

				// 获取到用户
				AVUser user = arg0.get(0);
				getPersonInfo(user);
				getUserStatusByRelation(user);
			}
		});
	}

	private void getUserStatusByRelation(AVUser user) {
		// 获取发表记录
		AVRelation<Status> statuses = user.getRelation(XUser.ANNOUNCE);

		if (null == statuses.getTargetClass()) {
			return;
		}

		AVQuery<Status> query = statuses.getQuery();
		query.orderByDescending(XUser.CREATEDAT);
		query.whereEqualTo("isAnonymous", false);
		query.whereEqualTo("isShow", true);
		query.findInBackground(new FindCallback<Status>() {

			@Override
			public void done(List<Status> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					return;
				}
				if (arg0 == null || arg0.size() == 0) {
					tv_status.setVisibility(View.VISIBLE);
					tv_status.setText("没有记录");
					tv_statusnum.setVisibility(View.VISIBLE);
					tv_statusnum.setText("0");
					return;
				}

				// 获取到了发布记录，显示在界面上
				int statusnum = arg0.size();
				tv_statusnum.setVisibility(View.VISIBLE);
				tv_statusnum.setText(String.valueOf(statusnum));

				AVObject item = arg0.get(0);
				StatusData data = BBSService.transformStatusData(getApplicationContext(), (Status) item);

				if (TextUtils.isEmpty(data.imgUrl)) {
					// 显示文字
					tv_status.setTextColor(getResources().getColor(R.color.reply));
					tv_status.setVisibility(View.VISIBLE);
					tv_status.setText(data.content_txt);
				} else {
					// 显示图片
					iv_statuspic.setVisibility(View.VISIBLE);
					imageLoader.displayImage(data.imgUrl, iv_statuspic, options);
				}
			}
		});
	}

	private void getPersonInfo(AVUser user) {
		if (user == null) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "获取个人信息失败");
			return;
		}

		// 头像
		if (user.containsKey("image") && user.getAVFile("image") != null) {

			headimgurl = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE, 50,
					"jpg");
		}

		// 其他信息
		nickname = user.getString(XUser.NICKNAME);
		birthday = user.getString(XUser.BIRTHDAY);
		hometown = user.getString(XUser.HOMETOWN);
		college = user.getString(XUser.COLLEGE);
		studentid = user.getString(XUser.STUDENTID);

		int int_gender = user.getInt(XUser.GENDER);
		if (int_gender == 0)
			gender = "女";
		else
			gender = "男";

		// 显示在界面上
		showInfo();
	}

	public void iniView() {
		// 个人信息
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_head.setOnClickListener(this);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname_edit);
		tv_gender = (TextView) findViewById(R.id.tv_gender_edit);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday_edit);
		tv_hometown = (TextView) findViewById(R.id.tv_hometown_edit);
		tv_college = (TextView) findViewById(R.id.tv_college_edit);

		rl_status = (RelativeLayout) findViewById(R.id.rl_status);
		tv_statusnum = (TextView) findViewById(R.id.tv_statusnum);
		tv_status = (TextView) findViewById(R.id.tv_statustext_userinfo);
		iv_statuspic = (ImageView) findViewById(R.id.iv_statuspic_userinfo);

		rl_status.setOnClickListener(this);
		tv_statusnum.setVisibility(View.GONE);
		tv_status.setVisibility(View.GONE);
		iv_statuspic.setVisibility(View.GONE);

		btn_tochat = (Button) findViewById(R.id.btn_tochat);
		btn_tochat.setVisibility(View.INVISIBLE);
	}

	// private void iniTitle() {
	// titleBar = (TitleBar) findViewById(R.id.head);
	// titleBar.setTitle("个人信息");
	// }

	private void showInfo() {
		tv_nickname.setText(nickname);
		tv_birthday.setText(birthday);
		tv_college.setText(college);
		tv_gender.setText(gender);
		tv_hometown.setText(hometown);
		imageLoader.displayImage(headimgurl, iv_head, options);

		// 显示聊天按钮
		btn_tochat.setVisibility(View.VISIBLE);
		btn_tochat.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_tochat:
			toChat();
			break;

		case R.id.iv_head:
			toPhotoView();
			break;
		case R.id.rl_status:
			toUserStatus();
			break;

		}
	}

	private void toUserStatus() {
		UserProfileActivity.startThis(OtherUserInfoActivity2_bakbakbak.this, userid);
	}

	private void toPhotoView() {
		if (TextUtils.isEmpty(headimgurl))
			return;

		FullScreenPhotoViewActivity.startThis(OtherUserInfoActivity2_bakbakbak.this, headimgurl);
	}
}
