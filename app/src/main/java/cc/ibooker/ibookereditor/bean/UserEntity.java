package cc.ibooker.ibookereditor.bean;

/**
 * 用户表-实体类
 *
 * @author 邹峰立
 */
public class UserEntity {
    private long uId;// 编号
    private long uPhone;// 账号-手机号
    private String uPasswd;// 密码，4-20字符，MD5加密
    private String uPic;// 头像，格式：账号+时间戳+后缀（.png/.jpg等）
    private long uRegtime;// 注册时间，时间戳
    private String uNickname;// 昵称，最多15个汉字
    private String uSex;// 性别
    private float uHeight;// 身高，单位CM
    private float uWeight;// 体重，单位KG
    private String uBirthday;// 出生日期，格式：yyyy-MM-dd
    private String uDomicile;// 现居住地
    private double uPointx;// 现居住地-经度
    private double uPointy;// 现居住地-维度
    private String uEmail;// 绑定邮箱账号
    private String uWeixin;// 绑定微信账号
    private String uWeibo;// 绑定微博账号
    private String uQq;// 绑定QQ账号
    private String uIntroduce;// 自我简介，最多500个汉字
    private String uIsboard;// 基本信息是否对外公开，0不公开，1公开
    private String uRealnameIdentifyState;// 实名认证状态，0未认证，1待认证，2已认证，3认证不通过
    private String uTxToken;// 即时通讯token
    private String uType;// 用户类型，0普通用户，11,12,13,14,15分别为爱书客一到五级用户，2普通管理员，3系统管理员，4高级管理员
    private long uCheckUid;// 用户类型审核人员ID
    private String uSignState;// 是否签约，0未签约，1已签约
    private String uState;// 账号状态，0正常状态，1冻结，2封号
    /**
     * 业务需求额外添加数据
     */
    private String uToken;// Token数据JSON
    private String uIsvip;// 是否为会员，0不是会员，1是会员
    private String uFormatState;// 格式化账号状态
    private String uFormatRegtime;// 格式化注册时间
    private long uRegDays;// 注册天数
    private String uFormatEmail;// 格式化邮箱
    private String uFormatHeight;// 格式化身高
    private String uFormatWeight;// 格式化体重
    private String uForamtType;// 格式化用户类型
    private String uFormatRealnameIdentifyState;// 格式化实名认证状态
    private String uArticleCount;// 发布文章数

    public UserEntity() {
        super();
    }

    public UserEntity(long uId, long uPhone, String uPasswd, String uPic,
                      long uRegtime, String uNickname, String uSex, float uHeight,
                      float uWeight, String uBirthday, String uDomicile, double uPointx,
                      double uPointy, String uEmail, String uWeixin, String uWeibo,
                      String uQq, String uIntroduce, String uIsboard,
                      String uRealnameIdentifyState, String uTxToken, String uType,
                      long uCheckUid, String uSignState, String uState, String uToken,
                      String uIsvip, String uFormatState, String uFormatRegtime,
                      long uRegDays, String uFormatEmail, String uFormatHeight,
                      String uFormatWeight, String uForamtType,
                      String uFormatRealnameIdentifyState, String uArticleCount) {
        super();
        this.uId = uId;
        this.uPhone = uPhone;
        this.uPasswd = uPasswd;
        this.uPic = uPic;
        this.uRegtime = uRegtime;
        this.uNickname = uNickname;
        this.uSex = uSex;
        this.uHeight = uHeight;
        this.uWeight = uWeight;
        this.uBirthday = uBirthday;
        this.uDomicile = uDomicile;
        this.uPointx = uPointx;
        this.uPointy = uPointy;
        this.uEmail = uEmail;
        this.uWeixin = uWeixin;
        this.uWeibo = uWeibo;
        this.uQq = uQq;
        this.uIntroduce = uIntroduce;
        this.uIsboard = uIsboard;
        this.uRealnameIdentifyState = uRealnameIdentifyState;
        this.uTxToken = uTxToken;
        this.uType = uType;
        this.uCheckUid = uCheckUid;
        this.uSignState = uSignState;
        this.uState = uState;
        this.uToken = uToken;
        this.uIsvip = uIsvip;
        this.uFormatState = uFormatState;
        this.uFormatRegtime = uFormatRegtime;
        this.uRegDays = uRegDays;
        this.uFormatEmail = uFormatEmail;
        this.uFormatHeight = uFormatHeight;
        this.uFormatWeight = uFormatWeight;
        this.uForamtType = uForamtType;
        this.uFormatRealnameIdentifyState = uFormatRealnameIdentifyState;
        this.uArticleCount = uArticleCount;
    }

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public long getuPhone() {
        return uPhone;
    }

