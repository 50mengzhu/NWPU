package com.zyw.nwpu;

import java.util.ArrayList;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.fragment.CommentListFragment;
import com.zyw.nwpu.fragment.ConditionSearchUser_Fragment;
import com.zyw.nwpu.fragment.ConversationListFragment;
import com.zyw.nwpu.fragment.LastLoginUser_Fragment;
import com.zyw.nwpu.fragment.LikeListFragment;
import com.zyw.nwpulib.utils.CommonUtil;

/**
 * 
 * 2015年11月4日
 * 
 * @author Rocket
 * 
 */
@ContentView(R.layout.activity_searchuser)
public class SearchUserActivity extends BaseActivity implements OnClickListener {

	ViewPager mViewPager;
	private ArrayList<Fragment> fragmentList;

	// 下划线
	private View view_lastlogin, view_conditionsearch;

	private TitleBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iniView();
		initViewPager();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (fragmentList.get(1) != null) {
			fragmentList.get(1).onActivityResult(arg0, arg1, arg2);
		}
	}

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登录");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, SearchUserActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	public void setProgressBarVisible(boolean is) {
		bar.setProgressBarVisible(is);
	}

	private void iniView() {
		view_lastlogin = findViewById(R.id.bar_lastlogin);
		view_conditionsearch = findViewById(R.id.bar_conditionsearch);
		bar = (TitleBar) findViewById(R.id.head);
		findViewById(R.id.rl_lastlogin).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 显示近期登录用户表
				currentPage = 0;
				mViewPager.setCurrentItem(currentPage);
			}
		});
		findViewById(R.id.rl_conditionsearch).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 显示条件查询
				currentPage = 1;
				mViewPager.setCurrentItem(currentPage);
			}
		});

		view_lastlogin.setVisibility(View.VISIBLE);
		view_conditionsearch.setVisibility(View.GONE);
	}

	@SuppressWarnings("deprecation")
	public void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);

		fragmentList = new ArrayList<Fragment>();
		LastLoginUser_Fragment frag1 = new LastLoginUser_Fragment();
		ConditionSearchUser_Fragment frag2 = new ConditionSearchUser_Fragment();

		fragmentList.add(frag1);
		fragmentList.add(frag2);

		// 给ViewPager设置适配器
		mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mViewPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(2);

		changePager(0);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(int arg0) {
			changePager(arg0);
		}
	}

	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}
	}

	@Override
	public void onClick(View arg0) {
		int currentpage = 0;
		switch (arg0.getId()) {
		case R.id.tv_chat:
			currentpage = 0;
			mViewPager.setCurrentItem(currentpage);
			break;
		case R.id.tv_comment:
			currentpage = 1;
			mViewPager.setCurrentItem(currentpage);
			break;
		case R.id.tv_like:
			currentpage = 2;
			mViewPager.setCurrentItem(currentpage);
			break;

		case R.id.iv_back:
			this.onBackPressed();
			break;
		}
	}

	private int currentPage = 0;

	private void changePager(int page) {
		switch (page) {
		case 0:
			currentPage = 0;
			view_lastlogin.setVisibility(View.VISIBLE);
			view_conditionsearch.setVisibility(View.GONE);
			break;
		case 1:
			currentPage = 1;
			view_lastlogin.setVisibility(View.GONE);
			view_conditionsearch.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

}
