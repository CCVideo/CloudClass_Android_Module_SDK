package com.bokecc.room.ui.view.video.manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 作者 ${CC视频}.<br/>
 */

public class MyLinearlayoutManager extends LinearLayoutManager {

    private int mItemCountBeforeLayout = -1;

    public MyLinearlayoutManager(Context context) {
        super(context);
    }

    public MyLinearlayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            if (!state.isMeasuring() && mItemCountBeforeLayout == getItemCount()) { // 每次测量完成进行一次布局
                super.onLayoutChildren(recycler, state);
                return;
            }
            if (mItemCountBeforeLayout != state.getItemCount() || state.didStructureChange()) {
                super.onLayoutChildren(recycler, state);
                mItemCountBeforeLayout = state.getItemCount();
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
