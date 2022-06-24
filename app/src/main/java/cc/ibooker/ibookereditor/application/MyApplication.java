package cc.ibooker.ibookereditor.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import cc.ibooker.ibookereditor.utils.ConstantUtil;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.TEXTVIEWSIZE;

/**
 * Application设置全局变量
 * 1.保持全局上下文，方便初始化OkhttpClient对象
 * 2.初始化手机基本信息，方便捕获异常
 * 3.友盟初始化-推送
 * Created by 邹峰立 on 2016/12/9.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        // 全局上下文赋值
        instance = this;

        // 友盟 - 统计/推送 - 只能放在Application - onCreate
        UMConfigure.init(this, "5b867262b27b0a4c9a00010e", null, UMConfigure.DEVICE_TYPE_PHONE, "697fce64d35084390b590996f0e35c07");
        // 注册推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        // 注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                // 注册成功会返回device token
                Log.d("deviceToken", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d("deviceToken", s + "---" + s1);
            }
        });
        // 通知栏最多显示3条
        mPushAgent.setDisplayNotificationNumber(3);
        // 控制通知是否展示
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                // 调用super则会走通知展示流程，不调用super则不展示通知
                SharedPreferences sharedPreferences = instance.getSharedPreferences(ConstantUtil.SHAREDPREFERENCES_SET_NAME, Context.MODE_PRIVATE);
                boolean bool = sharedPreferences.getBoolean(ConstantUtil.SHAREDPREFERENCES_ARTICLE_RECOMMEND, true);
                if (bool) {
                    super.dealWithNotificationMessage(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        // 启动服务执行耗时操作
        InitializeService.start(this);
    }

    // 获取全局上下文对象
    public static MyApplication getInstance() {
        return instance;
    }

    // 设置字体大小
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
//        Configuration config = new Configuration();
//        config.setToDefaults();
//        res.updateConfiguration(config, res.getDisplayMetrics());

        Configuration config = res.getConfiguration();
        config.fontScale = TEXTVIEWSIZE;// 1 设置正常字体大小的倍数
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
