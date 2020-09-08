package com.bokecc.room.ui.view.video;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bokecc.room.ui.view.doc.CCDocView;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.common.exception.StreamException;
import com.bokecc.room.ui.view.base.CCBaseActivity;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.video.manager.MyLinearlayoutManager;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;
import com.bokecc.stream.bean.CCStream;

import static android.view.View.VISIBLE;

/**
 * 文档视频模式
 * @author wy
 */
public class LectureVideoManager extends BaseVideoManager {

    private FrameLayout docFl;
    /**文档区视频布局*/
    private FrameLayout mVideoDocSurfaceContainer;

    /**视频全屏布局*/
    private RelativeLayout mVideoFullScreenLayout;
    private RelativeLayout mSurfaceContainer;
    private RelativeLayout mNoVideoLayout;

    private ImageView mMicClose;
    private ImageView mVideoExit;
    private CCDocView mCCDocView;


    public boolean isVideoFullScreen() {
        return isVideoFullScreen;
    }


    public LectureVideoManager(CCBaseActivity context,int sClassDirection,ViewStub stub) {
        super(context,sClassDirection,stub);

        docFl = context.findViewById(R.id.id_lecture_doc_fl);
        mVideoDocSurfaceContainer = context.findViewById(R.id.id_lecture_doc_video_container);
        mVideoDocSurfaceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoViewListener!=null) {
                    videoViewListener.onClickDocVideo();
                }
            }
        });
        mVideoFullScreenLayout = context.findViewById(R.id.id_lecture_video_full_screen_layout);
        mSurfaceContainer = context.findViewById(R.id.id_lecture_video_full_screen_container);
        mNoVideoLayout = context.findViewById(R.id.id_lecture_video_full_screen_no_video_layout);
        mMicClose = context.findViewById(R.id.id_lecture_video_full_screen_mic_close);
        mVideoExit = context.findViewById(R.id.id_lecture_video_exit);
        mVideoExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitVideoFullScreen(true);
            }
        });
    }

    public void setCCDocView(CCDocView ccDocView){
        this.mCCDocView = ccDocView;
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.id_lecture_videos;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        if(sClassDirection == 0){
            return new MyLinearlayoutManager(context, LinearLayoutManager.HORIZONTAL, true);
        }else{
            return new LinearLayoutManager(context);
        }
    }

    @Override
    protected RecyclerView.ItemDecoration getRecyclerViewItemDecoration() {
        if(sClassDirection == 0){
            return new RecycleViewDivider(context,
                    LinearLayoutManager.VERTICAL, Tools.dipToPixel(4), Color.parseColor("#00000000"),
                    0, 0, RecycleViewDivider.TYPE_BETWEEN);
        }else{
            return new RecycleViewDivider(context,
                    LinearLayoutManager.HORIZONTAL, Tools.dipToPixel(4), Color.parseColor("#00000000"),
                    0, 0, RecycleViewDivider.TYPE_BETWEEN);
        }
    }

    @Override
    public void recyclerViewClickListening(int position){
        if (position >= mVideoStreamViews.size()) {
            return;
        }

        VideoStreamView videoStreamView = mVideoStreamViews.get(position);
        videoStreamView.setIsBig(0);

        if (mRole == CCAtlasClient.TALKER||mRole == CCAtlasClient.INSPECTOR) {//学生端
            if (!videoStreamView.getAudio() && !videoStreamView.getBlackStream()) {
                videoFullScreen(position);//放大视频框
            }
        } else if(mRole == CCAtlasClient.PRESENTER){
            int clickRole = videoStreamView.getStream().getUserRole();
            if(clickRole == CCAtlasClient.PRESENTER) {
                if (!videoStreamView.getAudio() && !videoStreamView.getBlackStream()) {
                    videoFullScreen(position);//放大视频框
                }
            }else if(clickRole == CCAtlasClient.ASSISTANT){
                if (mVideoClickListener != null) {
                    mVideoClickListener.onVideoClick(position, videoStreamView);
                }
            }else {
                if (mVideoClickListener != null) {
                    mVideoClickListener.onVideoClick(position, videoStreamView);
                }
            }
        }
    }

    /**是否全屏*/
    private boolean isVideoFullScreen;
    /**全屏显示对象*/
    private VideoStreamView mVideoStreamView;
    /**全屏显示视图*/
    private SurfaceView mSurfaceView;
    /**全屏显示位置*/
    private volatile int positionStream;

    /**
     * 点击视频框（学生和老师都支持），放大视频
     * @param position
     */
    public void videoFullScreen(final int position) {
        try {
            isVideoFullScreen = true;
            if(videoViewListener != null){
                videoViewListener.fullScreen();
            }
            positionStream = position;
            mVideoFullScreenLayout.setClickable(true);
            mVideoFullScreenLayout.setVisibility(View.VISIBLE);

            mVideoStreamView = mVideoStreamViews.get(position);
            SubscribeRemoteStream stream = mVideoStreamView.getStream();

            if (mVideoStreamView.getSurfaceViewList() != null) {
                mSurfaceView = mCCAtlasClient.setSubRender(context,stream.getRemoteStream(),com.bokecc.stream.config.Config.RENDER_MODE_FIT);

                setSuraceViewConfig(mSurfaceContainer, mSurfaceView);
            }
            mSurfaceContainer.setVisibility(View.VISIBLE);

            mVideoFullScreenLayout.bringToFront();//点击的时候视频放到最前，视频列表隐藏

            //从当前列表中删除
            notifyItemChanged(mVideoStreamView, position, false);

            dismissScreen();//隐藏列表



            handleVideo(stream);
        } catch (Exception e) {
            e.printStackTrace();
            Tools.handleException("LectureVideoManager",e);
            mVideoFullScreenLayout.setVisibility(View.GONE);
        }
    }

    private void handleVideo(SubscribeRemoteStream stream) {
        mMicClose.setVisibility(stream.isAllowAudio() ? View.GONE : View.VISIBLE);
        if (stream.getUserRole() != CCAtlasClient.PRESENTER &&
                stream.getUserRole() != CCAtlasClient.ASSISTANT &&
                CCAtlasClient.getInstance().getMediaMode() == CCAtlasClient.MEDIA_MODE_AUDIO) {
            if (stream.getUserId().equals(CCAtlasClient.SHARE_SCREEN_STREAM_ID)) {
                mNoVideoLayout.setVisibility(View.GONE); // 显示音频贴图
            } else {
                mNoVideoLayout.setVisibility(VISIBLE); // 显示音频贴图
                if (sClassDirection == 0) {
                    mNoVideoLayout.setBackgroundResource(R.mipmap.only_mic_bg);
                } else {
                    mNoVideoLayout.setBackgroundResource(R.mipmap.only_mic_bg_land);
                }
            }
        } else {
            if (!stream.isAllowVideo()) { // 关闭视频
                mNoVideoLayout.setVisibility(VISIBLE); // 显示摄像头被关闭贴图
                if (sClassDirection == 0) {
                    mNoVideoLayout.setBackgroundResource(R.mipmap.camera_close_bg);
                } else {
                    mNoVideoLayout.setBackgroundResource(R.mipmap.camera_close_bg_land);
                }
            } else {
                mNoVideoLayout.setVisibility(View.GONE);
                if (stream.getRemoteStream() != null) {
                    if (!stream.getRemoteStream().hasAudio()) {
                        mMicClose.setVisibility(VISIBLE);
                        mMicClose.setImageResource(R.mipmap.no_mic_icon);
                    }
                    if (!stream.getRemoteStream().hasVideo()) {
                        mNoVideoLayout.setVisibility(VISIBLE); // 显示摄像头被关闭贴图
                        if (sClassDirection == 0) {
                            mNoVideoLayout.setBackgroundResource(R.mipmap.no_camera_icon);
                        } else {
                            mNoVideoLayout.setBackgroundResource(R.mipmap.no_camera_icon_land);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateVideos(String userid, boolean flag, boolean isSelf, int changed) {
        super.updateVideos(userid, flag, isSelf, changed);

        if (mVideoStreamViews.isEmpty() && isVideoFullScreen) {
            SubscribeRemoteStream stream = mVideoStreamView.getStream();

            if (stream.getUserId().equals(userid)) {
                if (changed == 0) {
                    stream.setAllowAudio(flag);
                    if (mCCAtlasClient.getUserIdInPusher().equals(userid) && !isSelf) {
                        Tools.showToast(flag ? "您被老师开启麦克风" : "您被老师关闭麦克风", false);
                    }
                } else if (changed == 1) {
                    stream.setAllowVideo(flag);
                } else if (changed == 3) {
                    stream.setLock(flag);
                }
                handleVideo(stream);
            }
        }
    }
    /**
     * 点击退出按钮，退出视频全屏
     * @param isToRecyclerView true回到列表/false不回列表
     */
    public void exitVideoFullScreen(boolean isToRecyclerView) {
        isVideoFullScreen = false;
        showScreen();

        mVideoFullScreenLayout.setVisibility(View.GONE);

        if (mSurfaceView != null && mVideoStreamView != null) {
            if(isToRecyclerView){
                //将数据添加回列表
                CCStream stream = mVideoStreamView.getStream().getRemoteStream();
                SurfaceView surfaceView = mCCAtlasClient.setSubRender(context,stream,com.bokecc.stream.config.Config.RENDER_MODE_FIT);
                mVideoStreamView.setSurfaceViewList(surfaceView);
                notifyItemChanged(mVideoStreamView, positionStream, true);
            }

            //置空临时数据
            mSurfaceView.setVisibility(View.GONE);
            mSurfaceContainer.removeView(mSurfaceView);
            mSurfaceView = null;
            mVideoStreamView = null;
        }

        if(videoViewListener != null){
            videoViewListener.exitFullScreen();
        }
    }

    /**
     *
     * @param mSurfaceContainer
     * @param mSurfaceView
     */
    private void setSuraceViewConfig(RelativeLayout mSurfaceContainer, SurfaceView mSurfaceView) {
        RelativeLayout.LayoutParams params;
        if (sClassDirection == 0) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setVisibility(View.VISIBLE);
        mSurfaceView.setLayoutParams(params);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setZOrderMediaOverlay(true);
        mSurfaceContainer.addView(mSurfaceView);
    }

    @Override
    protected void removeVideoView(String userId){
        if (mDocVideoStreamView != null && mDocVideoStreamView.getStream().getUserId().equals(userId)) {
            if (mSurfaceView1 != null) {
                mSurfaceView1.setVisibility(View.GONE);
                mVideoDocSurfaceContainer.removeView(mSurfaceView1);
            }
            mDocVideoStreamView =null;
            videoposition = 0;
            mVideoDocSurfaceContainer.setVisibility(View.GONE);

            if (mSurfaceView1 != null) {
                mSurfaceView1.setVisibility(View.GONE);
            }
        }else if(isVideoFullScreen){
            exitVideoFullScreen(false);
        }
    }

    @Override
    protected synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        try{
            if (mVideoStreamViews == null || videoStreamView == null) {
                return;
            }

            while(position > mVideoStreamViews.size()) {
                position = position - 1;
            }

            if(position < 0){
                position = 0;
            }

            if (isAdd) {
                for (VideoStreamView temp : mVideoStreamViews) {
                    if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                        if (videoStreamView.getStream().getRemoteStream() != null) {
                            if (videoStreamView.getStream().getRemoteStream().getHasImprove()) {
                                break;
                            }
                        }
                        return;
                    }
                }

                mVideoStreamViews.add(position, videoStreamView);
            } else {
                mVideoStreamViews.remove(videoStreamView);
            }

            mVideoAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected int getAdapterType() {
        return 1;
    }


    private VideoStreamView mDocVideoStreamView;
    /**文档区域*/
    private SurfaceView mSurfaceView1;
    /**当前视频序列号*/
    private int videoposition;

    /**
     * 老师双击视频放大到文档区(app端不支持双击视频放大到文档区，只有web端双击了，app监听事件，再放大到文档区)
     * @param streamId 点击对应用户的 userid
     * @param mode  放大缩小
     */
    public void setVideoToDoc(String streamId, String mode) {
        if (mode.equals("big")) {
            if (mDocVideoStreamView != null) {
                closeDocVideoView();
            }
            openDocVideoView(streamId);
        } else if (mode.equals("small")) {
            closeDocVideoView();
        }
    }

    /**
     * 打开文档视频区域
     * @param streamId
     */
    private void openDocVideoView(String streamId){
        if(isVideoFullScreen){
            exitVideoFullScreen(true);
        }
        //根据userid获取到当前视频
        for (int i = 0; i < mVideoStreamViews.size(); i++) {
            VideoStreamView streamView = mVideoStreamViews.get(i);
            CCStream stream = streamView.getStream().getRemoteStream();
            if(stream != null){
                if (stream.getStreamId().equals(streamId)) {
                    mDocVideoStreamView = streamView;
                    videoposition = i;
                    break;
                }
            }else{
                mDocVideoStreamView = streamView;
                videoposition = i;
            }
        }

        //如果没有这个视频，直接返回
        if (mDocVideoStreamView == null) {
            return;
        }

        //
        if (mVideoStreamViews.get(videoposition).getSurfaceViewList() != null) {
            ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(mVideoDocSurfaceContainer.getLayoutParams());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
            int width = Tools.getScreenWidth();

            if(mCCDocView!=null){
                if(mCCDocView.isDocFullScreen()){
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }else {
                    layoutParams.height = width * 9 / 16;
                }
            }

            //surface设置
            mSurfaceView1 = mCCAtlasClient.setSubRender(context,mDocVideoStreamView.getStream().getRemoteStream(),com.bokecc.stream.config.Config.RENDER_MODE_FIT);

//            mSurfaceView1 = mCCAtlasClient.getSurfaceView();
//            mSurfaceView1.setLayoutParams(layoutParams);
//            mSurfaceView1.setZOrderOnTop(false);
//            mSurfaceView1.setZOrderMediaOverlay(false);
            //framelayout设置
            mVideoDocSurfaceContainer.setLayoutParams(layoutParams);
            mVideoDocSurfaceContainer.addView(mSurfaceView1);
            mVideoDocSurfaceContainer.setVisibility(View.VISIBLE);

            //
//            if (mDocVideoStreamView.getStream().getRemoteStream() == null) {
//                mCCAtlasClient.setSubRender(mSurfaceView1, "0");
//            } else {
//                mCCAtlasClient.setSubRender(mSurfaceView1, mDocVideoStreamView.getStream().getRemoteStream().getSubRenderId());
//            }
        }

        //从当前列表中删除
        notifyItemChanged(mDocVideoStreamView, videoposition, false);
    }

    /**
     * 关闭文档视频区域
     */
    private void closeDocVideoView(){
        if (mDocVideoStreamView != null){// && mDocVideoStreamView.getStream().getUserId().equals(userid)) {
            if (mSurfaceView1 != null) {
                mSurfaceView1.setVisibility(View.GONE);
                mVideoDocSurfaceContainer.removeView(mSurfaceView1);
                if (mDocVideoStreamView.getStream().getUserRole() != CCAtlasClient.PRESENTER) {
                    videoposition = videoposition + 1;
                }

                if (mDocVideoStreamView.getStream().getRemoteStream() == null) {
                    OnPositionStream(mDocVideoStreamView, videoposition, mDocVideoStreamView.getStream().getUserId());
                } else {
                    OnPositionStream(mDocVideoStreamView, videoposition, mDocVideoStreamView.getStream().getRemoteStream().getUserid());
                }
            } else {
                notifyItemChanged(mDocVideoStreamView, videoposition, true);
            }
            mDocVideoStreamView =null;
            videoposition = 0;
        }
        mVideoDocSurfaceContainer.setVisibility(View.GONE);

        if (mSurfaceView1 != null) {
            mSurfaceView1.setVisibility(View.GONE);
        }
    }

    /**
     * 重新订阅
     * @param videoStreamView
     * @param position
     * @param uid
     */
    private void OnPositionStream(final VideoStreamView videoStreamView,final int position, String uid) {
        try {
            if (uid.equals(mCCAtlasClient.getUserIdInPusher())) {
                if(videoViewListener == null){
                    Tools.showToast("请设置监听！");
                    return;
                }

                notifyItemChanged(videoViewListener.getMySelfVideoStreamView(), position, false);

                mCCAtlasClient.setAppOrientation(sClassDirection == 0);//true横屏竖屏
                mCCAtlasClient.attachLocalCameraStram(null);
                videoViewListener.getMySelfVideoStreamView().setSurfaceViewList(mCCAtlasClient.getSurfaceViewList().get(0));
                notifyItemChanged(videoViewListener.getMySelfVideoStreamView(), position, true);
            } else if (videoStreamView.getStream().getRemoteStream() != null) {
                CCStream stream = videoStreamView.getStream().getRemoteStream();
                SurfaceView surfaceView = mCCAtlasClient.setSubRender(context,stream,com.bokecc.stream.config.Config.RENDER_MODE_FIT);
                videoStreamView.setSurfaceViewList(surfaceView);
                notifyItemChanged(videoStreamView, position, true);
            }
        } catch (StreamException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClearDatas() {
        super.onClearDatas();

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        docFl.setVisibility(visibility);
    }

    /**
     * 隐藏列表视图
     */
    @Override
    protected void dismissScreen(){
        super.dismissScreen();
        if (mSurfaceView1 != null) {
            mSurfaceView1.setVisibility(View.GONE);
        }
    }

    /**
     * 显示列表视图
     */
    @Override
    protected void showScreen(){
        super.showScreen();
        if (mSurfaceView1 != null) {
            mSurfaceView1.setVisibility(View.VISIBLE);
        }
    }

    public void exitDocFullScreen() {
        if(mVideoDocSurfaceContainer.getVisibility()==View.VISIBLE){
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoDocSurfaceContainer.getLayoutParams();
            int width = Tools.getScreenWidth();
            int height = Tools.getScreenHeight();
            int tempHeight = Math.min(width,height);
            layoutParams.height = tempHeight * 9 / 16;
            mVideoDocSurfaceContainer.setLayoutParams(layoutParams);
        }

    }
}