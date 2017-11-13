package com.zyw.nwpu.xmz;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zyw.nwpu.R;
import com.zyw.nwpu.TagActivity;
import com.zyw.nwpu.adapter.StatusAdapter;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;
import com.zyw.nwpu.xmz.XmzHelper.OnComplete;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_xmz_myproject)
public class MyProjectsActivity extends BaseActivity implements
		IXListViewListener, View.OnClickListener {

	// XListView相关的参数
	private XListView mListView;
	private List<Project> mData;// 与ListView绑定的数据
	private ProjectsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
	}

	private void getData() {
		XmzHelper.getProjectList(new OnComplete() {

			@Override
			public void onComplete(List<Project> data) {
				if (data == null || data.size() == 0)
					return;
				mData.clear();
				for (int i = 0; i < data.size(); i++) {
					mData.add(data.get(i));
				}
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void initView() {
		mData = new ArrayList<Project>();
		mListView = (XListView) findViewById(R.id.mListView);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mAdapter = new ProjectsAdapter(getApplicationContext(), mData);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 进入评价页面
				EvaluationActivity.startThis(MyProjectsActivity.this);
			}
		});
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, MyProjectsActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}
