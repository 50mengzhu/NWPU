package com.zyw.nwpu.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpu.tool.Options;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 2014-10-15
 * 
 * @author Rocket
 * 
 */
public class StatusAdapter extends BaseAdapter {

	private final int MAX_TEXT_NUM = 40;

	private List<StatusData> data;
	private int huodongListviewItem;
	LayoutInflater layoutInflater;
	private Handler mhandler;
	private Context mcontext;
	private TagClickListener tagClickListener = null;

	// 加载图片所需的变量
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();
	private DisplayImageOptions headImageOptions = Options.getHeadImageDisplayOptions();

	/**
	 * 自定义构造器
	 * 
	 * @param context
	 *            上下文
	 * @param data
	 *            listview中所要呈现的数据
	 * @param huodongListviewItem
	 *            listview中条目的布局文件
	 * @param cache
	 *            图片缓存路径
	 * @param handler
	 */
	public StatusAdapter(Context context, List<StatusData> data, int huodongListviewItem, Handler handler,
			TagClickListener l) {
		this.data = data;
		this.huodongListviewItem = huodongListviewItem;
		this.mhandler = handler;
		this.mcontext = context;
		this.tagClickListener = l;
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

	/**
	 * 当ListView每次显示一个条目，则会调用该方法
	 */
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DataWrapper dataWrapper = null;

		if (convertView == null) {
			convertView = layoutInflater.inflate(huodongListviewItem, null);

			dataWrapper = new DataWrapper();
			// 头部
			dataWrapper.rl_headbar = (RelativeLayout) convertView.findViewById(R.id.rl_headbar);
			dataWrapper.tv_creatorName = (TextView) convertView.findViewById(R.id.tv_creatorName);
			dataWrapper.iv_headImg = (ImageView) convertView.findViewById(R.id.iv_headimg_list);
			dataWrapper.iv_creatorGender = (ImageView) convertView.findViewById(R.id.iv_creatorGender);
			dataWrapper.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			dataWrapper.iv_down = (ImageView) convertView.findViewById(R.id.iv_downbtn);
			dataWrapper.iv_stick = (ImageView) convertView.findViewById(R.id.iv_stick);
			dataWrapper.rl_name = (RelativeLayout) convertView.findViewById(R.id.rl_name);

			// 中部
			dataWrapper.tv_txt = (TextView) convertView.findViewById(R.id.tv_txt);
			dataWrapper.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);

			// 底部
			dataWrapper.ll_position = (LinearLayout) convertView.findViewById(R.id.ll_position);
			dataWrapper.tv_position = (TextView) convertView.findViewById(R.id.tv_position);
			dataWrapper.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
			dataWrapper.likeWidget.tv_like = (TextView) convertView.findViewById(R.id.tv_like);
			dataWrapper.likeWidget.ll_like = (LinearLayout) convertView.findViewById(R.id.ll_like);
			dataWrapper.likeWidget.iv_like = (ImageView) convertView.findViewById(R.id.iv_like);

			convertView.setTag(dataWrapper);

		} else {
			dataWrapper = (DataWrapper) convertView.getTag();
		}

		StatusData stt = data.get(position);

