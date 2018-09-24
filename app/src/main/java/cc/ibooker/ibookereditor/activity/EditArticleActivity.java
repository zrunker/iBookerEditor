package cc.ibooker.ibookereditor.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditorlib.IbookerEditorEditView;
import cc.ibooker.ibookereditorlib.IbookerEditorTopView;
import cc.ibooker.ibookereditorlib.IbookerEditorView;
import cc.ibooker.ibookereditorlib.IbookerEditorWebView;
import cc.ibooker.zdialoglib.ProgressDialog;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PERMISSIONS_REQUEST_OPER_FILE;
import static cc.ibooker.ibookereditorlib.IbookerEditorEnum.TOOLVIEW_TAG.IBTN_ELSE;

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
    private String preTitle, preContent;

    private IbookerEditorView ibookerEditerView;

    // 线程池保存文件
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean titleLock = false, contentLock = false;
    private Handler titleHandler = new Handler(),
            contentHandler = new Handler(),
            myHandler = new MyHandler(this);

    // 权限组
    private String[] needPermissions = new String[]{
            // SDK在Android 6.0+需要进行运行检测的权限如下：
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int FINISH_ACTIVITY_CODE = 111;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setStatusBarColor(R.color.colorEFEFEF);

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
        if (!TextUtils.isEmpty(filePath)) {
            currentFilePath = filePath;
            fileInfoBean.setFilePath(filePath);
            currentFile = new File(filePath);
        }
        if (!TextUtils.isEmpty(title)) {
            preTitle = title;
            fileInfoBean.setFileName(title);
            ibookerEditerView.setIEEditViewIbookerTitleEdText(title);
            ibookerEditerView.getIbookerEditorVpView().getEditView().getIbookerTitleEd().setSelection(title.length());
        }
        // 读取文件内容并赋值
        if (currentFile != null && currentFile.exists() && currentFile.isFile()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String content = readSdData(currentFile);
                    preContent = content;
                    sendFirstMessage(content);
                }
            }).start();
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
        renameFile();
        EventBus.getDefault().postSticky(new SaveArticleSuccessEvent(true, _id, fileInfoBean));
        super.finish();
    }

    @Override
    protected void onDestroy() {
        closeProgressDialog();
        super.onDestroy();
        if (titleHandler != null)
            titleHandler = null;
        if (contentHandler != null)
            contentHandler = null;
        if (myHandler != null)
            myHandler = null;
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
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String title = charSequence.toString().trim();
                if (!TextUtils.isEmpty(title) && !title.equals(preTitle)) {
                    if (!titleLock) {
                        titleLock = true;
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
                                    preTitle = title;
                                    fileInfoBean.setFileName(title);
                                    if (_id <= 0)// 当前文件不存在
                                        _id = sqLiteDao.insertLocalFile2(fileInfoBean);
                                    else
                                        sqLiteDao.updateLocalFileById(fileInfoBean, _id);

                                    titleLock = false;
                                }
                            }, 500);
                        }
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
                    if (!contentLock) {
                        contentLock = true;
                        // 创建文件
                        if (currentFile == null || !currentFile.exists()) {
                            // 创建目录
                            FileUtil.createSDDirs(FileUtil.LOCALFILE_PATH);
                            // 创建文件
                            currentFile = FileUtil.createFile(currentFilePath);
                        }
                        if (currentFile != null && currentFile.exists()) {
                            // 1s更新一次
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
                                            if (bool)
                                                preContent = charSequence.toString();
                                            contentLock = false;
                                        }
                                    });
                                    if (executorService == null || executorService.isShutdown())
                                        executorService = Executors.newSingleThreadExecutor();
                                    executorService.execute(thread);
                                }
                            }, 1000);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ibookerEditerView.setIEEditViewBackgroundColor(Color.parseColor("#DDDDDD"))
                .setIbookerEditorImgPreviewListener(new IbookerEditorWebView.IbookerEditorImgPreviewListener() {
                    @Override
                    public void onIbookerEditorImgPreview(String currentPath, int position, ArrayList<String> imgAllPathList) {
                        Intent intent = new Intent(EditArticleActivity.this, ImgVPagerActivity.class);
                        intent.putExtra("currentPath", currentPath);
                        intent.putExtra("position", position);
                        intent.putStringArrayListExtra("imgAllPathList", imgAllPathList);
                        startActivity(intent);
                    }
                });

        ibookerEditerView.getIbookerEditorTopView().getShareIBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.setType("text/*");
