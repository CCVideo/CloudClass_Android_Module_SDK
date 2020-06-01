package com.bokecc.room.ui.view.video.manager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 作者 ${CC视频}.<br/>
 */

public class MyGridLayoutManager extends GridLayoutManager {

    private int mItemCountBeforeLayout = -1;
    private int mCount = -1;
    private boolean isRefresh = false;

    public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            if (mCount <= state.getItemCount()*2) { // onMeasure onLayout
                super.onLayoutChildren(recycler, state);
                mCount++;
            } else {
                if (isRefresh || mItemCountBeforeLayout != state.getItemCount() || state.didStructureChange()) {
                    super.onLayoutChildren(recycler, state);
                    mCount = 0;
                    isRefresh = false;
                    mItemCountBeforeLayout = state.getItemCount();
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
