package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zyw.nwpu.adapter.UserListAdapter;
import com.zyw.nwpu.adapter.VisitorListAdapter;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.UserInfoService.QueryUsersCallback;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpulib.model.UserInfo;

@ContentView(R.layout.activity_userlist)
public class VisitorListActivity extends BaseActivity implements IXListViewListener {

	// XListView相关的参数
	private XListView mListView;
	private List<UserInfo> mData;// 与ListView绑定的数据
	private VisitorListAdapter mAdapter;

	private String userObjId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("访客记录");

		userObjId = getIntent().getStringExtra("userObjId");

		showWaiteDialog();
		getData(userObjId);
	}

	private void getData(String id) {
		// AVUser.getCurrentUser().toString();
		BBSService.getVisitors(AVUser.getCurrentUser(), 0, new QueryUsersCallback() {

			@Override
			public void onSuccess(List<UserInfo> userList) {
				if (userList == null || userList.size() == 0) {
					ToastUtils.showShortToast(getApplicationContext(), "没有查询到用户");
					dismissWaiteDialog();
					return;
				}
				mData.clear();
				for (int i = 0; i < userList.size(); i++) {
					mData.add(userList.get(i));
				}
				mAdapter.notifyDataSetChanged();
				dismissWaiteDialog();
			}

			@Override
			public void onFailed(String errorTip) {
				ToastUtils.showShortToast(getApplicationContext(), errorTip);
				dismissWaiteDialog();
			}
		});
	}

	private ProgressDialog pd = null;

	private void showWaiteDialog() {
		pd = new ProgressDialog(VisitorListActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		pd.setMessage("正在加载...");
		pd.show();
	}

	private void dismissWaiteDialog() {
		if (pd != null)
			pd.dismiss();
		pd = null;
	}

	@Override
	public void initView() {
		mData = new ArrayList<UserInfo>();
		mListView = (XListView) findViewById(R.id.mListView);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		mAdapter = new VisitorListAdapter(this, mData);
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
		UserProfileActivity.startThis(VisitorListActivity.this, userid);
	}

	public static void startThis(Context cxt, String userObjId) {
		Intent intent = new Intent(cxt, VisitorListActivity.class);
		intent.putExtra("userObjId", userObjId);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		loadMore();
	}

	private void loadMore() {
		if (mData == null)
			return;

		BBSService.getVisitors(AVUser.getCurrentUser(), mData.size(), new QueryUsersCallback() {

			@Override
			public void onSuccess(List<UserInfo> userList) {
				if (userList == null || userList.size() == 0) {
					ToastUtils.showShortToast(getApplicationContext(), "没有更多了");
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
				ToastUtils.showShortToast(getApplicationContext(), errorTip);
				mListView.stopLoadMore();
			}
		});
	}
}
