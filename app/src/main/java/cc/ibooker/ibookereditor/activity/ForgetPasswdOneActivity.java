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
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.zcountdownviewlib.SingleCountDownView;
import cc.ibooker.zdialoglib.ProgressDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 忘记密码一
 */
public class ForgetPasswdOneActivity extends BaseActivity implements View.OnClickListener {
    private SingleCountDownView singleCountDownView;
    private EditText phoneEd, codeEd;
    private String phone;

    private Subscriber<ResultData<Boolean>> validAccountExistSubscriber;
    private Subscriber<ResultData<String>> getSmsCodeSubscriber;
    private Subscriber<ResultData<Boolean>> validSmsCodeSubscriber;
    private CompositeSubscription mSubscription;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwd_one);

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
                        // 倒计时结束
                        updateSingleCountDownView(0);
                    }
                });
        Button nextBtn = findViewById(R.id.btn_next);
        nextBtn.setOnClickListener(this);
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.singleCountDownView:
                phone = phoneEd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.shortToast(this, "请输入手机号");
                } else if (phone.length() != 11) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_phone_tip));
                } else {// 验证手机号是否可以注册
                    validAccountExist(phone);
                }
                break;
            case R.id.btn_next:// 下一步
                phone = phoneEd.getText().toString().trim();
                String code = codeEd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.shortToast(this, "请输入手机号");
                } else if (phone.length() != 11) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_phone_tip));
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.shortToast(this, "请输入验证码");
                } else if (code.length() != 6) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_code_tip));
                } else {
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
                    ToastUtil.shortToast(ForgetPasswdOneActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<Boolean> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        if (resultData.getData() == null) {
                            ToastUtil.shortToast(ForgetPasswdOneActivity.this, "获取数据失败！");
                        } else if (resultData.getData()) {
                            ToastUtil.shortToast(ForgetPasswdOneActivity.this, "该账号还未注册！");
                        } else {
                            singleCountDownView.startCountDown();
                            updateSingleCountDownView(1);
                            // 请求验证码
                            getSmsCode(phone);
                        }
                    } else {// 失败
                        ToastUtil.shortToast(ForgetPasswdOneActivity.this, resultData.getResultMsg());
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
                    ToastUtil.shortToast(ForgetPasswdOneActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<String> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        if (resultData.getData() == null) {
                            ToastUtil.shortToast(ForgetPasswdOneActivity.this, "获取数据失败！");
                        } else {
                            ToastUtil.shortToast(ForgetPasswdOneActivity.this, "验证码已发送！");
                        }
                    } else {// 失败
                        ToastUtil.shortToast(ForgetPasswdOneActivity.this, resultData.getResultMsg());
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
                    ToastUtil.shortToast(ForgetPasswdOneActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<Boolean> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        Intent intent = new Intent(ForgetPasswdOneActivity.this, ForgetPasswdTwoActivity.class);
                        intent.putExtra("uPhone", mobile);
                        intent.putExtra("code", smsCode);
                        startActivity(intent);
                        finish();
                    } else {// 失败
                        ToastUtil.shortToast(ForgetPasswdOneActivity.this, resultData.getResultMsg());
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
