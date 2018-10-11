package cc.ibooker.ibookereditor.net.service;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.bean.ArticleUserInfoData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.dto.UserDto;
import cc.ibooker.ibookereditor.net.request.MyGsonConverterFactory;
import cc.ibooker.ibookereditor.net.request.MyOkHttpClient;
import cc.ibooker.ibookereditor.utils.AESUtil;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Retrofit 弊端对非JSON数据处理存在很多不便
 * Okhttp 弊端适合对键值对缓存（Get），不能对加密数据直接缓存
 * Created by 邹峰立 on 2016/9/17.
 */
public class HttpMethods {
    // 测试服
//    private static final String BASE_URL = "http://47.93.239.223:808/mobile/";
    // 正式服
//    private static final String BASE_URL = "http://192.168.1.113:8080/ibooker/mobile/";
    private static final String BASE_URL = "http://ibooker.cc/ibooker/mobile/";

    private MyService myService;

    // 构造方法私有
    private HttpMethods() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                Log.d("MyTAG", "OkHttp: " + message);
//            }
//        });
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        //手动创建一个OkHttpClient
//        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//超时时间15S;
//                .retryOnConnectionFailure(true)//连接失败后是否重新连接
//                .addInterceptor(logging)
//                .cache()//设置缓存 10M
//                .addInterceptor(new CaheInterceptor(getAppl, null))//离线
//                .addNetworkInterceptor(new CaheInterceptor(context, null));//在线

        Retrofit retrofit = new Retrofit.Builder()
//                .client(httpClientBuilder.build())
                .client(MyOkHttpClient.getClient())
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        myService = retrofit.create(MyService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // 定义头部信息
    private Map<String, String> getHeaderMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("ua", ConstantUtil.userDto != null ? ConstantUtil.userDto.getUa() : "");
        headers.put("token", ConstantUtil.userDto != null ? ConstantUtil.userDto.getToken() : "");
        return headers;
    }

    /**
     * 获取推荐文章列表
     */
    public void getRecommendArticleList(Subscriber<ResultData<ArrayList<ArticleUserData>>> subscriber, int page) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("page", page);
        myService.getRecommendArticleList(AESUtil.encrypt(map.toString()))
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }

    /**
     * 获取特定文章
     */
    public void getArticleUserDataById(Subscriber<ResultData<ArticleUserData>> subscriber, long aId) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("aId", aId);
        myService.getArticleUserDataById(AESUtil.encrypt(map.toString()))
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }

    /**
     * 插入反馈信息
     */
    public void insertSuggest(Subscriber<ResultData<Boolean>> subscriber, String stStyle, String stContent, String stEmail) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("stStyle", stStyle);
        map.put("stContent", stContent);
        map.put("stEmail", stEmail);
        myService.insertSuggest(AESUtil.encrypt(map.toString()))
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }

    /**
     * 下载文件
     */
    public void downloadFile(Subscriber<ResponseBody> subscriber, String url) {
        myService.downloadFile(url)
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);
    }

    /**
     * 用户登录
     */
    public void userLogin(Subscriber<ResultData<UserDto>> subscriber, String account, String uPasswd) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("account", account);
        map.put("uPasswd", uPasswd);
        myService.userLogin(AESUtil.encrypt(map.toString()))
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);
    }

    /**
     * 通过用户ID获取与文章相关信息
     */
    public void getArticleUserInfoDataById(Subscriber<ResultData<ArticleUserInfoData>> subscriber, long aId) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("aId", aId);
        myService.getArticleUserInfoDataById(AESUtil.encrypt(map.toString()), getHeaderMap())
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }

    /**
     * 更新文章喜欢
     */
    public void modifyArticleAppreciate(Subscriber<ResultData<Boolean>> subscriber, long aId) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("aaAid", aId);
        myService.modifyArticleAppreciate(AESUtil.encrypt(map.toString()), getHeaderMap())
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }

    /**
     * 根据用户ID查询文章喜欢信息列表
     */
    public void getArticleAppreciateDataListByPuid2(Subscriber<ResultData<ArrayList<ArticleAppreciateData>>> subscriber, int page) {
        Map<String, Object> map;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            map = new ArrayMap<>();
        else
            map = new HashMap<>();
        map.put("page", page);
        myService.getArticleAppreciateDataListByPuid2(AESUtil.encrypt(map.toString()), getHeaderMap())
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }

    /**
     * 根据ID修改文章喜欢是否删除状态
     */
    public void updateArticleAppreciateIsdeleteById(Subscriber<ResultData<Boolean>> subscriber, long aaId) {
        myService.updateArticleAppreciateIsdeleteById(aaId, getHeaderMap())
                //指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //指定subscriber的回调发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //实现订阅关系
                .subscribe(subscriber);

    }
}
