package cc.ibooker.ibookereditor.application;

import android.app.Application;

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
        // 应用程序捕获异常
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
    }

    // 获取全局上下文对象
    public static MyApplication getInstance() {
        return instance;
    }

}
