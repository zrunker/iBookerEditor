package cc.ibooker.ibookereditor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditor.zrecycleview.AutoSwipeRefreshLayout;
import cc.ibooker.ibookereditorlib.IbookerEditorWebView;

/**
 * 书客编辑器Web页面 - 用来监听外界点击唤醒APP
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

    private File file;
    private String title = "";// 标记文章主题
    private MyHandler myHandler = new MyHandler(this);
    private final static int FROM_WEB_EDIT_REQUST_CODE = 111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibookereditor_preview);

        String action = getIntent().getAction();
        // 将文件复制到制定目录中
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = getIntent().getData();
            String filePath = Uri.decode(uri != null ? uri.getEncodedPath() : "");
            String lowerFilePath = filePath.toLowerCase();
            if (!TextUtils.isEmpty(filePath) && (lowerFilePath.contains(".md")
                    || lowerFilePath.contains(".txt") || lowerFilePath.contains(".pdf")
                    || lowerFilePath.contains(".doc") || lowerFilePath.contains(".html")
                    || lowerFilePath.contains(".htm") || lowerFilePath.contains(".docx")
                    || lowerFilePath.contains(".epub") || lowerFilePath.contains(".xml")
                    || lowerFilePath.contains(".java") || lowerFilePath.contains(".jsp")
                    || lowerFilePath.contains(".cpp") || lowerFilePath.contains(".c")
                    || lowerFilePath.contains(".php") || lowerFilePath.contains(".js")
                    || lowerFilePath.contains(".conf") || lowerFilePath.contains(".py")
                    || lowerFilePath.contains(".mm") || lowerFilePath.contains(".oc"))) {

                file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    title = file.getName();
                    // 初始化
                    init();

                    swipeRefreshLayout.autoRefresh();
                    readFileContent(file);
                } else {
                    ToastUtil.shortToast(this, "打开文件失败！");
                }
            } else {
                ToastUtil.shortToast(this, "打开文件失败！");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myHandler != null)
            myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null)
            myHandler = null;
        preWebView.destroy();
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        ImageView editImg = findViewById(R.id.img_edit);
        editImg.setOnClickListener(this);
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
            case R.id.img_edit:// 编辑
                if (file != null && file.exists() && file.isFile()) {
                    // 进入编辑界面
                    Intent intent = new Intent(this, EditArticleActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("_id", -1);
                    intent.putExtra("filePath", file.getAbsolutePath());
                    intent.putExtra("createTime", file.lastModified());
                    startActivityForResult(intent, FROM_WEB_EDIT_REQUST_CODE);
                }
                break;
        }
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        readFileContent(file);
    }

    /**
     * 读取SD卡文件内容 - 子线程
     */
    private String readSdData(File file) {
        StringBuilder sb = new StringBuilder();
        if (file != null && file.exists()) {
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, len));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 读取文件内容
     */
    private void readFileContent(final File currentFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String content = readSdData(currentFile);
                if (myHandler == null)
                    myHandler = new MyHandler(IbookerEditorWebActivity.this);
                myHandler.removeCallbacksAndMessages(null);
                Message message = Message.obtain();
                message.obj = content;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 定义一个Handler处理UI
     */
    static class MyHandler extends Handler {
        WeakReference<IbookerEditorWebActivity> mWeakRef;

        MyHandler(IbookerEditorWebActivity activity) {
            mWeakRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            IbookerEditorWebActivity currentActivity = mWeakRef.get();
            if (msg.obj != null) {// 成功
                String content = (String) msg.obj;
                if (TextUtils.isEmpty(content)) {
                    currentActivity.updateStateLayout(true, 4, null);
                } else {
                    currentActivity.preWebView.ibookerCompile(content);
                    currentActivity.updateStateLayout(false, -1, null);
                }
            } else {// 失败
                currentActivity.updateStateLayout(true, 3, "文件解析失败！");
            }
            currentActivity.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_WEB_EDIT_REQUST_CODE) {
            swipeRefreshLayout.autoRefresh();
            readFileContent(file);
        }
    }
}
