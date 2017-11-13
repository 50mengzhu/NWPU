package com.zyw.nwpu;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.easemob.easeui.domain.EaseEmojicon;
import com.easemob.easeui.widget.EaseChatInputMenu;
import com.easemob.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.base.TitleBar;
import com.zyw.nwpu.jifen.JiangpinActivity;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper;
import com.zyw.nwpu.jifen.leancloud.ScoreHelper.BuyProductCallback;
import com.zyw.nwpu.robot.RobotMsg;
import com.zyw.nwpu.robot.RobotMsg.MsgType;
import com.zyw.nwpu.robot.RobotMsgAdapter;
import com.zyw.nwpu.robot.SearchResultList.OnItemClick;
import com.zyw.nwpu.service.RobotServer;
import com.zyw.nwpu.service.RobotServer.QueryChapterCallback;
import com.zyw.nwpu.service.RobotServer.QueryQuestionCallback;
import com.zyw.nwpulib.utils.CommonUtil.ToastUtils;
import com.zyw.nwpulib.utils.JsonParser;

/**
 * 
 * @author Rocket
 * 
 */
@ContentView(R.layout.activity_robot)
public class RobotActivity extends BaseActivity implements OnClickListener {

	private TitleBar bar;
	private EaseChatInputMenu inputMenu;

	private ListView lv_chat;
	private List<RobotMsg> msgList;
	private RobotMsgAdapter adapter;

	private final String robotName = "百问百答";
	private String userAvatarUrl = "http://img4.imgtn.bdimg.com/it/u=1970775010,3974966098&fm=23&gp=0.jpg";// 用户头像url
	private String robotAvatarUrl = "http://img0.imgtn.bdimg.com/it/u=1519095833,1566178015&fm=21&gp=0.jpg";// 小助手的头像

	// private RobotServer robotServer = null;

	/* 讯飞语音识别 */
	// 语音听写对象
	// private SpeechRecognizer mIat;
	// 语音听写UI
	// private RecognizerDialog iatDialog;
	// private Toast mToast;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setSlideBackEnable(false);

		bar = (TitleBar) findViewById(R.id.head);
		bar.setTitle(robotName);

		// robotServer = new RobotServer(this, listener);
		// robotServer.setIsAutoSpeakAnswer(false);

		// iniIFly();

		onReceiveMessage("有什么可以帮你的？可以发文字或语音给我哦~");

