package cc.ibooker.ibookereditor.net.imgdownload;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * 文件下载管理类
 *
 * @author 邹峰立
 */
public class FileDownLoadUtil {

    private static FileDownLoadUtil fileDownLoadUtil;

    public static synchronized FileDownLoadUtil getInstance() {
        if (fileDownLoadUtil == null)
            fileDownLoadUtil = new FileDownLoadUtil();
        return fileDownLoadUtil;
    }

    /**
     * 下载文件
     *
     * @param fileName     文件名
     * @param responseBody 相应体
     * @return 下载好的文件
     */
    public File fileDownLoad(String fileName, ResponseBody responseBody) {
        File file = null;
        if (responseBody != null && responseBody.contentLength() <= 0) {
            FileOutputStream os = null;
            InputStream is = null;
            try {
                // 创建文件
                String imgFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Images" + File.separator + fileName;
                file = new File(imgFilePath);
                boolean bool = file.exists();
                if (!bool)
                    bool = file.mkdirs();
                if (bool) {
                    // 获取文件输出流
                    os = new FileOutputStream(file);

                    // 获取responseBody输入流
                    is = responseBody.byteStream();

                    // 写入文件
                    int length;
                    int record = 0;
                    byte[] buffer = new byte[1024];
                    while ((length = is.read(buffer)) != -1) {
                        os.write(buffer, 0, length);
                        record += length;
                        if (onDownLoadListener != null)
                            onDownLoadListener.downLoadProgress(record);
                    }

                    os.flush();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                // 关闭资源
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return file;
    }

    // 下载监听器
    public interface OnDownLoadListener {
        void downLoadProgress(int record);
    }

    private OnDownLoadListener onDownLoadListener;

    public void setOnDownLoadListener(OnDownLoadListener onDownLoadListener) {
        this.onDownLoadListener = onDownLoadListener;
    }
}
