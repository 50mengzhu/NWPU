package com.zyw.nwpu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.ui.EaseChatFragment;
import com.easemob.easeui.ui.EaseChatFragment.EaseChatFragmentListener;
import com.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void setUpView() {
		setChatFragmentListener(this);
		super.setUpView();
	}

	// 扩展底部菜单
	@Override
	protected void registerExtendMenuItem() {
		// demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
		super.registerExtendMenuItem();
	}

	@Override
	public void onEnterToChatDetails() {
	}

	@Override
	public void onAvatarClick(String username) {
		// 头像点击事件
	}

	@Override
	public boolean onMessageBubbleClick(EMMessage message) {
		// 消息框点击事件，demo这里不做覆盖，如需覆盖，return true
		return false;
	}

	@Override
	public void onMessageBubbleLongClick(EMMessage message) {
		// 消息框长按

		final CharSequence[] items1 = { "复制消息", "删除消息" };
		final CharSequence[] items2 = { "删除消息" };

		if (message.getType().ordinal() == EMMessage.Type.TXT.ordinal()) {
			// 文本消息
			new AlertDialog.Builder(getActivity()).setItems(items1, new DialogInterface.OnClickListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						// 复制消息
						clipboard.setText(((TextMessageBody) contextMenuMessage.getBody()).getMessage());
					} else {
						// 删除消息
						conversation.removeMessage(contextMenuMessage.getMsgId());
						messageList.refresh();
					}
				}
			}).create().show();
		} else {
			// 其他消息
			new AlertDialog.Builder(getActivity()).setItems(items2, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						// 删除消息
						conversation.removeMessage(contextMenuMessage.getMsgId());
						messageList.refresh();
					}
				}
			}).create().show();
		}
	}

	@Override
	public boolean onExtendMenuItemClick(int itemId, View view) {
		// 不覆盖已有的点击事件
		return false;
	}

	@Override
	public void onSetMessageAttributes(EMMessage message) {
		// TODO Auto-generated method stub
	}

	@Override
	public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
		// TODO Auto-generated method stub
		return null;
	}
}
