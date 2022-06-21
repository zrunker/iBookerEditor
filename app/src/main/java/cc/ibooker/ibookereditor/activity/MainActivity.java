package cc.ibooker.ibookereditor.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.commonsdk.debug.E;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.ALocalAdapter;
import cc.ibooker.ibookereditor.adapter.ANewAdapter;
import cc.ibooker.ibookereditor.adapter.LocalOperDialogLvAdapter;
import cc.ibooker.ibookereditor.adapter.SideMenuAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.bean.LocalEntity;
import cc.ibooker.ibookereditor.bean.SideMenuItem;
import cc.ibooker.ibookereditor.dto.FileInfoBean;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.event.LocalOperDialogEvent;
import cc.ibooker.ibookereditor.event.MainReflashHeaderEvent;
import cc.ibooker.ibookereditor.event.SaveArticleSuccessEvent;
import cc.ibooker.ibookereditor.event.UpdateUserInfoSuccessEvent;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ActivityUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.utils.DateUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditor.utils.UserUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditor.zglide.GlideCircleTransform;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditor.zrecycleview.MyLinearLayoutManager;
import cc.ibooker.ibookereditor.zrecycleview.RecyclerViewScrollListener;
import cc.ibooker.zdialoglib.DiyDialog;
import cc.ibooker.zdialoglib.TipDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PAGE_SIZE_NEW_ARTICLE;
import static cc.ibooker.ibookereditor.utils.ConstantUtil.PERMISSIONS_REQUEST_OPER_FILE;

