package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xutils.view.annotation.ViewInject;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zyw.nwpu.BBSCommentActivity;
import com.zyw.nwpu.BBSPublishActivity;
import com.zyw.nwpu.MainTabActivity;
import com.zyw.nwpu.MsgActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.SearchUserActivity;
import com.zyw.nwpu.TagActivity;
import com.zyw.nwpu.UserProfileActivity;
import com.zyw.nwpu.adapter.StatusAdapter;
import com.zyw.nwpu.adapter.StatusAdapter.LikeWidget;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.model.StatusData;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpu.service.BBSService;
import com.zyw.nwpu.service.BBSServiceCallback.GetStatusListCallback;
import com.zyw.nwpu.service.BBSServiceCallback.ToggleStatusStickCallback;
import com.zyw.nwpu.view.xlistview.XListView;
import com.zyw.nwpu.view.xlistview.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 没有区分栏目 20160206
 * 
 * @author Rocket
 * 
 */
public class StatusListFragment extends Fragment implements IXListViewListener {

	private View rootView;

	// XListView相关的参数
	private XListView mListView;
	private View searchBar;
	private List<StatusData> mData;// 与ListView绑定的数据
	private StatusAdapter mAdapter;

	private String tag = "";
	private AVGeoPoint targetPosition = null;

	public void setPosition(AVGeoPoint p) {
		targetPosition = p;
	}

