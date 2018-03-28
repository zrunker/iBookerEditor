package cc.ibooker.ibookereditor.bean;

import java.util.List;

/**
 * 文章信息类
 *
 * @author 邹峰立
 */
public class ArticleUserData {
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
    private long aAtid;// 文章类别ID
    private List<ArticleLabelEntity> articleLabels;// 文章标签
    private ABookTypeEntity aBookType;// 一级文章/书籍类别
    private ABookTypeSecondEntity aBookTypeSecond;// 二级文章/书籍类别
    private UserEntity user;// 作者信息
    /**
     * 根据业务需要额外添加
     */
    private String aFormatPubtime;// 格式化发布时间
    private int aZanCount;// 赞数量
    private int aAppreciateCount;// 喜欢数量
    private String aFormatArticleLabel;// 格式化文章标签
    private boolean isZan;// 是否已赞文章
    private boolean isAppreciate;// 是否已喜欢文章
    private boolean isAttentionUser;// 是否已关注作者

    public ArticleUserData() {
        super();
    }

    public ArticleUserData(long aId, String aTitle, String aAbstract,
                           String aContent, String aHtml, long aPubtime,
                           String aArticleCodeAddr, String aArticleVedioAddr,
                           String aCoverPath, int aBrowsenum, String aIsonline,
                           String aIsdelete, String aIsallow, String aIspub, String aStyle,
                           long aUid, long aAtid, List<ArticleLabelEntity> articleLabels,
                           ABookTypeEntity aBookType, ABookTypeSecondEntity aBookTypeSecond,
                           UserEntity user, String aFormatPubtime, int aZanCount,
                           int aAppreciateCount, String aFormatArticleLabel, boolean isZan,
                           boolean isAppreciate, boolean isAttentionUser) {
        super();
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
        this.articleLabels = articleLabels;
        this.aBookType = aBookType;
        this.aBookTypeSecond = aBookTypeSecond;
        this.user = user;
        this.aFormatPubtime = aFormatPubtime;
        this.aZanCount = aZanCount;
        this.aAppreciateCount = aAppreciateCount;
        this.aFormatArticleLabel = aFormatArticleLabel;
        this.isZan = isZan;
        this.isAppreciate = isAppreciate;
        this.isAttentionUser = isAttentionUser;
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

    public List<ArticleLabelEntity> getArticleLabels() {
        return articleLabels;
    }

    public void setArticleLabels(List<ArticleLabelEntity> articleLabels) {
        this.articleLabels = articleLabels;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getaFormatPubtime() {
        return aFormatPubtime;
    }

    public void setaFormatPubtime(String aFormatPubtime) {
        this.aFormatPubtime = aFormatPubtime;
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

    public int getaZanCount() {
        return aZanCount;
    }

    public void setaZanCount(int aZanCount) {
        this.aZanCount = aZanCount;
    }

    public int getaAppreciateCount() {
        return aAppreciateCount;
    }

    public void setaAppreciateCount(int aAppreciateCount) {
        this.aAppreciateCount = aAppreciateCount;
    }

    public boolean isZan() {
        return isZan;
    }

    public void setZan(boolean isZan) {
        this.isZan = isZan;
    }

    public boolean isAppreciate() {
        return isAppreciate;
    }

    public void setAppreciate(boolean isAppreciate) {
        this.isAppreciate = isAppreciate;
    }

    public boolean isAttentionUser() {
        return isAttentionUser;
    }

    public void setAttentionUser(boolean isAttentionUser) {
        this.isAttentionUser = isAttentionUser;
    }

    public String getaFormatArticleLabel() {
        return aFormatArticleLabel;
    }

    public void setaFormatArticleLabel(String aFormatArticleLabel) {
        this.aFormatArticleLabel = aFormatArticleLabel;
    }

    @Override
    public String toString() {
        return "ArticleUserData [aId=" + aId + ", aTitle=" + aTitle
                + ", aAbstract=" + aAbstract + ", aContent=" + aContent
                + ", aHtml=" + aHtml + ", aPubtime=" + aPubtime
                + ", aArticleCodeAddr=" + aArticleCodeAddr
                + ", aArticleVedioAddr=" + aArticleVedioAddr + ", aCoverPath="
                + aCoverPath + ", aBrowsenum=" + aBrowsenum + ", aIsonline="
                + aIsonline + ", aIsdelete=" + aIsdelete + ", aIsallow="
                + aIsallow + ", aIspub=" + aIspub + ", aStyle=" + aStyle
                + ", aUid=" + aUid + ", aAtid=" + aAtid + ", articleLabels="
                + articleLabels + ", aBookType=" + aBookType
                + ", aBookTypeSecond=" + aBookTypeSecond + ", user=" + user
                + ", aFormatPubtime=" + aFormatPubtime + ", aZanCount="
                + aZanCount + ", aAppreciateCount=" + aAppreciateCount
                + ", aFormatArticleLabel=" + aFormatArticleLabel + ", isZan="
                + isZan + ", isAppreciate=" + isAppreciate
                + ", isAttentionUser=" + isAttentionUser + "]";
    }

}
