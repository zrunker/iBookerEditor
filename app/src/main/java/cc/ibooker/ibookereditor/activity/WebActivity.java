package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.view.ZWebView;
import cc.ibooker.ibookereditor.view.ZWebviewListener;

/**
 * @program: iBookerEditor
 * @description:
 * @author: zoufengli01
 * @create: 2022/6/24 14:18
 **/
public class WebActivity extends BaseActivity {
    private TextView tvTitle;
    private ProgressBar progressBarH;
    private ZWebView zWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initView();

        String webUrl = getIntent().getStringExtra("webUrl");
        zWebview.loadUrl(webUrl);
    }

    private void initView() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                finish();
            }
        });
        tvTitle = findViewById(R.id.tv_title);
        progressBarH = findViewById(R.id.progress_horizontal);
        progressBarH.setProgress(100);
        zWebview = findViewById(R.id.zwebview);
        zWebview.setOnZWebviewListener(new ZWebviewListener() {
            @Override
            public void onReceivedTitle(View view, String title) {
                super.onReceivedTitle(view, title);
                tvTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBarH.setProgress(newProgress);
                if (newProgress >= 100) {
                    progressBarH.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zWebview.destroy();
    }
}