	public void setTag(String t) {
		tag = t;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_statuslist, container, false);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				iniView();
				showWaiteDialog();
				refreshData();
			}
		}, 1);

		return rootView;
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
		pd.setMessage("正在加载...");
		pd.show();
	}

	private void dismissWaiteDialog() {
		if (pd != null)
			pd.dismiss();
		pd = null;
	}

	private void refreshData() {
		mData.clear();

		if (targetPosition != null) {
			BBSService.getStatusesByPosition(getActivity(), targetPosition, 0, new GetStatusListCallback() {

				@Override
				public void onSuccess(List<StatusData> data) {
					if (data != null && data.size() != 0) {
						mData.clear();
						for (int i = 0; i < data.size(); i++) {
							mData.add(data.get(i));
						}
						mAdapter.notifyDataSetChanged();
						mListView.setVisibility(View.VISIBLE);
						dismissWaiteDialog();
					}
				}

				@Override
				public void onFailed(String errorTip) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), errorTip);
					// 隐藏刷新头
					mListView.stopRefresh();
					dismissWaiteDialog();
				}
			});
			return;
		}

		// 先获取置顶消息
		BBSService.getStickStatus(getActivity(), tag, new GetStatusListCallback() {

			@Override
			public void onSuccess(List<StatusData> data) {
				if (data != null && data.size() != 0) {
					mData.clear();
					for (int i = 0; i < data.size(); i++) {
						if (data.get(i).isSticky)
							mData.add(data.get(i));
					}
					mAdapter.notifyDataSetChanged();
					mListView.setVisibility(View.VISIBLE);
					dismissWaiteDialog();
				}

				// 获取其他状态
				BBSService.getStatuses(getActivity(), tag, new GetStatusListCallback() {

					@Override
					public void onSuccess(List<StatusData> data) {
						if (data != null && data.size() != 0) {
							for (int i = 0; i < data.size(); i++) {
								if (!data.get(i).isSticky)// 非置顶
									mData.add(data.get(i));
							}
							// TODO
							ToastUtils.showShortToast("获取到数据" + String.valueOf(data.size()));
							mAdapter.notifyDataSetChanged();
							mListView.setVisibility(View.VISIBLE);
						}

						// 隐藏刷新头
						mListView.stopRefresh();
						dismissWaiteDialog();
					}

					@Override
					public void onFailed(String errorTip) {
						CommonUtil.ToastUtils.showShortToast(getActivity(), errorTip);
						// 隐藏刷新头
						mListView.stopRefresh();
						dismissWaiteDialog();
					}
				});
			}

			@Override
			public void onFailed(String errorTip) {
				CommonUtil.ToastUtils.showShortToast(getActivity(), errorTip);
				// 隐藏刷新头
				mListView.stopRefresh();
				dismissWaiteDialog();
			}
		});
	}

	private void loadMore() {
		if (targetPosition != null) {
			BBSService.getStatusesByPosition(getActivity(), targetPosition, mAdapter.getCount(),
					new GetStatusListCallback() {

						@Override
						public void onSuccess(List<StatusData> data) {
							if (data == null || data.size() == 0) {
								CommonUtil.ToastUtils.showShortToast(getActivity(), "没有更多了");
								mListView.stopLoadMore();
								return;
							}
							for (int i = 0; i < data.size(); i++) {
								mData.add(data.get(i));
							}
							mAdapter.notifyDataSetChanged();
							mListView.stopLoadMore();
						}

						@Override
						public void onFailed(String errorTip) {
							CommonUtil.ToastUtils.showShortToast(getActivity(), errorTip);
							mListView.stopLoadMore();
						}
					});
			return;
		}

		Date lastDate = mData.get(mData.size() - 1).date;
		BBSService.loadMoreStatuses(getActivity(), lastDate, tag, new GetStatusListCallback() {

			@Override
			public void onSuccess(List<StatusData> data) {
				if (data == null || data.size() == 0) {
					CommonUtil.ToastUtils.showShortToast(getActivity(), "没有更多了");
					mListView.stopLoadMore();
					return;
				}
				for (int i = 0; i < data.size(); i++) {
					if (!data.get(i).isSticky)// 非置顶
						mData.add(data.get(i));
				}
				mAdapter.notifyDataSetChanged();
				mListView.stopLoadMore();
			}

			@Override
			public void onFailed(String errorTip) {
				CommonUtil.ToastUtils.showShortToast(getActivity(), errorTip);
				mListView.stopLoadMore();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	// 进入用户个人中心
	private void toUserInfo(final int position) {
		String userid = mData.get(position).userId;
		UserProfileActivity.startThis(getActivity(), userid);
	}

	protected void toCommentActivity(final int position) {
		BBSCommentActivity.startThis(getActivity(), mData.get(position).AVObjectID);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.ll_position:
				// 位置
				ToastUtils.showShortToast("位置");
				int index = msg.arg1;
				String position = mData.get(index).position;
				double lat = mData.get(index).lat;
				double lng = mData.get(index).lng;

				if (TextUtils.isEmpty(position))
					break;
				if (lat == -1 || lng == -1)
					break;
				TagActivity.startThis(getActivity(), position, lng, lat);
				break;

			// 点赞
			case R.id.tv_like:
				clickLike(msg.arg1, (LikeWidget) msg.obj);
				break;

			// 显示用户个人信息
			case R.id.rl_headbar:
				toUserInfo(msg.arg1);
				break;

			// 复制
			case R.id.tv_txt:
			case R.id.iv_downbtn:
				showLongClickMenu(msg.arg1);
				break;
			}
		}
	};

	@Override
	public void onRefresh() {
		refreshData();
	}

	@Override
	public void onLoadMore() {
		loadMore();
	}

	/**
	 * 点击点赞按钮
	 * 
	 * @param position
	 * @param likeWidget
	 */
	private void clickLike(final int position, final LikeWidget likeWidget) {
		// 检查是否登陆
		if (!AccountHelper.isLogedIn(getActivity())) {
			CommonUtil.ToastUtils.showShortToast(getActivity(), "请先登陆");
			Login.startThis(getActivity());
			return;
		}

		if (mData.get(position).AlreadyLiked) {
			// 取消点赞
			removeLike(position, likeWidget);
		} else {
			// 点赞
			addLike(position, likeWidget);
		}
	}

	/**
	 * 点赞
	 * 
	 * @param position
	 * @param likeWidget
	 */
	private void addLike(final int position, final LikeWidget likeWidget) {

		mData.get(position).likeNum++;
		mData.get(position).AlreadyLiked = true;
		mAdapter.notifyDataSetChanged();

		// 动画
		startScaleAnim(likeWidget.iv_like);
		String statusId = mData.get(position).AVObjectID;
		String tInstallationId = mData.get(position).deviceId;
		String targetUserObjId = mData.get(position).creator.getObjectId();
		BBSService.addLike(getActivity(), AVUser.getCurrentUser(), tInstallationId, statusId, targetUserObjId);
	}

	/**
	 * 取消点赞
	 * 
	 * @param position
	 * @param likeWidget
	 */
	private void removeLike(final int position, final LikeWidget likeWidget) {
		mData.get(position).likeNum--;
		mData.get(position).AlreadyLiked = false;
		mAdapter.notifyDataSetChanged();

		String statusId = mData.get(position).AVObjectID;
		BBSService.removeLike(AVUser.getCurrentUser(), statusId);

	}

	/**
	 * 点赞按钮缩放动画
	 * 
	 * @param v
	 */
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

	@SuppressLint("InflateParams")
	private void iniSearchBar() {
		searchBar = getActivity().getLayoutInflater().inflate(R.layout.include_search_bar, null);
		searchBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SearchUserActivity.startThis(getActivity());
			}
		});
	}

	private void iniView() {
		iniSearchBar();
		mData = new ArrayList<StatusData>();
		mListView = (XListView) rootView.findViewById(R.id.mListView);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this);
		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

		mAdapter = new StatusAdapter(getActivity(), mData, R.layout.list_item_status, mHandle,
				new StatusAdapter.TagClickListener() {

					@Override
					public void onTagClicked(String tag) {
						TagActivity.startThis(getActivity(), tag);
					}
				});
		mListView.setAdapter(mAdapter);

		// 长按复制
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showLongClickMenu(arg2 - 2);
				return true;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				toCommentActivity(arg2 - 2);
			}
		});

		mListView.addHeaderView(searchBar);
	}

	/**
	 * 长按弹出菜单
	 * 
	 * @param i
	 */
	protected void showLongClickMenu(final int i) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		String[] items = new String[] { "复制内容" };

		// TODO 这里要取消注释
		// if (AccountHelper.isLogedIn(getActivity().getApplicationContext())) {
		// if (AVUser.getCurrentUser() != null) {
		// if (AVUser.getCurrentUser().getBoolean(XUser.IS_ADMIN)) {
		// items = null;
		//
		// boolean isSticky = mData.get(i).isSticky;
		// if (isSticky) {
		// items = new String[] { "复制内容", "取消置顶" };
		// } else {
		// items = new String[] { "复制内容", "置顶" };
		// }
		// }
		// }
		// }

		// TODO 这里要注释掉
		boolean isSticky = mData.get(i).isSticky;
		if (isSticky) {
			items = new String[] { "复制内容", "取消置顶" };
		} else {
			items = new String[] { "复制内容", "置顶" };
		}

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
				default:
					CommonUtil.SystemUtils.copy(getActivity(), mData.get(i).content_txt);
					break;
				case 1:
					// 置顶与取消置顶
					if (mData.get(i).isSticky) {
						BBSService.setStatusUnstick(mData.get(i).AVObjectID, new ToggleStatusStickCallback() {

							@Override
							public void onSuccess(Boolean isStick) {
								if (isStick) {
									ToastUtils.showShortToast("置顶成功");
								} else {
									ToastUtils.showShortToast("取消置顶成功");
								}
							}

							@Override
							public void onFailed(String errorTip) {
								ToastUtils.showShortToast(errorTip);
							}
						});
					} else {
						BBSService.setStatusStick(mData.get(i).AVObjectID, new ToggleStatusStickCallback() {

							@Override
							public void onSuccess(Boolean isStick) {
								if (isStick) {
									ToastUtils.showShortToast("置顶成功");
								} else {
									ToastUtils.showShortToast("取消置顶成功");
								}
							}

							@Override
							public void onFailed(String errorTip) {
								ToastUtils.showShortToast(errorTip);
							}
						});

					}
					// report(i);
					break;
				}
			}
		});
		builder.show();
	}

	/**
	 * 举报
	 * 
	 * @param i
	 */
	private void report(int i) {
		String statusId = mData.get(i).AVObjectID;
		BBSService.reportStatus(statusId);

		// 隐藏帖子
		mData.remove(i);
		mAdapter.notifyDataSetChanged();

		CommonUtil.ToastUtils.showShortToast(getActivity(), "感谢您的举报，我们将认真核实内容！");
	}
}