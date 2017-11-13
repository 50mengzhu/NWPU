package com.zyw.nwpu.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeListener;
import com.turing.androidsdk.asr.VoiceRecognizeManager;
import com.turing.androidsdk.tts.TTSListener;
import com.turing.androidsdk.tts.TTSManager;
import com.zyw.nwpu.app.Const;

public class RobotServer {

	private final String TAG = RobotServer.class.getSimpleName();
	private final String TURING_APIKEY = "67b91c4adc84b3d7728e994b77652598";
	private final String TURING_SECRET = "b15539d544591061";
	private final String UNIQUEID = "131313131aw";

	// 百度语音识别与TTS
	private final String BD_APIKEY = "8wYrnpAb657eEGpaTEq52Hhx";
	private final String BD_SECRET = "2nGq7b7S24erArlO0qncCSk1lkcFXw2n";

	private Context cxt;
	// private TuringApiManager mTuringApiManager;
	private OnReceiveResultListener listener;

	// private TTSManager ttsManager;
	// private VoiceRecognizeManager recognizerManager;

	private final boolean isAutoSpeakAnswer = false;// TTS还不稳定

	public interface QueryQuestionCallback {
		public void onAnswer(String answer, Boolean isImage);

		public void onQuestionList(List<String> list);

		public void onFailure(String tip);

		public void onNotKnow(String question);

	}

	public interface QueryChapterCallback {
		public void onGet(List<String> list);

		public void onFailure(String tip);
	}

	public static final void addTimes(String question) {

	}

