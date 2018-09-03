package cc.ibooker.ibookereditor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.bean.SideMenuItem;

/**
 * 侧边栏Adapter
 * Created by 邹峰立 on 2018/3/27.
 */
public class SideMenuAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<SideMenuItem> mDatas;

    public SideMenuAdapter(Context context, ArrayList<SideMenuItem> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mDatas = list;
    }

    // 刷新数据
    public void reflashData(ArrayList<SideMenuItem> list) {
        this.mDatas = list;
        this.notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.layout_side_nav_bar_menu_item, parent, false);
            holder.textView = convertView.findViewById(R.id.textView);
            holder.img = convertView.findViewById(R.id.img);
            holder.view = convertView.findViewById(R.id.view);
            if (position == 0) {
                holder.textView.setPadding(dpToPx(20), dpToPx(20), holder.textView.getPaddingRight(), dpToPx(10));
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SideMenuItem data = mDatas.get(position);
        holder.textView.setText(data.getName());
        holder.img.setImageResource(data.getRes());
        if (position == 2)
            holder.view.setVisibility(View.VISIBLE);
        else
            holder.view.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        ImageView img;
        TextView textView;
        View view;
    }

    // dp to px
    private int dpToPx(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
