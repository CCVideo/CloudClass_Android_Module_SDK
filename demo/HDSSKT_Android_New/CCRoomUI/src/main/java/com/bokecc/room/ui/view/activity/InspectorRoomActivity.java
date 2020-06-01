package com.bokecc.room.ui.view.activity;

import android.os.Bundle;

import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.sskt.base.bean.BrainStom;
import com.bokecc.sskt.base.bean.Vote;

import static com.bokecc.sskt.base.CCAtlasClient.INSPECTOR;

/**
 * 隐身者直播间
 * @author Swh
 */

public class InspectorRoomActivity extends StudentRoomActivity {

    private static final String TAG = "InspectorRoomActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRole = INSPECTOR;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        menuBottomView.setOperatorAreaShow(false);
    }

    /**
     * 显示头脑风暴
     * @param brainStom
     */
    @Override
    protected void showBrainStom(final BrainStom brainStom){
        super.showBrainStom(brainStom);
        brainStomDialog.disableInteractivie();
    }

    /**
     * 点名
     * @param time
     */
    @Override
    protected void showCallNamed(int time) {
        super.showCallNamed(time);
        mRollCallDialog.disableInteractive();
    }

    /**
     * 答题卡
     * @param vote
     */
    @Override
    protected void showVote(Vote vote) {
        super.showVote(vote);
        voteDialog.disableInteractive();
    }

    /**
     * 投票
     * @param mBallot
     */
    @Override
    protected void showBallot(Ballot mBallot) {
        super.showBallot(mBallot);
        ballotDialog.disableInteractive();
    }


}
