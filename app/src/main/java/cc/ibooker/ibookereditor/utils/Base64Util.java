package cc.ibooker.ibookereditor.utils;

import android.util.Base64;

/**
 * Base64的管理类，Android不支持commons-codec.jar高版本，所以这里采用Android自身android.util.Base64
 *
 * @author 邹峰立
 */
public class Base64Util {

//    public static void main(String[] args) {
//        String src = "测试数据abc...###";
//
//        String str2 = encodeBase64String(src.getBytes());
//        System.out.println(str2);
//
//        String str4 = new String(decodeBase64String(str2));
//        System.out.println(str4);
//
//    }

    // 加密二，采用android自带
    public static String encodeBase64String(byte[] bytes) {
        String encodeStr = null;
        if (bytes != null) {
            encodeStr = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return encodeStr;
    }

    // 解密二，采用android自带
    public static byte[] decodeBase64String(String str) {
        byte[] decodeBytes = null;
        if (str != null) {
            decodeBytes = Base64.decode(str, Base64.DEFAULT);
        }
        return decodeBytes;
    }

}
