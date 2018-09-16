package cc.ibooker.ibookereditor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.AppUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditor.utils.UserUtil;

/**
 * 设置
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class SetActivity extends BaseActivity implements View.OnClickListener {
    private final int FROM_SET_TO_LOGIN_REQUEST_CDE = 1112;
    private TextView cacheTv, logoutTv, notesTv, readingTv;
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

        notesTv = findViewById(R.id.tv_notes);
        notesTv.setOnClickListener(this);
        readingTv = findViewById(R.id.tv_reading);
        readingTv.setOnClickListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantUtil.SHAREDPREFERENCES_SET_NAME, Context.MODE_PRIVATE);
        int index = sharedPreferences.getInt(ConstantUtil.SHAREDPREFERENCES_MAIN_SET, 0);
        updateMainSetView(index);

        logoutTv = findViewById(R.id.tv_logout);
        logoutTv.setOnClickListener(this);
        if (!UserUtil.isLogin(this))
            logoutTv.setText("登 录");
        ToggleButton saveToggleBtn = findViewById(R.id.togglebtn_save);
        saveToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSharedPreferences(SetActivity.this, ConstantUtil.SHAREDPREFERENCES_SET_NAME,
                        Context.MODE_PRIVATE, ConstantUtil.SHAREDPREFERENCES_ARTICLE_SAVE, isChecked);
            }
        });
        ToggleButton recommendToggleBtn = findViewById(R.id.togglebtn_recommend);
        recommendToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSharedPreferences(SetActivity.this, ConstantUtil.SHAREDPREFERENCES_SET_NAME,
                        Context.MODE_PRIVATE, ConstantUtil.SHAREDPREFERENCES_ARTICLE_RECOMMEND, isChecked);
            }
        });
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
    }

    // 点击事件监听
    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;
        switch (view.getId()) {
            case R.id.img_back:// 返回
                finish();
                break;
            case R.id.tv_notes:// 笔记
                updateMainSetView(0);
                break;
            case R.id.tv_reading:// 阅读
                updateMainSetView(1);
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
            case R.id.tv_logout:// 退出登录/登录
                UserUtil.logout(this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, FROM_SET_TO_LOGIN_REQUEST_CDE);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
        }
    }

    /**
     * 修改首页设置界面
     *
     * @param index 0-笔记 1-阅读
     */
    private void updateMainSetView(int index) {
        if (index == 0) {// 笔记
            notesTv.setBackgroundResource(R.drawable.bg_orange_shi_left_btn);
            notesTv.setTextColor(Color.parseColor("#FFFFFF"));
            readingTv.setBackgroundResource(R.drawable.bg_orange_right_btn);
            readingTv.setTextColor(Color.parseColor("#FE7517"));
            saveSharedPreferences(SetActivity.this, ConstantUtil.SHAREDPREFERENCES_SET_NAME,
                    Context.MODE_PRIVATE, ConstantUtil.SHAREDPREFERENCES_MAIN_SET, 0);
        } else {// 阅读
            notesTv.setBackgroundResource(R.drawable.bg_orange_left_btn);
            notesTv.setTextColor(Color.parseColor("#FE7517"));
            readingTv.setBackgroundResource(R.drawable.bg_orange_shi_right_btn);
            readingTv.setTextColor(Color.parseColor("#FFFFFF"));
            saveSharedPreferences(SetActivity.this, ConstantUtil.SHAREDPREFERENCES_SET_NAME,
                    Context.MODE_PRIVATE, ConstantUtil.SHAREDPREFERENCES_MAIN_SET, 1);
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
            switch (msg.what) {
                case 5:// 修改cacheTv
                    currentActivity.cacheTv.setText("");
                    if (msg.obj != null) {
                        String fileSize = msg.obj.toString();
                        if (TextUtils.isEmpty(fileSize) || !"0B".equals(fileSize.toUpperCase()))
                            currentActivity.cacheTv.setText(fileSize);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_SET_TO_LOGIN_REQUEST_CDE:// 登录页面返回
                    if (!UserUtil.isLogin(this))
                        logoutTv.setText("登 录");
                    else
                        logoutTv.setText("退出登录");
                    break;
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
