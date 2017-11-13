package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

import com.zyw.nwpu.adapter.BBSTagAdapter;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.BBSServiceCallback.GetTopicListCallback;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_addtag)
public class PickTagActivity extends BaseActivity implements OnClickListener,
		IXListViewListener {

	private EditText et_tag;

	// XListView相关的参数
	private XListView mListView;
	private List<String> mData;// 与ListView绑定的数据
	private BBSTagAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopics();
	}

	private void getTopics() {
		BBSService.getTopics(getApplicationContext(), 20, 0,
				new GetTopicListCallback() {

					@Override
					public void onSuccess(List<String> topics) {
						if (topics == null || topics.size() == 0)
							return;
						mData.clear();
						for (int i = 0; i < topics.size(); i++) {
							mData.add(topics.get(i));
						}
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onFailed(String errorTip) {
						CommonUtil.ToastUtils
								.showShortToast(getApplicationContext(),
										"获取话题列表失败：" + errorTip);
					}
				});
	}

	@Override
	public void initView() {
		et_tag = (EditText) findViewById(R.id.et_tag);

		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("选择话题");
		bar.setActionTextClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 保存话题
				BBSService.addTopic(et_tag.getText().toString().trim());

				Intent intent = PickTagActivity.this.getIntent();
				intent.putExtra("tag", et_tag.getText().toString().trim());
				PickTagActivity.this.setResult(RESULT_OK, intent);
				PickTagActivity.this.onBackPressed();
			}
		});

		mData = new ArrayList<String>();
		mListView = (XListView) findViewById(R.id.mListView);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);

		mAdapter = new BBSTagAdapter(getApplicationContext(), mData);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				et_tag.setText(mData.get(arg2 - 1));
			}
		});
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		if (mData == null || mData.size() == 0 || mListView == null)
			return;
		BBSService.getTopics(getApplicationContext(), 20, mData.size(),
				new GetTopicListCallback() {

					@Override
					public void onSuccess(List<String> topics) {
						if (topics == null || topics.size() == 0) {
							CommonUtil.ToastUtils.showShortToast(
									getApplicationContext(), "没有更多了");
							mListView.stopLoadMore();
							mListView.setPullLoadEnable(false);
							return;
						}
						for (int i = 0; i < topics.size(); i++) {
							mData.add(topics.get(i));
						}
						mAdapter.notifyDataSetChanged();
						mListView.stopLoadMore();
					}

					@Override
					public void onFailed(String errorTip) {
						CommonUtil.ToastUtils
								.showShortToast(getApplicationContext(),
										"获取话题列表失败：" + errorTip);
						mListView.stopLoadMore();
					}
				});
	}
}
