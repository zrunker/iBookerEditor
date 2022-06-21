package cc.ibooker.ibookereditor.sqlite;

/**
 * SQLite常量类
 * Created by 邹峰立 on 2017/2/16 0016.
 */
public class SQLiteConstant {
    static final String DB_NAME = "ibookereditor.db"; //数据库名称
    static final int DB_VERSION = 1; //数据库版本号
    // 创建本地文件表的SQL语句
    static final String SQL_CREATE_TABLE_LOCALFILE = "CREATE TABLE IF NOT EXISTS t_local_file(_id INTEGER PRIMARY KEY autoincrement,lf_name VARCHAR(100) NOT NULL,lf_path VARCHAR(200) NOT NULL UNIQUE,lf_size BIGINT DEFAULT 0,lf_create_time BIGINT NOT NULL)";
    // 删除本地文件表的SQL语句
    static final String SQL_DROP_TABLE_LOCALFILE = "DROP TABLE IF EXISTS t_local_file";
    // 创建用户表
    static final String SQL_CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS t_user(_id INTEGER PRIMARY KEY autoincrement,u_id BIGINT NOT NULL UNIQUE,u_phone BIGINT NOT NULL UNIQUE,u_pic VARCHAR(100) NOT NULL,u_nickname VARCHAR(15) NOT NULL,u_introduce VARCHAR(500),u_sex VARCHAR(6),u_height FLOAT,u_weight FLOAT,u_birthday VARCHAR(10),u_domicile VARCHAR(50),u_pointx DOUBLE,u_pointy DOUBLE,u_ua VARCHAR(500) NOT NULL,u_token VARCHAR(500) NOT NULL)";
    // 删除用户表
    static final String SQL_DROP_TABLE_USER = "DROP TABLE IF EXISTS t_user";
}
