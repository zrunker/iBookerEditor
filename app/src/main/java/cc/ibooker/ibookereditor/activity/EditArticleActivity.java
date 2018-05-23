package cc.ibooker.ibookereditor.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.dto.FileInfoBean;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditorlib.IbookerEditorEditView;
import cc.ibooker.ibookereditorlib.IbookerEditorTopView;
import cc.ibooker.ibookereditorlib.IbookerEditorView;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PERMISSIONS_REQUEST_OPER_FILE;
import static cc.ibooker.ibookereditorlib.IbookerEditorEnum.TOOLVIEW_TAG.IBTN_ABOUT;
import static cc.ibooker.ibookereditorlib.IbookerEditorEnum.TOOLVIEW_TAG.IMG_BACK;

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

    // 线程池保存文件
    private ExecutorService executorService = Executors.newCachedThreadPool();

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
        fileInfoBean = new FileInfoBean();
        fileInfoBean.setFileCreateTime(currentStamp);
        fileInfoBean.setFilePath(currentFilePath);

        IbookerEditorView ibookerEditerView = findViewById(R.id.ibookereditorview);
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

                    // 3s更新一次
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 更新数据库
                            fileInfoBean.setFileName(fileName);
                            if (_id <= 0)
                                _id = sqLiteDao.insertLocalFile2(fileInfoBean);
                            else
                                sqLiteDao.updateLocalFileById(fileInfoBean, _id);
                        }
                    }, 3000);
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
                    // 创建文件
                    if (currentFile == null || !currentFile.exists()) {
                        // 创建目录
                        FileUtil.createSDDirs(FileUtil.LOCALFILE_PATH);
                        // 创建文件
                        currentFile = FileUtil.createFile(currentFilePath);
                    }

                    // 5s更新一次
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 修改文件内容
                            if (currentFile != null) {
                                // 开启子线程保存
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean bool = writeSdData(charSequence.toString(), currentFile);
                                        if (bool) preContent = charSequence.toString();
                                    }
                                });
                                if (executorService == null)
                                    executorService = Executors.newCachedThreadPool();
                                executorService.execute(thread);
                            }
                        }
                    }, 5000);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // 重置保存按钮
        ImageView aboutImg = ibookerEditerView.getIbookerEditorTopView().getAboutImg();
        aboutImg.setImageResource(R.drawable.icon_save);
        aboutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
            }
        });
    }

    // 设置书客编辑器顶部按钮点击事件
    @Override
    public void onTopClick(Object tag) {
        if (ClickUtil.isFastClick()) return;
        if (tag.equals(IMG_BACK)) {// 返回

        } else if (tag.equals(IBTN_ABOUT)) {// 关于

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
     * 写入SD卡文件
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
}
