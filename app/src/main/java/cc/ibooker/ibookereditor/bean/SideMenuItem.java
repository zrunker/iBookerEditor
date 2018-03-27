package cc.ibooker.ibookereditor.bean;

/**
 * 侧滑菜单左侧数据
 * Created by 邹峰立 on 2018/3/27.
 */
public class SideMenuItem {
    private int res;
    private String name;
    private boolean isShowView;

    public SideMenuItem(int res, String name, boolean isShowView) {
        this.res = res;
        this.name = name;
        this.isShowView = isShowView;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowView() {
        return isShowView;
    }

    public void setShowView(boolean showView) {
        isShowView = showView;
    }

    @Override
    public String toString() {
        return "SideMenuItem{" +
                "res=" + res +
                ", name='" + name + '\'' +
                ", isShowView=" + isShowView +
                '}';
    }
}
