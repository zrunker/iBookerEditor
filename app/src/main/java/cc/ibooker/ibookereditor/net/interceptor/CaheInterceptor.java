package cc.ibooker.ibookereditor.net.interceptor;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import cc.ibooker.ibookereditor.utils.NetworkUtil;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 缓存拦截器
 * Created by 邹峰立 on 2016/12/8.
 */
public class CaheInterceptor implements Interceptor {

    private Context context;// 用来判断网络状态

    public CaheInterceptor(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetworkUtil.isNetworkConnected(context)) {// 有网络缓存
            Response response = chain.proceed(request);
            // read from cache for 60 s
            int maxAge = 60;
            //获取头部信息
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {// 无网络缓存
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)//只访问缓存
                    .build();
            Response response = chain.proceed(request);
            //set cahe times is 3 days
            int maxStale = 60 * 60 * 24 * 3;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    }
}