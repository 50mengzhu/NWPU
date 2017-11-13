package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;

import com.avos.avoscloud.AVUser;
import com.zyw.nwpu.BBSCommentActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.adapter.LikeAdapter;
import com.zyw.nwpulib.model.LikeData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.BBSServiceCallback.GetLikeListCallback;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

public class LikeListFragment extends Fragment implements IXListViewListener {

	private LikeAdapter madapter;
	private List<LikeData> mdata;
	private XListView mList;

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_bbs_mymsg, container,
				false);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initView();
				getLikes(0);
			}
		}, 1);
		return rootView;
	}

	private void initView() {
		// 评论列表
		mList = (XListView) rootView.findViewById(R.id.lv_mynotify);// 列表
		mList.setPullLoadEnable(true);
		mList.setPullRefreshEnable(true);
		mList.setXListViewListener(this);

		// mList.setRemoveListener(this);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				toCommentActivity(arg2 - 1);
			}
		});

		mdata = new ArrayList<LikeData>();// 数据
		madapter = new LikeAdapter(getActivity().getApplicationContext(),
				mdata, R.layout.list_item_mymsg);// 适配器
		mList.setAdapter(madapter);
	}

	private void getLikes(final int skip) {

		if (AVUser.getCurrentUser() == null) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "请先登录");
			return;
		}

		BBSService.getLikeList(AVUser.getCurrentUser().getObjectId(), skip,
				new GetLikeListCallback() {

					@Override
					public void onSuccess(List<LikeData> data) {
						if (data == null || data.size() == 0) {
							if (skip == 0) {
								CommonUtil.ToastUtils.showShortToast(
										getActivity(), "没有内容");
								mList.stopRefresh();
							} else {
								CommonUtil.ToastUtils.showShortToast(
										getActivity(), "没有更多了");
								mList.stopLoadMore();
							}
							return;
						}
						if (skip == 0) {
							mdata.clear();
						}
						for (int i = 0; i < data.size(); i++) {
							mdata.add(data.get(i));
						}
						madapter.notifyDataSetChanged();

						if (skip == 0) {
							mList.stopRefresh();
						} else {
							mList.stopLoadMore();
						}
					}

					@Override
					public void onFailed(String errorTip) {
						CommonUtil.ToastUtils.showShortToast(getActivity(),
								errorTip);

						if (skip == 0) {
							mList.stopRefresh();
						} else {
							mList.stopLoadMore();
						}

					}
				});
	}

	private void toCommentActivity(final int position) {
		BBSCommentActivity.startThis(getActivity(),
				mdata.get(position).targetTopicId);
	}

	@Override
	public void onRefresh() {
		getLikes(0);
	}

	@Override
	public void onLoadMore() {
		if (mdata != null) {
			getLikes(mdata.size());
		} else {
			mList.stopLoadMore();
		}
	}
}
