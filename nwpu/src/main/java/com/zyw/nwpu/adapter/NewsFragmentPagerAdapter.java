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

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private ArrayList<ChannelInfo> channelInfoList;

	private FragmentManager fm;
	private Boolean isJoke;

	public NewsFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public NewsFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments,
			ArrayList<ChannelInfo> channelInfoList) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
		this.channelInfoList = channelInfoList;

	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		//这里做过改动
		if(isJoke){
			JokeFragment f = new JokeFragment();
			return f;
		}else{
			NewsFragment f = new NewsFragment();
			return f;
		}
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

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		ChannelInfo info = channelInfoList.get(position);

		if (info.cha_id == AppConfig.JOKE_CHANNEL_ID) {
			// 笑话频道
			isJoke = true;
			JokeFragment f = (JokeFragment) super.instantiateItem(container,
					position);
			f.setChannelId(info.cha_id);
			f.setChannelName(info.cha_name);
			return f;
		} else {
			isJoke = false;
			NewsFragment f = (NewsFragment) super.instantiateItem(container,
					position);
			f.setChannelId(info.cha_id);
			f.setChannelName(info.cha_name);
			return f;
		}
	}
}
