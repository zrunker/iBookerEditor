package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditorlib.IbookerEditorView;

/**
 * 编辑文章
 *
 * Created by 邹峰立 on 2018/3/27 0027.
 */
public class EditArticleActivity extends BaseActivity {
    private IbookerEditorView ibookerEditerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }
}
