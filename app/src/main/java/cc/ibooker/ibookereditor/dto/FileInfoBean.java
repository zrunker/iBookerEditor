package cc.ibooker.ibookereditor.dto;

/**
 * 文件相关信息
 * Created by 邹峰立 on 2018/3/3.
 */
public class FileInfoBean {
    private int id;
    private String fileName;// 文件名-文章标题
    private String filePath;// 文件路径
    private long fileSize;// 文件大小 - 字节为单位
    private long fileCreateTime;// 文件创建时间

    public FileInfoBean() {
        super();
    }

    public FileInfoBean(String fileName, String filePath, long fileSize) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public FileInfoBean(int id, String fileName, String filePath, long fileSize, long fileCreateTime) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileCreateTime = fileCreateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(long fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    @Override
    public String toString() {
        return "FileInfoBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileCreateTime=" + fileCreateTime +
                '}';
    }
}
