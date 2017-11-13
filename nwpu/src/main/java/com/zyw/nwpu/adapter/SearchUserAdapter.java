package com.zyw.nwpu.adapter;

import java.util.ArrayList;

import com.zyw.nwpulib.model.ChannelInfo;
import com.zyw.nwpu.fragment.JokeFragment;
import com.zyw.nwpu.fragment.NewsFragment;
import com.zyw.nwpu.service.AppConfig;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class SearchUserAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public SearchUserAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public SearchUserAdapter(FragmentManager fm, ArrayList<Fragment> fragments,
			ArrayList<ChannelInfo> channelInfoList) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;

	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}
}
