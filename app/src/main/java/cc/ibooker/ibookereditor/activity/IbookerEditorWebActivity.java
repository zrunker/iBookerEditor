package cc.ibooker.ibookereditor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditorlib.IbookerEditorWebView;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 书客编辑器Web页面
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class IbookerEditorWebActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private IbookerEditorWebView preWebView;

    // 网络状态、数据加载状态
    private LinearLayout stateLayout;
    private ImageView stateImg;
    private TextView stateTv;

    private long aId;// 标记文章ID
    private String title;// 标记文章主题

    private Subscriber<ResultData<ArticleUserData>> getArticleUserDataByIdSubscriber;
    private CompositeSubscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibookereditor_preview);

        aId = getIntent().getLongExtra("aId", 0);
        title = getIntent().getStringExtra("title");

        // 初始化
        init();

        swipeRefreshLayout.autoRefresh();
        getArticleUserDataById();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getArticleUserDataByIdSubscriber != null)
            getArticleUserDataByIdSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
        preWebView.destroy();
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        TextView titleTv = findViewById(R.id.tv_title);
        preWebView = findViewById(R.id.ibookerEditorPreView);
        preWebView.setIbookerEditorImgPreviewListener(new IbookerEditorWebView.IbookerEditorImgPreviewListener() {
            @Override
            public void onIbookerEditorImgPreview(String currentPath, int position, ArrayList<String> imgAllPathList) {
                if (ClickUtil.isFastClick()) return;
                Intent intent = new Intent(IbookerEditorWebActivity.this, ImgVPagerActivity.class);
                intent.putExtra("currentPath", currentPath);
                intent.putExtra("position", position);
                intent.putExtra("imgAllPathList", imgAllPathList);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        // 赋值
        titleTv.setText(title);

        // 状态信息
        stateLayout = findViewById(R.id.layout_state);
        stateImg = stateLayout.findViewById(R.id.img_state);
        stateTv = stateLayout.findViewById(R.id.tv_state_tip);
        TextView reloadTv = stateLayout.findViewById(R.id.tv_reload);
        reloadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick()) return;
                updateStateLayout(false, -1, null);
                swipeRefreshLayout.autoRefresh();
                onRefresh();
            }
        });
        updateStateLayout(false, -1, null);
    }

    // 更新状态布局
    private void updateStateLayout(boolean isShow, int state, String stateTip) {
        if (isShow) {
            stateLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            switch (state) {
                case 1:// 无网络
                    stateImg.setImageResource(R.drawable.img_load_error);
                    stateTv.setText(getResources().getString(R.string.nonet_tip));
                    break;
                case 2:// 异常
                    stateImg.setImageResource(R.drawable.img_load_error);
                    stateTv.setText(stateTip);
                    break;
                case 3:// 失败
                    stateImg.setImageResource(R.drawable.img_load_failed);
                    stateTv.setText(stateTip);
                    break;
                case 4:// 数据为空
                    stateImg.setImageResource(R.drawable.img_load_empty);
                    stateTv.setText(getResources().getString(R.string.no_data));
                    break;
                case 0:// 成功
                    stateImg.setImageResource(R.drawable.img_load_success);
                    stateTv.setText(stateTip);
                    break;
            }
        } else {
            stateLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
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
        getArticleUserDataById();
    }

    /**
     * 根据文章ID文章详情
     */
    private void getArticleUserDataById() {
        if (NetworkUtil.isNetworkConnected(this)) {
            getArticleUserDataByIdSubscriber = new Subscriber<ResultData<ArticleUserData>>() {
                @Override
                public void onCompleted() {
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(IbookerEditorWebActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onNext(ResultData<ArticleUserData> articleUserDataResultData) {
                    if (articleUserDataResultData.getResultCode() == 0) {// 成功
                        if (articleUserDataResultData.getData() == null) {
                            updateStateLayout(true, 4, null);
                        } else {
                            preWebView.ibookerHtmlCompile(articleUserDataResultData.getData().getaHtml());

                            updateStateLayout(false, -1, null);
                        }
                    } else {// 失败
                        updateStateLayout(true, 3, articleUserDataResultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().getArticleUserDataById(getArticleUserDataByIdSubscriber, aId);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(getArticleUserDataByIdSubscriber);
        } else {// 无网络
            updateStateLayout(true, 1, null);
        }
    }
}