    public void setuPhone(long uPhone) {
        this.uPhone = uPhone;
    }

    public String getuPasswd() {
        return uPasswd;
    }

    public void setuPasswd(String uPasswd) {
        this.uPasswd = uPasswd;
    }

    public String getuPic() {
        return uPic;
    }

    public void setuPic(String uPic) {
        this.uPic = uPic;
    }

    public long getuRegtime() {
        return uRegtime;
    }

    public void setuRegtime(long uRegtime) {
        this.uRegtime = uRegtime;
    }

    public String getuNickname() {
        return uNickname;
    }

    public void setuNickname(String uNickname) {
        this.uNickname = uNickname;
    }

    public String getuSex() {
        return uSex;
    }

    public void setuSex(String uSex) {
        this.uSex = uSex;
    }

    public float getuHeight() {
        return uHeight;
    }

    public void setuHeight(float uHeight) {
        this.uHeight = uHeight;
    }

    public float getuWeight() {
        return uWeight;
    }

    public void setuWeight(float uWeight) {
        this.uWeight = uWeight;
    }

    public String getuBirthday() {
        return uBirthday;
    }

    public void setuBirthday(String uBirthday) {
        this.uBirthday = uBirthday;
    }

    public String getuDomicile() {
        return uDomicile;
    }

    public void setuDomicile(String uDomicile) {
        this.uDomicile = uDomicile;
    }

    public double getuPointx() {
        return uPointx;
    }

    public void setuPointx(double uPointx) {
        this.uPointx = uPointx;
    }

    public double getuPointy() {
        return uPointy;
    }

