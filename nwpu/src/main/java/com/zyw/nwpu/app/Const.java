package com.zyw.nwpu.app;

public class Const {

	public static final int LIMIT_GET_STATUS = 8;// 获取状态
	public static final int LIMIT_GET_COMMENTS = 15;// 获取评论
	public static final boolean IS_UPDATE_STRATEGY = false;// true:按更新时间排序,false:按发布时间排序
	public static final int HEADIMAGE_SIZE = 132;// 头像像素

	public static final String hosturl = "http://s-170401.gotocdn.com/install_package/index.php?";

	// 登陆接口
	public static final String LOGIN = "http://s-170401.gotocdn.com/login.php";

	// 修改信息接口
	public static final String UPDATE_NICKNAME = "http://s-170401.gotocdn.com/koma/update_user_nickname.php";

	// 注册接口
	public static final String registurl = hosturl + "m=member&c=index&a=register";

	// 找回密码接口
	public static final String getbackpwurl = "http://s-170401.gotocdn.com/mail_php.php";

	// 评论接口
	public static final String add_comment = hosturl + "m=comment&c=index&a=post";

	// 获取评论接口
	public static final String get_comments = "http://s-170401.gotocdn.com/koma/fetch_comments.php";

	// 评论点赞接口
	public static final String comment_supporturl = hosturl + "m=comment&c=index&a=support&format=jsonp&";

	// 点赞接口
	public static final String likeurl = "http://s-170401.gotocdn.com/insert_like.php";

	// 版本更新配置文件路径
	public final static String updateurl = "http://s-170401.gotocdn.com/update/update.xml";

	// 反馈意见接口
	public final static String feedbackurl = "http://s-170401.gotocdn.com/insert_feedback.php";

	// 获取新闻列表接口
	public final static String newslisturl = "http://s-170401.gotocdn.com/koma/koma_news.php";

	// 获取笑话列表接口
	public final static String jokelisturl = "http://s-170401.gotocdn.com/joke.php";

	// 软件使用帮助页面
	public final static String helpurl = "http://s-170401.gotocdn.com/app_introduction/intro.html";

	// 软件使用帮助页面
	public final static String cmt_likenumurl = "http://s-170401.gotocdn.com/back_comment_num.php";

	// 广告图片地址
	public final static String ad_imgurl = "http://s-170401.gotocdn.com/ad.png";

	// 图书馆超期提醒地址
	public final static String chaoqitixing_url = "http://s-170401.gotocdn.com/koma/koma_tscx.php";

	// 反馈 推送阅读数 +1
	public final static String notify_read_num_feedback = "http://s-170401.gotocdn.com/push/NS_main/NS_readnum.php";

	// 获取推送列表
	public final static String push_list = "http://s-170401.gotocdn.com/push/NS_main/NS_recordreview.php";

	// 场馆开放时间
	public final static String open_time = "http://wechat.npulife.com/SmartSchool/Zonghechaxun/openTime.html";

	// 西工大图书馆
	public final static String npu_lib = "http://m.5read.com/739";
	// public final static String npu_lib =
	// "http://202.117.88.170:8080/sms/opac/search/showSearch.action?xc=5";
	// public final static String npu_lib = "http://tushuguan.nwpu.edu.cn/";

	// 课程成绩
	public final static String cource = "http://wechat.npulife.com/SmartSchool/Graduatescorequery/login";

	// 奖助学金
	public final static String scholarship = "http://wechat.npulife.com/Tool/Home/Scholarship/index";

	// 校车时刻
	public final static String schoolbus = "http://s-170401.gotocdn.com/koma/koma_xiaoche.html";

	// 办事指南
	public final static String banshi = "http://wechat.npulife.com/SmartSchool/Zonghechaxun/banshiliucheng.html";

	// 校园网单点登录url
	public final static String sso_login = "http://s-170401.gotocdn.com/koma/cas/login.php";
	public final static String sso_login_success = "http://s-170401.gotocdn.com/koma/cas/loginSuccess.php";

}
