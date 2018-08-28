package cc.ibooker.ibookereditor.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间管理类
 *
 * @author 邹峰立
 */
public class DateUtil {
    /**
     * yyyy-MM-dd
     */
    public static final String Format_Date = "yyyy-MM-dd";

    /**
     * HH:mm:ss
     */
    public static final String Format_Time = "HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String Format_DateTime = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前日期，格式yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return new SimpleDateFormat(Format_Date, Locale.CHINA).format(new Date());
    }

    /**
     * 获取当前时间HH:mm:ss
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat(Format_Time, Locale.CHINA).format(new Date());
    }

    /**
     * 获取当天日期时间
     */
    public static String getCurrentDateTime() {
        return DateUtil.getCurrentDateTime(Format_DateTime);
    }

    /**
     * 格式化当天日期时间
     *
     * @format 格式化
     */
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat t = new SimpleDateFormat(format, Locale.CHINA);
        return t.format(new Date());
    }

    /**
     * 取当前年份
     */
    public static int getCurrentYear() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取当前时间戳
     */
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取day天前的时间戳
     *
     * @param day day天
     * @return
     */
    public static long getTimeStampBefore(int day) {
        Calendar now = Calendar.getInstance();
        int currentDay = now.get(Calendar.DATE);
        now.set(Calendar.DATE, currentDay - day);
        return now.getTimeInMillis();
    }

    /**
     * 获取day天前的日期
     *
     * @param day day天
     * @return yyyy-MM-dd
     */
    public static String getCurrentDateBefore(int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(now.getTime());
    }

    /**
     * 获取2天前的日期时间
     */
    public static String getCurrentDateBefore2() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 2);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(now.getTime());
    }

    /**
     * 获取day天后的日期yyyy-MM-dd
     *
     * @param day 天数
     */
    public static String getCurrentDateAfter(int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(now.getTime());
    }

    /**
     * 获取指定日期day天后的日期
     *
     * @param str 指定日期
     * @param day 天数
     * @return
     */
    public static String getDateAfter(String str, int day) {
        Date date = DateUtil.parseDateFormat(str, "yyyy-MM-dd");
        if (date == null) {
            return null;
        }
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(now.getTime());
    }

    /**
     * 取中文日期时间
     */
    public String getCurrentDateTimeCN() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int mouth = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);

        int hour = ca.get(Calendar.HOUR);
        int mimute = ca.get(Calendar.MINUTE);
        int second = ca.get(Calendar.SECOND);

        return year + "年" + (mouth + 1) + "月" + day + "日" + hour + "时" + mimute + "分" + second + "秒";
    }

    /**
     * 格式化当前日期
     */
    public static String getCurrentDate(String format) {
        SimpleDateFormat t = new SimpleDateFormat(format, Locale.CHINA);
        return t.format(new Date());
    }

    /**
     * 格式化当前时间
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat t = new SimpleDateFormat(format, Locale.CHINA);
        return t.format(new Date());
    }

    /**
     * 格式化字符串为日期格式yyyy-MM-dd HH:mm:ss
     */
    public static Date parseDateTime(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        if (str.length() <= 10)
            return parse(str);
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自定义字符串转换日期格式
     */
    public static Date parseDateFormat(String str, String format) {
        if (StringUtil.isEmpty(str))
            return null;
        try {
            SimpleDateFormat t = new SimpleDateFormat(format, Locale.CHINA);
            return t.parse(str.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化时间字符串，HH:mm:ss格式为HH:mm
     *
     * @param str 格式化字符串：HH:mm:ss
     */
    public static String formatTimeStrBySplit(String str) {
        if (StringUtil.isEmpty(str) || !RegularExpressionUtil.isTime(str)) {
            return null;
        } else {
            String[] times = str.split(":");
            if (times.length >= 2)
                return times[0] + ":" + times[1];
            else
                return null;
        }
    }

    /**
     * 格式化日期字符串，yyyy-MM-dd格式为MM-dd
     *
     * @param str 格式化字符串：yyyy-MM-dd
     */
    public static String formatDateStrBySplit(String str) {
        if (StringUtil.isEmpty(str) || !RegularExpressionUtil.isDate(str)) {
            return null;
        } else {
            String[] dates = str.split("-");
            if (dates.length >= 3)
                return dates[1] + "-" + dates[2];
            else
                return null;
        }
    }

    /**
     * 时间戳转换成字符串T（yyyy-MM-dd）
     *
     * @param time 时间戳
     */
    public static String getTimeStampToString(long time) {
        if (time <= 0) {
            return "";
        }
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sf.format(d);
    }

    /**
     * 时间戳转换成字符串T（yyyy-MM-dd HH:mm:ss）
     *
     * @param time 时间戳
     */
    public static String getTimeStampToString2(long time) {
        if (time <= 0) {
            return "";
        }
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sf.format(d);
    }

    /**
     * 将字符串转化成日期格式yyyy-MM-dd
     */
    public static Date parse(String str) {
        if (StringUtil.isEmpty(str))
            return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串日期格式转换成日期格式
     */
    public static String getStringToTime(String datetime) {
        String mDateTime = "";
        Date date = null;
        if (!StringUtil.isEmpty(datetime)) {
            if (datetime.length() == 8) {
                // 参数为 例如：19740306 时，返回下面代码执行效果
                mDateTime = (datetime.substring(0, 4) + "-" + datetime.substring(5, 6) + "-" + datetime.substring(7, 8));
            } else {
                if (!StringUtil.isEmpty(datetime)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
                    try {
                        date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.SIMPLIFIED_CHINESE).parse(datetime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mDateTime = formatter.format(date);
                }
            }
        }
        return mDateTime;
    }

    /**
     * 传入数据的格式为19740306020100 返回的数据格式为1974年03月06日02时01分00秒
     */
    public static String getFormatDate(String time) {
        try {
            if (!StringUtil.isEmpty(time)) {
                if (time.length() == 14) {
                    // 参数为 例如：19740306 时，返回下面代码执行效果
                    return time.subSequence(0, 4) + "年"
                            + time.subSequence(4, 6) + "月"
                            + time.subSequence(6, 8) + "日"
                            + time.subSequence(8, 10) + "时"
                            + time.subSequence(10, 12) + "分"
                            + time.subSequence(12, 14) + "秒";
                } else if (time.length() > 9) {// 1988-11-11 00:00:00
                    String temp = (String) time.subSequence(0, 10);
                    return getFormatTime(temp.replaceAll("-", ""));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 传入数据的格式为19740306020100 返回的数据格式为1974-03-06 02:01:00
     */
    public static String getFormatDate2(String time) {
        try {
            if (!StringUtil.isEmpty(time)) {
                if (time.length() == 14) {
                    // 参数为 例如：19740306 时，返回下面代码执行效果
                    return time.subSequence(0, 4) + "-"
                            + time.subSequence(4, 6) + "-"
                            + time.subSequence(6, 8) + " "
                            + time.subSequence(8, 10) + ":"
                            + time.subSequence(10, 12) + ":"
                            + time.subSequence(12, 14);
                } else if (time.length() > 9) {// 1988-11-11 00:00:00
                    String temp = (String) time.subSequence(0, 10);
                    return getFormatTime(temp.replaceAll("-", ""));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 传入数据的格式为19740306 返回的数据格式为1974年03月06日
     */
    public static String getFormatTime(String time) {
        try {
            if (!StringUtil.isEmpty(time)) {
                if (time.length() == 8) {
                    // 参数为 例如：19740306 时，返回下面代码执行效果
                    return time.subSequence(0, 4) + "年"
                            + time.subSequence(4, 6) + "月"
                            + time.subSequence(6, 8) + "日";
                } else if (time.length() > 9) {// 1988-11-11 00:00:00
                    String temp = (String) time.subSequence(0, 10);
                    return getFormatTime(temp.replaceAll("-", ""));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 传入数据的格式为19740306 返回的数据格式为03月06日
     */
    public static String getFormatTime2(String time) {
        try {
            if (!StringUtil.isEmpty(time)) {
                if (time.length() == 8) {
                    // 参数为 例如：19740306 时，返回下面代码执行效果
                    return time.subSequence(4, 6) + "月" + time.subSequence(6, 8) + "日";
                } else if (time.length() > 9) {// 1988-11-11 00:00:00
                    String temp = (String) time.subSequence(0, 10);
                    return getFormatTime2(temp.replaceAll("-", ""));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 两个时间之间相差距离多少天
     *
     * @param str1 时间参数 1：
     * @param str2 时间参数 2：
     * @return 相差天数
     */
    public static long getDistanceDays(String str1, String str2) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date one;
        Date two;
        long days = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceDateTime2(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }

    /**
     * 两个时间戳之间相差距离多少天
     *
     * @param startTimestamp1 时间参数 1：开始时间戳
     * @param endTimestamp2   时间参数 2：结束时间戳
     * @return 相差天数
     */
    public static long getTimeStampDistanceDays(long startTimestamp1, long endTimestamp2) {
        return (endTimestamp2 - startTimestamp1) / 1000 / 60 / 60 / 24;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceDateTime1(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new long[]{day, hour, min, sec};
    }

    /**
     * 格式化日期时间，
     * 如果是当年显示MM-dd，
     * 如果是当月显示几天前，
     * 如果是当天显示几小时前，
     * 如果是当时显示几分钟前，
     * 如果是当分显示刚刚
     * 否则显示yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static String getFormatTimeStampToDateTime(long time) {
        // 当前日期时间
        Calendar ca = Calendar.getInstance(TimeZone.getDefault());
        int yearNow = ca.get(Calendar.YEAR);
        int mouthNow = ca.get(Calendar.MONTH) + 1;
        int dayNow = ca.get(Calendar.DATE);
        int hourNow = ca.get(Calendar.HOUR);
        int mimuteNow = ca.get(Calendar.MINUTE);
        // 目标日期时间
        ca.setTimeInMillis(time);
        int year = ca.get(Calendar.YEAR);
        int mouth = ca.get(Calendar.MONTH) + 1;
        int day = ca.get(Calendar.DATE);
        int hour = ca.get(Calendar.HOUR);
        int mimute = ca.get(Calendar.MINUTE);

        String result;
        if (yearNow == year) {// 当年
            result = mouth + "-" + day;
            if (mouthNow == mouth) {// 当月
                result = Math.abs(dayNow - day) + "天前";
                if (dayNow == day) {// 当天
                    result = Math.abs(hourNow - hour) + "小时前";
                    if (hourNow == hour) {// 当时
                        result = Math.abs(mimuteNow - mimute) + "分钟前";
                        if (mimuteNow == mimute) {// 当分
                            result = "刚刚";
                        }
                    }
                }
            }
        } else {
            result = year + "-" + mouth + "-" + day;
        }
        return result;
    }

}
