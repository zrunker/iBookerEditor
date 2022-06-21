package cc.ibooker.ibookereditor.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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
    private TextView titleTv;

    // 网络状态、数据加载状态
    private LinearLayout stateLayout;
    private ImageView stateImg;
    private TextView stateTv;

    private File file;
    private String title = "";
    private String currentFilePath = "";
    private MyHandler myHandler = new MyHandler(this);
    private final static int FROM_WEB_EDIT_REQUST_CODE = 111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibookereditor_preview);

        // 初始化
        init();

        getIntentFile(getIntent());

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentFile(intent);
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        ImageView editImg = findViewById(R.id.img_edit);
        editImg.setOnClickListener(this);
        titleTv = findViewById(R.id.tv_title);
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
                    Intent intent = new Intent(this, EditNotesActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("_id", -1);
                    intent.putExtra("filePath", file.getAbsolutePath());
                    intent.putExtra("createTime", file.lastModified());
                    intent.putExtra("isNeedReName", false);
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
     * intent获取File
     */
    private void getIntentFile(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            // 将文件复制到制定目录中
            if (Intent.ACTION_VIEW.equals(action)) {
                Uri uri = intent.getData();
//            String filePath = Uri.decode(uri != null ? uri.getEncodedPath() : "");
                String filePath = uri2Path(this, uri);
                if (TextUtils.isEmpty(filePath) || filePath.equals(currentFilePath))
                    return;
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
                        || lowerFilePath.contains(".mm") || lowerFilePath.contains(".oc"))) {// 文件
                    file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        currentFilePath = filePath;
                        title = file.getName();
                        titleTv.setText(title);

                        swipeRefreshLayout.autoRefresh();
                        readFileContent(file);
                    } else {
                        ToastUtil.shortToast(this, "打开文件失败！");
                    }
                } else if (lowerFilePath.contains(".mp4")
                        /*|| url.contains(".mpg")
                        || url.contains(".avi")
                        || url.contains(".flv")
                        || url.contains(".swf")*/
                        || lowerFilePath.contains(".mkv")
                        || lowerFilePath.contains(".3gp")) {// 视频播放
                    Intent intentVideo = new Intent(this, VideoPlayerActivity.class);
                    intentVideo.setData(uri);
                    startActivity(intentVideo);
                    finish();
                } else if (lowerFilePath.contains(".mp3")) {// 音频播放
                    Intent audioIntent = new Intent(this, AudioPlayerActivity.class);
                    audioIntent.setData(uri);
                    startActivity(audioIntent);
                    finish();
                } else if (lowerFilePath.contains(".apk")) {
                    File apkFile = new File(filePath);
                    Intent intentApk = new Intent(Intent.ACTION_VIEW);
                    // 判断是否是AndroidN以及更高的版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intentApk.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = FileProvider.getUriForFile(this, "cc.ibooker.ibookereditor.fileProvider", apkFile);
                        intentApk.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intentApk.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        intentApk.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    startActivity(intentApk);
                } else {
                    ToastUtil.shortToast(this, "打开文件失败！");
                }
            }
        }
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

    /**
     * 将uri转化成真实文件路径path
     */
    public String uri2Path(Context context, Uri uri) {
        String path;
        if ("file".equalsIgnoreCase(uri.getScheme())) {// 使用第三方应用打开
            path = uri.getPath();
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {// 4.4以后
                path = getPath(context, uri);
            } else {// 4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
            }
        }
        return path;
    }

    /**
     * 获取文件路径
     */
    private String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) { // MediaProvider
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 4.4 以下获取文件真实路径
     */
    private String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] pro = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, pro, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }
}
