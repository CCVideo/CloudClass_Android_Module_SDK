package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.sskt.base.bean.BallotResult;
import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;

/**
 * 投票结果视图
 * @author wy
 */
public class BallotResultDialog extends CustomDialog {


    private final int rightZimuIcons[] = new int[]{
            R.mipmap.a_right_icon,
            R.mipmap.b_right_icon,
            R.mipmap.c_right_icon,
            R.mipmap.d_right_icon,
            R.mipmap.e_right_icon
    };
    private BallotResultAdapter mBallotResultAdapter;

    private TextView mBallotTip;
    private TextView mBallotContent;
    private TextView mText;
    private ImageView mRightImgs[];

    private Context context;
    private BallotResult ballotResult;

    public BallotResultDialog(Context context, BallotResult mBallotResult) {
        super(context);
        this.context = context;
        this.ballotResult = mBallotResult;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_ballot_result_layout);
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
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        mBallotTip = findViewById(R.id.id_ballot_result_tip);
        mBallotContent = findViewById(R.id.id_ballot_result_statistics);
        mText = findViewById(R.id.id_vote_result_right);
        RecyclerView resultContent = findViewById(R.id.id_ballot_result_content);
        ImageView rightImg1 = findViewById(R.id.id_ballot_result_right_img1);
        ImageView rightImg2 = findViewById(R.id.id_ballot_result_right_img2);
        ImageView rightImg3 = findViewById(R.id.id_ballot_result_right_img3);
        ImageView rightImg4 = findViewById(R.id.id_ballot_result_right_img4);
        ImageView rightImg5 = findViewById(R.id.id_ballot_result_right_img5);

        resultContent.setLayoutManager(new LinearLayoutManager(context));
        mBallotResultAdapter = new BallotResultAdapter(context);
        resultContent.setAdapter(mBallotResultAdapter);

        mRightImgs = new ImageView[]{
                rightImg1, rightImg2, rightImg3, rightImg4, rightImg5
        };

        findViewById(R.id.id_ballot_reslut_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(Config.mBallotContent.size() == 0){
            return;
        }
        mBallotTip.setText("主题：" + Config.mBallotTitle);
        Config.mBallotNum = ballotResult.getBallotResultNum();
        mBallotContent.setText("已有" + ballotResult.getBallotResultNum() + "人投票");
        if(ballotResult.getBallotResultNum() != 0){
            if(Config.mSeleteType == Ballot.SINGLE) {
                for(int i = 0; i< Config.mBallotResults.size(); i++){
                    mRightImgs[0].setVisibility(View.VISIBLE);
                    mRightImgs[0].setImageResource(rightZimuIcons[Config.mBallotResults.get(i)]);
                }
            } else {
                for (int i = 0; i < Config.mBallotResults.size(); i++) { // 多选
                    mRightImgs[i].setVisibility(View.VISIBLE);
                    mRightImgs[i].setImageResource(rightZimuIcons[Config.mBallotResults.get(i)]);
                }
            }
            mBallotResultAdapter.bindDatas(ballotResult.getContent());
            mBallotResultAdapter.notifyDataSetChanged();
        } else {
            BallotResult mBallotResult = new BallotResult();
//            ArrayList<BallotResult.choices> choices = new ArrayList<>();
//            for (int i = 0; i < ballotResult.getContent().size(); i++) {
//                BallotResult.choices mChoices = new BallotResult.choices();
//                mChoices.setContent(Config.mBallotContent.get(i));
//                choices.add(mChoices);
//            }
//            mBallotResult.setContent(choices);
            for (int i = 0; i < mRightImgs.length; i++) {
                mRightImgs[i].setVisibility(View.GONE);
            }
            mBallotResultAdapter.bindDatas(ballotResult.getContent());
            mBallotResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 监听
     */
    public interface OnCommitClickListener {
        void onCommit();
    }


}