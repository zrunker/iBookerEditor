package cc.ibooker.ibookereditor.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;

/**
 * 视频播放Activity
 *
 * @author 邹峰立
 */
public class VideoPlayerActivity extends BaseActivity
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private ProgressBar progressbar;
    private VideoView videoView;
    private Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 定义全屏参数
//        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        // 获得当前窗体对象
//        Window window = getWindow();
//        // 设置当前窗体为全屏显示
//        window.setFlags(flag, flag);
//        window.getDecorView().setSystemUiVisibility(View.INVISIBLE);

        setContentView(R.layout.activity_videoplayer);
        setStatusBarColor(R.color.colorBlank);

        uri = getIntent().getData();
        initView();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            uri = intent.getData();
            // 设置播放地址
            progressbar.setVisibility(View.VISIBLE);
            videoView.setVideoURI(uri);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    // 初始化View
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick()) return;
                finish();
            }
        });
        videoView = findViewById(R.id.videoview);
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);

        // 添加控制器
        videoView.setMediaController(new MediaController(this));
        // 设置播放地址
        videoView.setVideoURI(uri);
        progressbar = findViewById(R.id.progressbar);
    }

    // 视频播放完成
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        ToastUtil.shortToast(this, "播放完成！");
//        finish();
    }

    // 视频播放错误
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(this, "视频播放出错了", Toast.LENGTH_SHORT).show();
        finish();
        return true;
    }

    // 视频播放已准备就绪
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        videoView.start();

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                // 开始播放时，就把显示第一帧的View去掉
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    progressbar.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
    }

}
