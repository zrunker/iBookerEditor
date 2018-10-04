package cc.ibooker.ibookereditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.bean.ArticleAppreciateData;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.ryviewholder.FooterHolder;
import cc.ibooker.ibookereditor.ryviewholder.MeInfoArticleLikeHolder;
import cc.ibooker.ibookereditor.ryviewholder.MeInfoHeaderHolder;

/**
 * 个人中心 - 喜欢文章列表Adapter
 */
public class MeInfoArticleLikeAdapter extends RecyclerView.Adapter {
    private final int TYPE_HEADER = 0, TYPE_ONE = 1, TYPE_TWO = 2;
    private LayoutInflater inflater;
    private ArrayList<ArticleAppreciateData> mDatas;

    private FooterData footerData;
    private int isFooter = 0;// 0没有底部，1有底部

    public MeInfoArticleLikeAdapter(Context context, ArrayList<ArticleAppreciateData> list, FooterData footerData) {
        this.inflater = LayoutInflater.from(context);
        this.mDatas = list;
        if (footerData != null) {
            this.footerData = footerData;
            this.isFooter = 1;
        }
    }

    // 刷新数据
    public void reflashData(ArrayList<ArticleAppreciateData> list) {
        this.mDatas = list;
        this.notifyDataSetChanged();
    }

    // 删除数据
    public void removeData(int position) {
        mDatas.remove(position - 1);
        // 删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // 刷新底部
    public void updateFooterView(FooterData footerData) {
        this.footerData = footerData;
        this.notifyItemChanged(getItemCount() - 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEADER:
                viewHolder = new MeInfoHeaderHolder(inflater.inflate(R.layout.activity_meinfo_article_like_header, parent, false));
                break;
            case TYPE_ONE:
                viewHolder = new MeInfoArticleLikeHolder(inflater.inflate(R.layout.activity_meinfo_article_like_item, parent, false));
                break;
            case TYPE_TWO:
                viewHolder = new FooterHolder(inflater.inflate(R.layout.layout_footer, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_HEADER:
                ((MeInfoHeaderHolder) holder).onBind();
                break;
            case TYPE_ONE:
                ((MeInfoArticleLikeHolder) holder).onBindData(mDatas.get(position - 1), position);
                break;
            case TYPE_TWO:
                ((FooterHolder) holder).bindHolder(footerData);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + isFooter + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position > 0 && position <= mDatas.size())
            return TYPE_ONE;
        else
            return TYPE_TWO;
    }
}
