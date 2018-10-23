package cc.ibooker.ibookereditor.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.event.UpdateUserInfoSuccessEvent;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.BitmapUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.zdialoglib.ChoosePictrueDialog;
import cc.ibooker.zdialoglib.ChoosePictrueUtil;
import cc.ibooker.zdialoglib.ProgressDialog;
import cc.ibooker.zdialoglib.ZDialogConstantUtil;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 个人图像
 */
public class PicActivity extends BaseActivity implements View.OnClickListener {
    private final int LOGIN_REQUEST_CODE_UPLOAD = 200;// 上传图片登录请求码
    private ImageView imageView;
    private Bitmap picBitmap;
    private String imgPath, fileName, imgfile;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    private ChoosePictrueDialog choosePictrueDialog;
    private ProgressDialog proDialog;
    private Subscriber<ResultData<String>> upLoadUserPicImageSubscriber;
    private CompositeSubscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeChoosePictrueDialog();
        closeProDialog();
        if (upLoadUserPicImageSubscriber != null)
            upLoadUserPicImageSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (picBitmap != null) {
            picBitmap.recycle();
            picBitmap = null;
            System.gc();
        }
        if (mSubscription != null)
            mSubscription.unsubscribe();
        if (mExecutorService != null)
            mExecutorService.shutdownNow();
        if (viewHandler != null) {
            viewHandler.removeCallbacksAndMessages(null);
            viewHandler = null;
        }
    }

    // 初始化控件
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        imageView = findViewById(R.id.img_pic);
        Button choosePicBtn = findViewById(R.id.btn_choose_img);
        choosePicBtn.setOnClickListener(this);
        if (ConstantUtil.userDto != null && ConstantUtil.userDto.getUser() != null)
            GlideApp.with(this)
                    .load(ConstantUtil.userDto.getUser().getuPic())
                    .placeholder(R.drawable.icon_mepic)
                    .error(R.drawable.icon_mepic)
                    .into(imageView);
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_choose_img:
                showChoosePictrueDialog();
                break;
        }
    }

    // 展示ChoosePictrueDialog
    private void showChoosePictrueDialog() {
        if (choosePictrueDialog == null)
            choosePictrueDialog = new ChoosePictrueDialog(this).setBtnColor("#555555");
        choosePictrueDialog.showChoosePictrueDialog();
    }

    // 关闭ChoosePictrueDialog
    private void closeChoosePictrueDialog() {
        if (choosePictrueDialog != null)
            choosePictrueDialog.closeChoosePictrueDialog();
    }

    // 开启Dialog
    private void showProDialog() {
        if (proDialog == null)
            proDialog = new ProgressDialog(this);
        proDialog.showProDialog();
    }

    // 关闭Dialog
    private void closeProDialog() {
        if (proDialog != null)
            proDialog.closeProDialog();
    }

    /**
     * 通过回调方法处理图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ZDialogConstantUtil.RESULT_PHOTO_CODE:
                /*
                 * 拍照
                 */
                closeChoosePictrueDialog();
                Uri photoUri = ChoosePictrueUtil.photoUri;
                if (photoUri != null) {
                    // 通知系统刷新图库
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri);
                    sendBroadcast(localIntent);
                    // 启动裁剪
                    cropImageUri(photoUri);
                }
                break;
            case ZDialogConstantUtil.RESULT_LOAD_CODE:
                /*
                 * 从相册中选择图片
                 */
                closeChoosePictrueDialog();
                if (data == null) {
                    return;
                } else {
                    Uri uri = data.getData(); // 获取图片是以content开头
                    if (uri != null) {
                        cropImageUri(uri);// 开始裁剪
                    }
                }
                break;
            case ZDialogConstantUtil.REQUEST_CROP_CODE: // 裁剪图片结果处理
                if (data == null) {
                    return;
                } else {
                    compressUri(imgPath);
                }
                break;
            case LOGIN_REQUEST_CODE_UPLOAD:// 上传图片登录请求码
                upLoadUserPicImage();
                break;
            default:
                break;
        }
    }

    // 压缩图片并显示
    private void compressUri(final String imgPath) {
        showProDialog();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    picBitmap = BitmapUtil.getLoacalBitmap(imgPath);
                    if (picBitmap == null) {
                        fileName = "";
                        imgfile = "";
                    } else {
                        // 压缩
                        picBitmap = BitmapUtil.compressImageByRatio(picBitmap, 300, 300);
                        // 将字节转换成base64码
                        imgfile = bitMapToBase64(picBitmap);
                        fileName = System.currentTimeMillis() + ".jpg";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewHandler.sendEmptyMessage(2);
            }
        });
        if (mExecutorService == null || mExecutorService.isShutdown())
            mExecutorService = Executors.newCachedThreadPool();
        mExecutorService.execute(thread);
    }

    /**
     * 开启Android截图的功能
     */
    private void cropImageUri(Uri uri) {
        try {
            if (!FileUtil.isFileExist(FileUtil.IMAGE_PATH)) {// 创建文件夹
                File dirFilr = FileUtil.createSDDirs(FileUtil.IMAGE_PATH);
                if (dirFilr == null || !dirFilr.exists()) {
                    ToastUtil.shortToast(this, "创建SD文件失败,请仔细检查SD卡是否正确！");
                    return;
                }
            }
            // 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
            imgPath = FileUtil.IMAGE_PATH + System.currentTimeMillis() + ".jpg";
            File imgFile = new File(imgPath);
            Uri imageUri = Uri.fromFile(imgFile);
            /*
             * 开启Android截图的功能
             */
            final Intent intent = new Intent("com.android.camera.action.CROP");
            // 照片URL地址
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 100);
            intent.putExtra("aspectY", 100);
            intent.putExtra("outputX", 300);//X方向上的比例
            intent.putExtra("outputY", 300);//Y方向上的比例
            intent.putExtra("scale", true);//是否保留比例
            // 输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 输出格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            // 不启用人脸识别
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
            // 竖屏
            intent.putExtra(MediaStore.Images.ImageColumns.ORIENTATION, 0);
            startActivityForResult(intent, ZDialogConstantUtil.REQUEST_CROP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过handler改变UI
     */
    IHandler viewHandler = new IHandler(this);

    private static class IHandler extends Handler {
        // 定义一个对象用来引用Activity中的方法
        private final WeakReference<Activity> mActivity;

        IHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PicActivity currentActivity = ((PicActivity) mActivity.get());
            currentActivity.closeProDialog();// 关闭Dialog
            if (msg.what == 2) {
                if (currentActivity.picBitmap != null) {
                    currentActivity.imageView.setImageBitmap(currentActivity.picBitmap);
                    // 上传至服务端
                    currentActivity.upLoadUserPicImage();
                } else {
                    ToastUtil.shortToast(currentActivity, "图片获取失败");
                }
            }
        }
    }

    // 将BitMap转换成Base64
    private String bitMapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            // 将字节转换成base64码
            String str = Base64.encodeToString(bytes, Base64.DEFAULT);
            stream.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新用户图像
     */
    private void upLoadUserPicImage() {
        if (ConstantUtil.userDto == null || ConstantUtil.userDto.getUser() == null) {
            Intent intent_login = new Intent(this, LoginActivity.class);
            startActivityForResult(intent_login, LOGIN_REQUEST_CODE_UPLOAD);
            return;
        }
        if (NetworkUtil.isNetworkConnected(this)) {
            if (TextUtils.isEmpty(imgfile) || TextUtils.isEmpty(fileName)) {
                ToastUtil.shortToast(this, "图片转码失败！");
                return;
            }
            upLoadUserPicImageSubscriber = new Subscriber<ResultData<String>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    closeProDialog();
                    ToastUtil.shortToast(PicActivity.this, "错误：" + e.getMessage());
                }

                @Override
                public void onNext(ResultData<String> result) {
                    if (result.getResultCode() == 0) {// 成功
                        // 更新数据库
                        ConstantUtil.userDto.getUser().setuPic(result.getData());

                        SQLiteDao sqLiteDao = new SQLiteDaoImpl(PicActivity.this);
                        sqLiteDao.updateUser(ConstantUtil.userDto);
                        // 发送通信，更新界面
                        EventBus.getDefault().postSticky(new UpdateUserInfoSuccessEvent(true));
                        // 返回
                        setResult(RESULT_OK);
                        finish();
                    } else {// 失败
                        ToastUtil.shortToast(PicActivity.this, result.getResultMsg());
                    }
                }

                @Override
                public void onStart() {
                    showProDialog();
                }
            };
            HttpMethods.getInstance().upLoadUserPicImage(upLoadUserPicImageSubscriber, ConstantUtil.userDto.getUser().getuId(), imgfile);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(upLoadUserPicImageSubscriber);
        } else {// 当前网络不可用
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
    }

}
