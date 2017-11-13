package com.zyw.nwpu.view;

import com.zyw.nwpu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 滚动标题视图
 * 
 * @author Rocket
 * 
 */
public class PagerHeaderView extends LinearLayout implements View.OnClickListener {

	private View view;
	RelativeLayout rl_1, rl_2, rl_3, rl_4;
	View line_1, line_2, line_3, line_4;

	private OnClickHeaderListener l;

	public void setOnClickHeaderListener(OnClickHeaderListener l) {
		this.l = l;
	}

	public interface OnClickHeaderListener {
		public void onClick(int index);
	}

	public PagerHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.view_scroll_header, this);

		rl_1 = (RelativeLayout) (view.findViewById(R.id.rl_1));
		rl_2 = (RelativeLayout) (view.findViewById(R.id.rl_2));
		rl_3 = (RelativeLayout) (view.findViewById(R.id.rl_3));
		rl_4 = (RelativeLayout) (view.findViewById(R.id.rl_4));

		rl_1.setOnClickListener(this);
		rl_2.setOnClickListener(this);
		rl_3.setOnClickListener(this);
		rl_4.setOnClickListener(this);

		line_1 = view.findViewById(R.id.line_1);
		line_2 = view.findViewById(R.id.line_2);
		line_3 = view.findViewById(R.id.line_3);
		line_4 = view.findViewById(R.id.line_4);

		setCurrentPage(0);
	}

	public void setCurrentPage(int i) {
		line_1.setVisibility(View.GONE);
		line_2.setVisibility(View.GONE);
		line_3.setVisibility(View.GONE);
		line_4.setVisibility(View.GONE);

		switch (i) {
		case 0:
		default:
			line_1.setVisibility(View.VISIBLE);
			break;

		case 1:
			line_2.setVisibility(View.VISIBLE);
			break;

		case 2:
			line_3.setVisibility(View.VISIBLE);
			break;

		case 3:
			line_4.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_1:
			setCurrentPage(0);
			if (l != null)
				l.onClick(0);
			break;

		case R.id.rl_2:
			setCurrentPage(1);
			if (l != null)
				l.onClick(1);
			break;

		case R.id.rl_3:
			setCurrentPage(2);
			if (l != null)
				l.onClick(2);
			break;

		case R.id.rl_4:
			setCurrentPage(3);
			if (l != null)
				l.onClick(3);
			break;

		default:
			break;
		}
	}
}
