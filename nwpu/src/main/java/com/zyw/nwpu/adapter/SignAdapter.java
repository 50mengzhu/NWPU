package com.zyw.nwpu.adapter;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpu.service.SignService.SignBean;
import com.zyw.nwpu.tool.Options;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SignAdapter extends BaseAdapter {
	ArrayList<SignBean> signList;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public SignAdapter(Context applicationContext, ArrayList<SignBean> signList) {
		this.signList = signList;
		inflater = LayoutInflater.from(applicationContext);
	}

	@Override
	public int getCount() {
		return signList == null ? 0 : signList.size();
	}

	@Override
	public SignBean getItem(int position) {
		if (signList != null && signList.size() != 0) {
			return signList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_sign, null);
			mHolder = new ViewHolder();
			mHolder.iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
			mHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			mHolder.tv_rank = (TextView) view.findViewById(R.id.tv_rank);
			mHolder.tv_days = (TextView) view.findViewById(R.id.tv_days);
			mHolder.iv_first = (ImageView) view.findViewById(R.id.iv_first);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		SignBean sign = getItem(position);
		mHolder.tv_name.setText(sign.getNickname());
		mHolder.tv_rank.setText("第" + String.valueOf(position + 1) + "名");
		mHolder.tv_days.setText(String.valueOf(sign.getSignTimes()));
		imageLoader.displayImage(sign.getAvatar(), mHolder.iv_avatar, Options.getHeadImageDisplayOptions());
		if (position == 0) {
			mHolder.iv_first.setVisibility(View.VISIBLE);
		} else {
			mHolder.iv_first.setVisibility(View.GONE);
		}
		if (position < 3) {
			mHolder.tv_days.setTextColor(Color.parseColor("#FFCC00"));
		} else {
			mHolder.tv_days.setTextColor(Color.parseColor("#39B038"));
		}
		return view;
	}

	static class ViewHolder {
		ImageView iv_avatar;
		TextView tv_name;
		TextView tv_rank;
		TextView tv_days;
		ImageView iv_first;
	}
}
