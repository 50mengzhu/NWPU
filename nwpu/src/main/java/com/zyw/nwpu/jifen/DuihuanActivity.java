package com.zyw.nwpu.jifen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.Login;
import com.zyw.nwpu.R;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.jifen.leancloud.ScoreDetail;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.GetScoreDetailCallback;
import com.zyw.nwpu.jifen.widget.XListView;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_duihuan)
public class DuihuanActivity extends BaseActivity implements XListView.IXListViewListener {
	private XListView mListView;
	List<ScoreDetail> scoreDetailList;

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, DuihuanActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initView() {
		mListView = (XListView) findViewById(R.id.List_duihuanjilu);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mListView.setAutoLoadEnable(true);
		mListView.setXListViewListener(this);
		mListView.setRefreshTime(getTime());
		ScoreHelper.getPurchaseRecord(new GetScoreDetailCallback() {
			public void onSuccess(List<ScoreDetail> list) {
				scoreDetailList = list;
				JifenCardAdapter mAdapter = new JifenCardAdapter(DuihuanActivity.this, getItems());
				mListView.setAdapter(mAdapter);
			}

			public void onFailure(String errTip) {
				Toast.makeText(DuihuanActivity.this, errTip, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private List<JifenCard> getItems() {
		List<JifenCard> mCards = new ArrayList<JifenCard>();
		for (int i = 0; i < scoreDetailList.size(); i++) {
			JifenCard mCard = new JifenCard(scoreDetailList.get(i).getDescription(), scoreDetailList.get(i).getDate(),
					scoreDetailList.get(i).getScore());
			mCards.add(mCard);
		}
		return mCards;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			mListView.autoRefresh();
		}
	}

	@Override
	public void onRefresh() {
		// mHandler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// // 这里是对列表的更新。
		// ++mRefreshIndex;
		// mAdapter = new DuihuanCardAdapter(DuihuanActivity.this,
		// getItems(mCount));
		// mListView.setAdapter(mAdapter);
		// onLoad();
		// }
		// }, 1000);
	}

	@Override
	public void onLoadMore() {
		// mHandler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// mAdapter.notifyDataSetChanged();
		// mCount += 5;
		// mAdapter = new DuihuanCardAdapter(DuihuanActivity.this,
		// getItems(mCount));
		// mListView.setAdapter(mAdapter);
		// mListView.setSelection(mCount);
		// onLoad();
		// }
		// }, 1000);
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(getTime());
	}

	private String getTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
	}
}
