package com.zyw.nwpu.service;

import java.util.List;

import com.zyw.nwpulib.model.CommentData;
import com.zyw.nwpulib.model.LikeData;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpulib.model.StatusData;

public class BBSServiceCallback {

	public interface GetStatusCallback {
		public void onFailed(String errorTip);

		public void onSuccess(Status data);
	}

	public interface ToggleStatusStickCallback {
		public void onFailed(String errorTip);

		public void onSuccess(Boolean isStick);
	}

	public interface GetStatusListCallback {
		public void onFailed(String errorTip);

		public void onSuccess(List<StatusData> data);
	}

	public interface AddCommentCallback {
		public void onFailed(String errorTip);

		public void onSuccess();
	}

	public interface DeleteStatusCallback {
		public void onFailed(String errorTip);

		public void onSuccess();
	}

	public interface GetCommentsListCallback {
		public void onFailed(String errorTip);

		public void onSuccess(List<CommentData> data);
	}

	public interface GetLikeListCallback {
		public void onFailed(String errorTip);

		public void onSuccess(List<LikeData> data);
	}

	public interface GetTopicListCallback {
		public void onFailed(String errorTip);

		public void onSuccess(List<String> topics);
	}

	public interface RemoveCommentCallback {
		public void onFailed(String errorTip);

		public void onSuccess();
	}

}
