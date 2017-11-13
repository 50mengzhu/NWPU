package com.zyw.nwpu.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.zyw.nwpu.app.Const;
import com.zyw.nwpulib.utils.CommonUtil.AppUtils;

import android.content.Intent;
import android.text.TextUtils;

public class ChaoqitixingService {

	public static class ChaoQiInfo {
		private String unhandledOverdueBookNum = "0";// 未处理的欠款记录
		private String money = "0";// 未处理的欠款总金额
		private String overdueBookNum = "0";// 当前超期图书数

		public ChaoQiInfo() {
		}

		public String getUnhandledOverdueBookNum() {
			if (!TextUtils.isEmpty(unhandledOverdueBookNum))
				return unhandledOverdueBookNum;
			else
				return "0";
		}

		public void setUnhandledOverdueBookNum(String unhandledOverdueBookNum) {
			this.unhandledOverdueBookNum = unhandledOverdueBookNum;
		}

		public String getMoney() {
			if (!TextUtils.isEmpty(money))
				return money;
			else
				return "0";
		}

		public void setMoney(String money) {
			this.money = money;
		}

		public String getOverdueBookNum() {
			if (!TextUtils.isEmpty(overdueBookNum))
				return overdueBookNum;
			else
				return "0";
		}

		public void setOverdueBookNum(String overdueBookNum) {
			this.overdueBookNum = overdueBookNum;
		}
	}

	/**
	 * 回调接口
	 * 
	 * @author Rocket
	 *
	 */
	public interface GetChaoqiInfoCallback {
		public void onFailed(String errorTip);

		public void onSuccess(ChaoQiInfo data);
	}

	/**
	 * 获取超期图书信息
	 * 
	 * @param isNotify
	 *            是否推送通知
	 * 
	 * @param studentId
	 *            学号
	 * @param callback
	 */
	public static final void getChaoqiInfo(final Intent intent, final String studentId,
			final GetChaoqiInfoCallback callback) {

		RequestParams param = new RequestParams(Const.chaoqitixing_url);
		param.addParameter("userid", studentId);
		x.http().get(param, new CommonCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				if (callback != null)
					callback.onFailed("查询出错：" + arg0.getLocalizedMessage());
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(String arg0) {
				// 解析
				ChaoQiInfo info = parseResult(arg0);

				if (intent != null && info != null && !TextUtils.isEmpty(info.getOverdueBookNum())
						&& info.getOverdueBookNum().compareTo("0") != 0) {
					Notifier.notify(intent, "借书超期提醒", "您有" + info.getOverdueBookNum() + "本图书超期未还",
							"【" + AppUtils.getAppName(x.app()) + "】" + "借书超期提醒");
				}

				if (callback != null)
					callback.onSuccess(info);
			}
		});
	}

	private static ChaoQiInfo parseResult(String text) {
		if (TextUtils.isEmpty(text))
			return new ChaoQiInfo();
		JSONTokener jsonParser = new JSONTokener(text);
		JSONObject js;

		ChaoQiInfo info = new ChaoQiInfo();

		try {
			js = (JSONObject) jsonParser.nextValue();
			info.setUnhandledOverdueBookNum(js.getString("debtnote"));
			info.setMoney(js.getString("debt"));
			info.setOverdueBookNum(js.getString("beyondnote"));

		} catch (JSONException e) {
		}

		return info;
	}

}
