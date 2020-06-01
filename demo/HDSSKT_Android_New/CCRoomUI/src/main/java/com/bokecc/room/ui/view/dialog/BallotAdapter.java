package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;
import com.bokecc.room.ui.view.adapter.SelectAdapter;

/**
 * 投票适配器
 * Created by CC视频 on 2018/6/6.
 */

public class BallotAdapter extends SelectAdapter<BallotAdapter.BallotItemViewHolder, Ballot.Statisic> {

    private OnSelectOnClickListener mOnSelectOnClickListener;
    public BallotAdapter(Context context) {
        super(context);
    }
    private static String ToSBC(String src) {
        char c[] = src.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }
    private Drawable img;
    @Override
    public void onBindViewHolder(BallotItemViewHolder holder, int position) {
        Ballot.Statisic statisic = mDatas.get(position);
        Config.mBallotContent.add(statisic.getContent());
        Resources res = this.mContext.getResources();
        if(Config.mSeleteType == Ballot.SINGLE){
            if(mSelPosition == position){
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageResource(R.mipmap.choose_icon);
                holder.mBallotItem.setBackgroundColor(Color.parseColor("#33F27C19"));
            } else {
                holder.mIcon.setVisibility(View.GONE);
                holder.mBallotItem.setBackgroundColor(Color.parseColor("#CCCCCC"));
            }
        } else {
            if(mSelPosition == position) {
                if (holder.mIcon.getVisibility() == View.GONE) {
                    holder.mIcon.setVisibility(View.VISIBLE);
                    holder.mIcon.setImageResource(R.mipmap.choose_icon);
                    holder.mBallotItem.setBackgroundColor(Color.parseColor("#33F27C19"));
                } else {
                    holder.mIcon.setVisibility(View.GONE);
                    holder.mBallotItem.setBackgroundColor(Color.parseColor("#CCCCCC"));
                }
            }
            if(mSelPosition == -1){
                if (holder.mIcon.getVisibility() == View.VISIBLE) {
                    holder.mIcon.setVisibility(View.GONE);
                    holder.mBallotItem.setBackgroundColor(Color.parseColor("#CCCCCC"));
                }
            }
        }

        String str = statisic.getContent();
        switch (position) {
            case 0:
                holder.mBallotItemNum.setText(" A: " + str);
                break;
            case 1:
                holder.mBallotItemNum.setText(" B: " + str);
                break;
            case 2:
                holder.mBallotItemNum.setText(" C: " + str);
                break;
            case 3:
                holder.mBallotItemNum.setText(" D: " + str);
                break;
            case 4:
                holder.mBallotItemNum.setText(" E: " + str);
                break;
        }
        holder.mIcon.setOnClickListener(new SelectOnClicklistener(holder,position, statisic));
//        holder.mBallotIcon.setImageResource(R.drawable.circle_empty);
    }

    public void setOnSelectOnClickListener(OnSelectOnClickListener onSelectOnClickListener) {
        mOnSelectOnClickListener = onSelectOnClickListener;
    }

    public interface OnSelectOnClickListener {
        void onSelect(int position, Ballot.Statisic statisic);
    }

    private class SelectOnClicklistener implements View.OnClickListener {

        private int mPosition;
        private Ballot.Statisic mStatisic;
        private BallotItemViewHolder mHolder;

        SelectOnClicklistener(BallotItemViewHolder holder,int position, Ballot.Statisic statisic) {
            mPosition = position;
            mStatisic = statisic;
            mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            if (mOnSelectOnClickListener != null) {
//                mHolder.mBallotIcon.setImageResource(R.drawable.circle_solid);
                mOnSelectOnClickListener.onSelect(mPosition, mStatisic);
            }
        }
    }
    @Override
    public int getItemView(int viewType) {
        return R.layout.dialog_ballot_item_layout;
    }

    @Override
    public BallotItemViewHolder getViewHolder(View itemView, int viewType) {
        return new BallotItemViewHolder(itemView);
    }

    final class BallotItemViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        RelativeLayout mBallotItem;
        ImageView mBallotItemIcon;
        TextView mBallotItemNum;
        ImageView mIcon;

        BallotItemViewHolder(View itemView) {
            super(itemView);
            mBallotItem = itemView.findViewById(R.id.id_ballot_item);
            mBallotItemIcon = itemView.findViewById(R.id.id_ballot_item_icon);
            mBallotItemNum = itemView.findViewById(R.id.id_ballot_item_num);
            mIcon = itemView.findViewById(R.id.id_ballot_choose_icon);
        }
    }
}
