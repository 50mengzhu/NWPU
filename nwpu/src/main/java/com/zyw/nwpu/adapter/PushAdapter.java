package com.zyw.nwpu.adapter;

import java.util.ArrayList;

import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.PushEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PushAdapter extends BaseAdapter {
	ArrayList<PushEntity> push_list;
	Context context;
	LayoutInflater inflater = null;

	public PushAdapter(Context applicationContext,
			ArrayList<PushEntity> push_list) {
		this.context = applicationContext;
		this.push_list = push_list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return push_list == null ? 0 : push_list.size();
	}

	@Override
	public PushEntity getItem(int position) {
		if (push_list != null && push_list.size() != 0) {
			return push_list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_push, null);
			mHolder = new ViewHolder();
			mHolder.tv_push_title = (TextView) view
					.findViewById(R.id.push_title);
			mHolder.tv_push_time = (TextView) view.findViewById(R.id.push_time);

			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		// 获取position对应的数据
		PushEntity push = getItem(position);
		mHolder.tv_push_title.setText(push.title);
		mHolder.tv_push_time.setText(push.time);
		return view;
	}

	static class ViewHolder {
		TextView tv_push_title;
		TextView tv_push_time;
	}

}
