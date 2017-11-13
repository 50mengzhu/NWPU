package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.avos.avoscloud.FunctionCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zyw.nwpu.adapter.BBSCommentAdapter;
import com.zyw.nwpu.adapter.StatusAdapter.Clickable;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.avos.AVCloudMethod;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.AddScoreRecordCallback;
import com.zyw.nwpu.service.AvatarAndNicknameService;
import com.zyw.nwpu.service.AvatarAndNicknameService.GetUserInfoCallback;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.BBSServiceCallback.AddCommentCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetCommentsListCallback;
import com.zyw.nwpu.service.BBSServiceCallback.GetStatusCallback;
import com.zyw.nwpu.service.BBSServiceCallback.RemoveCommentCallback;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.tool.Options;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.model.UserInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 状态评论页面
 * 
 * 所需的参数是帖子的ID
 * 
 * @author Rocket
 * 
 */
@ContentView(R.layout.activity_bbs_comment)
public class BBSCommentActivity extends BaseActivity implements OnClickListener, IXListViewListener {

	private EditText et_comment;
	private List<CommentData> cmtdata_set;

	private XListView cmtListView;
	private BBSCommentAdapter cmtAdapter;
	private View headview;

	// 头部
	private RelativeLayout rl_headBar_hd;
	private TextView tv_publisherName_hd, tv_schoolName_hd, tv_time_hd;
	private ImageView iv_headImg_hd;
	private ImageView iv_gender_hd;
	private TextView tv_likerlist;
	// 位置相关
	private LinearLayout ll_position;
	private TextView tv_position;

	// 中部
	private TextView tv_content_txt_hd;
	private ImageView iv_img_hd;

	// 底部
	private TextView tv_likenum_hd, tv_commentnum_hd;
	private ImageView iv_like;

	// 加载图片所需的变量
	private ImageLoader mImageLoader = ImageLoader.getInstance();

	private Status mStatus = null;
	private StatusData mStatusData = null;

	private AVUser targetUser;
	private String targetUsername = "";

	private String topicid;// 本条状态的id

	private List<UserInfo> likersInfo = new ArrayList<UserInfo>();

	public static void startThis(Context cxt, String topicid) {
		Intent intent = new Intent(cxt, BBSCommentActivity.class);
		intent.putExtra("topicid", topicid);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getData();
		iniView();
		iniEvent();

		iniTitle();

		getCurrentStatus(topicid);
	}

	private void iniTitle() {
		TitleBar bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle("评论");
	}

	private void getData() {
		Intent intent = this.getIntent();
		topicid = intent.getStringExtra("topicid");
	}