    public void setuPointy(double uPointy) {
        this.uPointy = uPointy;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuWeixin() {
        return uWeixin;
    }

    public void setuWeixin(String uWeixin) {
        this.uWeixin = uWeixin;
    }

    public String getuWeibo() {
        return uWeibo;
    }

    public void setuWeibo(String uWeibo) {
        this.uWeibo = uWeibo;
    }

    public String getuQq() {
        return uQq;
    }

    public void setuQq(String uQq) {
        this.uQq = uQq;
    }

    public String getuIntroduce() {
        return uIntroduce;
    }

    public void setuIntroduce(String uIntroduce) {
        this.uIntroduce = uIntroduce;
    }

    public String getuIsboard() {
        return uIsboard;
    }

    public void setuIsboard(String uIsboard) {
        this.uIsboard = uIsboard;
    }

    public String getuRealnameIdentifyState() {
        return uRealnameIdentifyState;
    }

    public void setuRealnameIdentifyState(String uRealnameIdentifyState) {
        this.uRealnameIdentifyState = uRealnameIdentifyState;
    }

    public String getuTxToken() {
        return uTxToken;
    }

    public void setuTxToken(String uTxToken) {
        this.uTxToken = uTxToken;
    }

    public String getuType() {
        return uType;
    }

    public void setuType(String uType) {
        this.uType = uType;
    }

    public long getuCheckUid() {
        return uCheckUid;
    }

    public void setuCheckUid(long uCheckUid) {
        this.uCheckUid = uCheckUid;
    }

    public String getuSignState() {
        return uSignState;
    }

    public void setuSignState(String uSignState) {
        this.uSignState = uSignState;
    }

    public String getuState() {
        return uState;
    }

    public void setuState(String uState) {
        this.uState = uState;
    }

    public String getuToken() {
        return uToken;
    }

    public void setuToken(String uToken) {
        this.uToken = uToken;
    }

    public String getuIsvip() {
        return uIsvip;
    }

    public void setuIsvip(String uIsvip) {
        this.uIsvip = uIsvip;
    }

    public String getuFormatState() {
        return uFormatState;
    }

    public void setuFormatState(String uFormatState) {
        this.uFormatState = uFormatState;
    }

    public String getuFormatRegtime() {
        return uFormatRegtime;
    }

    public void setuFormatRegtime(String uFormatRegtime) {
        this.uFormatRegtime = uFormatRegtime;
    }

    public long getuRegDays() {
        return uRegDays;
    }

    public void setuRegDays(long uRegDays) {
        this.uRegDays = uRegDays;
    }

    public String getuFormatEmail() {
        return uFormatEmail;
    }

    public void setuFormatEmail(String uFormatEmail) {
        this.uFormatEmail = uFormatEmail;
    }

    public String getuFormatHeight() {
        return uFormatHeight;
    }

    public void setuFormatHeight(String uFormatHeight) {
        this.uFormatHeight = uFormatHeight;
    }

    public String getuFormatWeight() {
        return uFormatWeight;
    }

    public void setuFormatWeight(String uFormatWeight) {
        this.uFormatWeight = uFormatWeight;
    }

    public String getuForamtType() {
        return uForamtType;
    }

    public void setuForamtType(String uForamtType) {
        this.uForamtType = uForamtType;
    }

    public String getuFormatRealnameIdentifyState() {
        return uFormatRealnameIdentifyState;
    }

    public void setuFormatRealnameIdentifyState(
            String uFormatRealnameIdentifyState) {
        this.uFormatRealnameIdentifyState = uFormatRealnameIdentifyState;
    }

    public String getuArticleCount() {
        return uArticleCount;
    }

    public void setuArticleCount(String uArticleCount) {
        this.uArticleCount = uArticleCount;
    }

    @Override
    public String toString() {
        return "UserEntity [uId=" + uId + ", uPhone=" + uPhone + ", uPasswd="
                + uPasswd + ", uPic=" + uPic + ", uRegtime=" + uRegtime
                + ", uNickname=" + uNickname + ", uSex=" + uSex + ", uHeight="
                + uHeight + ", uWeight=" + uWeight + ", uBirthday=" + uBirthday
                + ", uDomicile=" + uDomicile + ", uPointx=" + uPointx
                + ", uPointy=" + uPointy + ", uEmail=" + uEmail + ", uWeixin="
                + uWeixin + ", uWeibo=" + uWeibo + ", uQq=" + uQq
                + ", uIntroduce=" + uIntroduce + ", uIsboard=" + uIsboard
                + ", uRealnameIdentifyState=" + uRealnameIdentifyState
                + ", uTxToken=" + uTxToken + ", uType=" + uType
                + ", uCheckUid=" + uCheckUid + ", uSignState=" + uSignState
                + ", uState=" + uState + ", uToken=" + uToken + ", uIsvip="
                + uIsvip + ", uFormatState=" + uFormatState
                + ", uFormatRegtime=" + uFormatRegtime + ", uRegDays="
                + uRegDays + ", uFormatEmail=" + uFormatEmail
                + ", uFormatHeight=" + uFormatHeight + ", uFormatWeight="
                + uFormatWeight + ", uForamtType=" + uForamtType
                + ", uFormatRealnameIdentifyState="
                + uFormatRealnameIdentifyState + ", uArticleCount="
                + uArticleCount + "]";
    }

}
