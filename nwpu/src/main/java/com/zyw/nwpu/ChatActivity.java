package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.model.EaseNotifier;
import com.easemob.easeui.ui.EaseChatFragment;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.HXConst;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.fragment.ChatFragment;
import com.zyw.nwpulib.utils.CommonUtil;

/**
 * 聊天页面，需要fragment的使用{@link #EaseChatFragment}
 */
@ContentView(R.layout.em_activity_chat)
public class ChatActivity extends BaseActivity {
	public static ChatActivity activityInstance;
	private EaseChatFragment chatFragment;
	String toChatUsername;

	public static void startThis(Context cxt, String studentId, String nickname) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, ChatActivity.class);
			intent.putExtra(EaseConstant.EXTRA_USER_ID, studentId);
			intent.putExtra(EaseConstant.EXTRA_USER_NICKNAME, nickname);
			intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, HXConst.CHATTYPE_SINGLE);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		activityInstance = this;
		// 聊天人或群id
		toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);

		// 可以直接new EaseChatFratFragment使用
		chatFragment = new ChatFragment();
		// 传入参数
		chatFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityInstance = null;
		EaseNotifier.setIsOnChat(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EaseNotifier.setIsOnChat(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EaseNotifier.setIsOnChat(true);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		chatFragment.onBackPressed();
	}

	// public String getToChatUsername() {
	// return toChatUsername;
	// }
}
