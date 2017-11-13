package com.zyw.nwpu.xmz;

import java.util.ArrayList;
import java.util.List;

import com.zyw.nwpulib.model.StatusData;

/**
 * 2016年4月1日
 * 
 * 项目制网络请求类
 * 
 * 项目实体类
 * 
 * @author Rocket
 * 
 */
public class XmzHelper {

	public interface OnComplete {
		public void onComplete(List<Project> data);
	}

	public static void getProjectList(OnComplete callback) {

		List<Project> mData = new ArrayList<Project>();

		Project p = new Project();
		p.setName("人文艺术等素质素养");
		p.setStartTime("2016-09-24 22:57:31");
		p.setEndTime("2016-09-28 22:57:44");
		p.setProject_location("篮球场");
		p.setDWMC("航天学院");

		mData.add(p);

		Project p2 = p;
		mData.add(p2);

		if (callback != null) {
			callback.onComplete(mData);
		}
	}
}