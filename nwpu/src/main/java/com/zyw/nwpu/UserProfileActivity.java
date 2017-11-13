package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.zyw.nwpu.adapter.MyStatusAdapter;
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

@ContentView(R.layout.activity_userprofile)
public class UserProfileActivity extends BaseActivity implements IXListViewListener, View.OnClickListener {

	private static final int NUM = 10;

	private List<StatusData> mData;// 与ListView绑定的数据
	private MyStatusAdapter mAdapter;
	private AVUser user;
	private String userid;
	private String nickname;
	private String studentId;

	private TitleBar titleBar;
	private RelativeLayout titleTab;// title下面的tab条
	private View headview;// 头部
	private RelativeLayout headTab;// 头部背景图下面的tab条
	private LinearLayout userInfoView;// 用户信息列表
	private XListView mListView;
	private Button btn_sendmsg;// 发送信息按钮
	private TextView tv_visitcount;// 主页访问量

	SelectableRoundedImageView iv_avatar;

	// 下划线
	private View title_bottombar_userinfo, title_bottombar_userstatus;
	private View head_bottombar_userinfo, head_bottombar_userstatus;

	public static void startThis(Context cxt, String userid) {
		Intent intent = new Intent(cxt, UserProfileActivity.class);
		intent.putExtra("userid", userid);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	public static void startThisWithStudentId(Context cxt, String studentId) {
		Intent intent = new Intent(cxt, UserProfileActivity.class);
		intent.putExtra("studentId", studentId);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	/**
	 * 添加访问记录
	 * 
	 * @param tInstall
	 */
	public void addVisit(AVUser user) {
		BBSService.addVisit(user);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 获取数据
		userid = getIntent().getStringExtra("userid");
		studentId = getIntent().getStringExtra("studentId");

		if (!TextUtils.isEmpty(userid)) {
			getUserInfo(userid);
		} else if (!TextUtils.isEmpty(studentId)) {
			getUserInfoByStudentId(studentId);
		}
	}

	private void getUserInfo(String userId) {
		AVQuery<AVUser> query = new AVQuery<AVUser>(XUser.CLASSNAME);
		query.whereEqualTo(XUser.OBJID, userId);
		query.include(XUser.HEADIMG);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() != 1) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "获取个人信息失败");
					return;
				}

				// 获取到用户
				user = arg0.get(0);
				addVisit(user);
				showUserInfo(user);

