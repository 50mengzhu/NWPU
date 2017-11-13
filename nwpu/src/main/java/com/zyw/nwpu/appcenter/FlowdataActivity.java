package com.zyw.nwpu.appcenter;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ContentView(R.layout.activity_flowdata)
public class FlowdataActivity extends BaseActivity {
	private WaterWaveView mWaterWaveView;
	private TextView mLeftMoney;
	private TextView mTaoCan;
	private TextView mUsedFlow;
	private TextView mLeftFlow;
	private TextView mLeftFlowText;
	private int itaocan;
	private float iusedflow;// 确定所占百分比
	private float ioverflow;
	private float imoneyflow;// 确定超出流量时所占百分比
	private float ileftflow;
	private float irate;
	private boolean over = false;
	private String sUsedFlow;
	private String sLeftFlow;
	Pattern p;// 正则表达式存储
	Matcher m;// 匹配结果

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initView() {

		mWaterWaveView = (WaterWaveView) findViewById(R.id.wave_view);

		mTaoCan = (TextView) findViewById(R.id.taocan);
		mLeftFlow = (TextView) findViewById(R.id.leftflow);
		mLeftMoney = (TextView) findViewById(R.id.leftmoney);
		mUsedFlow = (TextView) findViewById(R.id.usedflow);
		mLeftFlowText = (TextView) findViewById(R.id.leftflowtext);
		String data = new String(getIntent().getStringExtra("Data"));

		// 读取JSON数据
		try {
			JSONObject jsonObject = new JSONObject(data);
			p = Pattern.compile("\\d+(?=G)");
			Log.i("info", jsonObject.getString("taocan"));
			m = p.matcher(jsonObject.getString("taocan"));
			if (m.find()) {
				Log.i("info", "套餐(G):" + m.group());
				itaocan = Integer.parseInt(m.group());// 套餐为
				// Log.i("info", "itaocan:" + itaocan);
			}
			// 如果有赠送套餐
			if (m.find()) {
				Log.i("info", "套餐(G):" + m.group());
				itaocan += Integer.parseInt(m.group());// 赠送套餐为
			}
			if (jsonObject.isNull("yiyongliuliang")) {
				// 已用流量
				Log.i("info", "没有超出流量");
				over = false;
				// 提取已用流量GB
				String[] taocanliuliang = jsonObject.getString("taocanliuliang").split("/", 2);
				p = Pattern.compile("\\d+\\s(?=GB)");
				Log.i("info", taocanliuliang[0]);
				m = p.matcher(taocanliuliang[0]);
				if (m.find()) {
					Log.i("info", "已用流量(G):" + m.group());
					try {
						iusedflow = (float) Integer.parseInt(m.group().trim());
					} catch (NumberFormatException e) {
						e.printStackTrace();
						Log.i("info", "error");
						Toast.makeText(FlowdataActivity.this, "数据转换出错", Toast.LENGTH_SHORT).show();
					}
				}
				// 提取多少MB
				// 提取已用流量
				p = Pattern.compile("\\s*([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)\\s*(?=MB)");
				Log.i("info", taocanliuliang[0]);
				m = p.matcher(taocanliuliang[0]);
				if (m.find()) {
					Log.i("info", "已用流量(MB):" + m.group());
					try {
						iusedflow += Float.parseFloat(m.group().trim()) / 1024;
					} catch (NumberFormatException e) {
						e.printStackTrace();
						Log.i("info", "error");
						Toast.makeText(FlowdataActivity.this, "数据转换出错", Toast.LENGTH_SHORT).show();
					}
				}
				Log.i("info", "iusedflow:" + iusedflow);
				ileftflow = (float) itaocan - iusedflow;
				Log.i("info", "ileftfow:" + ileftflow);
				Log.i("info", "套餐内使用流量:" + taocanliuliang[0].substring(7));
				sUsedFlow = taocanliuliang[0].substring(7);

				sLeftFlow = FloatToString(ileftflow);

			} else {// 如果超出流量
				// 已用流量
				over = true;
				p = Pattern.compile("([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)");
				Log.i("info", jsonObject.getString("yiyongliuliang"));
				m = p.matcher(jsonObject.getString("yiyongliuliang"));
				if (m.find()) {
					Log.i("info", "已用流量(G):" + m.group());
					iusedflow = Integer.parseInt(m.group().trim());// 提取多少G
				}
				if (m.find()) {
					Log.i("info", "已用流量(MB):" + m.group());
					iusedflow += Float.parseFloat(m.group()) / 1024;// 提取多少G
				}
				// 金钱对应的流量
				p = Pattern.compile("([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)");
				Log.i("info", jsonObject.getString("keyongliuliang"));
				m = p.matcher(jsonObject.getString("keyongliuliang"));
				if (m.find()) {
					Log.i("info", "可用流量(G):" + m.group());
					imoneyflow = Integer.parseInt(m.group().trim());// 提取多少G
				}
				if (m.find()) {
					Log.i("info", "已用流量(MB):" + m.group());
					imoneyflow += Float.parseFloat(m.group()) / 1024;// 提取多少G
				}
				ileftflow = iusedflow;// 超出流量
				iusedflow += (float) itaocan;// 所有已用流量
				// 剩余流量
				sUsedFlow = FloatToString(iusedflow);
				sLeftFlow = FloatToString(ileftflow);
				Log.i("info", "iused flow: " + iusedflow + " ileftflow: " + ileftflow + " iTaocao:" + itaocan);
			}
			mUsedFlow.setText(sUsedFlow);
			mLeftFlow.setText(sLeftFlow);
			mLeftMoney.setText(jsonObject.getString("yue"));
			mTaoCan.setText(jsonObject.getString("taocan"));
			mWaterWaveView.setTopText(String.valueOf((int) ((ileftflow * 10 + 5) / 10)) + "G");
			if (!over) {// 如果没有超出流量
				mWaterWaveView.setIsOver(false);
				irate = (itaocan - iusedflow) / itaocan;
				// 如果流量过低,30%
				if (irate <= 0.3)
					mLeftFlow.setTextColor(getResources().getColor(R.color.over_text));
			} else {// 如果有超出流量
				mWaterWaveView.setIsOver(true);
				mLeftFlowText.setText("超出流量");
				mLeftFlow.setTextColor(getResources().getColorStateList(R.color.over_text));
				Log.i("info", "over:" + ileftflow + "money" + imoneyflow);
				irate = ileftflow / (ileftflow + imoneyflow);
			}
		} catch (JSONException je) {
			je.printStackTrace();
			this.finish();
			Toast.makeText(FlowdataActivity.this, "用户名或者密码错误\n", Toast.LENGTH_SHORT).show();
		}
		// 设置数据格式
		mWaterWaveView.setmWaterLevel(irate);
		mWaterWaveView.startWave();
	}

	private String FloatToString(float f) {
		String str;
		int GB = (int) f;
		int MB = (int) (((f - (int) f) * 10240 + 5) / 10);
		if (MB >= 1024) {
			MB -= 1024;
			GB++;
		}
		str = String.valueOf(GB) + "G " + String.valueOf(MB) + "M";
		return str;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mWaterWaveView.stopWave();
		mWaterWaveView = null;
		super.onDestroy();
	}

}
