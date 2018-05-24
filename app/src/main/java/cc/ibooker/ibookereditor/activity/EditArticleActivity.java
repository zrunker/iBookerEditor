package cc.ibooker.ibookereditor.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.dto.FileInfoBean;
import cc.ibooker.ibookereditor.event.SaveArticleSuccessEvent;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditorlib.IbookerEditorEditView;
import cc.ibooker.ibookereditorlib.IbookerEditorTopView;
import cc.ibooker.ibookereditorlib.IbookerEditorView;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PERMISSIONS_REQUEST_OPER_FILE;
import static cc.ibooker.ibookereditorlib.IbookerEditorEnum.TOOLVIEW_TAG.IBTN_ABOUT;

/**
 * 编辑文章
 * <p>
 * Created by 邹峰立 on 2018/3/27 0027.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class EditArticleActivity extends BaseActivity implements IbookerEditorTopView.OnTopClickListener {
    private long currentStamp = System.currentTimeMillis();
    private String currentFileName = currentStamp + ".md";
    private String currentFilePath = FileUtil.LOCALFILE_PATH + currentFileName;
    private File currentFile;

    private SQLiteDao sqLiteDao;
    private FileInfoBean fileInfoBean;
    private int _id;
    private String preContent;

    private IbookerEditorView ibookerEditerView;

    // 线程池保存文件
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean lock = false;
    private Handler titleHandler = new Handler(), contentHandler = new Handler(), myHandler = new MyHandler(this);

    // 权限组
    private String[] needPermissions = new String[]{
            // SDK在Android 6.0+需要进行运行检测的权限如下：
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // 申请权限
        if (!hasPermission(needPermissions))
            requestPermission(PERMISSIONS_REQUEST_OPER_FILE, needPermissions);

        // 初始化
        init();

        if (fileInfoBean == null) {
            fileInfoBean = new FileInfoBean();
            fileInfoBean.setFileCreateTime(currentStamp);
            fileInfoBean.setFilePath(currentFilePath);
        }

        // 获取传递数据
        int id = getIntent().getIntExtra("_id", 0);
        long createTime = getIntent().getLongExtra("createTime", 0);
        String title = getIntent().getStringExtra("title");
        final String filePath = getIntent().getStringExtra("filePath");
        if (id > 0) {
            _id = id;
            fileInfoBean.setId(id);
        }
        if (createTime > 0) {
            currentStamp = createTime;
            fileInfoBean.setFileCreateTime(createTime);
        }
        // 赋值
        if (!TextUtils.isEmpty(filePath)) {
            currentFilePath = filePath;
            fileInfoBean.setFilePath(filePath);
            currentFile = new File(filePath);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String content = readSdData(currentFile);
                    if (!TextUtils.isEmpty(content)) {
                        sendFirstMessage(content);
                    }
                }
            }).start();
        }
        if (!TextUtils.isEmpty(title)) {
            fileInfoBean.setFileName(title);
            ibookerEditerView.setIEEditViewIbookerTitleEdText(title);
            sendFirstMessage(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (titleHandler != null)
            titleHandler.removeCallbacksAndMessages(null);
        if (contentHandler != null)
            contentHandler.removeCallbacksAndMessages(null);
        if (myHandler != null)
            myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void finish() {
        EventBus.getDefault().postSticky(new SaveArticleSuccessEvent(true, _id, fileInfoBean));
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null)
            executorService.shutdownNow();
    }

    // 初始化
    private void init() {
        sqLiteDao = new SQLiteDaoImpl(this);

        ibookerEditerView = findViewById(R.id.ibookereditorview);
        ibookerEditerView.setOnIbookerTitleEdTextChangedListener(new IbookerEditorEditView.OnIbookerTitleEdTextChangedListener() {
            // 设置主题改变监听
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                final String fileName = charSequence.toString().trim();
                if (!TextUtils.isEmpty(fileName)) {
                    // 创建文件
                    if (currentFile == null || !currentFile.exists()) {
                        // 创建目录
                        FileUtil.createSDDirs(FileUtil.LOCALFILE_PATH);
                        // 创建文件
                        currentFile = FileUtil.createFile(currentFilePath);
                    }
                    if (currentFile != null && currentFile.exists()) {
                        // 0.5s更新一次
                        if (titleHandler == null)
                            titleHandler = new Handler();
                        titleHandler.removeCallbacksAndMessages(null);
                        titleHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 更新数据库
                                fileInfoBean.setFileName(fileName);
                                if (_id <= 0)
                                    _id = sqLiteDao.insertLocalFile2(fileInfoBean);
                                else
                                    sqLiteDao.updateLocalFileById(fileInfoBean, _id);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ibookerEditerView.setOnIbookerEdTextChangedListener(new IbookerEditorEditView.OnIbookerEdTextChangedListener() {
            // 设置内容改变监听
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && !charSequence.equals(preContent)) {
                    if (!lock) {
                        lock = true;
                        // 创建文件
                        if (currentFile == null || !currentFile.exists()) {
                            // 创建目录
                            FileUtil.createSDDirs(FileUtil.LOCALFILE_PATH);
                            // 创建文件
                            currentFile = FileUtil.createFile(currentFilePath);
                        }

                        if (currentFile != null && currentFile.exists()) {
                            // 2s更新一次
                            if (contentHandler == null)
                                contentHandler = new Handler();
                            contentHandler.removeCallbacksAndMessages(null);
                            contentHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 修改文件内容
                                    // 开启子线程保存
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean bool = writeSdData(charSequence.toString(), currentFile);
                                            if (bool) preContent = charSequence.toString();
                                            lock = false;
                                        }
                                    });
                                    if (executorService == null || executorService.isShutdown())
                                        executorService = Executors.newSingleThreadExecutor();
                                    executorService.execute(thread);
                                }
                            }, 2000);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // 设置书客编辑器顶部按钮点击事件
    @Override
    public void onTopClick(Object tag) {
        if (ClickUtil.isFastClick()) return;
        if (tag.equals(IBTN_ABOUT)) {// 关于 - 改成保存
            Toast.makeText(EditArticleActivity.this, "保存1", Toast.LENGTH_SHORT).show();
        }
    }

    // 请求权限结果监听
    @Override
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        super.doRequestPermissionsResult(requestCode, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_OPER_FILE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                finish();
        }
    }

    /**
     * 写入SD卡文件 - 子线程
     *
     * @param content 写入文件内容
     * @param file    待写入文件
     * @return 是否写入成功
     */
    private boolean writeSdData(String content, File file) {
        boolean bool = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            bool = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bool;
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
     * 发送消息
     *
     * @param content 显示文本
     */
    private void sendFirstMessage(String content) {
        if (myHandler == null)
            myHandler = new MyHandler(this);
        Message message = Message.obtain();
        message.obj = content;
        myHandler.sendMessage(message);
    }

    /**
     * 定义一个Handler处理UI
     */
    static class MyHandler extends Handler {
        WeakReference<EditArticleActivity> mWeakRef;

        MyHandler(EditArticleActivity activity) {
            mWeakRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final EditArticleActivity currentActivity = mWeakRef.get();
            if (msg.obj != null) {
                String content = (String) msg.obj;
                currentActivity.ibookerEditerView.setIEEditViewIbookerEdText(content);
            }
            // 切换预览
            currentActivity.ibookerEditerView.getIbookerEditorTopView().getPreviewIBtn().callOnClick();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentActivity.ibookerEditerView.changeVpUpdateIbookerEditorTopView(1);
                }
            }, 100);
        }
    }
}
