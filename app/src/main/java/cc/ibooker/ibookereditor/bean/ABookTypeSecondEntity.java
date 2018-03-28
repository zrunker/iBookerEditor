package cc.ibooker.ibookereditor.bean;

/**
 * 文章/书籍二级类别表-实体类
 *
 * @author 邹峰立
 */
public class ABookTypeSecondEntity {
    private long atsId;// 编号
    private String atsName;// 类别名
    private String atsNameDesc;// 类别描述信息
    private String atsPinyin;// 类别名拼音
    private long atsAtid;// 一级类型ID

    public ABookTypeSecondEntity() {
        super();
    }

    public ABookTypeSecondEntity(long atsId, String atsName, String atsNameDesc,
                                 String atsPinyin, long atsAtid) {
        super();
        this.atsId = atsId;
        this.atsName = atsName;
        this.atsNameDesc = atsNameDesc;
        this.atsPinyin = atsPinyin;
        this.atsAtid = atsAtid;
    }

    public long getAtsId() {
        return atsId;
    }

    public void setAtsId(long atsId) {
        this.atsId = atsId;
    }

    public String getAtsName() {
        return atsName;
    }

    public void setAtsName(String atsName) {
        this.atsName = atsName;
    }

    public String getAtsNameDesc() {
        return atsNameDesc;
    }

    public void setAtsNameDesc(String atsNameDesc) {
        this.atsNameDesc = atsNameDesc;
    }

    public String getAtsPinyin() {
        return atsPinyin;
    }

    public void setAtsPinyin(String atsPinyin) {
        this.atsPinyin = atsPinyin;
    }

    public long getAtsAtid() {
        return atsAtid;
    }

    public void setAtsAtid(long atsAtid) {
        this.atsAtid = atsAtid;
    }

    @Override
    public String toString() {
        return "ABookTypeSecondEntity [atsId=" + atsId + ", atsName=" + atsName
                + ", atsNameDesc=" + atsNameDesc + ", atsPinyin=" + atsPinyin
                + ", atsAtid=" + atsAtid + "]";
    }

}
