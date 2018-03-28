package cc.ibooker.ibookereditor.sqlite;

import android.content.Context;

/**
 * 数据库访问接口实现类
 * Created by 邹峰立 on 2017/2/16 0016.
 */
public class SQLiteDaoImpl implements SQLiteDao {
    private SQLiteHelper dbHelper = null;

    /**
     * 构造方法
     *
     * @param context 上下文对象
     */
    public SQLiteDaoImpl(Context context) {
        dbHelper = SQLiteHelper.getSqliteHelper(context);
    }

//    /**
//     * 插入用户信息-删除旧的数据
//     *
//     * @param user 插入值（用户信息）
//     */
//    @Override
//    public void insertUserOne(User user) {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
//        db.execSQL("delete from t_user", new Object[]{});
//        if (user != null) {
//            String sql = "insert into t_user(app_token, user_id, user_name) values(?,?,?)";
//            db.execSQL(sql, new Object[]{user.getApp_token(), user.getUser_id(), user.getUser_name()});
//        }
//        dbHelper.closeDatabase();
//    }
//
//    /**
//     * 插入用户信息
//     *
//     * @param user 插入值（用户信息）
//     */
//    @Override
//    public synchronized void insertUser(User user) {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
//        String sql = "insert into t_user(app_token, user_id, user_name) values(?,?,?)";
//        db.execSQL(sql, new Object[]{user.getApp_token(), user.getUser_id(), user.getUser_name()});
//        dbHelper.closeDatabase();
//    }
//
//    /**
//     * 删除用户信息
//     */
//    @Override
//    public synchronized void deleteUser() {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
//        db.execSQL("delete from t_user", new Object[]{});
//        dbHelper.closeDatabase();
//    }
//
//    /**
//     * 查询用户信息
//     */
//    @Override
//    public synchronized User selectUser() {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
//        User user = null;
//        Cursor cursor = db.rawQuery("select * from t_user", new String[]{});
//        while (cursor.moveToNext()) {
//            user = new User();
//            user.setApp_token(cursor.getString(cursor.getColumnIndex("app_token")));
//            user.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
//            user.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
//        }
//        cursor.close();
//        dbHelper.closeDatabase();
//        return user;
//    }
//
//    /**
//     * 查询历史搜索内容
//     */
//    @Override
//    public synchronized List<String> selectSearchHistory() {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
//        List<String> list = new ArrayList<>();
//        Cursor cursor = db.rawQuery("select * from t_search_history", new String[]{});
//        while (cursor.moveToNext()) {
//            String str = cursor.getString(cursor.getColumnIndex("content"));
//            list.add(str);
//        }
//        cursor.close();
//        dbHelper.closeDatabase();
//        return list;
//    }
//
//    /**
//     * 判断搜索是否已经存在
//     */
//    @Override
//    public synchronized boolean isSearchExists(String content) {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
//        String sql = "select * from t_search_history where content = ?";
//        Cursor cursor = db.rawQuery(sql, new String[]{content});
//        boolean exists = cursor.moveToNext();
//        cursor.close();
//        dbHelper.closeDatabase();
//        return exists;
//    }
//
//    /**
//     * 插入历史搜索表
//     */
//    @Override
//    public synchronized void insertSearchHistory(String content) {
//        if (!isSearchExists(content)) {
//            // 不存在才进行插入
//            SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
//            String sql = "insert into t_search_history(content) values(?)";
//            db.execSQL(sql, new Object[]{content});
//            dbHelper.closeDatabase();
//        }
//    }
//
//    /**
//     * 删除历史搜索表
//     */
//    @Override
//    public void deleteSearchHistory() {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
//        db.execSQL("delete from t_search_history", new Object[]{});
//        dbHelper.closeDatabase();
//    }
//
//    /**
//     * 插入新人礼包弹框记录表
//     */
//    @Override
//    public void insertNewGiftBagDiyDialog(NewGiftBagDiyDialogEntity newGiftBagDiyDialogEntity) {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
//        try {
//            String sql = "insert into t_new_gift_bag_dialog(ishasshow,time,userid) values(?,?,?)";
//            db.execSQL(sql, new Object[]{newGiftBagDiyDialogEntity.getIshasshow(), newGiftBagDiyDialogEntity.getTime(), newGiftBagDiyDialogEntity.getUserid()});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dbHelper.closeDatabase();
//    }
//
//    /**
//     * 查询新人礼包弹框记录表
//     */
//    @Override
//    public NewGiftBagDiyDialogEntity selectNewGiftBagDiyDialog(String userid, String time) {
//        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
//        NewGiftBagDiyDialogEntity newGiftBagDiyDialogEntity = null;
//        try {
//            String sql = "select * from t_new_gift_bag_dialog where time=? and userid=?";
//            Cursor cursor = db.rawQuery(sql, new String[]{time, userid});
//            while (cursor.moveToNext()) {
//                newGiftBagDiyDialogEntity = new NewGiftBagDiyDialogEntity();
//                newGiftBagDiyDialogEntity.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
//                newGiftBagDiyDialogEntity.setIshasshow(cursor.getString(cursor.getColumnIndex("ishasshow")));
//                newGiftBagDiyDialogEntity.setTime(cursor.getString(cursor.getColumnIndex("time")));
//                newGiftBagDiyDialogEntity.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dbHelper.closeDatabase();
//        return newGiftBagDiyDialogEntity;
//    }
}
