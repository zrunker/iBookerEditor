package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;

/**
 * 忘记密码二
 */
public class ForgetPasswdTwoActivity extends BaseActivity implements View.OnClickListener {
    private EditText passwEd;
    private String uPhone, code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwd_two);

        uPhone = getIntent().getStringExtra("uPhone");
        code = getIntent().getStringExtra("code");

        initView();
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

                break;
        }
    }
}