/**
 * 书客编辑器开源项目
 * <p>
 * 本地文件一次性全部加载，推荐文章分页加载
 *
 * @author 邹峰立
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends BaseActivity implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        RecyclerViewScrollListener.OnLoadListener {
    private final int FROM_MAIN_TO_LOGIN_REQUEST_CDE = 111;
    private DrawerLayout drawer;
    private TextView topTv, nameTv, phoneTv;
    private ImageView picImg;
    private ImageButton editImgBtn;
    private Animation showAnimation, hiddenAnimation;
    private ListView sideListview;
    private SideMenuAdapter sideMenuAdapter;
    private ArrayList<SideMenuItem> mSideMenuDatas;

    // 内容区
    private RecyclerViewScrollListener ryScrollListener;
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ANewAdapter aNewAdapter;
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
    private int newArticlePage = 1;
    private Subscriber<ResultData<ArrayList<ArticleUserData>>> getNewArticleUserDataListSubscriber;
    private CompositeSubscription mSubscription;

    // 权限组
    private String[] needPermissions = new String[]{
            // SDK在Android 6.0+需要进行运行检测的权限如下：
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private SQLiteDao sqLiteDao;

    private DiyDialog localOperDialog, aDetailDialog;
    private TipDialog delDialog;

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

        // 获取用户信息
        reflashHeaderLayout();

        // 加载数据
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantUtil.SHAREDPREFERENCES_SET_NAME, Context.MODE_PRIVATE);
        dataRes = sharedPreferences.getInt(ConstantUtil.SHAREDPREFERENCES_MAIN_SET, 0);
        updateMainSetView();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer(GravityCompat.START, true);
        closeLocalOperDialog();
        closeDelDialog();
        closeADetailDialog();
        if (getNewArticleUserDataListSubscriber != null)
            getNewArticleUserDataListSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(SaveArticleSuccessEvent.class);
        EventBus.getDefault().removeStickyEvent(LocalOperDialogEvent.class);
        EventBus.getDefault().removeStickyEvent(MainReflashHeaderEvent.class);
        EventBus.getDefault().removeStickyEvent(UpdateUserInfoSuccessEvent.class);
        EventBus.getDefault().unregister(this);
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        swipeRefreshLayout.setRefreshing(false);
        ryScrollListener.setLoadingMore(false);
    }

    // 保存本地文章成功事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeSaveArticleSuccessEvent(SaveArticleSuccessEvent event) {
        // 保存相关数据到数据中
        SQLiteDao sqLiteDao = new SQLiteDaoImpl(this);
        sqLiteDao.updateLocalFileById(event.getFileInfoBean(), event.get_id());

        // 刷新界面-只刷新本地
        if (event.isIsflashData() && 0 == dataRes)
            onRefresh();

        EventBus.getDefault().removeStickyEvent(event);
    }

    // 展示本地文章相关操作事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeLocalOperDialogEvent(LocalOperDialogEvent event) {
        int position = event.getPosition();
        LocalEntity localEntity = event.getLocalEntity();
        showLocalOperDialog(localEntity, position);
        EventBus.getDefault().removeStickyEvent(event);
    }

    // 执行首页用户信息刷新事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeMainReflashHeaderEvent(MainReflashHeaderEvent event) {
        if (event.isReflash())
            reflashHeaderLayout();
        EventBus.getDefault().removeStickyEvent(event);
    }

    // 执行修改用户信息成功事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeUpdateUserInfoSuccessEvent(UpdateUserInfoSuccessEvent event) {
        if (event.isReflash())
            reflashHeaderLayout();
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
            case R.id.layout_side_nav_bar_header:// 个人中心
                drawer.closeDrawer(GravityCompat.START, true);
                if (!UserUtil.isLogin(this)) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, FROM_MAIN_TO_LOGIN_REQUEST_CDE);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                } else {
                    Intent intent = new Intent(this, MeInfoActivity.class);
                    startActivity(intent);
                }
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
        ryScrollListener.setOnLoadListener(this);
        recyclerView.addOnScrollListener(ryScrollListener);

        footerData = new FooterData(false, false, getResources().getString(R.string.load_more_before));

        // 侧边栏相关信息
        LinearLayout headerLayout = findViewById(R.id.layout_side_nav_bar_header);
        headerLayout.setOnClickListener(this);
        picImg = findViewById(R.id.img_pic);
        nameTv = findViewById(R.id.tv_name);
        phoneTv = findViewById(R.id.tv_phone);

        sideListview = findViewById(R.id.listview);
        sideListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ClickUtil.isFastClick()) return;
                switch (position) {
                    case 1:// 笔记
                        drawer.closeDrawer(GravityCompat.START, true);
                        dataRes = 0;
                        updateMainSetView();
                        break;
                    case 2:// 阅读
                        drawer.closeDrawer(GravityCompat.START, true);
                        dataRes = 1;
                        updateMainSetView();
                        break;
                    case 3:// 语法参考
                        drawer.closeDrawer(GravityCompat.START, true);
//                        Intent intentGrammer = new Intent(MainActivity.this, IbookerEditorWebActivity.class);
//                        intentGrammer.putExtra("aId", 1L);
//                        intentGrammer.putExtra("title", "语法参考");
//                        startActivity(intentGrammer);

                        Intent intentGrammer = new Intent(MainActivity.this, ArticleDetailActivity.class);
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
//                        String mAddress = "market://details?id=" + AppUtil.getVersion(MainActivity.this);
//                        Intent marketIntent = new Intent("android.intent.action.VIEW");
//                        marketIntent.setData(Uri.parse(mAddress));
//                        startActivity(marketIntent);

                        String mAddress = "market://details?id=" + getPackageName();
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse(mAddress));
                        if (marketIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(marketIntent);
                        } else {
                            // 要调起的应用不存在时的处理
                            ToastUtil.shortToast(MainActivity.this, "没有可以打开的应用市场！");
                        }
                        break;
                    case 7:// 关于
                        drawer.closeDrawer(GravityCompat.START, true);
//                        Intent intentAbout = new Intent(MainActivity.this, IbookerEditorWebActivity.class);
//                        intentAbout.putExtra("aId", 182L);
//                        intentAbout.putExtra("title", "关于");
//                        startActivity(intentAbout);

                        Intent intentAbout = new Intent(MainActivity.this, ArticleDetailActivity.class);
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

        // 初始化sqLiteDao
        if (sqLiteDao == null)
            sqLiteDao = new SQLiteDaoImpl(this);
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        ryScrollListener.setLoadingMore(false);
        if (0 == dataRes) {// 加载本地数据
            getLocalArticleList();
        } else if (1 == dataRes) {// 加载推荐数据
            newArticlePage = 1;
            if (getNewArticleUserDataListSubscriber != null && !getNewArticleUserDataListSubscriber.isUnsubscribed())
                getNewArticleUserDataListSubscriber.unsubscribe();
            getNewArticleUserDataList();
        }
    }

    // 加载更多
    @Override
    public void onLoad() {
        if (0 == dataRes) {// 加载本地数据
            ryScrollListener.setLoadingMore(false);
        } else if (1 == dataRes) {// 加载推荐数据
            if (isCanLoadMore && articleUserDataList.size() >= PAGE_SIZE_NEW_ARTICLE) {
                newArticlePage++;
                swipeRefreshLayout.setRefreshing(false);
                if (getNewArticleUserDataListSubscriber != null && !getNewArticleUserDataListSubscriber.isUnsubscribed())
                    getNewArticleUserDataListSubscriber.unsubscribe();
                getNewArticleUserDataList();
            } else {
                ryScrollListener.setLoadingMore(false);
            }
            // 刷新底部
            updateFooterView();
        }
    }

    /**
     * 修改首页设置界面
     */
    private void updateMainSetView() {
        if (dataRes == 0) {// 笔记
            topTv.setText(getString(R.string.notes_tip));
            setaLocalAdapter();
            // 加载数据
            swipeRefreshLayout.autoRefresh();
            onRefresh();
            updateEditImgBtnVisibility();
        } else {// 阅读
            topTv.setText(getString(R.string.reading_tip));
            setaNewAdapter();
            // 加载数据
            swipeRefreshLayout.autoRefresh();
            onRefresh();
            updateEditImgBtnVisibility();
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
        if (aLocalAdapter == null)
            aLocalAdapter = new ALocalAdapter(this, localEntities);
        else
            aLocalAdapter.reflashData(localEntities);

        if (recyclerView.getAdapter() != aLocalAdapter)
            recyclerView.setAdapter(aLocalAdapter);
    }

    // 刷新推荐文章列表
    private void setaNewAdapter() {
        if (aNewAdapter == null)
            aNewAdapter = new ANewAdapter(this, articleUserDataList, footerData);
        else
            aNewAdapter.reflashData(articleUserDataList);

        if (recyclerView.getAdapter() != aNewAdapter)
            recyclerView.setAdapter(aNewAdapter);

        stateLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        isCanLoadMore = ((articleUserDataList.size() >= PAGE_SIZE_NEW_ARTICLE) && (articleUserDataList.size() % PAGE_SIZE_NEW_ARTICLE == 0));
        updateFooterView();
    }

    // 更新RecyclerView底部
    private void updateFooterView() {
        if (ryScrollListener.isLoadingMore()) {
            footerData.setShowFooter(true);
            footerData.setShowProgressBar(true);
            footerData.setTitle(getResources().getString(R.string.load_more));
        } else {
            if (articleUserDataList == null || articleUserDataList.size() < PAGE_SIZE_NEW_ARTICLE) {
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
        aNewAdapter.updateFooterView(footerData);
    }

    // 初始化数据
    private void initData() {
        if (mSideMenuDatas == null)
            mSideMenuDatas = new ArrayList<>();
        mSideMenuDatas.clear();
        mSideMenuDatas.add(new SideMenuItem(0, getString(R.string.util), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_location, getString(R.string.notes), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_recommend, getString(R.string.reading), true));
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
     * 获取本地文章相关信息
     */
    private void getLocalArticleList() {
        // 清空原有数据
        if (localEntities.size() > 0)
            localEntities.clear();
        // 先从数据库中获取
        if (sqLiteDao == null)
            sqLiteDao = new SQLiteDaoImpl(this);
        ArrayList<FileInfoBean> localFileList1 = sqLiteDao.selectLocalFilesByTime();
        // 再从本地文件中获取
        ArrayList<FileInfoBean> localFileList2 = FileUtil.getFileInfos(FileUtil.LOCALFILE_PATH);
        // 重置数据
        ArrayList<FileInfoBean> fileInfoBeans = new ArrayList<>(localFileList1);
        if (fileInfoBeans.size() <= 0) {
            fileInfoBeans.addAll(localFileList2);
        }
        // 合并数据
        if (localFileList2.size() > 0 && localFileList1.size() > 0) {
            for (FileInfoBean data2 : localFileList2) {// 遍历本地
                boolean isAdd = true;
                for (FileInfoBean data1 : localFileList1) {// 遍历数据库
                    // 通过地址判断 是否未同一个文件
                    if (TextUtils.isEmpty(data2.getFilePath())
                            || data2.getFilePath().equals(data1.getFilePath())) {
                        isAdd = false;
                        break;
                    }
                }
                // 防止文件重复添加
                if (isAdd) {
                    String filePath = data2.getFilePath();
                    for (FileInfoBean data3 : fileInfoBeans) {
                        if (!filePath.equals(data3.getFilePath())) {
                            // 插入数据库
                            int _id = sqLiteDao.insertLocalFile2(data2);
                            data2.setId(_id);
                            fileInfoBeans.add(data2);
                            break;
                        }
                    }
                }

            }
        }

        // 转换数据
        localEntities = localFileListToEntities(fileInfoBeans);

        // 刷新界面
        swipeRefreshLayout.setRefreshing(false);
        ryScrollListener.setLoadingMore(false);
        if (localEntities.size() <= 0)
            updateStateLayout(true, 4, null);
        else {
            updateStateLayout(false, -1, null);
            setaLocalAdapter();
        }
    }

    /**
     * 按时间顺序获取文章列表
     */
    private void getNewArticleUserDataList() {
        if (NetworkUtil.isNetworkConnected(this)) {
            getNewArticleUserDataListSubscriber = new Subscriber<ResultData<ArrayList<ArticleUserData>>>() {
                @Override
                public void onCompleted() {
                    swipeRefreshLayout.setRefreshing(false);
                    ryScrollListener.setLoadingMore(false);
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(MainActivity.this, e.getMessage());
                    swipeRefreshLayout.setRefreshing(false);
                    ryScrollListener.setLoadingMore(false);
                }

                @Override
                public void onNext(ResultData<ArrayList<ArticleUserData>> arrayListResultData) {
                    if (arrayListResultData.getResultCode() == 0) {// 成功
                        if (arrayListResultData.getData() == null) {
                            if (articleUserDataList.size() <= 0)
                                updateStateLayout(true, 4, null);
                            else {
                                footerData.setTitle("未获取到任何数据！");
                                footerData.setShowProgressBar(false);
                                footerData.setShowFooter(true);
                                aNewAdapter.updateFooterView(footerData);
                            }
                        } else {
                            if (articleUserDataList == null)
                                articleUserDataList = new ArrayList<>();
                            if (newArticlePage == 1)
                                articleUserDataList.clear();
                            articleUserDataList.addAll(arrayListResultData.getData());
                            setaNewAdapter();

                            updateStateLayout(false, -1, null);
                        }
                    } else {// 失败
                        if (articleUserDataList.size() <= 0)
                            updateStateLayout(true, 3, arrayListResultData.getResultMsg());
                        else {
                            footerData.setTitle(arrayListResultData.getResultMsg());
                            footerData.setShowProgressBar(false);
                            footerData.setShowFooter(true);
                            aNewAdapter.updateFooterView(footerData);
                        }
                    }
                }
            };
            HttpMethods.getInstance().getNewArticleUserDataList(getNewArticleUserDataListSubscriber, newArticlePage);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(getNewArticleUserDataListSubscriber);
        } else {// 无网络
            updateStateLayout(true, 1, null);
        }
    }

    // 退出应用
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN) {
            drawer.closeDrawer(GravityCompat.START, true);
            if (System.currentTimeMillis() - exitTime > 5000) {
                exitTime = System.currentTimeMillis();
                ToastUtil.shortToast(getApplicationContext(), "再按一次退出程序");
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
                getNewArticleUserDataList();
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
            int _id = fileInfoBean.getId();
            File file = new File(fileInfoBean.getFilePath());
            if (file.exists() && file.isFile()) {// 文件存在
                data.setFile(file);
                data.setaFormatSize(FileUtil.formatFileSize(FileUtil.getFileSize(file)));
                data.setaId(_id);
                data.setaFilePath(fileInfoBean.getFilePath());
                data.setaTitle(fileInfoBean.getFileName());
                data.setaTime(fileInfoBean.getFileCreateTime());
                data.setaFormatTime(DateUtil.getFormatTimeStampToDateTime(fileInfoBean.getFileCreateTime()));
                localList.add(data);
            } else {// 文件不存在 - 删除数据库中数据
                sqLiteDao.deleteLocalFileById(_id);
            }
        }
        return localList;
    }

    /**
     * 展示本地操作Dialog
     */
    private ListView listView;

    private void showLocalOperDialog(final LocalEntity localEntity, final int position) {
        if (localOperDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_local_oper, null);
            listView = view.findViewById(R.id.listview);
            LocalOperDialogLvAdapter localOperDialogLvAdapter = new LocalOperDialogLvAdapter(this);
            listView.setAdapter(localOperDialogLvAdapter);
            localOperDialog = new DiyDialog(this, view);
            localOperDialog.setDiyDialogWidth(70);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if (ClickUtil.isFastClick()) return;
                closeLocalOperDialog();
                if (i == 0) {// 详情
                    showADetailDialog(localEntity);
                } else if (i == 1) {// 分享
                    shareFile(MainActivity.this, new File(localEntity.getaFilePath()), localEntity.getaTitle());
                } else if (i == 2) {// 删除
                    showDelDialog(localEntity.getaFilePath(), position);
                }
            }
        });
        localOperDialog.showDiyDialog();
    }

    /**
     * 关闭本地操作Dialog
     */
    private void closeLocalOperDialog() {
        if (localOperDialog != null)
            localOperDialog.closeDiyDialog();
    }

    /**
     * 展示删除Dialog
     */
    private void showDelDialog(final String filePath, final int position) {
        if (delDialog == null)
            delDialog = new TipDialog(this);
        delDialog.setEnsureColor("#FE7517")
                .setOnTipEnsureListener(new TipDialog.OnTipEnsureListener() {
                    @Override
                    public void onEnsure() {
                        if (ClickUtil.isFastClick()) return;
                        // 删除文件
                        boolean bool = FileUtil.deleteDirs(filePath);
                        if (bool) {
                            // 移除数据
                            if (position >= 0 && position < localEntities.size())
                                localEntities.remove(position);
                            // 刷新列表
                            aLocalAdapter.reflashData(localEntities);
                        } else {
                            ToastUtil.shortToast(MainActivity.this, "删除文件失败！");
                        }
                    }
                });
        delDialog.showTipDialog();
    }

    /**
     * 关闭删除Dialog
     */
    private void closeDelDialog() {
        if (delDialog != null)
            delDialog.closeTipDialog();
    }

    /**
     * 展示详情Dialog
     */
    private TextView adNameTv, adTimeTv, adSizeTv, adPathTv;

    private void showADetailDialog(LocalEntity localEntity) {
        if (aDetailDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_adetail_dialog, null);
            adNameTv = view.findViewById(R.id.tv_name);
            adTimeTv = view.findViewById(R.id.tv_time);
            adSizeTv = view.findViewById(R.id.tv_size);
            adPathTv = view.findViewById(R.id.tv_path);
            aDetailDialog = new DiyDialog(this, view);
            aDetailDialog.setDiyDialogWidth(75);
        }
        if (localEntity != null) {
            if (localEntity.getFile() != null)
                adNameTv.setText(localEntity.getFile().getName());
            adTimeTv.setText(localEntity.getaFormatTime());
            adSizeTv.setText(localEntity.getaFormatSize());
            adPathTv.setText(localEntity.getaFilePath());
        }
        aDetailDialog.showDiyDialog();
    }

    /**
     * 关闭详情Dialog
     */
    private void closeADetailDialog() {
        if (aDetailDialog != null)
            aDetailDialog.closeDiyDialog();
    }

    /**
     * 分享文件
     */
    private void shareFile(Context context, File file, String Kdescription) {
        if (file != null && file.exists() && file.isFile()) {
//            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra("subject", Kdescription);
            intent.putExtra("body", ""); // 正文
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, "cc.ibooker.ibookereditor.fileProvider", file);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                uri = Uri.fromFile(file);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }

            intent.putExtra(Intent.EXTRA_STREAM, uri); // 添加附件，附件为file对象
            if (uri.toString().endsWith(".gz")) {
                intent.setType("application/x-gzip"); // 如果是gz使用gzip的mime
            } else if (uri.toString().endsWith(".txt")) {
                intent.setType("text/plain"); // 纯文本则用text/plain的mime
            } else {
                intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发送
            }
            context.startActivity(intent);// 调用系统的mail客户端进行发送
        }
    }

    /**
     * 修改editImgBtn显示状态
     */
    private void updateEditImgBtnVisibility() {
        if (editImgBtn.getAnimation() != null)
            editImgBtn.clearAnimation();
        if (dataRes == 0) {
            if (editImgBtn.getVisibility() != View.VISIBLE) {
                if (showAnimation == null)
                    showAnimation = AnimationUtils.loadAnimation(this, R.anim.editimgbtn_show);
                editImgBtn.startAnimation(showAnimation);
                editImgBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (editImgBtn.getVisibility() != View.GONE) {
                if (hiddenAnimation == null)
                    hiddenAnimation = AnimationUtils.loadAnimation(this, R.anim.editimgbtn_hidden);
                editImgBtn.startAnimation(hiddenAnimation);
                editImgBtn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 刷新用户相关信息
     */
    private void reflashHeaderLayout() {
        if (UserUtil.isLogin(this)) {
            nameTv.setText(ConstantUtil.userDto.getUser().getuNickname());
            phoneTv.setText(ConstantUtil.userDto.getUser().getuIntroduce());
            GlideApp.with(this)
                    .load(ConstantUtil.userDto.getUser().getuPic())
                    .placeholder(R.drawable.icon_mepic)
                    .error(R.drawable.icon_mepic)
                    .transforms(new GlideCircleTransform())
                    .into(picImg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_MAIN_TO_LOGIN_REQUEST_CDE:// 登录返回-刷新界面
                    reflashHeaderLayout();
                    break;
            }
        }
    }

    @Override
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        super.doRequestPermissionsResult(requestCode, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_OPER_FILE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onRefresh();
        }
    }
}
