package cc.ibooker.ibookereditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.bean.LocalEntity;
import cc.ibooker.ibookereditor.ryviewholder.LocalViewHolder;

/**
 * 本地文章Adapter
 *
 * @author 邹峰立
 */
public class ALocalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<LocalEntity> mDatas;

    public ALocalAdapter(Context context, ArrayList<LocalEntity> list) {
        this.inflater = LayoutInflater.from(context);
        this.mDatas = list;
    }

    public void reflashData(ArrayList<LocalEntity> list) {
        this.mDatas = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocalViewHolder(inflater.inflate(R.layout.activity_main_ry_item_local, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((LocalViewHolder) holder).onBindData(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
