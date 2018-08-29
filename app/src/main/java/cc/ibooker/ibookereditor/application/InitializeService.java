package cc.ibooker.ibookereditor.application;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.tencent.bugly.crashreport.CrashReport;

import cc.ibooker.ibookereditor.BuildConfig;
import cc.ibooker.ibookereditor.utils.AppUtil;

/**
 * 初始化APP - IntentService
 */
public class InitializeService extends IntentService {
    private static final String ACTION_INIT_WHEN_APP_CREATE = "cc.ibooker.ibookereditor.service.action.INIT";

    public InitializeService() {
        super("InitializeService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                performInit();
            }
        }
    }

    private void performInit() {
        // Bugly
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(AppUtil.getVersion(getApplicationContext()));// App的版本
        CrashReport.initCrashReport(getApplicationContext(), "108bf3bed2", BuildConfig.DEBUG, strategy);
    }

}
