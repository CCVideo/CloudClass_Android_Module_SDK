package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.room.ui.R;


/**
 * 作者 ${CC视频}.<br/>
 */

public class StringSelectAdapter extends SelectAdapter<StringSelectAdapter.StringViewHolder, String> {

    public StringSelectAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.lianmai_item_layout_ui;
    }

    @Override
    public StringViewHolder getViewHolder(View itemView, int viewType) {
        return new StringViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StringViewHolder holder, int position) {
        holder.mTip.setText(mDatas.get(position));
        if (position == mSelPosition) {
            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mIcon.setImageResource(R.mipmap.choose_icon);
        } else {
            holder.mIcon.setVisibility(View.GONE);
        }
    }

    static class StringViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private TextView mTip;
        private ImageView mIcon;

        StringViewHolder(View itemView) {
            super(itemView);
            mTip = itemView.findViewById(R.id.id_lianmai_type);
            mIcon = itemView.findViewById(R.id.id_choose_icon);
        }
    }

}
