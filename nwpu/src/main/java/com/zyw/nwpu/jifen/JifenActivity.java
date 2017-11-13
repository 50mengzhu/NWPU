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
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.GetUserScoreSummaryCallback;
import com.zyw.nwpu.jifen.widget.XListView;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

@ContentView(R.layout.activity_jifen)
public class JifenActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
	private XListView mListView;
	TextView mJifen;
	List<ScoreDetail> scoreDetailList = new ArrayList<ScoreDetail>();

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, JifenActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initView() {
		// 积分的设置
		mJifen = (TextView) findViewById(R.id.jifencontent);
		ScoreHelper.getUserScoreSummary(new GetUserScoreSummaryCallback() {
			public void onSuccess(String usrNickname, String usrStudentId, String avatarUrl, int totalScore) {
				mJifen.setText(Integer.toString(totalScore));
			}

			public void onFailure(String errTip) {
				ToastUtils.showShortToast(errTip);
			}
		});

		mListView = (XListView) findViewById(R.id.ListView);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mListView.setAutoLoadEnable(true);
		mListView.setXListViewListener(this);
		mListView.setRefreshTime(getTime());

		ScoreHelper.getScoreDetail(1000, 0, new GetScoreDetailCallback() {
			public void onSuccess(List<ScoreDetail> list) {
				if (list == null || list.size() == 0) {
					ToastUtils.showShortToast("没有积分记录，快去赚一些积分吧！");
					return;
				}
				scoreDetailList = list;
				JifenCardAdapter mAdapter = new JifenCardAdapter(JifenActivity.this, getItems());
				mListView.setAdapter(mAdapter);
			}

			public void onFailure(String errTip) {
				ToastUtils.showShortToast(errTip);
			}
		});

		findViewById(R.id.ll_jifenguize).setOnClickListener(this);
		findViewById(R.id.ll_duihuanjilu).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_jifenguize: {
			Intent intent = new Intent(JifenActivity.this, JfenGuizeActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.ll_duihuanjilu: {
			DuihuanActivity.startThis(JifenActivity.this);
			break;
		}
		}
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
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
	}

	private String getTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
	}
}
