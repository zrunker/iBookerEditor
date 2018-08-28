package cc.ibooker.ibookereditor.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;

/**
 * 自定义Toast管理类
 *
 * @author 邹峰立
 */
public class ToastUtil {
    private static ArrayList<Toast> mDatas;

    /**
     * 展示自定义Toast - 除小米
     *
     * @param context  上下文对象
     * @param text     内容
     * @param duration 延长时间
     */
    public static void showDiyToast(Context context, String text, int duration) {
        Toast toast = new Toast(context);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView textView = view.findViewById(R.id.message);
        textView.setText(text);

        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 长吐司
     */
    public static void LongToast(Context ctx, String msg) {
        try {
            if (mDatas == null)
                mDatas = new ArrayList<>();
            for (Toast toast : mDatas) {
                toast.cancel();
                mDatas.remove(toast);
            }
            Toast toast = Toast.makeText(ctx, null, Toast.LENGTH_LONG);
            toast.setText(msg);
            toast.show();
            mDatas.add(toast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 短吐司
     */
    public static void shortToast(Context ctx, String msg) {
        try {
            if (mDatas == null)
                mDatas = new ArrayList<>();
            for (Toast toast : mDatas) {
                toast.cancel();
                mDatas.remove(toast);
            }
            Toast toast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
            if (TextUtils.isEmpty(msg))
                msg = "发生未知异常！";
            toast.setText(msg);
            toast.show();
            mDatas.add(toast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 带或不带图片的吐司
     *
     * @param msg   显示的内容
     * @param resId 显示的图片资源的id
     * @param which 显示方式 1.添加一个TextView和一个ImageView 2.只添加一个带drawable的TextView 3.不显示图片
     * @param locat 显示Toast位置
     */
    public static void diyToast(Context context, String msg, int resId, int which, int locat, int bottomDistance) {
        if (mDatas == null)
            mDatas = new ArrayList<>();
        for (Toast toast : mDatas) {
            toast.cancel();
            mDatas.remove(toast);
        }
        // 定义一个toast
        Toast toast = new Toast(context);
        // 定义一个TextView，显示提示信息，设置TextView文字居中，高度占满父控件
        TextView mTextView = new TextView(context);
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mTextView.setGravity(Gravity.CENTER_VERTICAL);// 设置文字居中
        mTextView.setPadding(40, 20, 40, 20);
        mTextView.setTextColor(Color.parseColor("#FFFFFF"));
        // 自定义一个LinearLayout设置背景布局
        LinearLayout mLinearLayout = new LinearLayout(context);
        mLinearLayout.setBackgroundResource(R.drawable.bg_toast);// toast_frame就是系统的toast的背景图片
        if (which == 1) {
            mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            // 设置imageView的属性
            ImageView mImageView = new ImageView(context);
            mImageView.setImageResource(resId);
            mImageView.setPadding(5, 5, 5, 5); // 设置imageView的间距
            mLinearLayout.addView(mImageView);
            mTextView.setText(msg);// 换行为了模拟文字居中
        } else if (which == 2) {
            mTextView.setText(msg);
            mTextView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        } else {
            mTextView.setText(msg);
        }
        mLinearLayout.addView(mTextView);
        mLinearLayout.invalidate();
        toast.setView(mLinearLayout);
        // 如果你想往右边移动，将第二个参数设为>0；往下移动，增大第三个参数；后两个参数都只得像素
        if (locat == Gravity.TOP
                || locat == Gravity.CENTER
                || locat == Gravity.BOTTOM
                || locat == Gravity.START
                || locat == Gravity.END) {
            toast.setGravity(locat, 0, bottomDistance);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        mDatas.add(toast);
    }

}
