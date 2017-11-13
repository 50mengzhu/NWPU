package com.zyw.nwpu.appcenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.R;
import com.zyw.nwpu.R.id;
import com.zyw.nwpu.R.layout;
import com.zyw.nwpu.base.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

@ContentView(R.layout.activity_allowanceshow)
public class Allowanceshow extends BaseActivity {

	private String allowancedata;
	private ListView lv;
	private List<Map<String, Object>> data;
	private String remark;
	private String time;
	private String salary;
	private TextView tvname, tvschoolnum;
	private String name;
	private String schoilnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tvname = (TextView) findViewById(R.id.name);
		tvschoolnum = (TextView) findViewById(R.id.schoolnum);
		// 获得从上一个Activity传来的intent对象
		Intent intent = getIntent();
		allowancedata = intent.getStringExtra("json");

		try {
			// {"time":"android","time":"iphone"}
			// JSONObject demoJson = new JSONObject(jsonString);
			// String time = demoJson.getString("time");
			// String version = demoJson.getString("version");
			// JSONArray jsonArray = new JSONArray(MAINBODYHTML);
			// {
			// "flag":"1",
			// "msg":"0",
			// "std_num":"2015262061",
			// "status":"已发放",
			// "name":"明赟",
			// "salary":"700",
			// "remark":"硕士助学金",
			// "time":"201609"
			// }
			JSONObject jsonObject = new JSONObject(allowancedata);

			name = (String) jsonObject.get("name");
			schoilnum = (String) jsonObject.get("std_num");
			remark = (String) jsonObject.get("remark");
			time = (String) jsonObject.get("time");
			salary = (String) jsonObject.get("salary");

			tvname.setText(name);
			tvschoolnum.setText(schoilnum);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lv = (ListView) findViewById(R.id.lv);
		// 获取将要绑定的数据设置到data中
		data = getData();
		MyAdapter adapter = new MyAdapter(this);
		lv.setAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for (int i = 0; i < 1; i++) {
			map = new HashMap<String, Object>();
			map.put("remark", remark);
			map.put("time", time);
			map.put("salary", "¥" + salary);
			list.add(map);
		}
		return list;
	}

	// ViewHolder静态类
	static class ViewHolder {

		public TextView tvremark;
		public TextView tvtime;
		public TextView tvsalary;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater = null;

		private MyAdapter(Context context) {
			// 根据context上下文加载布局，这里的是Demo17Activity本身，即this
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// How many items are in the data set represented by this Adapter.
			// 在此适配器中所代表的数据集中的条目数
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// Get the data item associated with the specified position in the
			// data set.
			// 获取数据集中与指定索引对应的数据项
			return position;
		}

		@Override
		public long getItemId(int position) {
			// Get the row id associated with the specified position in the
			// list.
			// 获取在列表中与指定索引对应的行id
			return position;
		}

		// Get a View that displays the data at the specified position in the
		// data set.
		// 获取一个在数据集中指定索引的视图来显示数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			// 如果缓存convertView为空，则需要创建View
			if (convertView == null) {
				holder = new ViewHolder();
				// 根据自定义的Item布局加载布局
				convertView = mInflater.inflate(R.layout.list, null);
				holder.tvremark = (TextView) convertView.findViewById(R.id.remark);
				holder.tvtime = (TextView) convertView.findViewById(R.id.time);
				holder.tvsalary = (TextView) convertView.findViewById(R.id.salary);
				// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.remark.setBackgroundResource((Integer)
			// data.get(position).get("remark"));
			holder.tvremark.setText((String) data.get(position).get("remark"));
			holder.tvtime.setText((String) data.get(position).get("time"));
			holder.tvsalary.setText((String) data.get(position).get("salary"));

			return convertView;
		}

	}
}
