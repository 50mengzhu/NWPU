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
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.fragment.CommentListFragment;
import com.zyw.nwpu.fragment.ConversationListFragment;
import com.zyw.nwpu.fragment.LikeListFragment;
import com.zyw.nwpulib.utils.CommonUtil;

/**
 * 
 * 2015年11月4日
 * 
 * @author Rocket
 * 
 */
@ContentView(R.layout.activity_usermsg)
public class MsgActivity extends BaseActivity implements OnClickListener {

	ViewPager mViewPager;
	private ArrayList<Fragment> fragmentList;

	private TextView tv_chat;
	private TextView tv_comment;
	private TextView tv_like;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iniView();
		initViewPager();
	}

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, MsgActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	private void iniView() {
		tv_chat = (TextView) findViewById(R.id.tv_chat);
		tv_comment = (TextView) findViewById(R.id.tv_comment);
		tv_like = (TextView) findViewById(R.id.tv_like);

		tv_chat.setOnClickListener(this);
		tv_comment.setOnClickListener(this);
		tv_like.setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	public void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);

		fragmentList = new ArrayList<Fragment>();
		ConversationListFragment conversationListFragment = new ConversationListFragment();
		conversationListFragment.hideTitleBar();

		CommentListFragment commentFrag = new CommentListFragment();
		LikeListFragment likeFrag = new LikeListFragment();

		fragmentList.add(conversationListFragment);
		fragmentList.add(commentFrag);
		fragmentList.add(likeFrag);

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
			// switch (arg0) {
			// case 0:
			// fragmentList.get(0).getView().setAlpha(1 - arg1);
			// fragmentList.get(1).getView().setAlpha(arg1);
			//
			// break;
			// case 1:
			// fragmentList.get(0).getView().setAlpha(arg1);
			// fragmentList.get(1).getView().setAlpha(1 - arg1);
			// break;
			// case 2:
			// fragmentList.get(0).getView().setAlpha(arg1);
			// fragmentList.get(1).getView().setAlpha(1 - arg1);
			// break;
			// }
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

	private void changePager(int page) {
		switch (page) {
		case 0:
			// tv_chat.setBackgroundResource(R.drawable.shape_flat_round_left_pressed);
			// tv_comment.setBackgroundResource(R.drawable.shape_flat_round_mid_nor);
			// tv_like.setBackgroundResource(R.drawable.shape_flat_round_right_nor);

			tv_chat.setTextColor(getApplicationContext().getResources().getColor(R.color.bg_title));
			tv_comment.setTextColor(getApplicationContext().getResources().getColor(R.color.darkgray));
			tv_like.setTextColor(getApplicationContext().getResources().getColor(R.color.darkgray));
			break;
		case 1:
			// tv_chat.setBackgroundResource(R.drawable.shape_flat_round_left_nor);
			// tv_comment.setBackgroundResource(R.drawable.shape_flat_round_mid_pressed);
			// tv_like.setBackgroundResource(R.drawable.shape_flat_round_right_nor);

			tv_chat.setTextColor(getApplicationContext().getResources().getColor(R.color.darkgray));
			tv_comment.setTextColor(getApplicationContext().getResources().getColor(R.color.bg_title));
			tv_like.setTextColor(getApplicationContext().getResources().getColor(R.color.darkgray));
			break;
		case 2:
			// tv_chat.setBackgroundResource(R.drawable.shape_flat_round_left_nor);
			// tv_comment.setBackgroundResource(R.drawable.shape_flat_round_mid_nor);
			// tv_like.setBackgroundResource(R.drawable.shape_flat_round_right_pressed);

			tv_chat.setTextColor(getApplicationContext().getResources().getColor(R.color.darkgray));
			tv_comment.setTextColor(getApplicationContext().getResources().getColor(R.color.darkgray));
			tv_like.setTextColor(getApplicationContext().getResources().getColor(R.color.bg_title));
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
