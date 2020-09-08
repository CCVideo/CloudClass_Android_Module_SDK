package com.bokecc.room.ui.view.video;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.room.ui.listener.OnDisplayInteractionListener;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.common.exception.StreamException;
import com.bokecc.room.ui.view.base.CCBaseActivity;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;
import com.example.ccbarleylibrary.CCBarLeyCallBack;
import com.example.ccbarleylibrary.CCBarLeyManager;

/**
 * 主视频模式
 * @author wy
 */
public class MainVideoManager extends BaseVideoManager implements View.OnClickListener {

    /**主视频显示区域*/
    private RelativeLayout mMainVideoLayout;
    /**视频布局*/
    private RelativeLayout mSurfaceContainer;
    /**无视频界面*/
    private RelativeLayout mOtherLayout;

    /**主视频*/
    private RelativeLayout mMainVideoTop;
    /**视频名*/
    private TextView mMainVideoName;
    /**mic图标*/
    private ImageView mMicClose;
    /**是否可以绘制的图标*/
    private ImageView mDrawIcon;

    /**老师图标*/
    private ImageView mSetUpTeacherIcon;
    /**锁图标*/
    private ImageView mLockIcon;

    /**外层点击回调*/
    private OnDisplayInteractionListener onDisplayInterListener;

    private static final String TAG = "MainVideoManager";

    public void setOnDisplayInterListener(OnDisplayInteractionListener onDisplayInterListener) {
        this.onDisplayInterListener = onDisplayInterListener;
    }


    public MainVideoManager(CCBaseActivity context,int sClassDirection, ViewStub stub) {
        super(context,sClassDirection,stub);

        mMainVideoLayout = context.findViewById(R.id.id_main_video_layout);
        mSurfaceContainer = context.findViewById(R.id.id_main_video_container);
        mOtherLayout = context.findViewById(R.id.id_main_video_layout_camera_close);


        mMainVideoTop = context.findViewById(R.id.id_main_video_top_content);
        mMainVideoName = context.findViewById(R.id.id_main_video_name);
        mMicClose = context.findViewById(R.id.id_main_video_mic_close);
        mDrawIcon = context.findViewById(R.id.id_main_video_draw);


        mSetUpTeacherIcon = context.findViewById(R.id.id_main_video_setup_theacher);
        mLockIcon = context.findViewById(R.id.id_main_video_lock);
        context.findViewById(R.id.id_main_video_container).setOnClickListener(this);
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.id_main_video_little_videos;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new LinearLayoutManager(context);
    }

    @Override
    protected RecyclerView.ItemDecoration getRecyclerViewItemDecoration() {
        return new RecycleViewDivider(context,
                LinearLayoutManager.HORIZONTAL, Tools.dipToPixel(4), Color.parseColor("#00000000"),
                0, 0, RecycleViewDivider.TYPE_BETWEEN);
    }

    public String getMainVideoUserid() {
        if (mMainVideoView != null) {
            return mMainVideoView.getStream().getUserId();
        } else {
            return null;
        }
    }

    /**
     * 视频点击回调
     * @param position
     */
    @Override
    protected void recyclerViewClickListening(final int position){
       final VideoStreamView videoStreamView = mVideoStreamViews.get(position);
        if (mRole == CCAtlasClient.TALKER||mRole == CCAtlasClient.INSPECTOR) {
            if (!TextUtils.isEmpty(mCCAtlasClient.getInteractBean().getFollowId())) {
                Tools.showToast("跟随模式下不能切换视频");
            }else{
                switchMainVideo(videoStreamView, position);
            }
        } else if(mRole == CCAtlasClient.PRESENTER){
            // 老师并且设置了跟随
            if(videoStreamView.getStream().getUserRole() ==
                    CCAtlasClient.PRESENTER || videoStreamView.getStream().getUserRole() == CCAtlasClient.ASSISTANT){
                if (!TextUtils.isEmpty(mCCAtlasClient.teacherFollowUserID())){
                    handleFollowMode(position, videoStreamView);
                }else {
                    switchMainVideo(videoStreamView, position);
                }
            }else {
                if (mVideoClickListener != null) {
                    mVideoClickListener.onVideoClick(position, videoStreamView);
                }
            }

        }
    }

