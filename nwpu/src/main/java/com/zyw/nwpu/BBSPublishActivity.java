package com.zyw.nwpu;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xutils.view.annotation.ContentView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.SaveCallback;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.avos.AVCloudMethod;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.AddScoreRecordCallback;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.tool.Bimp;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpu.tool.FileManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@ContentView(R.layout.activity_publish)
public class BBSPublishActivity extends BaseActivity implements OnClickListener {

	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_ADD_TAG = 2;

	private EditText et_imgdesc;

	// 获取图片所需的变量
	private ImageView iv_pic;
	private String tempDir_Img;
	private Uri photoUri = null;
	private String photoPath = null;
	private AVFile imgFile;

	private int picWidth;
	private int picHeight;

	private boolean isanonymous = false;
	private String content = "";

	private CheckBox cb_anonymous;

	private String tag = "";
	private TextView tv_tag;
	private RelativeLayout rl_tag;

	// 位置相关
	private boolean isShowPosition = true;// 默认显示位置
	private LinearLayout ll_position;
	private TextView tv_position;
	private ImageView iv_close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tempDir_Img = FileManager.getImageFilePath();
		iniView();
		iniEvent();
		iniTitle();
		showData();
	}

	private void showData() {
		tag = getIntent().getStringExtra("tag");
		content = getIntent().getStringExtra("question");

		// 显示数据
		if (!TextUtils.isEmpty(content)) {
			et_imgdesc.setText(content);
		}
		if (!TextUtils.isEmpty(tag)) {
			tv_tag.setText(tag);
			rl_tag.setVisibility(View.VISIBLE);

			BBSService.addTopic(tag);
		} else {
			rl_tag.setVisibility(View.GONE);
		}
	}

	private void iniView() {
		// 话题相关
		rl_tag = (RelativeLayout) findViewById(R.id.rl_tag);
		tv_tag = (TextView) findViewById(R.id.tv_tag);
		rl_tag.setVisibility(View.GONE);

		// 位置相关
		ll_position = (LinearLayout) findViewById(R.id.ll_position);
		tv_position = (TextView) findViewById(R.id.tv_position);
		iv_close = (ImageView) findViewById(R.id.iv_close);

		if (TextUtils.isEmpty(AppApplication.addr)) {
			isShowPosition = false;
			ll_position.setVisibility(View.GONE);
		} else {
			isShowPosition = true;
			iv_close.setVisibility(View.VISIBLE);
			tv_position.setText(AppApplication.addr);
			ll_position.setVisibility(View.VISIBLE);
		}

		ll_position.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isShowPosition) {
					// 隐藏位置
					iv_close.setVisibility(View.GONE);
					tv_position.setText("不显示位置");
				} else {
					// 显示位置
					iv_close.setVisibility(View.VISIBLE);
					tv_position.setText(AppApplication.addr);
				}
				isShowPosition = !isShowPosition;
			}
		});
	}

	private void iniTitle() {
		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("发布");
		bar.setActionTextVisible(true);
		bar.setActionText("确定");
		bar.setActionTextClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				publish();
			}
		});
	}

	public static void startThis(Context cxt, String tag) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, BBSPublishActivity.class);
			intent.putExtra("tag", tag);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	public static void startThisWithQuestionAndTag(Context cxt, String tag, String question) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, BBSPublishActivity.class);
			intent.putExtra("question", question);
			intent.putExtra("tag", tag);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, BBSPublishActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	private void iniEvent() {
		et_imgdesc = (EditText) findViewById(R.id.et_imgdescription_crt_img);
		cb_anonymous = (CheckBox) findViewById(R.id.checkbox_anonymous);
		this.findViewById(R.id.iv_camera_img).setOnClickListener(this);
		this.findViewById(R.id.iv_gallery_img).setOnClickListener(this);
		this.findViewById(R.id.iv_tag_img).setOnClickListener(this);
		this.findViewById(R.id.tv_tag).setOnClickListener(this);
		this.findViewById(R.id.iv_clear_tag).setOnClickListener(this);

		// 添加图片所需的
		iv_pic = (ImageView) this.findViewById(R.id.iv_pic_crt_img);
		iv_pic.setVisibility(View.GONE);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		case R.id.iv_clear_tag:
			// 清空tag
			tag = "";
			rl_tag.setVisibility(View.GONE);
			break;

		case R.id.iv_camera_img:
			takePhoto();
			break;

		case R.id.iv_gallery_img:
			openGallery();
			break;

		case R.id.iv_tag_img:
		case R.id.tv_tag:
			toAddTag();
			break;
		}
	}

	private void toAddTag() {
		Intent intent = new Intent(BBSPublishActivity.this, PickTagActivity.class);
		startActivityForResult(intent, RESULT_ADD_TAG);
		overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	private void publish() {
		content = et_imgdesc.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "内容不得为空");
			return;
		}

		// 显示等待窗口
		showProgressDialog();

		// 是否匿名发布
		isanonymous = cb_anonymous.isChecked();

		upLoadImage(photoPath);
	}

	private void openGallery() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	private void takePhoto() {
		// 检验SD卡是否存在
		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(BBSPublishActivity.this, "SD卡不存在，无法使用语音功能", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			// 检查临时文件路径是否存在
			File fileDir = new File(tempDir_Img);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(tempDir_Img + System.currentTimeMillis() + ".JPEG");
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (file != null) {
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK != resultCode) {
			return;
		}
		switch (requestCode) {
		case TAKE_PICTURE:
			// 若为相机
			photoPath = photoUri.getEncodedPath();
			showPic(photoUri);
			break;
		case RESULT_LOAD_IMAGE:
			// 若图片来自图库，则需要解析
			photoUri = data.getData();
			photoPath = CommonUtil.FileUtils.getFilePathFromUri(BBSPublishActivity.this, photoUri);
			showPic(photoUri);
			break;

		case RESULT_ADD_TAG:
			// 添加话题标签
			tag = data.getStringExtra("tag");
			tv_tag.setText("#" + tag + "#");
			rl_tag.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void showPic(Uri uri) {
		@SuppressWarnings("deprecation")
		int newWidth = this.getWindowManager().getDefaultDisplay().getWidth()
				- CommonUtil.ScreenUtils.dp2px(BBSPublishActivity.this, 20);
		Bitmap bitmap = Bimp.getLocalBitmapFromUri(BBSPublishActivity.this, uri, newWidth);

		// 此处的图片高度、宽度均为缩放厚度尺寸
		picHeight = bitmap.getHeight();
		picWidth = bitmap.getWidth();

		iv_pic.setImageBitmap(bitmap);
		iv_pic.setVisibility(View.VISIBLE);
	}

	private void publishTopic(AVFile imgfile) {
		// 获取地理坐标和地名
		AVGeoPoint avgeopoint = new AVGeoPoint(0, 0);
		String address = "";
		if (isShowPosition) {
			address = AppApplication.addr;
			avgeopoint.setLatitude(AppApplication.lat);
			avgeopoint.setLongitude(AppApplication.lng);
		} else {
			address = "火星";
		}

		// 发布话题
		AVCloudMethod.publishStatus(imgfile, content, address, isanonymous, tag, avgeopoint,
				new FunctionCallback<String>() {

					@Override
					public void done(String arg0, AVException arg1) {
						if (arg1 != null) {
							// 出错
							CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getMessage());
							dismissProgressDialog();
							return;
						}
						if (arg0.equals("success")) {
							CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "发布成功");

							BBSPublishActivity.this.onBackPressed();

							// 增加积分
							ScoreHelper.addScore("论坛发帖", 2, new AddScoreRecordCallback() {

								@Override
								public void onSuccess() {
									ToastUtils.showShortToast("论坛发帖 增加2积分");
								}

								@Override
								public void onFailure(String errTip) {
									// TODO Auto-generated method stub
								}
							});

							return;
						} else {
							CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "发布失败" + arg0);
							dismissProgressDialog();
						}
						return;
					}
				});
	}

	/**
	 * 上传图片
	 * 
	 * @param path
	 */
	private void upLoadImage(String path) {

		if (path == null) {
			publishTopic(null);// 图片为空
			return;
		}
		imgFile = new AVFile();
		try {
			imgFile = AVFile.withAbsoluteLocalPath("android.jpg", path);
		} catch (IOException e) {
			return;
		}
		if (imgFile == null) {
			publishTopic(null);// 图片为空
			return;
		}

		imgFile.addMetaData("width", picWidth);
		imgFile.addMetaData("height", picHeight);
		imgFile.addMetaData("rate", ((double) picHeight / (double) picWidth));// 这里可能需要改

		imgFile.saveInBackground(new SaveCallback() {
			public void done(AVException arg0) {
				if (arg0 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "上传图片失败：" + arg0.getMessage());
					dismissProgressDialog();
					return;
				}
				publishTopic(imgFile);
			}
		});
	}
}
