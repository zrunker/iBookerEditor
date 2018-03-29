package cc.ibooker.ibookereditor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.SideMenuAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.bean.SideMenuItem;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.event.SaveArticleSuccessEvent;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.AppUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditor.zrecycleview.MyLinearLayoutManager;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 书客编辑器开源项目
 *
 * @author 邹峰立
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private DrawerLayout drawer;
    private TextView topTv, nameTv, phoneTv;
    private ImageButton editImgBtn;
    private ImageView picImg;
    private ListView sideListview;
    private SideMenuAdapter sideMenuAdapter;
    private ArrayList<SideMenuItem> mSideMenuDatas;

    // 内容区
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private int page = 1;

    private Subscriber<ResultData<ArrayList<ArticleUserData>>> getRecommendArticleListSubscriber;
    private CompositeSubscription mSubscription;

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
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    // 保存文章成功事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeSaveArticleSuccessEvent(SaveArticleSuccessEvent event) {

        EventBus.getDefault().removeStickyEvent(event);
    }

    // 返回按钮监听
    @Override
    public void onBackPressed() {
        if (drawer == null)
            drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
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
        recyclerView.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
                        drawer.closeDrawer(GravityCompat.START);
                        topTv.setText("本地");
                        break;
                    case 2:// 推荐
                        drawer.closeDrawer(GravityCompat.START);
                        topTv.setText("推荐");
                        swipeRefreshLayout.autoRefresh();
                        // 加载数据
                        onRefresh();
                        break;
                    case 3:// 语法参考
                        drawer.closeDrawer(GravityCompat.START);
                        Intent intentGrammer = new Intent(MainActivity.this, IbookerEditorWebActivity.class);
                        intentGrammer.putExtra("aId", 1);
                        intentGrammer.putExtra("title", "语法参考");
                        startActivity(intentGrammer);
                        break;
                    case 4:// 设置
                        drawer.closeDrawer(GravityCompat.START);
                        Intent intentSet = new Intent(MainActivity.this, SetActivity.class);
                        startActivity(intentSet);
                        break;
                    case 5:// 反馈
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case 6:// 评分
                        String mAddress = "market://details?id=" + AppUtil.getVersion(MainActivity.this);
                        Intent marketIntent = new Intent("android.intent.action.VIEW");
                        marketIntent.setData(Uri.parse(mAddress));
                        startActivity(marketIntent);
                        break;
                    case 7:// 关于
                        drawer.closeDrawer(GravityCompat.START);
                        Intent intentAbout = new Intent(MainActivity.this, IbookerEditorWebActivity.class);
                        intentAbout.putExtra("aId", 182);
                        intentAbout.putExtra("title", "关于");
                        startActivity(intentAbout);
                        break;
                }
            }
        });
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        page = 1;
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
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(ResultData<ArrayList<ArticleUserData>> arrayListResultData) {
                    Log.d("arrayListResultData", arrayListResultData.toString());
                }
            };
            HttpMethods.getInstance().getRecommendArticleList(getRecommendArticleListSubscriber, page);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(getRecommendArticleListSubscriber);
        } else {// 无网络
        }
    }
}
