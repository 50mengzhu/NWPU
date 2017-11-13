package com.zyw.nwpu.xmz;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zyw.nwpu.R;
import com.zyw.nwpu.TagActivity;
import com.zyw.nwpu.adapter.StatusAdapter;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;
import com.zyw.nwpu.xmz.XmzHelper.OnComplete;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_xmz_scan)
public class ScanActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initView() {
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, ScanActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
				R.anim.fade_outs);
	}

}
