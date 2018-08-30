package cc.ibooker.ibookereditor.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import cc.ibooker.ibookereditor.net.imgdownload.FileDownLoadUtil;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 下载文件服务
 *
 * @author 邹峰立
 */
public class DownLoadService extends Service {
    private String url;
    private Subscriber<ResponseBody> downloadFileSubscriber;
    private CompositeSubscription mSubscription;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url))
            ToastUtil.shortToast(this, "找不到文件地址");
        else {
            ToastUtil.shortToast(this, "下载中...");
            downloadFile();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadFileSubscriber != null)
            downloadFileSubscriber.unsubscribe();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 下载文件
    private void downloadFile() {
        if (NetworkUtil.isNetworkConnected(this)) {
            downloadFileSubscriber = new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(DownLoadService.this, e.getMessage());
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    if (responseBody != null && responseBody.contentLength() > 0) {
                        MediaType mediaType = responseBody.contentType();
                        if (mediaType != null) {
                            String type = mediaType.subtype();
                            String fileName = System.currentTimeMillis() + "." + type;
                            File file = FileDownLoadUtil.getInstance().fileDownLoad(fileName, responseBody);
                            ToastUtil.shortToast(DownLoadService.this, "文件已保存：" + file.getAbsolutePath());
                        }
                    } else {
                        ToastUtil.shortToast(DownLoadService.this, "下载文件失败");
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
