package cc.ibooker.ibookereditor.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.service.DownLoadService;
import cc.ibooker.zpopupwindowlib.ZPopupWindow;

/**
 * 下载图片PopupWindow
 */
public class DownLoadImgPopuwindow extends ZPopupWindow implements View.OnClickListener {
    private Context context;
    private String url;

    private DownLoadImgPopuwindow(Context context) {
        super(context);
        this.context = context;
    }

    public DownLoadImgPopuwindow(Context context, String url) {
        this(context);
        this.url = url;
    }

    @Override
    protected View generateCustomView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_imagepager_pwindow, null);
        TextView saveTv = rootView.findViewById(R.id.tv_save);
        saveTv.setOnClickListener(this);
        TextView cancelTv = rootView.findViewById(R.id.tv_cancel);
        cancelTv.setOnClickListener(this);
        return rootView;
    }

    // 点击事件监听
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:// 保存 - 启动服务进行下载
                Intent intent = new Intent(context, DownLoadService.class);
                intent.putExtra("url", url);
                context.startService(intent);

                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

}
