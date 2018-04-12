package cc.ibooker.ibookereditor.ryviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.dto.FooterData;


/**
 * RecyclerView底部ViewHolder
 * Created by 邹峰立 on 2017/4/30 0030.
 */
public class FooterHolder extends RecyclerView.ViewHolder {
    private View view;
    private LinearLayout footerLayout;
    private ProgressBar progressBar;
    private TextView textView;

    public FooterHolder(View itemView) {
        super(itemView);
        view = itemView;
        footerLayout = itemView.findViewById(R.id.load_layout);
        progressBar = itemView.findViewById(R.id.footer_progressbar);
        textView = itemView.findViewById(R.id.footer_tip);
    }

    public void bindHolder(FooterData footerData) {
        if (footerData != null) {
            view.setVisibility(View.VISIBLE);
            if (footerData.isShowFooter()) {
                footerLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(footerData.isShowProgressBar() ? View.VISIBLE : View.GONE);
                textView.setText(footerData.getTitle());
            } else {
                footerLayout.setVisibility(View.GONE);
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
