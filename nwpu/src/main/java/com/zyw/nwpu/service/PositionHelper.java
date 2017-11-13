package com.zyw.nwpu.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUtils;
import com.zyw.nwpulib.model.CommentLikePushData;
import com.zyw.nwpulib.model.PositionInfo;

import android.text.TextUtils;

/**
 * 位置工具类
 * 
 * @author Rocket
 * 
 */
public class PositionHelper {

	public static final String getDetailedPositionName(String name, double lat, double lng) {
		String newName = getNearestPositionName(lat, lng);
		if (TextUtils.isEmpty(newName))
			return name;
		return newName;
	}

	public static final String getDetailedPositionName(String name, AVGeoPoint p) {
		if (p == null)
			return name;
		return getDetailedPositionName(name, p.getLatitude(), p.getLongitude());
	}

	private static boolean isGetData = false;

	private static void getData() {
		if (isGetData)
			return;
		isGetData = true;

		positions = new ArrayList<>();

		positions.add(new PositionInfo("星天苑A座", 108.77011, 34.040384));
		positions.add(new PositionInfo("星天苑B座", 108.768317, 34.040561));
		positions.add(new PositionInfo("星天苑C座", 108.770155, 34.041274));
		positions.add(new PositionInfo("星天苑D座", 108.768333, 34.041352));
		positions.add(new PositionInfo("星天苑E座", 108.770028, 34.042038));
		positions.add(new PositionInfo("星天苑F座", 108.76827, 34.042109));
		positions.add(new PositionInfo("星天苑G座", 108.769932, 34.042834));
		positions.add(new PositionInfo("星天苑H座", 108.767028, 34.043112));
		positions.add(new PositionInfo("云天苑A座", 108.774009, 34.041305));
		positions.add(new PositionInfo("云天苑B座", 108.773324, 34.041334));
		positions.add(new PositionInfo("云天苑C座", 108.774018, 34.042058));
		positions.add(new PositionInfo("云天苑D座", 108.773297, 34.041693));
		positions.add(new PositionInfo("云天苑E座", 108.773866, 34.042835));
		positions.add(new PositionInfo("云天苑F座", 108.773432, 34.042687));
		positions.add(new PositionInfo("教学东楼A座", 108.773253, 34.0385));
		positions.add(new PositionInfo("教学东楼B座", 108.773109, 34.039569));
		positions.add(new PositionInfo("教学东楼C座", 108.773477, 34.039301));
		positions.add(new PositionInfo("教学东楼D座", 108.77416, 34.039247));
		positions.add(new PositionInfo("教学西楼A座", 108.769821, 34.03672));
		positions.add(new PositionInfo("教学西楼B座", 108.769312, 34.037466));
		positions.add(new PositionInfo("教学西楼C座", 108.77135, 34.037549));
		positions.add(new PositionInfo("教学西楼D座", 108.770764, 34.038552));
		positions.add(new PositionInfo("力学与土木建筑学院", 108.774007, 34.038088));
		positions.add(new PositionInfo("动力与能源学院", 108.775804, 34.037272));
		positions.add(new PositionInfo("电子信息学院", 108.774518, 34.035598));
		positions.add(new PositionInfo("自动化学院", 108.775642, 34.034669));
		positions.add(new PositionInfo("计算机学院", 108.775833, 34.038751));
		positions.add(new PositionInfo("理学院", 108.775149, 34.033818));
		positions.add(new PositionInfo("管理学院", 108.774019, 34.037519));
		positions.add(new PositionInfo("人文经法学院", 108.775938, 34.038805));
		positions.add(new PositionInfo("外国语学院", 108.772757, 34.038757));
		positions.add(new PositionInfo("云天苑餐厅", 108.773386, 34.040361));
		positions.add(new PositionInfo("星天苑南餐厅", 108.769273, 34.040466));
		positions.add(new PositionInfo("星天苑北餐厅", 108.768938, 34.04285));
		positions.add(new PositionInfo("实验大楼", 108.769345, 34.036009));
		positions.add(new PositionInfo("长安校区图书馆", 108.772673, 34.036903));
		positions.add(new PositionInfo("长安校区游泳馆", 108.768234, 34.036829));
		positions.add(new PositionInfo("长安校区翱翔体育馆", 108.775941, 34.040168));
		positions.add(new PositionInfo("长安校区翱翔学生中心", 108.772261, 34.040158));
		positions.add(new PositionInfo("数字化大楼", 108.769541, 34.039487));
		positions.add(new PositionInfo("东元超市", 108.768314, 34.043166));
		positions.add(new PositionInfo("工程实践训练中心", 108.766746, 34.040593));
		positions.add(new PositionInfo("东一门", 108.77634, 34.039712));
		positions.add(new PositionInfo("东二门", 108.776508, 34.036505));
		positions.add(new PositionInfo("北门", 108.771512, 34.043352));
		positions.add(new PositionInfo("东风广场", 108.77157, 34.042335));
		positions.add(new PositionInfo("和尊", 108.768954, 34.040496));
		positions.add(new PositionInfo("长安校区校医院", 108.776987, 34.040394));
		positions.add(new PositionInfo("星天苑操场", 108.767779, 34.038057));
		positions.add(new PositionInfo("云天苑操场", 108.775725, 34.042383));
		positions.add(new PositionInfo("中国邮政", 108.768954, 34.040496));
		positions.add(new PositionInfo("中国工商银行", 108.777812, 34.039371));
		positions.add(new PositionInfo("东篱菊园", 108.776873, 34.036121));
		positions.add(new PositionInfo("旺园学生公寓", 108.915524, 34.249982));
		positions.add(new PositionInfo("友谊校区西门", 108.91697, 34.250397));
		positions.add(new PositionInfo("航空学院，流体力学系", 108.918322, 34.250818));
		positions.add(new PositionInfo("材料科技大楼B段", 108.917927, 34.249781));
		positions.add(new PositionInfo("TSCMCMC技术研究部", 108.91975, 34.249154));
		positions.add(new PositionInfo("材料学院—材料科技大楼", 108.918709, 34.250143));
		positions.add(new PositionInfo("西安航空馆", 108.91901, 34.249576));
		positions.add(new PositionInfo("友谊校区出版社", 108.919522, 34.250024));
		positions.add(new PositionInfo("陕西省材料研究分析中心", 108.919562, 34.249445));
		positions.add(new PositionInfo("公共管理硕士教育中心", 108.920627, 34.25024));
		positions.add(new PositionInfo("机电实验教学中心", 108.920927, 34.250024));
		positions.add(new PositionInfo("诚字楼", 108.920245, 34.249486));
		positions.add(new PositionInfo("航空楼", 108.922423, 34.250139));
		positions.add(new PositionInfo("管理学院", 108.921642, 34.249852));
		positions.add(new PositionInfo("动力楼", 108.921669, 34.249513));
		positions.add(new PositionInfo("公字楼", 108.917999, 34.247924));
		positions.add(new PositionInfo("干部教育培训中心", 108.918628, 34.247752));
		positions.add(new PositionInfo("西馆", 108.919387, 34.248058));
		positions.add(new PositionInfo("中比宇航工程计算技术实验室", 108.919001, 34.248405));
		positions.add(new PositionInfo("空间生物模拟技术重点实验室", 108.919266, 34.247476));
		positions.add(new PositionInfo("办公楼A座", 108.920227, 34.247487));
		positions.add(new PositionInfo("办公楼B座", 108.921206, 34.247454));
		positions.add(new PositionInfo("勇字楼", 108.92024, 34.247968));
		positions.add(new PositionInfo("东馆", 108.921206, 34.248028));
		positions.add(new PositionInfo("毅字楼", 108.922252, 34.248091));
		positions.add(new PositionInfo("校友会办公室", 108.92311, 34.247931));
		positions.add(new PositionInfo("友谊校区新图书馆", 108.922639, 34.248923));
		positions.add(new PositionInfo("国际会议中心", 108.922163, 34.248912));
		positions.add(new PositionInfo("友谊校区老图书馆", 108.921103, 34.24883));
		positions.add(new PositionInfo("校史馆", 108.921112, 34.249151));
		positions.add(new PositionInfo("爱生楼（学生餐厅）", 108.924444, 34.24899));
		positions.add(new PositionInfo("留学生公寓", 108.925114, 34.249292));
		positions.add(new PositionInfo("浴室", 108.925257, 34.249632));
		positions.add(new PositionInfo("正禾超市", 108.925172, 34.250102));
		positions.add(new PositionInfo("学生公寓一号楼A座", 108.925949, 34.249688));
		positions.add(new PositionInfo("学生公寓一号楼B座", 108.926519, 34.250027));
		positions.add(new PositionInfo("学生公寓5号楼", 108.925971, 34.250046));
		positions.add(new PositionInfo("学生公寓2号楼", 108.925105, 34.250423));
		positions.add(new PositionInfo("学生公寓6号楼", 108.924197, 34.250053));
		positions.add(new PositionInfo("学生公寓11号楼", 108.923654, 34.247864));
		positions.add(new PositionInfo("学生公寓三号楼C座", 108.924076, 34.25037));
		positions.add(new PositionInfo("学生公寓三号楼A座", 108.923654, 34.249841));
		positions.add(new PositionInfo("友谊校区南门", 108.923421, 34.247096));
		positions.add(new PositionInfo("友谊校区东门", 108.926535, 34.25049));
		positions.add(new PositionInfo("翱翔训练馆", 108.924287, 34.250673));
		positions.add(new PositionInfo("友谊校区篮球场", 108.925019, 34.250926));
		positions.add(new PositionInfo("友谊校区体育场", 108.92537, 34.252243));
		positions.add(new PositionInfo("友谊校区游泳池", 108.924494, 34.252799));
		positions.add(new PositionInfo("友谊校区校医院", 108.918839, 34.252244));
		positions.add(new PositionInfo("航天学院", 108.922001, 34.245831));
		positions.add(new PositionInfo("航海学院", 108.921615, 34.246286));
		positions.add(new PositionInfo("友谊校区校车始发点", 108.925787, 34.251075));
	}

	private static final String getNearestPositionName(double lat, double lng) {
		getData();

		AVGeoPoint p = new AVGeoPoint(lat, lng);
		double minDistance = 100000000;
		String name = "";

		for (int i = 0; i < positions.size(); i++) {
			double d = p.distanceInKilometersTo(new AVGeoPoint(positions.get(i).lat, positions.get(i).lng));
			if (d < minDistance) {
				minDistance = d;
				name = positions.get(i).positionName;
				// name = positions.get(i).positionName + " " +
				// String.valueOf(minDistance);
			}
		}

		if (minDistance < 0.1)
			return name;
		return "";
	}

	private static List<PositionInfo> positions;
}
