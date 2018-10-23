package cc.ibooker.ibookereditor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;

/**
 * POI列表Adapter
 * Created by 邹峰立 on 2017/3/2 0002.
 */
public class AmapLocationPOIAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PoiItem> mDatas;
    private LayoutInflater inflater;

    public AmapLocationPOIAdapter(Context context, ArrayList<PoiItem> list) {
        this.context = context;
        this.mDatas = list;
        this.inflater = LayoutInflater.from(context);
    }

    // 刷新数据
    public void reflashData(ArrayList<PoiItem> list) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_amaplocation_poi_lv_item, parent, false);
            viewHolder.titleTv = convertView.findViewById(R.id.tv_title);
            viewHolder.descTv = convertView.findViewById(R.id.tv_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PoiItem poiItem = mDatas.get(position);
        viewHolder.titleTv.setText(poiItem.getTitle());
        viewHolder.descTv.setText(poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getDirection() + poiItem.getSnippet());
        return convertView;
    }

    private static class ViewHolder {
        TextView titleTv, descTv;
    }
}
