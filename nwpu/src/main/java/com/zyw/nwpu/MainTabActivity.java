package com.zyw.nwpu;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;

import org.xutils.x;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.app.AppHelper;
import com.zyw.nwpu.app.HXConst;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.fragment.App_Fragment;
import com.zyw.nwpu.fragment.Me_Fragment;
import com.zyw.nwpu.fragment.News_Fragment;
import com.zyw.nwpu.fragment.StatusListFragment;
import com.zyw.nwpu.listener.ShakeListener;
import com.zyw.nwpu.listener.ShakeListener.OnShakeListener;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.service.ChaoqitixingService;
import com.zyw.nwpu.service.WeekHelper;
import com.zyw.nwpu.service.MyTabContentFactory;
import com.zyw.nwpu.service.SignService;
import com.zyw.nwpu.service.SignService.OnCheckSignListener;
import com.zyw.nwpu.tool.AppUtils;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpulib.utils.SPUtils;
import com.zyw.nwpu.update.UpdateInfoEntity;
import com.zyw.nwpu.update.UpdateService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/** * Tab导航 */
public class MainTabActivity extends FragmentActivity implements EMEventListener, View.OnClickListener {

	final String TABID_NEWS = "资讯";
	final String TABID_NOTIFY = "论坛";
	final String TABID_APP = "应用";
	final String TABID_ME = "我";

	News_Fragment newsFragment;
	StatusListFragment bbsFragment;
	
	App_Fragment appFragment;
	Me_Fragment meFragment;

	private LocationClient mLocationClient;

	android.support.v4.app.FragmentTransaction ft;

	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	// 标题
	private RelativeLayout rl_titlebar;
	private TextView tv_title;
	private ImageButton ib_left;
	private TextView tv_unread_num;
	private TextView tv_right_text;
	private ImageButton ib_right;

	// 底部的四个按钮
	private LinearLayout rl_tab_main;
	private LinearLayout rl_tab_bbs;
	private LinearLayout rl_tab_app;
	private LinearLayout rl_tab_me;

	// 底部标题
	private TextView tv_main;
	private TextView tv_bbs;
	private TextView tv_app;
	private TextView tv_me;

	private LocalBroadcastManager broadcastManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.getBoolean(HXConst.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			AppHelper.getInstance().logout(false, null);
			finish();
			Login.startThis(MainTabActivity.this);
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			Login.startThis(MainTabActivity.this);
			return;
		}

		setContentView(R.layout.activity_main);

		iniView();

		// 开始定位
		iniLocClient();

		// 第一次调用初始化接口
		iniMethod(null);

		checkUpdate();
		checkNetwork();

		onTabChange(0);

		// 超期查询
		if (AVUser.getCurrentUser() != null) {
			String studentId = AVUser.getCurrentUser().getString(XUser.STUDENTID);
			ChaoqitixingService.getChaoqiInfo(new Intent(MainTabActivity.this, ChaoqitixingActivity.class), studentId,
					null);
		}

