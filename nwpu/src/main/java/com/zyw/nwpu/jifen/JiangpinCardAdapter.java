package com.zyw.nwpu.jifen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

import com.zyw.nwpu.R;

import java.util.List;

/**
 * Created by 13202 on 2016/12/6.
 */

public class JiangpinCardAdapter extends BaseAdapter {
	private List<JiangpinCard> mCards;
	private Context mContext;

	public JiangpinCardAdapter(Context mContext, List<JiangpinCard> mCards) {
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
		// 奖品Adapter的相关设置
		JiangpinCardAdapter.ViewHolder mHolder = new JiangpinCardAdapter.ViewHolder();
		// adapter_myjifen 表示奖品每个控件
		convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_jiangpin, null);
		mHolder.Card_Title = (TextView) convertView.findViewById(R.id.myjifen_jiangpinxushu);
		mHolder.Card_Title.setText(mCards.get(position).getDescription());
		mHolder.Card_Jifen = (TextView) convertView.findViewById(R.id.myjifen_xuyaojifen);
		mHolder.Card_Jifen.setText(mCards.get(position).getJifen());
		mHolder.Card_Img = (ImageView) convertView.findViewById(R.id.myjifen_jiangpinimg);
		// 通过xutils设置图片
		x.image().bind(mHolder.Card_Img, mCards.get(position).getImgUrl());

		// 记住啊，这里是setImageResource()方法，不是setBackgroundResource(),否则图像会变形啊

		return convertView;
	}

	// Adapter的变量内容
	private static class ViewHolder {
		ImageView Card_Img;
		TextView Card_Title;
		TextView Card_Jifen;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}
}
