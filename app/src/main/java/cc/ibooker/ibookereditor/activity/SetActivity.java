package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;

/**
 * 设置
 *
 * Created by 邹峰立 on 2018/3/28.
 */
public class SetActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        init();
    }

    // 初始化方法
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
    }

    // 点击事件监听
    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;
        switch (view.getId()) {
            case R.id.img_back:// 返回
                finish();
                break;
        }
    }
}
