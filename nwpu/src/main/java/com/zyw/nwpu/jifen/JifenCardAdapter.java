package com.zyw.nwpu.jifen;

import android.annotation.SuppressLint;
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
public class JifenCardAdapter extends BaseAdapter {
	private List<JifenCard> mCards;
	private Context mContext;

	public JifenCardAdapter(Context mContext, List<JifenCard> mCards) {
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

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = new ViewHolder();
		convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_jifen, null);
		mHolder.Card_Title = (TextView) convertView.findViewById(R.id.Card_Title);
		mHolder.Card_Title.setText(mCards.get(position).getOperationName());

		mHolder.Card_Pic = (TextView) convertView.findViewById(R.id.Card_Pic);
		mHolder.Card_Pic.setText(mCards.get(position).getDate());

		mHolder.Card_jifen = (TextView) convertView.findViewById(R.id.Card_jifen);

		String jifen = String.valueOf(mCards.get(position).getScore());
		if (mCards.get(position).getScore() >= 0) {
			jifen = "+" + jifen;
		} else {
			jifen = "-" + jifen;
		}
		mHolder.Card_jifen.setText(jifen);
		return convertView;
	}

	private class ViewHolder {
		TextView Card_Title;
		TextView Card_Pic;
		TextView Card_jifen;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
