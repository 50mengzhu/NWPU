package com.zyw.nwpu.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpu.tool.Options;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 2014-10-28
 * 
 * @author Rocket
 * 
 */

public class MyStatusAdapter extends BaseAdapter {

	private List<StatusData> data;
	private int myStatusItem;
	LayoutInflater layoutInflater;
	private Context mcontext;

	public void setStatusData(List<StatusData> data) {
		if (this.data != null)
			this.data.clear();
		this.data = null;
		this.data = new ArrayList<StatusData>();

		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				this.data.add(data.get(i));
			}
		}
	}

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();

	public MyStatusAdapter(Context context, List<StatusData> data, int myStatusItem) {
		this.data = data;
		this.myStatusItem = myStatusItem;
		this.mcontext = context;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private boolean isBlock = true;

	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}

	@Override
	public int getCount() {
		if (isBlock)
			return 0;
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
			convertView = layoutInflater.inflate(myStatusItem, null);

			dataWrapper = new DataWrapper();

			// 头部
			// dataWrapper.ib_del = (ImageButton) convertView
			// .findViewById(R.id.ib_del_my);
			dataWrapper.tv_createTime = (TextView) convertView.findViewById(R.id.tv_createtime_my);
			// 中部 内容
			dataWrapper.tv_txt = (TextView) convertView.findViewById(R.id.tv_txt_my);
			dataWrapper.iv_img = (ImageView) convertView.findViewById(R.id.iv_img_my);

			// 底部
			dataWrapper.tv_like = (TextView) convertView.findViewById(R.id.tv_like);
			dataWrapper.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);

			dataWrapper.iv_timeline_point = (ImageView) convertView.findViewById(R.id.iv_timeline_point);

			convertView.setTag(dataWrapper);

		} else {
			dataWrapper = (DataWrapper) convertView.getTag();
		}

		// 头部
		// 中部
		dataWrapper.tv_txt.setText(data.get(position).content_txt);
		showImg(data.get(position), dataWrapper.iv_img);

		// 时间轴节点
		if (position % 2 == 0) {
			dataWrapper.iv_timeline_point.setImageResource(R.drawable.ic_timeline_point_yellow);
		} else {
			dataWrapper.iv_timeline_point.setImageResource(R.drawable.ic_timeline_point_blue);
		}

		dataWrapper.tv_createTime.setText(DateChangeUtils.toToday(data.get(position).date));

		// 底部
		dataWrapper.tv_like.setText(String.valueOf(data.get(position).likeNum));
		dataWrapper.tv_comment.setText(String.valueOf(data.get(position).commentNum));

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

		imageLoader.displayImage(url, iv, options);
	}

	private final class DataWrapper {
		public TextView tv_txt;
		public TextView tv_createTime;
		public ImageView iv_img;
		public ImageView iv_timeline_point;

		public TextView tv_like;
		public TextView tv_comment;

		public DataWrapper() {
		}
	}

}
