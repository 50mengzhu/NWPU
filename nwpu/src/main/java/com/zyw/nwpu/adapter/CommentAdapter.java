package com.zyw.nwpu.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.CommentEntity;
import com.zyw.nwpulib.model.UserInfo;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppHelper;
import com.zyw.nwpu.service.AvatarAndNicknameService;
import com.zyw.nwpu.tool.Options;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private Handler mhandler;
	ArrayList<CommentEntity> commentList;
	Context context;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public CommentAdapter(Context applicationContext,
			ArrayList<CommentEntity> commentList, Handler handler) {
		this.context = applicationContext;
		this.commentList = commentList;
		this.mhandler = handler;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return commentList == null ? 0 : commentList.size();
	}

	@Override
	public CommentEntity getItem(int position) {
		if (commentList != null && commentList.size() != 0) {
			return commentList.get(position);
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
			view = inflater.inflate(R.layout.comment_item, null);
			mHolder = new ViewHolder();
			mHolder.head_image = (ImageView) view
					.findViewById(R.id.iv_headimg_cmt);
			mHolder.tv_username = (TextView) view
					.findViewById(R.id.tv_creatorname_cmt);
			mHolder.rl_like = (RelativeLayout) view
					.findViewById(R.id.rl_like_cmt);
			mHolder.tv_time = (TextView) view.findViewById(R.id.tv_time_cmt);
			mHolder.tv_likenum = (TextView) view
					.findViewById(R.id.tv_likenum_cmt);
			mHolder.like_image = (ImageView) view
					.findViewById(R.id.iv_like_cmt);
			mHolder.tv_content = (TextView) view
					.findViewById(R.id.tv_content_cmt);

			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		// 获取position对应的数据
		CommentEntity comment = getItem(position);
		mHolder.tv_time.setText(comment.getPublishTime());
		mHolder.tv_content.setText(comment.getContent());
		mHolder.tv_likenum.setText(String.valueOf(comment.getLikeNum()));
		// 是否已经赞过
		if (comment.isLiked()) {
			mHolder.like_image
					.setImageResource(R.drawable.ic_action_like_pressed);
			mHolder.tv_likenum.setTextColor(context.getResources().getColor(
					R.color.likenum_pre));
		} else {
			mHolder.like_image
					.setImageResource(R.drawable.ic_action_like_normal);
			mHolder.tv_likenum.setTextColor(context.getResources().getColor(
					R.color.likenum_nor));
		}

		UserInfo info = new UserInfo();

		if (AvatarAndNicknameService.userInfoMap != null
				&& AvatarAndNicknameService.userInfoMap.containsKey(comment
						.getUsername())) {
			info = AvatarAndNicknameService.userInfoMap.get(comment
					.getUsername());
		}

		// 显示昵称
		if (!TextUtils.isEmpty(info.nickname)) {
			mHolder.tv_username.setText(info.nickname);
		} else {
			mHolder.tv_username.setText(comment.getUsername());
		}

		// 显示头像
		if (!TextUtils.isEmpty(info.avatar)) {
			imageLoader.displayImage(info.avatar, mHolder.head_image, Options.getHeadImageDisplayOptions());
		} else {
			mHolder.head_image.setImageResource(R.drawable.ic_defaut_headimg);
		}

		// 点赞事件
		mHolder.rl_like.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message message = new Message();
				message.arg1 = position;
				mhandler.sendMessage(message);
			}
		});

		// 暂时先隐藏掉
		mHolder.rl_like.setVisibility(View.GONE);

		return view;
	}

	static class ViewHolder {
		// 头像
		ImageView head_image;
		// 用户名
		TextView tv_username;
		// 发布时间
		TextView tv_time;
		// 点赞布局
		RelativeLayout rl_like;
		// 点赞数量
		TextView tv_likenum;
		// 点赞
		ImageView like_image;
		// 评论内容
		TextView tv_content;
	}

}
