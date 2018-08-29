package cc.ibooker.ibookereditor.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.tencent.bugly.crashreport.CrashReport;

import cc.ibooker.ibookereditor.BuildConfig;
import cc.ibooker.ibookereditor.utils.AppUtil;

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

        // 应用程序捕获异常
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

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