    public void handleFullScreenVideo(final VideoStreamView videoStreamView, final int position){
        if(!TextUtils.isEmpty(mCCAtlasClient.teacherFollowUserID())){
            handleFollowMode(position, videoStreamView);
        }else {
            switchMainVideo(videoStreamView, position);
        }

    }

    private void handleFollowMode(final int position, final VideoStreamView videoStreamView) {
        CCBarLeyManager.getInstance().changeMainStreamInSigleTemplate(videoStreamView.getStream().getUserId(), new
                CCBarLeyCallBack<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (position < 0 || position >= mVideoStreamViews.size()) {
                            Tools.log(TAG, "position: " + position + ", mVideoStreamViews.size():" + mVideoStreamViews.size());
                            return;
                        }
                        switchMainVideo(videoStreamView, position);
                    }

                    @Override
                    public void onFailure(String err) {
                        Tools.showToast(err);
                    }
                });
    }

    /**上一个主视频*/
    private VideoStreamView mPreMainVideoView;
    /**主视频流*/
    private VideoStreamView mMainVideoView;

    /**
     * 切换主视图
     */
    private void switchMainVideo(VideoStreamView videoStreamView,int position) {
//        if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
//            if (videoStreamView.getStream().getUserRole() == CCAtlasClient.PRESENTER
//                    || videoStreamView.getStream().getUserRole() == CCAtlasClient.ASSISTANT) {
//                mCCAtlasClient.setRegion(mCCAtlasClient.getLocalStreamId(), null);
//            } else {
//                mCCAtlasClient.setRegion(videoStreamView.getStream().getStreamId(), null);
//            }
//        }

        //切换主视图
        mPreMainVideoView = mMainVideoView;
        mMainVideoView = videoStreamView;

        //更新列表
        mVideoStreamViews.set(position, mPreMainVideoView);
        mVideoAdapter.update(position, mPreMainVideoView);

        switchVideo = true;
        displayMainVideo();


        //2 平台需要更新流布局，否则白屏
//        if (mCCAtlasClient.getHuoDePlatform() == CCAtlasClient.ZEGO_PLATFORM && mVideoStreamViews.size() > position) {
//            if (mVideoStreamViews.get(position).getStream().getRemoteStream() == null) {
//                mCCAtlasClient.setSubRender(mVideoStreamViews.get(position).getSurfaceViewList(),null);
//            } else {
//                mCCAtlasClient.setSubRender(mVideoStreamViews.get(position).getSurfaceViewList(), mVideoStreamViews.get(position).getStream().getRemoteStream());
//            }
//        }
    }

    @Override
    protected synchronized void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd) {
        if (isAdd) {
            //当前添加视频和主视频一致，返回
            if (mMainVideoView != null && videoStreamView.getStream().getUserId().equals(mMainVideoView.getStream().getUserId())) {
                return;
            }
            //判断视频列表中是否有一样的视频
            for (VideoStreamView temp : mVideoStreamViews) {
                if (temp.getStream().getUserId().equals(videoStreamView.getStream().getUserId())) {
                    return;
                }
            }
        }

        // 1. 判断跟随
        // 6. 有教师 显示教师
        // 7. 没有教师显示第一个
        // 因为教师默认在第一个所以可以直接显示第一个
        if (TextUtils.isEmpty(mCCAtlasClient.getInteractBean().getFollowId())) { // 2. 如果没有跟随
            if (isAdd) {
                if (mMainVideoView == null) { //如果position为0，并且主视图为空，优先添加主视图
                    if (videoStreamView.getStream().getUserRole() == CCAtlasClient.PRESENTER) {
                        setMainViewAndUpdateVideos(videoStreamView, true);
                    }else{
                        updateVideoList(videoStreamView, true); // 更新列表
                    }
                } else { // 不是第一个
                    updateVideoList(videoStreamView, true); // 更新列表
                }
            } else {
                // 如果当前被移除的视图是主视图，两种情况：(1)当前主视图是远程视图，videoStreamView==null;(2)当前视图是本视图，videoStreamView == mMainVideoView
                if (mMainVideoView != null && (videoStreamView == null || (videoStreamView.getStream()!=null&&TextUtils.equals(videoStreamView.getStream().getUserId(),mMainVideoView.getStream().getUserId())))) {
                    setMainViewAndUpdateVideos(mMainVideoView, false);
                } else {
                    updateVideoList(videoStreamView, false);
                }
            }
        } else { // 3. 如果有跟随
            // 当前被操纵的视图不是当前跟随的视图
            if (!videoStreamView.getStream().getUserId().equals(mCCAtlasClient.getInteractBean().getFollowId())) { // 不是
                if (isAdd) { // 如果是添加视图
                    if (position == 0) { // 第一个
                        if (mMainVideoView == null) { // 主视频没有直接添加
                            mPreMainVideoView = null;
                            mMainVideoView = videoStreamView; // 获取当前视图上面的流
                            displayMainVideo(); // 添加到主视图
                        } else { // 主视频有 当前添加的是老师，
                            // 主视频显示的不是当前的跟随视频 更新主视频为老师视频
                            if (!mMainVideoView.getStream().getUserId().equals(mCCAtlasClient.getInteractBean().getFollowId())) {
                                if (videoStreamView.getStream().getUserRole() == CCAtlasClient.PRESENTER
                                        || videoStreamView.getStream().getUserRole() == CCAtlasClient.ASSISTANT) {
                                    setMainViewAndUpdateVideos(videoStreamView, true);
                                }
                            } else { // 主视频显示的是跟随视频，添加老师到列表
                                updateVideoList(videoStreamView, true);
                            }
                        }
                    } else {
                        updateVideoList(videoStreamView, true); // 更新列表
                    }
                } else {
                    // 如果当前被移除的视图是主视图
                    if (mMainVideoView != null && videoStreamView.getStream().getUserId().equals(mMainVideoView.getStream().getUserId())) {
                        setMainViewAndUpdateVideos(videoStreamView, false);
                    } else {
                        updateVideoList(videoStreamView, false);
                    }
                }
            } else { // 当前操作的视图是跟随视图
                setMainViewAndUpdateVideos(videoStreamView, isAdd);
            }
        }
    }

    /**
     * 设置主视频视图
     * @param videoStreamView
     * @param isAdd
     */
    private void setMainViewAndUpdateVideos(VideoStreamView videoStreamView, boolean isAdd) {
        mPreMainVideoView = mMainVideoView;
        if (isAdd) { // 添加的是主视频
            mMainVideoView = videoStreamView;
            // 将上一个主视频移回列表 todo
            if (mPreMainVideoView != null) {
                updateVideoList(mPreMainVideoView, true);
            }
            displayMainVideo();
        } else { // 移除主视频
            if (mSurfaceView != null) {
                mSurfaceContainer.removeView(mSurfaceView);
            }

            if (mVideoStreamViews != null && mVideoStreamViews.size() > 0) { // 列表还有数据
                mMainVideoView = null;
                // 遍历列表 如果有老师存在 主视频显示老师
                for (VideoStreamView temp : mVideoStreamViews) {
                    if (temp.getStream().getUserRole() == CCAtlasClient.PRESENTER) {
                        mMainVideoView = temp;
                        break;
                    }
                }

                updateVideoList(mMainVideoView, false); // 将主视频显示的从列表移除
                displayMainVideo();
            } else {
                try {
                    if (mPreMainVideoView != null && mMainVideoRenderer != null) {
                        if (mPreMainVideoView.getStream().isLocalCameraStream()) {
                            mCCAtlasClient.detachLocalCameraStram(mMainVideoRenderer);
                        } else {
                            mPreMainVideoView.getStream().detach(mMainVideoRenderer);
                        }
                    }
                } catch (StreamException e) {
                    if (mMainVideoRenderer != null) {
                        mMainVideoRenderer = null;
                    }
                } finally {
                    mPreMainVideoView = null;
                    mMainVideoView = null;
                }
            }
        }
    }

    /**标记程序是运行中状态*/
    private boolean isViewInitialize = true;
    private SurfaceView mMainVideoRenderer;
    private SurfaceView mSurfaceView;

    /**不知道作用 todo*/
    private volatile boolean switchVideo = false;

    /**
     * 显示主视频
     */
    private void displayMainVideo() {
        try {
            if (!isViewInitialize || mMainVideoView == null) {
                return;
            }
            if (mPreMainVideoView != null && mMainVideoRenderer != null) { // 移除视图上面的旧数据
                if (mPreMainVideoView.getStream().isLocalCameraStream()) {
                    mCCAtlasClient.detachLocalCameraStram(mMainVideoRenderer);
                } else {
                    mPreMainVideoView.getStream().detach(mMainVideoRenderer);
                }
            }

            //移除旧视图
            if (mSurfaceView != null) {
                mSurfaceContainer.removeView(mSurfaceView);
            }

            //获取新的surfaceView
            if (mMainVideoView.getSurfaceViewList() != null) {
                //布局约束
                RelativeLayout.LayoutParams params;
                if (sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
                params.addRule(RelativeLayout.CENTER_IN_PARENT);

                //
//                mSurfaceView = mCCAtlasClient.getSurfaceView();
                if (mMainVideoView.getStream().getRemoteStream() != null) {
//                    ViewGroup parent = (ViewGroup)mSurfaceView.getParent();
//                    if(parent!=null)
//                        parent.removeView(mSurfaceView);
//
//                    mSurfaceView.setLayoutParams(params);
//                    mSurfaceView.setZOrderOnTop(false);
//                    mSurfaceView.setZOrderMediaOverlay(false);

                    mSurfaceView = mCCAtlasClient.setSubRender(context, mMainVideoView.getStream().getRemoteStream(),com.bokecc.stream.config.Config.RENDER_MODE_FIT);
                    mSurfaceView.setLayoutParams(params);
                    mSurfaceContainer.addView(mSurfaceView);

                    mMainVideoView.setSurfaceViewList(mSurfaceView);
                } else {
                    if (switchVideo) {
                        mSurfaceView = mCCAtlasClient.setSubRender(mSurfaceView, null);
                        mSurfaceView.setLayoutParams(params);
                        mSurfaceContainer.addView(mSurfaceView);
                        mMainVideoView.setSurfaceViewList(mSurfaceView);
                    } else {
                        if (mMainVideoView.getStream().getUserRole() == CCAtlasClient.PRESENTER && mMainVideoView
                                .getStream().getUserId().equals(mCCAtlasClient.getInteractBean().getUserId())) {
                            mSurfaceView = mCCAtlasClient.setSubRender(context, null,com.bokecc.stream.config.Config.RENDER_MODE_FIT);
                            mSurfaceView.setLayoutParams(params);
                            mSurfaceContainer.addView(mSurfaceView);
//                            mCCAtlasClient.setSubRender(mSurfaceView, null);
                            mMainVideoView.setSurfaceViewList(mSurfaceView);
                        } else {
                            mSurfaceView = mCCAtlasClient.setSubRender(context, null,com.bokecc.stream.config.Config.RENDER_MODE_FIT);
                            mSurfaceView.setLayoutParams(params);
                            mSurfaceContainer.addView(mSurfaceView);
//                            mCCAtlasClient.setSubRender(mSurfaceView, null);
                            mMainVideoView.setSurfaceViewList(mSurfaceView);
                            switchVideo = false;
                        }
                        switchVideo = false;
                    }
                }
            } else {
                if (mMainVideoRenderer == null) {
                    mMainVideoRenderer = new SurfaceView(context);
                    RelativeLayout.LayoutParams params;
                    if (sClassDirection == 0) {
                        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    } else {
                        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    }
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);

                    mMainVideoRenderer.setLayoutParams(params);
                    mCCAtlasClient.initSurfaceContext(mMainVideoRenderer);
//                    mMainVideoRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    mSurfaceContainer.addView(mMainVideoRenderer);
                }
            }

            if (mMainVideoRenderer != null) {
                mMainVideoRenderer.setZOrderOnTop(false);
                mMainVideoRenderer.setZOrderMediaOverlay(false);
//                mMainVideoRenderer.cleanFrame();
                if (mMainVideoView.getStream().isLocalCameraStream()) { // 当前用户角色是教师，主视频显示自己的预览
                    mCCAtlasClient.attachLocalCameraStram(mMainVideoRenderer);
                } else {
//                    mMainVideoRenderer.setMirror(false);
                    mMainVideoView.getStream().attach(mMainVideoRenderer);
                }
            }

            mMainVideoName.setText(mMainVideoView.getStream().getUserName());
            mMicClose.setVisibility(View.VISIBLE);
            if (mMainVideoView.getStream().isAllowAudio()) {
                mMicClose.setImageResource(R.mipmap.mic_open_icon);
            } else {
                mMicClose.setImageResource(R.mipmap.mic_close_icon);
            }
            if (mMainVideoView.getStream().getUserName().equals(CCAtlasClient.SHARE_SCREEN_STREAM_NAME)) {
                mMicClose.setVisibility(View.GONE);
            }
            mDrawIcon.setVisibility(mMainVideoView.getStream().isAllowDraw() ? View.VISIBLE : View.GONE);
            //设为讲师
            mSetUpTeacherIcon.setVisibility(mMainVideoView.getStream().isSetupTeacher() ? View.VISIBLE : View.GONE);

            if (mCCAtlasClient.getInteractBean().getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_AUTO) {
                mLockIcon.setVisibility(mMainVideoView.getStream().isLock() ? View.VISIBLE : View.GONE);
            } else {
                mLockIcon.setVisibility(View.GONE);
            }
            // 学生仅音频模式
            if (mMainVideoView.getStream().getUserRole() != CCAtlasClient.PRESENTER &&
                    mMainVideoView.getStream().getUserRole() != CCAtlasClient.ASSISTANT &&
                    CCAtlasClient.getInstance().getMediaMode() == CCAtlasClient.MEDIA_MODE_AUDIO) {
                if (mMainVideoView.getStream().getUserId().equals(CCAtlasClient.SHARE_SCREEN_STREAM_ID)) {
                    mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
                } else {
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                    if (sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.mipmap.only_mic_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.mipmap.only_mic_bg_land);
                    }
                }
            } else {
                if (!mMainVideoView.getStream().isAllowVideo()) { // 关闭视频
                    mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                    if (sClassDirection == 0) {
                        mOtherLayout.setBackgroundResource(R.mipmap.camera_close_bg);
                    } else {
                        mOtherLayout.setBackgroundResource(R.mipmap.camera_close_bg_land);
                    }
                } else {
                    mOtherLayout.setVisibility(View.GONE);
                    if (mMainVideoView.getStream().getRemoteStream() != null) {
                        if (!mMainVideoView.getStream().getRemoteStream().hasAudio()) {
                            mMicClose.setVisibility(View.VISIBLE);
                            mMicClose.setImageResource(R.mipmap.no_mic_icon);
                        }
                        if (!mMainVideoView.getStream().getRemoteStream().hasVideo()) {
                            mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                            if (sClassDirection == 0) {
                                mOtherLayout.setBackgroundResource(R.mipmap.no_camera_icon);
                            } else {
                                mOtherLayout.setBackgroundResource(R.mipmap.no_camera_icon_land);
                            }
                        }
                    }
                }
            }
        } catch (StreamException e) {
            e.printStackTrace();
            mPreMainVideoView = null;
            if (mMainVideoRenderer != null) {
//                mMainVideoRenderer.cleanFrame();
//                mMainVideoRenderer.release();
                mMainVideoRenderer = null;
            }
        }
    }

    /**
     *
     * @param videoStreamView
     * @param isAdd
     */
    private synchronized void updateVideoList(VideoStreamView videoStreamView, boolean isAdd) {
        if (mVideoStreamViews == null || videoStreamView == null) {
            return;
        }
        int index;
        if (isAdd) {
            index = mVideoStreamViews.size();
            mVideoStreamViews.add(videoStreamView);
        } else {
            index = mVideoStreamViews.indexOf(videoStreamView);
            mVideoStreamViews.remove(videoStreamView);
        }
        if (index == -1 || !isViewInitialize) {
            return;
        }

        if (isAdd) {
            mVideoAdapter.notifyItemInserted(index);
        } else {
            mVideoAdapter.notifyItemRemoved(index);
        }
        if (index != mVideoStreamViews.size() - 1) {
            mVideoAdapter.notifyItemRangeChanged(index, mVideoStreamViews.size() - index);
        }
    }

    /**
     * 主视频模式需要更新主视图
     * @param userid
     * @param flag
     * @param isSelf
     * @param changed
     */
    public void updateVideos(String userid, boolean flag, boolean isSelf, int changed) {
        super.updateVideos(userid, flag, isSelf, changed);
        updateVideos(userid, flag, changed);
    }

    /**
     * 如果没有找到对应的数据 判断是否是主视频模式，对主视频进行修改
     * @param userid
     * @param flag
     * @param changed
     */
    public void updateVideos(String userid, boolean flag, int changed) {
        super.updateVideos(userid,flag,changed);
        if (mMainVideoView == null || !userid.equals(mMainVideoView.getStream().getUserId())) {
            return;
        }
        if (changed == 0) { // audio
            mMainVideoView.getStream().setAllowAudio(flag);
            if (flag) {
                mMicClose.setImageResource(R.mipmap.mic_open_icon);
            } else {
                mMicClose.setImageResource(R.mipmap.mic_close_icon);
            }
        } else if (changed == 1) {
            mMainVideoView.getStream().setAllowVideo(flag);
            if (sClassDirection == 0) {
                mOtherLayout.setBackgroundResource(R.mipmap.camera_close_bg);
            } else {
                mOtherLayout.setBackgroundResource(R.mipmap.camera_close_bg_land);
            }
            mOtherLayout.setVisibility(flag ? View.GONE : View.VISIBLE);
        } else if (changed == 2) {
            mDrawIcon.setVisibility(flag ? View.VISIBLE : View.GONE);
        } else if (changed == 3) {
            if (mCCAtlasClient.getInteractBean().getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_AUTO) {
                mLockIcon.setVisibility(flag ? View.VISIBLE : View.GONE);
            } else {
                mLockIcon.setVisibility(View.GONE);
            }
        } else if (changed == 4) {
            mSetUpTeacherIcon.setVisibility(flag ? View.VISIBLE : View.GONE);//设为讲师
        }

        // 学生仅音频模式
        if (mMainVideoView.getStream().getUserRole() != CCAtlasClient.PRESENTER &&
                mMainVideoView.getStream().getUserRole() != CCAtlasClient.ASSISTANT &&
                CCAtlasClient.getInstance().getMediaMode() == CCAtlasClient.MEDIA_MODE_AUDIO) {
            if (mMainVideoView.getStream().getUserId().equals(CCAtlasClient.SHARE_SCREEN_STREAM_ID)) {
                mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
            } else {
                mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                if (sClassDirection == 0) {
                    mOtherLayout.setBackgroundResource(R.mipmap.only_mic_bg);
                } else {
                    mOtherLayout.setBackgroundResource(R.mipmap.only_mic_bg_land);
                }
            }
        } else {
            if (!mMainVideoView.getStream().isAllowVideo()) { // 关闭视频
                mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                if (sClassDirection == 0) {
                    mOtherLayout.setBackgroundResource(R.mipmap.camera_close_bg);
                } else {
                    mOtherLayout.setBackgroundResource(R.mipmap.camera_close_bg_land);
                }
            } else {
                mOtherLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected int getAdapterType() {
        return 2;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mMainVideoLayout.setVisibility(visibility);
        mMainVideoTop.setVisibility(visibility);
    }

    /**
     * 跟随模式-切换主视频
     * @param userid
     */
    @Override
    public void switchMainVideo(String userid) {
        if (TextUtils.isEmpty(userid) || (mMainVideoView != null && mMainVideoView.getStream().getUserId().equals(userid))) {
            return;
        }

        for (int i = 0; i < mVideoStreamViews.size(); i++) {
            VideoStreamView videoStreamView = mVideoStreamViews.get(i);
            if (videoStreamView.getStream().getUserId().equals(userid)) {
                switchMainVideo(videoStreamView,i);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.id_main_video_container){
            if(onDisplayInterListener!=null)
                onDisplayInterListener.toggleTopAndBottom();
        }
    }
}
