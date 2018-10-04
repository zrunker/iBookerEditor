package cc.ibooker.ibookereditor.event;

import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;

/**
 * 个人中心取消长按事件
 */
public class MeInfoArticleLikeLongClickEvent {
    private int position;
    private ArticleAppreciateData data;

    public MeInfoArticleLikeLongClickEvent(int position, ArticleAppreciateData data) {
        this.position = position;
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArticleAppreciateData getData() {
        return data;
    }

    public void setData(ArticleAppreciateData data) {
        this.data = data;
    }
}
