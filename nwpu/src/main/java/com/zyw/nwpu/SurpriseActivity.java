package com.zyw.nwpu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.zyw.nwpu.avos.XUser;
import com.zyw.nwpulib.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_surprise)
public class SurpriseActivity extends Activity {

	@ViewInject(R.id.tv_surprise)
	private TextView tv_surprise;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);

		tv_surprise.setText(sb.toString());

		getData();
	}

	private void getData() {
		AVQuery<AVUser> query = new AVQuery<AVUser>("_User");
		query.setLimit(1000);
		query.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> arg0, AVException arg1) {
				if (arg1 != null) {
					CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
					return;
				}
				for (int i = 0; i < arg0.size(); i++) {
					allUsr.add(arg0.get(i));
				}

				/////
				AVQuery<AVUser> query = new AVQuery<AVUser>("_User");
				query.setLimit(1000);
				query.findInBackground(new FindCallback<AVUser>() {

					@Override
					public void done(List<AVUser> arg0, AVException arg1) {
						if (arg1 != null) {
							CommonUtil.ToastUtils.showShortToast(getApplicationContext(), arg1.getLocalizedMessage());
							return;
						}
						for (int i = 0; i < arg0.size(); i++) {
							allUsr.add(arg0.get(i));
						}

						show();

					}

				});

				for (int i = 0; i < arg0.size(); i++) {
					// 性别
					if (arg0.get(i).getInt("gender") == 1)
						maleNum++;

					// 学院
					for (int j = 0; j < collegeName.length; j++) {
						if (arg0.get(i).getString("college").trim().compareTo(collegeName[j]) == 0) {
							collegeUserNum[j]++;
							break;
						}
					}
				}
				for (int j = 0; j < collegeName.length; j++) {
					if (!TextUtils.isEmpty(collegeName[j])) {
						sb.append(collegeName[j] + ":		");
						sb.append(collegeUserNum[j]);
						sb.append("\n");
					} else {
						sb.append("本科生:		");
						sb.append(collegeUserNum[j]);
						sb.append("\n");
					}
				}

				tv_surprise.setText(sb.toString());

			}
		});
	}

	List<AVUser> allUsr = new ArrayList<>();
	int[] collegeUserNum = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 17
	String[] collegeName = { "航空学院", "航天学院", "航海学院", "材料学院", "机电学院", "力学与土木建筑学院", "动力与能源学院", "电子信息学院", "自动化学院", "计算机学院",
			"理学院", "管理学院", "人文与经法学院", "软件与微电子学院", "生命学院", "外国语学院", "" };

	int total = 0;
	int maleNum = 0;
	int teacherNum = 0;
	int benkeNum = 0;
	int yanjiushengNum = 0;
	int boshiNum = 0;

	private StringBuffer sb = new StringBuffer("瓜大生活\n\n");

	private void show() {
		for (int i = 0; i < allUsr.size(); i++) {

			// 性别
			if (allUsr.get(i).getInt("gender") == 1)
				maleNum++;

			// 学院
			for (int j = 0; j < collegeName.length; j++) {
				if (allUsr.get(i).getString("college").trim().compareTo(collegeName[j]) == 0) {
					collegeUserNum[j]++;
					break;
				}
			}

			// 学号
			String id = allUsr.get(i).getString(XUser.STUDENTID).trim();
			if (id.length() == 10) {
				String key = id.substring(4, 5);
				if (TextUtils.equals(key, "0")) {
					teacherNum++;
				} else if (TextUtils.equals(key, "1")) {
					boshiNum++;
				} else if (TextUtils.equals(key, "2")) {
					yanjiushengNum++;
				} else if (TextUtils.equals(key, "3")) {
					benkeNum++;
				}
			}
		}
	}

	// private void getData() {
	// AVQuery<AVUser> query = new AVQuery<AVUser>("_User");
	// query.setLimit(1000);
	// query.findInBackground(new FindCallback<AVUser>() {
	//
	// @Override
	// public void done(List<AVUser> arg0, AVException arg1) {
	// if (arg1 != null) {
	// CommonUtil.ToastUtils.showShortToast(
	// getApplicationContext(), arg1.getLocalizedMessage());
	// return;
	// }
	//
	// total += arg0.size();
	// sb.append("总用户:");
	// sb.append(total);
	// sb.append("\n\n");
	// int maleNum = 0;
	//
	// String[] collegeName = { "航空学院", "航天学院", "航海学院", "材料学院",
	// "机电学院", "力学与土木建筑学院", "动力与能源学院", "电子信息学院", "自动化学院",
	// "计算机学院", "理学院", "管理学院", "人文与经法学院", "软件与微电子学院", "生命学院",
	// "外国语学院", "" };
	//
	// for (int i = 0; i < arg0.size(); i++) {
	// // 性别
	// if (arg0.get(i).getInt("gender") == 1)
	// maleNum++;
	//
	// // 学院
	// for (int j = 0; j < collegeName.length; j++) {
	// if (arg0.get(i).getString("college").trim()
	// .compareTo(collegeName[j]) == 0) {
	// collegeUserNum[j]++;
	// break;
	// }
	// }
	// }
	// sb.append("男生: ");
	// sb.append(maleNum);
	// sb.append("\n");
	// sb.append("女生: ");
	// sb.append(total - maleNum);
	// sb.append("\n\n");
	//
	// for (int j = 0; j < collegeName.length; j++) {
	// if (!TextUtils.isEmpty(collegeName[j])) {
	// sb.append(collegeName[j] + ": ");
	// sb.append(collegeUserNum[j]);
	// sb.append("\n");
	// } else {
	// sb.append("本科生: ");
	// sb.append(collegeUserNum[j]);
	// sb.append("\n");
	// }
	// }
	//
	// tv_surprise.setText(sb.toString());
	//
	// }
	// });
	// }

	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, SurpriseActivity.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.fade_ins, R.anim.show_out);
	}
}
