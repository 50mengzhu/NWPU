package com.zyw.nwpu.base;

import com.zyw.nwpu.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * 
 * @author Rocket
 * 
 */
public class TitleBar extends FrameLayout implements View.OnClickListener {
	private View headView;
	private TextView tv_title;
	private TextView tv_text;
	private ImageView iv_back;
	private ImageView iv_more;
	private ProgressBar pb_progress;

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = LayoutInflater.from(context);
		headView = inflater.inflate(R.layout.base_head, this);
		tv_title = (TextView) headView.findViewById(R.id.tv_title);
		iv_back = (ImageView) headView.findViewById(R.id.iv_back);
		tv_text = (TextView) headView.findViewById(R.id.tv_action_text);
		iv_more = (ImageView) headView.findViewById(R.id.iv_more_icon);
		pb_progress = (ProgressBar) headView.findViewById(R.id.pb_progress);
		pb_progress.setVisibility(View.GONE);

		iv_back.setOnClickListener(this);

		// 获取属性
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitleBar, 0, 0);

		String title = typedArray.getString(R.styleable.TitleBar_titleText);
		String actionText = typedArray.getString(R.styleable.TitleBar_actionText);

		// 是否启用返回按钮
		boolean isShowBackButton = typedArray.getBoolean(R.styleable.TitleBar_isShowBackButton, true);

		String type = typedArray.getString(R.styleable.TitleBar_actionType);
		if (TextUtils.isEmpty(type)) {
			type = "0";// 默认值
		}

		if (type.equals("0")) {
			// 无动作
			iv_more.setVisibility(View.GONE);
			tv_text.setVisibility(View.GONE);

		} else if (type.equals("1")) {
			// 单个动作
			iv_more.setVisibility(View.GONE);
			tv_text.setVisibility(View.VISIBLE);
			tv_text.setText(actionText);

		} else if (type.equals("2")) {
			// 多个动作
			iv_more.setVisibility(View.VISIBLE);
			tv_text.setVisibility(View.GONE);

		} else {
			// 无动作
			iv_more.setVisibility(View.GONE);
			tv_text.setVisibility(View.GONE);
		}

		// 设置标题
		tv_title.setText(title);

		// 是否显示返回按钮
		setBackButtonVisible(isShowBackButton);

		typedArray.recycle();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_back:
			((Activity) getContext()).onBackPressed();
			break;
		default:
			break;
		}
	}

	public void setProgressBarVisible(boolean isVisible) {
		if (pb_progress == null)
			return;
		if (isVisible) {
			pb_progress.setVisibility(View.VISIBLE);
		} else {
			pb_progress.setVisibility(View.GONE);
		}
	}

	public void setBackButtonVisible(boolean isVisible) {
		if (isVisible) {
			iv_back.setVisibility(View.VISIBLE);
		} else {
			iv_back.setVisibility(View.GONE);
		}
	}

	// 设置标题
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	// 设置右上角文本
	public void setActionText(String text) {
		tv_text.setText(text);
		tv_text.setVisibility(View.VISIBLE);
	}

	// 设置头部背景
	public void setTitleBackGroundColor(int color) {
		headView.setBackgroundColor(color);
	}

	// 设置Icon点击事件
	public void setMoreIconClickListener(View.OnClickListener l) {
		iv_more.setOnClickListener(l);
	}

	public void setMoreIconResource(int res) {
		iv_more.setImageResource(res);
	}

	/**
	 * 设置左侧按钮图标
	 * 
	 * @param res
	 */
	public void setBackIconResource(int res) {
		iv_back.setImageResource(res);
	}

	/**
	 * 
	 * @param l
	 */
	public void setBackIconClickListener(View.OnClickListener l) {
		iv_back.setOnClickListener(l);
	}

	// 设置文本点击事件
	public void setActionTextClickListener(View.OnClickListener l) {
		tv_text.setOnClickListener(l);
	}

	public void setMoreIconVisible(boolean visible) {
		if (visible) {
			iv_more.setVisibility(View.VISIBLE);
			tv_text.setVisibility(View.GONE);
		} else {
			iv_more.setVisibility(View.GONE);
			tv_text.setVisibility(View.VISIBLE);
		}
	}

	public void setActionTextVisible(boolean visible) {
		setMoreIconVisible(!visible);
	}

}
