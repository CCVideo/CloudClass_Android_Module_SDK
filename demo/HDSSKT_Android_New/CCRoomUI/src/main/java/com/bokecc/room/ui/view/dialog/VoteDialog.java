package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bokecc.sskt.base.bean.Vote;
import com.bokecc.common.utils.Tools;
import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 投票视图
 * @author wy
 */
public class VoteDialog extends CustomDialog {

    private final int optionUnselectedids[] = new int[]{
            R.mipmap.a_unselected, R.mipmap.b_unselected, R.mipmap.c_unselected,
            R.mipmap.d_unselected, R.mipmap.e_unselected
    };
    private final int optionSelectedids[] = new int[]{
            R.mipmap.a_selected, R.mipmap.b_selected, R.mipmap.c_selected,
            R.mipmap.d_selected, R.mipmap.e_selected
    };
    private final int judgeUnselectedids[] = new int[]{
            R.mipmap.r_unselected, R.mipmap.w_unselected
    };
    private final int judgeSelectedids[] = new int[]{
            R.mipmap.r_selected, R.mipmap.w_selected
    };

    /**回调*/
    private Context mContext;

    private OnVoteClickListener voteClickListener;

    private LinearLayout mSelectZone;
    private Button mCommit;
    private ImageButton mOptionViews[];
    private int[] optionUnselected;
    private int[] optionSelected;

    private boolean isSingle = true; // 是否是单选题默认是

    private ArrayList<Integer> mResults = new ArrayList<>();

    private Vote mVote;
    private boolean disableClick;

    public VoteDialog(Context context, Vote vote, OnVoteClickListener voteClickListener) {
        super(context);
        this.mContext = context;
        this.mVote = vote;
        this.isSingle = vote.getVoteType() == Vote.SINGLE;
        this.voteClickListener = voteClickListener;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_vote_layout);
        setCanceledOnTouchOutside(false);
        super.onCreateWrapContent(savedInstanceState);

        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    return true;
                }
                return false;
            }
        });

        mSelectZone = findViewById(R.id.id_vote_select_zone);
        findViewById(R.id.id_vote_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mCommit = findViewById(R.id.id_vote_commit);
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (voteClickListener != null) {
                        Collections.sort(mResults);//需要对答案排序
                        voteClickListener.onCommit(isSingle,mResults);
                    }
                    dismiss();
                }catch (Exception e){
                    Tools.showToast(e.getMessage());
                }
            }
        });

        setAnswerCount(mVote.getVoteCount());
    }
    public void disableInteractive(){
        disableClick = true;
        mCommit.setClickable(false);
    }

    /**
     * 设置选项
     * @param count
     */
    private void setAnswerCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        boolean isJudgment = count == 2;
        mSelectZone.removeAllViews();
        mOptionViews = new ImageButton[count];
        for (int i = 0; i < count; i++) {
            addChild4SelectZone(i, isJudgment);
        }
    }

    /**
     * 添加选项视图
     * @param index
     * @param isJudgment
     */
    private void addChild4SelectZone(int index, boolean isJudgment) {
        LinearLayout itemRoot = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        itemRoot.setOrientation(LinearLayout.VERTICAL);
        itemRoot.setLayoutParams(params);
        mSelectZone.addView(itemRoot);
        int resId;
        if (isJudgment) {
            optionSelected = judgeSelectedids;
            optionUnselected = judgeUnselectedids;
            resId = judgeUnselectedids[index];
        } else {
            optionSelected = optionSelectedids;
            optionUnselected = optionUnselectedids;
            resId = optionUnselectedids[index];
        }
        ImageButton item = new ImageButton(mContext);
        params = new LinearLayout.LayoutParams(Tools.dipToPixel(50), Tools.dipToPixel(50));
        params.gravity = Gravity.CENTER;
        item.setLayoutParams(params);
        item.setBackgroundResource(resId);
        item.setTag(index);
        item.setOnClickListener(new MyOptionClickListener());
        itemRoot.addView(item);
        mOptionViews[index] = item;
    }

    /**
     * 重置选项
     */
    private void resetOption() {
        mResults.clear();
        for (int i = 0; i < mOptionViews.length; i++) {
            mOptionViews[i].setBackgroundResource(optionUnselected[i]);
        }
    }

    /**
     * 选项点击监听
     */
    private class MyOptionClickListener implements View.OnClickListener {

        private boolean isSelected = false; // 是否被选中默认为选中

        MyOptionClickListener() {
        }

        @Override
        public void onClick(View v) {
            if(disableClick)return;
            if (isSingle && !isSelected) { // 单选 并且上一次的选择不是该选项
                resetOption();
            }
            if (isSelected) {
                mResults.remove(Integer.valueOf(String.valueOf(v.getTag())));
                v.setBackgroundResource(optionUnselected[(int) v.getTag()]);
            } else {
                mResults.add((Integer) v.getTag());
                v.setBackgroundResource(optionSelected[(int) v.getTag()]);
            }
            isSelected = !isSelected;
            if (mResults.isEmpty()) {
                mCommit.setEnabled(false);
            } else {
                mCommit.setEnabled(true);
            }
        }
    }


    /**监听*/
    public interface OnVoteClickListener {
        void onCommit(boolean isSingle,ArrayList<Integer> mResults);
    }

}