package cc.ibooker.ibookereditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.bean.ArticleUserData;
import cc.ibooker.ibookereditor.dto.FooterData;
import cc.ibooker.ibookereditor.ryviewholder.ARecommendViewHolder;
import cc.ibooker.ibookereditor.ryviewholder.FooterHolder;

/**
 * 推荐文章Adapter
 */
public class ARecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ONE = 1, TYPE_TWO = 2;
    private LayoutInflater inflater;
    private ArrayList<ArticleUserData> mDatas;

    private FooterData footerData;
    private int isFooter = 0;// 0没有底部，1有底部

    public ARecommendAdapter(Context context, ArrayList<ArticleUserData> list, FooterData footerData) {
        this.inflater = LayoutInflater.from(context);
        this.mDatas = list;
        if (footerData != null) {
            this.footerData = footerData;
            this.isFooter = 1;
        }
    }

    // 刷新数据
    public void reflashData(ArrayList<ArticleUserData> list) {
        this.mDatas = list;
        this.notifyDataSetChanged();
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
            case TYPE_ONE:
                viewHolder = new ARecommendViewHolder(inflater.inflate(R.layout.activity_main_ry_item_recommend, parent, false));
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
            case TYPE_ONE:
                ((ARecommendViewHolder) holder).onBindData(mDatas.get(position));
                break;
            case TYPE_TWO:
                ((FooterHolder) holder).bindHolder(footerData);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + isFooter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < mDatas.size())
            return TYPE_ONE;
        else
            return TYPE_TWO;
    }
}
