package cc.ibooker.ibookereditor.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;

/**
 * 显示文章详情页面
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private WebView webView;
    private TextView titleTv;

    // 网络状态、数据加载状态
    private LinearLayout stateLayout;
    private ImageView stateImg;
    private TextView stateTv;

    private String title;// 标记文章主题
    private String webUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        long aId = getIntent().getLongExtra("aId", 0);
        title = getIntent().getStringExtra("title");
        webUrl = "http://ibooker.cc/article/" + aId + "/detail";

        // 初始化
        init();
        initWebView();

        swipeRefreshLayout.autoRefresh();
        getArticleUserDataById();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null)
            webView.destroy();
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        titleTv = findViewById(R.id.tv_title);
        webView = findViewById(R.id.webView);

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

    // 初始化WebView
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        // 使页面获取焦点，防止点击无响应
        webView.requestFocus();
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(false);
        // WebView默认是通过浏览器打开url，使用url在WebView中打开
        webView.setWebViewClient(new WebViewClient() {

            // 错误代码处理，一般是加载本地Html页面，或者使用TextView显示错误
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // 当网页加载出错时，加载本地错误文件
                swipeRefreshLayout.setRefreshing(false);
//                updateStateLayout(true, 3, "网页加载失败！");
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                updateStateLayout(false, -1, null);
            }
        });

        // 监听网页加载进度
        webView.setWebChromeClient(new WebChromeClient() {

            // 网页Title信息
            @Override
            public void onReceivedTitle(WebView view, String titleStr) {
                super.onReceivedTitle(view, titleStr);
                if (!title.equals(titleStr))
                    titleTv.setText(titleStr);
            }
        });

        // 设置WebView支持JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        // 支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 允许JS
        webSettings.setJavaScriptEnabled(true);
        // 支持插件
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // access Assets and resources
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(false);
        // 提高渲染优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        // 支持缩放，默认为true。
        webSettings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);

        // 设置缓存，默认不使用缓存
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 有缓存，使用缓存
    }

    // 返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                // 判断WebView是否能够返回，能-返回
                webView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            webView.loadUrl(webUrl);
        } else {// 无网络
            updateStateLayout(true, 1, null);
        }
    }
}
