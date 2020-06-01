package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.BallotResult;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;

/**
 * 投票结果适配器
 * Created by wdh on 2018/6/14.
 */

public class BallotResultAdapter extends BaseRecycleAdapter<BallotResultAdapter
        .BallotItemViewHolder, BallotResult.choices> {

    public BallotResultAdapter(Context context) {
        super(context);
    }
    private static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }
    @Override
    public void onBindViewHolder(BallotItemViewHolder holder, int position) {
        if(mDatas.size() == 0 || Config.mBallotContent.size() == 0)
            return;
        BallotResult.choices choices = mDatas.get(position);
        int percent = 0;
        String str = "";
        if(Config.isBallot){
            str = Config.mBallotContent.get(position);
            percent = Integer.valueOf(choices.getContent());
            float  num = (float) percent/ Config.mBallotNum*100;
            holder.mOptionBar.setProgress((int)num);
        } else {
            str = Config.mBallotContent.get(position);
            percent = Integer.valueOf(choices.getContent());
            float  num = (float) percent/ Config.mBallotNum*100;
            holder.mOptionBar.setProgress((int)num);
        }
//        holder.mOptionSelectedNum.setText(": " + Config.mBallotContent.get(position));
        holder.mBallotResult.setText(percent + "票");
        switch (position) {
            case 0:
                holder.mOptionSelectedNum.setText(" A: " + str);
                break;
            case 1:
                holder.mOptionSelectedNum.setText(" B: " + str);
                break;
            case 2:
                holder.mOptionSelectedNum.setText(" C: " + str);
                break;
            case 3:
                holder.mOptionSelectedNum.setText(" D: " + str);
                break;
            case 4:
                holder.mOptionSelectedNum.setText(" E: " + str);
                break;
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.dialog_ballot_result_item;
    }

    @Override
    public BallotItemViewHolder getViewHolder(View itemView, int viewType) {
        return new BallotItemViewHolder(itemView);
    }

    final class BallotItemViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        TextView mOptionSelectedNum;
        ProgressBar mOptionBar;
        TextView mBallotResult;

        BallotItemViewHolder(View itemView) {
            super(itemView);
            mOptionSelectedNum = itemView.findViewById(R.id.id_ballot_result_item_num);
            mOptionBar = itemView.findViewById(R.id.id_ballot_result_item_pb);
            mBallotResult = itemView.findViewById(R.id.id_ballot_result);
        }
    }

}
