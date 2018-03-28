package cc.ibooker.ibookereditor.utils;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加密，密钥分为128位，192位，256位三种 此处AES-128-CBC加密模式
 *
 * @author 邹峰立
 */
public class AESUtil {
    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static final String KEY = "ibooker179Runker";
    private static final String VIPARA = "0102030405060708";
    private static final String bm = "UTF8";

    // KeyGenerator提供对称密钥生成器的功能，支持各种算法
    // SecretKey负责保存对称密钥
    // Cipher负责完成加密或解密工作

    public static void main(String[] args) {
        String str = "测试数据66566533大润发grad个##22322@@@";

        String encryptStr = encrypt(str);
        System.out.println(encryptStr);

        String decryptStr = decrypt(encryptStr);
        System.out.println(decryptStr);
    }

    /**
     * 加密
     *
     * @param content 加密内容
     */
    public static String encrypt(String content) {
        if (!TextUtils.isEmpty(content)) {
            try {
                IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
                SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, zeroIv);
                byte[] encryptedData = cipher.doFinal(content.getBytes(bm));
//				return new BASE64Encoder().encode(encryptedData);// 此处使用BASE64做转码。
                return Base64Util.encodeBase64String(encryptedData);
            } catch (Exception e) {
                e.printStackTrace();
                // 打印日志信息
            }
        }
        return null;
    }

    /**
     * 加密2
     *
     * @param content 加密内容
     * @param key     密钥
     * @return 加密结果
     */
    public static String encryptByKey(String content, String key) {
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(key)) {
            try {
                IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, zeroIv);
                byte[] encryptedData = cipher.doFinal(content.getBytes(bm));
//				return new BASE64Encoder().encode(encryptedData);// 此处使用BASE64做转码。
                return Base64Util.encodeBase64String(encryptedData);
            } catch (Exception e) {
                e.printStackTrace();
                // 打印日志信息
            }
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content 解密内容
     */
    public static String decrypt(String content) {
        try {
//			byte[] byteMi = new BASE64Decoder().decodeBuffer(content);// 先用base64解密
            byte[] byteMi = Base64Util.decodeBase64String(content);
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, bm);
        } catch (Exception e) {
            e.printStackTrace();
            // 打印日志信息
        }
        return null;
    }

    /**
     * 解密2
     *
     * @param content 解密内容
     * @param key     密钥
     */
    public static String decryptByKey(String content, String key) {
        try {
//			byte[] byteMi = new BASE64Decoder().decodeBuffer(content);// 先用base64解密
            byte[] byteMi = Base64Util.decodeBase64String(content);
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, bm);
        } catch (Exception e) {
            e.printStackTrace();
            // 打印日志信息
        }
        return null;
    }
}