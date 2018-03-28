package cc.ibooker.ibookereditor.net.service;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.dto.ResultData;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
}
