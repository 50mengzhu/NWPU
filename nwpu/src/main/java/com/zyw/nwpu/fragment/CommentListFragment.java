package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.AVException;
import com.zyw.nwpu.BBSCommentActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.adapter.MyMsgAdapter;
import com.zyw.nwpulib.model.Comment;
import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

public class CommentListFragment extends Fragment implements IXListViewListener {

	private static final int LIMIT_NUM = 10;

	private MyMsgAdapter madapter;
	private List<CommentData> mdata;
	private XListView mList;

	private int shown_notify_num = 0;

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_bbs_mymsg, container, false);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initView();
				getComments(0, LIMIT_NUM, false);
			}
		}, 1);
		return rootView;
	}

	private void initView() {
		// 评论列表
		mList = (XListView) rootView.findViewById(R.id.lv_mynotify);// 列表
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
		madapter = new MyMsgAdapter(getActivity().getApplicationContext(), mdata, R.layout.list_item_mymsg);// 适配器
		mList.setAdapter(madapter);
	}

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, CommentListFragment.class);
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
		query.include("targetTopic");
		query.include("creator");
		query.findInBackground(new FindCallback<Comment>() {

			@Override
			public void done(final List<Comment> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getActivity().getApplicationContext(),
							arg1.getLocalizedMessage());
					mList.stopLoadMore();
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					if (shown_notify_num == 0) {
						showNoMsgTip();
						CommonUtil.ToastUtils.showShortToast(getActivity().getApplicationContext(), "没有记录");
					} else {
						CommonUtil.ToastUtils.showShortToast(getActivity().getApplicationContext(), "没有更多了");
					}
					// mSwipeLayout.setRefreshing(false);
					mList.stopLoadMore();
					return;
				}

				if (!is_load_more) {
					mdata.clear();
				}

				shown_notify_num += arg0.size();

				for (final Comment cmt : arg0) {
					if (null != cmt && null != cmt.getCreatedAt()) {
						CommentData item = new CommentData();
						item = BBSService.transformCommentData(cmt, "");
						mdata.add(item);
					}
				}

				// 将未读消息标记为已读
				for (AVObject obj : arg0) {
					if (obj != null && !obj.getBoolean("hasread")) {
						AVObject post = AVObject.createWithoutData("comments", obj.getObjectId());
						post.put("hasread", true);
						post.saveInBackground();
					}
				}

				madapter.notifyDataSetChanged();
				mList.stopLoadMore();
			}
		});
	}

	private void toCommentActivity(final int position) {
		BBSCommentActivity.startThis(getActivity(), mdata.get(position).targetTopicId);
	}

	@Override
	public void onRefresh() {
		shown_notify_num = 0;
		getComments(shown_notify_num, LIMIT_NUM, false);

		// 隐藏刷新头
		mList.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		getComments(shown_notify_num, LIMIT_NUM, true);
	}
}
