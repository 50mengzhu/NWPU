package com.zyw.nwpu;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.avos.avoscloud.AVGeoPoint;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.fragment.MultiStatusListFragment;
import com.zyw.nwpu.fragment.StatusListFragment;

/**
 * 本Activity不仅用于显示指定话题的帖子，还用于显示某一地点附近的帖子
 * 
 * @author Rocket
 *
 */
@ContentView(R.layout.activity_tag)
public class TagActivity extends BaseActivity implements OnClickListener {

	public static void startThis(Context cxt, String tag) {
		Intent intent = new Intent(cxt, TagActivity.class);
		intent.putExtra("tag", tag);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	public static void startThis(Context cxt, String position, double lng, double lat) {
		Intent intent = new Intent(cxt, TagActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("lng", lng);
		intent.putExtra("lat", lat);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	private MultiStatusListFragment statusListFragment;
	// private StatusListFragment statusListFragment;
	private String tag;
	private String position;
	private double lat, lng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tag = getIntent().getStringExtra("tag");
		position = getIntent().getStringExtra("position");
		lat = getIntent().getDoubleExtra("lat", 0);
		lng = getIntent().getDoubleExtra("lng", 0);

		statusListFragment = new MultiStatusListFragment();

		if (!TextUtils.isEmpty(tag)) {
			// statusListFragment.setTag(tag);

			TitleBar bar = (TitleBar) findViewById(R.id.head);
			bar.setTitle("#" + tag + "#");
			bar.setActionTextClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					BBSPublishActivity.startThis(TagActivity.this, tag);
				}
			});
		} else {
			// statusListFragment.setPosition(new AVGeoPoint(lat, lng));

			TitleBar bar = (TitleBar) findViewById(R.id.head);
			bar.setTitle(position + " 附近");
			bar.setActionTextClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					BBSPublishActivity.startThis(TagActivity.this);
				}
			});
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fl_tag_frag, statusListFragment).commit();
	}

	@Override
	public void initView() {
	}

	@Override
	public void onClick(View v) {
	}
}
