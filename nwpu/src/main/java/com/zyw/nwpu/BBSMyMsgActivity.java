package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.GetCallback;
import com.zyw.nwpu.adapter.MyMsgAdapter;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpulib.model.Comment;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.DateChangeUtils;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

@ContentView(R.layout.activity_bbs_mymsg)
public class BBSMyMsgActivity extends BaseActivity implements IXListViewListener {

	private static final int NUM = 20;

	// 下拉刷新控件
	// private SwipeRefreshLayout mSwipeLayout;

	private MyMsgAdapter madapter;
	private List<CommentData> mdata;
	private int index;
	private XListView mList;

	private int shown_notify_num = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar titleBar = (TitleBar) findViewById(R.id.head);
		titleBar.setTitle("我的消息");

		getComments(0, NUM, false);

	}

	@Override
	public void initView() {
		// mSwipeLayout = (SwipeRefreshLayout) this
		// .findViewById(R.id.id_swipe_ly);
		// mSwipeLayout.setOnRefreshListener(this);
		// mSwipeLayout.setColorScheme(android.R.color.holo_blue_light,
		// android.R.color.holo_red_light,
		// android.R.color.holo_orange_light,
		// android.R.color.holo_green_light);

		// 评论列表
		mList = (XListView) this.findViewById(R.id.lv_mynotify);// 列表
		mList.setPullLoadEnable(true);
		mList.setPullRefreshEnable(true);
		mList.setXListViewListener(this);

		// mList.setRemoveListener(this);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				toCommentActivity(arg2 - 1);
			}
		});

		mdata = new ArrayList<CommentData>();// 数据
		madapter = new MyMsgAdapter(getApplicationContext(), mdata, R.layout.list_item_mymsg);// 适配器
		mList.setAdapter(madapter);

	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, BBSMyMsgActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	private void showNoMsgTip() {
		// 显示列表 隐藏过程条
		// this.findViewById(R.id.iv_nomsg_mynotify).setVisibility(View.VISIBLE);
	}

	private void getComments(int skip, int limit, final boolean is_load_more) {

		AVRelation<Comment> comments = AVUser.getCurrentUser().getRelation("commentsToMe");// 获取针对当前用户的评论

		if (comments.getTargetClass() == null) {
			if (shown_notify_num == 0)
				showNoMsgTip();
			return;
		}

		AVQuery<Comment> query = comments.getQuery();
		query.orderByDescending("createdAt");
		// query.whereEqualTo("hasread", false);
		query.setLimit(limit);
		query.setSkip(skip);
		query.include("targetTopic");// 这里要优化？

		query.findInBackground(new FindCallback<Comment>() {

			@Override
			public void done(final List<Comment> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					// mSwipeLayout.setRefreshing(false);
					mList.stopLoadMore();
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					if (shown_notify_num == 0) {
						showNoMsgTip();
						CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "没有记录");
					} else {
						CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "没有更多了");
					}
					// mSwipeLayout.setRefreshing(false);
					mList.stopLoadMore();
					return;
				}

				// 将获取到的消息设为已读
				for (AVObject obj : arg0) {
					AVObject post = AVObject.createWithoutData("comments", obj.getObjectId());
					post.put("hasread", true);
					post.saveInBackground();
				}

				if (!is_load_more) {
					mdata.clear();
				}

				shown_notify_num += arg0.size();

				for (final Comment cmt : arg0) {
					if (null != cmt && null != cmt.getCreatedAt()) {
						CommentData item = new CommentData();
						item.creator = AVUser.getCurrentUser();// ////////////////////////////////////
						item.content = cmt.getText();
						item.createTime = DateChangeUtils.toToday(cmt.getCreatedAt());
						item.targetTopicId = cmt.getAVObject("targetTopic").getObjectId();
						item.targetTopicContent = cmt.getAVObject("targetTopic").getString("content");

						if (cmt.getAVObject("targetTopic").getAVFile("announce_img") != null) {
							int height = CommonUtil.ScreenUtils.dp2px(getApplicationContext(),
									getResources().getDimension(R.dimen.mymsgtopic));
							item.targetTopicImageURL = cmt.getAVObject("targetTopic").getAVFile("announce_img")
									.getThumbnailUrl(false, height, height);
						} else {
							item.targetTopicImageURL = "";
						}

						item.hasRead = cmt.getBoolean("hasread");
						mdata.add(item);
					}
				}

				for (index = 0; index < arg0.size(); index++) {
					final Comment cmt = arg0.get(index);
					if (null != cmt && null != cmt.getCreatedAt()) {

						AVUser usr = cmt.getCreator();
						if (null == usr.getCreatedAt()) {
							usr.fetchInBackground(new GetCallback<AVObject>() {

								@Override
								public void done(AVObject user, AVException arg1) {
									mdata.get(getIndex()).creator = (AVUser) user;
									madapter.notifyDataSetChanged();
									// mSwipeLayout.setRefreshing(false);
									mList.stopLoadMore();
								}

								private int getIndex() {
									int i = 0;
									while (cmt != arg0.get(i)) {
										i++;
									}
									return i + shown_notify_num - arg0.size();
								}
							});
						}
					}
				}
			}
		});
	}

	private void toCommentActivity(final int position) {
		BBSCommentActivity.startThis(BBSMyMsgActivity.this, mdata.get(position).targetTopicId);
	}

	@Override
	public void onRefresh() {
		shown_notify_num = 0;
		// mSwipeLayout.setRefreshing(true);
		getComments(shown_notify_num, NUM, false);

		// 隐藏刷新头
		mList.stopRefresh();

	}

	@Override
	public void onLoadMore() {
		getComments(shown_notify_num, NUM, true);
	}

}
