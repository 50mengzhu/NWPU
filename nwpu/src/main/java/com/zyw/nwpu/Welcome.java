package com.zyw.nwpu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.PushService;
import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.bean.ChannelManage;
import com.zyw.nwpu.jifen.leancloud.Product;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.OnGetProductsCallback;
import com.zyw.nwpu.service.SplashHelper;
import com.zyw.nwpu.service.SplashHelper.OnGetSplashCallback;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpu.wlan.WLANLoginActivity;
import com.zyw.nwpu.wlan.WifiServer;
import com.zyw.nwpulib.utils.SPUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@ContentView(R.layout.welcome)
public class Welcome extends Activity {

	private ImageView iv_welcome;

	@ViewInject(R.id.rl_foot)
	private RelativeLayout rl_foot;

	private int animTime = 800;

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, Welcome.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

		// 开启推送服务
		PushService.setDefaultPushCallback(this, DetailsActivity.class);
		PushService.subscribe(this, "public", DetailsActivity.class);
		PushService.subscribe(this, "private", DetailsActivity.class);
		PushService.subscribe(this, "protected", DetailsActivity.class);

		// 动画
		iv_welcome = (ImageView) findViewById(R.id.iv_id1);

		// 底部阴影
		rl_foot.setVisibility(View.GONE);

		// int pic_id = (int) (Math.random() * 5);
		iv_welcome.setImageResource(R.drawable.pic_welcome);
		// iv_welcome.setImageResource(pic[pic_id]);

		startScaleAnim();

		// 创建WiFi快捷方式
		// if (AccountHelper.isFirstLaunch(getApplicationContext()))
		// WifiServer.createShortCut(Welcome.this);

		SplashHelper.getSplash(new OnGetSplashCallback() {

			@Override
			public void onSuccess(boolean isVideo, String url) {
				if (!isVideo) {
					animTime += 2500;// 多等待两秒
					// 加载广告图片
					ImageLoader.getInstance().displayImage(url, iv_welcome, Options.getListOptions());
				}
			}

			@Override
			public void onFailure(String errTip) {

			}
		});
	}

	private void startScaleAnim() {
		// ScaleAnimation animation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
		// Animation.RELATIVE_TO_SELF, 0.4f, Animation.RELATIVE_TO_SELF,
		// 0.4f);
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(animTime);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		iv_welcome.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (AccountHelper.isFirstLaunch(getApplicationContext())) {
					// 首次运行
					AccountHelper.setAlreadyFirstLaunched(getApplicationContext());

					// 首次运行，初始化频道选择
					ChannelManage.getManage(AppApplication.getApp().getSQLHelper()).initDefaultChannel();
					GuideActivity.startThis(Welcome.this);
					finish();
				} else {
					if (AccountHelper.isLogedIn(getApplicationContext())) {
						// ** 免登陆情况 加载所有本地会话
						// 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
						// 加上的话保证进了主页面会话和群组都已经load完毕
						ToastUtils.showShortToast("这里");
						long start = System.currentTimeMillis();
						EMChatManager.getInstance().loadAllConversations();
						long costTime = System.currentTimeMillis() - start;
						// 等待sleeptime时长
						if (animTime - costTime > 0) {
							try {
								Thread.sleep(animTime - costTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// 进入主页面
						// RobotActivity.startThis(Welcome.this);
						startActivity(new Intent(Welcome.this, MainTabActivity.class));
						// startActivity(new Intent(Welcome.this,
						// MainTabActivity.class));
						finish();
					} else {
						try {
							Thread.sleep(animTime);
						} catch (InterruptedException e) {
						}
						// 进入单点登录界面
						startActivity(new Intent(Welcome.this, MainTabActivity.class));
						finish();
					}
				}
			}
		}).start();
	}
}
