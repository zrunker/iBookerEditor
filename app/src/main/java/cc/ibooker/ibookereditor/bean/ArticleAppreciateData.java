package cc.ibooker.ibookereditor.bean;

/**
 * 文章喜欢信息-实体类
 *
 * @author 邹峰立
 */
public class ArticleAppreciateData {
    private long aaId;// 编号ID
    private long aaTime;// 喜欢时间，格式：时间戳
    private String aaIsread;// 被喜欢人是否已读，0代表未读，1代表已读，被喜欢人操作
    private String aaIsdelete;// 喜欢人是否删除，0代表未删除，1代表已删除，喜欢人操作
    private long aaAid;// 外键关联文章ID
    private long aaPuid;// 喜欢人ID
    // 文章信息
    private long aId;// 编号
    private String aTitle;// 文章标题
    private String aAbstract;// 文章摘要
    private String aContent;// 文章内容
    private String aHtml;// 文章内容Html
    private long aPubtime;// 发布时间，时间戳
    private String aArticleCodeAddr;// 文章源码地址
    private String aArticleVedioAddr;// 文章视频地址
    private String aCoverPath;// 文章封面图地址，格式：账号+时间戳+articlecover+后缀（.png/.jpg等）
    private int aBrowsenum;// 浏览量
    private String aIsonline;// 是否上线，0下线，1上线
    private String aIsdelete;// 是否删除，0未删除，1删除
    private String aIsallow;// 是否审核通过，0未审核，1已审核，2审核未通过
    private String aIspub;// 是否已发布，0未发布，1已发布
    private String aStyle;// 文章类型，0普通文章，1首页推荐文章
    private long aUid;// 用户ID
    private long aAtid;// 一级文章类别ID
    private long aAtsid;// 二级文章类别ID
    private ABookTypeEntity aBookType;// 一级文章/书籍类别
    private ABookTypeSecondEntity aBookTypeSecond;// 二级文章/书籍类别
    /**
     * 根据业务需求添加
     */
    private String aaFormatTime;// 格式化喜欢时间

    public ArticleAppreciateData() {
        super();
    }

    public ArticleAppreciateData(long aaId, long aaTime, String aaIsread,
                                 String aaIsdelete, long aaAid, long aaPuid, long aId,
                                 String aTitle, String aAbstract, String aContent, String aHtml,
                                 long aPubtime, String aArticleCodeAddr, String aArticleVedioAddr,
                                 String aCoverPath, int aBrowsenum, String aIsonline,
                                 String aIsdelete, String aIsallow, String aIspub, String aStyle,
                                 long aUid, long aAtid, long aAtsid, ABookTypeEntity aBookType,
                                 ABookTypeSecondEntity aBookTypeSecond, String aaFormatTime) {
        super();
        this.aaId = aaId;
        this.aaTime = aaTime;
        this.aaIsread = aaIsread;
        this.aaIsdelete = aaIsdelete;
        this.aaAid = aaAid;
        this.aaPuid = aaPuid;
        this.aId = aId;
        this.aTitle = aTitle;
        this.aAbstract = aAbstract;
        this.aContent = aContent;
        this.aHtml = aHtml;
        this.aPubtime = aPubtime;
        this.aArticleCodeAddr = aArticleCodeAddr;
        this.aArticleVedioAddr = aArticleVedioAddr;
        this.aCoverPath = aCoverPath;
        this.aBrowsenum = aBrowsenum;
        this.aIsonline = aIsonline;
        this.aIsdelete = aIsdelete;
        this.aIsallow = aIsallow;
        this.aIspub = aIspub;
        this.aStyle = aStyle;
        this.aUid = aUid;
        this.aAtid = aAtid;
        this.aAtsid = aAtsid;
        this.aBookType = aBookType;
        this.aBookTypeSecond = aBookTypeSecond;
        this.aaFormatTime = aaFormatTime;
    }

    public long getAaId() {
        return aaId;
    }

    public void setAaId(long aaId) {
        this.aaId = aaId;
    }

    public long getAaTime() {
        return aaTime;
    }

    public void setAaTime(long aaTime) {
        this.aaTime = aaTime;
    }

    public String getAaIsread() {
        return aaIsread;
    }

    public void setAaIsread(String aaIsread) {
        this.aaIsread = aaIsread;
    }

    public String getAaIsdelete() {
        return aaIsdelete;
    }

    public void setAaIsdelete(String aaIsdelete) {
        this.aaIsdelete = aaIsdelete;
    }

    public long getAaAid() {
        return aaAid;
    }

    public void setAaAid(long aaAid) {
        this.aaAid = aaAid;
    }

    public long getAaPuid() {
        return aaPuid;
    }

    public void setAaPuid(long aaPuid) {
        this.aaPuid = aaPuid;
    }

