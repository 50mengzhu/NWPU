package com.zyw.nwpu.app;

import java.io.File;

import org.xutils.x;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zyw.nwpulib.model.Comment;
import com.zyw.nwpulib.model.Status;
import com.zyw.nwpu.db.SQLHelper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppApplication extends Application {
	private static AppApplication mAppApplication;
	private SQLHelper sqlHelper;
	public static String homeTown;// 选择家乡时传递参数
	public static String province = "未知", city = "未知", addr = "未知";
	public static double lat = 0, lng = 0;

	@Override
	public void onCreate() {
		// MultiDex.install(this);
		super.onCreate();
		mAppApplication = this;

		// 开启摇一摇监听服务
		startService(new Intent("com.zyw.nwpu"));

		// xutils初始化
		x.Ext.init(this);
		x.Ext.setDebug(false); // 是否输出debug日志

		iniLeanCloud();
		initImageLoader(getApplicationContext());
		AppHelper.getInstance().init(this);

		SpeechUtility.createUtility(this, "appid=" + "54222dde");
	}

	/**
	 * 初始化LeanCloud
	 */
	private void iniLeanCloud() {
		AVOSCloud.setDebugLogEnabled(false);

		// 注册自定义子类
		AVObject.registerSubclass(Status.class);
		AVObject.registerSubclass(Comment.class);

		AVOSCloud.initialize(this, "p4hsuepxidt9d12a51ospuvflrwmgqgy5bxmm6tkh17pz6ak",
				"jk3vx6qph2b0i3pxdnglxrjbz5uwm4iu6rex2i03mpzps8d4");

		AVAnalytics.start(this); // 开始统计 已经不再需要这行代码了
		AVAnalytics.enableCrashReport(this, true);// 报告崩溃

		AVInstallation.getCurrentInstallation().saveInBackground();

	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		// MultiDex.install(this);
	}

	/** 获取Application */
	public static AppApplication getApp() {
		return mAppApplication;
	}

	/** 获取数据库Helper */
	public SQLHelper getSQLHelper() {
		if (sqlHelper == null)
			sqlHelper = new SQLHelper(mAppApplication);
		return sqlHelper;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (sqlHelper != null)
			sqlHelper.close();
		super.onTerminate();
		// 整体摧毁的时候调用这个方法
	}

	/** 初始化ImageLoader */
	private void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "topnews/Cache");// 获取到缓存的目录地址
		Log.d("cacheDir", cacheDir.getPath());
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				// .memoryCacheExtraOptions(480, 800) // max width, max
				// height，即保存的每个缓存文件的最大长宽
				// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
				// null) // Can slow ImageLoader, use it carefully (Better don't
				// use it)设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 *
				// 1024)) // You can pass your own memory cache
				// implementation你可以通过自己的内存缓存实现
				// .memoryCacheSize(2 * 1024 * 1024)
				// /.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5
																		// 加密
				// .discCacheFileNameGenerator(new
				// HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .discCacheFileCount(100) //缓存的File数量
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// .imageDownloader(new BaseImageDownloader(context, 5 * 1000,
				// 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}
}