	private void getCurrentStatus(String objId) {
		BBSService.getStatusById(getApplicationContext(), objId, new GetStatusCallback() {

			@Override
			public void onSuccess(Status data) {
				mStatus = data;
				targetUser = mStatus.getPublisher();

				// 刷新帖子内容
				mStatusData = BBSService.transformStatusData(getApplicationContext(), mStatus);

				showStatusInfo(mStatusData);

				// 获取评论
				getComments();
			}

			@Override
			public void onFailed(String errorTip) {
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(), errorTip);
			}
		});
	}

	// private void getCurrentStatus(String objId) {
	// AVQuery<AVObject> query = new AVQuery<AVObject>(Status.CLASSNAME);
	// query.include("comment");// 存放AVRelation
	// query.include("creator");
	// query.getInBackground(objId, new GetCallback<AVObject>() {
	//
	// @Override
	// public void done(AVObject arg0, AVException arg1) {
	// if (arg1 != null) {
	// CommonUtil.ToastUtils.showShortToast(
	// getApplicationContext(), arg1.getLocalizedMessage());
	// // mSwipeLayout.setRefreshing(false);
	// return;
	// }
	// if (arg0 == null) {
	// CommonUtil.ToastUtils.showShortToast(
	// getApplicationContext(), "服务器查询出错");
	// // mSwipeLayout.setRefreshing(false);
	// return;
	// }
	//
	// mStatus = (Status) arg0;
	// targetUser = mStatus.getPublisher();
	//
	// // 刷新帖子内容
	// mStatusData = StatusData.statusDataTransform(
	// getApplicationContext(), mStatus);
	//
	// showStatusInfo(mStatusData);
	//
	// // 获取评论
	// getComments(0, NUM);
	//
	// }
	// });
	// }

	protected void showStatusInfo(final StatusData mStatusData) {
		// 显示头部信息
		if (mStatusData.isAnonymous) {
			// 匿名
			tv_publisherName_hd.setText("某同学");// 用户名
			tv_publisherName_hd.setTextColor(getResources().getColor(R.color.gray));
			// 匿名两个字居中
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) tv_publisherName_hd.getLayoutParams();
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			tv_publisherName_hd.setLayoutParams(layoutParams);

			iv_headImg_hd.setImageResource(R.drawable.default_round_head);// 默认头像
			tv_schoolName_hd.setText("");// 学院
			iv_gender_hd.setVisibility(View.INVISIBLE);// 性别

		} else {
			rl_headBar_hd.setOnClickListener(this);
			// 用户名
			tv_publisherName_hd.setText(mStatusData.nickName);
			// 性别
			if (mStatusData.gender == 0) {
				iv_gender_hd.setImageResource(R.drawable.ic_girl);// 女性图标
			} else if (mStatusData.gender == 1) {
				iv_gender_hd.setImageResource(R.drawable.ic_boy);// 男性图标
			} else {
				iv_gender_hd.setVisibility(View.INVISIBLE);
			}

			// 学校这里显示为学院与年级
			StringBuffer sb = new StringBuffer();

			// 学院
			if (!TextUtils.isEmpty(mStatusData.college)) {
				sb.append(mStatusData.college);
				sb.append(" ");
			}

			// // 年级
			// if (!TextUtils.isEmpty(mStatusData.studentId)) {
			// sb.append(mStatusData.studentId.substring(0, 4));
			// sb.append("级");
			// }

			// 学校 这里显示为学历
			tv_schoolName_hd.setText(sb.toString());

			// 头像
			if (!TextUtils.isEmpty(mStatusData.headImgUrl))
				mImageLoader.displayImage(mStatusData.headImgUrl, iv_headImg_hd, // 再从网上下载
						Options.getHeadImageDisplayOptions());
			else
				iv_headImg_hd.setImageResource(R.drawable.ic_defaut_headimg);// 先设置为默认头像
		}

		// 创建时间
		Date createdate = mStatusData.date;
		String createtime = DateChangeUtils.toToday(createdate);
		tv_time_hd.setText(createtime);
		tv_time_hd.setVisibility(View.VISIBLE);

		// 位置
		if (TextUtils.isEmpty(mStatusData.position) || TextUtils.equals(mStatusData.position, "火星")) {
			ll_position.setVisibility(View.INVISIBLE);
		} else {
			ll_position.setVisibility(View.VISIBLE);
			tv_position.setText(mStatusData.position);
		}

		// 中部
		SpannableString spannableString = null;
		if (!TextUtils.isEmpty(mStatusData.tag)) {
			spannableString = new SpannableString("#" + mStatusData.tag + "#" + mStatusData.content_txt);
			int start = 0;
			int end = 2 + mStatusData.tag.length();
			// 显示标签的颜色
			spannableString.setSpan(
					new ForegroundColorSpan(getApplicationContext().getResources().getColor(R.color.bg_title)), start,
					end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			// 标签点击事件
			spannableString.setSpan(new ClickableSpan() {

				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setUnderlineText(false);
				}

				@Override
				public void onClick(View arg0) {
					TagActivity.startThis(BBSCommentActivity.this, mStatusData.tag);
				}
			}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			spannableString = new SpannableString(mStatusData.content_txt);
		}

		tv_content_txt_hd.setHighlightColor(getApplicationContext().getResources().getColor(R.color.transparent));
		tv_content_txt_hd.setMovementMethod(LinkMovementMethod.getInstance());
		tv_content_txt_hd.setText(spannableString);// 文字内容

		showImg(mStatusData.imgUrl, iv_img_hd);

		// 底部
		tv_commentnum_hd.setText(String.valueOf(mStatusData.commentNum));
		tv_likenum_hd.setText("赞");
		if (mStatusData.AlreadyLiked) {
			tv_likenum_hd.setTextColor(Color.parseColor("#FFCC00"));
			iv_like.setImageResource(R.drawable.ic_action_like_pressed);
		} else {
			tv_likenum_hd.setTextColor(Color.BLACK);
			iv_like.setImageResource(R.drawable.ic_action_like_normal);
		}

		// 点赞列表
		if (mStatusData.likeNum != 0 && !TextUtils.isEmpty(mStatusData.likeUserIds)) {

			String[] likeUserList = mStatusData.likeUserIds.split(",");
			if (likeUserList != null && likeUserList.length != 0)
				tv_likenum_hd.setText(String.valueOf(likeUserList.length));

			if (AVUser.getCurrentUser() == null)
				return;
			if (AVUser.getCurrentUser().getObjectId().isEmpty())
				return;
			if (!TextUtils.equals(AVUser.getCurrentUser().getObjectId(), mStatusData.userId))
				return;

			for (int i = 0; i < likeUserList.length; i++) {
				final String userId = likeUserList[i];
				AvatarAndNicknameService.getUserInfoAndSaveByObjId(userId, new GetUserInfoCallback() {

					@Override
					public void done() {
						if (AvatarAndNicknameService.userInfoMap != null
								&& AvatarAndNicknameService.userInfoMap.containsKey(userId)) {
							likersInfo.add(AvatarAndNicknameService.userInfoMap.get(userId));
							showLikerList();
						}
					}
				});
			}
		}
	}

	private void showLikerList() {
		if (likersInfo == null || likersInfo.size() == 0)
			return;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < likersInfo.size(); i++) {
			if (sb.length() != 0)
				sb.append("、");
			sb.append(likersInfo.get(i).nickname);
		}
		sb.append("等" + likersInfo.size() + "人觉得赞");

		SpannableString spannableString = null;
		spannableString = new SpannableString(sb.toString());

		for (int i = 0; i < likersInfo.size(); i++) {
			String nickname = likersInfo.get(i).nickname;
			String userObjId = likersInfo.get(i).objId;

			int start = 0;
			if (i != 0) {
				for (int j = 0; j < i; j++) {
					start += likersInfo.get(j).nickname.length() + 1;
				}
			}
			int end = start + nickname.length();
			// 显示标签的颜色
			spannableString.setSpan(
					new ForegroundColorSpan(getApplicationContext().getResources().getColor(R.color.bg_title)), start,
					end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			// 标签点击事件
			spannableString.setSpan(new LikersClickableSpan(userObjId), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		}

		tv_likerlist.setVisibility(View.VISIBLE);

		tv_likerlist.setHighlightColor(getApplicationContext().getResources().getColor(R.color.transparent));
		tv_likerlist.setMovementMethod(LinkMovementMethod.getInstance());
		tv_likerlist.setText(spannableString);// 文字内容
	}

	private class LikersClickableSpan extends ClickableSpan {

		private String likerObjId = "";

		public LikersClickableSpan(String id) {
			likerObjId = id;
		}

		@Override
		public void onClick(View arg0) {
			UserProfileActivity.startThis(BBSCommentActivity.this, likerObjId);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	protected void showMenu() {
		if (mStatusData == null)
			return;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "复制内容" }, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
				default:
					CommonUtil.SystemUtils.copy(getApplicationContext(), mStatusData.content_txt);
					break;
				case 1:
					report();
					break;
				}
			}
		});
		builder.show();
	}

	// 举报
	private void report() {
		BBSService.reportStatus(mStatusData.AVObjectID);
		CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "感谢您的举报，我们将认真核实内容！");
	}

	private void showImg(String imgUrl, ImageView iv) {

		// 这里需要将获取的图片显示在控件上
		if (AVUtils.isBlankString(imgUrl)) {
			iv.setVisibility(View.GONE);
			return;
		}

		iv.setVisibility(View.VISIBLE);
		iv.setImageResource(R.drawable.ic_chat_def_pic);
		mImageLoader.displayImage(imgUrl, iv, Options.getListOptions());
	}

	@SuppressLint("InlinedApi")
	private void iniView() {
		// ListView头部
		// 头部
		headview = getLayoutInflater().inflate(R.layout.bbs_comment_header, null);
		rl_headBar_hd = (RelativeLayout) headview.findViewById(R.id.rl_headbar_cmt_hd);
		tv_publisherName_hd = (TextView) headview.findViewById(R.id.tv_creatorName_cmt_hd);// 发布者用户名
		iv_headImg_hd = (ImageView) headview.findViewById(R.id.iv_headImg_cmt_hd);// 头像
		iv_gender_hd = (ImageView) headview.findViewById(R.id.iv_creatorGender_cmt_hd);// 性别图标
		tv_schoolName_hd = (TextView) headview.findViewById(R.id.tv_schoolname_cmt_hd);// 学校名称
		tv_time_hd = (TextView) headview.findViewById(R.id.tv_time_cmt_hd);// 创建时间
		headview.findViewById(R.id.iv_downbtn).setOnClickListener(this);

		ll_position = (LinearLayout) headview.findViewById(R.id.ll_position);
		tv_position = (TextView) headview.findViewById(R.id.tv_position);
		ll_position.setVisibility(View.INVISIBLE);

		// 中部
		tv_content_txt_hd = (TextView) headview.findViewById(R.id.tv_txt_cmt_hd);// 文本
		iv_img_hd = (ImageView) headview.findViewById(R.id.iv_img_cmt_hd);// 图片
		iv_img_hd.setOnClickListener(this);

		// 底部
		tv_likenum_hd = (TextView) headview.findViewById(R.id.tv_like_cmt_hd);
		iv_like = (ImageView) headview.findViewById(R.id.iv_like_cmt_hd);
		tv_commentnum_hd = (TextView) headview.findViewById(R.id.tv_comment_cmt_hd);
		tv_likerlist = (TextView) headview.findViewById(R.id.tv_likerlist);

		tv_likerlist.setVisibility(View.GONE);

		// 评论列表
		cmtListView = (XListView) BBSCommentActivity.this.findViewById(R.id.lv_comment);// 列表
		cmtListView.addHeaderView(headview);
		// cmtListView.addFooterView(footView);
		cmtListView.setPullLoadEnable(true);
		cmtListView.setPullRefreshEnable(false);
		cmtListView.setXListViewListener(this);

		cmtListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg2--;

				if (arg2 == 0) {
					et_comment.setHint("评论");
					targetUser = mStatus.getPublisher();
					targetUsername = "";
					et_comment.setFocusable(true);
					et_comment.requestFocus();
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					return;
				}

				// 当listview有Footview时，将这句话取消注释
				// 点击最底部时，不是@
				if (arg2 == (cmtListView.getCount() - 1)) {
					return;
				}

				// 设置回复对象
				if (cmtdata_set.get(arg2 - 1).isAnonymous) {
					// 回复匿名者
					targetUser = cmtdata_set.get(arg2 - 1).creator;

					if (!cmtdata_set.get(arg2 - 1).creatorIsLZ) {
						// 不是楼主
						targetUsername = "某同学";
						et_comment.setHint("@" + "某同学");
					} else {
						// 是楼主
						targetUsername = "楼主 匿名用户";
						et_comment.setHint("@" + "楼主 某同学");
					}

				} else {
					targetUser = cmtdata_set.get(arg2 - 1).creator;
					if (cmtdata_set.get(arg2 - 1).creatorIsLZ) {
						targetUsername = "楼主 " + targetUser.getString("name");
						et_comment.setHint("@楼主 " + targetUser.getString("name"));
					} else {
						targetUsername = targetUser.getString("name");
						et_comment.setHint("@" + targetUser.getString("name"));
					}
				}

				// 打开输入法
				et_comment.setFocusable(true);
				et_comment.requestFocus();
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		cmtdata_set = new ArrayList<CommentData>();// 数据
		cmtAdapter = new BBSCommentAdapter(BBSCommentActivity.this, cmtdata_set, R.layout.bbs_comment_item, mHandle);// 适配器
		cmtListView.setAdapter(cmtAdapter);
		et_comment = (EditText) findViewById(R.id.et_comment);// 评论输入框
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BBSCommentAdapter.WHAT_CLICK_ITEM) {
				// 进入个人中心
				if (cmtdata_set.get(msg.arg1).creator != null)
					toUserInfo(cmtdata_set.get(msg.arg1).creator.getObjectId());
			} else if (msg.what == BBSCommentAdapter.WHAT_CLICK_DELETE) {
				// 删除评论
				showConfirmMenu(msg.arg1);
			}
		}
	};

	private void showConfirmMenu(final int position) {
		new AlertDialog.Builder(BBSCommentActivity.this).setTitle("确定要删除这条评论吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						BBSService.removeComments(mStatusData.AVObjectID, cmtdata_set.get(position).objId,
								new RemoveCommentCallback() {

									@Override
									public void onSuccess() {
										mStatusData.commentNum--;
										tv_commentnum_hd.setText(String.valueOf(mStatusData.commentNum));

										int a = cmtdata_set.size();
										cmtdata_set.remove(position);
										cmtAdapter.notifyDataSetChanged();
										CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "删除成功");
									}

									@Override
									public void onFailed(String errorTip) {
										CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
												"删除失败：" + errorTip);
									}
								});
					}
				}).setNegativeButton("取消", null).show();
	}

	private void iniEvent() {

		this.findViewById(R.id.ll_like_cmt_hd).setOnClickListener(this);
		this.findViewById(R.id.btn_send_cmt).setOnClickListener(this);
		et_comment.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
				if (AVUtils.isBlankString(arg0.toString())) {
					et_comment.setHint("评论");
					targetUser = mStatus.getPublisher();
					targetUsername = "";
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_send_cmt:
			addComment();
			break;

		case R.id.ll_like_cmt_hd:
			like();
			break;

		case R.id.rl_headbar_cmt_hd:
			toUserInfo(mStatusData.userId);
			break;

		case R.id.iv_img_cmt_hd:
			toPhotoView();
			break;
		case R.id.iv_downbtn:
			showMenu();
			break;
		}
	}

	// 暂时不显示个人中心
	private void toUserInfo(String userId) {
		if (TextUtils.isEmpty(userId))
			return;
		UserProfileActivity.startThis(BBSCommentActivity.this, userId);
	}

	private void toPhotoView() {
		float maxWidth, picWidth, picHeight, maxHeight;

		// 指定图片宽度
		maxWidth = (float) (CommonUtil.ScreenUtils.getScreenWidth(getApplicationContext()));

		picWidth = Float.parseFloat(mStatus.getImg().getMetaData("width").toString());
		picHeight = Float.parseFloat(mStatus.getImg().getMetaData("height").toString());
		maxHeight = (int) (maxWidth * picHeight / picWidth);

		maxHeight = (float) (CommonUtil.ScreenUtils.getScreenHeight(getApplicationContext()));

		FullScreenPhotoViewActivity.startThis(BBSCommentActivity.this, mStatusData.imgUrl);
	}

	private void addComment() {
		// 检查是否登陆
		if (!AccountHelper.isLogedIn(getApplicationContext())) {
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请先登陆");
			Login.startThis(BBSCommentActivity.this);
			return;
		}

		if (mStatus == null) {
			CommonUtil.ToastUtils.showShortToast(BBSCommentActivity.this, "请检查网络连接");
			return;
		}

		final String content = et_comment.getText().toString().trim();
		if (content.equals("")) {
			CommonUtil.ToastUtils.showShortToast(BBSCommentActivity.this, "请填写内容");
			return;
		}

		// 是否匿名评论
		boolean isAnonymous = false;
		if (AVUser.getCurrentUser().getObjectId().compareTo(mStatusData.userId) == 0 && mStatusData.isAnonymous) {
			isAnonymous = true;
		} else {
			isAnonymous = false;
		}

		BBSService.addComment(getApplicationContext(), mStatus, content, isAnonymous, targetUsername, targetUser,
				new AddCommentCallback() {

					@Override
					public void onSuccess() {
						// 添加评论成功
						CommonUtil.ToastUtils.showShortToast(BBSCommentActivity.this, "评论成功");

						// 评论数加1 头部
						mStatusData.commentNum++;
						tv_commentnum_hd.setText(String.valueOf(mStatusData.commentNum));

						// 增加积分
						ScoreHelper.addScore("评论帖子", 1, new AddScoreRecordCallback() {

							@Override
							public void onSuccess() {
								ToastUtils.showShortToast("评论帖子 增加1积分");
							}

							@Override
							public void onFailure(String errTip) {
								// TODO Auto-generated method stub
							}
						});

					}

					@Override
					public void onFailed(String errorTip) {
						// 评论失败
						CommonUtil.ToastUtils.showShortToast(BBSCommentActivity.this, errorTip);
					}
				});

		// 清空内容
		et_comment.setText("");
	}

	private void like() {
		// 检查是否登陆
		if (!AccountHelper.isLogedIn(getApplicationContext())) {
			// 未登录自己服务器
			CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请先登陆");
			Login.startThis(BBSCommentActivity.this);
			return;
		}

		if (mStatusData.AlreadyLiked) {
			// 取消点赞
			removeLike();
		} else {
			// 点赞
			addLike();
		}
	}

	private void addLike() {
		iv_like.setImageResource(R.drawable.ic_action_like_pressed);
		startScaleAnim(iv_like);

		mStatusData.likeNum++;
		mStatusData.AlreadyLiked = true;
		tv_likenum_hd.setText(String.valueOf(mStatusData.likeNum));
		tv_likenum_hd.setTextColor(Color.parseColor("#FFCC00"));

		String tInstallationId = mStatus.getPublisher().getString(XUser.DEVICEID);
		String targetUserObjId = mStatus.getPublisher().getObjectId();
		BBSService.addLike(getApplicationContext(), AVUser.getCurrentUser(), tInstallationId, mStatus.getObjectId(),
				targetUserObjId);
	}

	private void startScaleAnim(View v) {
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(150);
		animation.setFillEnabled(false);
		animation.setFillAfter(false);
		animation.setRepeatCount(1);
		animation.setRepeatMode(Animation.REVERSE);
		v.startAnimation(animation);
	}

	private void removeLike() {
		iv_like.setImageResource(R.drawable.ic_action_like_normal);
		mStatusData.likeNum--;
		mStatusData.AlreadyLiked = false;
		tv_likenum_hd.setTextColor(Color.BLACK);
		tv_likenum_hd.setText(String.valueOf(mStatusData.likeNum));

		BBSService.removeLike(AVUser.getCurrentUser(), topicid);
	}

	private void loadMoreComments(int skip) {
		BBSService.loadMoreComments(getApplicationContext(), mStatus, cmtdata_set.size(),
				new GetCommentsListCallback() {

					@Override
					public void onSuccess(List<CommentData> data) {
						if (data != null && data.size() != 0) {
							for (int i = 0; i < data.size(); i++) {
								cmtdata_set.add(data.get(i));
							}
							cmtAdapter.notifyDataSetChanged();
						} else {
							CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "没有更多了");
							cmtListView.setPullLoadEnable(false);
						}
						cmtListView.stopLoadMore();
					}

					@Override
					public void onFailed(String errorTip) {
						CommonUtil.ToastUtils.showShortToast(getApplicationContext(), errorTip);
						cmtListView.stopLoadMore();
					}
				});
	}

	private void getComments() {
		BBSService.getComments(getApplicationContext(), mStatus, new GetCommentsListCallback() {

			@Override
			public void onSuccess(List<CommentData> data) {
				if (data != null && data.size() != 0) {
					cmtdata_set.clear();
					for (int i = 0; i < data.size(); i++) {
						cmtdata_set.add(data.get(i));
					}
					cmtAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailed(String errorTip) {
				CommonUtil.ToastUtils.showShortToast(getApplicationContext(), errorTip);
			}
		});
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		if (cmtdata_set != null)
			loadMoreComments(cmtdata_set.size());
	}

	// @Override
	// public void onRefresh() {
	// shown_comments_num = 0;
	// totalfloornum = 1;
	// getComments(shown_comments_num, NUM);
	// }
}
