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
import com.zyw.nwpulib.utils.CommonUtil.CheckUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

/**
 * 
 * 
 * @author Rocket
 * 
 */
@ContentView(R.layout.activity_setphonenum)
public class SetPhoneNumActivity extends BaseActivity {

	private EditText et_phone;
	private EditText et_yaoqingren;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setActionTextClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String phone = et_phone.getText().toString();
				if (!CheckUtils.isMobileNO(phone)) {
					ToastUtils.showShortToast("请输入正确的手机号，以便赠送您礼品时联系您！");
					return;
				}
				String yaoqingren = et_yaoqingren.getText().toString();

				if (!TextUtils.isEmpty(yaoqingren) && AVUser.getCurrentUser() != null) {
					// 保存邀请人信息
					AVObject obj = new AVObject("YaoQingInfo");
					obj.put("user", AVUser.getCurrentUser());
					obj.put("yaoqingren", yaoqingren);
					obj.saveInBackground();
				}

				if (AVUser.getCurrentUser() != null) {
					// 保存手机号
					AVUser.getCurrentUser().setMobilePhoneNumber(phone);
					AVUser.getCurrentUser().setFetchWhenSave(true);
					AVUser.getCurrentUser().saveInBackground();
				}

				fetchCurrentUser();
				SetPhoneNumActivity.this.finish();
				startActivity(new Intent(SetPhoneNumActivity.this, MainTabActivity.class));
			}
		});
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, SetPhoneNumActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	public void initView() {
		et_yaoqingren = (EditText) findViewById(R.id.et_yaoqingren);
		et_phone = (EditText) findViewById(R.id.et_phone);
	}

	private void fetchCurrentUser() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);
				}
			}
		});
	}

}
