package cc.ibooker.ibookereditor.net.service;

import java.util.ArrayList;
import java.util.Map;

import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.bean.ArticleUserInfoData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.dto.UserDto;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
     * 按时间顺序获取文章列表
     */
    @GET("article/new/list")
    Observable<ResultData<ArrayList<ArticleUserData>>> getNewArticleUserDataList(@Query("values") String values);

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

    /**
     * 通过用户ID获取与文章相关信息
     */
    @GET("article/info")
    Observable<ResultData<ArticleUserInfoData>> getArticleUserInfoDataById(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 更新文章喜欢
     */
    @POST("articleappreciate/modify")
    Observable<ResultData<Boolean>> modifyArticleAppreciate(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 根据用户ID查询文章喜欢信息列表
     */
    @GET("articleappreciate/article/list")
    Observable<ResultData<ArrayList<ArticleAppreciateData>>> getArticleAppreciateDataListByPuid2(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 根据ID修改文章喜欢是否删除状态
     */
    @POST("articleappreciate/{aaId}/isdelete/update")
    Observable<ResultData<Boolean>> updateArticleAppreciateIsdeleteById(@Path("aaId") long aaId, @HeaderMap Map<String, String> headers);

    /**
     * 获取短信验证码
     */
    @POST("sms/code/send")
    Observable<ResultData<String>> getSmsCode(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 验证账号（该账号是否可以注册）
     */
    @POST("user/account/valid")
    Observable<ResultData<Boolean>> validAccountExist(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 验证短信验证码
     */
    @POST("sms/code/valid")
    Observable<ResultData<Boolean>> validSmsCode(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 通过手机号注册
     */
    @POST("user/phone/register")
    Observable<ResultData<UserDto>> registerByPhone(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 根据手机号修改密码
     */
    @POST("user/passwd/phone/update")
    Observable<ResultData<Boolean>> updatePasswdByUphone(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 验证昵称（该昵称是否可以使用）
     */
    @POST("user/nickname/valid")
    Observable<ResultData<Boolean>> validNicknameExist(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 根据用户ID修改用户信息
     */
    @POST("user/update")
    Observable<ResultData<Boolean>> updateUserByUid(@Query("values") String values, @HeaderMap Map<String, String> headers);

    /**
     * 根据ID修改用户图像
     */
    @FormUrlEncoded
    @POST("user/pic/upload")
    Observable<ResultData<String>> upLoadUserPicImage(@Field("values") String values, @Field("imgfile") String imgfile, @HeaderMap Map<String, String> headers);
//    @Multipart
//    @PUT("user/pic/upload")
//    Observable<ResultData<String>> upLoadUserPicImage(@Part("values") RequestBody values, @HeaderMap Map<String, String> headers);

}
