package com.zyw.nwpu.view;

import com.zyw.nwpu.tool.Bimp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 橡皮檫功能，类似刮刮乐效果
 * 
 * @author delmore 20110827
 */

// 设置的颜色必须要有透明度。
// ((Text_Rubbler) findViewById(R.id.rubbler)).beginRubbler(0xdddddddd, 10, 2f);

public class Text_Rubbler extends TextView {

	private float TOUCH_TOLERANCE; // 填充距离，使线条更自然，柔和,值越小，越柔和。

	// private final int bgColor;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Path mPath;
	private float mX, mY;

	private boolean isDraw = false;

	private Context mContext;

	public Text_Rubbler(Context context) {
		super(context);
		mContext = context;
	}

	public Text_Rubbler(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public Text_Rubbler(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isDraw) {
			mCanvas.drawPath(mPath, mPaint);
			// mCanvas.drawPoint(mX, mY, mPaint);
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}

	/**
	 * 开启檫除功能
	 */
	public void beginRubbler(int resourceimage) {

		final int paintStrokeWidth = 50;// 触点（橡皮）宽度
		float touchTolerance = 1f;// 填充距离,值越小，越柔和。

		TOUCH_TOLERANCE = touchTolerance;
		// 设置画笔
		mPaint = new Paint();
		mPaint.setAlpha(0);
		// 画笔划过的痕迹就变成透明色了
		mPaint.setColor(Color.WHITE); // 此处不能为透明色,别的颜色任意
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		// 或者
		// mPaint.setAlpha(0);
		// mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND); // 前圆角
		mPaint.setStrokeCap(Paint.Cap.ROUND); // 后圆角
		mPaint.setStrokeWidth(paintStrokeWidth); // 笔宽

		// 痕迹
		mPath = new Path();

		mBitmap = Bitmap.createBitmap(getLayoutParams().width,
				getLayoutParams().height, Config.ARGB_8888);

		mCanvas = new Canvas(mBitmap);

		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
				resourceimage);
		bitmap = Bimp.scaleBitmap(bitmap, getLayoutParams().width);
		mCanvas.drawBitmap(bitmap, 0, 0, null);
		isDraw = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isDraw) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 触点按下
			// touchDown(event.getRawX(),event.getRawY());
			touchDown(event.getX(), event.getY());
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE: // 触点移动
			touchMove(event.getX(), event.getY());
			invalidate();
			break;
		case MotionEvent.ACTION_UP: // 触点弹起
			touchUp(event.getX(), event.getY());
			invalidate();
			break;
		default:
			break;
		}
		return true;
	}

	private void touchDown(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}

	}

	private void touchUp(float x, float y) {
		mPath.lineTo(x, y);
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}
}
