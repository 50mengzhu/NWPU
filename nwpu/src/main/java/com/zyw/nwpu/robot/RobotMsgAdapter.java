package com.zyw.nwpu.robot;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.MsgActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.robot.RobotMsg.MsgType;
import com.zyw.nwpu.robot.SearchResultList.OnItemClick;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.Options;
import android.annotation.SuppressLint;
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
 * @author Rocket
 */
public class RobotMsgAdapter extends BaseAdapter {

	private List<RobotMsg> data;
	LayoutInflater layoutInflater;
	private Context context;
	private OnItemClick itemClickListner;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions headImageOptions = Options.getHeadImageDisplayOptions();
	private DisplayImageOptions imageOptions = Options.getListOptions();

	public RobotMsgAdapter(Context context, List<RobotMsg> data, OnItemClick itemClickListner) {
		this.data = data;
		this.itemClickListner = itemClickListner;
		this.context = context;
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

	// 每个convert view都会调用此方法，获得当前所需要的view样式
	@Override
	public int getItemViewType(int position) {
		if (data.get(position).getType() == MsgType.SEND)
			return 0;
		if (data.get(position).getType() == MsgType.RECEIVE)
			return 1;
		if (data.get(position).getType() == MsgType.LIST)
			return 2;
		if (data.get(position).getType() == MsgType.RECEIVE_IMAGE)
			return 3;

		return 0;// 默认值
	}

	/**
	 * 返回所有的layout的数量
	 * 
	 */
	@Override
	public int getViewTypeCount() {
		return RobotMsg.TYPENUM;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RobotMsg msg = data.get(position);
		int type = getItemViewType(position);

		ViewHolder_Send viewHolder_Send = null;
		ViewHolder_Receive viewHolder_Receive = null;

		if (convertView == null || convertView.getTag() == null) {
			switch (type) {
			case 0:
			default:
				// 发送
				convertView = layoutInflater.inflate(R.layout.robot_row_sent, null);
				viewHolder_Send = new ViewHolder_Send();
				viewHolder_Send.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
				viewHolder_Send.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				convertView.setTag(viewHolder_Send);
				break;

			case 1:
			case 3:
				// 接收 文字或图片
				convertView = layoutInflater.inflate(R.layout.robot_row_received, null);
				viewHolder_Receive = new ViewHolder_Receive();
				viewHolder_Receive.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
				viewHolder_Receive.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				viewHolder_Receive.iv_img = (ImageView) convertView.findViewById(R.id.iv_image);
				convertView.setTag(viewHolder_Receive);
				break;
			case 2:
				// 列表
				SearchResultList searchResultList = new SearchResultList(context, itemClickListner);
				searchResultList.setList(msg.getList(), msg.getKeyWord());
				searchResultList.setAvatar(msg.getAvatarUrl());
				return searchResultList;
			}
		}

		// 赋值
		switch (type) {
		case 0:
		default:
			// 发送
			viewHolder_Send = (ViewHolder_Send) convertView.getTag();
			imageLoader.displayImage(msg.getAvatarUrl(), viewHolder_Send.iv_avatar, headImageOptions);
			viewHolder_Send.tv_content.setText(msg.getContent());
			break;

		case 1:
			// 接收文字
			viewHolder_Receive = (ViewHolder_Receive) convertView.getTag();
			imageLoader.displayImage(msg.getAvatarUrl(), viewHolder_Receive.iv_avatar, headImageOptions);
			viewHolder_Receive.tv_content.setText(msg.getContent());
			viewHolder_Receive.tv_content.setVisibility(View.VISIBLE);
			viewHolder_Receive.iv_img.setVisibility(View.GONE);
			break;

		case 3:
			// 接收图片
			viewHolder_Receive = (ViewHolder_Receive) convertView.getTag();
			imageLoader.displayImage(msg.getAvatarUrl(), viewHolder_Receive.iv_avatar, headImageOptions);
			imageLoader.displayImage(msg.getContent(), viewHolder_Receive.iv_img, imageOptions);
			viewHolder_Receive.tv_content.setVisibility(View.GONE);
			viewHolder_Receive.iv_img.setVisibility(View.VISIBLE);
			break;

		case 2:
			// 列表
			SearchResultList searchResultList = new SearchResultList(context, itemClickListner);
			searchResultList.setList(msg.getList(), msg.getKeyWord());
			searchResultList.setAvatar(msg.getAvatarUrl());
			return searchResultList;
		}

		// // if (position % 3 == 0) {
		// List<String> list = new ArrayList<String>();
		// list.add("内容内容内容内容内容1");
		// list.add("内容内容内容内容内容2");
		// list.add("内容内容内容内容内容3");
		// return searchResultList;
		// }

		return convertView;
	}

	private final class ViewHolder_Send {
		public ImageView iv_avatar;
		public TextView tv_content;

		public ViewHolder_Send() {
		}
	}

	private final class ViewHolder_Receive {
		public ImageView iv_avatar;
		public ImageView iv_img;
		public TextView tv_content;

		public ViewHolder_Receive() {
		}
	}

}
