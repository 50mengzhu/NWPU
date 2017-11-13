package com.zyw.nwpu.xmz;

import java.util.List;

import com.zyw.nwpu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 2014-10-15
 * 
 * @author Rocket
 * 
 */

public class ProjectsAdapter extends BaseAdapter {

	private List<Project> data;
	LayoutInflater layoutInflater;

	public ProjectsAdapter(Context context, List<Project> data) {
		this.data = data;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DataWrapper datawrapper = new DataWrapper();

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_project,
					null);

			datawrapper.tv_name = (TextView) convertView
					.findViewById(R.id.item_title);
			datawrapper.tv_starttime = (TextView) convertView
					.findViewById(R.id.item_start);
			datawrapper.tv_endtime = (TextView) convertView
					.findViewById(R.id.item_end);
			datawrapper.tv_loc = (TextView) convertView
					.findViewById(R.id.item_loc);
			datawrapper.tv_zhuban = (TextView) convertView
					.findViewById(R.id.item_zhuban);

			convertView.setTag(datawrapper);

		} else {
			datawrapper = (DataWrapper) convertView.getTag();
		}

		Project item = data.get(position);
		datawrapper.tv_name.setText("项目名称:" + item.getName());
		datawrapper.tv_starttime.setText("开始时间:" + item.getStartTime());
		datawrapper.tv_endtime.setText("结束时间:" + item.getEndTime());
		datawrapper.tv_loc.setText("活动地点" + item.getProject_location());
		datawrapper.tv_zhuban.setText("主办单位:" + item.getDWMC());
		return convertView;
	}

	private final class DataWrapper {
		public TextView tv_name;
		public TextView tv_zhuban;
		public TextView tv_starttime;
		public TextView tv_endtime;
		public TextView tv_loc;

		public DataWrapper() {
		}
	}

}
