package com.zyw.nwpu.service;

import org.xutils.image.ImageOptions;

import com.zyw.nwpu.R;

public class DownLoadImageOption {

	public static final ImageOptions getHeadImageOptions() {
		ImageOptions options = new ImageOptions.Builder()
		// 是否忽略GIF格式的图片
				.setIgnoreGif(false)
				// 下载中显示的图片
				.setLoadingDrawableId(R.drawable.default_round_head)
				// 下载失败显示的图片
				.setFailureDrawableId(R.drawable.default_round_head).build();
		return options;
	}

	public static final ImageOptions getOtherImageOptions() {
		ImageOptions options = new ImageOptions.Builder()
		// 是否忽略GIF格式的图片
				.setIgnoreGif(false)
				// 下载中显示的图片
				.setLoadingDrawableId(R.drawable.ic_chat_def_pic)
				// 下载失败显示的图片
				.setFailureDrawableId(R.drawable.ic_chat_def_pic).build();
		return options;
	}
}
