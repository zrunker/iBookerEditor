package cc.ibooker.ibookereditor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.MeInfoArticleLikeAdapter;
import cc.ibooker.ibookereditor.adapter.MeInfoDialogAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.event.MeInfoArticleLikeLongClickEvent;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditor.zrecycleview.MyLinearLayoutManager;
import cc.ibooker.ibookereditor.zrecycleview.RecyclerViewScrollListener;
import cc.ibooker.zdialoglib.DiyDialog;
import cc.ibooker.zdialoglib.ProgressDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PAGE_SIZE_NEW_ARTICLE;

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

    private DiyDialog diyDialog;
    private ProgressDialog progressDialog;

    private Subscriber<ResultData<Boolean>> updateArticleAppreciateIsdeleteByIdSubscriber;
    private Subscriber<ResultData<ArrayList<ArticleAppreciateData>>> getArticleAppreciateDataListByPuid2Subscriber;
    private CompositeSubscription mSubscription;
    private final int FROM_MEINFO_TO_LOGIN_REQUEST_CDE = 112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meinfo);

        initView();

        swipeRefreshLayout.autoRefresh();
        getArticleAppreciateDataListByPuid2();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeDiyDialog();
        closeProgressDialog();
        if (getArticleAppreciateDataListByPuid2Subscriber != null)
            getArticleAppreciateDataListByPuid2Subscriber.unsubscribe();
        if (updateArticleAppreciateIsdeleteByIdSubscriber != null)
            updateArticleAppreciateIsdeleteByIdSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(MeInfoArticleLikeLongClickEvent.class);
        EventBus.getDefault().unregister(this);
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 执行列表长按事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeMeInfoArticleLikeLongClickEvent(MeInfoArticleLikeLongClickEvent event) {
        if (event != null) {
            showDiyDialog(event.getData(), event.getPosition());
            EventBus.getDefault().removeStickyEvent(event);
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

        setAdapter();
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
        if (isCanLoadMore && articleList.size() >= PAGE_SIZE_NEW_ARTICLE) {
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

        isCanLoadMore = ((articleList.size() >= PAGE_SIZE_NEW_ARTICLE) && (articleList.size() % PAGE_SIZE_NEW_ARTICLE == 0));
        updateFooterView();
    }

    // 更新RecyclerView底部
    private void updateFooterView() {
        if (ryScrollListener.isLoadingMore()) {
            footerData.setShowFooter(true);
            footerData.setShowProgressBar(true);
            footerData.setTitle(getResources().getString(R.string.load_more));
        } else {
            if (articleList == null || articleList.size() < PAGE_SIZE_NEW_ARTICLE) {
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
                    } else if (arrayListResultData.getResultCode() == 5001) {
                        Intent intent = new Intent(MeInfoActivity.this, LoginActivity.class);
                        startActivityForResult(intent, FROM_MEINFO_TO_LOGIN_REQUEST_CDE);
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_MEINFO_TO_LOGIN_REQUEST_CDE:// 登录页面返回
                    onRefresh();
                    break;
            }
        }
    }

    // 关闭TipDialog
    private void closeDiyDialog() {
        if (diyDialog != null)
            diyDialog.closeDiyDialog();
    }

    // 展示TipDialog
    private ListView listView;

    private void showDiyDialog(final ArticleAppreciateData data, int position) {
        if (diyDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_meinfo_article_like_dialog, null);
            listView = view.findViewById(R.id.listview);
            listView.setAdapter(new MeInfoDialogAdapter(this));
            diyDialog = new DiyDialog(this, view);
            diyDialog.setDiyDialogWidth(70);
        }
        if (data != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (ClickUtil.isFastClick()) return;
                    closeDiyDialog();
                    if (position == 0) {
                        Intent intent = new Intent(MeInfoActivity.this, ArticleDetailActivity.class);
                        intent.putExtra("aId", data.getAaAid());
                        intent.putExtra("title", data.getaTitle());
                        startActivity(intent);
                    } else if (position == 1) {
                        updateArticleAppreciateIsdeleteById(data.getAaId(), position);
                    }
                }
            });
            diyDialog.showDiyDialog();
        }
    }

    /**
     * 根据ID修改文章喜欢是否删除状态
     */
    private void updateArticleAppreciateIsdeleteById(long aaId, final int position) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProgressDialog();
            updateArticleAppreciateIsdeleteByIdSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {
                    closeProgressDialog();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(MeInfoActivity.this, e.getMessage());
                    closeProgressDialog();
                }

                @Override
                public void onNext(ResultData<Boolean> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        int realPosition = position - 1;
                        if (realPosition > 0 && realPosition < articleList.size())
                            articleList.remove(realPosition);
                        adapter.removeData(position);
                    } else if (resultData.getResultCode() == 5001) {
                        Intent intent = new Intent(MeInfoActivity.this, LoginActivity.class);
                        startActivityForResult(intent, FROM_MEINFO_TO_LOGIN_REQUEST_CDE);
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    } else {// 失败
                        ToastUtil.shortToast(MeInfoActivity.this, resultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().updateArticleAppreciateIsdeleteById(updateArticleAppreciateIsdeleteByIdSubscriber, aaId);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(updateArticleAppreciateIsdeleteByIdSubscriber);
        } else {// 无网络
            ToastUtil.shortToast(MeInfoActivity.this, "当前网络不给力！");
        }
    }

    // 展示进度条Dialog
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.showProDialog();
    }

    // 关闭进度条Dialog
    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.closeProDialog();
    }


}
