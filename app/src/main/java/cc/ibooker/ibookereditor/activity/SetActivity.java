package cc.ibooker.ibookereditor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.AppUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;

/**
 * 设置
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class SetActivity extends BaseActivity implements View.OnClickListener {
    private TextView cacheTv;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null)
            mExecutorService.shutdownNow();
        if (myHandler != null) {
            myHandler.mActivity.clear();
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
    }

    // 初始化方法
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        TextView versionTv = findViewById(R.id.tv_version);
        versionTv.setText("V" + AppUtil.getVersion(this));
        versionTv.setOnClickListener(this);

        cacheTv = findViewById(R.id.tv_cache);
        cacheTv.setOnClickListener(this);
        // 获取当前SD/缓存大小赋值-子线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long cache = FileUtil.getTotalCacheSize(getApplicationContext());
                String fileSize = FileUtil.formatFileSize(cache);
                if (!TextUtils.isEmpty(fileSize)) {
                    Message message = new Message();
                    message.obj = fileSize;
                    message.what = 5;
                    myHandler.sendMessage(message);
                }
            }
        });
        if (mExecutorService == null || mExecutorService.isShutdown())
            mExecutorService = Executors.newCachedThreadPool();
        mExecutorService.execute(thread);
        TextView userProtocolTv = findViewById(R.id.tv_user_protocol);
        userProtocolTv.setOnClickListener(this);
        TextView enjoyUsTv = findViewById(R.id.tv_enjoy_us);
        enjoyUsTv.setOnClickListener(this);
    }

    // 点击事件监听
    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;
        switch (view.getId()) {
            case R.id.img_back:// 返回
                finish();
                break;
            case R.id.tv_version:// 检测版本

                break;
            case R.id.tv_cache:// 清空缓存-内部缓存和外部缓存
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (FileUtil.getTotalCacheSize(getApplicationContext()) > 0) {
                                FileUtil.clearAllCache(getApplicationContext());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myHandler.sendEmptyMessage(5);
                    }
                });
                if (mExecutorService == null || mExecutorService.isShutdown())
                    mExecutorService = Executors.newCachedThreadPool();
                mExecutorService.execute(thread);
                break;
            case R.id.tv_user_protocol:// 用户协议
                Intent intentUserProtocol = new Intent(this, WebActivity.class);
                intentUserProtocol.putExtra("webUrl", "http://ibooker.cc/article/2/detail");
                startActivity(intentUserProtocol);
                break;
            case R.id.tv_enjoy_us:// 加入我们
                Intent intentEnjoyUs = new Intent(this, WebActivity.class);
                intentEnjoyUs.putExtra("webUrl", "http://ibooker.cc/article/160/detail");
                startActivity(intentEnjoyUs);
                break;
        }
    }

    /**
     * 通过handler执行主线程
     */
    private MyHandler myHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        // 定义一个对象用来引用Activity中的方法
        private final WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SetActivity currentActivity = (SetActivity) mActivity.get();
            if (msg.what == 5) {// 修改cacheTv
                currentActivity.cacheTv.setText("");
                if (msg.obj != null) {
                    String fileSize = msg.obj.toString();
                    if (TextUtils.isEmpty(fileSize) || !"0B".equals(fileSize.toUpperCase()))
                        currentActivity.cacheTv.setText(fileSize);
                }
            }
        }
    }

    /**
     * 保存数据
     **/
    private void saveSharedPreferences(Context context, String name, int mode, String key, Object obj) {
        if (mode != Context.MODE_PRIVATE && mode != Context.MODE_APPEND && mode != MODE_MULTI_PROCESS)
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (obj instanceof String)
            editor.putString(key, obj.toString());
        if (obj instanceof Boolean)
            editor.putBoolean(key, (Boolean) obj);
        if (obj instanceof Integer)
            editor.putInt(key, (Integer) obj);
        if (obj instanceof Float)
            editor.putFloat(key, (Float) obj);
        if (obj instanceof Long)
            editor.putLong(key, (Long) obj);

        editor.apply();
    }
}
