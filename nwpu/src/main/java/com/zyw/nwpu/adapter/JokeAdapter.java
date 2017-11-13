package com.zyw.nwpu.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.JokeEntity;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.tool.Options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JokeAdapter extends BaseAdapter {
	ArrayList<JokeEntity> jokeList;
	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	private Boolean blockImage = false;// 是否启用无图模式

	public JokeAdapter(Activity activity, ArrayList<JokeEntity> jokeList) {
		this.activity = activity;
		this.jokeList = jokeList;
		options = Options.getListOptions();
		inflater = LayoutInflater.from(activity);
		blockImage = AppSetting.isBlockImage(activity);
	}

	@Override
	public int getCount() {
		return jokeList == null ? 0 : jokeList.size();
	}

	@Override
	public JokeEntity getItem(int position) {
		if (jokeList != null && jokeList.size() != 0) {
			return jokeList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.list_joke_item, null);
			mHolder = new ViewHolder();
			mHolder.tv_title = (TextView) view.findViewById(R.id.tv_joketitle);
			mHolder.tv_content = (TextView) view
					.findViewById(R.id.tv_jokecontent);
			mHolder.iv_img = (ImageView) view.findViewById(R.id.iv_jokeimg);
			mHolder.tv_likenum = (TextView) view.findViewById(R.id.like_count);
			mHolder.tv_viewnum = (TextView) view.findViewById(R.id.view_count);
			mHolder.tv_time = (TextView) view.findViewById(R.id.publish_time);

			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		// 获取position对应的数据
		JokeEntity joke = getItem(position);

		mHolder.tv_title.setText(joke.getTitle());

		// 文字
		mHolder.tv_content
				.setText(Html.fromHtml(joke.getContent(), null, null));

		// 笑话中的图片
		String imgUrl = joke.getImgUrl();
		// 是否开启无图模式 是否有缩略图
		if (!blockImage) {
			if (!TextUtils.isEmpty(imgUrl)) {
				mHolder.iv_img.setVisibility(View.VISIBLE);
				imageLoader.displayImage(imgUrl, mHolder.iv_img, options);
			} else {
				mHolder.iv_img.setVisibility(View.GONE);
			}
		} else {
			mHolder.iv_img.setVisibility(View.GONE);
		}

		mHolder.tv_likenum.setText("点赞:" + joke.getLikeNum());
		mHolder.tv_viewnum.setText("阅读:" + joke.getViewNum());

		mHolder.tv_viewnum.setVisibility(View.GONE);
		mHolder.tv_likenum.setVisibility(View.GONE);

		// 发布时间 改成容易识别的时间
		String publishTime = joke.getPublishTime();
		Date date = new Date();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = simple.parse(publishTime);
			mHolder.tv_time.setText(DateChangeUtils.toToday(date));
		} catch (ParseException e) {
			mHolder.tv_time.setVisibility(View.GONE);
		}

		return view;
	}

	static class ViewHolder {
		TextView tv_title;
		TextView tv_content;
		TextView tv_likenum;
		TextView tv_viewnum;
		TextView tv_time;
		ImageView iv_img;

	}

}
