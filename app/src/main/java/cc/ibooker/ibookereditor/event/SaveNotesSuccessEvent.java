package cc.ibooker.ibookereditor.event;

import cc.ibooker.ibookereditor.dto.FileInfoBean;

/**
 * 保存笔记事件
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class SaveNotesSuccessEvent {
    private boolean isflashData;// 是否刷新数据
    private int _id;// 本地文件ID
    private FileInfoBean fileInfoBean;// 本地文件相关数据

    public SaveNotesSuccessEvent(boolean isflashData, int _id, FileInfoBean fileInfoBean) {
        this.isflashData = isflashData;
        this._id = _id;
        this.fileInfoBean = fileInfoBean;
    }

    public boolean isIsflashData() {
        return isflashData;
    }

    public void setIsflashData(boolean isflashData) {
        this.isflashData = isflashData;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public FileInfoBean getFileInfoBean() {
        return fileInfoBean;
    }

    public void setFileInfoBean(FileInfoBean fileInfoBean) {
        this.fileInfoBean = fileInfoBean;
    }

    @Override
    public String toString() {
        return "SaveArticleSuccessEvent{" +
                "isflashData=" + isflashData +
                ", _id=" + _id +
                ", fileInfoBean=" + fileInfoBean +
                '}';
    }
}
