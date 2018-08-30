package cc.ibooker.ibookereditor.jsevent;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.activity.ImgVPagerActivity;

/**
 * 书客编辑器 - 查看图片js事件
 * Created by 邹峰立 on 2018/3/13.
 */
public class IbookerEditorJsCheckImgsEvent {
    private Context context;
    private ArrayList<String> mImgPathList;

    public ArrayList<String> getmImgPathList() {
        return mImgPathList;
    }

    public void setmImgPathList(ArrayList<String> mImgPathList) {
        this.mImgPathList = mImgPathList;
    }

    public IbookerEditorJsCheckImgsEvent(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void onCheckImg(String currentPath) {
        // 执行相应的逻辑操作-查看图片
        int position = 0;
        if (mImgPathList != null) {
            for (int i = 0; i < mImgPathList.size(); i++) {
                String imgPath = mImgPathList.get(i);
                if (imgPath.equals(currentPath)) {
                    position = i;
                    break;
                }
            }
        }
        // 跳转查看图片界面
        Intent intent = new Intent(context, ImgVPagerActivity.class);
        intent.putExtra("currentPath", currentPath);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("imgAllPathList", mImgPathList);
        context.startActivity(intent);
    }
}