				// 获取状态
				getMyStatusByRelation(0, NUM, false);
			}
		});
	}

	private void getUserInfoByStudentId(String studentID) {
		AVQuery<AVUser> query = new AVQuery<AVUser>(XUser.CLASSNAME);
		query.whereEqualTo(XUser.STUDENTID, studentID);
		query.include(XUser.HEADIMG);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() != 1) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "获取个人信息失败");
					return;
				}

				// 获取到用户
				user = arg0.get(0);
				addVisit(user);// 添加访问记录
				showUserInfo(user);

				// 获取状态
				getMyStatusByRelation(0, NUM, false);
			}
		});
	}

	/**
	 * 将用户信息显示在控件上
	 * 
	 * @param user
	 */
	private void showUserInfo(AVUser user) {
		nickname = user.getString(XUser.NICKNAME);
		studentId = user.getString(XUser.STUDENTID);
		int int_gender = user.getInt(XUser.GENDER);
		String hometown = user.getString(XUser.HOMETOWN);
		String college = user.getString(XUser.COLLEGE);
		String birthday = user.getString(XUser.BIRTHDAY);
		String relation = user.getString(XUser.RELATIONSHIP_STATE);

		String xingzuo = CommonUtil.DateUtils.getConstellation(birthday);
		String headImageUrl = "";
		if (user.getAVFile("image") != null) {
			headImageUrl = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE,
					50, "jpg");
		}

		// 昵称
		titleBar.setTitle(nickname);
		TextView tv_nickname = (TextView) headview.findViewById(R.id.tv_nickname);
		tv_nickname.setText(nickname);

		tv_visitcount = (TextView) headview.findViewById(R.id.tv_visitcount);
		tv_visitcount.setVisibility(View.GONE);

		// 头像

		iv_avatar = (SelectableRoundedImageView) headview.findViewById(R.id.iv_avatar);
		ImageLoader.getInstance().displayImage(headImageUrl, iv_avatar, Options.getListOptions());
		iv_avatar.setOnClickListener(this);

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

		// 家乡
		TextView tv_relation_edit = (TextView) userInfoView.findViewById(R.id.tv_relationship_edit);
		tv_relation_edit.setText(relation);

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

	@SuppressLint("InflateParams")
	@Override
	public void initView() {
		iniListView();
		titleBar = (TitleBar) findViewById(R.id.head);
	}

	@SuppressLint("InflateParams")
	private void iniListView() {
		mListView = (XListView) this.findViewById(R.id.lv_mystatus);// 列表
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

		mData = new ArrayList<StatusData>();// 数据集合
		mAdapter = new MyStatusAdapter(getApplicationContext(), mData, R.layout.item_timeline);// 适配器
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

		titleTab = (RelativeLayout) findViewById(R.id.title_tab);

		// 用户信息头部
		headview = getLayoutInflater().inflate(R.layout.userprofile_head, null);
		headTab = (RelativeLayout) (headview.findViewById(R.id.head_tab));

		// 用户信息列表
		userInfoView = (LinearLayout) getLayoutInflater().inflate(R.layout.userprofile_userinfo, null);

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
		headview.findViewById(R.id.iv_back_head).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		headview.setOnClickListener(null);

		title_bottombar_userinfo = (View) findViewById(R.id.bar_userinfo);
		title_bottombar_userstatus = (View) findViewById(R.id.bar_userstatus);

		head_bottombar_userinfo = (View) headview.findViewById(R.id.bar_userinfo);
		head_bottombar_userstatus = (View) headview.findViewById(R.id.bar_userstatus);

		title_bottombar_userinfo.setVisibility(View.VISIBLE);
		head_bottombar_userinfo.setVisibility(View.VISIBLE);

		title_bottombar_userstatus.setVisibility(View.INVISIBLE);
		head_bottombar_userstatus.setVisibility(View.INVISIBLE);

		titleTab.setVisibility(View.GONE);
		userInfoView.setVisibility(View.VISIBLE);
		mListView.addHeaderView(headview);
		mListView.addHeaderView(userInfoView);

		btn_sendmsg = (Button) userInfoView.findViewById(R.id.btn_chat);
		btn_sendmsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ChatActivity.startThis(UserProfileActivity.this, studentId, nickname);
			}
		});
	}

	private void getMyStatusByRelation(int skip, int limit, final boolean is_loadmore) {

		// 获取发表记录
		AVRelation<Status> statuses = user.getRelation(XUser.ANNOUNCE);

		if (null == statuses.getTargetClass()) {
			CommonUtil.ToastUtils.showShortToast(this.getApplicationContext(), "没有发布记录哦，快去发布一条吧");
			return;
		}

		AVQuery<Status> query = statuses.getQuery();
		query.orderByDescending(XUser.CREATEDAT);
		query.include(XUser.CREATOR);
		query.setSkip(skip);
		query.whereEqualTo("isShow", true);
		query.whereEqualTo("isAnonymous", false);// 只显示非匿名状态
		query.setLimit(limit);
		query.findInBackground(new FindCallback<Status>() {

			@Override
			public void done(List<Status> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
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
						mData.add(BBSService.transformStatusData(getApplicationContext(), (Status) item));
					}

					mAdapter.notifyDataSetChanged();
					mListView.stopLoadMore();
				}
			}
		});
	}

	protected void toCommentActivity(final int position) {
		BBSCommentActivity.startThis(UserProfileActivity.this, mData.get(position).AVObjectID);
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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_avatar:
			// 点击头像，查看头像大图
			toShowBigAvatar();
			break;

		default:
			break;
		}

	}

	private void toShowBigAvatar() {
		// 显示头像原图
		if (user != null && user.getAVFile("image") != null) {
			String userHeadImgUrl = user.getAVFile("image").getUrl();
			FullScreenPhotoViewActivity.startThis(UserProfileActivity.this, userHeadImgUrl);
		}
	}

}
