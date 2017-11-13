package com.zyw.nwpu;

import java.io.File;
import java.io.IOException;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.FileManager;
import com.zyw.nwpu.tool.HttpUtils;
import com.zyw.nwpu.tool.Options;

@ContentView(R.layout.activity_editinfo)
public class EditUserInfoActivity extends BaseActivity implements
		OnClickListener {

	private ImageView iv_head;
	private EditText et_nickname;

	String userid;

	// 获取头像所需的参数
	private final int TAKE_PICTURE = 0;
	private final int RESULT_LOAD_IMAGE = 1;
	private final int CUT_PHOTO_REQUEST_CODE = 2;
	private Uri photoUri;
	private String headImgPath;

	// 下载头像所需
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();

	// 上传头像
	private AVFile headImgFile = null;

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, EditUserInfoActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iniTitle();
		showLocalData();
	}

	private void iniTitle() {
		TitleBar titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle("修改个人信息");
		titleBar.setActionText("确定");
		titleBar.setActionTextVisible(true);
		titleBar.setActionTextClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveUserNickName();
			}
		});
	}

	private void showLocalData() {
		// 将本地数据显示在控件上
		String nickname = AVUser.getCurrentUser().getString(XUser.NICKNAME);
		et_nickname.setText(nickname);

		// 下载头像
		loadHeadImage();
	}

	private void fetchCurrentUserAfterImage() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);

					CommonUtil.ToastUtils.showShortToast(
							getApplicationContext(), "保存成功");

					dismissProgressDialog();
				}
			}
		});
	}

	private void fetchCurrentUserAfterNickName() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);

					// 更新昵称
					EMChatManager.getInstance().updateCurrentUserNick(
							arg0.getString(XUser.NICKNAME));

					CommonUtil.ToastUtils.showShortToast(
							getApplicationContext(), "保存成功");
					dismissProgressDialog();
					EditUserInfoActivity.this.onBackPressed();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_head:
			toPhotoView();
			break;

		case R.id.rl_head:
			showPicSelector();
			break;

		}
	}

	@Override
	public void initView() {
		findViewById(R.id.rl_head).setOnClickListener(this);

		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_head.setOnClickListener(this);

		et_nickname = (EditText) findViewById(R.id.et_nickname_edit);
	}

	private void loadHeadImage() {
		// 显示旧头像
		if (AVUser.getCurrentUser() != null
				&& AVUser.getCurrentUser().containsKey("image")
				&& AVUser.getCurrentUser().getAVFile("image") != null) {

			// 显示缩放后的头像
			int headImageHeight = CommonUtil.ScreenUtils.dp2px(
					getApplicationContext(),
					getResources().getDimension(R.dimen.avatar_size_big));

			String userHeadImgUrl = AVUser.getCurrentUser().getAVFile("image")
					.getThumbnailUrl(false, headImageHeight, headImageHeight);
			imageLoader.displayImage(userHeadImgUrl, iv_head, options);
		}
	}

	private void showPicSelector() {
		// 显示选项
		final CharSequence[] items = { "图库", "拍照" };
		new AlertDialog.Builder(EditUserInfoActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							openGallery();
						} else {
							takePhoto();
						}
					}
				}).create().show();
	}

	private void openGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	private void takePhoto() {
		try {
			// 检查临时文件路径是否存在
			File fileDir = new File(FileManager.getHeadImageFilePath());
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(FileManager.getHeadImageFilePath()
					+ System.currentTimeMillis() + ".JPEG");
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			if (file != null) {
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveUserNickName() {

		final String nickname = et_nickname.getText().toString().trim();

		if (TextUtils.isEmpty(nickname)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"请输入昵称");
			return;
		}

		// 提交到云服务器
		String updateurl = Const.UPDATE_NICKNAME;
		HttpUtils.doPostAsyn(null, updateurl, "userid=" + userid + "&nickname="
				+ nickname);

		// 提交到LeanCloud
		final AVUser avUser = AVUser.getCurrentUser();
		if (avUser == null) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"保存信息出错");
			return;
		}

		avUser.put(XUser.NICKNAME, nickname);
		avUser.setFetchWhenSave(true);

		showProgressDialog();

		avUser.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (e != null) {
					CommonUtil.ToastUtils.showShortToast(
							getApplicationContext(), "保存失败:" + e.getMessage());
					dismissProgressDialog();
					return;
				}

				fetchCurrentUserAfterNickName();
			}
		});
	}

	private void saveUserHeadImage() {
		final AVUser avUser = AVUser.getCurrentUser();
		if (avUser == null) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"保存头像出错");
			return;
		}
		if (headImgFile != null) {
			avUser.put("image", headImgFile);
		}
		avUser.setFetchWhenSave(true);

		avUser.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (e != null) {
					CommonUtil.ToastUtils.showShortToast(
							getApplicationContext(), "保存失败:" + e.getMessage());
					dismissProgressDialog();
					return;
				}

				fetchCurrentUserAfterImage();
			}

		});
	}

	private void upLoadImage(String path) {

		showProgressDialog();

		if (path == null) {
			return;
		}
		headImgFile = new AVFile();
		try {
			headImgFile = AVFile.withAbsoluteLocalPath("headimg.jpg", path);
		} catch (IOException e) {
			return;
		}

		if (headImgFile == null)
			return;

		headImgFile.saveInBackground(new SaveCallback() {
			public void done(AVException arg0) {
				if (arg0 != null) {
					CommonUtil.ToastUtils.showShortToast(
							getApplicationContext(),
							"上传头像失败:" + arg0.getMessage());

					dismissProgressDialog();
					return;
				}

				saveUserHeadImage();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (RESULT_OK != resultCode) {
			return;
		}

		switch (requestCode) {
		case TAKE_PICTURE:
			// 拍照
			startPhotoZoom(photoUri);
			break;

		case RESULT_LOAD_IMAGE:
			// 相册返回
			if (null != data) {
				Uri uri = data.getData();
				if (uri != null) {
					startPhotoZoom(uri);
				}
			}
			break;

		case CUT_PHOTO_REQUEST_CODE:
			// 裁剪返回
			if (null != data) {
				if (photoUri != null) {
					iv_head.setImageURI(photoUri);

					// 上传图片
					upLoadImage(headImgPath);
				}
			}
			break;
		}
	}

	private void startPhotoZoom(Uri uri) {
		// 检查临时文件路径是否存在
		File fileDir = new File(FileManager.getHeadImageFilePath());
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		headImgPath = FileManager.getHeadImageFilePath()
				+ System.currentTimeMillis() + ".JPEG";
		File file = new File(headImgPath);

		if (file != null) {
			photoUri = Uri.fromFile(file);
			final Intent intent = new Intent("com.android.camera.action.CROP");

			// 照片URL地址
			intent.setDataAndType(uri, "image/*");

			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 480);
			intent.putExtra("outputY", 480);
			// 输出路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

			// 输出格式
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			// 不启用人脸识别
			intent.putExtra("noFaceDetection", false);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
		}
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

			FullScreenPhotoViewActivity.startThis(EditUserInfoActivity.this,
					userHeadImgUrl);
		}
	}
}
