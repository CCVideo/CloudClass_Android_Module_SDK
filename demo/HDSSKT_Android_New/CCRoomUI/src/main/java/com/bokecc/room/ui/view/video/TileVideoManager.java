package com.bokecc.room.ui.view.video;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.bokecc.room.ui.view.base.CCBaseActivity;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.listener.OnDisplayInteractionListener;
import com.bokecc.room.ui.view.video.manager.MyGridLayoutManager;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.sskt.base.CCAtlasClient;

/**
 * 平铺视频管理类
 * @author wy
 */
public class TileVideoManager extends BaseVideoManager implements View.OnClickListener {

    /**布局管理*/
    private MyGridLayoutManager mGridLayoutManager;
    /**每行显示的数量*/
    protected int mCurSpanCount = -1;

    /**外层点击回调*/
    private OnDisplayInteractionListener onDisplayInterListener;

    public TileVideoManager(CCBaseActivity context,int sClassDirection, ViewStub stub) {
        super(context,sClassDirection,stub);
        if(mRole==CCAtlasClient.ASSISTANT||mRole==CCAtlasClient.INSPECTOR)
        context.findViewById(R.id.id_tiling_receive_click).setOnClickListener(this);
    }

    public void setOnDisplayInterListener(OnDisplayInteractionListener onDisplayInterListener) {
        this.onDisplayInterListener = onDisplayInterListener;
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.id_tiling_videos;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return null;
    }

    @Override
    protected RecyclerView.ItemDecoration getRecyclerViewItemDecoration() {
        return null;
    }

    /**
     * 刷新列表视图
     * @param videoStreamView
     * @param position
     * @param isAdd
     */
    protected synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        //刷新子视图
        if(mGridLayoutManager != null){
            mGridLayoutManager.setRefresh(true);
        }

        if(videoStreamView == null){
            return;
        }
        //添加到数据中
        if (isAdd) {
            for (VideoStreamView temp : mVideoStreamViews) {
                if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                    return;
                }
            }
            mVideoStreamViews.add(position, videoStreamView);
        } else {
            mVideoStreamViews.remove(videoStreamView);
        }

        //布局的动态判断切换
        int size = mVideoStreamViews.size();
        if (mVideoStreamViews.size() == 0) {
            mVideoAdapter.notifyDataSetChanged();
            return;
        }
        int spanCount = (int) Math.ceil(Math.sqrt(size));
        if (mCurSpanCount != spanCount) {
            mCurSpanCount = spanCount;
            if (mGridLayoutManager != null) {
                videosRecyclerView.getRecycledViewPool().clear();
                mGridLayoutManager.removeAllViews();
                mGridLayoutManager.setSpanCount(mCurSpanCount);
                mVideoAdapter.refresh();
            } else {
                mGridLayoutManager = new MyGridLayoutManager(context, mCurSpanCount);
                videosRecyclerView.setLayoutManager(mGridLayoutManager);
            }
            mVideoAdapter.notifyDataSetChanged();
        } else {
            if(mGridLayoutManager != null){
                mGridLayoutManager.setRefresh(true);
            }
            mVideoAdapter.notifyDataSetChanged();
        }
    }

    public void updateVideos(String userid, boolean flag, int changed) {
        super.updateVideos(userid,flag,changed);
//        notifyItemChanged(mVideoStreamViews.get(), mVideoStreamViews.size(), true);
    }

    @Override
    protected int getAdapterType() {
        return 4;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.id_tiling_receive_click){
            if(onDisplayInterListener!=null)
                onDisplayInterListener.toggleTopAndBottom();
        }
    }
    /**
     * 重写父类点击方法
     * @param position
     */
    @Override
    protected void recyclerViewClickListening(int position){
        if(mRole != CCAtlasClient.PRESENTER){
            return;
        }
        if (mVideoStreamViews.get(position).getStream().getUserRole() ==
                CCAtlasClient.PRESENTER || mVideoStreamViews.get(position)
                .getStream().getUserRole() == CCAtlasClient.ASSISTANT) {
            return;
        }
        VideoStreamView videoStreamView = mVideoStreamViews.get(position);
        if (mVideoClickListener != null) {
            mVideoClickListener.onVideoClick(position, videoStreamView);
        }
    }
}
