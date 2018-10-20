package cc.ibooker.ibookereditor.activity;

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
import cc.ibooker.ibookereditor.utils.RegularExpressionUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.zdialoglib.ProgressDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 忘记密码二
 */
public class ForgetPasswdTwoActivity extends BaseActivity implements View.OnClickListener {
    private EditText passwEd;
    private String uPhone, code;

    private Subscriber<ResultData<Boolean>> updatePasswdByUphoneSubscriber;
    private CompositeSubscription mSubscription;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwd_two);

        uPhone = getIntent().getStringExtra("uPhone");
        code = getIntent().getStringExtra("code");

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (updatePasswdByUphoneSubscriber != null)
            updatePasswdByUphoneSubscriber.unsubscribe();
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
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        passwEd = findViewById(R.id.ed_passwd);
        Button submitBtn = findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(this);
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_submit:// 提交
                String newCode = passwEd.getText().toString().trim();
                if (TextUtils.isEmpty(newCode)) {
                    ToastUtil.shortToast(this, "请输入密码");
                } else if (!RegularExpressionUtil.isPassword(newCode)) {
                    ToastUtil.shortToast(this, getResources().getString(R.string.input_passwd_tip));
                } else {// 修改密码
                    updatePasswdByUphone(uPhone, code, newCode);
                }
                break;
        }
    }

    /**
     * 根据手机号修改密码
     */
    private void updatePasswdByUphone(String uPhone, String code, String newCode) {
        if (NetworkUtil.isNetworkConnected(this)) {
            showProDialog();
            updatePasswdByUphoneSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.shortToast(ForgetPasswdTwoActivity.this, e.getMessage());
                    closeProDialog();
                }

                @Override
                public void onNext(ResultData<Boolean> resultData) {
                    if (resultData.getResultCode() == 0) {// 成功
                        ToastUtil.shortToast(ForgetPasswdTwoActivity.this, "密码修改成功，重新登录试试！");
                        finish();
                    } else {// 失败
                        ToastUtil.shortToast(ForgetPasswdTwoActivity.this, resultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().updatePasswdByUphone(updatePasswdByUphoneSubscriber, uPhone, code, newCode);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(updatePasswdByUphoneSubscriber);
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
