package com.zyw.nwpu.adapter;

import java.util.List;

import com.zyw.nwpu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 2016-4-22
 * 
 * @author Rocket
 * 
 */
public class BBSTagAdapter extends BaseAdapter {

	private List<String> data;
	private Context context;

	public BBSTagAdapter(Context context, List<String> tagData) {
		this.context = context;
		this.data = tagData;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/**
	 * 当ListView每次显示一个条目，则会调用该方法
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DataWrapper dataWrapper = null;

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_item_tag, null);
			dataWrapper = new DataWrapper();
			dataWrapper.tv_tag = (TextView) convertView
					.findViewById(R.id.tv_tag);
			convertView.setTag(dataWrapper);
		} else {
			dataWrapper = (DataWrapper) convertView.getTag();
		}
		dataWrapper.tv_tag.setText("#" + data.get(position) + "#");
		return convertView;
	}

	private final class DataWrapper {
		public TextView tv_tag;

		public DataWrapper() {
		}
	}
}
