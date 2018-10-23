package cc.ibooker.ibookereditor.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.dto.ResultData;
import cc.ibooker.ibookereditor.event.UpdateUserInfoSuccessEvent;
import cc.ibooker.ibookereditor.net.service.HttpMethods;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.utils.DateUtil;
import cc.ibooker.ibookereditor.utils.NetworkUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.zdialoglib.ProgressDialog;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * 完善个人信息
 */
public class PrefectMeInfoActivity extends BaseActivity implements View.OnClickListener {
    private final int CHOOSE_ADDR_REQUESR_CODE = 66;// 地址选择请求码
    private final int LOGIN_REQUEST_CODE = 199;// 登录请求码
    private EditText nickNameEd, heightEd, weightEd, introduceEd;
    private RadioButton maleRb, femaleRb;
    private TextView birthTv, addrTv;
    private String u_nickname, u_sex, u_birthday, u_introduce, u_domicile;
    private float u_height, u_weight;
    private double u_pointx, u_pointy;
    private boolean isValidNicknameExist;

    private Subscriber<ResultData<Boolean>> updateUserByUidSubscriber;
    private Subscriber<ResultData<Boolean>> validNicknameExistSubscriber;
    private ProgressDialog proDialog;
    private CompositeSubscription mSubscription;
    private DatePickerDialog dateDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefect_meinfo);

        isValidNicknameExist = getIntent().getBooleanExtra("isValidNicknameExist", false);
        initView();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeDatePickerDialog();
        closeProDialog();
        if (updateUserByUidSubscriber != null)
            updateUserByUidSubscriber.unsubscribe();
        if (validNicknameExistSubscriber != null)
            validNicknameExistSubscriber.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    // 初始化控件
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(this);
        nickNameEd = findViewById(R.id.ed_nickname);
        nickNameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    u_nickname = s.toString().trim();
            }
        });
        RadioGroup sexRp = findViewById(R.id.rp);
        sexRp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //获取变更后的选中项的ID
                int radioButtonId = group.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = findViewById(radioButtonId);
                u_sex = rb.getText().toString().trim();
            }
        });
        maleRb = findViewById(R.id.male);
        femaleRb = findViewById(R.id.female);
        heightEd = findViewById(R.id.ed_user_height);
        heightEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.length() > 0) {
                        float height = Float.parseFloat(s.toString().trim());
                        if (height > 300)
                            // 身高不能超过3M
                            ToastUtil.shortToast(PrefectMeInfoActivity.this, "身高不能超过300CM");
                        else
                            u_height = height;
                    }
                } catch (Exception e) {
                    ToastUtil.shortToast(PrefectMeInfoActivity.this, "身高只能输入数字！");
                }

            }
        });
        weightEd = findViewById(R.id.ed_user_weight);
        weightEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.length() > 0) {
                        float weight = Float.parseFloat(s.toString().trim());
                        if (weight > 1000)
                            // 体重不能超过1吨
                            ToastUtil.shortToast(PrefectMeInfoActivity.this, "体重不能超过1000KG");
                        else
                            u_weight = weight;
                    }
                } catch (Exception e) {
                    ToastUtil.shortToast(PrefectMeInfoActivity.this, "体重只能输入数字！");
                }
            }
        });
        birthTv = findViewById(R.id.tv_user_birth);
        birthTv.setOnClickListener(this);
        addrTv = findViewById(R.id.tv_addr);
        addrTv.setOnClickListener(this);
        introduceEd = findViewById(R.id.ed_user_introduce);
        introduceEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    u_introduce = s.toString().trim();
            }
        });
        Button submitBtn = findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(this);
    }

    // 初始化数据
    private void initData() {
        if (ConstantUtil.userDto != null && ConstantUtil.userDto.getUser() != null) {
            u_nickname = ConstantUtil.userDto.getUser().getuNickname();
            u_height = ConstantUtil.userDto.getUser().getuHeight();
            u_weight = ConstantUtil.userDto.getUser().getuWeight();
            u_introduce = ConstantUtil.userDto.getUser().getuIntroduce();
            u_birthday = ConstantUtil.userDto.getUser().getuBirthday();
            u_sex = ConstantUtil.userDto.getUser().getuSex();
            u_domicile = ConstantUtil.userDto.getUser().getuDomicile();
            u_pointx = ConstantUtil.userDto.getUser().getuPointx();
            u_pointy = ConstantUtil.userDto.getUser().getuPointy();
            if ("男".equals(u_sex))
                maleRb.setChecked(true);
            else if ("女".equals(u_sex))
                femaleRb.setChecked(true);
            nickNameEd.setText(u_nickname);
            heightEd.setText(u_height + "");
            weightEd.setText(u_weight + "");
            introduceEd.setText(u_introduce);
            birthTv.setText(u_birthday);
            addrTv.setText(u_domicile);
        }
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:// 返回
                finish();
                break;
            case R.id.tv_user_birth:// 出生日期
                showDatePickerDialog();
                break;
            case R.id.tv_addr:// 选择地址
                Intent intentAddr = new Intent(this, AMapLocationPOIActivity.class);
                startActivityForResult(intentAddr, CHOOSE_ADDR_REQUESR_CODE);
                break;
            case R.id.btn_submit:// 提交
                if (TextUtils.isEmpty(u_nickname)) {
                    ToastUtil.shortToast(this, "请输入昵称！");
                } else if (!TextUtils.isEmpty(u_birthday) && u_birthday.compareTo(DateUtil.getCurrentDate()) > 0) {
                    ToastUtil.shortToast(this, "出生日期不能大于当前日期！");
                } else {
                    if (TextUtils.isEmpty(u_sex))
                        u_sex = "保密";
                    if (isValidNicknameExist || !ConstantUtil.userDto.getUser().getuNickname().equals(u_nickname))
                        // 第一次设置昵称、昵称有变更
                        validNicknameExist();
                    else
                        updateUserByUid();
                }
                break;
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

    // 显示日期Dialog
    private void showDatePickerDialog() {
        if (dateDialog == null) {
            // 获取当前年月日
            Calendar ca = Calendar.getInstance();
            if (!TextUtils.isEmpty(u_birthday)) {
                Date date = DateUtil.parseDateFormat(u_birthday, DateUtil.Format_Date);
                if (date != null)
                    ca.setTime(date);
            }
            dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    u_birthday = year + "-" + (month + 1) + "-" + dayOfMonth;
                    birthTv.setText(u_birthday);
                }
            }, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        }
        dateDialog.show();
    }

    // 关闭日期Dialog
    private void closeDatePickerDialog() {
        if (dateDialog != null)
            dateDialog.dismiss();
    }

    /**
     * 通过ID修改个人信息
     */
    private void updateUserByUid() {
        if (ConstantUtil.userDto == null || ConstantUtil.userDto.getUser() == null) {
            Intent intent_login = new Intent(PrefectMeInfoActivity.this, LoginActivity.class);
            startActivityForResult(intent_login, LOGIN_REQUEST_CODE);
            return;
        }
        if (NetworkUtil.isNetworkConnected(this)) {
            updateUserByUidSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    closeProDialog();
                    ToastUtil.shortToast(PrefectMeInfoActivity.this, "错误：" + e.getMessage());
                }

                @Override
                public void onNext(ResultData<Boolean> result) {
                    if (result.getResultCode() == 0) {// 成功
                        // 更新数据库
                        ConstantUtil.userDto.getUser().setuPointy(u_pointy);
                        ConstantUtil.userDto.getUser().setuPointx(u_pointx);
                        ConstantUtil.userDto.getUser().setuDomicile(u_domicile);
                        ConstantUtil.userDto.getUser().setuBirthday(u_birthday);
                        ConstantUtil.userDto.getUser().setuWeight(u_weight);
                        ConstantUtil.userDto.getUser().setuHeight(u_height);
                        ConstantUtil.userDto.getUser().setuSex(u_sex);
                        ConstantUtil.userDto.getUser().setuIntroduce(u_introduce);
                        ConstantUtil.userDto.getUser().setuNickname(u_nickname);

                        SQLiteDao sqLiteDao = new SQLiteDaoImpl(PrefectMeInfoActivity.this);
                        sqLiteDao.updateUser(ConstantUtil.userDto);
                        // 发送通信，更新界面
                        EventBus.getDefault().postSticky(new UpdateUserInfoSuccessEvent(true));
                        // 返回
                        setResult(RESULT_OK);
                        finish();
                    } else {// 失败
                        ToastUtil.shortToast(PrefectMeInfoActivity.this, result.getResultMsg());
                    }
                }

                @Override
                public void onStart() {
                    showProDialog();
                }
            };
            HttpMethods.getInstance().updateUserByUid(updateUserByUidSubscriber, ConstantUtil.userDto.getUser().getuId(),
                    u_nickname, u_sex, u_height, u_weight, u_birthday, u_domicile, u_pointx, u_pointy, u_introduce);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(updateUserByUidSubscriber);
        } else {// 当前网络不可用
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
    }

    /**
     * 验证昵称（该昵称是否可以使用）
     */
    private void validNicknameExist() {
        if (ConstantUtil.userDto == null || ConstantUtil.userDto.getUser() == null) {
            Intent intent_login = new Intent(PrefectMeInfoActivity.this, LoginActivity.class);
            startActivityForResult(intent_login, LOGIN_REQUEST_CODE);
            return;
        }
        if (NetworkUtil.isNetworkConnected(this)) {
            validNicknameExistSubscriber = new Subscriber<ResultData<Boolean>>() {
                @Override
                public void onCompleted() {
                    closeProDialog();
                }

                @Override
                public void onError(Throwable e) {
                    closeProDialog();
                    ToastUtil.shortToast(PrefectMeInfoActivity.this, "错误：" + e.getMessage());
                }

                @Override
                public void onNext(ResultData<Boolean> result) {
                    if (result.getResultCode() == 0) {// 成功
                        if (result.getData()) {
                            updateUserByUid();
                        } else
                            ToastUtil.shortToast(PrefectMeInfoActivity.this, "该昵称已被使用！");
                    } else {// 失败
                        ToastUtil.shortToast(PrefectMeInfoActivity.this, result.getResultMsg());
                    }
                }

                @Override
                public void onStart() {
                    showProDialog();
                }
            };
            HttpMethods.getInstance().validNicknameExist(validNicknameExistSubscriber, u_nickname);
            if (mSubscription == null)
                mSubscription = new CompositeSubscription();
            mSubscription.add(validNicknameExistSubscriber);
        } else {// 当前网络不可用
            ToastUtil.shortToast(this, "当前网络不给力！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_REQUEST_CODE:// 登录返回刷新数据
                    updateUserByUid();
                    break;
                case CHOOSE_ADDR_REQUESR_CODE:// 地址选择请求码
                    u_domicile = ConstantUtil.sCurrentAddress;
                    u_pointx = ConstantUtil.sPointx;
                    u_pointy = ConstantUtil.sPointy;
                    addrTv.setText(u_domicile);
                    break;
            }
        }
    }
}

