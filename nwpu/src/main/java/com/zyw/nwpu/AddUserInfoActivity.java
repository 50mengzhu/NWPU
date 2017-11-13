package com.zyw.nwpu;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.easemob.chat.EMChatManager;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.tool.FileManager;
import com.zyw.nwpu.view.MDatePicker;
import com.zyw.nwpu.view.MDatePicker.OnChooseEvent;
import com.zyw.nwpu.view.popupwindow.SelectGenderWindow;
import com.zyw.nwpulib.utils.CommonUtil;

/**
 * 
 * 2015年11月4日
 * 
 * @author Rocket
 * 
 */
@ContentView(R.layout.activity_adduserinfo)
public class AddUserInfoActivity extends BaseActivity implements OnClickListener {

	private Button btn_gender;
	private Button btn_age;
	private Button btn_hometown;
	private Button btn_college;
	private EditText et_nickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("完善个人信息");

		CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请完善个人信息");
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, AddUserInfoActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_gender:
			showGenderWindow();
			break;

		case R.id.btn_age:
			toPickBirthday();
			break;

		case R.id.btn_hometown:
			toPickHomeTown();
			break;

		case R.id.btn_college:
			toPickCollege();
			break;

		case R.id.btn_commit:
			// 上传图片
			checkInput();
			break;

