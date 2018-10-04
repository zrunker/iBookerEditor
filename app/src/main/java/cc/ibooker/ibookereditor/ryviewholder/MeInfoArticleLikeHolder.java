package cc.ibooker.ibookereditor.ryviewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.activity.ArticleDetailActivity;
import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.event.MeInfoArticleLikeLongClickEvent;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditor.zglide.GlideRoundRectTransform;

public class MeInfoArticleLikeHolder extends RecyclerView.ViewHolder {
    private Context context;
    private View view;
    private ImageView acoverImg;
    private TextView pubtimeTv, titleTv, abstractTv;

    public MeInfoArticleLikeHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        view = itemView;
        acoverImg = itemView.findViewById(R.id.img_acover);
        pubtimeTv = itemView.findViewById(R.id.tv_pubtime);
        titleTv = itemView.findViewById(R.id.tv_title);
        abstractTv = itemView.findViewById(R.id.tv_abstract);
    }

    public void onBindData(final ArticleAppreciateData data, final int position) {
        if (data != null) {
            GlideApp.with(context)
                    .load(data.getaCoverPath())
                    .placeholder(R.drawable.img_picture)
                    .error(R.drawable.img_picture)
                    .centerCrop()
                    .transform(new GlideRoundRectTransform(5))
                    .into(acoverImg);
            pubtimeTv.setText(data.getAaFormatTime());
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
                    EventBus.getDefault().postSticky(new MeInfoArticleLikeLongClickEvent(position, data));
                    return true;
                }
            });
        }
    }
}
