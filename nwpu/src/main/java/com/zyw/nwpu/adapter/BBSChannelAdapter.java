package com.zyw.nwpu.adapter;

import java.util.List;

import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.BBSChannelEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 2015-11-6
 * 
 * @author Rocket
 * 
 */
public class BBSChannelAdapter extends BaseAdapter {

	private List<BBSChannelEntity> data;
	private Context context;

	public BBSChannelAdapter(Context context, List<BBSChannelEntity> channelData) {
		this.context = context;
		this.data = channelData;

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
			convertView = layoutInflater.inflate(
					R.layout.list_item_bbs_channel, null);

			dataWrapper = new DataWrapper();
			dataWrapper.tv_channel = (TextView) convertView
					.findViewById(R.id.tv_channel);

			dataWrapper.tv_topicnum = (TextView) convertView
					.findViewById(R.id.tv_topicnum);

			dataWrapper.tv_firsttopic = (TextView) convertView
					.findViewById(R.id.tv_firsttopic);

			dataWrapper.tv_label = (TextView) convertView
					.findViewById(R.id.tv_label);

			dataWrapper.iv_label = (ImageView) convertView
					.findViewById(R.id.iv_label);

			convertView.setTag(dataWrapper);

		} else {
			dataWrapper = (DataWrapper) convertView.getTag();
		}

		dataWrapper.tv_channel.setText(data.get(position).getTitle());
		dataWrapper.tv_topicnum.setText(String.valueOf(data.get(position)
				.getTopicNum()));

		if (data.get(position).getTopicNum() == 0) {
			dataWrapper.tv_topicnum.setVisibility(View.INVISIBLE);
		} else {
			dataWrapper.tv_topicnum.setVisibility(View.VISIBLE);
		}

		dataWrapper.tv_firsttopic.setText(String.valueOf(data.get(position)
				.getFirstTopicContent()));
		dataWrapper.tv_label.setText(data.get(position).getTitle());

		// convertView.startAnimation(AnimationUtils.loadAnimation(mcontext,
		// android.R.anim.fade_in));
		return convertView;
	}

	private final class DataWrapper {
		public TextView tv_channel;
		public TextView tv_topicnum;
		public TextView tv_firsttopic;
		public TextView tv_label;
		public ImageView iv_label;

		public DataWrapper() {
		}
	}

}
