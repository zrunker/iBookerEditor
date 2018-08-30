package cc.ibooker.ibookereditor.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.ImgVPagerAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.net.imgdownload.FileDownLoadUtil;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.RegularExpressionUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.ibookereditor.view.DownLoadImgPopuwindow;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditorlib.IbookerEditorScaleImageView;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 图片预览Activity
 * <p>
 * Created by 邹峰立 on 2018/3/13 0013.
 */
public class ImgVPagerActivity extends BaseActivity implements View.OnClickListener {
    private String currentPath;
    private int currentPosition;
    private ArrayList<String> imgAllPathList;

    private ViewPager mViewPager;
    private ImgVPagerAdapter mAdapter;
    private TextView indicatorTv;

    private DownLoadImgPopuwindow downLoadImgPopuwindow;
    private Subscriber<ResponseBody> downloadFileSubscriber;
    private CompositeSubscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgvpger);
        setStatusBarColor(R.color.colorBlank);

        // 获取上一个界面传值
        currentPath = getIntent().getStringExtra("currentPath");
        currentPosition = getIntent().getIntExtra("position", 0);
        imgAllPathList = getIntent().getStringArrayListExtra("imgAllPathList");

        // 初始化
        if (imgAllPathList != null && imgAllPathList.size() > 0)
            init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (downLoadImgPopuwindow != null)
            downLoadImgPopuwindow.dismiss();
        if (downloadFileSubscriber != null)
            downloadFileSubscriber.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 初始化
    private void init() {
        mViewPager = findViewById(R.id.id_viewpager);
        ImageView shareImg = findViewById(R.id.img_share);
        shareImg.setOnClickListener(this);
        ImageView leftImg = findViewById(R.id.img_left);
        leftImg.setOnClickListener(this);
        ImageView rightImg = findViewById(R.id.img_right);
        rightImg.setOnClickListener(this);
        indicatorTv = findViewById(R.id.tv_indicator);

        // 初始化数据
        initVpData();
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                currentPath = imgAllPathList.get(position);
                updateIndicatorTv(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateIndicatorTv(currentPosition);
    }

    // 格式化indicatorTv
    private void updateIndicatorTv(int currentPosition) {
        String indicatorTip = (currentPosition + 1) + "/" + imgAllPathList.size();
        indicatorTv.setText(indicatorTip);
    }

    // 初始化ViewPager数据
    private void initVpData() {
        if (imgAllPathList != null && imgAllPathList.size() > 0) {
            ArrayList<IbookerEditorScaleImageView> imageViews = new ArrayList<>();
            // 获取图片资源，并保存到imageViews中
            for (int i = 0; i < imgAllPathList.size(); i++) {
                IbookerEditorScaleImageView imageView = new IbookerEditorScaleImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setOnMyClickListener(new IbookerEditorScaleImageView.OnMyClickListener() {
                    @Override
                    public void onMyClick(View v) {// 点击事件
                        finish();
                    }
                });
                final String imgPath = imgAllPathList.get(i);
                imageView.setOnMyLongClickListener(new IbookerEditorScaleImageView.OnMyLongClickListener() {
                    @Override
                    public void onMyLongClick(View v) {// 长按事件
                        downLoadImgPopuwindow = new DownLoadImgPopuwindow(ImgVPagerActivity.this, imgPath);
                        downLoadImgPopuwindow.showBottom();
                    }
                });
                GlideApp.with(this).load(imgPath).into(imageView);
                imageViews.add(imageView);
            }
            // 刷新数据
            setAdapter(imageViews);
        }
    }

    // 自定义setAdapter
    private void setAdapter(ArrayList<IbookerEditorScaleImageView> list) {
        if (mAdapter == null) {
            mAdapter = new ImgVPagerAdapter(list);
            mViewPager.setAdapter(mAdapter);
        } else {
            mAdapter.reflashData(list);
        }
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_share:// 分享
                if (RegularExpressionUtil.isInternetURL(currentPath)) {// 下载文件在分享
                    ToastUtil.shortToast(this, "图片保存中...");
                    downloadFile(currentPath);
                } else {// 本地文件直接分享
                    File file = new File(currentPath);
                    sharePicture(ImgVPagerActivity.this, file, file.getName());
                }
                break;
            case R.id.img_left:// 左移图片
                mViewPager.setCurrentItem(currentPosition == 0 ? imgAllPathList.size() - 1 : currentPosition - 1);
                break;
            case R.id.img_right:// 右移事件
                mViewPager.setCurrentItem(currentPosition == imgAllPathList.size() - 1 ? 0 : currentPosition + 1);
                break;
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
        if (file.exists() && file.isFile()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, Kdescription);
            context.startActivity(intent);
        }
    }

    // 下载文件
    private void downloadFile(String url) {
        if (NetworkUtil.isNetworkConnected(this)) {
            downloadFileSubscriber = new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(ImgVPagerActivity.this, e.getMessage());
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    if (responseBody != null && responseBody.contentLength() > 0) {
                        MediaType mediaType = responseBody.contentType();
                        if (mediaType != null) {
                            String type = mediaType.subtype();
                            String fileName = System.currentTimeMillis() + "." + type;
                            File file = FileDownLoadUtil.getInstance().fileDownLoad(fileName, responseBody);
                            ToastUtil.shortToast(ImgVPagerActivity.this, "文件已保存：" + file.getAbsolutePath());
                            sharePicture(ImgVPagerActivity.this, file, file.getName());
                        }
                    } else {
                        ToastUtil.shortToast(ImgVPagerActivity.this, "下载文件失败");
                    }
                }
            };
            HttpMethods.getInstance().downloadFile(downloadFileSubscriber, url);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(downloadFileSubscriber);
        } else {
            ToastUtil.shortToast(this, "网络不给力！");
        }
    }
}
