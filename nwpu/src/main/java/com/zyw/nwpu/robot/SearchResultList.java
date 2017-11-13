package com.zyw.nwpu.robot;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.R;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ScreenUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 搜索结果列表
 * 
 * @author Rocket
 * 
 */
public class SearchResultList extends LinearLayout {

	private Context context;
	private View view;
	private LinearLayout ll_list;
	private ImageView iv_avatar;
	private OnItemClick l;

	public SearchResultList(Context context, OnItemClick l) {
		super(context);

		this.context = context;
		this.l = l;

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.robot_searchresult, this);
		ll_list = (LinearLayout) view.findViewById(R.id.ll_searchresult);
		iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
	}

	public void setAvatar(String avatar) {
		ImageLoader.getInstance().displayImage(avatar, iv_avatar, Options.getHeadImageDisplayOptions());
	}

	private List<Integer> findWordPositions(String sentence, String keyWord) {
		List<Integer> posList = new ArrayList<Integer>();

		while (sentence.contains(keyWord)) {
			Integer indx = sentence.indexOf(keyWord);

			if (posList.size() == 0)
				posList.add(indx);
			else
				posList.add(indx + posList.get(posList.size() - 1) + keyWord.length());
			sentence = sentence.substring(indx + keyWord.length());
		}

		return posList;
	}

	/**
	 * 设置列表
	 * 
	 * @param list
	 */
	@SuppressLint("NewApi")
	public void setList(List<String> list, String keyWord) {

		// if (ll_list != null)
		// ll_list.removeAllViews();

		if (list == null || list.size() == 0)
			return;

		LinearLayout.LayoutParams p = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < list.size(); i++) {
			String text = list.get(i);
			if (TextUtils.isEmpty(text))
				continue;

			TextView tv = new TextView(context);
			if (!TextUtils.isEmpty(keyWord)) {
				//将关键字变成红色
				List<Integer> pos = findWordPositions(text, keyWord);
				SpannableString spannableString = new SpannableString(text);
				for (int j = 0; j < pos.size(); j++) {
					int start = pos.get(j);
					int end = start + keyWord.length();
					spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)),
							start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				tv.setText(spannableString);
			} else {
				tv.setText(text);
			}

			tv.setOnClickListener(new OnItemClickListner(text));
			tv.setTextColor(context.getResources().getColor(R.color.black));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			tv.setPadding(ScreenUtils.dp2px(context, 14), ScreenUtils.dp2px(context, 10),
					ScreenUtils.dp2px(context, 14), ScreenUtils.dp2px(context, 10));
			tv.setBackground(context.getResources().getDrawable(R.drawable.selector_white));

			View divider = new View(context);
			divider.setBackgroundColor(context.getResources().getColor(R.color.divider_day));
			divider.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

			if (i != 0)
				ll_list.addView(divider);
			ll_list.addView(tv, p);
		}
	}

	public interface OnItemClick {
		public void onItemClick(String text);
	}

	private class OnItemClickListner implements OnClickListener {
		private String text;

		public OnItemClickListner(String text) {
			this.text = text;
		}

		@Override
		public void onClick(View arg0) {
			l.onItemClick(text);
		}
	}
}
