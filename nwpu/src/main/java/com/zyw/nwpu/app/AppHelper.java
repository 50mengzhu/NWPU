package com.zyw.nwpu.app;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.zyw.nwpu.R;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseSettingsProvider;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.model.EaseNotifier;
import com.easemob.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.util.EMLog;
import com.zyw.nwpu.ChatActivity;
import com.zyw.nwpu.MainTabActivity;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.service.AvatarAndNicknameService;
import com.zyw.nwpulib.model.UserInfo;

public class AppHelper {
	/**
	 * 数据同步listener
	 */
	static public interface DataSyncListener {
		/**
		 * 同步完毕
		 * 
		 * @param success
		 *            true：成功同步到数据，false失败
		 */
		public void onSyncComplete(boolean success);
	}

	protected static final String TAG = "DemoHelper";

	private EaseUI easeUI;

	private static AppHelper instance = null;

	private boolean alreadyNotified = false;

	private Context appContext;

	private AppHelper() {
	}

	public synchronized static AppHelper getInstance() {
		if (instance == null) {
			instance = new AppHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {

		if (EaseUI.getInstance().init(context)) {
			appContext = context;

			// 设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
			EMChat.getInstance().setDebugMode(false);

			iniEaseUI();

			// demoModel = new DemoModel(context);
			// 设置chat options
			setChatoptions();

			// 设置全局监听
			setGlobalListeners();
		}
	}

	private void setChatoptions() {
		// easeui库默认设置了一些options，可以覆盖
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		options.setAcceptInvitationAlways(false);// 不需要验证，直接同意
	}

	private void iniEaseUI() {
		// get easeui instance
		easeUI = EaseUI.getInstance();
		// 调用easeui的api设置providers
		setEaseUIProviders();
	}

	public UserInfo getUserInfoByStudentId(String stuid) {
		if (AvatarAndNicknameService.userInfoMap != null && AvatarAndNicknameService.userInfoMap.containsKey(stuid)) {
			return AvatarAndNicknameService.userInfoMap.get(stuid);
		} else {
			AvatarAndNicknameService.getUserInfoAndSave(stuid, null);
			UserInfo info = new UserInfo();
			return info;
		}
	}

	protected void setEaseUIProviders() {
		// 需要easeui库显示用户头像和昵称设置此provider
		easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

			@Override
			public EaseUser getUser(String username) {
				EaseUser easeUser = new EaseUser(username);
				UserInfo info = getUserInfoByStudentId(username);
				if (TextUtils.isEmpty(info.nickname)) {
					info.nickname = username;
				}
				easeUser.setNick(info.nickname);
				easeUser.setAvatar(info.avatar);
				return easeUser;
			}
		});

		// 不设置，则使用easeui默认的
		easeUI.setSettingsProvider(new EaseSettingsProvider() {

			@Override
			public boolean isSpeakerOpened() {
				return AppSetting.isSpeakerOpened(appContext);
			}

			@Override
			public boolean isMsgVibrateAllowed(EMMessage message) {
				return AppSetting.isMsgVibrateAllowed(appContext);
			}

			@Override
			public boolean isMsgSoundAllowed(EMMessage message) {
				return AppSetting.isMsgSoundAllowed(appContext);
			}

			@Override
			public boolean isMsgNotifyAllowed(EMMessage message) {
				return AppSetting.isMsgNotifyAllowed(appContext);
			}
		});

		// 不设置，则使用easeui默认的
		easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// 修改标题,这里使用默认
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// 设置小图标，这里为默认
				return 0;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// 设置状态栏的消息提示，可以根据message的类型做相应提示
				String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}
				return message.getFrom() + ": " + ticker;
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
				return null;
				// return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// 设置点击通知栏跳转事件
				Intent intent = new Intent(appContext, ChatActivity.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // 单聊信息
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", HXConst.CHATTYPE_SINGLE);
				} else { // 群聊信息
					// message.getTo()为群聊id
					intent.putExtra("userId", message.getTo());
					if (chatType == ChatType.GroupChat) {
						intent.putExtra("chatType", HXConst.CHATTYPE_GROUP);
					} else {
						intent.putExtra("chatType", HXConst.CHATTYPE_CHATROOM);

					}
				}
				return intent;
			}
		});
	}

	/**
	 * 设置全局事件监听
	 */
	protected void setGlobalListeners() {

		// 注册连接监听
		EMChatManager.getInstance().addConnectionListener(new EMConnectionListener() {
			@Override
			public void onDisconnected(int error) {
				// 断开连接
				if (error == EMError.USER_REMOVED) {
					onCurrentAccountRemoved();
				} else if (error == EMError.CONNECTION_CONFLICT) {
					onConnectionConflict();
				}
			}

			@Override
			public void onConnected() {

				// in case group and contact were already synced, we
				// supposed to
				// notify sdk we are ready to receive the events
				new Thread() {
					@Override
					public void run() {
						AppHelper.getInstance().notifyForRecevingEvents();
					}
				}.start();
			}
		});
		// 注册消息事件监听
		/**
		 * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
		 * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
		 */
		EMChatManager.getInstance().registerEventListener(new EMEventListener() {
			private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onEvent(EMNotifierEvent event) {
				EMMessage message = null;
				if (event.getData() instanceof EMMessage) {
					message = (EMMessage) event.getData();
					EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
				}

				switch (event.getEvent()) {
				case EventNewMessage:
					// 应用在后台，不需要刷新UI,通知栏提示新消息
					if (!easeUI.hasForegroundActivies()) {
						getNotifier().onNewMsg(message);
					}
					break;
				case EventOfflineMessage:
					if (!easeUI.hasForegroundActivies()) {
						EMLog.d(TAG, "received offline messages");
						@SuppressWarnings("unchecked")
						List<EMMessage> messages = (List<EMMessage>) event.getData();
						getNotifier().onNewMesg(messages);
					}
					break;
				// below is just giving a example to show a cmd toast,
				// the app
				// should not follow this
				// so be careful of this
				case EventNewCMDMessage: {

					EMLog.d(TAG, "收到透传消息");
					// 获取消息body
					CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
					final String action = cmdMsgBody.action;// 获取自定义action

					// 获取扩展属性 此处省略
					// message.getStringAttribute("");
					EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
					final String str = appContext.getString(R.string.receive_the_passthrough);

					final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
					IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

					if (broadCastReceiver == null) {
						broadCastReceiver = new BroadcastReceiver() {

							@Override
							public void onReceive(Context context, Intent intent) {
								// TODO Auto-generated method stub
								Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT)
										.show();
							}
						};

						// 注册广播接收者
						appContext.registerReceiver(broadCastReceiver, cmdFilter);
					}

					Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
					broadcastIntent.putExtra("cmd_value", str + action);
					appContext.sendBroadcast(broadcastIntent, null);

					break;
				}
				case EventDeliveryAck:
					message.setDelivered(true);
					break;
				case EventReadAck:
					message.setAcked(true);
					break;
				// add other events in case you are interested in
				default:
					break;
				}
			}
		});
	}

	/**
	 * 账号在别的设备登录
	 */
	protected void onConnectionConflict() {
		Intent intent = new Intent(appContext, MainTabActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HXConst.ACCOUNT_CONFLICT, true);
		appContext.startActivity(intent);
	}

	/**
	 * 账号被移除
	 */
	protected void onCurrentAccountRemoved() {
		Intent intent = new Intent(appContext, MainTabActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HXConst.ACCOUNT_REMOVED, true);
		appContext.startActivity(intent);
	}

	/**
	 * 是否登录成功过
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMChat.getInstance().isLoggedIn();
	}

	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		endCall();
		EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}

	/**
	 * 获取消息通知类
	 * 
	 * @return
	 */
	public EaseNotifier getNotifier() {
		return easeUI.getNotifier();
	}

	void endCall() {
		try {
			EMChatManager.getInstance().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void notifyForRecevingEvents() {
		if (alreadyNotified) {
			return;
		}

		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		alreadyNotified = true;
	}

	synchronized void reset() {

		alreadyNotified = false;
	}

	public void pushActivity(Activity activity) {
		easeUI.pushActivity(activity);
	}

	public void popActivity(Activity activity) {
		easeUI.popActivity(activity);
	}

}
