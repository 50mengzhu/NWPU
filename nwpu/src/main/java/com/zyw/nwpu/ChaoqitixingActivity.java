package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.service.ChaoqitixingService;
import com.zyw.nwpu.service.ChaoqitixingService.ChaoQiInfo;
import com.zyw.nwpu.service.ChaoqitixingService.GetChaoqiInfoCallback;
import com.zyw.nwpu.view.SwitchView;
import com.zyw.nwpu.view.SwitchView.OnStateChangedListener;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.SPUtils;

@ContentView(R.layout.activity_chaoqitixing)
public class ChaoqitixingActivity extends BaseActivity {

	private SwitchView switchView;
	private final String KEY = "isOpenChaoqitixing";
	private TextView tv_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		switchView = (SwitchView) findViewById(R.id.switchView);
		tv_info = (TextView) findViewById(R.id.tv_info);

		if (!SPUtils.contains(getApplicationContext(), KEY)) {
			switchView.toggleSwitch(true);
			SPUtils.put(getApplicationContext(), KEY, true);
		} else {
			boolean isOpen = (boolean) SPUtils.get(getApplicationContext(), KEY, true);
			switchView.toggleSwitch(isOpen);
		}

		switchView.setOnStateChangedListener(new OnStateChangedListener() {

			@Override
			public void toggleToOn(View view) {
				SPUtils.put(getApplicationContext(), KEY, true);
				switchView.toggleSwitch(true);
			}

			@Override
			public void toggleToOff(View view) {
				SPUtils.put(getApplicationContext(), KEY, false);
				switchView.toggleSwitch(false);
			}
		});

		if (AVUser.getCurrentUser() != null) {
			ChaoqitixingService.getChaoqiInfo(null, AVUser.getCurrentUser().getString(XUser.STUDENTID),
					new GetChaoqiInfoCallback() {

						@Override
						public void onSuccess(ChaoQiInfo data) {
							if (data != null) {
								String text = "您有" + data.getUnhandledOverdueBookNum() + "条欠款记录未处理，共" + data.getMoney()
										+ "元";
								int num = Integer.parseInt(data.getOverdueBookNum());
								if (num != 0) {
									text += "\n当前有" + data.getOverdueBookNum() + "本书超期，请及时归还。";
								}
								tv_info.setText(text);
							}
						}

						@Override
						public void onFailed(String errorTip) {
							ToastUtils.showShortToast(getApplicationContext(), errorTip);
						}
					});
		}
	}

	@Override
	public void initView() {
	}

	public static void startThis(Context cxt) {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(cxt)) {
			CommonUtil.ToastUtils.showShortToast(cxt, "请先登陆");
			Login.startThis(cxt);
		} else {
			Intent intent = new Intent(cxt, ChaoqitixingActivity.class);
			cxt.startActivity(intent);
			((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
		}
	}
}
