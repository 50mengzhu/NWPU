package com.zyw.nwpu.wlan;

import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.MainTabActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

@ContentView(R.layout.activity_wlan_login_shortcut)
public class ShortCutWLANLoginActivity extends BaseActivity {

	// private ImageView iv_handimg;
	// private ImageView iv_wifiimg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// iv_handimg = (ImageView) findViewById(R.id.iv_handimg);
		// iv_wifiimg = (ImageView) findViewById(R.id.iv_wifiimg);

		// startShakeAnim();
		// startFrameAnim();
	}

	// private boolean isStopShake = false;

	private void startShakeAnim() {

		final RotateAnimation animation1 = new RotateAnimation(-2f, 2f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation1.setDuration(30);// 设置动画持续时间
		animation1.setRepeatMode(Animation.REVERSE);
		animation1.setRepeatCount(10);
		animation1.setFillAfter(false);// 动画执行完后是否停留在执行完的状态

		// RotateAnimation anim = new RotateAnimation(-30.0f, 30.0f);
		RotateAnimation anim1 = new RotateAnimation(0.0f, 50.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		RotateAnimation anim2 = new RotateAnimation(50.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		RotateAnimation anim3 = new RotateAnimation(0.0f, 50.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		RotateAnimation anim4 = new RotateAnimation(50.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);

		anim1.setDuration(300);
		anim2.setDuration(300);
		anim3.setDuration(150);
		anim4.setDuration(150);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(animation1);
		set.setStartOffset(500);

		// iv_handimg.startAnimation(set);
		set.start();
	}

	// private AnimationDrawable animationDrawable;

	private void startFrameAnim() {
		// iv_wifiimg.setImageResource(R.anim.frame_anim);
		// animationDrawable = (AnimationDrawable) iv_wifiimg.getDrawable();
		// animationDrawable.start();
	}

	@Override
	public void onBackPressed() {
		toMain();
	}

	private void toWLANLogin() {
		WLANLoginActivity.startThis(ShortCutWLANLoginActivity.this);
		finish();
	}

	private void toMain() {
		startActivity(new Intent(ShortCutWLANLoginActivity.this, MainTabActivity.class));
		finish();
	}
}
