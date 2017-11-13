package com.zyw.nwpu.listener;

import com.zyw.nwpu.base.BaseActivity;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * 返回手势监听接口
 * 
 * @author Rocket
 * 
 */
public class BackGestureListener implements OnGestureListener {
	BaseActivity activity;

	public BackGestureListener(BaseActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// 返回手势的判别标准
		if ((e2.getX() - e1.getX()) > 200
				&& Math.abs(e1.getY() - e2.getY()) < 60) {
			activity.onBackPressed();
			return true;
		}
		
//		if (Math.abs((e2.getX() - e1.getX())) < 40
//				&& e1.getY() - e2.getY() > 40) {
//			activity.onMoveUp();
//			return true;
//		}
//		
//		if (Math.abs((e2.getX() - e1.getX())) < 40
//				&& e2.getY() - e1.getY() > 40) {
//			activity.onMoveDown();
//			return true;
//		}
		
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
