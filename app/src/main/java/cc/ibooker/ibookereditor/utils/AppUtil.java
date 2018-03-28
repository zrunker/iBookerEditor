package cc.ibooker.ibookereditor.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 * APP管理类
 * Created by 邹峰立 on 2017/2/21.
 */
public class AppUtil {

    /**
     * 获取应用版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 唯一的设备ID：（IMEI，MEID，ESN，IMSI）
     * GSM手机的 IMEI 和 CDMA手机的 MEID.
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getImei(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm != null ? tm.getDeviceId() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }


    /**
     * 获取设备ID-Android_id
     *
     * @param activity 界面
     * @return Android_id
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Activity activity) {
        return Settings.Secure.getString(activity.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
}
