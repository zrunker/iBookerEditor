package cc.ibooker.ibookereditor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;

/**
 * 本地Dialog操作Adapter
 */
public class LocalOperDialogLvAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> mDatas = new ArrayList<>();

    public LocalOperDialogLvAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        if (mDatas == null)
            mDatas = new ArrayList<>();
        mDatas.add(context.getString(R.string.share));
        mDatas.add(context.getString(R.string.delete));
        mDatas.add(context.getString(R.string.cancel));
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_dialog_local_oper_item, parent, false);
            holder.textView = convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String text = mDatas.get(position);
        holder.textView.setText(text);
        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}
