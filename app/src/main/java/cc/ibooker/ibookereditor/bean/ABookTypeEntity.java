package cc.ibooker.ibookereditor.bean;

/**
 * 文章/书籍一级类别表-实体类
 *
 * @author 邹峰立
 */
public class ABookTypeEntity {
    private long atId;// 编号
    private String atName;// 类别名
    private String atNameDesc;// 类别描述信息
    private String atPinyin;// 类别名拼音

    public ABookTypeEntity() {
        super();
    }

    public ABookTypeEntity(long atId, String atName, String atNameDesc,
                           String atPinyin) {
        super();
        this.atId = atId;
        this.atName = atName;
        this.atNameDesc = atNameDesc;
        this.atPinyin = atPinyin;
    }

    public long getAtId() {
        return atId;
    }

    public void setAtId(long atId) {
        this.atId = atId;
    }

    public String getAtName() {
        return atName;
    }

    public void setAtName(String atName) {
        this.atName = atName;
    }

    public String getAtNameDesc() {
        return atNameDesc;
    }

    public void setAtNameDesc(String atNameDesc) {
        this.atNameDesc = atNameDesc;
    }

    public String getAtPinyin() {
        return atPinyin;
    }

    public void setAtPinyin(String atPinyin) {
        this.atPinyin = atPinyin;
    }

    @Override
    public String toString() {
        return "ABookTypeEntity [atId=" + atId + ", atName=" + atName
                + ", atNameDesc=" + atNameDesc + ", atPinyin=" + atPinyin + "]";
    }

}
