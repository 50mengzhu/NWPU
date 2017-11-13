package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.ImageVideoBitmapDecoder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.adapter.BBSCommentAdapter;
import com.zyw.nwpu.adapter.SignAdapter;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.AddScoreRecordCallback;
import com.zyw.nwpu.service.SignService;
import com.zyw.nwpu.service.SignService.OnCheckSignListener;
import com.zyw.nwpu.service.SignService.OnGetCurrentUserSignStatisticsListener;
import com.zyw.nwpu.service.SignService.OnGetRankListListener;
import com.zyw.nwpu.service.SignService.OnSignListener;
import com.zyw.nwpu.service.SignService.SignBean;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.model.UserInfo;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

@ContentView(R.layout.activity_qiandao)
public class QiandaoActivity extends BaseActivity implements OnClickListener, IXListViewListener {

	private XListView mListView;
	private SignAdapter mAdapter;
	private ArrayList<SignBean> signList = new ArrayList<SignBean>();

	// 头部布局
	private View headView;

	// 头部布局里的控件
	private TextView tv_date;
	private ImageView iv_sign;
	private TextView tv_monthnum;
	private TextView tv_totalnum;

	private View ll_currentuser;
	private ImageView iv_avatar;
	private ImageView iv_first;
	private TextView tv_name;
	private TextView tv_rank;
	private TextView tv_days;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
	}

	private void getData() {

		// 当前日期
		tv_date.setText(CommonUtil.DateUtils.getFormatTime("yyyy年MM月dd日"));

		// 是否已经签到
		SignService.checkSign(new OnCheckSignListener() {

			@Override
			public void onFailure(String error) {
				ToastUtils.showShortToast("查询签到信息失败" + error);
			}

			@Override
			public void onSuccess(boolean isSigned) {
				if (isSigned) {
					iv_sign.setImageResource(R.drawable.ic_sign_yes);
					iv_sign.setOnClickListener(null);
				} else {
					iv_sign.setImageResource(R.drawable.ic_sign_no);
					iv_sign.setOnClickListener(QiandaoActivity.this);
				}
			}
		});

		getCurrentUserSignStatistics();
		getRankList();
	}

	/**
	 * 获取签到排名列表
	 */
	private void getRankList() {
		// 获取排名列表

		// TODO 这里要修改
		// SignService.getRankList(0, new OnGetRankListListener() {
		ScoreHelper.getScoreRank(0, new OnGetRankListListener() {

			@Override
			public void onFailure(String error) {
				ToastUtils.showShortToast("查询签到信息失败" + error);
			}

			@Override
			public void onSuccess(List<SignBean> list) {
				if (list == null || list.size() == 0)
					return;

				signList.clear();

				for (int i = 0; i < list.size(); i++) {
					signList.add(list.get(i));
				}

				mAdapter.notifyDataSetChanged();

				// 允许加载更多
				mListView.setPullLoadEnable(true);
			}
		});
	}

	/**
	 * 签到
	 */
	private void sign() {
		iv_sign.setOnClickListener(null);
		SignService.sign(new OnSignListener() {

			@Override
			public void onFailure(String error) {
				ToastUtils.showShortToast("签到失败" + error);
				iv_sign.setOnClickListener(QiandaoActivity.this);
			}

			@Override
			public void onSuccess() {
				ToastUtils.showShortToast("签到成功");
				iv_sign.setImageResource(R.drawable.ic_sign_yes);
				iv_sign.setOnClickListener(null);

				getCurrentUserSignStatistics();
				getRankList();

				// 增加积分
				ScoreHelper.addScore("签到", 1, new AddScoreRecordCallback() {

					@Override
					public void onSuccess() {
						ToastUtils.showShortToast("增加1积分");
					}

					@Override
					public void onFailure(String errTip) {
						// TODO Auto-generated method stub
					}
				});
			}
		});
	}

	private void getCurrentUserSignStatistics() {
		// 获取当前用户的签到统计信息
		SignService.getCurrentUserSignStatistics(new OnGetCurrentUserSignStatisticsListener() {

			@Override
			public void onFailure(String error) {
				ToastUtils.showShortToast("查询签到信息失败" + error);
			}

			@Override
			public void onSuccess(int total, int thisMonth, int rank, String nickname, String avatar) {
				tv_totalnum.setText(String.valueOf(total));// 总签到数
				tv_monthnum.setText(String.valueOf(thisMonth));// 当月签到数

				if (thisMonth != 0) {
					ll_currentuser.setVisibility(View.VISIBLE);

					tv_name.setText("我");// 当前用户昵称
					tv_rank.setText("第" + String.valueOf(rank) + "名");// 排名
					tv_days.setText(String.valueOf(thisMonth));// 当月签到数
					ImageLoader.getInstance().displayImage(avatar, iv_avatar, Options.getHeadImageDisplayOptions());// 当前用户头像

					if (rank == 1) {
						iv_first.setVisibility(View.VISIBLE);
					} else {
						iv_first.setVisibility(View.GONE);
					}
				} else {
					ll_currentuser.setVisibility(View.GONE);
				}
			}
		});
	}

	@SuppressLint("InflateParams")
	private void iniHeadView() {
		headView = getLayoutInflater().inflate(R.layout.list_head_sign, null);
		tv_date = (TextView) headView.findViewById(R.id.tv_date);
		iv_sign = (ImageView) headView.findViewById(R.id.iv_sign);
		tv_monthnum = (TextView) headView.findViewById(R.id.tv_monthnum);
		tv_totalnum = (TextView) headView.findViewById(R.id.tv_totalnum);

		ll_currentuser = headView.findViewById(R.id.ll_currentuser);

		iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);
		iv_first = (ImageView) headView.findViewById(R.id.iv_first);
		tv_name = (TextView) headView.findViewById(R.id.tv_name);
		tv_rank = (TextView) headView.findViewById(R.id.tv_rank);
		tv_days = (TextView) headView.findViewById(R.id.tv_days);

		ll_currentuser.setVisibility(View.GONE);
		headView.setOnClickListener(null);
	}

	final String tip = "前30名并且签到天数达到10天及以上的用户，每人赠送3G锐捷流量。简单但竞争力大哦，记得明天继续签到哦！\n\n本次活动最终解释权归“瓜大生活APP”所有。";

	@Override
	public void initView() {
		iniHeadView();

		TitleBar head = (TitleBar) findViewById(R.id.head);
		head.setActionText("签到福利");
		head.setActionTextClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(QiandaoActivity.this).setTitle("签到规则").setMessage(tip)
						.setPositiveButton("确定", null).show();
			}
		});

		mListView = (XListView) findViewById(R.id.mListView);// 列表
		mListView.addHeaderView(headView);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 进入个人中心
				UserProfileActivity.startThis(QiandaoActivity.this, signList.get(arg2 - 2).getUserObjId());
			}
		});

		mAdapter = new SignAdapter(getApplicationContext(), signList);
		mListView.setAdapter(mAdapter);
	}

	public static void startThis(Context cxt) {

		boolean isLogedIn = AccountHelper.isLogedIn(cxt);

		// TODO 下面这要删掉
		isLogedIn = true;

		// 判断是否已经登录
		if (!isLogedIn) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登录");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, QiandaoActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {

		// 签到列表加载更多
		if (signList == null) {
			mListView.stopLoadMore();
			mListView.setPullLoadEnable(false);
			return;
		}

		// TODO 这里要修改
		ScoreHelper.getScoreRank(signList.size(), new OnGetRankListListener() {
			// SignService.getRankList(signList.size(), new
			// OnGetRankListListener() {

			@Override
			public void onFailure(String error) {
				ToastUtils.showShortToast("查询签到信息失败" + error);
			}

			@Override
			public void onSuccess(List<SignBean> list) {
				if (list == null || list.size() == 0) {
					mListView.stopLoadMore();
					ToastUtils.showShortToast("没有更多了");
					mListView.setPullLoadEnable(false);
					return;
				}

				for (int i = 0; i < list.size(); i++) {
					signList.add(list.get(i));
				}

				mAdapter.notifyDataSetChanged();
				mListView.stopLoadMore();
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_sign:
			sign();
			break;

		default:
			break;
		}
	}
}