		if (stt.isAnonymous) {
			// 若为匿名消息
			dataWrapper.rl_headbar.setVisibility(View.VISIBLE);
			dataWrapper.tv_creatorName.setText("某同学");// 用户名
			dataWrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.gray));
			dataWrapper.iv_headImg.setImageResource(R.drawable.default_round_head);// 默认头像
			dataWrapper.iv_creatorGender.setVisibility(View.INVISIBLE);
		} else {
			// 若为署名消息
			dataWrapper.rl_headbar.setVisibility(View.VISIBLE);
			dataWrapper.tv_creatorName.setText(stt.nickName);// 用户名

			if (stt.isAdmin) {
				// 管理员昵称颜色
				dataWrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.red));
			} else {
				// 其他用户昵称颜色
				dataWrapper.tv_creatorName.setTextColor(mcontext.getResources().getColor(R.color.black));
			}

			if (!TextUtils.isEmpty(stt.headImgUrl)) {
				dataWrapper.iv_headImg.setImageResource(R.drawable.ic_defaut_headimg);// 用户头像
																						// 默认头像
				imageLoader.displayImage(stt.headImgUrl, dataWrapper.iv_headImg, headImageOptions);// 下载用户头像
			} else {
				dataWrapper.iv_headImg.setImageResource(R.drawable.ic_defaut_headimg);// 用户头像
																						// 默认头像
			}

			if (stt.gender == 0) {
				dataWrapper.iv_creatorGender.setVisibility(View.VISIBLE);
				dataWrapper.iv_creatorGender.setImageResource(R.drawable.ic_girl);// 女性图标
			} else if (stt.gender == 1) {
				dataWrapper.iv_creatorGender.setVisibility(View.VISIBLE);
				dataWrapper.iv_creatorGender.setImageResource(R.drawable.ic_boy);// 男性图标
			} else {
				dataWrapper.iv_creatorGender.setVisibility(View.INVISIBLE);
			}
		}
		// 计算时间
		dataWrapper.tv_time.setText(DateChangeUtils.toToday(stt.date));

		// 置顶
		if (stt.isSticky) {
			dataWrapper.iv_stick.setVisibility(View.VISIBLE);
		} else {
			dataWrapper.iv_stick.setVisibility(View.GONE);
		}

		// 中部
		if (!TextUtils.isEmpty(stt.content_txt) && stt.content_txt.length() > MAX_TEXT_NUM) {
			StringBuffer sb = new StringBuffer(stt.content_txt.subSequence(0, MAX_TEXT_NUM));
			sb.append("\n... ...");
			stt.content_txt = sb.toString();
		}
		SpannableString ss = null;
		if (!TextUtils.isEmpty(stt.tag)) {
			ss = new SpannableString("#" + stt.tag + "#" + stt.content_txt);
			int start = 0;
			int end = 2 + stt.tag.length();
			// 显示标签的颜色
			ss.setSpan(new ForegroundColorSpan(mcontext.getResources().getColor(R.color.bg_title)), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			// 标签点击事件
			ss.setSpan(new Clickable(stt.tag, tagClickListener), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			ss = new SpannableString(stt.content_txt);
		}

		dataWrapper.tv_txt.setHighlightColor(mcontext.getResources().getColor(R.color.transparent));
		dataWrapper.tv_txt.setMovementMethod(LinkMovementMethod.getInstance());
		dataWrapper.tv_txt.setText(ss);// 文字内容

		showImg(stt, dataWrapper.iv_img);

		// 底部
		// 位置
		if (TextUtils.isEmpty(stt.position) || TextUtils.equals(stt.position, "火星")
				|| TextUtils.equals(stt.position, "未知")) {
			dataWrapper.ll_position.setVisibility(View.INVISIBLE);
		} else {
			dataWrapper.ll_position.setVisibility(View.VISIBLE);
			dataWrapper.tv_position.setText(stt.position);
		}

		dataWrapper.likeWidget.tv_like.setText(String.valueOf(stt.likeNum));
//		if (stt.AlreadyLiked) {
//			dataWrapper.likeWidget.tv_like.setTextColor(Color.parseColor("#FFCC00"));
//			dataWrapper.likeWidget.iv_like.setImageResource(R.drawable.ic_action_like_pressed);
//		} else {
//			dataWrapper.likeWidget.tv_like.setTextColor(Color.BLACK);
//			dataWrapper.likeWidget.iv_like.setImageResource(R.drawable.ic_action_like_normal);
//		}
		dataWrapper.tv_comment.setText(String.valueOf(stt.commentNum));// 评论数

		// 设置按钮点击事件
		final LikeWidget likeWidget = dataWrapper.likeWidget;
		final RelativeLayout rl_temp = dataWrapper.rl_headbar;

		// 点击头部 判断是否匿名
		if (!stt.isAnonymous)
			dataWrapper.rl_headbar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Message message = new Message();
					message.what = rl_temp.getId();
					message.arg1 = position;
					mhandler.sendMessage(message);
				}
			});
		else
			dataWrapper.rl_headbar.setOnClickListener(null);

		// 点赞按钮
		dataWrapper.likeWidget.ll_like.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message message = new Message();
				message.what = likeWidget.tv_like.getId();
				message.arg1 = position;
				message.obj = likeWidget;// 将点赞控件作为参数传到主线程
				mhandler.sendMessage(message);
			}
		});

		// 位置信息
		final LinearLayout ll_position_final = dataWrapper.ll_position;
		dataWrapper.ll_position.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message message = new Message();
				message.what = ll_position_final.getId();
				message.arg1 = position;
				mhandler.sendMessage(message);
			}
		});

		// 向下箭头按钮
		final ImageView iv_down = dataWrapper.iv_down;
		dataWrapper.iv_down.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message message = new Message();
				message.what = iv_down.getId();
				message.arg1 = position;
				mhandler.sendMessage(message);
			}
		});

		return convertView;
	}

	private void showImg(StatusData stt, ImageView iv) {
		String url;
		url = stt.imgUrl;

		// 这里需要将获取的图片显示在控件上
		if (url == "") {
			iv.setVisibility(View.GONE);
			return;
		}

		iv.setVisibility(View.VISIBLE);
		iv.setImageResource(R.drawable.ic_chat_def_pic);

		// iv.setMinimumWidth(Integer.parseInt(stt.getImg().getMetaData("").toString()));
		// iv.setMinimumHeight(Integer.parseInt(stt.getImg().getMetaData("").toString()));

		// x.image().bind(iv, url, DownLoadImageOption.getOtherImageOptions());
		imageLoader.displayImage(url, iv, options);
	}

	private final class DataWrapper {
		public TextView tv_creatorName;
		public TextView tv_time;
		public TextView tv_txt;
		public LikeWidget likeWidget;
		public TextView tv_comment;
		public RelativeLayout rl_headbar;
		public ImageView iv_img;
		public ImageView iv_creatorGender;
		public ImageView iv_headImg;
		public ImageView iv_down;

		public RelativeLayout rl_name;

		public ImageView iv_stick;// 置顶标签

		public LinearLayout ll_position;// 发布位置
		public TextView tv_position;

		public DataWrapper() {
			likeWidget = new LikeWidget();
		}
	}

	public final class LikeWidget {
		public TextView tv_like;
		public LinearLayout ll_like;
		public ImageView iv_like;

		public LikeWidget() {
		}
	}

	public class Clickable extends ClickableSpan {
		private String tag = "";
		private TagClickListener l;

		public Clickable(String t, TagClickListener l) {
			tag = t;
			this.l = l;
		}

		@Override
		public void onClick(View arg0) {
			l.onTagClicked(tag);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			// TODO Auto-generated method stub
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}

	}

	public interface TagClickListener {
		public void onTagClicked(String tag);
	}

}
