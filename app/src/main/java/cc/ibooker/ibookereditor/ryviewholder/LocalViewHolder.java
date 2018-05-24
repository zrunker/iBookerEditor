package cc.ibooker.ibookereditor.ryviewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.activity.EditArticleActivity;
import cc.ibooker.ibookereditor.bean.LocalEntity;
import cc.ibooker.ibookereditor.utils.ClickUtil;

/**
 * 本地文章ViewHolder
 * <p>
 * Created by 邹峰立 on 2018/3/27 0027.
 */
public class LocalViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private View view;
    private TextView titleTv, sizeTv, timeTv;

    public LocalViewHolder(View itemView) {
        super(itemView);
        this.context = itemView.getContext();
        this.view = itemView;
        this.timeTv = itemView.findViewById(R.id.tv_time);
        this.titleTv = itemView.findViewById(R.id.tv_title);
        this.sizeTv = itemView.findViewById(R.id.tv_size);
    }

    public void onBindData(final LocalEntity data) {
        if (data != null) {
            timeTv.setText(data.getaFormatTime());
            titleTv.setText(data.getaTitle());
            sizeTv.setText(data.getaFormatSize());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastClick()) return;
                    // 进入编辑界面
                    Intent intent = new Intent(context, EditArticleActivity.class);
                    intent.putExtra("title", data.getaTitle());
                    intent.putExtra("filePath", data.getaFilePath());
                    intent.putExtra("_id", data.getaId());
                    intent.putExtra("createTime", data.getaTime());
                    context.startActivity(intent);
                }
            });
        }
    }
}