		case R.id.rl_head_add:
			showPicSelector();
			break;
		}
	}

	private void toPickHomeTown() {
		PickHomeTownActivity.startThisForResult(AddUserInfoActivity.this, RESULTCODE);
	}

	private String[] collegeName = { "航空学院", "航天学院", "航海学院", "材料学院", "机电学院", "力学与土木建筑学院", "动力与能源学院", "电子信息学院", "自动化学院",
			"计算机学院", "理学院", "管理学院", "人文与经法学院", "软件与微电子学院", "生命学院", "外国语学院", "教育实验学院", "其他" };

	private int selectedCollegeId = -1;

	private void toPickCollege() {
		AlertDialog.Builder b = new Builder(AddUserInfoActivity.this);
		b.setItems(collegeName, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				selectedCollegeId = arg1;
				btn_college.setText(collegeName[arg1]);
			}
		});
		b.show();
	}

	/**
	 * 选择生日
	 */
	private void toPickBirthday() {
		MDatePicker datePicker = null;
		datePicker = new MDatePicker(AddUserInfoActivity.this, "");
		datePicker.pick(new OnChooseEvent() {

			@Override
			public void onChoose(String birthday) {
				if (!TextUtils.isEmpty(birthday)) {
					btn_age.setText(birthday);
				}
			}
		});
	}

	private void showGenderWindow() {
		final SelectGenderWindow genderWindow = new SelectGenderWindow();

		OnClickListener l = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.tv_man:
					btn_gender.setText("男");
					break;
				case R.id.tv_woman:
					btn_gender.setText("女");
					break;
				}
				genderWindow.dismiss();
			}
		};

		genderWindow.showWindow(getApplicationContext(), findViewById(R.id.main_view), l);
	}

	private void checkInput() {
		final String gender = btn_gender.getText().toString().trim();
		final String birthday = btn_age.getText().toString().trim();

		if (TextUtils.isEmpty(headImgPath)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请设置头像");
			return;
		}

		if (TextUtils.isEmpty(gender) || (gender.compareTo("男") != 0 && gender.compareTo("女") != 0)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请输入性别");
			return;
		}

		if (TextUtils.isEmpty(birthday) || birthday.compareTo("年龄") == 0) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请输入年龄");
			return;
		}

		if (TextUtils.isEmpty(AppApplication.homeTown)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请输入家乡");
			return;
		}

		if (selectedCollegeId == -1) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请设置学院");
			return;
		}

		submitInfo();
		return;
	}

	private void submitInfo() {
		final String nickname = et_nickname.getText().toString().trim();
		final String gender = btn_gender.getText().toString().trim();
		final String birthday = btn_age.getText().toString().trim();
		final String college = collegeName[selectedCollegeId];

		showProgressDialog();

		int intGender;
		// 保存性别
		if (gender.compareTo("男") == 0) {
			intGender = 1;
		} else {
			intGender = 0;
		}

		// 用户的其他信息
		final String studentId = AccountHelper.getStudentId(getApplicationContext());

		final String password = studentId;
		String school = "西北工业大学";
		String email = "";

		// 8个参数
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("studentId", studentId);
		parameters.put("password", password);
		parameters.put("nickName", nickname);
		parameters.put("school", school);
		parameters.put("email", email);
		parameters.put("age", 0);
		// parameters.put("image", headImgFile);// 头像
		parameters.put("gender", intGender);
		parameters.put("college", college);
		parameters.put("hometown", AppApplication.homeTown);
		parameters.put("birthday", birthday);
		parameters.put("deviceId", AVInstallation.getCurrentInstallation().getInstallationId());

		AVCloud.callFunctionInBackground("login", parameters, new FunctionCallback<String>() {

			@Override
			public void done(String arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					dismissProgressDialog();
					return;
				}

				// 注册成功
				if (arg0.compareTo("success") == 0) {

					AVUser.logInInBackground(studentId, password, new LogInCallback<AVUser>() {

						@Override
						public void done(AVUser arg0, AVException arg1) {
							if (arg1 != null) {
								CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
										arg1.getLocalizedMessage());
								return;
							}

							// 更新昵称
							EMChatManager.getInstance().updateCurrentUserNick(arg0.getString(XUser.NICKNAME));

							AccountHelper.setLogedIn(getApplicationContext(), true);

							// 这里登陆成功了，上传头像
							upLoadImage(headImgPath);
						}
					});

					return;
				}

				dismissProgressDialog();
				// 错误 显示错误信息
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg0);

			}
		});
	}

	private ImageView iv_head;

	@Override
	public void initView() {
		findViewById(R.id.btn_commit).setOnClickListener(this);
		findViewById(R.id.rl_head_add).setOnClickListener(this);

		iv_head = (ImageView) findViewById(R.id.iv_head_add);

		btn_age = (Button) findViewById(R.id.btn_age);
		btn_gender = (Button) findViewById(R.id.btn_gender);
		btn_hometown = (Button) findViewById(R.id.btn_hometown);
		btn_college = (Button) findViewById(R.id.btn_college);

		et_nickname = (EditText) findViewById(R.id.et_nickname);

		btn_age.setOnClickListener(this);
		btn_hometown.setOnClickListener(this);
		btn_gender.setOnClickListener(this);
		btn_college.setOnClickListener(this);
	}

	// 获取头像所需的参数
	private final int TAKE_PICTURE = 0;
	private final int RESULT_LOAD_IMAGE = 1;
	private final int CUT_PHOTO_REQUEST_CODE = 2;
	private final int RESULTCODE = 3;// 获取家乡

	private Uri photoUri;
	private String headImgPath;

	private void showPicSelector() {
		// 显示选项
		final CharSequence[] items = { "图库", "拍照" };
		new AlertDialog.Builder(AddUserInfoActivity.this).setItems(items, new DialogInterface.OnClickListener() {

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

	// 打开图库
	private void openGallery() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	// 相机
	private void takePhoto() {
		try {
			// 检查临时文件路径是否存在
			File fileDir = new File(FileManager.getHeadImageFilePath());
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(FileManager.getHeadImageFilePath() + System.currentTimeMillis() + ".JPEG");
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

	// 上传头像
	private AVFile headImgFile = null;

	private void upLoadImage(String path) {

		if (TextUtils.isEmpty(path)) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请设置头像");
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

		// 等待窗口
		showProgressDialog();

		headImgFile.saveInBackground(new SaveCallback() {
			public void done(AVException arg0) {
				if (arg0 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "上传头像失败:" + arg0.getMessage());

					dismissProgressDialog();
					return;
				}

				setUserHeadImage();
				// 上传其他信息
				// submitInfo();
			}
		});
	}

	private void setUserHeadImage() {
		final AVUser avUser = AVUser.getCurrentUser();
		if (avUser == null) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "保存头像出错");
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
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "保存失败:" + e.getMessage());
					dismissProgressDialog();
					return;
				}

				fetchCurrentUser();
			}
		});
	}

	private void fetchCurrentUser() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);

					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "完善个人信息成功");

					dismissProgressDialog();

					AddUserInfoActivity.this.finish();
				}
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
				}
			}
			break;

		case RESULTCODE:
			if (btn_hometown != null) {
				if (!TextUtils.isEmpty(AppApplication.homeTown))
					btn_hometown.setText(AppApplication.homeTown);
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
		headImgPath = FileManager.getHeadImageFilePath() + System.currentTimeMillis() + ".JPEG";
		File file = new File(headImgPath);

		if (file != null) {
			photoUri = Uri.fromFile(file);
			final Intent intent = new Intent("com.android.camera.action.CROP");

			// 照片URL地址
			intent.setDataAndType(uri, "image/*");

			// 相关参数
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 480);
			intent.putExtra("outputY", 480);

			// 输出路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			// 输出格式
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			// 不启用人脸识别
			intent.putExtra("noFaceDetection", false);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
		}
	}
}
