package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.Options;

@ContentView(R.layout.activity_userinfo)
public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_head;
	private TextView tv_nickname;
	private TextView tv_gender;
	private TextView tv_college;
	private TextView tv_birthday;
	private TextView tv_hometown;

	private String headimgurl;
	private String nickname;
	private String gender;
	private String college;
	private String birthday;
	private String hometown;

	// 下载头像所需
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, UserInfoActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iniView();
		iniTitle();
	}

	private void getInfo() {
		headimgurl = getHeadImageUrl();

		nickname = AVUser.getCurrentUser().getString(XUser.NICKNAME);
		birthday = AVUser.getCurrentUser().getString(XUser.BIRTHDAY);
		hometown = AVUser.getCurrentUser().getString(XUser.HOMETOWN);
		college = AVUser.getCurrentUser().getString(XUser.COLLEGE);

		int int_gender = AVUser.getCurrentUser().getInt(XUser.GENDER);
		if (int_gender == 0)
			gender = "女";
		else
			gender = "男";
	}

	private String getHeadImageUrl() {
		if (AVUser.getCurrentUser() != null
				&& AVUser.getCurrentUser().containsKey("image")
				&& AVUser.getCurrentUser().getAVFile("image") != null) {

			// 显示缩放后的头像
			int headImageHeight = CommonUtil.ScreenUtils.dp2px(
					getApplicationContext(),
					getResources().getDimension(R.dimen.avatar_size_big));

			return AVUser.getCurrentUser().getAVFile("image")
					.getThumbnailUrl(false, headImageHeight, headImageHeight);
		}
		return "";
	}

	private void showInfo() {
		tv_nickname.setText(nickname);
		tv_birthday.setText(birthday);
		tv_college.setText(college);
		tv_gender.setText(gender);
		tv_hometown.setText(hometown);
		imageLoader.displayImage(headimgurl, iv_head, options);
	}

	private void iniTitle() {
		TitleBar titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle("个人信息");

		// 是当前用户，可编辑
		titleBar.setActionText("修改");
		titleBar.setActionTextVisible(true);
		titleBar.setActionTextClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toEditInfo();
			}
		});
	}

	protected void toEditInfo() {
		EditUserInfoActivity.startThis(UserInfoActivity.this);
	}

	// private void fetchCurrentUser() {
	// AVUser user = AVUser.getCurrentUser();
	// user.fetchInBackground(new GetCallback<AVObject>() {
	//
	// @Override
	// public void done(AVObject arg0, AVException arg1) {
	// if (arg1 == null) {
	// AVUser.changeCurrentUser((AVUser) arg0, true);
	//
	// CommonUtil.ToastUtils.showShortToast(
	// getApplicationContext(), "保存成功");
	//
	// dismissWindow();
	// }
	// }
	// });
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_head:
			toPhotoView();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		getInfo();
		showInfo();

		// //更新头像
		// headimgurl = getHeadImageUrl();
		// imageLoader.displayImage(headimgurl, iv_head, options);
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
	}

	private void toPhotoView() {
		// 显示头像原图
		if (AVUser.getCurrentUser() != null
				&& AVUser.getCurrentUser().getAVFile("image") != null) {

			String userHeadImgUrl = AVUser.getCurrentUser().getAVFile("image")
					.getUrl();

			// imageLoader.displayImage(userHeadImgUrl, iv_head, options);
			//
			// float maxWidth, picWidth, picHeight, maxHeight;
			//
			// // 指定图片宽度
			// maxWidth = (float) (CommonUtil.ScreenUtils
			// .getScreenWidth(getApplicationContext()));
			//
			// picWidth = Float.parseFloat(mStatus.getImg().getMetaData("width")
			// .toString());
			// picHeight =
			// Float.parseFloat(mStatus.getImg().getMetaData("height")
			// .toString());
			// maxHeight = (int) (maxWidth * picHeight / picWidth);

			FullScreenPhotoViewActivity.startThis(UserInfoActivity.this,
					userHeadImgUrl);
		}
	}
}
