package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditorlib.IbookerEditorPreView;

/**
 * 书客编辑器Web页面
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class IbookerEditorWebActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView titleTv;
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private IbookerEditorPreView preWebView;

    private long aid;// 标记文章ID
    private String title;// 标记文章主题

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibookereditor);

        aid = getIntent().getLongExtra("aId", 0);
        title = getIntent().getStringExtra("title");

        // 初始化
        init();
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        titleTv = findViewById(R.id.tv_title);
        preWebView = findViewById(R.id.ibookerEditorPreView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        // 赋值
        titleTv.setText(title);
    }

    // 点击事件监听
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:// 返回
                finish();
                break;
        }
    }

    // 下拉刷新
    @Override
    public void onRefresh() {

    }
}
