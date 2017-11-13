package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.easyway.barcode.PhonegapBarcodeActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.CalendarActivity;
import com.zyw.nwpu.ChaoqitixingActivity;
import com.zyw.nwpu.QiandaoActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.RobotActivity;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.TushuguanActivity;
import com.zyw.nwpu.WebViewActivity;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.appcenter.Allowance;
import com.zyw.nwpu.appcenter.GradeActivity;
import com.zyw.nwpu.appcenter.GraduateGrade;
import com.zyw.nwpu.appcenter.RuijiActivity;
import com.zyw.nwpu.appcenter.UndergraduateGrade;
import com.zyw.nwpu.appcenter.YktLoginActivity;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.service.AppCenterService;
import com.zyw.nwpu.service.SignService;
import com.zyw.nwpu.service.AppCenterService.OnGetImagesCallback;
import com.zyw.nwpu.service.SignService.OnCheckSignListener;
import com.zyw.nwpu.service.SignService.OnGetCurrentUserSignStatisticsListener;
import com.zyw.nwpu.service.SignService.OnGetRankListListener;
import com.zyw.nwpu.service.SignService.OnSignListener;
import com.zyw.nwpu.service.SignService.SignBean;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpu.wlan.WLANLoginActivity;
import com.zyw.nwpu.xmz.XMZActivity;
import com.zyw.nwpulib.model.ScrollImageEntity;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ScreenUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class App_Fragment extends Fragment {
	private View rootView;

	private ViewPager adViewPager;
	private List<View> pageViews;
	private AdPageAdapter adapter;
	private List<ScrollImageEntity> scrollImageList;// 轮播图列表
	private ImageView[] imageViews;// 小圆点

	private RelativeLayout head;

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	private boolean isContinue = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_app, container, false);
		initView();
		getScrollImages();
		return rootView;
	}

	public void initView() {
		rootView.findViewById(R.id.ll_tushuguan).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_yikatong).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_wifi).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_chengji).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_qiandao).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_xiaoche).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_xiaoli).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_chaoqitixing).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_xiangmuzhi).setOnClickListener(new ClickListener());

		rootView.findViewById(R.id.ll_cjcx).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_ykt).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_paocao).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_cj).setOnClickListener(new ClickListener());
		rootView.findViewById(R.id.ll_jzj).setOnClickListener(new ClickListener());

		// 成绩和项目制先不显示
		rootView.findViewById(R.id.ll_ykt).setVisibility(View.GONE);
		// rootView.findViewById(R.id.ll_xiangmuzhi).setVisibility(View.GONE);
		// rootView.findViewById(R.id.ll_qiandao).setVisibility(View.GONE);

		rootView.findViewById(R.id.ll_cj).setVisibility(View.GONE);

		x.http().get(new RequestParams("http://222.24.192.175/npulife_api/judge_date.php"),
				new CommonCallback<String>() {

					@Override
					public void onCancelled(CancelledException arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(Throwable arg0, boolean arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFinished() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(String arg0) {
						if (TextUtils.equals(arg0, "1")) {
							rootView.findViewById(R.id.ll_cj).setVisibility(View.VISIBLE);
						}
					}
				});

		initViewPager();
	}

	@SuppressWarnings("deprecation")
	private void initViewPager() {
		adViewPager = (ViewPager) rootView.findViewById(R.id.app_view_pager);
		pageViews = new ArrayList<View>();
		adapter = new AdPageAdapter(pageViews);
		adViewPager.setAdapter(adapter);
		adViewPager.setOnPageChangeListener(new AdPageChangeListener());

		head = (RelativeLayout) rootView.findViewById(R.id.head);
		head.setVisibility(View.GONE);
	}

	/**
	 * 获取轮播图列表
	 */
	private void getScrollImages() {
		AppCenterService.getImageList(new OnGetImagesCallback() {

			@Override
			public void onSuccess(List<ScrollImageEntity> imageList) {
				// 获取成功
				if (imageList == null || imageList.size() == 0)
					return;

				scrollImageList = new ArrayList<ScrollImageEntity>();
				for (int i = 0; i < imageList.size(); i++) {
					scrollImageList.add(imageList.get(i));
					ImageView img = new ImageView(rootView.getContext());
					img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					img.setAdjustViewBounds(true);
					img.setMaxWidth(ScreenUtils.getScreenWidth(getActivity()));
					img.setMaxHeight(ScreenUtils.getScreenHeight(getActivity()));
					img.setOnClickListener(new ClickListener(i));
					img.setScaleType(ScaleType.CENTER_CROP);
					ImageLoader.getInstance().displayImage(imageList.get(i).getImgUrl(), img, Options.getListOptions());
					pageViews.add(img);
				}
				adapter.notifyDataSetChanged();

				head.setVisibility(View.VISIBLE);

				// 显示小圆点
				initCirclePoint();

				// 开始自动切换图片
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							if (isContinue) {
								viewHandler.sendEmptyMessage(atomicInteger.get());
								atomicOption();
							}
						}
					}
				}).start();
			}

			@Override
			public void onFailed(String errorTip) {
				// 获取失败，这里没有任何操作

			}
		});
	}

	private void atomicOption() {
		atomicInteger.incrementAndGet();
		if (atomicInteger.get() > imageViews.length - 1) {
			atomicInteger.getAndAdd(-5);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
	}

	/*
	 * 每隔固定时间切换广告栏图片
	 */
	@SuppressLint("HandlerLeak")
	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			adViewPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};

	private void initCirclePoint() {
		ViewGroup group = (ViewGroup) rootView.findViewById(R.id.viewGroup);
		imageViews = new ImageView[pageViews.size()];
		// 广告栏的小圆点图标
		for (int i = 0; i < pageViews.size(); i++) {
			// 创建一个ImageView, 并设置宽高. 将该对象放入到数组中
			ImageView imageView = new ImageView(rootView.getContext());
			imageView.setLayoutParams(new LayoutParams(10, 10));
			imageViews[i] = imageView;

			// 初始值, 默认第0个选中
			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.point_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.point_unfocused);
			}
			// 将小圆点放入到布局中
			group.addView(imageViews[i]);
		}
	}

	private final class ClickListener implements OnClickListener {

		private int mPosition;

		public ClickListener() {
		}

		public ClickListener(int mPosition) {
			this.mPosition = mPosition;
		}

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_qiandao:
				QiandaoActivity.startThis(getActivity());
				break;
			case R.id.ll_chengji:
				RobotActivity.startThis(getActivity());
				// 成绩查询
				// WebViewActivity.startThis(getActivity(), Const.npu_lib,
				// "本科生成绩查询");
				// WebViewActivity.startThis(getActivity(), Const.cource,
				// "研究生课程");
				break;
			case R.id.ll_chaoqitixing:
				ChaoqitixingActivity.startThis(getActivity());
				break;
			case R.id.ll_tushuguan:
				// 图书馆
				TushuguanActivity.startThis(getActivity(), Const.npu_lib, "图书馆");
				// WebViewActivity.startThis(getActivity(), Const.npu_lib,
				// "图书馆");
				break;
			case R.id.ll_wifi:
				// wlan
				WLANLoginActivity.startThis(getActivity());
				break;

			case R.id.ll_xiaoche:
				// 校车时刻
				WebViewActivity.startThis(getActivity(), Const.schoolbus, "校车时刻");
				break;
			case R.id.ll_xiaoli:
				// 校历
				toCalendar();
				break;

			case R.id.ll_yikatong:
				// 一卡通流水
				RuijiActivity.startThis(getActivity());
				break;

			case R.id.ll_xiangmuzhi:
				PhonegapBarcodeActivity.startThis(getActivity());
				break;

			case R.id.ll_cjcx:
				GradeActivity.startThis(getActivity());
				break;

			case R.id.ll_jzj:
				// 奖助金查询
				Allowance.startThis(getActivity());
				break;

			case R.id.ll_ykt:
				YktLoginActivity.startThis(getActivity());
				break;

			case R.id.ll_cj:
				toChouJiang();
				break;

			case R.id.ll_paocao:
				break;
			}
			if (v instanceof ImageView) {
				clickImage(mPosition);
			} else {
				return;
			}
		}
	}

	private void clickImage(int position) {
		String url = scrollImageList.get(position).getLinkUrl();
		if (!TextUtils.isEmpty(url))
			WebViewActivity.startThis(getActivity(), url, "");
		// AppCenterService.addImageClickNum(scrollImageList.get(position).getId());
	}

	public void toChouJiang() {
		// 判断是否已经登录
		if (!AccountHelper.isLogedIn(getActivity()) || AVUser.getCurrentUser() == null) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "请先登录");
			Login.startThis(getActivity());
		} else {
			String url = "http://222.24.192.175/npulife_api/choujiang/index.php?student_id="
					+ AVUser.getCurrentUser().getString(XUser.STUDENTID);
			WebViewActivity.startThis(getActivity(), url, "圣诞抽奖");
		}
	}

	private void toCalendar() {
		int w = CommonUtil.ScreenUtils.getScreenWidth(getActivity().getApplicationContext());
		CalendarActivity.startThis(getActivity(), w);
	}

	/**
	 * ViewPager 页面改变监听器
	 */

	@SuppressLint("NewApi")
	private final class AdPageChangeListener implements OnPageChangeListener {

		/**
		 * 页面滚动状态发生改变的时候触发
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		/**
		 * 页面滚动的时候触发
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		/**
		 * 页面选中的时候触发
		 */
		@Override
		public void onPageSelected(int arg0) {
			// 获取当前显示的页面是哪个页面
			atomicInteger.getAndSet(arg0);
			// 重新设置原点布局集合
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.point_focused);

				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.point_unfocused);
				}
			}
		}
	}

	private final class AdPageAdapter extends PagerAdapter {
		private List<View> views = null;

		/**
		 * 初始化数据源, 即View数组
		 */
		public AdPageAdapter(List<View> views) {
			this.views = views;
		}

		/**
		 * 从ViewPager中删除集合中对应索引的View对象
		 */
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		/**
		 * 获取ViewPager的个数
		 */
		@Override
		public int getCount() {
			return views.size();
		}

		/**
		 * 从View集合中获取对应索引的元素, 并添加到ViewPager中
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position), 0);
			return views.get(position);
		}

		/**
		 * 是否将显示的ViewPager页面与instantiateItem返回的对象进行关联 这个方法是必须实现的
		 */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
}