		broadcastManager = LocalBroadcastManager.getInstance(this);

//		showQianDaoDialog();
	}

	private void showQianDaoDialog() {
		// 第一次进入会有提示
		if (!SPUtils.contains(x.app(), "hasShowQianDaoTip")) {
			new AlertDialog.Builder(this).setTitle("福利福利！！！").setMessage("【应用】中新增了签到功能，签到就送锐捷流量！有这等好事，别忘了转告你的同学哦。")
					.setPositiveButton("好的", null).show();
			SPUtils.put(x.app(), "hasShowQianDaoTip", true);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (meFragment != null) {
			meFragment.onActivityResult(arg0, arg1, arg2);
		}
	}

	private void iniTab() {
		rl_tab_main = (LinearLayout) findViewById(R.id.tab_main);
		rl_tab_bbs = (LinearLayout) findViewById(R.id.tab_bbs);
		rl_tab_app = (LinearLayout) findViewById(R.id.tab_app);
		rl_tab_me = (LinearLayout) findViewById(R.id.tab_me);

		tv_main = (TextView) findViewById(R.id.tv_main);
		tv_bbs = (TextView) findViewById(R.id.tv_bbs);
		tv_app = (TextView) findViewById(R.id.tv_app);
		tv_me = (TextView) findViewById(R.id.tv_me);

		rl_tab_main.setOnClickListener(this);
		rl_tab_bbs.setOnClickListener(this);
		rl_tab_app.setOnClickListener(this);
		rl_tab_me.setOnClickListener(this);
	}

	private void iniView() {
		rl_titlebar = (RelativeLayout) findViewById(R.id.rl_titlebar);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_unread_num = (TextView) findViewById(R.id.unread_msg_number);
		ib_left = (ImageButton) findViewById(R.id.ib_msg);
		tv_right_text = (TextView) findViewById(R.id.tv_right_txt);
		ib_right = (ImageButton) findViewById(R.id.ib_right_img);

		rl_titlebar.setVisibility(View.VISIBLE);
		tv_title.setText(AppUtils.getAppName(getApplicationContext()));
		tv_unread_num.setVisibility(View.GONE);
		tv_right_text.setVisibility(View.GONE);
		ib_right.setVisibility(View.GONE);

		ib_left.setOnClickListener(this);
		tv_right_text.setOnClickListener(this);
		ib_right.setOnClickListener(this);

		tv_right_text.setText(WeekHelper.getWeek());

		iniTab();
	}

	protected void toPublishActivity() {
		if (!AccountHelper.isLogedIn(getApplicationContext())) {
			// 未登录自己服务器
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请先登陆");
			Login.startThis(MainTabActivity.this);
		} else {
			BBSPublishActivity.startThis(MainTabActivity.this);
		}
	}

	protected void onPause() {
		super.onPause();
		AVAnalytics.onPause(this);
	}

	protected void onResume() {
		super.onResume();
		AVAnalytics.onResume(this);

		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
		}

		// unregister this event listener when this activity enters the
		// background
		AppHelper.getInstance().pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });

		// 更新消息数
		refreshUIWithMessage();

	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		AppHelper.getInstance().popActivity(this);
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(HXConst.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	private void checkNetwork() {
		if (!CommonUtil.NetworkUtils.checkNetState(getApplicationContext())) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
					"【" + AppUtils.getAppName(getApplicationContext()) + "】请检查网络连接");
		}
	}

	public Handler checkUpdateHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			if (msg == null)
				return;

			UpdateInfoEntity updateInfoEntity = (UpdateInfoEntity) msg.obj;

			if (updateInfoEntity == null)
				return;

			boolean isForceUpdate = updateInfoEntity.getForceUpdate();
			String url = updateInfoEntity.getApkurl();
			String desc = updateInfoEntity.getDescription();
			String size = updateInfoEntity.getSize();

			desc += "\n大小：" + size;

			// 强制更新
			showUpdateDialog(url, desc);
		}
	};

	/**
	 * 强制用户更新
	 * 
	 * @param downloadUrl
	 */
	protected void showUpdateDialog(final String downloadUrl, String desc) {
		final boolean isForceUpdate = false;
		new AlertDialog.Builder(MainTabActivity.this).setTitle("新版本提示").setMessage(desc)
				// 设置显示的内容
				.setPositiveButton("下载", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonUtil.openBrowser(MainTabActivity.this, downloadUrl);
					}
				}).setNegativeButton("忽略", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isForceUpdate)
							System.exit(0);
					}
				}).show();
	}

	/**
	 * 检查更新
	 */
	private void checkUpdate() {
		UpdateService service = new UpdateService(getApplicationContext());
		service.setHandler(checkUpdateHandler);
		service.isNeedUpdate_main();// 结果在handler中返回
	}

	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "再按一次退出");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void hideAllFrag() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (newsFragment != null) {
			fragmentTransaction.hide(newsFragment);
		}
		if (bbsFragment != null) {
			fragmentTransaction.hide(bbsFragment);
		}
		if (appFragment != null) {
			fragmentTransaction.hide(appFragment);
		}
		if (meFragment != null) {
			fragmentTransaction.hide(meFragment);
		}

		fragmentTransaction.commit();
	}

	@Override
	public void onLowMemory() {

		FragmentManager fm = getSupportFragmentManager();
		ft = fm.beginTransaction();

		if (newsFragment != null) {
			ft.remove(newsFragment);
			newsFragment = null;
		}

		if (bbsFragment != null) {
			ft.remove(bbsFragment);
			bbsFragment = null;
		}

		if (appFragment != null) {
			ft.remove(appFragment);
			appFragment = null;
		}
		if (meFragment != null) {
			ft.remove(meFragment);
			meFragment = null;
		}

		super.onLowMemory();
	}

	// 定位相关
	private void iniLocClient() {
		mLocationClient = new LocationClient(this);// 定位初始化
		MyLocationListenner myListener = new MyLocationListenner();
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(10000);// 设置发起定位请求的间隔时间为2000ms
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// 第二次调用初始化接口
			iniMethod(location);

			// 保存到AppApplication

			if (!TextUtils.isEmpty(location.getCity())) {
				AppApplication.province = location.getProvince();
				AppApplication.city = location.getCity();
				AppApplication.addr = location.getAddrStr();
				AppApplication.lat = location.getLatitude();
				AppApplication.lng = location.getLongitude();
				destroyLocClient();
			}
		}
	}

	private void iniMethod(BDLocation location) {

		Map<String, Object> parameters = new HashMap<String, Object>();

		// 设备号
		parameters.put("installationId", AVInstallation.getCurrentInstallation().getInstallationId());

		// 位置信息
		if (location != null) {
			parameters.put("province", location.getProvince());
			parameters.put("city", location.getCity());
			parameters.put("addr", location.getAddrStr());
			parameters.put("lat", location.getLatitude());
			parameters.put("lng", location.getLongitude());
		}

		// 手机信息
		parameters.put("deviceManufacturer", CommonUtil.SystemUtils.getDeviceManufacturer());
		parameters.put("deviceModel", CommonUtil.SystemUtils.getModel());
		parameters.put("phoneNum", CommonUtil.SystemUtils.getPhoneNum(getApplicationContext()));
		parameters.put("sdk", CommonUtil.SystemUtils.getSystemSDKVersion());
		parameters.put("release", CommonUtil.SystemUtils.getSystemReleaseVersion());

		// 客户端版本号
		parameters.put("appVersion", CommonUtil.AppUtils.getVersionName(getApplicationContext()));

		// 学号
		if (AccountHelper.isLogedIn(getApplicationContext()))
			parameters.put("schoolnum", AVUser.getCurrentUser().getString(XUser.STUDENTID));
		else
			parameters.put("schoolnum", "未登录用户");

		AVCloud.setProductionMode(true); // 调用生产环境云代码
		AVCloud.callFunctionInBackground("initMethod", parameters, null);

	}

	public void destroyLocClient() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();// 关闭定位SDK
			mLocationClient = null;
		}
	}

	@Override
	protected void onDestroy() {
		destroyLocClient();
		super.onDestroy();
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;

	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;

	// private BroadcastReceiver internalDebugReceiver;
	// private BroadcastReceiver broadcastReceiver;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		AccountHelper.logout(getApplicationContext());
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainTabActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainTabActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						Login.startThis(MainTabActivity.this);
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		AppHelper.getInstance().logout(false, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainTabActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainTabActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
						Login.startThis(MainTabActivity.this);
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(HXConst.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(HXConst.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			AppHelper.getInstance().getNotifier().onNewMsg(message);

			refreshUIWithMessage();
			break;
		}

		case EventOfflineMessage: {
			refreshUIWithMessage();
			break;
		}

		case EventConversationListChanged: {
			refreshUIWithMessage();
			break;
		}

		default:
			break;
		}
	}

	public static final String Message_Action = "Message_Action";

	public void refreshUIWithMessage() {
		broadcastManager.sendBroadcast(new Intent(Message_Action));
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
			}
		});
	}

	/**
	 * 刷新未读消息数
	 */
	public int updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count != 0 && tv_unread_num != null) {
			tv_unread_num.setText(String.valueOf(count));
			tv_unread_num.setVisibility(View.VISIBLE);
		} else {
			tv_unread_num.setVisibility(View.GONE);
		}
		return count;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}

	private int currentTab = -1;

	private void onTabChange(int i) {
		if (i == currentTab)
			return;

		currentTab = i;

		hideAllFrag();
		ft = getSupportFragmentManager().beginTransaction();
		switch (i) {
		case 0:
		default:
			rl_titlebar.setVisibility(View.VISIBLE);
			tv_title.setText(AppUtils.getAppName(getApplicationContext()));
			tv_right_text.setVisibility(View.GONE);
			ib_right.setVisibility(View.GONE);

			tv_main.setTextColor(getResources().getColor(R.color.bg_title));
			tv_bbs.setTextColor(getResources().getColor(R.color.gray));
			tv_app.setTextColor(getResources().getColor(R.color.gray));
			tv_me.setTextColor(getResources().getColor(R.color.gray));

			rl_tab_main.setSelected(true);
			rl_tab_bbs.setSelected(false);
			rl_tab_app.setSelected(false);
			rl_tab_me.setSelected(false);

			if (newsFragment == null) {
				newsFragment = new News_Fragment();
				ft.add(R.id.realtabcontent, newsFragment, TABID_NEWS);
			}
			ft.show(newsFragment);
			break;
		case 1:
			rl_titlebar.setVisibility(View.VISIBLE);
			tv_title.setText("论坛");
			tv_right_text.setVisibility(View.GONE);
			ib_right.setVisibility(View.VISIBLE);
			rl_tab_main.setSelected(false);
			rl_tab_bbs.setSelected(true);
			rl_tab_app.setSelected(false);
			rl_tab_me.setSelected(false);
			tv_main.setTextColor(getResources().getColor(R.color.gray));
			tv_bbs.setTextColor(getResources().getColor(R.color.bg_title));
			tv_app.setTextColor(getResources().getColor(R.color.gray));
			tv_me.setTextColor(getResources().getColor(R.color.gray));

			if (bbsFragment == null) {
				bbsFragment = new StatusListFragment();
				ft.add(R.id.realtabcontent, bbsFragment, TABID_NEWS);
			}
			ft.show(bbsFragment);
			break;
		case 2:
			rl_titlebar.setVisibility(View.VISIBLE);
			tv_title.setText("应用");
			tv_right_text.setVisibility(View.VISIBLE);
			ib_right.setVisibility(View.GONE);
			rl_tab_main.setSelected(false);
			rl_tab_bbs.setSelected(false);
			rl_tab_app.setSelected(true);
			rl_tab_me.setSelected(false);
			tv_main.setTextColor(getResources().getColor(R.color.gray));
			tv_bbs.setTextColor(getResources().getColor(R.color.gray));
			tv_app.setTextColor(getResources().getColor(R.color.bg_title));
			tv_me.setTextColor(getResources().getColor(R.color.gray));

			if (appFragment == null) {
				appFragment = new App_Fragment();
				ft.add(R.id.realtabcontent, appFragment, TABID_NEWS);
			}
			ft.show(appFragment);
			break;
		case 3:
			rl_titlebar.setVisibility(View.GONE);
			rl_tab_main.setSelected(false);
			rl_tab_bbs.setSelected(false);
			rl_tab_app.setSelected(false);
			rl_tab_me.setSelected(true);
			tv_main.setTextColor(getResources().getColor(R.color.gray));
			tv_bbs.setTextColor(getResources().getColor(R.color.gray));
			tv_app.setTextColor(getResources().getColor(R.color.gray));
			tv_me.setTextColor(getResources().getColor(R.color.bg_title));

			if (meFragment == null) {
				meFragment = new Me_Fragment();
				ft.add(R.id.realtabcontent, meFragment, TABID_NEWS);
			}
			ft.show(meFragment);
			break;
		}
		ft.commit();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ib_msg:
			// 消息界面
			MsgActivity.startThis(MainTabActivity.this);
			break;
		case R.id.tv_right_txt:
			// 右边的文字 用于显示第几周
			CalendarActivity.startThis(MainTabActivity.this,
					CommonUtil.ScreenUtils.getScreenWidth(getApplicationContext()));
			break;

		case R.id.ib_right_img:
			// 右边的图片，用于显示发布状态
			BBSPublishActivity.startThis(MainTabActivity.this);
			break;

		case R.id.tab_main:
			onTabChange(0);
			break;
		case R.id.tab_bbs:
			onTabChange(1);
			break;
		case R.id.tab_app:
			onTabChange(2);
			break;
		case R.id.tab_me:
			onTabChange(3);
			break;
		default:
			break;
		}
	}
}