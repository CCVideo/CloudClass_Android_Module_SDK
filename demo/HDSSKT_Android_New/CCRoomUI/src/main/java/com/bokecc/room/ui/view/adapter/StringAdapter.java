package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bokecc.room.ui.R;


/**
 * 作者 ${CC视频}.<br/>
 */

public class StringAdapter extends BaseRecycleAdapter<StringAdapter.StringViewHolder, String> {

    private int mIndex = -1;
    private int mColor;

    public StringAdapter(Context context) {
        super(context);
    }

    public void setIndexColor(int index, int color) {
        if (index < 0 || index >= mDatas.size()) {
            return;
        }
        mIndex = index;
        mColor = color;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(StringViewHolder holder, int position) {
        if (mIndex == position) {
            holder.mTip.setTextColor(mColor);
        } else {
            holder.mTip.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
        holder.mTip.setText(mDatas.get(position));
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.choose_item_layout_ui;
    }

    @Override
    public StringViewHolder getViewHolder(View itemView, int viewType) {
        return new StringViewHolder(itemView);
    }

    public void update(int index, String value) {
        mDatas.set(index, value);
        notifyDataSetChanged();
    }

    final class StringViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private TextView mTip;

        StringViewHolder(View itemView) {
            super(itemView);
            mTip = itemView.findViewById(R.id.id_choose_tip);
        }
    }

}
