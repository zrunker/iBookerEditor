package cc.ibooker.ibookereditor.ryviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.bean.UserEntity;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.zglide.GlideApp;
import cc.ibooker.ibookereditor.zglide.GlideCircleTransform;

/**
 * 个人中心头部信息
 */
public class MeInfoHeaderHolder extends RecyclerView.ViewHolder {
    private ImageView meInfoImg;
    private TextView nickNameTv, introduceTv;

    public MeInfoHeaderHolder(View itemView) {
        super(itemView);
        meInfoImg = itemView.findViewById(R.id.img_mepic);
        nickNameTv = itemView.findViewById(R.id.tv_nickname);
        introduceTv = itemView.findViewById(R.id.tv_introduce);
    }

    public void onBind() {
        if (ConstantUtil.userDto != null && ConstantUtil.userDto.getUser() != null) {
            UserEntity userEntity = ConstantUtil.userDto.getUser();
            GlideApp.with(itemView.getContext())
                    .load(userEntity.getuPic())
                    .placeholder(R.drawable.icon_mepic)
                    .error(R.drawable.icon_mepic)
                    .transforms(new GlideCircleTransform())
                    .into(meInfoImg);
            nickNameTv.setText(userEntity.getuNickname());
            introduceTv.setText(userEntity.getuIntroduce());
        }
    }
}
