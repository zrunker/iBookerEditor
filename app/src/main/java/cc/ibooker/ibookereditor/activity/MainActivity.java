package cc.ibooker.ibookereditor.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.ALocalAdapter;
import cc.ibooker.ibookereditor.adapter.ARecommendAdapter;
import cc.ibooker.ibookereditor.adapter.SideMenuAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.bean.LocalEntity;
import cc.ibooker.ibookereditor.bean.SideMenuItem;
import cc.ibooker.ibookereditor.dto.FileInfoBean;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.event.SaveArticleSuccessEvent;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ActivityUtil;
import cc.ibooker.ibookereditor.utils.AppUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.DateUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditor.zrecycleview.MyLinearLayoutManager;
import cc.ibooker.ibookereditor.zrecycleview.RecyclerViewScrollListener;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PAGE_SIZE_RECOMMEND_ARTICLE;
import static cc.ibooker.ibookereditor.utils.ConstantUtil.PERMISSIONS_REQUEST_OPER_FILE;

/**
 * 书客编辑器开源项目
 *
 * @author 邹峰立
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends BaseActivity implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        RecyclerViewScrollListener.OnScrollDistanceListener,
        RecyclerViewScrollListener.OnLoadListener {
    private DrawerLayout drawer;
    private TextView topTv, nameTv, phoneTv;
    private ImageButton editImgBtn;
    private ImageView picImg;
    private ListView sideListview;
    private SideMenuAdapter sideMenuAdapter;
    private ArrayList<SideMenuItem> mSideMenuDatas;

    // 内容区
    private RecyclerViewScrollListener ryScrollListener;
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ARecommendAdapter aRecommendAdapter;
    private ArrayList<ArticleUserData> articleUserDataList = new ArrayList<>();
    private ALocalAdapter aLocalAdapter;
    private ArrayList<LocalEntity> localEntities = new ArrayList<>();

    private boolean isCanLoadMore = false;
    private FooterData footerData;// 底部数据

    // 网络状态、数据加载状态
    private LinearLayout stateLayout;
    private ImageView stateImg;
    private TextView stateTv;

    private int dataRes = 0;// 数据来源，0来自本地，1来自推荐。
    private int recommendPage = 1, localPage = 1;
    private Subscriber<ResultData<ArrayList<ArticleUserData>>> getRecommendArticleListSubscriber;
    private CompositeSubscription mSubscription;

    // 权限组
    private String[] needPermissions = new String[]{
            // SDK在Android 6.0+需要进行运行检测的权限如下：
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private SQLiteDao sqLiteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置DrawerLayout
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // 申请权限
        if (!hasPermission(needPermissions))
            requestPermission(PERMISSIONS_REQUEST_OPER_FILE, needPermissions);

        // 初始化
        init();

        // 初始化侧边栏数据
        initData();
        setSideAdapter();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getRecommendArticleListSubscriber != null)
            getRecommendArticleListSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().register(SaveArticleSuccessEvent.class);
        EventBus.getDefault().unregister(this);
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 保存文章成功事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeSaveArticleSuccessEvent(SaveArticleSuccessEvent event) {
        // 保存相关数据到数据中
        SQLiteDao sqLiteDao = new SQLiteDaoImpl(this);
        sqLiteDao.updateLocalFileById(event.getFileInfoBean(), event.get_id());

        // 刷新界面
        if (event.isIsflashData())
            onRefresh();

        EventBus.getDefault().removeStickyEvent(event);
    }

    // 返回按钮监听
    @Override
    public void onBackPressed() {
        if (drawer == null)
            drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else {
            super.onBackPressed();
        }
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.ibtn_edit:// 编辑
                Intent intent_edit = new Intent(this, EditArticleActivity.class);
                startActivity(intent_edit);
                break;
        }
    }

    // 初始化方法
    private void init() {
        topTv = findViewById(R.id.tv_top);
        editImgBtn = findViewById(R.id.ibtn_edit);
        editImgBtn.setOnClickListener(this);

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
        ryScrollListener.setOnScrollDistanceListener(this);
        ryScrollListener.setOnLoadListener(this);
        recyclerView.addOnScrollListener(ryScrollListener);

        footerData = new FooterData(false, false, getResources().getString(R.string.load_more_before));

        // 侧边栏相关信息
        picImg = findViewById(R.id.img_pic);
        nameTv = findViewById(R.id.tv_name);
        phoneTv = findViewById(R.id.tv_phone);

        sideListview = findViewById(R.id.listview);
        sideListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ClickUtil.isFastClick()) return;
                switch (position) {
                    case 1:// 本地
                        if (dataRes != 0) {
                            topTv.setText("本地");

                            // 加载数据
                            dataRes = 0;
                            swipeRefreshLayout.autoRefresh();
                            onRefresh();
                        }
                        drawer.closeDrawer(GravityCompat.START, true);
                        break;
                    case 2:// 推荐
                        if (dataRes != 1) {
                            topTv.setText("推荐");

                            // 加载数据
                            dataRes = 1;
                            swipeRefreshLayout.autoRefresh();
                            onRefresh();
                        }
                        drawer.closeDrawer(GravityCompat.START, true);
                        break;
                    case 3:// 语法参考
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentGrammer = new Intent(MainActivity.this, IbookerEditorWebActivity.class);
                        intentGrammer.putExtra("aId", 1L);
                        intentGrammer.putExtra("title", "语法参考");
                        startActivity(intentGrammer);
                        break;
                    case 4:// 设置
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentSet = new Intent(MainActivity.this, SetActivity.class);
                        startActivity(intentSet);
                        break;
                    case 5:// 反馈
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentFeedback = new Intent(MainActivity.this, FeedbackActivity.class);
                        startActivity(intentFeedback);
                        break;
                    case 6:// 评分
                        String mAddress = "market://details?id=" + AppUtil.getVersion(MainActivity.this);
                        Intent marketIntent = new Intent("android.intent.action.VIEW");
                        marketIntent.setData(Uri.parse(mAddress));
                        startActivity(marketIntent);
                        break;
                    case 7:// 关于
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentAbout = new Intent(MainActivity.this, IbookerEditorWebActivity.class);
                        intentAbout.putExtra("aId", 182L);
                        intentAbout.putExtra("title", "关于");
                        startActivity(intentAbout);
                        break;
                }
            }
        });

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

    // 下拉刷新
    @Override
    public void onRefresh() {
        ryScrollListener.setLoadingMore(false);
        if (0 == dataRes) {// 加载本地数据
            localPage = 1;
            if (sqLiteDao == null)
                sqLiteDao = new SQLiteDaoImpl(this);
            if (localEntities.size() > 0)
                localEntities.clear();
            ArrayList<FileInfoBean> localFileList = sqLiteDao.selectLocalFilesByTimePager(localPage);
            localEntities = localFileListToEntities(localFileList);
            setaLocalAdapter();
        } else if (1 == dataRes) {// 加载推荐数据
            recommendPage = 1;
            if (getRecommendArticleListSubscriber != null && !getRecommendArticleListSubscriber.isUnsubscribed())
                getRecommendArticleListSubscriber.unsubscribe();
            getRecommendArticleList();
        }
    }

    // 加载更多
    @Override
    public void onLoad() {
        if (0 == dataRes) {// 加载本地数据
            swipeRefreshLayout.setRefreshing(false);
            localPage++;
            if (sqLiteDao == null)
                sqLiteDao = new SQLiteDaoImpl(this);
            ArrayList<FileInfoBean> localFileList = sqLiteDao.selectLocalFilesByTimePager(localPage);
            localEntities.addAll(localFileListToEntities(localFileList));
            setaLocalAdapter();
        } else if (1 == dataRes) {// 加载推荐数据
            if (isCanLoadMore && articleUserDataList.size() >= PAGE_SIZE_RECOMMEND_ARTICLE) {
                recommendPage++;
                swipeRefreshLayout.setRefreshing(false);
                if (getRecommendArticleListSubscriber != null && !getRecommendArticleListSubscriber.isUnsubscribed())
                    getRecommendArticleListSubscriber.unsubscribe();
                getRecommendArticleList();
            } else {
                ryScrollListener.setLoadingMore(false);
            }
            // 刷新底部
            updateFooterView();
        }
    }

    // 滚动距离判断
    private boolean isHidden = false;
    private long preTime = 0;

    @Override
    public void onScrollDistance(int dy) {
        if (System.currentTimeMillis() - preTime > 1000)
            if (dy > 0) {// 向上滚动
                if (!isHidden) {
                    topTv.setVisibility(View.GONE);
                    isHidden = !isHidden;
                    preTime = System.currentTimeMillis();
                }
            } else {
                if (isHidden) {
                    topTv.setVisibility(View.VISIBLE);
                    isHidden = !isHidden;
                    preTime = System.currentTimeMillis();
                }
            }
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

    // 刷新本地文章列表
    private void setaLocalAdapter() {
        if (aLocalAdapter == null) {
            aLocalAdapter = new ALocalAdapter(this, localEntities);
            recyclerView.setAdapter(aLocalAdapter);
        } else {
            aLocalAdapter.reflashData(localEntities);
        }
    }

    // 刷新推荐文章列表
    private void setaRecommendAdapter() {
        if (aRecommendAdapter == null) {
            aRecommendAdapter = new ARecommendAdapter(this, articleUserDataList, footerData);
            recyclerView.setAdapter(aRecommendAdapter);
        } else {
            aRecommendAdapter.reflashData(articleUserDataList);
        }

        stateLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
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
        aRecommendAdapter.updateFooterView(footerData);
    }

    // 初始化数据
    private void initData() {
        if (mSideMenuDatas == null)
            mSideMenuDatas = new ArrayList<>();
        mSideMenuDatas.clear();
        mSideMenuDatas.add(new SideMenuItem(0, getString(R.string.article), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_location, getString(R.string.local), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_recommend, getString(R.string.recommend), true));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_question, getString(R.string.grammar_reference), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_set, getString(R.string.set), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_feedback, getString(R.string.feedback), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_star, getString(R.string.score), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_about, getString(R.string.about), false));
    }

    // 自定义setSideAdapter
    private void setSideAdapter() {
        if (sideMenuAdapter == null) {
            sideMenuAdapter = new SideMenuAdapter(this, mSideMenuDatas);
            sideListview.setAdapter(sideMenuAdapter);
        } else {
            sideMenuAdapter.reflashData(mSideMenuDatas);
        }
    }

    /**
     * 获取推荐文章相关信息
     */
    private void getRecommendArticleList() {
        if (NetworkUtil.isNetworkConnected(this)) {
            getRecommendArticleListSubscriber = new Subscriber<ResultData<ArrayList<ArticleUserData>>>() {
                @Override
                public void onCompleted() {
                    swipeRefreshLayout.setRefreshing(false);
                    ryScrollListener.setLoadingMore(false);
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    ryScrollListener.setLoadingMore(false);
                }

                @Override
                public void onNext(ResultData<ArrayList<ArticleUserData>> arrayListResultData) {
                    if (arrayListResultData.getResultCode() == 0) {// 成功
                        if (arrayListResultData.getData() == null) {
                            updateStateLayout(true, 4, null);
                        } else {
                            if (articleUserDataList == null)
                                articleUserDataList = new ArrayList<>();
                            if (recommendPage == 1)
                                articleUserDataList.clear();
                            articleUserDataList.addAll(arrayListResultData.getData());
                            setaRecommendAdapter();

                            updateStateLayout(false, -1, null);
                        }
                    } else {// 失败
                        updateStateLayout(true, 3, arrayListResultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().getRecommendArticleList(getRecommendArticleListSubscriber, recommendPage);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(getRecommendArticleListSubscriber);
        } else {// 无网络
            updateStateLayout(true, 1, null);
        }
    }

    // 退出应用
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 5000) {
                exitTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
            } else {
                ActivityUtil.getInstance().exitSystem();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 网络状态监听
    @Override
    public void onNetChange(boolean netWorkState) {
        super.onNetChange(netWorkState);
        if (netWorkState) {
            if (dataRes == 1)
                getRecommendArticleList();
        }
    }

    /**
     * 将ArrayList<FileInfoBean>转化为ArrayList<LocalEntity>
     *
     * @param list 待转化数据
     */
    private ArrayList<LocalEntity> localFileListToEntities(ArrayList<FileInfoBean> list) {
        ArrayList<LocalEntity> localList = new ArrayList<>();
        for (FileInfoBean fileInfoBean : list) {
            LocalEntity data = new LocalEntity();
            data.setaId(fileInfoBean.getId());
            data.setaFilePath(fileInfoBean.getFilePath());
            data.setaTitle(fileInfoBean.getFileName());
            data.setaTime(fileInfoBean.getFileCreateTime());
            data.setaFormatTime(DateUtil.getFormatTimeStampToDateTime(fileInfoBean.getFileCreateTime()));
            data.setaFormatSize(FileUtil.formatFileSize(fileInfoBean.getFileSize()));
            localList.add(data);
        }
        return localList;
    }
}
