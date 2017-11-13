package com.zyw.nwpu.fragment;

import java.util.ArrayList;

import com.zyw.nwpu.JokeDetailActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.adapter.JokeAdapter;
import com.zyw.nwpulib.model.JokeEntity;
import com.zyw.nwpu.listener.JokeDataReceiveListener;
import com.zyw.nwpu.service.JokeListService;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

public class JokeFragment extends Fragment implements IXListViewListener, JokeDataReceiveListener {
	Activity activity;
	ArrayList<JokeEntity> jokeList = new ArrayList<JokeEntity>();
	XListView mListView;
	JokeAdapter mAdapter = null;
	private String channel_name;
	private int channel_id;
	ImageView detail_loading;

	private boolean isVisibleToUser = false;
	private boolean hasCreatedView = false;

	public final static int SET_NEWSLIST = 0;

	public void setChannelId(int id) {
		channel_id = id;
	}

	public void setChannelName(String name) {
		channel_name = name;
	}

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
		detail_loading = (ImageView) view.findViewById(R.id.detail_loading);// 等待界面
		iniXlistview(view);
		return view;
	}

	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		this.isVisibleToUser = isVisibleToUser;

		// 不可见什么都不做
		if (!isVisibleToUser) {
			return;
		}

		// 可见时的操作 有数据直接加载 没数据请求数据并加载
		if (jokeList != null && jokeList.size() != 0) {
			// 可见且有数据时直接加载
			detail_loading.setVisibility(View.GONE);
			if (hasCreatedView) {
				if (mAdapter == null) {
					mAdapter = new JokeAdapter(activity, jokeList);
					mListView.setAdapter(mAdapter);
				} else {
					// mAdapter.notifyDataSetChanged();
				}
			}
		} else {
			// 可见但没有数据时，请求数据
			new JokeListService().getJokeListFromInternetAsyn(this, channel_id, 0, false);
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void iniXlistview(View view) {
		mListView = (XListView) view.findViewById(R.id.mListView);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);

		mAdapter = new JokeAdapter(activity, jokeList);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0) {
					return;
				}
				position--;

				// 进入详细信息页面
				Intent intent = new Intent(activity, JokeDetailActivity.class);
				intent.putExtra("joke", mAdapter.getItem(position));
				intent.putExtra("cat_name", channel_name);

				startActivity(intent);
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

				// 刷新界面
				// mAdapter.notifyDataSetChanged();
			}
		});

		hasCreatedView = true;
	}

	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAdapter = null;
		hasCreatedView = false;
		jokeList.clear();// 数据不清空
	}

	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		// 只从网络获取数据
		new JokeListService().getJokeListFromInternetAsyn(this, channel_id, 0, false);

		// 隐藏刷新头
		mListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		mListView.stopLoadMore();
		int skip;
		if (jokeList == null || jokeList.size() == 0) {
			skip = 0;
		} else {
			skip = jokeList.size();
		}
		new JokeListService().getJokeListFromInternetAsyn(this, channel_id, skip, true);// 加载更多
		// mListView.setPullLoadEnable(false);
	}

	@Override
	public void OnJokeDataReceived(ArrayList<JokeEntity> list, boolean loadmore) {
		if (list == null || list.size() == 0) {
			if (loadmore) {
				CommonUtil.ToastUtils.showShortToast(activity, "没有更多了");
			}
			return;
		}

		if (!loadmore) {
			// 不是加载更多，要清空
			jokeList.clear();
		}

		for (JokeEntity item : list) {
			jokeList.add(item);
		}

		if (isVisibleToUser && hasCreatedView) {
			if (mAdapter == null) {
				mAdapter = new JokeAdapter(activity, jokeList);
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

}
