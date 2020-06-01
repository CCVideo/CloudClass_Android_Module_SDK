package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.VoteResult;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;

/**
 * 作者 ${CC视频}.<br/>
 */

public class VoteResultAdapter extends BaseRecycleAdapter<VoteResultAdapter.VoteItemViewHolder, VoteResult.Statisic> {

    public VoteResultAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(VoteItemViewHolder holder, int position) {
        VoteResult.Statisic statisic = mDatas.get(position);
        int percent = Integer.valueOf(statisic.getPercent());
        holder.mOptionBar.setProgress(percent);
        holder.mOptionSelectedNum.setText(statisic.getCount() + "人(" + statisic.getPercent() + "%)");
        switch (statisic.getOption()) {
            case 0:
                if (mDatas.size() == 2) {
                    holder.mOptionIcon.setImageResource(R.mipmap.r_tip);
                } else {
                    holder.mOptionIcon.setImageResource(R.mipmap.a_tip);
                }
                break;
            case 1:
                if (mDatas.size() == 2) {
                    holder.mOptionIcon.setImageResource(R.mipmap.w_tip);
                } else {
                    holder.mOptionIcon.setImageResource(R.mipmap.b_tip);
                }
                break;
            case 2:
                holder.mOptionIcon.setImageResource(R.mipmap.c_tip);
                break;
            case 3:
                holder.mOptionIcon.setImageResource(R.mipmap.d_tip);
                break;
            case 4:
                holder.mOptionIcon.setImageResource(R.mipmap.e_tip);
                break;
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.dialog_vote_result_item;
    }

    @Override
    public VoteItemViewHolder getViewHolder(View itemView, int viewType) {
        return new VoteItemViewHolder(itemView);
    }

    final class VoteItemViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        ImageView mOptionIcon;
        TextView mOptionSelectedNum;
        ProgressBar mOptionBar;

        VoteItemViewHolder(View itemView) {
            super(itemView);
            mOptionIcon = itemView.findViewById(R.id.id_vote_result_item_icon);
            mOptionSelectedNum = itemView.findViewById(R.id.id_vote_result_item_num);
            mOptionBar = itemView.findViewById(R.id.id_vote_result_item_pb);
        }
    }

}
