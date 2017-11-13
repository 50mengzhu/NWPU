package com.zyw.nwpu.tool;

import com.zyw.nwpulib.utils.CommonUtil;

public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.FileUtils.hasSDCard()) {
			return CommonUtil.FileUtils.getRootFilePath() + "com.me/files/";
		} else {
			return CommonUtil.FileUtils.getRootFilePath() + "com.me/files";
		}
	}

	public static String getImageFilePath() {
		if (CommonUtil.FileUtils.hasSDCard()) {
			return CommonUtil.FileUtils.getRootFilePath() + "com.me/images/";
		} else {
			return CommonUtil.FileUtils.getRootFilePath() + "com.me/images";
		}
	}

	public static String getHeadImageFilePath() {
		if (CommonUtil.FileUtils.hasSDCard()) {
			return CommonUtil.FileUtils.getRootFilePath()
					+ "com.me/headImages/";
		} else {
			return CommonUtil.FileUtils.getRootFilePath() + "com.me/headImages";
		}
	}
}
