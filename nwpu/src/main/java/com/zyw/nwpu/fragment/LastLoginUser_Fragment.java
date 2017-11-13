package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import android.support.v4.app.Fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zyw.nwpu.PickHomeTownActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.SearchUserActivity;
import com.zyw.nwpu.UserListActivity;
import com.zyw.nwpu.UserProfileActivity;
import com.zyw.nwpu.adapter.UserListAdapter;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.service.AppConfig;
import com.zyw.nwpu.service.UserInfoService;
import com.zyw.nwpu.service.UserInfoService.QueryUsersCallback;
import com.zyw.nwpu.service.UserInfoService.SearchCondition;
import com.zyw.nwpulib.model.UserInfo;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpu.view.MDatePicker;
import com.zyw.nwpu.view.MDatePicker.OnChooseEvent;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

public class LastLoginUser_Fragment extends Fragment implements IXListViewListener {

	// XListView相关的参数
	private XListView mListView;
	private List<UserInfo> mData;// 与ListView绑定的数据
	private UserListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_userlist, container, false);
		initView();
		showWaiteDialog();
		getData();
		return rootView;
	}

	private void getData() {
		String currentUserObjId = "";
		if (AVUser.getCurrentUser() != null)
			currentUserObjId = AVUser.getCurrentUser().getObjectId();
		UserInfoService.queryUsers(currentUserObjId, null, 0, new QueryUsersCallback() {

			@Override
			public void onSuccess(List<UserInfo> userList) {
				if (userList == null || userList.size() == 0) {
					ToastUtils.showShortToast(getActivity(), "没有查询到用户");
					dismissWaiteDialog();
					mListView.stopRefresh();
					return;
				}

				mData.clear();
				for (int i = 0; i < userList.size(); i++) {
					mData.add(userList.get(i));
				}
				mAdapter.notifyDataSetChanged();
				dismissWaiteDialog();
				mListView.stopRefresh();
			}

			@Override
			public void onFailed(String errorTip) {
				ToastUtils.showShortToast(getActivity(), errorTip);
				dismissWaiteDialog();
				mListView.stopRefresh();
			}
		});
	}

	private void showWaiteDialog() {
		((SearchUserActivity) getActivity()).setProgressBarVisible(true);

	}

	private void dismissWaiteDialog() {
		((SearchUserActivity) getActivity()).setProgressBarVisible(false);
	}

	public void initView() {
		mData = new ArrayList<UserInfo>();
		mListView = (XListView) rootView.findViewById(R.id.mListView);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		mAdapter = new UserListAdapter(getActivity(), mData);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				toUserInfo(arg2 - 1);
			}
		});
	}

	// 进入用户个人中心
	private void toUserInfo(final int position) {
		String userid = mData.get(position).objId;
		UserProfileActivity.startThis(getActivity(), userid);
	}

	@Override
	public void onLoadMore() {
		loadMore();
	}

	private void loadMore() {
		if (mData == null)
			return;

		String currentUserObjId = "";
		if (AVUser.getCurrentUser() != null)
			currentUserObjId = AVUser.getCurrentUser().getObjectId();
		UserInfoService.queryUsers(currentUserObjId, null, mData.size(), new QueryUsersCallback() {

			@Override
			public void onSuccess(List<UserInfo> userList) {
				if (userList == null || userList.size() == 0) {
					ToastUtils.showShortToast(getActivity(), "没有更多了");
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
					return;
				}
				for (int i = 0; i < userList.size(); i++) {
					mData.add(userList.get(i));
				}
				mAdapter.notifyDataSetChanged();
				mListView.stopLoadMore();
			}

			@Override
			public void onFailed(String errorTip) {
				ToastUtils.showShortToast(getActivity(), errorTip);
				mListView.stopLoadMore();
			}
		});
	}

	@Override
	public void onRefresh() {
		getData();
	}

}
