package cc.ibooker.ibookereditor.bean;

import java.io.File;

/**
 * 本地数据
 */
public class LocalEntity {
    private long aId;
    private long aTime;
    private String aTitle;
    private String aFilePath;
    // 根据业务需求额外添加
    private String aFormatSize;
    private String aFormatTime;
    private String aFormatContent;
    private File file;

    public LocalEntity() {
        super();
    }

    public LocalEntity(long aId, long aTime, String aTitle, String aFilePath, String aFormatSize, String aFormatTime, String aFormatContent, File file) {
        this.aId = aId;
        this.aTime = aTime;
        this.aTitle = aTitle;
        this.aFilePath = aFilePath;
        this.aFormatSize = aFormatSize;
        this.aFormatTime = aFormatTime;
        this.aFormatContent = aFormatContent;
        this.file = file;
    }

    public long getaId() {
        return aId;
    }

    public void setaId(long aId) {
        this.aId = aId;
    }

    public long getaTime() {
        return aTime;
    }

    public void setaTime(long aTime) {
        this.aTime = aTime;
    }

    public String getaTitle() {
        return aTitle;
    }

    public void setaTitle(String aTitle) {
        this.aTitle = aTitle;
    }

    public String getaFilePath() {
        return aFilePath;
    }

    public void setaFilePath(String aFilePath) {
        this.aFilePath = aFilePath;
    }

    public String getaFormatSize() {
        return aFormatSize;
    }

    public void setaFormatSize(String aFormatSize) {
        this.aFormatSize = aFormatSize;
    }

    public String getaFormatTime() {
        return aFormatTime;
    }

    public void setaFormatTime(String aFormatTime) {
        this.aFormatTime = aFormatTime;
    }

    public String getaFormatContent() {
        return aFormatContent;
    }

    public void setaFormatContent(String aFormatContent) {
        this.aFormatContent = aFormatContent;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "LocalEntity{" +
                "aId=" + aId +
                ", aTime=" + aTime +
                ", aTitle='" + aTitle + '\'' +
                ", aFilePath='" + aFilePath + '\'' +
                ", aFormatSize='" + aFormatSize + '\'' +
                ", aFormatTime='" + aFormatTime + '\'' +
                ", aFormatContent='" + aFormatContent + '\'' +
                ", file=" + file +
                '}';
    }
}
