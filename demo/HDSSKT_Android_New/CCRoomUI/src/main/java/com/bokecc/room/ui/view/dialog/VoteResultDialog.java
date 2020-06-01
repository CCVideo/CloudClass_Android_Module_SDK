package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.Vote;
import com.bokecc.sskt.base.bean.VoteResult;
import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 投票结果视图
 * @author wy
 */
public class VoteResultDialog extends CustomDialog {

    private final int rightZimuIcons[] = new int[]{
            R.mipmap.a_right_icon,
            R.mipmap.b_right_icon,
            R.mipmap.c_right_icon,
            R.mipmap.d_right_icon,
            R.mipmap.e_right_icon
    };
    private final int wrongZimuIcons[] = new int[]{
            R.mipmap.a_wrong_icon,
            R.mipmap.b_wrong_icon,
            R.mipmap.c_wrong_icon,
            R.mipmap.d_wrong_icon,
            R.mipmap.e_wrong_icon
    };
    private final int rightIcons[] = new int[]{
            R.mipmap.gou_right_icon,
            R.mipmap.cha_right_icon,
    };
    private final int wrongIcons[] = new int[]{
            R.mipmap.gou_wrong_icon,
            R.mipmap.cha_wrong_icon,
    };

    private Context mContext;

    private LinearLayout mSelfAnswerLayout, mRightAnswerLayout;
    private ImageView mSelfImgs[], mRightImgs[];
    private TextView mAnswerCount, mSelfTip;

    private VoteResultAdapter mVoteResultAdapter;

    private VoteResult voteResult;
    private ArrayList<Integer> results;


    public VoteResultDialog(Context context, VoteResult voteResult,ArrayList<Integer> results) {
        super(context);
        this.mContext = context;
        this.voteResult = voteResult;
        this.results = results;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_vote_result_layout);
        setCanceledOnTouchOutside(false);
        super.onCreateWrapContent(savedInstanceState);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    return true;
                }
                return false;
            }
        });

        mAnswerCount = findViewById(R.id.id_vote_result_statistics);
        RecyclerView resultContent = findViewById(R.id.id_vote_result_content);
        mSelfTip = findViewById(R.id.id_vote_result_self);
        mSelfAnswerLayout = findViewById(R.id.id_vote_result_self_layout);
        mRightAnswerLayout = findViewById(R.id.id_vote_result_right_layout);
        ImageView selfImg1 = findViewById(R.id.id_vote_result_self_img1);
        ImageView selfImg2 = findViewById(R.id.id_vote_result_self_img2);
        ImageView selfImg3 = findViewById(R.id.id_vote_result_self_img3);
        ImageView selfImg4 = findViewById(R.id.id_vote_result_self_img4);
        ImageView selfImg5 = findViewById(R.id.id_vote_result_self_img5);
        ImageView rightImg1 = findViewById(R.id.id_vote_result_right_img1);
        ImageView rightImg2 = findViewById(R.id.id_vote_result_right_img2);
        ImageView rightImg3 = findViewById(R.id.id_vote_result_right_img3);
        ImageView rightImg4 = findViewById(R.id.id_vote_result_right_img4);
        ImageView rightImg5 = findViewById(R.id.id_vote_result_right_img5);
        mSelfImgs = new ImageView[]{
                selfImg1, selfImg2, selfImg3, selfImg4, selfImg5
        };
        mRightImgs = new ImageView[]{
                rightImg1, rightImg2, rightImg3, rightImg4, rightImg5
        };

        resultContent.setLayoutManager(new LinearLayoutManager(mContext));
        mVoteResultAdapter = new VoteResultAdapter(mContext);
        resultContent.setAdapter(mVoteResultAdapter);

        findViewById(R.id.id_vote_reslut_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        showAnswer();
    }

    private void showAnswer(){
        mAnswerCount.setText("答题结束，共有" + voteResult.getAnswerCount() + "人回答");
        if (voteResult.getCorrectOptionSingle() == -1) {
            mRightAnswerLayout.setVisibility(View.GONE);
        } else {
            mRightAnswerLayout.setVisibility(View.VISIBLE);
            for (ImageView mRightImg : mRightImgs) {
                mRightImg.setVisibility(View.GONE);
            }
            if (voteResult.getStatisics().size() == 2) {
                mRightImgs[0].setVisibility(View.VISIBLE);
                mRightImgs[0].setImageResource(rightIcons[voteResult.getCorrectOptionSingle()]);
            } else {
                int[] options = voteResult.getCorrectOptionMul();
                if (options == null || options.length == 0) { // 单选
                    mRightImgs[0].setVisibility(View.VISIBLE);
                    mRightImgs[0].setImageResource(rightZimuIcons[voteResult.getCorrectOptionSingle()]);
                } else {
                    for (int i = 0; i < options.length; i++) { // 多选
                        mRightImgs[i].setVisibility(View.VISIBLE);
                        mRightImgs[i].setImageResource(rightZimuIcons[options[i]]);
                    }
                }
            }
        }
        if (results == null || results.isEmpty()) {
            mSelfAnswerLayout.setVisibility(View.GONE);
        } else {
            mSelfAnswerLayout.setVisibility(View.VISIBLE);
            for (ImageView mSelfImg : mSelfImgs) {
                mSelfImg.setVisibility(View.GONE);
            }
            boolean isZimu = voteResult.getStatisics().size() != 2;
            boolean isRight = true;
            if (voteResult.getVoteType() == Vote.SINGLE) { // 单选
                if (results.get(0) != voteResult.getCorrectOptionSingle()) {
                    isRight = false;
                }
            } else {
                if (voteResult.getCorrectOptionMul() != null && results.size() == voteResult.getCorrectOptionMul().length) {
                    Collections.sort(results);//需要对答案排序
                    for (int i = 0; i < results.size(); i++) {
                        if (results.get(i) != voteResult.getCorrectOptionMul()[i]) {
                            isRight = false;
                        }
                    }
                } else {
                    isRight = false;
                }
            }
            if (isRight) {
                mSelfTip.setTextColor(Color.parseColor("#4bbd3f"));
            } else {
                mSelfTip.setTextColor(Color.parseColor("#e53f28"));
            }
            for (int i = 0; i < results.size(); i++) {
                mSelfImgs[i].setVisibility(View.VISIBLE);
                if (isZimu) {
                    if (isRight) {
                        mSelfImgs[i].setImageResource(rightZimuIcons[results.get(i)]);
                    } else {
                        mSelfImgs[i].setImageResource(wrongZimuIcons[results.get(i)]);
                    }
                } else {
                    if (isRight) {
                        mSelfImgs[i].setImageResource(rightIcons[results.get(i)]);
                    } else {
                        mSelfImgs[i].setImageResource(wrongIcons[results.get(i)]);
                    }
                }
            }

        }
        mVoteResultAdapter.bindDatas(voteResult.getStatisics());
        mVoteResultAdapter.notifyDataSetChanged();
    }
}