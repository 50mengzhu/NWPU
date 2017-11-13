package com.zyw.nwpu.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.easemob.chat.EMChatManager;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zyw.nwpu.AddUserInfoActivity;
import com.zyw.nwpu.BBSCommentActivity;
import com.zyw.nwpu.FullScreenPhotoViewActivity;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.MsgActivity;
import com.zyw.nwpu.PickHomeTownActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.SettingsActivity;
import com.zyw.nwpu.VisitorListActivity;
import com.zyw.nwpu.adapter.MyStatusAdapter;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.app.Const;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.JfenGuizeActivity;
import com.zyw.nwpu.jifen.JiangpinActivity;
import com.zyw.nwpu.jifen.JifenActivity;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.GetUserScoreSummaryCallback;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpu.service.AppConfig;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.BBSService.GetVisitCountCallback;
import com.zyw.nwpu.service.BBSServiceCallback.DeleteStatusCallback;
import com.zyw.nwpu.tool.FileManager;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpulib.utils.SPUtils;
import com.zyw.nwpu.view.MDatePicker;
import com.zyw.nwpu.view.MDatePicker.OnChooseEvent;
import com.zyw.nwpu.view.SelectableRoundedImageView;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

public class Me_Fragment extends Fragment implements IXListViewListener, View.OnClickListener {

	private static final int NUM = 10;
	private View view;

	private List<StatusData> mData;// 与ListView绑定的数据
	private MyStatusAdapter mAdapter;

	private TitleBar titleBar;
	private RelativeLayout titleTab;// title下面的tab条
	private View headview;// 头部
	private RelativeLayout headTab;// 头部背景图下面的tab条
	private LinearLayout userInfoView;// 用户信息列表
	private XListView mListView;
	private TextView tv_pleaselogin;
	private TextView tv_visitcount;// 主页访问量

	private SelectableRoundedImageView iv_avatar;// 头像
	private TextView tv_nickname;// 昵称

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

	private void showDeleteTip() {
		// 第一次进入会有提示
		if (!SPUtils.contains(getActivity(), "hasShowTip")) {
			new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("长按内容可以删除哦").setPositiveButton("知道了", null)
					.show();
			SPUtils.put(getActivity(), "hasShowTip", true);
		}
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
		mListView.setVisibility(View.VISIBLE);
		if (AVUser.getCurrentUser() != null) {
			// 显示用户信息
			showUserInfo(AVUser.getCurrentUser());

			// 获取状态列表
			getMyStatusByRelation(0, NUM, false);
		}
	}

