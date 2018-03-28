package cc.ibooker.ibookereditor.event;

/**
 * 保存文章事件
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class SaveArticleSuccessEvent {
    private boolean isflashData;

    public SaveArticleSuccessEvent(boolean isflashData) {
        this.isflashData = isflashData;
    }

    public boolean isIsflashData() {
        return isflashData;
    }

    public void setIsflashData(boolean isflashData) {
        this.isflashData = isflashData;
    }

    @Override
    public String toString() {
        return "SaveArticleSuccessEvent{" +
                "isflashData=" + isflashData +
                '}';
    }
}
