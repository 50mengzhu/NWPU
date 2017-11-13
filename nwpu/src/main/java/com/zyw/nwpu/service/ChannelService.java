package com.zyw.nwpu.service;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

public class ChannelService {

	public void queryChannels() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Channel");
		query.orderByDescending("channelId");
		query.whereEqualTo("isUsed", true);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (!filterException(arg1))
					return;
				if (arg0 == null || arg0.size() == 0)
					return;

			}
		});
	}
//
//	private void changeChannels(List<AVObject> arg0) {
//		List<ChannelItem> localUserChannels = ChannelManage.getManage(
//				AppApplication.getApp().getSQLHelper()).getUserChannel();
//
//		List<ChannelItem> localOtherChannels = ChannelManage.getManage(
//				AppApplication.getApp().getSQLHelper()).getOtherChannel();
//
//		List<ChannelItem> serverChannels = new ArrayList<ChannelItem>();
//
//		for (int i = 0; i < arg0.size(); i++) {
//			ChannelItem item = new ChannelItem();
//			item.id = Integer.parseInt(arg0.get(i).getString("channelId"));
//			item.name = arg0.get(i).getString("channelTitle");
//			item.orderId = i;
//			if (arg0.get(i).getBoolean("isSelected"))
//				item.selected = 1;
//			else
//				item.selected = 0;
//		}
//
//		for (int i = 0; i < localUserChannels.size(); i++) {
//
//			Boolean isExist = false;
//
//			int currentId = localUserChannels.get(i).getId();
//
//			for (ChannelItem channelItem : serverChannels) {
//				if (currentId == channelItem.getId()) {
//					isExist = true;
//					break;
//				}
//			}
//			
//			if(!isExist)
//			
//		}
//	}

	private Boolean filterException(AVException e) {
		if (e != null) {
			// CommonUtil.ToastUtils.showShortToast(context, txt);
			return false;
		}
		return true;
	}
}
