package cc.ibooker.ibookereditor.utils;

import android.content.Context;

import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;

/**
 * 用户管理类
 */
public class UserUtil {

    /**
     * 判断用户是否登录
     */
    public synchronized static boolean isLogin(Context context) {
        if (ConstantUtil.userDto == null || ConstantUtil.userDto.getUser() == null) {
            SQLiteDao sqLiteDao = new SQLiteDaoImpl(context.getApplicationContext());
            ConstantUtil.userDto = sqLiteDao.selectUser();
        }
        return ConstantUtil.userDto != null && ConstantUtil.userDto.getUser() != null;
    }

    /**
     * 退出登录登录
     */
    public synchronized static void logout(Context context) {
        if (ConstantUtil.userDto != null) {
            SQLiteDao sqLiteDao = new SQLiteDaoImpl(context.getApplicationContext());
            sqLiteDao.deleteUser(ConstantUtil.userDto);
            ConstantUtil.userDto = null;
        }
    }
}
