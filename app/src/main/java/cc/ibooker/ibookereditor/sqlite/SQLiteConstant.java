package cc.ibooker.ibookereditor.sqlite;

/**
 * SQLite常量类
 * Created by 邹峰立 on 2017/2/16 0016.
 */
public class SQLiteConstant {
    static final String DB_NAME = "ibookereditor.db"; //数据库名称
    static final int DB_VERSION = 1; //数据库版本号
    //创建User表的SQL语句
    static final String SQL_CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS t_user(_id INTEGER PRIMARY KEY autoincrement,app_token VARCHAR(100) NOT NULL UNIQUE,user_id VARCHAR(30) NOT NULL UNIQUE,user_name VARCHAR(30) NOT NULL)";
    //删除User表的SQL语句
    static final String SQL_DROP_TABLE_USER = "DROP TABLE IF EXISTS t_user";
    //创建search_history搜索历史表
    static final String SQL_CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE IF NOT EXISTS t_search_history(_id INTEGER PRIMARY KEY autoincrement,content VARCHAR(100) NOT NULL UNIQUE)";
    // 删除search_history搜索历史表
    static final String SQL_DROP_TABLE_SEARCH_HISTORY = "DROP TABLE IF EXISTS t_search_history";
    // 创建新人礼包弹框表
    static final String SQL_CREATE_TABLE_NEW_GIFT_BAG_DIALOG = "CREATE TABLE IF NOT EXISTS t_new_gift_bag_dialog(_id INTEGER PRIMARY KEY autoincrement,ishasshow VARCHAR(1) NOT NULL, time VARCHAR(10) NOT NULL, userid VARCHAR(30))";
    // 删除新人礼包弹框表
    static final String SQL_DROP_TABLE_NEW_GIFT_BAG_DIALOG = "DROP TABLE IF EXISTS t_new_gift_bag_dialog";
}
