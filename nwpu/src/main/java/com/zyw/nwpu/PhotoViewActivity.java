package com.zyw.nwpu;

import com.zyw.nwpulib.photoiew.PhotoViewAttacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PhotoViewActivity extends Activity {

	private ImageView mImageView;
	private PhotoViewAttacher mAttacher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoview);

		// 初始化视图
		mImageView = (ImageView) findViewById(R.id.iv_photoview);
		mImageView.setVisibility(View.VISIBLE);

		int imageid = getIntent().getIntExtra("imageid", R.drawable.calendar1);
		mImageView.setImageResource(imageid);

		// 奇迹由这句话发生！
		mAttacher = new PhotoViewAttacher(mImageView);

		// 注册单击事件 单击退出
		mAttacher
				.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
					@Override
					public void onViewTap(View view, float x, float y) {
						PhotoViewActivity.this.finish();
						overridePendingTransition(R.anim.show_in,
								R.anim.zoom_out);
					}
				});
	}

	public static void startThis(Context cxt, int imageid) {
		Intent intent = new Intent(cxt, PhotoViewActivity.class);
		intent.putExtra("imageid", imageid);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}
}
