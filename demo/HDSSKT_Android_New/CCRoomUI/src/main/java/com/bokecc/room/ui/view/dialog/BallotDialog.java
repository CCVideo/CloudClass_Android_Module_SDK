package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.Ballot;
import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.listener.BaseOnItemTouch;

/**
 * 投票视图
 * @author wy
 */
public class BallotDialog extends CustomDialog {

    private Context mContext;
    private OnCommitClickListener mOnCommitClickListener;


    private TextView mBallotTile;
    private Button mCommit;
    private BallotAdapter mBallotAdapter;
    private Ballot mBallot;
    private String mBallotContent;
    private int prePos = -1;
    private boolean flag = false;
    private boolean disableClick;

    public BallotDialog(Context context, Ballot mBallot, OnCommitClickListener mOnCommitClickListener) {
        super(context);
        this.mContext = context;
        this.mBallot = mBallot;
        this.mOnCommitClickListener = mOnCommitClickListener;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_ballot_layout);
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
        mBallotTile = findViewById(R.id.id_ballot_tip);
        final RecyclerView resultContent = findViewById(R.id.id_ballot_content);
        mCommit = findViewById(R.id.id_ballot_commit);
        resultContent.setLayoutManager(new LinearLayoutManager(mContext));
        resultContent.addItemDecoration(new SpacesItemDecoration(15));
        mBallotAdapter = new BallotAdapter(mContext);
        resultContent.setAdapter(mBallotAdapter);
        mBallotAdapter.setSelPosition(-1);
        findViewById(R.id.id_ballot_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnCommitClickListener != null) {
                    mOnCommitClickListener.onCommit();
                    Config.isBallot = true;
                }
            }
        });

        resultContent.addOnItemTouchListener(new BaseOnItemTouch(resultContent, new com.bokecc.room.ui.listener.OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                if(disableClick)return;
                int position = resultContent.getChildAdapterPosition(viewHolder.itemView);
                if(mBallot.getBallotType() == Ballot.SINGLE){
                    Config.mBallotResults.clear();
                    Config.mBallotResults.add(position);
                } else {
                    if(position != prePos){
                        if(Config.mBallotResults.size() >= 0){
                            for(int i = 0; i< Config.mBallotResults.size(); i++){
                                Log.i("1", "onClick: "+ Config.mBallotResults.get(i));
                                if(Config.mBallotResults.get(i) == position){
                                    Config.mBallotResults.remove(i);
                                    flag = true;
                                }
                            }
                            if(Config.mBallotResults.size() <= mBallot.getContent().size()){
                                if(!flag){
                                    Config.mBallotResults.add(position);
                                    prePos = position;
                                }
                                flag = false;
                            } else {
                                for(int i = 0; i< Config.mBallotResults.size(); i++){
                                    Log.i("1", "onClick: "+ Config.mBallotResults.get(i));
                                    if(Config.mBallotResults.get(i) == position){
                                        Config.mBallotResults.remove(i);
                                    }
                                }
                                prePos = -1;
                            }
                        }
                    } else {
                        if(Config.mBallotResults.size() > 0){
                            for(int i = 0; i< Config.mBallotResults.size(); i++){
                                Log.i("2", "onClick: "+ Config.mBallotResults.get(i));
                                if(Config.mBallotResults.get(i) == position){
                                    Config.mBallotResults.remove(i);
                                }
                            }
                        }
                        prePos = -1;
                    }
                }
                mBallotAdapter.setSelPosition(position);
                if(Config.mBallotResults.isEmpty()){
                    mCommit.setEnabled(false);
                } else {
                    mCommit.setEnabled(true);
                }
            }
        }));

        mBallotAdapter.setOnSelectOnClickListener(new BallotAdapter.OnSelectOnClickListener() {

            @Override
            public void onSelect(int position, Ballot.Statisic statisic) {
                mBallotContent = statisic.getContent();
                Config.mBallotResults.add(position);
                mCommit.setEnabled(true);
            }
        });

        mBallotAdapter.clear();
        Config.mSeleteType = mBallot.getBallotType();
        Config.mBallotTitle = mBallot.getTileName();
        mBallotTile.setText("主题：" + mBallot.getTileName());
        mCommit.setEnabled(false);
        Config.mBallotResults.clear();
        Config.mBallotContent.clear();
        Config.mBallot.clear();
        Config.isBallot = false;
        Config.mBallotNum = 0;
        prePos = -1;
        mBallotAdapter.setSelPosition(-1);
        Config.mBallot.add(mBallot.getContent());
        mBallotAdapter.bindDatas(mBallot.getContent());
        mBallotAdapter.notifyDataSetChanged();
    }

    public void disableInteractive(){
        disableClick = true;
        mCommit.setEnabled(false);
    }

    /**
     * 监听
     */
    public interface OnCommitClickListener {
        void onCommit();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration{
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }

    }

}