	/**
	 * 显示未登录的布局
	 */
	private void showNotLoginState() {

		// Login.startThis(getActivity());

		tv_pleaselogin.setVisibility(View.VISIBLE);
		titleBar.setAlpha(1);
		titleBar.setTitle("我");
		mListView.setVisibility(View.GONE);
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
		String relationState = user.getString(XUser.RELATIONSHIP_STATE);
		String xingzuo = CommonUtil.DateUtils.getConstellation(birthday);
		String headImageUrl = "";
		if (user.getAVFile("image") != null) {
			headImageUrl = user.getAVFile("image").getThumbnailUrl(false, Const.HEADIMAGE_SIZE, Const.HEADIMAGE_SIZE,
					50, "jpg");
		}

		// 昵称
		titleBar.setTitle(nickname);
		tv_nickname.setText(nickname);
		if (TextUtils.isEmpty(nickname)) {
			tv_nickname.setText("请设置昵称");
		}

		// 头像
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
		tv_relation_edit.setText(relationState);

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

	private boolean isShowUserInfoTab = true;

	/**
	 * 是否显示用户信息tab
	 */
	private void showUserInfoTab(boolean is) {
		if (is) {
			if (isShowUserInfoTab)
				return;

			isShowUserInfoTab = true;
			mListView.addHeaderView(userInfoView);

			mAdapter.setBlock(true);
			mAdapter.notifyDataSetChanged();
			mListView.setPullLoadEnable(false);

			title_bottombar_userinfo.setVisibility(View.VISIBLE);
			head_bottombar_userinfo.setVisibility(View.VISIBLE);

			title_bottombar_userstatus.setVisibility(View.INVISIBLE);
			head_bottombar_userstatus.setVisibility(View.INVISIBLE);

		} else {
			if (!isShowUserInfoTab)
				return;

			isShowUserInfoTab = false;
			mListView.removeHeaderView(userInfoView);

			mAdapter.setBlock(false);
			mAdapter.notifyDataSetChanged();
			mListView.setPullLoadEnable(true);

			title_bottombar_userinfo.setVisibility(View.INVISIBLE);
			head_bottombar_userinfo.setVisibility(View.INVISIBLE);

			title_bottombar_userstatus.setVisibility(View.VISIBLE);
			head_bottombar_userstatus.setVisibility(View.VISIBLE);
			showDeleteTip();
		}
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
	 * 初始化标题 添加设置按钮
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
		titleBar.setBackIconResource(R.drawable.ic_title_msg);
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
				showUserInfoTab(false);
			}
		});
		titleTab.findViewById(R.id.rl_userinfo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserInfoTab(true);
			}
		});

	}

	@SuppressLint("InflateParams")
	private void iniHeadView() {

		// 用户信息头部
		headview = getActivity().getLayoutInflater().inflate(R.layout.userprofile_head_me, null);
		headview.setOnClickListener(null);
		headTab = (RelativeLayout) (headview.findViewById(R.id.head_tab));
		headview.findViewById(R.id.rl_userpublish).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserInfoTab(false);
			}
		});
		headview.findViewById(R.id.rl_userinfo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showUserInfoTab(true);
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
		tv_visitcount.setOnClickListener(this);
		tv_visitcount.setVisibility(View.GONE);

		// 头像
		iv_avatar = (SelectableRoundedImageView) headview.findViewById(R.id.iv_avatar);
		iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPicSelector();
			}
		});

		// 昵称
		tv_nickname = (TextView) headview.findViewById(R.id.tv_nickname);
		tv_nickname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showInputDialog();// 修改昵称
			}
		});

		// tab下面的下划线
		head_bottombar_userinfo = (View) headview.findViewById(R.id.bar_userinfo);
		head_bottombar_userstatus = (View) headview.findViewById(R.id.bar_userstatus);
	}

	@SuppressLint("InflateParams")
	private void iniUserInfoList() {
		// 用户信息列表
		userInfoView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.userprofile_userinfo_me, null);
		userInfoView.setVisibility(View.VISIBLE);
		userInfoView.setOnClickListener(null);
		userInfoView.findViewById(R.id.ll_college).setOnClickListener(this);
		userInfoView.findViewById(R.id.ll_birthday).setOnClickListener(this);
		userInfoView.findViewById(R.id.ll_hometown).setOnClickListener(this);
		userInfoView.findViewById(R.id.ll_relationship).setOnClickListener(this);
		userInfoView.findViewById(R.id.ll_myscore).setOnClickListener(this);

		ScoreHelper.getUserScoreSummary(new GetUserScoreSummaryCallback() {

			@Override
			public void onSuccess(String usrNickname, String usrStudentId, String avatarUrl, int totalScore) {
				TextView tv_myscore_edit = (TextView) userInfoView.findViewById(R.id.tv_myscore_edit);
				tv_myscore_edit.setText(String.valueOf(totalScore) + "分");
			}

			@Override
			public void onFailure(String errTip) {
				ToastUtils.showShortToast(errTip);
			}
		});

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

		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

				// 删除状态
				new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要删除吗？")
						.setNegativeButton("取消", null).setPositiveButton("删除", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteStatus(arg2 - 2);
							}
						}).show();

				return false;
			}
		});
		mListView.addHeaderView(headview);
		mListView.addHeaderView(userInfoView);
	}

	private void deleteStatus(final int index) {
		String statusId = mData.get(index).AVObjectID;
		BBSService.deleteStatus(statusId, new DeleteStatusCallback() {

			@Override
			public void onSuccess() {
				CommonUtil.ToastUtils.showShortToast(getActivity(), "删除成功");
				mData.remove(index);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailed(String errorTip) {
				CommonUtil.ToastUtils.showShortToast(getActivity(), "删除失败:" + errorTip);
			}
		});
	}

	private void getMyStatusByRelation(int skip, int limit, final boolean is_loadmore) {

		// 获取发表记录
		AVRelation<Status> statuses = AVUser.getCurrentUser().getRelation(XUser.ANNOUNCE);

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

	private void showInputDialog() {
		final EditText et_nickname = new EditText(getActivity());
		et_nickname.setMaxEms(AppConfig.NICKNAME_MAX_EMS);
		new AlertDialog.Builder(getActivity()).setTitle("请设置昵称").setView(et_nickname)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (TextUtils.isEmpty(et_nickname.getText().toString())) {
							ToastUtils.showShortToast("昵称不可为空！");
							return;
						}
						tv_nickname.setText(et_nickname.getText().toString());
						updateNickname(et_nickname.getText().toString());
					}
				}).setNegativeButton("取消", null).show();
	}

	/** 修改头像相关 ******************************************************************************/
	// 获取头像所需的参数
	private final int TAKE_PICTURE = 0;
	private final int RESULT_LOAD_IMAGE = 1;
	private final int CUT_PHOTO_REQUEST_CODE = 2;
	private final int HOMETOWN_REQUEST_CODE = 3;

	private Uri photoUri;
	private String headImgPath;
	private AVFile headImgFile = null;

	private void showPicSelector() {
		// 显示选项
		final CharSequence[] items = { "查看", "图库", "拍照" };
		new AlertDialog.Builder(getActivity()).setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					showAvatar();
					break;
				case 1:
					openGallery();
					break;
				case 2:
					takePhoto();
					break;

				default:
					break;
				}
			}
		}).create().show();
	}

	private void openGallery() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	private void takePhoto() {
		try {
			// 检查临时文件路径是否存在
			File fileDir = new File(FileManager.getHeadImageFilePath());
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(FileManager.getHeadImageFilePath() + System.currentTimeMillis() + ".JPEG");
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (file != null) {
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存昵称
	 * 
	 * @param nickname
	 */
	private void updateNickname(final String nickname) {
		if (TextUtils.isEmpty(nickname)) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "请输入昵称");
			return;
		}
		// 提交到LeanCloud
		final AVUser avUser = AVUser.getCurrentUser();
		if (avUser == null) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "保存信息出错");
			return;
		}

		avUser.put(XUser.NICKNAME, nickname);
		avUser.setFetchWhenSave(true);

		showWaiteDialog();

		avUser.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (e != null) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), "保存失败:" + e.getMessage());
					dismissProgressDialog();
					return;
				}

				fetchCurrentUserAfterNickName();
			}
		});
	}

	private void updateAvatar() {
		final AVUser avUser = AVUser.getCurrentUser();
		if (avUser == null) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "保存头像出错");
			return;
		}
		if (headImgFile != null) {
			avUser.put("image", headImgFile);
		}
		avUser.setFetchWhenSave(true);
		avUser.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (e != null) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), "保存失败:" + e.getMessage());
					dismissProgressDialog();
					return;
				}

				fetchCurrentUserAfterImage();
			}

		});
	}

	/**
	 * 更新用户信息
	 * 
	 * @param college
	 * @param birthday
	 * @param hometown
	 */
	private void updateUserInfo(String college, String birthday, String hometown, String relationship) {
		if (TextUtils.isEmpty(college) && TextUtils.isEmpty(birthday) && TextUtils.isEmpty(hometown)
				&& TextUtils.isEmpty(relationship)) {
			return;
		}

		// 提交到LeanCloud
		final AVUser avUser = AVUser.getCurrentUser();
		if (avUser == null) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "修改信息出错");
			return;
		}

		if (!TextUtils.isEmpty(college))
			avUser.put(XUser.COLLEGE, college);
		if (!TextUtils.isEmpty(birthday))
			avUser.put(XUser.BIRTHDAY, birthday);
		if (!TextUtils.isEmpty(hometown))
			avUser.put(XUser.HOMETOWN, hometown);
		if (!TextUtils.isEmpty(relationship))
			avUser.put(XUser.RELATIONSHIP_STATE, relationship);

		avUser.setFetchWhenSave(true);
		showWaiteDialog();
		avUser.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (e != null) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), "保存失败:" + e.getMessage());
					dismissProgressDialog();
					return;
				}
				fetchCurrentUserAfterInfoChanged();
			}
		});
	}

	private void upLoadImage(String path) {
		showWaiteDialog();

		if (path == null) {
			return;
		}
		headImgFile = new AVFile();
		try {
			headImgFile = AVFile.withAbsoluteLocalPath("headimg.jpg", path);
		} catch (IOException e) {
			return;
		}

		if (headImgFile == null)
			return;

		headImgFile.saveInBackground(new SaveCallback() {
			public void done(AVException arg0) {
				if (arg0 != null) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), "上传头像失败:" + arg0.getMessage());
					dismissProgressDialog();
					return;
				}
				updateAvatar();
			}
		});
	}

	private void fetchCurrentUserAfterInfoChanged() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);
					CommonUtil.ToastUtils.showShortToast(getActivity(), "保存成功");
					dismissProgressDialog();
				}
			}
		});
	}

	private void fetchCurrentUserAfterNickName() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);

					// 更新昵称
					EMChatManager.getInstance().updateCurrentUserNick(arg0.getString(XUser.NICKNAME));

					CommonUtil.ToastUtils.showShortToast(getActivity(), "保存成功");
					dismissProgressDialog();
				}
			}
		});
	}

	private void fetchCurrentUserAfterImage() {
		AVUser user = AVUser.getCurrentUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				if (arg1 == null) {
					AVUser.changeCurrentUser((AVUser) arg0, true);
					CommonUtil.ToastUtils.showShortToast(getActivity(), "保存成功");
					showUserInfo((AVUser) arg0);
					dismissProgressDialog();
				}
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			return;
		}

		switch (requestCode) {
		case TAKE_PICTURE:
			// 拍照
			startPhotoZoom(photoUri);
			break;

		case RESULT_LOAD_IMAGE:
			// 相册返回
			if (null != data) {
				Uri uri = data.getData();
				if (uri != null) {
					startPhotoZoom(uri);
				}
			}
			break;

		case CUT_PHOTO_REQUEST_CODE:
			// 裁剪返回
			if (null != data) {
				if (photoUri != null) {
					iv_avatar.setImageURI(photoUri);

					// 上传图片
					upLoadImage(headImgPath);
				}
			}
			break;

		case HOMETOWN_REQUEST_CODE:
			// 选择家乡返回
			if (!TextUtils.isEmpty(AppApplication.homeTown)) {
				// 家乡
				TextView tv_hometown_edit = (TextView) userInfoView.findViewById(R.id.tv_hometown_edit);
				tv_hometown_edit.setText(AppApplication.homeTown);
				updateUserInfo("", "", AppApplication.homeTown, "");
			}
			break;
		}
	}

	private void startPhotoZoom(Uri uri) {
		// 检查临时文件路径是否存在
		File fileDir = new File(FileManager.getHeadImageFilePath());
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		headImgPath = FileManager.getHeadImageFilePath() + System.currentTimeMillis() + ".JPEG";
		File file = new File(headImgPath);

		if (file != null) {
			photoUri = Uri.fromFile(file);
			final Intent intent = new Intent("com.android.camera.action.CROP");

			// 照片URL地址
			intent.setDataAndType(uri, "image/*");

			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 480);
			intent.putExtra("outputY", 480);
			// 输出路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

			// 输出格式
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			// 不启用人脸识别
			intent.putExtra("noFaceDetection", false);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
		}
	}

	private void showAvatar() {
		// 显示头像原图
		if (AVUser.getCurrentUser() != null && AVUser.getCurrentUser().getAVFile("image") != null) {
			String userHeadImgUrl = AVUser.getCurrentUser().getAVFile("image").getUrl();
			FullScreenPhotoViewActivity.startThis(getActivity(), userHeadImgUrl);
		}
	}

	private ProgressDialog pd = null;

	private void showWaiteDialog() {
		pd = new ProgressDialog(getActivity());
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		pd.setMessage("请稍等...");
		pd.show();
	}

	private void dismissProgressDialog() {
		if (pd != null)
			pd.dismiss();
		pd = null;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_visitcount:
			// 进入访客记录页面
			// TODO
			if (AVUser.getCurrentUser() != null) {
				String currentUserId = AVUser.getCurrentUser().getObjectId();
				VisitorListActivity.startThis(getActivity(), currentUserId);
			}
			break;

		case R.id.ll_college:
			toPickCollege();
			break;
		case R.id.ll_birthday:
			toPickBirthday();
			break;
		case R.id.ll_hometown:
			toPickHomeTown();
			break;

		case R.id.ll_relationship:
			toPickRelationship();
			break;
		case R.id.ll_myscore:
			toScore();
			break;
		default:
			break;
		}
	}

	private void toScore() {
		JiangpinActivity.startThis(getActivity());
		// JifenActivity.startThis(getActivity());
	}

	private void toPickRelationship() {
		AlertDialog.Builder b = new Builder(getActivity());
		b.setItems(relationShips, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 情感
				TextView tv_college_edit = (TextView) userInfoView.findViewById(R.id.tv_relationship_edit);
				tv_college_edit.setText(relationShips[arg1]);
				updateUserInfo("", "", "", relationShips[arg1]);
			}
		});
		b.show();
	}

	/**
	 * 选择家乡
	 */
	private void toPickHomeTown() {
		PickHomeTownActivity.startThisForResult(getActivity(), HOMETOWN_REQUEST_CODE);
	}

	private String[] collegeName = { "航空学院", "航天学院", "航海学院", "材料学院", "机电学院", "力学与土木建筑学院", "动力与能源学院", "电子信息学院", "自动化学院",
			"计算机学院", "理学院", "管理学院", "人文与经法学院", "软件与微电子学院", "生命学院", "外国语学院", "教育实验学院", "其他" };
	private String[] relationShips = { "单身", "恋爱中", "保密" };

	/**
	 * 选择学院
	 */
	private void toPickCollege() {
		AlertDialog.Builder b = new Builder(getActivity());
		b.setItems(collegeName, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 学院
				TextView tv_college_edit = (TextView) userInfoView.findViewById(R.id.tv_college_edit);
				tv_college_edit.setText(collegeName[arg1]);
				updateUserInfo(collegeName[arg1], "", "", "");
			}
		});
		b.show();
	}

	/**
	 * 选择生日
	 */
	private void toPickBirthday() {
		MDatePicker datePicker = null;
		datePicker = new MDatePicker(getActivity(), "");
		datePicker.pick(new OnChooseEvent() {

			@Override
			public void onChoose(String birthday) {
				if (!TextUtils.isEmpty(birthday)) {
					// 生日
					TextView tv_birthday_edit = (TextView) userInfoView.findViewById(R.id.tv_birthday_edit);
					tv_birthday_edit.setText(birthday);

					// 星座
					String xingzuo = CommonUtil.DateUtils.getConstellation(birthday);
					TextView tv_xingzuo_edit = (TextView) userInfoView.findViewById(R.id.tv_xingzuo_edit);
					tv_xingzuo_edit.setText(xingzuo);

					updateUserInfo("", birthday, "", "");
				}
			}
		});
	}
}
