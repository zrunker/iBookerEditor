package cc.ibooker.ibookereditor.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常错误
 *
 * @author 邹峰立
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final String TAG = getClass().getSimpleName();
    private Map<String, Object> map = new HashMap<>();// 保存数据信息
    private Context mContext;
    private Thread.UncaughtExceptionHandler mExHandler;

    @SuppressLint("StaticFieldLeak")
    private static CrashHandler crashHandler;

    public static CrashHandler getInstance() {
        if (crashHandler == null)
            crashHandler = new CrashHandler();
        return crashHandler;
    }

    // 初始化方法
    public void init(Context context) {
        this.mContext = context;
        this.mExHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mExHandler);
    }

    /**
     * 当发生UncaughtException异常时候会执行以下方法
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        boolean isExecuteExAfter = executeExAfter(throwable);
        if (!isExecuteExAfter) {
            if (mExHandler != null) {
                // 用户未处理，并且mExHander不为空 - 交由系统处理
                mExHandler.uncaughtException(thread, throwable);
            } else {
                exitSystem();
            }
        }
    }

    /**
     * 异常处理 - 当异常信息不为空时进行后台传递
     */
    private boolean executeExAfter(Throwable throwable) {
        if (throwable != null) {
            // 提示用户
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉，应用程序出现异常，程序即将关闭！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();

            // 将异常信息传到后台
//            getDeviceInfo(mContext);
//            saveCrashInfoToFile(throwable);

            exitSystem();

            return true;
        } else {
            return false;
        }
    }

    /**
     * 退出程序
     */
    private void exitSystem() {
        // 退出进程
        Process.killProcess(Process.myPid());
        // 异常退出 1
        System.exit(1);
    }

    /**
     * 获取设备/应用信息
     */
    private void getDeviceInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pi = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            String versionName = pi.versionName;
            int versionCode = pi.versionCode;
            map.put("versionName", versionName);
            map.put("versionCode", versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * 写入日志文件
     */
    private void saveCrashInfoToFile(final Throwable throwable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                FileOutputStream os = null;
                try {
                    // 获取数据
                    long timestamp = System.currentTimeMillis();
                    map.put("timestamp", timestamp);
                    // 转换数据
                    StringBuilder sb = new StringBuilder();
                    sb.append("---------------------start---------------------");

                    // 创建一个不带自动换行的PrintWriter
                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);

                    // 获取导致异常的Cause
                    Throwable cause = throwable.getCause();
                    while (cause != null) {
                        cause.printStackTrace(printWriter);
                        cause = cause.getCause();
                    }

                    printWriter.close();

                    String throwableResult = writer.toString();
                    sb.append(throwableResult);

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
                    }
                    sb.append("---------------------end---------------------");

                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 创建文件
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "AIXiZi" + File.separator + "crash-" + File.separator + timestamp + ".log";
                    file = new File(filePath);
                    boolean fileExists = file.exists();
                    if (!fileExists) {
                        fileExists = file.mkdirs();
                    }

                    // 写入文件
                    if (fileExists) {
                        os = new FileOutputStream(file);
                        os.write(sb.toString().getBytes());
                        os.flush();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } finally {
                    // 关闭资源
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }

                // 发送到服务器
                uploadLogFile(file);
            }
        }).start();
    }

    /**
     * 上传文件
     */
    private void uploadLogFile(File file) {
//        if (file == null || !file.exists())
//            exitSystem();
//        else {
//            // 调用接口实现上传功能
//        }

        exitSystem();
    }
}
