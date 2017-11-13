package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import com.zyw.nwpu.R;
import com.zyw.nwpu.view.PagerHeaderView;
import com.zyw.nwpu.view.PagerHeaderView.OnClickHeaderListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 多栏目帖子列表
 * 
 * @author Rocket
 * 
 */
public class MultiStatusListFragment extends Fragment {

	private View rootView;
	private PagerHeaderView phv;
	private ViewPager mViewPager;
	private ArrayList<Fragment> fragmentList;

	StatusListFragment f1 = new StatusListFragment();
	StatusListFragment f2 = new StatusListFragment();
	StatusListFragment f3 = new StatusListFragment();
	StatusListFragment f4 = new StatusListFragment();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_multi_status, container, false);
		initView();
		return rootView;
	}

	private void initView() {
		phv = (PagerHeaderView) rootView.findViewById(R.id.phv);
		phv.setOnClickHeaderListener(new OnClickHeaderListener() {

			@Override
			public void onClick(int index) {
				mViewPager.setCurrentItem(index);
			}
		});

		iniFrags();
	}

	@SuppressWarnings("deprecation")
	private void iniFrags() {
		mViewPager = (ViewPager) rootView.findViewById(R.id.mViewPager);

		fragmentList = new ArrayList<Fragment>();

		// StatusListFragment f1 = new StatusListFragment();
		// StatusListFragment f2 = new StatusListFragment();
		// StatusListFragment f3 = new StatusListFragment();
		// StatusListFragment f4 = new StatusListFragment();
		f1.setTag("瓜大问答");

		fragmentList.add(f1);
		fragmentList.add(f2);
		fragmentList.add(f3);
		fragmentList.add(f4);

		// 给ViewPager设置适配器
		mViewPager.setAdapter(new MyFragmentPagerAdapter(getFragmentManager(), fragmentList));
		mViewPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(3);
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
			phv.setCurrentPage(arg0);
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

}