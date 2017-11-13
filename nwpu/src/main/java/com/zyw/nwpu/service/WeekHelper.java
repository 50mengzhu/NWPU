package com.zyw.nwpu.service;

import com.zyw.nwpulib.utils.CommonUtil;

/**
 * 获取现在是第几周
 * 
 * 2016年04月12日
 * 
 * @author Rocket
 * 
 */
public class WeekHelper {
	public static String getWeek() {
		int y = CommonUtil.DateUtils.getCurrentYear();
		if (y == 2016) {
			int weeknum = CommonUtil.DateUtils.getCurrentWeekOfYear();
			if (CommonUtil.DateUtils.getCurrentDayOfWeek() == 1)
				weeknum--;
			weeknum -= 9;
			if (weeknum > 19)
				return "";
			else
				return "第" + String.valueOf(weeknum) + "周";
		} else {
			return "";
		}
	}
}
