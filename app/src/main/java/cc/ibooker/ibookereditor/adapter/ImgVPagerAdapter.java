package cc.ibooker.ibookereditor.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cc.ibooker.ibookereditorlib.IbookerEditorScaleImageView;

/**
 * 自定义图片缩放Adapter
 * <p>
 * Created by 邹峰立 on 2018/3/14.
 */
public class ImgVPagerAdapter extends PagerAdapter {
    private ArrayList<IbookerEditorScaleImageView> mDatas;

    public ImgVPagerAdapter(ArrayList<IbookerEditorScaleImageView> list) {
        this.mDatas = list;
    }

    // 刷新
    public void reflashData(ArrayList<IbookerEditorScaleImageView> list) {
        this.mDatas = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        IbookerEditorScaleImageView scaleImageView = mDatas.get(position);
        container.addView(scaleImageView);
        return scaleImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        container.removeView(mDatas.get(position));
    }
}
