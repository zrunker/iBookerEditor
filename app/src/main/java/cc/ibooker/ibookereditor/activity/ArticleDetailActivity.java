package cc.ibooker.ibookereditor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.jsevent.IbookerEditorJsCheckImgsEvent;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.view.MyWebView;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;

/**
 * 显示文章详情页面
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    private MyWebView webView;
    private WebSettings webSettings;
    private TextView titleTv;

    private TextView fontSizeAddTv, fontSizeReduceTv;

    // 网络状态、数据加载状态
    private LinearLayout stateLayout;
    private ImageView stateImg;
    private TextView stateTv;

    private long aId;
    private String title;// 标记文章主题
    private String webUrl;

    private ArrayList<String> imgPathList;// WebView所有图片地址
    private IbookerEditorJsCheckImgsEvent ibookerEditorJsCheckImgsEvent;

    private int currentFontSize;// 用来控制字体
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        aId = getIntent().getLongExtra("aId", -1);
        title = getIntent().getStringExtra("title");
        webUrl = "http://ibooker.cc/article/" + aId + "/detail";

        // 初始化
        init();
        initWebView();

        // 第三方APP调用
        receiveIntent(getIntent());
        pushArticleIntent(getIntent());

        if (aId != -1) {
            swipeRefreshLayout.autoRefresh();
            getArticleUserDataById();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        receiveIntent(intent);
        pushArticleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null)
            webView.destroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        titleTv = findViewById(R.id.tv_title);
        webView = findViewById(R.id.webView);
        webView.setOnScrollChangedCallback(new MyWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(final int dx, final int dy) {
                if (handler == null)
                    handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Math.abs(dy) > 0) {
                            fontSizeAddTv.setVisibility(View.GONE);
                            fontSizeReduceTv.setVisibility(View.GONE);
                        }
                    }
                }, 500);
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

        LinearLayout setLayout = findViewById(R.id.layout_set);
        setLayout.setOnClickListener(this);
        fontSizeAddTv = findViewById(R.id.tv_font_size_add);
        fontSizeAddTv.setOnClickListener(this);
        fontSizeReduceTv = findViewById(R.id.tv_font_size_reduce);
        fontSizeReduceTv.setOnClickListener(this);

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
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
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
                addWebViewListener();
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
                addWebViewListener();
                swipeRefreshLayout.setRefreshing(false);
                updateStateLayout(false, -1, null);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error);
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
        webSettings = webView.getSettings();
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

        // 添加js
        ibookerEditorJsCheckImgsEvent = new IbookerEditorJsCheckImgsEvent(this);
        webView.addJavascriptInterface(ibookerEditorJsCheckImgsEvent, "ibookerEditorJsCheckImgsEvent");

        // 初始化字体
        if (webSettings.getTextSize() == WebSettings.TextSize.SMALLEST) {
            currentFontSize = 1;
        } else if (webSettings.getTextSize() == WebSettings.TextSize.SMALLER) {
            currentFontSize = 2;
        } else if (webSettings.getTextSize() == WebSettings.TextSize.NORMAL) {
            currentFontSize = 3;
        } else if (webSettings.getTextSize() == WebSettings.TextSize.LARGER) {
            currentFontSize = 4;
        } else if (webSettings.getTextSize() == WebSettings.TextSize.LARGEST) {
            currentFontSize = 5;
        }
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
            case R.id.layout_set:// 设置显示或隐藏
                if (fontSizeAddTv.getVisibility() == View.VISIBLE
                        || fontSizeReduceTv.getVisibility() == View.VISIBLE) {
                    fontSizeAddTv.setVisibility(View.GONE);
                    fontSizeReduceTv.setVisibility(View.GONE);
                } else {
                    fontSizeAddTv.setVisibility(View.VISIBLE);
                    fontSizeReduceTv.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_font_size_add:// 字体增加
                currentFontSize++;
                setWebViewFontSize(currentFontSize);
                break;
            case R.id.tv_font_size_reduce:// 字体减小
                currentFontSize--;
                setWebViewFontSize(currentFontSize);
                break;
        }
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        webView.clearHistory();
        webView.clearCache(true);
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

    // 给WebView添加相关监听
    private void addWebViewListener() {
        // 动态添加图片点击事件
        webView.loadUrl("javascript:(function() {"
                + "  var objs = document.getElementsByTagName(\"img\"); "
                + "  for(var i = 0; i < objs.length; i++) {"
                + "     objs[i].onclick = function() {"
                + "          window.ibookerEditorJsCheckImgsEvent.onCheckImg(this.src);"
                + "     }"
                + "  }"
                + "})()");

        // 获取WebView中全部图片地址
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:getImgAllPaths()", new ValueCallback<String>() {

                @Override
                public void onReceiveValue(String value) {
                    // value即为所有图片地址
                    if (!TextUtils.isEmpty(value)) {
                        value = value.replace("\"", "").replace("\"", "");
                        if (!TextUtils.isEmpty(value)) {
                            if (imgPathList == null)
                                imgPathList = new ArrayList<>();
                            imgPathList.clear();
                            String[] imgPaths = value.split(";ibookerEditor;");
                            for (String imgPath : imgPaths) {
                                if (!TextUtils.isEmpty(imgPath))
                                    imgPathList.add(imgPath);
                            }
                            ibookerEditorJsCheckImgsEvent.setmImgPathList(imgPathList);
                        }
                    }
                }
            });
        }
    }

    /**
     * 接收其他APP调用
     */
    private synchronized void receiveIntent(Intent intent) {
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                aId = getIntent().getLongExtra("aId", 0);
                title = getIntent().getStringExtra("title");
                webUrl = "http://ibooker.cc/article/" + aId + "/detail";

                if (aId != -1) {
                    swipeRefreshLayout.autoRefresh();
                    getArticleUserDataById();
                }
            }
        }
    }

    /**
     * 友盟推送文章
     */
    public synchronized void pushArticleIntent(Intent intent) {
        if (intent != null) {
            Bundle bun = intent.getExtras();
            if (bun != null) {
                Set<String> keySet = bun.keySet();
                for (String key : keySet) {
                    if ("aId".equals(key))
                        aId = bun.getLong(key);
                    if ("title".equals(key))
                        title = bun.getString(key);
                }

                webUrl = "http://ibooker.cc/article/" + aId + "/detail";
                if (aId != -1) {
                    if (!TextUtils.isEmpty(title))
                        titleTv.setText(title);
                    swipeRefreshLayout.autoRefresh();
                    getArticleUserDataById();
                }
            }
        }
    }

    /**
     * 设置当前字体大小
     */
    public void setWebViewFontSize(int fontSize) {
        if (fontSize >= 1 && fontSize <= 5) {
            currentFontSize = fontSize;
            switch (fontSize) {
                case 1:
                    webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
                    break;
                case 2:
                    webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                    break;
                case 3:
                    webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                    break;
                case 4:
                    webSettings.setTextSize(WebSettings.TextSize.LARGER);
                    break;
                case 5:
                    webSettings.setTextSize(WebSettings.TextSize.LARGEST);
                    break;
            }
        }

        if (fontSize < 1)
            currentFontSize = 1;
        if (fontSize > 5)
            currentFontSize = 5;
    }
}
