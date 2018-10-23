package cc.ibooker.ibookereditor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import cc.ibooker.ibookereditor.utils.RegularExpressionUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.zcountdownviewlib.SingleCountDownView;
import cc.ibooker.zdialoglib.ProgressDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 注册
 */
public class RegActivity extends BaseActivity implements View.OnClickListener {
    private EditText phoneEd, codeEd, passwdEd, ensurePasswdEd;
    private SingleCountDownView singleCountDownView;
    private String phone, code, passwd;

    private Subscriber<ResultData<Boolean>> validAccountExistSubscriber;
    private Subscriber<ResultData<String>> getSmsCodeSubscriber;
    private Subscriber<ResultData<Boolean>> validSmsCodeSubscriber;
    private Subscriber<ResultData<UserDto>> registerByPhoneSubscriber;
    private CompositeSubscription mSubscription;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (validAccountExistSubscriber != null)
            validAccountExistSubscriber.unsubscribe();
        if (getSmsCodeSubscriber != null)
            getSmsCodeSubscriber.unsubscribe();
        if (validSmsCodeSubscriber != null)
            validSmsCodeSubscriber.unsubscribe();
        if (registerByPhoneSubscriber != null)
            registerByPhoneSubscriber.unsubscribe();
        closeProDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleCountDownView != null)
            singleCountDownView.destorySingleCountDownView();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 初始化控件
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        phoneEd = findViewById(R.id.ed_phone);
        codeEd = findViewById(R.id.ed_code);
        passwdEd = findViewById(R.id.ed_passwd);
        ensurePasswdEd = findViewById(R.id.ed_ensure_passwd);
        singleCountDownView = findViewById(R.id.singleCountDownView);
        singleCountDownView.setOnClickListener(this);
        singleCountDownView.setTime(60)
                .setTimeColorHex("#ffffff")
                .setTimePrefixText("重新获取(")
                .setTimeSuffixText(")")
                .setDefaultText("获取验证码")
                .setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
                    @Override
                    public void onSingleCountDownEnd() {
                        // 验证码结束
                        updateSingleCountDownView(0);
                    }
                });
        Button regBtn = findViewById(R.id.btn_reg);
        regBtn.setOnClickListener(this);
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.singleCountDownView:// 倒计时
                phone = phoneEd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.shortToast(this, "请输入手机号");
                } else if (phone.length() != 11) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_phone_tip));
                } else {// 验证手机号是否可以注册
                    validAccountExist(phone);
                }
                break;
            case R.id.btn_reg:// 注册
                phone = phoneEd.getText().toString().trim();
                code = codeEd.getText().toString().trim();
                passwd = passwdEd.getText().toString().trim();
                String ensurePasswd = ensurePasswdEd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.shortToast(this, "请输入手机号");
                } else if (phone.length() != 11) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_phone_tip));
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.shortToast(this, "请输入验证码");
                } else if (code.length() != 6) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_code_tip));
                } else if (TextUtils.isEmpty(passwd)) {
                    ToastUtil.shortToast(this, "请输入密码");
                } else if (!RegularExpressionUtil.isPassword(passwd)) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_passwd_tip));
                } else if (TextUtils.isEmpty(ensurePasswd)) {
                    ToastUtil.shortToast(this, "请输入确认密码");
                } else if (!passwd.equals(ensurePasswd)) {
                    ToastUtil.shortToast(this, "两次密码不一致");
                } else {// 验证验证码-注册
                    validSmsCode(phone, code);
                }
                break;
        }
    }

    /**
     * 修改倒计时按钮状态
     *
     * @param index 0代表未点击状态，1代表已点击状态
     */
    private void updateSingleCountDownView(int index) {
        switch (index) {
            case 0:
                singleCountDownView.setBackgroundResource(R.drawable.bg_fe7517_corner_2);
                break;
            case 1:
                singleCountDownView.setBackgroundResource(R.drawable.bg_87fe7517_corner_2);
                break;
        }
    }

    /**
     * 验证账号（该账号是否可以注册）
     */
    private void validAccountExist(String account) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProDialog();
            validAccountExistSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(RegActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<Boolean> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        if (resultData.getData() == null) {
                            ToastUtil.shortToast(RegActivity.this, "获取数据失败！");
                        } else if (!resultData.getData()) {
                            ToastUtil.shortToast(RegActivity.this, "该账号已被注册！");
                        } else {
                            singleCountDownView.startCountDown();
                            updateSingleCountDownView(1);
                            // 请求验证码
                            getSmsCode(phone);
                        }
                    } else {// 失败
                        ToastUtil.shortToast(RegActivity.this, resultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().validAccountExist(validAccountExistSubscriber, account);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(validAccountExistSubscriber);
        } else {// 无网络
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
    }

    /**
     * 获取短信验证码
     */
    private void getSmsCode(String mobile) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProDialog();
            getSmsCodeSubscriber = new Subscriber<ResultData<String>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(RegActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<String> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        if (resultData.getData() == null) {
                            ToastUtil.shortToast(RegActivity.this, "获取数据失败！");
                        } else {
                            ToastUtil.shortToast(RegActivity.this, "验证码已发送！");
                        }
                    } else {// 失败
                        ToastUtil.shortToast(RegActivity.this, resultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().getSmsCode(getSmsCodeSubscriber, mobile);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(getSmsCodeSubscriber);
        } else {// 无网络
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
    }

    /**
     * 验证短信验证码
     */
    private void validSmsCode(final String mobile, final String smsCode) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProDialog();
            validSmsCodeSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(RegActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<Boolean> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功 - 执行注册
                        registerByPhone(phone, passwd, code);
                    } else {// 失败
                        ToastUtil.shortToast(RegActivity.this, resultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().validSmsCode(validSmsCodeSubscriber, mobile, smsCode);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(validSmsCodeSubscriber);
        } else {// 无网络
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
    }

    /**
     * 通过手机号注册
     */
    private void registerByPhone(String account, String uPasswd, String smsCode) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProDialog();
            registerByPhoneSubscriber = new Subscriber<ResultData<UserDto>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(RegActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<UserDto> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        if (resultData.getData() == null) {
                            ToastUtil.shortToast(RegActivity.this, "获取数据失败！");
                        } else {
                            // 静态赋值
                            ConstantUtil.userDto = resultData.getData();
                            // 将数据保存到数据库
                            SQLiteDao sqLiteDao = new SQLiteDaoImpl(RegActivity.this);
                            sqLiteDao.insertUser(resultData.getData());

                            // 关闭当前页面进入完善个人信息界面
                            Intent intent = new Intent(RegActivity.this, PrefectMeInfoActivity.class);
                            intent.putExtra("isValidNicknameExist", true);
                            startActivity(intent);
                            setResult(RESULT_OK);
                            finish();
                        }
                    } else {// 失败
                        ToastUtil.shortToast(RegActivity.this, resultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().registerByPhone(registerByPhoneSubscriber, account, uPasswd, smsCode);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(registerByPhoneSubscriber);
        } else {// 无网络
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
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
}
