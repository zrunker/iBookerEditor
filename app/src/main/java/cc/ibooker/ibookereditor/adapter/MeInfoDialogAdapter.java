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
 * 个人中心喜欢长按Dialog Adapter
 */
public class MeInfoDialogAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> mDatas = new ArrayList<>();

    public MeInfoDialogAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        mDatas.add("查看详情");
        mDatas.add("取消喜欢");
        mDatas.add("取消");
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
            convertView = inflater.inflate(R.layout.activity_meinfo_article_like_dialog_item, parent, false);
            holder.textView = convertView.findViewById(R.id.textView);
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