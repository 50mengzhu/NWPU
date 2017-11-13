package com.zyw.nwpu.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.NewsEntity;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpu.service.AppSetting;
import com.zyw.nwpu.tool.Options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	ArrayList<NewsEntity> newsList;
	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	private Boolean blockImage = false;// 是否启用无图模式

	@SuppressWarnings("unused")
	private int textSize = AppSetting.TS_MEDIUM;// 中号

	public NewsAdapter(Activity activity, ArrayList<NewsEntity> newsList) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		blockImage = AppSetting.isBlockImage(activity);
		textSize = AppSetting.getTextSize(activity);
	}

	@Override
	public int getCount() {
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public NewsEntity getItem(int position) {
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item, null);
			mHolder = new ViewHolder();
			mHolder.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
			mHolder.item_title = (TextView) view.findViewById(R.id.item_title);
			mHolder.comment_count = (TextView) view.findViewById(R.id.comment_count);
			mHolder.like_count = (TextView) view.findViewById(R.id.like_count);
			mHolder.view_count = (TextView) view.findViewById(R.id.view_count);
			mHolder.publish_time = (TextView) view.findViewById(R.id.publish_time);
			// mHolder.item_abstract = (TextView) view
			// .findViewById(R.id.item_abstract);
			mHolder.right_image = (ImageView) view.findViewById(R.id.right_image);
			mHolder.iv_bigimg = (ImageView) view.findViewById(R.id.iv_bigimg);
			mHolder.tv_copyFrom = (TextView) view.findViewById(R.id.tv_copyfrom);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		// 获取position对应的数据
		NewsEntity news = getItem(position);

		// 标题
		mHolder.item_title.setText(news.getTitle());

		// 评论数
		if (news.getCommentNum() != 0) {
			mHolder.comment_count.setText("评论:" + news.getCommentNum() + "  ");
			mHolder.comment_count.setVisibility(View.VISIBLE);
		} else {
			mHolder.comment_count.setVisibility(View.GONE);
		}

		// 点赞量
		if (news.getLikeNum() != 0) {
			mHolder.like_count.setText("赞:" + news.getLikeNum() + "  ");
			mHolder.like_count.setVisibility(View.VISIBLE);
		} else {
			mHolder.like_count.setVisibility(View.GONE);
		}

		// 阅读量
		if (news.getViewnum().trim().compareTo("0") != 0) {
			mHolder.view_count.setText("阅读:" + news.getViewnum() + "  ");
			mHolder.view_count.setVisibility(View.VISIBLE);
		} else {
			mHolder.view_count.setVisibility(View.GONE);
		}

		// 来源
		if (!TextUtils.isEmpty(news.getCopyFrom().trim()) && news.getCopyFrom().trim().compareTo("null") != 0) {
			mHolder.tv_copyFrom.setVisibility(View.VISIBLE);
			mHolder.tv_copyFrom.setText(news.getCopyFrom() + "  ");
		} else {
			mHolder.tv_copyFrom.setVisibility(View.GONE);
		}

		mHolder.tv_copyFrom.setVisibility(View.GONE);// 不显示来源 2016年5月9日

		// 发布时间
		String publishTime = news.getPublishTime();
		Date date = new Date();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = simple.parse(publishTime);
			mHolder.publish_time.setText(DateChangeUtils.toToday(date));
		} catch (ParseException e) {
			mHolder.publish_time.setVisibility(View.GONE);
		}

		// 是否开启无图模式 是否有缩略图
		if (!blockImage) {
			if (!TextUtils.isEmpty(news.getPicUrl())) {

				// 是否显示为大图
				boolean isShowBigImage = news.isShowBigImage();

				if (isShowBigImage) {
					mHolder.right_image.setVisibility(View.GONE);
					mHolder.iv_bigimg.setVisibility(View.VISIBLE);
					mHolder.iv_bigimg.setBackgroundColor(this.activity.getResources().getColor(R.color.gray));
					imageLoader.displayImage(news.getPicUrl(), mHolder.iv_bigimg, options);
				} else {
					mHolder.iv_bigimg.setVisibility(View.GONE);
					mHolder.right_image.setVisibility(View.VISIBLE);
					mHolder.right_image.setBackgroundColor(this.activity.getResources().getColor(R.color.gray));
					imageLoader.displayImage(news.getPicUrl(), mHolder.right_image, options);
				}
			} else {
				mHolder.right_image.setVisibility(View.GONE);
				mHolder.iv_bigimg.setVisibility(View.GONE);
			}
		} else {
			mHolder.iv_bigimg.setVisibility(View.GONE);
			mHolder.right_image.setVisibility(View.GONE);
		}

		return view;
	}

	static class ViewHolder {
		LinearLayout item_layout;
		// title
		TextView item_title;
		// 评论数量
		TextView comment_count;
		// 点赞数量
		TextView like_count;
		// 阅读数量
		TextView view_count;
		// 发布时间
		TextView publish_time;
		// 新闻摘要
		// TextView item_abstract;
		// 右边图片
		ImageView right_image;
		// 来源
		TextView tv_copyFrom;
		// 中间大图图片
		ImageView iv_bigimg;

	}

}
