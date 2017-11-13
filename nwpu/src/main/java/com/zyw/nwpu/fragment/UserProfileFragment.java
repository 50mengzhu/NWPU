package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.AVException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zyw.nwpu.BBSCommentActivity;
import com.zyw.nwpu.MainTabActivity;
import com.zyw.nwpu.MsgActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.SettingsActivity;
import com.zyw.nwpu.adapter.MyStatusAdapter;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.BBSService.GetVisitCountCallback;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.view.SelectableRoundedImageView;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

public class UserProfileFragment extends Fragment implements IXListViewListener {

	private static final int NUM = 10;
	private View view;

	private List<StatusData> mData;// 与ListView绑定的数据
	private MyStatusAdapter mAdapter;
	private AVUser user;

	private TitleBar titleBar;
	private RelativeLayout titleTab;// title下面的tab条
	private View headview;// 头部
	private RelativeLayout headTab;// 头部背景图下面的tab条
	private LinearLayout userInfoView;// 用户信息列表
	private XListView mListView;
	private TextView tv_pleaselogin;
	private TextView tv_visitcount;// 主页访问量

	// 下划线
	private View title_bottombar_userinfo, title_bottombar_userstatus;
	private View head_bottombar_userinfo, head_bottombar_userstatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_me2, container, false);
		initView();
		return view;
	}

	@Override
	public void onResume() {
		checkLogin();
		super.onResume();
	}

	private void checkLogin() {
		// 这里要另开线程
		if (AccountHelper.isLogedIn(getActivity().getApplicationContext())) {
			// 已登录
			checkLoginHandler.sendEmptyMessage(1);
		} else {
			checkLoginHandler.sendEmptyMessage(0);// 未登录
		}
	}

	private Handler checkLoginHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null)
				return;
			if (msg.what == 1) {
				showLoginState();
			} else if (msg.what == 0) {
				showNotLoginState();
			}
		}
	};

	/**
	 * 显示已经登录的布局
	 */
	private void showLoginState() {
		tv_pleaselogin.setVisibility(View.GONE);
		tv_pleaselogin.setOnClickListener(null);

		titleBar.setVisibility(View.GONE);

		if (AVUser.getCurrentUser() != null) {
			user = AVUser.getCurrentUser();

			// 显示用户信息
			showUserInfo(user);

			// 获取状态
			getMyStatusByRelation(0, NUM, false);
		}
	}

	/**
	 * 显示未登录的布局
	 */
	private void showNotLoginState() {
		tv_pleaselogin.setVisibility(View.VISIBLE);
		titleBar.setVisibility(View.VISIBLE);

		tv_pleaselogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 进入单点登录
				Login.startThis(getActivity());
			}
		});
	}

	/**
	 * 将用户信息显示在控件上
	 * 
	 * @param user
	 */
	private void showUserInfo(AVUser user) {
		String nickname = user.getString(XUser.NICKNAME);
		int int_gender = user.getInt(XUser.GENDER);
		String hometown = user.getString(XUser.HOMETOWN);
		String college = user.getString(XUser.COLLEGE);
		String birthday = user.getString(XUser.BIRTHDAY);
		String xingzuo = CommonUtil.DateUtils.getConstellation(birthday);
		String relationship = user.getString(XUser.RELATIONSHIP_STATE);
		String headImageUrl = "";
		if (user.getAVFile("image") != null) {
			headImageUrl = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE,
					50, "jpg");
		}

		// 昵称
		titleBar.setTitle(nickname);
		TextView tv_nickname = (TextView) headview.findViewById(R.id.tv_nickname);
		tv_nickname.setText(nickname);

		// 头像
		SelectableRoundedImageView iv_avatar = (SelectableRoundedImageView) headview.findViewById(R.id.iv_avatar);
		ImageLoader.getInstance().displayImage(headImageUrl, iv_avatar, Options.getListOptions());

		// 性别
		ImageView iv_gender = (ImageView) headview.findViewById(R.id.iv_gender);
		if (int_gender == 0)
			iv_gender.setImageResource(R.drawable.ic_girl);
		else
			iv_gender.setImageResource(R.drawable.ic_boy);

		// 学院
		TextView tv_college_edit = (TextView) userInfoView.findViewById(R.id.tv_college_edit);
		tv_college_edit.setText(college);

		// 生日
		TextView tv_birthday_edit = (TextView) userInfoView.findViewById(R.id.tv_birthday_edit);
		tv_birthday_edit.setText(birthday);

		// 星座
		TextView tv_xingzuo_edit = (TextView) userInfoView.findViewById(R.id.tv_xingzuo_edit);
		tv_xingzuo_edit.setText(xingzuo);

		// 家乡
		TextView tv_hometown_edit = (TextView) userInfoView.findViewById(R.id.tv_hometown_edit);
		tv_hometown_edit.setText(hometown);

		// 情感
		// TODO
		TextView tv_relation_edit = (TextView) userInfoView.findViewById(R.id.tv_relationship_edit);
		tv_relation_edit.setText(relationship);

		// 访问量
		BBSService.getVisitCount(user, new GetVisitCountCallback() {

			@Override
			public void onSuccess(int count) {
				if (count == 0) {
					tv_visitcount.setVisibility(View.GONE);
				} else {
					tv_visitcount.setText(String.valueOf(count));
					tv_visitcount.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFailed(String errorTip) {
				tv_visitcount.setVisibility(View.GONE);
			}
		});
	}

	private void showUserStatuses() {
		mListView.removeHeaderView(userInfoView);

		mAdapter.setBlock(false);
		mAdapter.notifyDataSetChanged();
		mListView.setPullLoadEnable(true);

		title_bottombar_userinfo.setVisibility(View.INVISIBLE);
		head_bottombar_userinfo.setVisibility(View.INVISIBLE);

		title_bottombar_userstatus.setVisibility(View.VISIBLE);
		head_bottombar_userstatus.setVisibility(View.VISIBLE);
	}

	private void showUserInfo() {
		mListView.addHeaderView(userInfoView);

		mAdapter.setBlock(true);
		mAdapter.notifyDataSetChanged();
		mListView.setPullLoadEnable(false);

		title_bottombar_userinfo.setVisibility(View.VISIBLE);
		head_bottombar_userinfo.setVisibility(View.VISIBLE);

		title_bottombar_userstatus.setVisibility(View.INVISIBLE);
		head_bottombar_userstatus.setVisibility(View.INVISIBLE);
	}

	public void initView() {
		iniTitleBar();
		iniHeadView();
		iniUserInfoList();
		iniListView();

		tv_pleaselogin = (TextView) view.findViewById(R.id.tv_pleaselogin);

		// 显示下划线
		title_bottombar_userinfo.setVisibility(View.VISIBLE);
		head_bottombar_userinfo.setVisibility(View.VISIBLE);
		title_bottombar_userstatus.setVisibility(View.INVISIBLE);
		head_bottombar_userstatus.setVisibility(View.INVISIBLE);

		showNotLoginState();
	}

	/**
	 * 初始化标题
	 */
	private void iniTitleBar() {
		titleBar = (TitleBar) view.findViewById(R.id.head);
		titleBar.setMoreIconVisible(true);
		titleBar.setMoreIconResource(R.drawable.ic_setting);
		titleBar.setMoreIconClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 进入设置页面
				toSetting();
			}
		});
		titleBar.setBackButtonVisible(true);
		titleBar.setBackIconClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 进入消息界面
				MsgActivity.startThis(getActivity());
			}
		});

		titleTab = (RelativeLayout) view.findViewById(R.id.title_tab);
		titleTab.setVisibility(View.GONE);
		title_bottombar_userinfo = (View) view.findViewById(R.id.bar_userinfo);
		title_bottombar_userstatus = (View) view.findViewById(R.id.bar_userstatus);
		titleTab.findViewById(R.id.rl_userpublish).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserStatuses();
			}
		});
		titleTab.findViewById(R.id.rl_userinfo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserInfo();
			}
		});

	}

	@SuppressLint("InflateParams")
	private void iniHeadView() {

		// 用户信息头部
		headview = getActivity().getLayoutInflater().inflate(R.layout.userprofile_head, null);
		headview.setOnClickListener(null);
		headTab = (RelativeLayout) (headview.findViewById(R.id.head_tab));
		headview.findViewById(R.id.rl_userpublish).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserStatuses();
			}
		});
		headview.findViewById(R.id.rl_userinfo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserInfo();
			}
		});

		// 左边的图标
		ImageView iv_left = (ImageView) headview.findViewById(R.id.iv_back_head);
		iv_left.setImageResource(R.drawable.ic_title_msg);// 消息图标
		headview.findViewById(R.id.iv_back_head).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 进入消息界面
				MsgActivity.startThis(getActivity());
			}
		});

		// 右边的图标
		ImageView iv_right = (ImageView) headview.findViewById(R.id.iv_right_icon);
		iv_right.setImageResource(R.drawable.ic_setting);// 设置图标
		iv_right.setVisibility(View.VISIBLE);
		headview.findViewById(R.id.iv_right_icon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toSetting();
			}
		});

		// 右边第二个图标
		tv_visitcount = (TextView) headview.findViewById(R.id.tv_visitcount);
		tv_visitcount.setVisibility(View.GONE);

		// tab下面的下划线
		head_bottombar_userinfo = (View) headview.findViewById(R.id.bar_userinfo);
		head_bottombar_userstatus = (View) headview.findViewById(R.id.bar_userstatus);
	}

	@SuppressLint("InflateParams")
	private void iniUserInfoList() {
		// 用户信息列表
		userInfoView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.userprofile_userinfo, null);
		userInfoView.setVisibility(View.VISIBLE);
		Button btn_sendmsg = (Button) userInfoView.findViewById(R.id.btn_chat);
		btn_sendmsg.setVisibility(View.GONE);

	}

	@SuppressLint("InflateParams")
	private void iniListView() {
		mData = new ArrayList<StatusData>();// 数据集合
		mAdapter = new MyStatusAdapter(getActivity(), mData, R.layout.item_timeline);// 适配器

		mListView = (XListView) view.findViewById(R.id.lv_mystatus);// 列表
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				View c = mListView.getChildAt(0);
				int total = c.getHeight() - titleBar.getHeight() - headTab.getHeight();
				int top = c.getTop();
				top = -top;
				float alpha = (float) ((double) top / total);

				if (alpha > 1)
					alpha = 1;

				if (arg1 >= 2)
					alpha = 1;

				if (alpha == 1) {
					titleTab.setVisibility(View.VISIBLE);
				} else {
					titleTab.setVisibility(View.GONE);
				}
				titleBar.setAlpha(alpha);
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				toCommentActivity(arg2 - 2);
			}
		});
		mListView.addHeaderView(headview);
		mListView.addHeaderView(userInfoView);
	}

	private void getMyStatusByRelation(int skip, int limit, final boolean is_loadmore) {

		// 获取发表记录
		AVRelation<Status> statuses = user.getRelation(XUser.ANNOUNCE);

		if (null == statuses.getTargetClass()) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "没有发布记录哦，快去发布一条吧");
			return;
		}

		AVQuery<Status> query = statuses.getQuery();
		query.orderByDescending(XUser.CREATEDAT);
		query.include(XUser.CREATOR);
		query.setSkip(skip);
		query.whereEqualTo("isShow", true);// 不显示已经删除的
		query.setLimit(limit);
		query.findInBackground(new FindCallback<Status>() {

			@Override
			public void done(List<Status> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), arg1.getLocalizedMessage());
					mListView.stopLoadMore();
					return;
				}
				if (arg0 == null || arg0.size() == 0) {
					mListView.stopLoadMore();
				} else {
					if (!is_loadmore) {
						mData.clear();
					}

					for (int i = 0; i < arg0.size(); i++) {
						AVObject item = arg0.get(i);
						mData.add(BBSService.transformStatusData(getActivity(), (Status) item));
					}

					mAdapter.notifyDataSetChanged();
					mListView.stopLoadMore();
				}
			}
		});
	}

	protected void toCommentActivity(final int position) {
		BBSCommentActivity.startThis(getActivity(), mData.get(position).AVObjectID);
	}

	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
		if (mData == null || mData.size() == 0) {
			mListView.stopLoadMore();
			return;
		}
		getMyStatusByRelation(mData.size(), NUM, true);
	}

	private void toSetting() {
		Intent intent = new Intent(getActivity(), SettingsActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}
}
