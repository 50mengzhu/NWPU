package com.zyw.nwpu.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpu.tool.Options;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 2015-1-13
 * 
 * @author Rocket
 * 
 */

public class MyMsgAdapter extends BaseAdapter {

	private List<CommentData> data;
	private int itemlayout;
	LayoutInflater layoutInflater;
	private CommentData item;
	// 加载图片所需的变量
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();

	private Context mcontext;

	public MyMsgAdapter(Context context, List<CommentData> data, int itemlayout) {
		this.mcontext = context;
		this.data = data;
		this.itemlayout = itemlayout;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
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
	public View getView(int position, View convertView, ViewGroup parent) {

		DataWrapper datawrapper = new DataWrapper();

		if (convertView == null) {
			convertView = layoutInflater.inflate(itemlayout, null);

			datawrapper.rl_item = (RelativeLayout) convertView
					.findViewById(R.id.rl_list_item_mymsg);

			datawrapper.iv_headImg = (ImageView) convertView
					.findViewById(R.id.iv_headimg_mynotify);
			datawrapper.tv_creatorName = (TextView) convertView
					.findViewById(R.id.tv_creatorName_mynotify);
			datawrapper.iv_gender = (ImageView) convertView
					.findViewById(R.id.iv_creatorGender_mynotify);
			datawrapper.tv_school = (TextView) convertView
					.findViewById(R.id.tv_schoolname_mynotify);
			datawrapper.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time_mynotify);
			datawrapper.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content_mynotify);

			datawrapper.tv_topiccontent = (TextView) convertView
					.findViewById(R.id.tv_topiccontent);

			datawrapper.iv_topicpic = (ImageView) convertView
					.findViewById(R.id.iv_topicpic);

			convertView.setTag(datawrapper);

		} else {
			datawrapper = (DataWrapper) convertView.getTag();
		}

		item = data.get(position);

		datawrapper.tv_topiccontent.setVisibility(View.GONE);
		datawrapper.iv_topicpic.setVisibility(View.GONE);

		// 用户头像 先设为默认头像，然后从网络上下载真是头像
		datawrapper.iv_headImg.setImageResource(R.drawable.ic_defaut_headimg);
		if (!TextUtils.isEmpty(item.headImgUrl)) {
			imageLoader.displayImage(item.headImgUrl, datawrapper.iv_headImg,
					Options.getHeadImageDisplayOptions());
		}
		// 评论者的用户名
		datawrapper.tv_creatorName.setText(item.nickName);
		datawrapper.tv_creatorName.setTextColor(mcontext.getResources()
				.getColor(R.color.username));

		if (item.gender == 0) {
			datawrapper.iv_gender.setImageResource(R.drawable.ic_girl);
		} else if (item.gender == 1) {
			datawrapper.iv_gender.setImageResource(R.drawable.ic_boy);
		} else {
			datawrapper.iv_gender.setVisibility(View.INVISIBLE);
		}

		datawrapper.tv_school.setText(item.college);
		datawrapper.tv_time.setText(item.createTime);
		datawrapper.tv_content.setText(item.content);// 评论内容

		// 帖子内容或图片
		if (TextUtils.isEmpty(item.targetTopicImageURL)) {
			datawrapper.tv_topiccontent.setVisibility(View.VISIBLE);
			datawrapper.tv_topiccontent.setText(item.targetTopicContent);// 帖子内容
		} else {
			datawrapper.iv_topicpic.setVisibility(View.VISIBLE);
			imageLoader.displayImage(item.targetTopicImageURL,
					datawrapper.iv_topicpic, options);
		}

		// 已读消息 背景变灰
		if (item.hasRead) {
			datawrapper.rl_item
					.setBackgroundResource(R.color.activity_bg_color);
		} else {
			datawrapper.rl_item
					.setBackgroundResource(R.drawable.left_drawer_item_bg);
		}

		// 添加动画
		// // convertView.setAnimation(animation);
		// convertView.startAnimation(AnimationUtils.loadAnimation(mcontext,
		// android.R.anim.fade_in));

		return convertView;
	}

	private final class DataWrapper {
		public ImageView iv_headImg;
		public TextView tv_creatorName;
		public ImageView iv_gender;
		public TextView tv_school;
		public TextView tv_time;
		public TextView tv_content;
		public RelativeLayout rl_item;
		public TextView tv_topiccontent;

		public ImageView iv_topicpic;

		public DataWrapper() {
		}
	}

}
