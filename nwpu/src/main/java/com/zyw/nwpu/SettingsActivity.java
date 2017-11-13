package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.service.ShareHelper;
import com.zyw.nwpu.tool.AppUtils;
import com.zyw.nwpu.update.UpdateInfoEntity;
import com.zyw.nwpu.update.UpdateService;
import com.zyw.nwpu.view.popupwindow.MPopupWindow;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.settings)
public class SettingsActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_update;
	private TextView tv_textsize;
	private TextView tv_shaketext;
	private TextView tv_current_version;

	private Integer textSize;

	private CheckBox cb_receiveNotify;
	private CheckBox cb_useSwipe;
	private CheckBox cb_blockImage;

	private CheckBox cb_msg_notification;
	private CheckBox cb_msg_sound;
	private CheckBox cb_msg_vibration;

	private Boolean newversion = false;

	private LinearLayout setting_msg_sound;
	private LinearLayout setting_msg_vibration;
	private LinearLayout ll_logout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("设置");

		iniView();
		iniData();
	}

	private int clickNum = 1;

	@Event(R.id.copyright)
	private void click(View v) {
		clickNum++;
		if (clickNum % 8 == 0) {
			SurpriseActivity.startThis(SettingsActivity.this);
			clickNum = 1;
		}
	}

	private void getData() {
		// 是否有新版本
		newversion = getIntent().getBooleanExtra("hasnewversion", false);
	}

	private void iniData() {
		// 获取设置信息
		Boolean receiveNotify = AppSetting.isReceiveNotify(getApplicationContext());
		Boolean useSwipe = AppSetting.isUseGestureBack(getApplicationContext());
		Boolean blockImage = AppSetting.isBlockImage(getApplicationContext());

		textSize = AppSetting.getTextSize(getApplicationContext());

		Boolean isMsgNotify = AppSetting.isMsgNotifyAllowed(getApplicationContext());
		Boolean isMsgSound = AppSetting.isMsgSoundAllowed(getApplicationContext());
		Boolean isMsgVibration = AppSetting.isMsgVibrateAllowed(getApplicationContext());

		// 在控件上显示设置信息
		cb_msg_notification.setChecked(isMsgNotify);
		cb_msg_sound.setChecked(isMsgSound);
		cb_msg_vibration.setChecked(isMsgVibration);

		toggleShowMsgSettingMenu(isMsgNotify);

		// 字体大小
		switch (textSize) {
		case AppSetting.TS_SMALL:
			tv_textsize.setText("小号");
			break;

		case AppSetting.TS_MEDIUM:
			tv_textsize.setText("中号");
			break;

		case AppSetting.TS_BIG:
			tv_textsize.setText("大号");
			break;

		default:
			tv_textsize.setText("中号");
			break;
		}

		// 版本号
		tv_current_version.setText(AppUtils.getVersionName(getApplicationContext()));

		// 是否有新版本
		if (newversion) {
			iv_update.setVisibility(View.VISIBLE);
		} else {
			iv_update.setVisibility(View.GONE);
		}
	}

	private void iniView() {

		// 单选框
		cb_receiveNotify = (CheckBox) findViewById(R.id.checkbox_notify);
		cb_useSwipe = (CheckBox) findViewById(R.id.checkbox_use_swipe);
		cb_blockImage = (CheckBox) findViewById(R.id.checkbox_block_img);

		cb_msg_notification = (CheckBox) findViewById(R.id.checkbox_msg_notification);
		cb_msg_sound = (CheckBox) findViewById(R.id.checkbox_msg_sound);
		cb_msg_vibration = (CheckBox) findViewById(R.id.checkbox_msg_vibration);

		// 声音和震动提醒菜单
		setting_msg_sound = (LinearLayout) findViewById(R.id.setting_msg_sound);
		setting_msg_vibration = (LinearLayout) findViewById(R.id.setting_msg_vibration);

		// 菜单
		findViewById(R.id.setting_font_size).setOnClickListener(this);
		findViewById(R.id.update).setOnClickListener(this);
		findViewById(R.id.recommendation).setOnClickListener(this);
		findViewById(R.id.use_help).setOnClickListener(this);
		findViewById(R.id.setting_shake).setOnClickListener(this);
		findViewById(R.id.ll_feedback).setOnClickListener(this);
		findViewById(R.id.setting_msgnotification).setOnClickListener(this);
		findViewById(R.id.setting_msg_sound).setOnClickListener(this);
		findViewById(R.id.setting_msg_vibration).setOnClickListener(this);
		findViewById(R.id.ll_logout).setOnClickListener(this);

		cb_msg_notification.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				toggleShowMsgSettingMenu(arg1);
			}
		});

		// 版本号
		iv_update = (ImageView) findViewById(R.id.version_new);
		tv_textsize = (TextView) findViewById(R.id.font_size);
		tv_shaketext = (TextView) findViewById(R.id.tv_shake);
		tv_current_version = (TextView) findViewById(R.id.current_version);
	}

	@Override
	public void onBackPressed() {

		// 保存设置内容
		AppSetting.setTextSize(getApplicationContext(), textSize);// 字体大小
		AppSetting.setReceiveNotify(getApplicationContext(), cb_receiveNotify.isChecked());// 是否接收推送
		AppSetting.setUseGestureBack(getApplicationContext(), cb_useSwipe.isChecked());// 是否启用手势返回
		AppSetting.setBlockImage(getApplicationContext(), cb_blockImage.isChecked());// 是否启用无图模式

		// 消息提醒
		AppSetting.setMsgNotifyAllowed(getApplicationContext(), cb_msg_notification.isChecked());
		AppSetting.setMsgSoundAllowed(getApplicationContext(), cb_msg_sound.isChecked());
		AppSetting.setMsgVibrateAllowed(getApplicationContext(), cb_msg_vibration.isChecked());

		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.setting_font_size:
			// 设置字体大小 弹出window
			showTextSizeWindow();
			break;
		case R.id.setting_shake:
			// 设置摇一摇 弹出window
			// showShakeSettingWindow();
			break;

		case R.id.setting_msgnotification:
			// 设置消息提醒
			cb_msg_notification.setChecked(!cb_msg_notification.isChecked());
			toggleShowMsgSettingMenu(cb_msg_notification.isChecked());
			break;

		case R.id.setting_msg_sound:
			// 设置声音提醒
			cb_msg_sound.setChecked(!cb_msg_sound.isChecked());
			break;

		case R.id.setting_msg_vibration:
			// 设置震动提醒
			cb_msg_vibration.setChecked(!cb_msg_vibration.isChecked());
			break;

		case R.id.update:
			// 检查更新
			checkUpdate();
			break;

		case R.id.recommendation:
			// 推荐给朋友
//			share("研究生院APP-手机校园门户");
		    ShareHelper.showShare(this, "瓜大生活", "欢迎下载瓜大生活APP", "http://www.pgyer.com/nwpu", "");
			break;

		case R.id.use_help:
			// 使用帮助
			toHelpActivity();
			break;
		case R.id.ll_feedback:
			toFeedback();
			break;

		case R.id.ll_logout:
			// 注销登录
			logout();
			break;
		}
	}

	private void toFeedback() {
		// startActivity(new Intent(getActivity().getApplicationContext(),
		// FeedbackActivity.class));
		// getActivity().overridePendingTransition(R.anim.slide_in_right,
		// R.anim.fade_outs);

		FeedbackAgent agent = new FeedbackAgent(getApplicationContext());
		agent.startDefaultThreadActivity();
	}

	private void toHelpActivity() {
		WebViewActivity.startThis(SettingsActivity.this, Const.helpurl, "平台简介");
	}

	private void showTextSizeWindow() {
		final MPopupWindow mPopupWindow = new MPopupWindow();

		OnClickListener l = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.ll_small:
					mPopupWindow.view.findViewById(R.id.iv_small).setVisibility(View.VISIBLE);
					mPopupWindow.view.findViewById(R.id.iv_medium).setVisibility(View.GONE);
					mPopupWindow.view.findViewById(R.id.iv_big).setVisibility(View.GONE);
					textSize = AppSetting.TS_SMALL;
					tv_textsize.setText("小号");
					mPopupWindow.dismiss();
					break;

				case R.id.ll_medium:
					mPopupWindow.view.findViewById(R.id.iv_small).setVisibility(View.GONE);
					mPopupWindow.view.findViewById(R.id.iv_medium).setVisibility(View.VISIBLE);
					mPopupWindow.view.findViewById(R.id.iv_big).setVisibility(View.GONE);
					textSize = AppSetting.TS_MEDIUM;
					tv_textsize.setText("中号");
					mPopupWindow.dismiss();
					break;

				case R.id.ll_big:
					mPopupWindow.view.findViewById(R.id.iv_small).setVisibility(View.GONE);
					mPopupWindow.view.findViewById(R.id.iv_medium).setVisibility(View.GONE);
					mPopupWindow.view.findViewById(R.id.iv_big).setVisibility(View.VISIBLE);
					textSize = AppSetting.TS_BIG;
					tv_textsize.setText("大号");
					mPopupWindow.dismiss();
					break;
				case R.id.ll_cancel:
					mPopupWindow.dismiss();
					break;

				}
			}
		};

		mPopupWindow.showWindow(getApplicationContext(), findViewById(R.id.main_view), l, textSize);
	}

	/**
	 * 分享
	 * 
	 * @param text
	 */
	private void share(String text) {
		Intent itt = new Intent(Intent.ACTION_SEND);
		itt.setType("text/plain");
		itt.putExtra(Intent.EXTRA_TEXT, text);
		itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(itt, "分享到："));
	}

	public Handler checkUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg != null && (Boolean) msg.obj) {
				// 需要更新
				showUpdateDialog();
			} else {
				// 不需要更新
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "已是最新版本");
			}
		}
	};

	/**
	 * 检查更新 点击菜单时调用
	 */
	private void checkUpdate() {
		CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "正在获取版本信息...");
		UpdateService service = new UpdateService(getApplicationContext());
		service.setHandler(checkUpdateHandler);
		service.isNeedUpdate();// 结果在handler中返回
	}

	private void openBrowser(String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri uri = Uri.parse(url);
		intent.setData(uri);
		startActivity(intent);
	}

	private void showUpdateDialog() {
		UpdateInfoEntity infoEntity = UpdateService.updateInfoEntity;
		if (infoEntity == null)
			return;
		StringBuffer msg = new StringBuffer();
		if (!TextUtils.isEmpty(infoEntity.getVersion())) {
			msg.append("版本号：" + infoEntity.getVersion() + "\n");
		}
		if (!TextUtils.isEmpty(infoEntity.getSize())) {
			msg.append("大小：" + infoEntity.getSize() + "\n");
		}
		if (!TextUtils.isEmpty(infoEntity.getDescription())) {
			msg.append("描述：" + infoEntity.getDescription() + "\n");
		}
		msg.append("是否下载更新?");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("检测到新版本");
		builder.setMessage(msg.toString());
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				{
					// 使用默认浏览器下载
					if (UpdateService.updateInfoEntity != null
							&& !TextUtils.isEmpty(UpdateService.updateInfoEntity.getApkurl())) {
						openBrowser(UpdateService.updateInfoEntity.getApkurl());
					}
				}
			}
		}).setNegativeButton("取消", null);
		builder.show();
	}

	private void toggleShowMsgSettingMenu(Boolean isShow) {
		if (isShow) {
			// 允许消息通知
			setting_msg_sound.setVisibility(View.VISIBLE);
			setting_msg_vibration.setVisibility(View.VISIBLE);
		} else {
			// 不允许消息通知
			setting_msg_sound.setVisibility(View.GONE);
			setting_msg_vibration.setVisibility(View.GONE);
		}
	}

	private void logout() {
		CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "正在注销...");
		AccountHelper.logout(getApplicationContext());
		onBackPressed();
	}
}
