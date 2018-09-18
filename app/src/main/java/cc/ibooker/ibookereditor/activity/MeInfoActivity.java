package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.ARecommendAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.bean.UserEntity;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditor.zglide.GlideCircleTransform;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditor.zrecycleview.MyLinearLayoutManager;
import cc.ibooker.ibookereditor.zrecycleview.RecyclerViewScrollListener;

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
    private ARecommendAdapter adapter;
    private ArrayList<ArticleUserData> articleUserDataList = new ArrayList<>();

    private boolean isCanLoadMore = false;
    private FooterData footerData;// 底部数据
    private int page = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meinfo);

        initView();
    }

    // 初始化控件
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        ImageView meInfoImg = findViewById(R.id.img_mepic);
        TextView nickNameTv = findViewById(R.id.tv_nickname);
        TextView introduceTv = findViewById(R.id.tv_introduce);

//        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//        swipeRefreshLayout.setOnRefreshListener(this);
//
//        recyclerView = findViewById(R.id.recycleview);
//        // 若item的布局是固定的，设置这个属性可以提高性能
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//
//        ryScrollListener = new RecyclerViewScrollListener();
//        ryScrollListener.setOnLoadListener(this);
//        recyclerView.addOnScrollListener(ryScrollListener);
//
//        footerData = new FooterData(false, false, getResources().getString(R.string.load_more_before));

        if (ConstantUtil.userDto != null && ConstantUtil.userDto.getUser() != null) {
            UserEntity userEntity = ConstantUtil.userDto.getUser();
            GlideApp.with(this)
                    .load(userEntity.getuPic())
                    .placeholder(R.drawable.icon_mepic)
                    .error(R.drawable.icon_mepic)
                    .transforms(new GlideCircleTransform())
                    .into(meInfoImg);
            nickNameTv.setText(userEntity.getuNickname());
            introduceTv.setText(userEntity.getuIntroduce());
        }
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
//        ryScrollListener.setLoadingMore(false);
//        page = 1;
//        if (getRecommendArticleListSubscriber != null && !getRecommendArticleListSubscriber.isUnsubscribed())
//            getRecommendArticleListSubscriber.unsubscribe();
//        getRecommendArticleList();
    }

    // 加载更多
    @Override
    public void onLoad() {
//        if (isCanLoadMore && articleUserDataList.size() >= PAGE_SIZE_RECOMMEND_ARTICLE) {
//            page++;
//            swipeRefreshLayout.setRefreshing(false);
//            if (getRecommendArticleListSubscriber != null && !getRecommendArticleListSubscriber.isUnsubscribed())
//                getRecommendArticleListSubscriber.unsubscribe();
//            getRecommendArticleList();
//        } else {
//            ryScrollListener.setLoadingMore(false);
//        }
//        // 刷新底部
//        updateFooterView();
    }

    // 自定义setAdapter
    private void setAdapter() {
        if (adapter == null) {
            adapter = new ARecommendAdapter(this, articleUserDataList, footerData);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.reflashData(articleUserDataList);
        }

        isCanLoadMore = ((articleUserDataList.size() >= PAGE_SIZE_RECOMMEND_ARTICLE) && (articleUserDataList.size() % PAGE_SIZE_RECOMMEND_ARTICLE == 0));
        updateFooterView();
    }

    // 更新RecyclerView底部
    private void updateFooterView() {
        if (ryScrollListener.isLoadingMore()) {
            footerData.setShowFooter(true);
            footerData.setShowProgressBar(true);
            footerData.setTitle(getResources().getString(R.string.load_more));
        } else {
            if (articleUserDataList == null || articleUserDataList.size() < PAGE_SIZE_RECOMMEND_ARTICLE) {
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
}
