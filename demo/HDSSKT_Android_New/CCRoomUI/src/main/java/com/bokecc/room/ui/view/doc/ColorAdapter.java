package com.bokecc.room.ui.view.doc;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.ColorStatus;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;
import com.bokecc.room.ui.view.doc.ColorAdapter.ColorViewHolder;

/**
 * 作者 ${CC视频}.<br/>
 */

public class ColorAdapter extends BaseRecycleAdapter<ColorViewHolder, ColorStatus> {

    public ColorAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        holder.mColor.setSelected(getDatas().get(position).isSelected());
        holder.mColor.setBackgroundResource(getDatas().get(position).getResId());
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.draw_bubble_color_item;
    }

    @Override
    public ColorViewHolder getViewHolder(View itemView, int viewType) {
        return new ColorViewHolder(itemView);
    }

    final class ColorViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        ImageButton mColor;

        ColorViewHolder(View itemView) {
            super(itemView);
            mColor = itemView.findViewById(R.id.id_bubble_color);
        }
    }

}
