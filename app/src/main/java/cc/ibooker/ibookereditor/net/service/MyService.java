package cc.ibooker.ibookereditor.net.service;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.dto.UserDto;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 请求数据接口
 * Created by 邹峰立 on 2016/9/17.
 */
interface MyService {

    /**
     * 获取推荐文章列表
     */
    @GET("article/recomend/list")
    Observable<ResultData<ArrayList<ArticleUserData>>> getRecommendArticleList(@Query("values") String values);

    /**
     * 根据文章ID获取文章详情
     */
    @GET("article/detail")
    Observable<ResultData<ArticleUserData>> getArticleUserDataById(@Query("values") String values);

    /**
     * 插入反馈内容
     */
    @POST("suggest/insert")
    Observable<ResultData<Boolean>> insertSuggest(@Query("values") String values);

    /**
     * 下载文件
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    /**
     * 用户登录
     */
    @POST("user/login")
    Observable<ResultData<UserDto>> userLogin(@Query("values") String values);
}
