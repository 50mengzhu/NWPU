package com.zyw.nwpu.fragment;

import java.util.ArrayList;

import com.zyw.nwpu.ChannelActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.adapter.NewsFragmentPagerAdapter;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpulib.model.ChannelInfo;
import com.zyw.nwpulib.model.ChannelItem;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ScreenUtils;
import com.zyw.nwpu.bean.ChannelManage;
import com.zyw.nwpu.service.AppConfig;
import com.zyw.nwpu.view.ColumnHorizontalScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

public class News_Fragment extends Fragment {
	Activity activity;

	private View rootView;

	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	LinearLayout mRadioGroup_content;
	LinearLayout ll_more_columns;
	RelativeLayout rl_column;
	private ViewPager mViewPager;
	private ImageView button_more_columns;
	private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();

	private int columnSelectIndex = 0;

	public ImageView shade_left;
	public ImageView shade_right;

	private int mScreenWidth = 0;

	// private int mItemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private ArrayList<ChannelInfo> channelInfoList = new ArrayList<ChannelInfo>();

	/** 请求CODE */
	public static final int CHANNELREQUEST = 1;
	// public static final int REQUESTCODE_LOGIN = 2;
	/** 调整返回的RESULTCODE */
	public static final int CHANNELRESULT = 10;
	public static final int RESULTCODE_LOGIN_SUCCEED = 20;
	public static final int RESULTCODE_LOGIN_FAILED = 21;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHANNELREQUEST:
			if (resultCode == CHANNELRESULT) {
				setChangelView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mScreenWidth = ScreenUtils.getScreenWidth(getActivity());

		rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_news, null);
		initView();

		return rootView;
	}

	private void initView() {
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) rootView
				.findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) rootView.findViewById(R.id.mRadioGroup_content);
		ll_more_columns = (LinearLayout) rootView.findViewById(R.id.ll_more_columns);
		rl_column = (RelativeLayout) rootView.findViewById(R.id.rl_column);
		button_more_columns = (ImageView) rootView.findViewById(R.id.button_more_columns);
		mViewPager = (ViewPager) rootView.findViewById(R.id.mViewPager);
		shade_left = (ImageView) rootView.findViewById(R.id.shade_left);
		shade_right = (ImageView) rootView.findViewById(R.id.shade_right);

		// 更多栏目
		button_more_columns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent_channel = new Intent(getActivity().getApplicationContext(), ChannelActivity.class);
				startActivityForResult(intent_channel, CHANNELREQUEST);
				getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		setChangelView();
	}

	/**
	 * 当栏目项发生变化时候调用
	 */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}

	/**
	 * 初始化Fragment
	 */
	private void initFragment() {
		fragments.clear();// 清空
		channelInfoList.clear();
		int count = userChannelList.size();
		for (int i = 0; i < count; i++) {
			ChannelInfo info = new ChannelInfo(userChannelList.get(i).getId(), userChannelList.get(i).getName());

			if (info.cha_id == AppConfig.JOKE_CHANNEL_ID) {
				// 笑话界面
				JokeFragment jokeFragment = new JokeFragment();
				fragments.add(jokeFragment);
				channelInfoList.add(info);
			} else {
				NewsFragment newfragment = new NewsFragment();
				fragments.add(newfragment);
				channelInfoList.add(info);
			}
		}
		NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getChildFragmentManager(), fragments,
				channelInfoList);
		// mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}

	/**
	 * ViewPager切换监听方法
	 */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};

	/**
	 * 初始化Column栏目项
	 */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = userChannelList.size();
		mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth, mRadioGroup_content, shade_left, shade_right,
				ll_more_columns, rl_column);

		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < count; i++) {
			// 这里设置 channel item 的宽度
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT);
			params.leftMargin = 8;
			params.rightMargin = 8;

			RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.colume_item, null);

			TextView columnTextView = (TextView) layout.findViewById(R.id.text_item);
			// columnTextView.setTextAppearance(getActivity(),
			// R.style.top_category_scroll_view_item_text);
			// localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
			// columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
			// columnTextView.setGravity(Gravity.CENTER);
			// columnTextView.setPadding(15, 5, 15, 5);
			layout.setId(i);
			columnTextView.setText(userChannelList.get(i).getName());
			// columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v) {
							localView.setSelected(false);
						} else {
							localView.setSelected(true);
							mViewPager.setCurrentItem(i);
						}
					}
					// CommonUtil.ToastUtils.showShortToast(getActivity().getApplicationContext(),
					// userChannelList.get(v.getId()).getName());
				}
			});

			mRadioGroup_content.addView(layout, i, params);
		}
		selectTab(0);// 选中第一个栏目
	}

	/**
	 * 选择的Column里面的Tab
	 */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
				checkView.findViewById(R.id.bottomLine).setVisibility(View.VISIBLE);
			} else {
				ischeck = false;
				checkView.findViewById(R.id.bottomLine).setVisibility(View.GONE);
			}
			checkView.setSelected(ischeck);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/** 获取Column栏目 数据 */
	private void initColumnData() {
		// 数据库读取用户选择的栏目
		userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(AppApplication.getApp().getSQLHelper())
				.getUserChannel());
	}

	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
