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

    /**
     * 保存定位信息
     */
    public static String sCurrentCountry = "";// 当前国家
    public static String sCurrentProv = "";// 当前省份
    public static String sCurrentCity = "";// 当前城市
    public static String sCurrentCityCode = "";// 当前城市编码
    public static String sCurrentDistrict = "";// 当前城区
    public static String sCurrentStreet = "";// 当前街道
    public static String sCurrentStreetNum = "";// 当前街道门牌号
    public static String sCurrentAdCode = "";// 地区编码
    public static String sCurrentAoiName = "";// 当前定位点的AOI信息
    public static String sCurrentAddress = "";// 当前地址
    public static double sPointx = 0;// 经度
    public static double sPointy = 0;// 纬度
}
