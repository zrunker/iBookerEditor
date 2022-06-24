package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.RegularExpressionUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;

/**
 * 反馈
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private EditText emailEd, contentEd;
    private RadioGroup styleRg;
    private String stStyle = "0", stContent, stEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        init();
    }

    // 初始化
    private void init() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        emailEd = findViewById(R.id.ed_email);
        contentEd = findViewById(R.id.ed_content);
        Button submitBtn = findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(this);
        styleRg = findViewById(R.id.rg_style);

        styleRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbtn_suggest:// 建议
                        stStyle = "0";
                        break;
                    case R.id.rbtn_complain:// 投诉
                        stStyle = "1";
                        break;
                    case R.id.rbtn_message:// 留言
                        stStyle = "2";
                        break;
                }
            }
        });

        initData();
    }

    // 初始化数据
    private void initData() {
        styleRg.check(R.id.rbtn_suggest);
        stStyle = "0";
        emailEd.setText("");
        contentEd.setText("");
    }

    // 点击事件监听
    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_submit:
                stContent = contentEd.getText().toString();
                stEmail = emailEd.getText().toString();
                if (TextUtils.isEmpty(stStyle)) {
                    ToastUtil.shortToast(this, "请选择反馈类型");
                } else if (TextUtils.isEmpty(stEmail)) {
                    ToastUtil.shortToast(this, "请输入联系邮箱");
                } else if (!RegularExpressionUtil.isEmail(stEmail)) {
                    ToastUtil.shortToast(this, "请输入正确的联系邮箱");
                } else if (TextUtils.isEmpty(stContent)) {
                    ToastUtil.shortToast(this, "请输入反馈内容");
                } else {
                    // TODO 上传
                }
                break;
        }
    }
}
