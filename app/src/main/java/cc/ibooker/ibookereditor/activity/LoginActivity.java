package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.dto.UserDto;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.zdialoglib.ProDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 登录界面
 *
 * @author 邹峰立
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText accountEd, passwdEd;
    private ProDialog proDialog;
    private Subscriber<ResultData<UserDto>> userLoginSubscriber;
    private CompositeSubscription mSubscription;
    private SQLiteDao sqLiteDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userLoginSubscriber != null)
            userLoginSubscriber.unsubscribe();
        closeProDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 初始化控件
    private void init() {
        accountEd = findViewById(R.id.ed_account);
        passwdEd = findViewById(R.id.ed_passwd);
        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.btn_login:
                String uPhone = accountEd.getText().toString().trim();
                String uPasswd = passwdEd.getText().toString().trim();
                if (TextUtils.isEmpty(uPhone)) {
                    Toast.makeText(this, "请输入书客创作账号", Toast.LENGTH_SHORT).show();
                } else if (uPhone.length() != 11) {
                    Toast.makeText(this, "请输入正确格式的手机号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(uPasswd)) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    // 提交到后台实现登录
                    userLogin(uPhone, uPasswd);
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void userLogin(String account, String uPasswd) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProDialog();
            userLoginSubscriber = new Subscriber<ResultData<UserDto>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<UserDto> userDtoResultData) {
                    if (userDtoResultData.getResultCode() == 0) {// 成功
                        if (userDtoResultData.getData() == null) {
                            Toast.makeText(LoginActivity.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
                        } else {
                            // 静态赋值
                            ConstantUtil.userDto = userDtoResultData.getData();
                            // 将数据保存到数据库
                            if (sqLiteDao == null)
                                sqLiteDao = new SQLiteDaoImpl(LoginActivity.this);
                            sqLiteDao.insertUser(userDtoResultData.getData());
                            finish();
                        }
                    } else {// 失败
                        Toast.makeText(LoginActivity.this, userDtoResultData.getResultMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            HttpMethods.getInstance().userLogin(userLoginSubscriber, account, uPasswd);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(userLoginSubscriber);
        } else {// 无网络
            Toast.makeText(this, "当前网络不给力！", Toast.LENGTH_SHORT).show();
        }
    }

    // 开启Dialog
    private void showProDialog() {
        if (proDialog == null)
            proDialog = new ProDialog(this);
        proDialog.showProDialog();
    }

    // 关闭Dialog
    private void closeProDialog() {
        if (proDialog != null)
            proDialog.closeProDialog();
    }
}
