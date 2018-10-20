package cc.ibooker.ibookereditor.utils;

import cc.ibooker.ibookereditor.dto.UserDto;

/**
 * 常量管理类
 * <p>
 * Created by 邹峰立 on 2018/3/5.
 */
public class ConstantUtil {
    /**
     * 操作文件相关权限状态码
     */
    public final static int PERMISSIONS_REQUEST_OPER_FILE = 212;

    /**
     * 字体缩放比例
     */
    public final static int TEXTVIEWSIZE = 1;

    /**
     * 本地文章每页显示条目
     */
    public final static int PAGE_SIZE_LOCAL_ARTICLE = 15;

    /**
     * 最新文章每页显示条目
     */
    public final static int PAGE_SIZE_NEW_ARTICLE = 8;

    /**
     * 用户信息
     */
    public static UserDto userDto;

    /**
     * SharedPreferences键名称
     */
    public final static String SHAREDPREFERENCES_SET_NAME = "SHAREDPREFERENCES_SET_NAME";
    public final static String SHAREDPREFERENCES_ARTICLE_SAVE = "SHAREDPREFERENCES_ARTICLE_SAVE";
    public final static String SHAREDPREFERENCES_ARTICLE_RECOMMEND = "SHAREDPREFERENCES_ARTICLE_RECOMMEND";
    public final static String SHAREDPREFERENCES_MAIN_SET = "SHAREDPREFERENCES_MAIN_SET";

}
