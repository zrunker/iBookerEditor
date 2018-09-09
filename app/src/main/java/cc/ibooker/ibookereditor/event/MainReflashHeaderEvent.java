package cc.ibooker.ibookereditor.event;

/**
 * 首页用户信息刷新事件
 */
public class MainReflashHeaderEvent {
    private boolean isReflash;

    public MainReflashHeaderEvent(boolean isReflash) {
        this.isReflash = isReflash;
    }

    public boolean isReflash() {
        return isReflash;
    }

    public void setReflash(boolean reflash) {
        isReflash = reflash;
    }
}