    public long getaId() {
        return aId;
    }

    public void setaId(long aId) {
        this.aId = aId;
    }

    public String getaTitle() {
        return aTitle;
    }

    public void setaTitle(String aTitle) {
        this.aTitle = aTitle;
    }

    public String getaAbstract() {
        return aAbstract;
    }

    public void setaAbstract(String aAbstract) {
        this.aAbstract = aAbstract;
    }

    public String getaContent() {
        return aContent;
    }

    public void setaContent(String aContent) {
        this.aContent = aContent;
    }

    public String getaHtml() {
        return aHtml;
    }

    public void setaHtml(String aHtml) {
        this.aHtml = aHtml;
    }

    public long getaPubtime() {
        return aPubtime;
    }

    public void setaPubtime(long aPubtime) {
        this.aPubtime = aPubtime;
    }

    public String getaArticleCodeAddr() {
        return aArticleCodeAddr;
    }

    public void setaArticleCodeAddr(String aArticleCodeAddr) {
        this.aArticleCodeAddr = aArticleCodeAddr;
    }

    public String getaArticleVedioAddr() {
        return aArticleVedioAddr;
    }

    public void setaArticleVedioAddr(String aArticleVedioAddr) {
        this.aArticleVedioAddr = aArticleVedioAddr;
    }

    public String getaCoverPath() {
        return aCoverPath;
    }

    public void setaCoverPath(String aCoverPath) {
        this.aCoverPath = aCoverPath;
    }

    public int getaBrowsenum() {
        return aBrowsenum;
    }

    public void setaBrowsenum(int aBrowsenum) {
        this.aBrowsenum = aBrowsenum;
    }

    public String getaIsonline() {
        return aIsonline;
    }

    public void setaIsonline(String aIsonline) {
        this.aIsonline = aIsonline;
    }

    public String getaIsdelete() {
        return aIsdelete;
    }

    public void setaIsdelete(String aIsdelete) {
        this.aIsdelete = aIsdelete;
    }

    public String getaIsallow() {
        return aIsallow;
    }

    public void setaIsallow(String aIsallow) {
        this.aIsallow = aIsallow;
    }

    public String getaIspub() {
        return aIspub;
    }

    public void setaIspub(String aIspub) {
        this.aIspub = aIspub;
    }

    public String getaStyle() {
        return aStyle;
    }

    public void setaStyle(String aStyle) {
        this.aStyle = aStyle;
    }

    public long getaUid() {
        return aUid;
    }

    public void setaUid(long aUid) {
        this.aUid = aUid;
    }

    public long getaAtid() {
        return aAtid;
    }

    public void setaAtid(long aAtid) {
        this.aAtid = aAtid;
    }

    public long getaAtsid() {
        return aAtsid;
    }

    public void setaAtsid(long aAtsid) {
        this.aAtsid = aAtsid;
    }

    public ABookTypeEntity getaBookType() {
        return aBookType;
    }

    public void setaBookType(ABookTypeEntity aBookType) {
        this.aBookType = aBookType;
    }

    public ABookTypeSecondEntity getaBookTypeSecond() {
        return aBookTypeSecond;
    }

    public void setaBookTypeSecond(ABookTypeSecondEntity aBookTypeSecond) {
        this.aBookTypeSecond = aBookTypeSecond;
    }

    public String getAaFormatTime() {
        return aaFormatTime;
    }

    public void setAaFormatTime(String aaFormatTime) {
        this.aaFormatTime = aaFormatTime;
    }

    @Override
    public String toString() {
        return "ArticleAppreciateData [aaId=" + aaId + ", aaTime=" + aaTime
                + ", aaIsread=" + aaIsread + ", aaIsdelete=" + aaIsdelete
                + ", aaAid=" + aaAid + ", aaPuid=" + aaPuid + ", aId=" + aId
                + ", aTitle=" + aTitle + ", aAbstract=" + aAbstract
                + ", aContent=" + aContent + ", aHtml=" + aHtml + ", aPubtime="
                + aPubtime + ", aArticleCodeAddr=" + aArticleCodeAddr
                + ", aArticleVedioAddr=" + aArticleVedioAddr + ", aCoverPath="
                + aCoverPath + ", aBrowsenum=" + aBrowsenum + ", aIsonline="
                + aIsonline + ", aIsdelete=" + aIsdelete + ", aIsallow="
                + aIsallow + ", aIspub=" + aIspub + ", aStyle=" + aStyle
                + ", aUid=" + aUid + ", aAtid=" + aAtid + ", aAtsid=" + aAtsid
                + ", aBookType=" + aBookType + ", aBookTypeSecond="
                + aBookTypeSecond + ", aaFormatTime=" + aaFormatTime + "]";
    }

}
