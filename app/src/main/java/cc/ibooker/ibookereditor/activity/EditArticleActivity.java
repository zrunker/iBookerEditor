package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditorlib.IbookerEditorTopView;
import cc.ibooker.ibookereditorlib.IbookerEditorView;

import static cc.ibooker.ibookereditorlib.IbookerEditorEnum.TOOLVIEW_TAG.IBTN_ABOUT;

/**
 * 编辑文章
 * <p>
 * Created by 邹峰立 on 2018/3/27 0027.
 */
public class EditArticleActivity extends BaseActivity implements IbookerEditorTopView.OnTopClickListener {
    private IbookerEditorView ibookerEditerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // 初始化
        init();
    }

    // 初始化
    private void init() {
        ibookerEditerView = findViewById(R.id.ibookereditorview);
        // 重置保存按钮
        ImageView aboutImg = ibookerEditerView.getIbookerEditorTopView().getAboutImg();
        aboutImg.setImageResource(R.drawable.icon_save);
        aboutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
            }
        });
    }

    // 设置书客编辑器顶部按钮点击事件
    @Override
    public void onTopClick(Object tag) {
        if (tag.equals(IBTN_ABOUT)) {// 关于

        }
    }
}
