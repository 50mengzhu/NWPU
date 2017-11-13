package com.zyw.nwpu.jifen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.R;
import com.zyw.nwpu.SetPhoneNumActivity;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.jifen.leancloud.Product;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.BuyProductCallback;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.GetUserScoreSummaryCallback;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.OnGetProductsCallback;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

/**
 * Created by 13202 on 2016/12/6.
 */
@ContentView(R.layout.activity_jiangpin)
public class JiangpinActivity extends BaseActivity {
	private GridView mGridView;
	// LinearLayout gotoJifen;
	ImageView iv_yonghu;
	TextView tv_yonghu;
	TextView tv_xuehao;
	TextView tv_totalScore;

	List<Product> imageList;

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, JiangpinActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScoreHelper.getProducts(new OnGetProductsCallback() {
			@Override
			public void onFailed(String errorTip) {
				ToastUtils.showShortToast(errorTip);
			}

			@Override
			public void onSuccess(List<Product> list) {
				imageList = list;

				JiangpinCardAdapter mAdapter = new JiangpinCardAdapter(JiangpinActivity.this, getItems());
				mGridView.setAdapter(mAdapter);
				mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						purchaseAlert((int) id);
					}
				});
			}
		});
	}

	public void initView() {
		tv_yonghu = (TextView) findViewById(R.id.jiangpin_user);
		tv_xuehao = (TextView) findViewById(R.id.jiangpin_id);
		iv_yonghu = (ImageView) findViewById(R.id.userportrait);

		tv_totalScore = (TextView) findViewById(R.id.jiangpin_yiyoujifen);

		// 获取用户信息并且设置
		ScoreHelper.getUserScoreSummary(new GetUserScoreSummaryCallback() {

			@Override
			public void onSuccess(String usrNickname, String usrStudentId, String avatarUrl, int totalScore) {
				tv_yonghu.setText(usrNickname);
				tv_xuehao.setText(usrStudentId);
				x.image().bind(iv_yonghu, avatarUrl);
				tv_totalScore.setText(String.valueOf(totalScore));
			}

			@Override
			public void onFailure(String errTip) {
				ToastUtils.showShortToast(errTip);
			}
		});
		findViewById(R.id.rl_jifenhead).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("info", "跳转");
				Intent intent = new Intent(JiangpinActivity.this, JifenActivity.class);
				startActivity(intent);
			}
		});

		mGridView = (GridView) findViewById(R.id.jifen_gridview);
	}

	private List<JiangpinCard> getItems() {
		List<JiangpinCard> mCards = new ArrayList<JiangpinCard>();
		for (int i = 0; i < imageList.size(); i++) {
			JiangpinCard mCard = new JiangpinCard(imageList.get(i).getName(),
					String.valueOf(imageList.get(i).getScore()), imageList.get(i).getImageUrl());
			mCards.add(mCard);
		}
		return mCards;
	}

	private void purchaseAlert(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请确认");
		// 设置对话框内的文本
		builder.setMessage("您确定要兑换该商品吗？");
		// 设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// 检查是否设置手机号
				if (AVUser.getCurrentUser() == null)
					return;
				if (TextUtils.isEmpty(AVUser.getCurrentUser().getMobilePhoneNumber())) {
					ToastUtils.showShortToast("请先设置您的手机号，以便我们联系您！");
					SetPhoneNumActivity.startThis(JiangpinActivity.this);
					return;
				}

				// 执行点击确定按钮的业务逻辑
				ScoreHelper.buyProduct(imageList.get(id).getProductId(), new BuyProductCallback() {
					@Override
					public void onFailure(String errorTip) {
						Toast.makeText(JiangpinActivity.this, errorTip, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess() {
						onBuySuccess(id);
					}
				});
			}
		});
		// 设置取消按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 执行点击取消按钮的业务逻辑
			}
		});
		// 使用builder创建出对话框对象
		AlertDialog dialog = builder.create();
		// 显示对话框
		dialog.show();
	}

	private void onBuySuccess(int id) {
		AVObject obj = new AVObject("DuiHuan");
		obj.put("user", AVUser.getCurrentUser());
		obj.put("product", imageList.get(id).getName());
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {

			}
		});
		Toast.makeText(JiangpinActivity.this, "您已经兑换成功", Toast.LENGTH_SHORT).show();
	}
}
