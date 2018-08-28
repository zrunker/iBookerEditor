package cc.ibooker.ibookereditor.ryviewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.activity.ArticleDetailActivity;
import cc.ibooker.ibookereditor.activity.IbookerEditorWebActivity;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditor.zglide.GlideCircleTransform;
import cc.ibooker.ibookereditor.zglide.GlideRoundRectTransform;

/**
 * 推荐文章ViewHolder
 */
public class ARecommendViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private View view;
    private ImageView picImg, identifyStateImg, acoverImg;
    private TextView nameTv, timeTv, labelTv, titleTv, abstractTv, browseNumTv, evalNumTv, zanNumTv;

    public ARecommendViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        view = itemView;
        picImg = itemView.findViewById(R.id.img_pic);
        identifyStateImg = itemView.findViewById(R.id.img_identify_state);
        nameTv = itemView.findViewById(R.id.tv_name);
        timeTv = itemView.findViewById(R.id.tv_time);
        acoverImg = itemView.findViewById(R.id.img_acover);
        labelTv = itemView.findViewById(R.id.tv_label);
        titleTv = itemView.findViewById(R.id.tv_title);
        abstractTv = itemView.findViewById(R.id.tv_abstract);
        browseNumTv = itemView.findViewById(R.id.tv_num_browse);
        evalNumTv = itemView.findViewById(R.id.tv_num_eval);
        zanNumTv = itemView.findViewById(R.id.tv_num_zan);
    }

    public void onBindData(final ArticleUserData data) {
        if (data != null) {
            GlideApp.with(context)
                    .load(data.getUser().getuPic())
                    .placeholder(R.drawable.icon_mepic)
                    .error(R.drawable.icon_mepic)
                    .transforms(new GlideCircleTransform())
                    .into(picImg);
            if ("1".equals(data.getUser().getuRealnameIdentifyState())) {
                identifyStateImg.setImageResource(R.drawable.icon_identify_state_ing);
                identifyStateImg.setVisibility(View.VISIBLE);
            } else if ("2".equals(data.getUser().getuRealnameIdentifyState())) {
                identifyStateImg.setImageResource(R.drawable.icon_identify_state_ed);
                identifyStateImg.setVisibility(View.VISIBLE);
            } else {
                identifyStateImg.setVisibility(View.GONE);
            }
            nameTv.setText(data.getUser().getuNickname());
            timeTv.setText(data.getaFormatPubtime());
            GlideApp.with(context)
                    .load(data.getaCoverPath())
                    .placeholder(R.drawable.img_picture)
                    .error(R.drawable.img_picture)
                    .centerCrop()
                    .transform(new GlideRoundRectTransform(20))
                    .into(acoverImg);
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
            titleTv.setText(data.getaTitle());
            abstractTv.setText(data.getaAbstract());
            browseNumTv.setText(MessageFormat.format("{0}", data.getaBrowsenum()));
            evalNumTv.setText("0");
            zanNumTv.setText(MessageFormat.format("{0}", data.getaZanCount()));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastClick()) return;
//                    Intent intent = new Intent(context, IbookerEditorWebActivity.class);
//                    intent.putExtra("aId", data.getaId());
//                    intent.putExtra("title", data.getaTitle());
//                    context.startActivity(intent);

                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("aId", data.getaId());
                    intent.putExtra("title", data.getaTitle());
                    context.startActivity(intent);
                }
            });
        }
    }
}
