package com.zyw.nwpu;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpulib.photoiew.PhotoViewAttacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FullScreenPhotoViewActivity extends Activity {

	private ImageView mImageView;

	private PhotoViewAttacher mAttacher;

	// 加载图片所需的变量
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options = Options.getListOptions();

	private ProgressBar progressBar;

	private ImageView iv_save;

	public static void startThis(Context cxt, String imgUrl) {
		Intent intent = new Intent(cxt, FullScreenPhotoViewActivity.class);
		intent.putExtra("imageurl", imgUrl);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbs_photoview);
		mImageView = (ImageView) findViewById(R.id.iv_photoview);
		mImageView.setVisibility(View.GONE);
		progressBar = (ProgressBar) findViewById(R.id.pb_photoview);
		progressBar.setVisibility(View.VISIBLE);

		iv_save = (ImageView) findViewById(R.id.iv_save);
		iv_save.setVisibility(View.GONE);
		iv_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mImageView.setDrawingCacheEnabled(true);
				Bitmap imageBitmap = mImageView.getDrawingCache();
				if (imageBitmap != null) {
					new SaveImageTask().execute(imageBitmap);
				}
			}
		});

		// 下载图片
		Intent intent = this.getIntent();
		String imageurl = intent.getStringExtra("imageurl");

		mImageLoader.displayImage(imageurl, mImageView, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub

						progressBar.setVisibility(View.GONE);
						iv_save.setVisibility(View.VISIBLE);
						mImageView.setVisibility(View.VISIBLE);
						// 奇迹由这句话发生！
						mAttacher = new PhotoViewAttacher(mImageView);

						// 注册单击事件 单击退出
						mAttacher
								.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
									@Override
									public void onViewTap(View view, float x,
											float y) {
										FullScreenPhotoViewActivity.this.finish();
										overridePendingTransition(
												R.anim.show_in, R.anim.zoom_out);
									}
								});
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});
	}

	private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
		@Override
		protected String doInBackground(Bitmap... params) {
			String result = getResources().getString(
					R.string.save_picture_failed);
			try {
				String sdcard = Environment.getExternalStorageDirectory()
						.toString();

				File file = new File(sdcard + "/Download");
				if (!file.exists()) {
					file.mkdirs();
				}

				File imageFile = new File(file.getAbsolutePath(),
						new Date().getTime() + ".jpg");
				FileOutputStream outStream = null;
				outStream = new FileOutputStream(imageFile);
				Bitmap image = params[0];
				image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				result = getResources().getString(
						R.string.save_picture_success, file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();

			mImageView.setDrawingCacheEnabled(false);
		}
	};
}
