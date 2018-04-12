package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.RegularExpressionUtil;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 反馈
 * <p>
 * Created by 邹峰立 on 2018/3/28.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout contentLayout;
    private EditText emailEd, contentEd;
    private RadioGroup styleRg;
    private String stStyle = "0", stContent, stEmail;

    private Subscriber<ResultData<Boolean>> insertSuggestSubscriber;
    private CompositeSubscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (insertSuggestSubscriber != null)
            insertSuggestSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription.unsubscribe();
        }
    }

    // 初始化
    private void init() {
        contentLayout = findViewById(R.id.layout_content);
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
                    Toast.makeText(this, "请选择反馈类型", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stEmail)) {
                    Toast.makeText(this, "请输入联系邮箱", Toast.LENGTH_SHORT).show();
                } else if (!RegularExpressionUtil.isEmail(stEmail)) {
                    Toast.makeText(this, "请输入正确的联系邮箱", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(stContent)) {
                    Toast.makeText(this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
                } else {
                    insertSuggest();
                }
                break;
        }
    }

    /**
     * 插入反馈信息
     */
    private void insertSuggest() {
        if (NetworkUtil.isNetworkConnected(this)) {
            insertSuggestSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(FeedbackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(ResultData<Boolean> booleanResultData) {
                    if (booleanResultData.getResultCode() == 0) {// 成功
                        if (booleanResultData.getData() == null) {
//                            updateStateLayout(true, 4, null);
                        } else {
                            initData();

//                            updateStateLayout(false, -1, null);
                        }
                    } else {// 失败
//                        updateStateLayout(true, 3, articleUserDataResultData.getResultMsg());
                    }
                }
            };
            HttpMethods.getInstance().insertSuggest(insertSuggestSubscriber, stStyle, stContent, stEmail);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(insertSuggestSubscriber);
        } else {// 无网络

        }
    }
}
