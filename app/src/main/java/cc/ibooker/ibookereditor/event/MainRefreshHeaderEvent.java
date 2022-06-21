package cc.ibooker.ibookereditor.event;

/**
 * 首页用户信息刷新事件
 */
public class MainRefreshHeaderEvent {
    private boolean isReflash;

    public MainRefreshHeaderEvent(boolean isReflash) {
        this.isReflash = isReflash;
    }

    public boolean isReflash() {
        return isReflash;
    }

    public void setReflash(boolean reflash) {
        isReflash = reflash;
    }
}
