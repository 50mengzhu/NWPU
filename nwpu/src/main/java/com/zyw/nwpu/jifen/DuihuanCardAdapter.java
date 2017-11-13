package com.zyw.nwpu.jifen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.zyw.nwpu.R;

/**
 * Created by 13202 on 2016/12/2.
 */

/**
 * 控制整个GridView的显示情况
 */
public class DuihuanCardAdapter extends BaseAdapter {
	private List<DuihuanCard> mCards;
	private Context mContext;

	public DuihuanCardAdapter(Context mContext, List<DuihuanCard> mCards) {
		this.mContext = mContext;
		this.mCards = mCards;
	}

	@Override
	public int getCount() {
		return mCards.size();
	}

	@Override
	public Object getItem(int position) {
		return mCards.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = new ViewHolder();
		convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_duihuanjilu, null);
		mHolder.Card_Title = (TextView) convertView.findViewById(R.id.Duihuan_Card_Title);
		mHolder.Card_Title.setText(mCards.get(position).getDescription());
		mHolder.Card_Pic = (TextView) convertView.findViewById(R.id.Duihuan_Card_Pic);
		// 记住啊，这里是setImageResource()方法，不是setBackgroundResource(),否则图像会变形啊
		mHolder.Card_Pic.setText(mCards.get(position).getText());
		mHolder.Card_jifen = (TextView) convertView.findViewById(R.id.Duihuan_Card_jifen);
		mHolder.Card_jifen.setText(mCards.get(position).getJifen());
		return convertView;
	}

	private static class ViewHolder {
		TextView Card_Title;
		TextView Card_Pic;
		TextView Card_jifen;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}
}
