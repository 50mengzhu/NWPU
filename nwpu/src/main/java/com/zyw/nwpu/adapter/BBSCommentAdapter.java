package com.zyw.nwpu.adapter;

import java.util.List;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.MsgActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.Options;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 2014-10-15
 * 
 * @author Rocket
 * 
 */
public class BBSCommentAdapter extends BaseAdapter {

	private List<CommentData> data;
	private int commentListviewItem;
	LayoutInflater layoutInflater;
	private CommentData item;
	private Context mcontext;
	private Handler mhandler;

	public static final int WHAT_CLICK_DELETE = 0;
	public static final int WHAT_CLICK_ITEM = 1;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	public BBSCommentAdapter(Context context, List<CommentData> data, int huodongListviewItem, Handler handler) {
		this.mcontext = context;
		this.data = data;
		this.mhandler = handler;
		this.commentListviewItem = huodongListviewItem;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = layoutInflater.inflate(commentListviewItem, null);

			datawrapper.iv_headImg = (ImageView) convertView.findViewById(R.id.iv_headImg_cmt);
			datawrapper.tv_creatorName = (TextView) convertView.findViewById(R.id.tv_creatorName_cmt);
			datawrapper.iv_gender = (ImageView) convertView.findViewById(R.id.iv_creatorGender_cmt);
			datawrapper.tv_school = (TextView) convertView.findViewById(R.id.tv_schoolname_cmt);
			datawrapper.tv_time = (TextView) convertView.findViewById(R.id.tv_time_cmt);
			datawrapper.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
			datawrapper.tv_content = (TextView) convertView.findViewById(R.id.tv_content_cmt);

			convertView.setTag(datawrapper);

		} else {
			datawrapper = (DataWrapper) convertView.getTag();
		}

		item = data.get(position);

		// 用户头像 先设为默认头像，然后从网络上下载真是头像
		if (!item.isAnonymous) {
			// 未匿名
			// 头像
			datawrapper.iv_headImg.setImageResource(R.drawable.default_round_head);
			if (!TextUtils.isEmpty(item.headImgUrl)) {
				imageLoader.displayImage(item.headImgUrl, datawrapper.iv_headImg, Options.getHeadImageDisplayOptions());
			}

			// 头像点击事件 进入个人信息
			datawrapper.iv_headImg.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Message message = new Message();
					message.what = WHAT_CLICK_ITEM;
					message.arg1 = position;
					mhandler.sendMessage(message);
				}
			});

			// 用户名
			if (item.creatorIsLZ) {
				// 若评论者是楼主，则其用户名显示不同的颜色
				datawrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.lzusername));
				datawrapper.tv_creatorName.setText("楼主 " + item.nickName);
			} else {
				datawrapper.tv_creatorName.setText(item.nickName);
				datawrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.username));
			}
			// // 用户名在顶部
			// RelativeLayout.LayoutParams layoutParams = (LayoutParams)
			// datawrapper.tv_creatorName
			// .getLayoutParams();
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			// layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
			// datawrapper.tv_creatorName.setLayoutParams(layoutParams);

			// 性别
			datawrapper.iv_gender.setVisibility(View.VISIBLE);
			if (item.gender == 0) {
				datawrapper.iv_gender.setImageResource(R.drawable.ic_girl);
			} else if (item.gender == 1) {
				datawrapper.iv_gender.setImageResource(R.drawable.ic_boy);
			} else {
				datawrapper.iv_gender.setVisibility(View.INVISIBLE);
			}

			// 学院
			datawrapper.tv_school.setVisibility(View.VISIBLE);
			datawrapper.tv_school.setText(item.college);
		} else {

			// 匿名
			datawrapper.iv_headImg.setImageResource(R.drawable.default_round_head);
			datawrapper.iv_headImg.setOnClickListener(null);

			// 用户名
			if (item.creatorIsLZ) {
				// 若评论者是楼主，则其用户名显示不同的颜色
				datawrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.lzusername));
				datawrapper.tv_creatorName.setText("楼主 某同学");
			} else {
				datawrapper.tv_creatorName.setText("某同学");
				datawrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.username));
			}
			// // 匿名两个字居中
			// RelativeLayout.LayoutParams layoutParams = (LayoutParams)
			// datawrapper.tv_creatorName
			// .getLayoutParams();
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			// layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			// datawrapper.tv_creatorName.setLayoutParams(layoutParams);

			datawrapper.iv_gender.setVisibility(View.GONE);
			datawrapper.tv_school.setVisibility(View.GONE);
		}

		// 发布时间
		datawrapper.tv_time.setText(item.createTime);

		// 评论删除按钮
		if (AVUser.getCurrentUser() != null && TextUtils.equals(item.userId, AVUser.getCurrentUser().getObjectId())) {
			datawrapper.tv_delete.setVisibility(View.VISIBLE);
			datawrapper.tv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Message message = new Message();
					message.what = WHAT_CLICK_DELETE;
					message.arg1 = position;
					mhandler.sendMessage(message);
				}
			});
		} else {
			datawrapper.tv_delete.setVisibility(View.GONE);
			datawrapper.tv_delete.setOnClickListener(null);
		}

		// 回复
		if (AVUtils.isBlankString(item.targetUsername)) {
			// 不是回复内容
			datawrapper.tv_content.setText(item.content);// 评论内容
		} else {
			// 是回复内容
			SpannableString ss = new SpannableString("@" + item.targetUsername + "  " + item.content);

			ss.setSpan(new ForegroundColorSpan(mcontext.getResources().getColor(R.color.content)), 0,
					3 + item.targetUsername.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new AbsoluteSizeSpan(CommonUtil.ScreenUtils.sp2px(mcontext, 15)), 0,
					3 + item.targetUsername.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			datawrapper.tv_content.setText(ss);// 评论内容
		}

		return convertView;
	}

	private final class DataWrapper {
		public ImageView iv_headImg;
		public TextView tv_creatorName;
		public ImageView iv_gender;
		public TextView tv_school;
		public TextView tv_time;
		public TextView tv_content;
		// public TextView tv_floornum;

		public TextView tv_delete;

		public DataWrapper() {
		}
	}

}
