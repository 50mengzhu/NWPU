package com.zyw.nwpu.fragment;

import java.util.ArrayList;
import android.support.v4.app.Fragment;

import java.util.List;

import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.zyw.nwpu.PickHomeTownActivity;
import com.zyw.nwpu.R;
import com.zyw.nwpu.Login;
import com.zyw.nwpu.UserListActivity;
import com.zyw.nwpu.app.AccountHelper;
import com.zyw.nwpu.app.AppApplication;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpu.service.AppConfig;
import com.zyw.nwpu.service.UserInfoService.SearchCondition;
import com.zyw.nwpulib.utils.CommonUtil;
import com.zyw.nwpu.view.MDatePicker;
import com.zyw.nwpu.view.MDatePicker.OnChooseEvent;

public class ConditionSearchUser_Fragment extends Fragment implements OnClickListener {

	private List<SearchCondition> conditions = new ArrayList<SearchCondition>();

	private TextView tv_name;
	private TextView tv_college;
	private TextView tv_birthday;
	private TextView tv_hometown;

	private String nickname = "";
	private String college = "";
	private String birthday = "";
	private String hometown = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_conditionsearchuser, container, false);

		rootView.findViewById(R.id.ll_nickname).setOnClickListener(this);
		rootView.findViewById(R.id.ll_college).setOnClickListener(this);
		rootView.findViewById(R.id.ll_birthday).setOnClickListener(this);
		rootView.findViewById(R.id.ll_hometown).setOnClickListener(this);

		tv_name = (TextView) rootView.findViewById(R.id.tv_name);
		tv_college = (TextView) rootView.findViewById(R.id.tv_college);
		tv_birthday = (TextView) rootView.findViewById(R.id.tv_birthday);
		tv_hometown = (TextView) rootView.findViewById(R.id.tv_hometown);

		rootView.findViewById(R.id.btn_search).setOnClickListener(this);

		return rootView;
	}

	private void refreshTextView() {
		tv_name.setText(nickname);
		tv_college.setText(college);
		tv_birthday.setText(birthday);
		tv_hometown.setText(hometown);
	}

	// public static void startThis(Context cxt) {
	// // 判断是否已经登录
	// if (!AccountHelper.isLogedIn(cxt)) {
	// CommonUtil.ToastUtils.showShortToast(cxt, "请先登录");
	// Login.startThis(cxt);
	// } else {
	// Intent intent = new Intent(cxt, ConditionSearchUser_Fragment.class);
	// cxt.startActivity(intent);
	// ((Activity) cxt).overridePendingTransition(R.anim.slide_in_right,
	// R.anim.fade_outs);
	// }
	// }

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_nickname:
			toInputName();
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
		case R.id.btn_search:
			searchUser();
			break;

		default:
			break;
		}
	}

	private void searchUser() {

		conditions.clear();

		if (!TextUtils.isEmpty(nickname)) {
			conditions.add(new SearchCondition(XUser.NICKNAME, nickname));
		}
		if (!TextUtils.isEmpty(birthday)) {
			conditions.add(new SearchCondition(XUser.BIRTHDAY, birthday));
		}
		if (!TextUtils.isEmpty(hometown)) {
			conditions.add(new SearchCondition(XUser.HOMETOWN, hometown));
		}
		if (!TextUtils.isEmpty(college)) {
			conditions.add(new SearchCondition(XUser.COLLEGE, college + " "));// 注意，数据库的学院信息多存了一个空格
		}
		UserListActivity.startThis(getActivity(), conditions);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			return;
		}

		switch (requestCode) {

		case HOMETOWN_REQUEST_CODE:
			// 选择家乡返回
			if (!TextUtils.isEmpty(AppApplication.homeTown)) {
				// 家乡
				hometown = AppApplication.homeTown;
			} else {
				hometown = "";
			}
			refreshTextView();
			break;
		}
	}

	private final int HOMETOWN_REQUEST_CODE = 3;

	/**
	 * 选择家乡
	 */
	private void toPickHomeTown() {
		PickHomeTownActivity.startThisForResult(getActivity(), HOMETOWN_REQUEST_CODE);
	}

	private void toInputName() {
		final EditText et_nickname = new EditText(getActivity());
		et_nickname.setMaxEms(AppConfig.NICKNAME_MAX_EMS);
		new AlertDialog.Builder(getActivity()).setTitle("请设置昵称").setView(et_nickname)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						nickname = et_nickname.getText().toString();
						refreshTextView();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						nickname = "";
						refreshTextView();
					}
				}).show();
	}

	private String[] collegeName = { "航空学院", "航天学院", "航海学院", "材料学院", "机电学院", "力学与土木建筑学院", "动力与能源学院", "电子信息学院", "自动化学院",
			"计算机学院", "理学院", "管理学院", "人文与经法学院", "软件与微电子学院", "生命学院", "外国语学院", "教育实验学院", "其他", "取消" };

	/**
	 * 选择学院
	 */
	private void toPickCollege() {
		AlertDialog.Builder b = new Builder(getActivity());
		b.setItems(collegeName, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				if (TextUtils.equals(collegeName[arg1], "取消")) {
					college = "";
				} else {
					college = collegeName[arg1];
				}

				refreshTextView();
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
			public void onChoose(String str) {
				if (!TextUtils.isEmpty(str)) {
					birthday = str;
				} else {
					birthday = "";
				}
				refreshTextView();
			}
		});
	}

}
