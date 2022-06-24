package cc.ibooker.ibookereditor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.dto.RvEmptyData;
import cc.ibooker.zrecyclerviewlib.BaseRvEmptyView;

/**
 * Rv列表空页面
 *
 * @author 邹峰立
 */
public class RvEmptyView extends BaseRvEmptyView<RvEmptyData> {
    private final Context context;
    private ImageView imgState;
    private TextView tvReload, tvStateTip;

    public RvEmptyView(Context context, RvEmptyData data) {
        super(context, data);
        this.context = context;
    }

    @Override
    public View createEmptyView(Context context) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.layout_result_state, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imgState = view.findViewById(R.id.img_state);
        tvReload = view.findViewById(R.id.tv_reload);
        tvStateTip = view.findViewById(R.id.tv_state_tip);
        return view;
    }

    @Override
    public void refreshEmptyView(RvEmptyData data) {
        if (data != null) {
            getEmptyView().setVisibility(View.VISIBLE);
            int statue = data.getStatue();
            String tip = data.getTip();
            if (!TextUtils.isEmpty(tip))
                tvStateTip.setText(tip);
            String operText = data.getOperText();
            if (!TextUtils.isEmpty(operText))
                tvReload.setText(operText);
            switch (statue) {
                case -5:// 无网络
                    imgState.setImageResource(R.drawable.img_load_error);
                    if (TextUtils.isEmpty(tip))
                        tvStateTip.setText(context.getResources().getString(R.string.nonet_tip));
                    break;
                case -2:// 失败
                case 3:// 查询失败
                case 1:// 传入值错误
                case 4:// 更新操作失败
                case 5:// 保存数据失败
                    imgState.setImageResource(R.drawable.img_load_failed);
                    break;
                case 2:// 数据为空
                    imgState.setImageResource(R.drawable.img_load_empty);
                    if (TextUtils.isEmpty(tip))
                        tvStateTip.setText(context.getResources().getString(R.string.no_data));
                    break;
                case 0:// 成功
                    imgState.setImageResource(R.drawable.img_load_success);
                    break;
                case -100:// 隐藏
                    getEmptyView().setVisibility(View.GONE);
                    break;
                case -1:// 服务端异常
                default:
                    imgState.setImageResource(R.drawable.img_load_error);
                    break;
            }
        }

    }
}