		RobotServer.queryChapters(new QueryChapterCallback() {

			@Override
			public void onGet(List<String> list) {
				if (list == null || list.size() == 0)
					return;
				onReceiveMessage(list, "");

				// 滚动到头部
				lv_chat.setSelection(0);
			}

			@Override
			public void onFailure(String tip) {

			}
		});
	}

	// private void iniIFly() {
	// // 初始化识别对象
	// mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
	// // 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
	// iatDialog = new RecognizerDialog(this, mInitListener);
	// mSharedPreferences = getSharedPreferences("com.iflytek.setting",
	// Activity.MODE_PRIVATE);
	// }

	// /**
	// * 初始化监听器。
	// */
	// private InitListener mInitListener = new InitListener() {
	//
	// @Override
	// public void onInit(int code) {
	// if (code != ErrorCode.SUCCESS) {
	// ToastUtils.showShortToast("讯飞语音识别初始化失败，错误码：" + code);
	// }
	// }
	// };

	// /*
	// * 开始识别
	// */
	// protected void startrecognizer() {
	// // 设置参数
	// setParam();
	// iatDialog.setListener(recognizerDialogListener);
	// iatDialog.show();
	// }

	// private RobotServer.OnReceiveResultListener listener = new
	// RobotServer.OnReceiveResultListener() {
	//
	// @Override
	// public void onRecognizeError(String err) {
	// bar.setTitle(err);
	// }
	//
	// @Override
	// public void onRecognize(String sentence) {
	// bar.setTitle(sentence);
	// }
	//
	// @Override
	// public void onReceiveQuestionList(List<String> list, String keyWord) {
	// onReceiveMessage(list, keyWord);
	// }
	//
	// @Override
	// public void onReceiveResult(String res, Boolean isImage) {
	// onReceiveMessage(res, isImage);
	// }
	// };

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, RobotActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.btn_gender:
		// break;
		// }
	}

	@Override
	public void initView() {
		iniListView();

		inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
		inputMenu.init(null);
		inputMenu.setFaceLayoutVisible(false);
		inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

			@Override
			public void onSendMessage(String content) {
				sendTextMessage(content);
			}

			@Override
			public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
				// return false;
				return onTouchSpeakBtn(v, event);
			}

			@Override
			public void onBigExpressionClicked(EaseEmojicon emojicon) {
				ToastUtils.showShortToast("onBigExpressionClicked");
			}
		});
	}

	private void iniListView() {
		lv_chat = (ListView) findViewById(R.id.lv_chat);
		msgList = new ArrayList<RobotMsg>();
		adapter = new RobotMsgAdapter(this, msgList, new OnItemClick() {

			@Override
			public void onItemClick(String text) {
				sendTextMessage(text);
			}
		});
		lv_chat.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (msgList.get(arg2).getType() == RobotMsg.MsgType.RECEIVE_IMAGE) {
					FullScreenPhotoViewActivity.startThis(RobotActivity.this, msgList.get(arg2).getContent());
				}
			}
		});
		lv_chat.setAdapter(adapter);
	}

	private boolean onTouchSpeakBtn(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			v.setPressed(true);
			// startrecognizer();
			return true;
		case MotionEvent.ACTION_MOVE:
			return true;
		case MotionEvent.ACTION_UP:
			v.setPressed(false);
			return true;
		default:
			return false;
		}
	}

	private void onReceiveMessage(String content) {
		onReceiveMessage(content, false);
	}

	private void onReceiveMessage(String content, Boolean isImage) {
		if (TextUtils.isEmpty(content))
			return;
		if (isImage) {
			msgList.add(new RobotMsg(content, RobotMsg.MsgType.RECEIVE_IMAGE, robotAvatarUrl));
		} else {
			msgList.add(new RobotMsg(content, RobotMsg.MsgType.RECEIVE, robotAvatarUrl));
		}

		adapter.notifyDataSetChanged();
		// 滚动到末尾
		lv_chat.setSelection(adapter.getCount() - 1);
	}

	private void onReceiveMessage(List<String> list, String keyWord) {
		if (list == null || list.size() == 0)
			return;

		RobotMsg msg = new RobotMsg();
		msg.setType(MsgType.LIST);
		msg.setList(list);
		msg.setKeyWord(keyWord);
		msg.setAvatarUrl(robotAvatarUrl);
		msgList.add(msg);

		adapter.notifyDataSetChanged();
		// 滚动到末尾
		lv_chat.setSelection(adapter.getCount() - 1);
	}

	// 发送消息方法
	// ==========================================================================
	protected void sendTextMessage(final String content) {
		if (TextUtils.isEmpty(content))
			return;

		// 保存到后台
		AVObject obj = new AVObject("QAUserRecord");
		obj.put("user", AVUser.getCurrentUser());
		obj.put("question", content);
		obj.saveInBackground();

		// robotServer.read(content);
		msgList.add(new RobotMsg(content, RobotMsg.MsgType.SEND, userAvatarUrl));
		adapter.notifyDataSetChanged();
		// 滚动到末尾
		lv_chat.setSelection(adapter.getCount() - 1);

		// 查询
		RobotServer.queryFromQuestionLib(content, new QueryQuestionCallback() {

			@Override
			public void onQuestionList(List<String> list) {
				// 返回文本结果
				onReceiveMessage(list, content);
			}

			@Override
			public void onFailure(String tip) {
				ToastUtils.showShortToast(tip);
			}

			@Override
			public void onAnswer(String answer, Boolean isImage) {
				onReceiveMessage(answer, isImage);
			}

			@Override
			public void onNotKnow(String question) {
				// 没有查询到
				onNotKnowQuestion(question);
			}
		});
	}

	private void onNotKnowQuestion(final String question) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		// 设置对话框内的文本
		builder.setMessage("本机器人并不知道这个问题，要把这个问题发往论坛 问问机智的人类吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				BBSPublishActivity.startThisWithQuestionAndTag(RobotActivity.this, "瓜大问答", question);
			}
		});

		// 设置取消按钮
		builder.setNegativeButton("取消", null);
		// 使用builder创建出对话框对象
		AlertDialog dialog = builder.create();
		// 显示对话框
		dialog.show();
	}

	// /**
	// * 参数设置
	// *
	// * @param param
	// * @return
	// */
	// @SuppressLint("SdCardPath")
	// public void setParam() {
	// String lag = mSharedPreferences.getString("iat_language_preference",
	// "mandarin");
	// if (lag.equals("en_us")) {
	// // 设置语言
	// mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
	// } else {
	// // 设置语言
	// mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
	// // 设置语言区域
	// mIat.setParameter(SpeechConstant.ACCENT, lag);
	// }
	// // 设置语音前端点
	// mIat.setParameter(SpeechConstant.VAD_BOS,
	// mSharedPreferences.getString("iat_vadbos_preference", "4000"));
	// // 设置语音后端点
	// mIat.setParameter(SpeechConstant.VAD_EOS,
	// mSharedPreferences.getString("iat_vadeos_preference", "1000"));
	// // 设置标点符号
	// mIat.setParameter(SpeechConstant.ASR_PTT,
	// mSharedPreferences.getString("iat_punc_preference", "0"));
	// // 设置音频保存路径
	// mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
	// "/sdcard/iflytek/wavaudio.pcm");
	// }

	// /**
	// * 听写监听器。
	// */
	// private RecognizerListener recognizerListener = new RecognizerListener()
	// {
	//
	// @Override
	// public void onBeginOfSpeech() {
	// }
	//
	// @Override
	// public void onError(SpeechError error) {
	// }
	//
	// @Override
	// public void onEndOfSpeech() {
	// }
	//
	// @Override
	// public void onResult(RecognizerResult results, boolean isLast) {
	// String text = JsonParser.parseIatResult(results.getResultString());
	// sendTextMessage(text);
	// if (isLast) {
	// // TODO 最后的结果
	// }
	// }
	//
	// @Override
	// public void onVolumeChanged(int volume) {
	// }
	//
	// @Override
	// public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
	// }
	// };
	//
	// /**
	// * 听写UI监听器
	// */
	// private RecognizerDialogListener recognizerDialogListener = new
	// RecognizerDialogListener() {
	// public void onResult(RecognizerResult results, boolean isLast) {
	// String text = JsonParser.parseIatResult(results.getResultString());
	// sendTextMessage(text);
	// }
	//
	// /**
	// * 识别回调错误.
	// */
	// public void onError(SpeechError error) {
	// }
	// };
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// // 退出时释放连接
	// mIat.cancel();
	// mIat.destroy();
	// }
}
