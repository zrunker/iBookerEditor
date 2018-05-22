package cc.ibooker.ibookereditor.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串管理类
 *
 * @author 邹峰立
 */
public class StringUtil {

    // 判断字符串为空
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim()) || "null".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 去掉指定字符串的开头和结尾的指定字符
     *
     * @param stream  要处理的字符串
     * @param trimstr 要去掉的字符串
     * @return 处理后的字符串
     */
    public static String sideTrim(String stream, String trimstr) {
        // null或者空字符串的时候不处理
        if (StringUtil.isEmpty(stream) || StringUtil.isEmpty(trimstr)) {
            return stream;
        }
        // 结束位置
        int epos = 0;
        // 正规表达式
        String regpattern = "[" + trimstr + "]*+";
        Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);
        // 去掉结尾的指定字符
        StringBuffer buffer = new StringBuffer(stream).reverse();
        Matcher matcher = pattern.matcher(buffer);
        if (matcher.lookingAt()) {
            epos = matcher.end();
            stream = new StringBuffer(buffer.substring(epos)).reverse().toString();
        }
        // 去掉开头的指定字符
        matcher = pattern.matcher(stream);
        if (matcher.lookingAt()) {
            epos = matcher.end();
            stream = stream.substring(epos);
        }
        // 返回处理后的字符串
        return stream;
    }

    /**
     * 格式化邮箱，如182333333@qq.com格式化后1****3@qq.com
     *
     * @param email 要格式的邮箱
     */
    public static String formatEmail(String email) {
        if (StringUtil.isEmpty(email)
                || !RegularExpressionUtil.isEmail(email)) {
            return email;
        }
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    /**
     * 格式化手机号，如18060600906格式化后180***0906
     *
     * @param phone 要格式的手机号
     */
    public static String formatPhone(String phone) {
        if (StringUtil.isEmpty(phone)
                || !RegularExpressionUtil.isPhone(phone)) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 格式化身份证号，如411522200310232816格式化后4115*****2816
     *
     * @param idCard 要格式的身份证号
     */
    public static String formatIdCard(String idCard) {
        if (StringUtil.isEmpty(idCard)
                || !StringUtil.isEmpty(ValidIDCard.iDCardValidate(idCard))) {
            return idCard;
        }
        return idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }

    /**
     * 获取文件后缀
     *
     * @param file 文件名/文件路径
     * @return
     */
    public static String getFileSuffix(String file) {
        String suffix = "";
        if (!StringUtil.isEmpty(file) && file.contains(".")) {
            suffix = file.substring(file.lastIndexOf('.') + 1);
        }
        return suffix;
    }

    /**
     * 去掉文件后缀/获取路径，不包含后缀
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getFilePathNoSuffix(String filePath) {
        if (!StringUtil.isEmpty(filePath) && filePath.contains(".")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("."));
        }
        return filePath;
    }

    /**
     * 随机获取6位随机数
     *
     * @return
     */
    public static String getRandomSix() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            buffer.append(rand.nextInt(array.length));
        }
        return buffer.toString();
    }

}
