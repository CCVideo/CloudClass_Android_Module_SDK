package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.common.utils.TimeUtil;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.NamedTime;


/**
 * 作者 ${CC视频}.<br/>
 */

public class NamedTimeAdapter extends BaseRecycleAdapter<NamedTimeAdapter.NamedTimeViewHolder, NamedTime> {

    public NamedTimeAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(NamedTimeViewHolder holder, int position) {
        NamedTime namedTime = mDatas.get(position);
        holder.mTimeValue.setText(TimeUtil.format(namedTime.getSeconds()));
        if (namedTime.isSelected()) {
            holder.mSelectedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.mSelectedIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.named_time_item_layout;
    }

    @Override
    public NamedTimeViewHolder getViewHolder(View itemView, int viewType) {
        return new NamedTimeViewHolder(itemView);
    }

    final class NamedTimeViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private ImageView mSelectedIcon;
        private  TextView mTimeValue;

        NamedTimeViewHolder(View itemView) {
            super(itemView);
            mSelectedIcon = itemView.findViewById(R.id.id_named_time_item_icon);
            mTimeValue = itemView.findViewById(R.id.id_named_time_item_value);
        }
    }

}
