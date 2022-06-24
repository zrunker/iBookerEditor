package cc.ibooker.ibookereditor.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.dto.FileInfoBean;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PAGE_SIZE_LOCAL_ARTICLE;

/**
 * 数据库访问接口实现类
 * Created by 邹峰立 on 2017/2/16 0016.
 */
public class SQLiteDaoImpl implements SQLiteDao {
    private final SQLiteHelper dbHelper;

    /**
     * 构造方法
     *
     * @param context 上下文对象
     */
    public SQLiteDaoImpl(Context context) {
        dbHelper = SQLiteHelper.getSqliteHelper(context);
    }

    /**
     * 插入本地文件信息
     *
     * @param data 插入值（本地文件信息）
     */
    @Override
    public synchronized void insertLocalFile(FileInfoBean data) {
        if (data != null) {
            SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
            String sql = "insert into t_local_file(lf_name, lf_path, lf_size, lf_create_time) values(?,?,?,?)";
            db.execSQL(sql, new Object[]{data.getFileName(), data.getFilePath(), data.getFileSize(), data.getFileCreateTime()});
            dbHelper.closeDatabase();
        }
    }

    /**
     * 插入本地文件信息，返回ID信息
     *
     * @param data 插入值（本地文件信息）
     */
    @Override
    public synchronized int insertLocalFile2(FileInfoBean data) {
        if (data != null) {
            SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库

            // 首先查找是否有该文件
            int _id = selectIdByFilePath(data.getFilePath());
            if (_id > 0) {
                deleteLocalFileById(_id);
            }
            // 插入数据
            String sql = "insert into t_local_file(lf_name, lf_path, lf_size, lf_create_time) values(?,?,?,?)";
            db.execSQL(sql, new Object[]{data.getFileName(), data.getFilePath(), data.getFileSize(), data.getFileCreateTime()});

            // 查询插入的ID
            Cursor cursor = db.rawQuery("select last_insert_rowid() from t_local_file", null);
            if (cursor.moveToFirst())
                _id = cursor.getInt(0);

            cursor.close();
            dbHelper.closeDatabase();
            return _id;
        }
        return 0;
    }

    /**
     * 根据ID删除本地文件信息
     *
     * @param _id 本地文件ID
     */
    @Override
    public synchronized void deleteLocalFileById(int _id) {
        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
        db.execSQL("delete from t_local_file where _id=?", new Object[]{_id});
        dbHelper.closeDatabase();
    }

    /**
     * 根据ID修改本地文件信息
     *
     * @param data 待修改本地文件信息
     * @param _id  文件信息ID
     */
    @Override
    public synchronized void updateLocalFileById(FileInfoBean data, int _id) {
        if (data != null) {
            SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可写的数据库
            db.execSQL("update t_local_file set lf_name = ?, lf_path = ?, lf_size = ? where _id=?", new Object[]{data.getFileName(), data.getFilePath(), data.getFileSize(), _id});
            dbHelper.closeDatabase();
        }
    }

    /**
     * 通过文件地址查询ID
     *
     * @param filePath 文件地址
     */
    @Override
    public int selectIdByFilePath(String filePath) {
        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
        int _id = 0;
        Cursor cursor = db.rawQuery("select _id from t_local_file where lf_path=?", new String[]{filePath});
        if (cursor.moveToFirst()) {
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        cursor.close();
        dbHelper.closeDatabase();
        return _id;
    }

    /**
     * 根据时间查询所有本地文件
     */
    @Override
    public synchronized ArrayList<FileInfoBean> selectLocalFilesByTime() {
        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
        ArrayList<FileInfoBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from t_local_file order by lf_create_time desc", new String[]{});
        while (cursor.moveToNext()) {
            FileInfoBean data = new FileInfoBean();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            data.setId(id);
            String fileName = cursor.getString(cursor.getColumnIndex("lf_name"));
            data.setFileName(fileName);
            String filePath = cursor.getString(cursor.getColumnIndex("lf_path"));
            data.setFilePath(filePath);
            long fileSize = cursor.getLong(cursor.getColumnIndex("lf_size"));
            data.setFileSize(fileSize);
            long fileCreateTime = cursor.getLong(cursor.getColumnIndex("lf_create_time"));
            data.setFileCreateTime(fileCreateTime);
            list.add(data);
        }
        cursor.close();
        dbHelper.closeDatabase();
        return list;
    }

    /**
     * 根据时间分页查询所有本地文件
     *
     * @param page 当前页
     */
    @Override
    public synchronized ArrayList<FileInfoBean> selectLocalFilesByTimePager(int page) {
        int startLimit = (page - 1) * PAGE_SIZE_LOCAL_ARTICLE;
        int endLimit = page * PAGE_SIZE_LOCAL_ARTICLE;
        SQLiteDatabase db = dbHelper.openDatabase(); // 获取一个可读的数据库
        ArrayList<FileInfoBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from t_local_file order by lf_create_time desc limit ?, ?", new String[]{startLimit + "", endLimit + ""});
        while (cursor.moveToNext()) {
            FileInfoBean data = new FileInfoBean();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            data.setId(id);
            String fileName = cursor.getString(cursor.getColumnIndex("lf_name"));
            data.setFileName(fileName);
            String filePath = cursor.getString(cursor.getColumnIndex("lf_path"));
            data.setFilePath(filePath);
            long fileSize = cursor.getLong(cursor.getColumnIndex("lf_size"));
            data.setFileSize(fileSize);
            long fileCreateTime = cursor.getLong(cursor.getColumnIndex("lf_create_time"));
            data.setFileCreateTime(fileCreateTime);
            list.add(data);
        }
        cursor.close();
        dbHelper.closeDatabase();
        return list;
    }

}