//                sendIntent.putExtra(Intent.EXTRA_TEXT, contentEditText.getText().toString());
//                startActivity(sendIntent);

                File file = ibookerEditerView.generateFile();
                sharePicture(EditArticleActivity.this, file,
                        ibookerEditerView.getIbookerEditorVpView().getEditView().getIbookerTitleEd().getText().toString().trim());
            }
        });

        ibookerEditerView.getIbookerEditorTopView().getBackImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回按钮覆写
                if (ClickUtil.isFastClick()) return;
                saveFile();
            }
        });

        ibookerEditerView.getIbookerEditorTopView().getHelpIBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 帮助按钮覆写
                if (ClickUtil.isFastClick()) return;
                Intent intentGrammer = new Intent(EditArticleActivity.this, ArticleDetailActivity.class);
                intentGrammer.putExtra("aId", 1L);
                intentGrammer.putExtra("title", "语法参考");
                startActivity(intentGrammer);
            }
        });

        ibookerEditerView.getIbookerEditorTopView().getAboutImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关于按钮覆写
                if (ClickUtil.isFastClick()) return;
                Intent intentAbout = new Intent(EditArticleActivity.this, ArticleDetailActivity.class);
                intentAbout.putExtra("aId", 182L);
                intentAbout.putExtra("title", "关于");
                startActivity(intentAbout);
            }
        });
    }

    // 设置书客编辑器顶部按钮点击事件
    @Override
    public void onTopClick(Object tag) {
        if (ClickUtil.isFastClick()) return;
        if (tag.equals(IBTN_ELSE)) {// 其他
            ToastUtil.shortToast(this, "保存1");
        }
    }

    // 请求权限结果监听
    @Override
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        super.doRequestPermissionsResult(requestCode, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_OPER_FILE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.shortToast(this, "获取读取文件权限失败！");
                finish();
            }
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
            if (msg.what == currentActivity.FINISH_ACTIVITY_CODE) {
                currentActivity.finish();
            } else {
                if (msg.obj != null) {
                    String content = (String) msg.obj;
                    currentActivity.ibookerEditerView.setIEEditViewIbookerEdText(content);
                }
                // 切换预览
                currentActivity.ibookerEditerView.getIbookerEditorVpView().setCurrentItem(1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentActivity.ibookerEditerView.changeVpUpdateIbookerEditorTopView(1);
                    }
                }, 100);
            }
        }
    }

    // 修改状态栏的颜色
    private void setStatusBarColor(int color) {
        try {
            Window window = getWindow();
            // 取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            // 需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            // 设置状态栏颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享图片
     */
    private void sharePicture(Context context, File file, String Kdescription) {
        if (file != null && file.exists() && file.isFile()) {
            Intent intent = new Intent();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, "cc.ibooker.ibookereditor.fileProvider", file);
            } else {
                uri = Uri.fromFile(file);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, Kdescription);
            context.startActivity(intent);
        }
    }

    /**
     * finish执行之前-保存文件
     */
    private void saveFile() {
        if (currentFile != null && currentFile.exists() && currentFile.isFile()) {
            // 保存最后信息
            String title = ibookerEditerView.getIbookerEditorVpView()
                    .getEditView()
                    .getIbookerTitleEd()
                    .getText()
                    .toString()
                    .trim();
            if (!TextUtils.isEmpty(title) && !title.equals(preTitle)) {
                if (_id <= 0)
                    _id = sqLiteDao.insertLocalFile2(fileInfoBean);
                else
                    sqLiteDao.updateLocalFileById(fileInfoBean, _id);
            }
            final String content = ibookerEditerView.getIbookerEditorVpView()
                    .getEditView()
                    .getIbookerEd()
                    .getText()
                    .toString();
            if (!TextUtils.isEmpty(content) && !content.equals(preContent)) {
                showProgressDialog();
                ToastUtil.shortToast(this, "文件保存中...");

                // 开启子线程保存
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean bool = writeSdData(content, currentFile);
                        if (bool)
                            preContent = content;
                        // 发送消息关闭当前界面
                        myHandler.sendEmptyMessage(FINISH_ACTIVITY_CODE);
                    }
                });
                if (executorService == null || executorService.isShutdown())
                    executorService = Executors.newSingleThreadExecutor();
                executorService.execute(thread);
                return;

                // 开启子线程保存文件
//                writeSdData(content, currentFile);
            }
        }
        finish();
    }

    /**
     * 关闭当前页面-文件重命名
     */
    public void renameFile() {
        if (currentFile != null && currentFile.exists() && currentFile.isFile()) {
            showProgressDialog();

            String fileName = currentFile.getName();
            String filePath = currentFile.getAbsolutePath();
            String title = ibookerEditerView.getIbookerEditorVpView()
                    .getEditView()
                    .getIbookerTitleEd()
                    .getText()
                    .toString()
                    .trim();
            String newFilePath = filePath.replace(fileName, title + "-" + fileName);
            boolean bool = currentFile.renameTo(new File(newFilePath));
            if (bool) {
                fileInfoBean.setFilePath(newFilePath);
                if (_id <= 0)// 当前文件不存在
                    _id = sqLiteDao.insertLocalFile2(fileInfoBean);
                else
                    sqLiteDao.updateLocalFileById(fileInfoBean, _id);
                currentFile.delete();
            }
        }
        closeProgressDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 点击手机上的返回键，返回上一层
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveFile();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 展示progressDialog
     */
    private void showProgressDialog() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.showProDialog();
    }

    /**
     * 关闭progressDialog
     */
    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.closeProDialog();
    }

}
