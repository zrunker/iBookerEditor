package cc.ibooker.ibookereditor.event;

/**
 * 更新用户信息成功事件
 */
public class UpdateUserInfoSuccessEvent {
    private boolean isReflash;

    public UpdateUserInfoSuccessEvent(boolean isReflash) {
        this.isReflash = isReflash;
    }

    public boolean isReflash() {
        return isReflash;
    }

    public void setReflash(boolean reflash) {
        isReflash = reflash;
    }
}
