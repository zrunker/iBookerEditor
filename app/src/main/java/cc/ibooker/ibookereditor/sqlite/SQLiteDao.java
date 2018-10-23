package cc.ibooker.ibookereditor.sqlite;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.dto.FileInfoBean;
import cc.ibooker.ibookereditor.dto.UserDto;

/**
 * 数据库访问接口
 * Created by 邹封立 on 2017/2/16 0016.
 */
public interface SQLiteDao {
    /**
     * 插入本地文件信息
     *
     * @param data 插入值（本地文件信息）
     */
    void insertLocalFile(FileInfoBean data);

    /**
     * 插入本地文件信息，返回ID信息
     *
     * @param data 插入值（本地文件信息）
     */
    int insertLocalFile2(FileInfoBean data);

    /**
     * 根据ID删除本地文件信息
     *
     * @param _id 本地文件ID
     */
    void deleteLocalFileById(int _id);

    /**
     * 根据ID修改本地文件信息
     *
     * @param data 待修改本地文件信息
     * @param _id  文件信息ID
     */
    void updateLocalFileById(FileInfoBean data, int _id);

    /**
     * 通过文件地址查询ID
     *
     * @param filePath 文件地址
     */
    int selectIdByFilePath(String filePath);

    /**
     * 根据时间查询所有本地文件
     */
    ArrayList<FileInfoBean> selectLocalFilesByTime();

    /**
     * 根据时间分页查询所有本地文件
     *
     * @param page 当前页
     */
    ArrayList<FileInfoBean> selectLocalFilesByTimePager(int page);

    /**
     * 插入用户表
     *
     * @param data 待插入数据
     */
    void insertUser(UserDto data);

    /**
     * 根据账号或者用户ID删除用户信息
     *
     * @param data 待删除数据
     */
    void deleteUser(UserDto data);

    /**
     * 查询用户信息
     */
    UserDto selectUser();

    /**
     * 修改用户表
     *
     * @param data 待修改数据
     */
    void updateUser(UserDto data);
}
