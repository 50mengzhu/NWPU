package com.zyw.nwpu.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.util.NetUtils;
import com.zyw.nwpu.ChatActivity;
import com.zyw.nwpu.MainTabActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.app.HXConst;

/**
 * 会话列表
 * 
 * @author Rocket
 * 
 */
public class ConversationListFragment extends EaseConversationListFragment {

	private TextView errorText;

	BroadcastReceiver mReceiver;// 接收消息通知，刷新页面

	@Override
	protected void initView() {
		super.initView();
		View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
		errorItemContainer.addView(errorView);
		errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(MainTabActivity.Message_Action)) {
					refresh();
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(MainTabActivity.Message_Action);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
	}

	@Override
	protected void setUpView() {
		super.setUpView();
		// 注册上下文菜单
		registerForContextMenu(conversationListView);
		conversationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = conversationListView.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(EMChatManager.getInstance().getCurrentUser()))
					Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, 0).show();
				else {
					// 进入聊天页面
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					if (conversation.isGroup()) {
						if (conversation.getType() == EMConversationType.ChatRoom) {
							// it's group chat
							intent.putExtra(HXConst.EXTRA_CHAT_TYPE, HXConst.CHATTYPE_CHATROOM);
						} else {
							intent.putExtra(HXConst.EXTRA_CHAT_TYPE, HXConst.CHATTYPE_GROUP);
						}
					}
					// it's single chat
					intent.putExtra(HXConst.EXTRA_USER_ID, username);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onConnectionDisconnected() {
		super.onConnectionDisconnected();
		if (NetUtils.hasNetwork(getActivity())) {
			errorText.setText(R.string.can_not_connect_chat_server_connection);
		} else {
			errorText.setText(R.string.the_current_network);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;
		boolean deleteMessage = false;
		/*
		 * if (item.getItemId() == R.id.delete_message) { deleteMessage = true;
		 * handled = true; } else
		 */if (item.getItemId() == R.id.delete_conversation) {
			deleteMessage = true;
			handled = true;
		}
		EMConversation tobeDeleteCons = conversationListView
				.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
		// 删除此会话
		EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(),
				deleteMessage);
		refresh();

		// 更新消息未读数
		// ((MainActivity) getActivity()).updateUnreadLabel();

		return handled ? true : super.onContextItemSelected(item);
	}

	@Override
	public void onResume() {
		// this.refresh();
		super.onResume();
	}

}
