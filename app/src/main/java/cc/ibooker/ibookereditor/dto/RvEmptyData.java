package cc.ibooker.ibookereditor.dto;

import android.support.annotation.NonNull;

/**
 * Rv列表空数据
 *
 * @author 邹峰立
 */
public class RvEmptyData {
    private int statue;// 状态，默认-隐藏，0-成功，-2-无网络，2-异常，3-失败，4-数据为空
    private String tip;// 提示
    private String operText;// 操作文本

    public RvEmptyData() {
        super();
    }

    public RvEmptyData(int statue) {
        this.statue = statue;
    }

    public RvEmptyData(int statue, String tip) {
        this.statue = statue;
        this.tip = tip;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getOperText() {
        return operText;
    }

    public void setOperText(String operText) {
        this.operText = operText;
    }

    @NonNull
    @Override
    public String toString() {
        return "RvEmptyData{" +
                "statue=" + statue +
                ", tip='" + tip + '\'' +
                ", operText='" + operText + '\'' +
                '}';
    }
}
