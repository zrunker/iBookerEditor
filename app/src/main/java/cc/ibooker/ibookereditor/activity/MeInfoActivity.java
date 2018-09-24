package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.MeInfoArticleLikeAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditor.zrecycleview.MyLinearLayoutManager;
import cc.ibooker.ibookereditor.zrecycleview.RecyclerViewScrollListener;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PAGE_SIZE_RECOMMEND_ARTICLE;

/**
 * 个人中心
 */
public class MeInfoActivity extends BaseActivity implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        RecyclerViewScrollListener.OnLoadListener {
    private RecyclerViewScrollListener ryScrollListener;
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MeInfoArticleLikeAdapter adapter;
    private ArrayList<ArticleAppreciateData> articleList = new ArrayList<>();

    private boolean isCanLoadMore = false;
    private FooterData footerData;// 底部数据
    private int page = 1;

    private Subscriber<ResultData<ArrayList<ArticleAppreciateData>>> getArticleAppreciateDataListByPuid2Subscriber;
    private CompositeSubscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meinfo);

        initView();

        swipeRefreshLayout.autoRefresh();
        getArticleAppreciateDataListByPuid2();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getArticleAppreciateDataListByPuid2Subscriber != null)
            getArticleAppreciateDataListByPuid2Subscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 初始化控件
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);

        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recycleview);
        // 若item的布局是固定的，设置这个属性可以提高性能
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ryScrollListener = new RecyclerViewScrollListener();
        ryScrollListener.setOnLoadListener(this);
        recyclerView.addOnScrollListener(ryScrollListener);

        footerData = new FooterData(false, false, getResources().getString(R.string.load_more_before));
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        ryScrollListener.setLoadingMore(false);
        page = 1;
        if (getArticleAppreciateDataListByPuid2Subscriber != null && !getArticleAppreciateDataListByPuid2Subscriber.isUnsubscribed())
            getArticleAppreciateDataListByPuid2Subscriber.unsubscribe();
        getArticleAppreciateDataListByPuid2();
    }

    // 加载更多
    @Override
    public void onLoad() {
        if (isCanLoadMore && articleList.size() >= PAGE_SIZE_RECOMMEND_ARTICLE) {
            page++;
            swipeRefreshLayout.setRefreshing(false);
            if (getArticleAppreciateDataListByPuid2Subscriber != null && !getArticleAppreciateDataListByPuid2Subscriber.isUnsubscribed())
                getArticleAppreciateDataListByPuid2Subscriber.unsubscribe();
            getArticleAppreciateDataListByPuid2();
        } else {
            ryScrollListener.setLoadingMore(false);
        }
        // 刷新底部
        updateFooterView();
    }

    // 自定义setAdapter
    private void setAdapter() {
        if (adapter == null) {
            adapter = new MeInfoArticleLikeAdapter(this, articleList, footerData);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.reflashData(articleList);
        }

        isCanLoadMore = ((articleList.size() >= PAGE_SIZE_RECOMMEND_ARTICLE) && (articleList.size() % PAGE_SIZE_RECOMMEND_ARTICLE == 0));
        updateFooterView();
    }

    // 更新RecyclerView底部
    private void updateFooterView() {
        if (ryScrollListener.isLoadingMore()) {
            footerData.setShowFooter(true);
            footerData.setShowProgressBar(true);
            footerData.setTitle(getResources().getString(R.string.load_more));
        } else {
            if (articleList == null || articleList.size() < PAGE_SIZE_RECOMMEND_ARTICLE) {
                footerData.setShowFooter(false);
                footerData.setShowProgressBar(false);
                footerData.setTitle(getResources().getString(R.string.load_more_before));
            } else if (isCanLoadMore) {
                footerData.setShowFooter(true);
                footerData.setShowProgressBar(false);
                footerData.setTitle(getResources().getString(R.string.load_more_before));
            } else {
                footerData.setShowFooter(true);
                footerData.setShowProgressBar(false);
                footerData.setTitle(getResources().getString(R.string.load_more_complete));
            }
        }
        adapter.updateFooterView(footerData);
    }

    /**
     * 根据用户ID查询文章喜欢信息列表
     */
    private void getArticleAppreciateDataListByPuid2() {
        if (NetworkUtil.isNetworkConnected(this)) {
            getArticleAppreciateDataListByPuid2Subscriber = new Subscriber<ResultData<ArrayList<ArticleAppreciateData>>>() {
                @Override
                public void onCompleted() {
                    swipeRefreshLayout.setRefreshing(false);
                    ryScrollListener.setLoadingMore(false);
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(MeInfoActivity.this, e.getMessage());
                    swipeRefreshLayout.setRefreshing(false);
                    ryScrollListener.setLoadingMore(false);
                }

                @Override
                public void onNext(ResultData<ArrayList<ArticleAppreciateData>> arrayListResultData) {
                    if (arrayListResultData.getResultCode() == 0) {// 成功
                        if (articleList == null)
                            articleList = new ArrayList<>();
                        if (page == 1)
                            articleList.clear();
                        articleList.addAll(arrayListResultData.getData());
                        setAdapter();
                    } else {// 失败
                        ToastUtil.shortToast(MeInfoActivity.this, arrayListResultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().getArticleAppreciateDataListByPuid2(getArticleAppreciateDataListByPuid2Subscriber, page);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(getArticleAppreciateDataListByPuid2Subscriber);
        } else {// 无网络
            ToastUtil.shortToast(MeInfoActivity.this, "当前网络不给力！");
        }
    }
}
