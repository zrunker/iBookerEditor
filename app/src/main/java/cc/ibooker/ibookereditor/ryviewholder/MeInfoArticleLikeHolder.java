package cc.ibooker.ibookereditor.ryviewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.activity.ArticleDetailActivity;
import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditor.zglide.GlideRoundRectTransform;

public class MeInfoArticleLikeHolder extends RecyclerView.ViewHolder {
    private Context context;
    private View view;
    private ImageView acoverImg;
    private TextView labelTv, titleTv, abstractTv;

    public MeInfoArticleLikeHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        view = itemView;
        acoverImg = itemView.findViewById(R.id.img_acover);
        labelTv = itemView.findViewById(R.id.tv_label);
        titleTv = itemView.findViewById(R.id.tv_title);
        abstractTv = itemView.findViewById(R.id.tv_abstract);
    }

    public void onBindData(final ArticleAppreciateData data) {
        if (data != null) {
            GlideApp.with(context)
                    .load(data.getaCoverPath())
                    .placeholder(R.drawable.img_picture)
                    .error(R.drawable.img_picture)
                    .centerCrop()
                    .transform(new GlideRoundRectTransform(20))
                    .into(acoverImg);
            if (data.getaBookType() != null && data.getaBookTypeSecond() != null) {
                labelTv.setText(String.format("%s·%s", data.getaBookType().getAtName(), data.getaBookTypeSecond().getAtsName()));
                if ("0".equals(data.getaStyle())) {
                    labelTv.setTextColor(view.getResources().getColor(R.color.labelGreen));
                    labelTv.setBackgroundResource(R.drawable.bg_label_green);
                } else if ("1".equals(data.getaStyle())) {
                    labelTv.setTextColor(view.getResources().getColor(R.color.labelBlue));
                    labelTv.setBackgroundResource(R.drawable.bg_label_blue);
                } else {
                    labelTv.setTextColor(view.getResources().getColor(R.color.labelOrange));
                    labelTv.setBackgroundResource(R.drawable.bg_label_orange);
                }
                labelTv.setVisibility(View.VISIBLE);
            } else {
                labelTv.setVisibility(View.GONE);
            }
            titleTv.setText(data.getaTitle());
            abstractTv.setText(data.getaAbstract());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastClick()) return;
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("aId", data.getAaAid());
                    intent.putExtra("title", data.getaTitle());
                    context.startActivity(intent);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {// 长按事件

                    return true;
                }
            });
        }
    }
}
