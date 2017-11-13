package com.zyw.nwpu.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpulib.model.UserInfo;
import com.zyw.nwpu.tool.Options;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 2014-10-15
 * 
 * @author Rocket
 * 
 */
public class VisitorListAdapter extends BaseAdapter {

	private List<UserInfo> data;
	LayoutInflater layoutInflater;
	private Context mcontext;

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
	public VisitorListAdapter(Context context, List<UserInfo> data) {
		this.data = data;
		this.mcontext = context;
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
	@SuppressLint({ "NewApi", "InflateParams" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DataWrapper dataWrapper = null;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_user, null);
			dataWrapper = new DataWrapper();
			dataWrapper.iv_headImg = (ImageView) convertView.findViewById(R.id.iv_headimg_list);
			dataWrapper.tv_creatorName = (TextView) convertView.findViewById(R.id.tv_creatorName);
			dataWrapper.tv_shoolName = (TextView) convertView.findViewById(R.id.tv_schoolname);
			dataWrapper.iv_creatorGender = (ImageView) convertView.findViewById(R.id.iv_creatorGender);
			dataWrapper.tv_hometown = (TextView) convertView.findViewById(R.id.tv_hometown);
			dataWrapper.tv_lastlogin = (TextView) convertView.findViewById(R.id.tv_lastlogin);
			convertView.setTag(dataWrapper);

		} else {
			dataWrapper = (DataWrapper) convertView.getTag();
		}

		UserInfo info = data.get(position);
		dataWrapper.tv_lastlogin.setText("访问时间:" + info.lastLogin);
		dataWrapper.tv_creatorName.setText(info.nickname);// 用户名
		dataWrapper.tv_hometown.setText(info.hometown);// 家乡
		if (!TextUtils.isEmpty(info.avatar)) {
			ImageLoader.getInstance().displayImage(info.avatar, dataWrapper.iv_headImg,
					Options.getHeadImageDisplayOptions());// 下载用户头像
		} else {
			dataWrapper.iv_headImg.setImageResource(R.drawable.default_round_head);// 用户头像
																					// 默认头像
		}

		// 学院
		String text = info.college;
		if (!TextUtils.isEmpty(info.college))
			text += " ";

		// 入学年份
		if (!TextUtils.isEmpty(info.studentId) && info.studentId.length() == 10) {
			text += info.studentId.substring(0, 4) + "级";
			switch (Integer.parseInt(info.studentId.substring(4, 5))) {
			case 0:
				text += "教师";
				break;
			case 1:
				text += "博士";
				break;
			case 2:
				text += "硕士";
				break;
			case 3:
				text += "本科生";
				break;

			default:
				break;
			}
		}

		// 学院以及入学年份
		dataWrapper.tv_shoolName.setText(text);

		if (info.gender == 0) {
			dataWrapper.iv_creatorGender.setVisibility(View.VISIBLE);
			dataWrapper.iv_creatorGender.setImageResource(R.drawable.ic_girl);// 女性图标
		} else if (info.gender == 1) {
			dataWrapper.iv_creatorGender.setVisibility(View.VISIBLE);
			dataWrapper.iv_creatorGender.setImageResource(R.drawable.ic_boy);// 男性图标
		} else {
			dataWrapper.iv_creatorGender.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private final class DataWrapper {
		public TextView tv_creatorName;
		public TextView tv_shoolName;
		public TextView tv_hometown;
		public ImageView iv_creatorGender;
		public ImageView iv_headImg;
		public TextView tv_lastlogin;

		public DataWrapper() {
		}
	}

}
