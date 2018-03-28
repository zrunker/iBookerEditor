package cc.ibooker.ibookereditor.bean;

/**
 * 文章标签表-实体类
 *
 * @author 邹峰立
 */
public class ArticleLabelEntity {
    private long alId;// 编号
    private String alName;// 标签名称
    private long alAid;// 文章ID

    public ArticleLabelEntity() {
        super();
    }

    public ArticleLabelEntity(long alId, String alName, long alAid) {
        super();
        this.alId = alId;
        this.alName = alName;
        this.alAid = alAid;
    }

    public long getAlId() {
        return alId;
    }

    public void setAlId(long alId) {
        this.alId = alId;
    }

    public String getAlName() {
        return alName;
    }

    public void setAlName(String alName) {
        this.alName = alName;
    }

    public long getAlAid() {
        return alAid;
    }

    public void setAlAid(long alAid) {
        this.alAid = alAid;
    }

    @Override
    public String toString() {
        return "ArticleLabelEntity [alId=" + alId + ", alName=" + alName
                + ", alAid=" + alAid + "]";
    }

}
