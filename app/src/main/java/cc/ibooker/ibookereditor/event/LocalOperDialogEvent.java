package cc.ibooker.ibookereditor.event;

import cc.ibooker.ibookereditor.bean.LocalEntity;

/**
 * 本地操作事件
 */
public class LocalOperDialogEvent {
    private LocalEntity localEntity;
    private int position;

    public LocalOperDialogEvent(LocalEntity localEntity, int position) {
        this.localEntity = localEntity;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LocalEntity getLocalEntity() {
        return localEntity;
    }

    public void setLocalEntity(LocalEntity localEntity) {
        this.localEntity = localEntity;
    }
}