	public static final void queryChapters(final QueryChapterCallback callback) {
		if (callback == null)
			return;

		AVQuery<AVObject> query = new AVQuery<AVObject>("QAChapters");
		query.whereEqualTo("isEnable", true);
		query.setLimit(1000);// 最多返回1000条相关的结果
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("query err" + arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					callback.onFailure("no result");
					return;
				}

				List<String> list = new ArrayList<String>();
				for (int i = 0; i < arg0.size(); i++) {
					String name = arg0.get(i).getString("name");
					list.add(name);
				}
				callback.onGet(list);
				return;
			}
		});
	}

	/**
	 * 从问题库里查询
	 * 
	 * @param originalQuestion
	 * @param callback
	 */
	public static final void queryFromQuestionLib(final String originalQuestion, final QueryQuestionCallback callback) {

		if (callback == null || TextUtils.isEmpty(originalQuestion) || TextUtils.isEmpty(originalQuestion.trim()))
			return;

		final String keyWords = originalQuestion.trim();

		AVQuery<AVObject> query1 = new AVQuery<AVObject>("QAs");
		query1.whereContains("question", keyWords);
		query1.whereEqualTo("isEnable", true);
		query1.setLimit(100);// 最多返回100条相关的结果

		AVQuery<AVObject> query2 = new AVQuery<AVObject>("QAs");
		query2.whereContains("chapter", keyWords);
		query2.whereEqualTo("isEnable", true);
		query2.setLimit(100);// 最多返回100条相关的结果

		AVQuery<AVObject> query = AVQuery.or(Arrays.asList(query1, query2));
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 != null) {
					callback.onFailure("query err" + arg1.getLocalizedMessage());
					return;
				}

				if (arg0 == null || arg0.size() == 0) {
					callback.onNotKnow(originalQuestion);
					return;
				}

				List<String> list = new ArrayList<String>();
				for (int i = 0; i < arg0.size(); i++) {
					String question = arg0.get(i).getString("question");
					if (TextUtils.equals(question, keyWords)) {
						String answer = arg0.get(i).getString("answer");
						Boolean isImage = arg0.get(i).getBoolean("isImage");
						if (isImage) {
							AVFile img = arg0.get(i).getAVFile("imageAnswer");
							if (img != null) {
								String imageUrl = img.getUrl();
								// String imageUrl = img.getThumbnailUrl(false,
								// 100, 100, 50, "jpg");// 图片像素为100
								callback.onAnswer(imageUrl, isImage);
							}
						} else {
							callback.onAnswer(answer, isImage);
						}

						// 将次数加1
						arg0.get(i).increment("times");
						arg0.get(i).saveInBackground();
						return;
					}
					list.add(question);
				}
				callback.onQuestionList(list);
				return;
			}
		});
	}

	// public void setIsAutoSpeakAnswer(boolean is) {
	// this.isAutoSpeakAnswer = is;
	// }

	public interface OnReceiveResultListener {
		/**
		 * 接收到图灵的结果
		 * 
		 * @param res
		 */
		public void onReceiveResult(String res, Boolean isImage);

		public void onReceiveQuestionList(List<String> list, String keyWord);

		public void onNotKnow(String question);

		public void onFailure(String err);

		// /**
		// * 语音识别错误
		// *
		// * @param err
		// */
		// public void onRecognizeError(String err);
		//
		// public void onRecognize(String sentence);
	}

	public RobotServer(Context context, OnReceiveResultListener l) {
		this.cxt = context;
		this.listener = l;

		// init();
	}

	public void read(final String request) {
		if (TextUtils.isEmpty(request))
			return;

		RobotServer.queryFromQuestionLib(request, new QueryQuestionCallback() {

			@Override
			public void onQuestionList(List<String> list) {
				// 返回文本结果
				if (listener != null)
					listener.onReceiveQuestionList(list, request);
			}

			@Override
			public void onFailure(String tip) {
				if (listener != null)
					listener.onFailure(tip);
				// // 从题库查询失败后，去问机器人
				// if (mTuringApiManager != null)
				// mTuringApiManager.requestTuringAPI(request);
			}

			@Override
			public void onAnswer(String answer, Boolean isImage) {
				// 返回结果
				if (listener != null)
					listener.onReceiveResult(answer, isImage);
			}

			@Override
			public void onNotKnow(String question) {
				if (listener != null)
					listener.onNotKnow(question);
			}
		});

	}

	/**
	 * 初始化turingSDK
	 * 
	 */
	// private void init() {
	// SDKInitBuilder builder = new
	// SDKInitBuilder(cxt).setSecret(TURING_SECRET).setTuringKey(TURING_APIKEY)
	// .setUniqueId(UNIQUEID);
	// SDKInit.init(builder, new InitListener() {
	// @Override
	// public void onFail(String error) {
	// Log.d(TAG, error);
	// }
	//
	// @Override
	// public void onComplete() {
	// // 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
	// mTuringApiManager = new TuringApiManager(cxt);
	// mTuringApiManager.setHttpListener(myHttpConnectionListener);
	// }
	// });
	//
	// // 初始化TTS
	// // initTTS();
	// // initVoiceRecognize();
	// }

	/**
	 * 网络请求回调
	 */
	HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {

		@Override
		public void onSuccess(RequestResult result) {
			if (result != null) {
				try {
					Log.d(TAG, result.getContent().toString());
					JSONObject result_obj = new JSONObject(result.getContent().toString());
					if (result_obj.has("text")) {
						String res = result_obj.get("text").toString();

						// 读出结果
						// if (isAutoSpeakAnswer) {
						// RobotServer.this.speak(res);
						// }
						// 返回文本结果
						if (listener != null)
							listener.onReceiveResult(res, false);
					}
				} catch (JSONException e) {
					Log.d(TAG, "JSONException:" + e.getMessage());
				}
			}
		}

		@Override
		public void onError(ErrorMessage errorMessage) {
			Log.d(TAG, errorMessage.getMessage());
		}
	};

	/** 语音识别相关 ******************************************************************************/
	// private void initVoiceRecognize() {
	// recognizerManager = new VoiceRecognizeManager(cxt, BD_APIKEY, BD_SECRET);
	// recognizerManager.setVoiceRecognizeListener(myVoiceRecognizeListener);
	// // recognizerManager.startRecognize();
	// }
	//
	// public void listen() {
	// recognizerManager.startRecognize();
	// }
	//
	// public void stopListen() {
	// recognizerManager.stopRecognize();
	// }

	/**
	 * 语音识别回调
	 */
	// VoiceRecognizeListener myVoiceRecognizeListener = new
	// VoiceRecognizeListener() {
	//
	// @Override
	// public void onVolumeChange(int volume) {
	// // 仅讯飞回调
	// }
	//
	// @Override
	// public void onStartRecognize() {
	// // 仅针对百度回调
	// }
	//
	// @Override
	// public void onRecordStart() {
	//
	// }
	//
	// @Override
	// public void onRecordEnd() {
	//
	// }
	//
	// @Override
	// public void onRecognizeResult(String result) {
	// if (result == null) {
	// recognizerManager.startRecognize();
	// RobotServer.this.read(result);
	// }
	// }
	//
	// @Override
	// public void onRecognizeError(String error) {
	// if (listener != null) {
	// listener.onRecognizeError("识别错误：" + error);
	// }
	// recognizerManager.startRecognize();
	// }
	// };

	/** TTS相关 **************************************************************************/
	// private void initTTS() {
	// ttsManager = new TTSManager(cxt, BD_APIKEY, BD_SECRET);
	// ttsManager.setTTSListener(myTTSListener);
	// }
	//
	// public void speak(String content) {
	// if (ttsManager != null) {
	// ttsManager.startTTS(content);
	// }
	// }
	//
	// /**
	// * TTS回调
	// */
	// TTSListener myTTSListener = new TTSListener() {
	//
	// @Override
	// public void onSpeechStart() {
	// Log.d(TAG, "TTS Start!");
	// }
	//
	// @Override
	// public void onSpeechProgressChanged() {
	//
	// }
	//
	// @Override
	// public void onSpeechPause() {
	// Log.d(TAG, "TTS Pause!");
	// }
	//
	// @Override
	// public void onSpeechFinish() {
	// ttsManager.resumeTTS();
	// }
	//
	// @Override
	// public void onSpeechError(int errorCode) {
	// Log.d(TAG, "TTS错误，错误码：" + errorCode);
	// }
	//
	// @Override
	// public void onSpeechCancel() {
	// Log.d(TAG, "TTS Cancle!");
	// }
	// };
}
