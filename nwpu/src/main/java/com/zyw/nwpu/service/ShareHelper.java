package com.zyw.nwpu.service;

import org.xutils.x;

import android.app.Activity;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.zyw.nwpulib.utils.CommonUtil.AppUtils;

/**
 * 分享
 * 
 * @author Rocket
 */
public class ShareHelper {

    public static final void showShare(Activity activity, final String title, String text, final String url, String imgPath) {
        ShareSDK.initSDK(activity);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // oks.setTitle(x.app().getString(R.string.ssdk_oks_share));

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath(imgPath);// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(AppUtils.getAppName(x.app()));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

            // 自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                // 点击微信好友
                if ("Wechat".equals(platform.getName())) {
                    // 微信分享应用 ,此功能需要微信绕开审核，需要使用项目中的wechatdemo.keystore进行签名打包
                    // 由于Onekeyshare没有关于应用分享的参数如setShareType等，我们需要通过自定义 分享来实现
                    // 比如下面设置了setTitle,可以覆盖oks.setTitle里面的title值
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    // paramsToShare.setImageUrl("http://noavatar.csdn.net/5/F/5/1_u010477563.jpg");
                }
                if ("WechatMoments".equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                }
//                微博分享链接是将链接写到setText内：eg：setText(“分享文本 http://mob.com”);
//                text,image;如果imagePath和imageUrl同时存在，imageUrl将被忽略。
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setText(title+url);
                }
            }
        });
        // 启动分享GUI
        oks.show(activity);
    }
}
