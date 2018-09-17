package cc.ibooker.ibookereditor.net.request;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cc.ibooker.ibookereditor.application.MyApplication;
import cc.ibooker.ibookereditor.net.interceptor.CacheInterceptor;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Okhttp 弊端适合对键值对缓存(Get)，不能对加密数据直接缓存
 * Created by 邹峰立 on 2016/12/11.
 */
public class MyOkHttpClient {

    private static OkHttpClient mClient;
    private static final int DEFAULT_TIMEOUT = 15;

    // 构造方法私有
    private MyOkHttpClient() {
        // 有缓存
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Log.d("HttpRequestState", "OkHttp: " + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        File fileCache = new File(Environment.getExternalStorageDirectory() + File.separator + "IbookerEditor", "cacheData");
        Cache cache = new Cache(fileCache.getAbsoluteFile(), 1024 * 1024 * 30);//设置缓存30M
        CacheInterceptor caheInterceptor = new CacheInterceptor(MyApplication.getInstance().getApplicationContext());// 缓存拦截器

        mClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//超时时间15S
                .retryOnConnectionFailure(true)// 连接失败后是否重新连接
                .cache(cache)//设置缓存
                .addInterceptor(caheInterceptor)//离线缓存
                .addNetworkInterceptor(caheInterceptor)//在线缓存
                .addInterceptor(new Interceptor() {//添加请求头
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("CLIENTOS", "android")
                                .addHeader("PLATFORM", "ibookereditor")
                                .addHeader("APPVERSION", "1.0")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        // 无缓存
//        mClient = new OkHttpClient.Builder()
//                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//超时时间15S
//                .retryOnConnectionFailure(true)//连接失败后是否重新连接
//                .build();
    }

    public static OkHttpClient getClient() {
        synchronized (MyOkHttpClient.class) {
            if (mClient == null) {
                new MyOkHttpClient();
            }
        }
        return mClient;
    }

}
