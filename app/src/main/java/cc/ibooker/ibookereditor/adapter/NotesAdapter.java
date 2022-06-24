package cc.ibooker.ibookereditor.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.bean.LocalEntity;
import cc.ibooker.ibookereditor.ryviewholder.LocalViewHolder;
import cc.ibooker.zrecyclerviewlib.BaseRvAdapter;
import cc.ibooker.zrecyclerviewlib.BaseViewHolder;

/**
 * 本地笔记Adapter
 *
 * @author 邹峰立
 */
public class NotesAdapter extends BaseRvAdapter<LocalEntity> {
    private int startPosition;

    public NotesAdapter(ArrayList<LocalEntity> list) {
        super(list);
        this.startPosition = getDataSize();
    }

    // 移除数据
    public void refreshRemoveItem(int positionStart, int itemCount) {
        if (itemCount > 1)
            notifyItemRangeRemoved(positionStart, itemCount);
        else
            notifyItemRemoved(positionStart);
        this.startPosition = getDataSize();
    }

    // 刷新Item
    public void refreshItems(int page, ArrayList<LocalEntity> list) {
        if (page <= 1)
            refreshData(list);
        else
            addAllItems(list);
    }

    // 刷新数据
    private void addAllItems(ArrayList<LocalEntity> list) {
        if (list != null)
            if (startPosition >= list.size())
                this.notifyDataSetChanged();
            else
                this.notifyItemRangeInserted(startPosition, list.size() - startPosition);
        // 重新标记位置
        this.startPosition = getDataSize();
    }

    @Override
    public BaseRvAdapter<?> removeItem2(int position) {
        super.removeItem2(position);
        this.startPosition = getDataSize();
        return this;
    }

    @Override
    public BaseRvAdapter<?> refreshData(ArrayList<LocalEntity> list) {
        if (list != null)
            this.startPosition = list.size();
        else
            this.startPosition = 0;
        return super.refreshData(list);
    }

    @Override
    public BaseViewHolder<?, ?> onCreateItemViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new LocalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_ry_item_local, viewGroup, false));
    }

    @Override
    public void onBindItemViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {
        ((LocalViewHolder) baseViewHolder).onBindData(getData().get(position), position);
    }

}
