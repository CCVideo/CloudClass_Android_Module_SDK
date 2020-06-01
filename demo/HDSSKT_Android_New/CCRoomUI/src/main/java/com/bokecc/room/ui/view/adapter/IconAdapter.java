package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.IconEntity;
import com.bumptech.glide.Glide;


/**
 * 作者 ${CC视频}.<br/>
 */

public class IconAdapter extends BaseRecycleAdapter<IconAdapter.IconViewHolder, IconEntity> {

    public IconAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(IconViewHolder holder, int position) {
        IconEntity iconEntity = getDatas().get(position);
        holder.mIconDes.setText(iconEntity.getResDes());
        Glide.with(mContext).load(iconEntity.getResId()).into(holder.mSrcIcon);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.item_icon_layout_ui;
    }

    @Override
    public IconViewHolder getViewHolder(View itemView, int viewType) {
        return new IconViewHolder(itemView);
    }

    final class IconViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private ImageView mSrcIcon;
        private TextView mIconDes;

        IconViewHolder(View itemView) {
            super(itemView);
            mSrcIcon = itemView.findViewById(R.id.id_icon_src);
            mIconDes = itemView.findViewById(R.id.id_icon_des);
        }
    }

}
