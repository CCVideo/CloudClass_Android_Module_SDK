package com.bokecc.room.ui.view.video;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.bokecc.room.ui.view.video.adapter.VideoAdapter;
import com.bokecc.room.ui.view.video.listener.VideoViewListener;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.config.LogConfig;
import com.bokecc.sskt.base.common.exception.StreamException;
import com.bokecc.common.base.CCBaseActivity;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.view.video.listener.OnVideoClickListener;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.stream.bean.CCStream;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.bokecc.sskt.base.CCAtlasClient.ASSISTANT;
import static com.bokecc.sskt.base.CCAtlasClient.PRESENTER;
import static com.bokecc.sskt.base.CCAtlasClient.TALKER;

/**
 * 视频基础类
 * @author wy
 */
public abstract class BaseVideoManager {

    private final String TAG = BaseVideoManager.class.getName();

    /**视频组数据*/
    protected CopyOnWriteArrayList<VideoStreamView> mVideoStreamViews = new CopyOnWriteArrayList<>();

    /**视频组控件*/
    protected RecyclerView videosRecyclerView;
    /**视频组适配器*/
    protected VideoAdapter mVideoAdapter;

    /**视频点击回调*/
    protected OnVideoClickListener mVideoClickListener;
    /**回调监听*/
    protected VideoViewListener videoViewListener = null;
    /***/
    protected CCBaseActivity context;

    /**底层控制器*/
    protected CCAtlasClient mCCAtlasClient = CCAtlasClient.getInstance();

    /**横竖屏标志位*/
    protected int sClassDirection = 0;

    /**角色*/
    protected int mRole;


    /**
     * 初始化
     * @param context
     * @param stub
     */
    public BaseVideoManager(CCBaseActivity context,int sClassDirection, ViewStub stub){
        this.context = context;
        this.sClassDirection = sClassDirection;
        this.mRole = CCAtlasClient.getInstance().getRole();
        stub.inflate();
        initRecyclerView();
    }

    public void setVideoClickListener(OnVideoClickListener onVideoClickListener) {
        mVideoClickListener = onVideoClickListener;
    }

    public void setListener(VideoViewListener videoViewListener){
        this.videoViewListener = videoViewListener;
    }

    /**
     * 初始化适配器
     */
    private void initRecyclerView(){
        //初始化适配器
        mVideoAdapter = new VideoAdapter(context,sClassDirection);
        mVideoAdapter.bindDatas(mVideoStreamViews);
        mVideoAdapter.setType(getAdapterType());
        mVideoAdapter.setOnClickVideoListening(new VideoAdapter.OnClickVideoListening() {
            @Override
            public void onClick(int position) {
                recyclerViewClickListening(position);
            }
        });

        //初始化控件
        videosRecyclerView = context.findViewById(getRecyclerViewId());
        videosRecyclerView.setAdapter(mVideoAdapter);
        RecyclerView.LayoutManager layoutManager = getRecyclerViewLayoutManager();
        if(layoutManager != null){
            videosRecyclerView.setLayoutManager(getRecyclerViewLayoutManager());
        }
        RecyclerView.ItemDecoration decoration = getRecyclerViewItemDecoration();
        if(decoration != null){
            videosRecyclerView.addItemDecoration(decoration);
        }
    }

    /**获取适配器布局类型*/
    protected abstract int getAdapterType();
    /**初始化视频组控件*/
    protected abstract int getRecyclerViewId();
    /**获取视频列表布局管理器*/
    protected abstract RecyclerView.LayoutManager getRecyclerViewLayoutManager();
    /**获取视频列表布局样式*/
    protected abstract RecyclerView.ItemDecoration getRecyclerViewItemDecoration();

    /**
     * 视频点击回调，子类视情况实现
     * @param position
     */
    protected abstract void recyclerViewClickListening(int position);

    /**
     * 移除不在列表的视频流，包括放大，文档视频，主视频等
     * @param userId
     */
    protected void removeVideoView(String userId){}


    /**
     *
     * @param userid
     * @param flag
     * @param changed
     */
    public void updateVideos(String userid, boolean flag, int changed) {
        for (int i = 0; i < mVideoStreamViews.size(); i++) {
            VideoStreamView videoStreamView = mVideoStreamViews.get(i);
            if (videoStreamView.getStream().getUserId().equals(userid)) {
                if (changed == 0) {
                    videoStreamView.getStream().setAllowAudio(flag);
                } else if (changed == 1) {
                    videoStreamView.getStream().setAllowVideo(flag);
                } else if (changed == 2) {
                    videoStreamView.getStream().setAllowDraw(flag);
                } else if (changed == 3) {
                    videoStreamView.getStream().setLock(flag);
                } else if (changed == 4) {
                    videoStreamView.getStream().setSetupTeacher(flag);
                }
                mVideoAdapter.update(i, videoStreamView, changed);
                if(mRole==CCAtlasClient.PRESENTER&&mVideoAdapter.getmType()==4)
                    notifyItemChanged(videoStreamView, mVideoStreamViews.size(), true);
                break;
            }
        }
    }

    /**
     * 更新视频相关信息
     * @param userid
     * @param flag
     * @param isSelf
     * @param changed
     */
    public void updateVideos(String userid, boolean flag, boolean isSelf, int changed) {
        for (int i = 0; i < mVideoStreamViews.size(); i++) {
            VideoStreamView videoStreamView = mVideoStreamViews.get(i);
            if (videoStreamView.getStream().getUserId().equals(userid)) {
                if (changed == 0) {
                    videoStreamView.getStream().setAllowAudio(flag);
                    if (mCCAtlasClient.getUserIdInPusher().equals(userid) && !isSelf) {
                        Tools.showToast(flag ? "您被老师开启麦克风" : "您被老师关闭麦克风",false);
                    }
                } else if (changed == 1) {
                    videoStreamView.getStream().setAllowVideo(flag);
                } else if (changed == 2) {
                    videoStreamView.getStream().setAllowDraw(flag);
                } else if (changed == 3) {
                    videoStreamView.getStream().setLock(flag);
                } else if (changed == 4) {
                    videoStreamView.getStream().setSetupTeacher(flag);
                }
                mVideoAdapter.update(i, videoStreamView, changed);
                notifyItemChanged(videoStreamView, mVideoStreamViews.size(), true);
                return;
            }
        }
    }

    /***/
    private final Object lock = new Object();

    /**
     * 订阅消息回调
     */
    private final class SubCallBack implements CCAtlasCallBack<CCStream> {

        private VideoStreamView videoStreamView;

        SubCallBack(VideoStreamView videoStreamView) {
            this.videoStreamView = videoStreamView;
        }

        @Override
        public void onSuccess(final CCStream stream) {
            //流的回调是异步过程，所以往视图添加时需要加锁，避免添加流到相同位置
            synchronized (lock){
                Tools.log(LogConfig.ADDVIDEOVIEW,"3.SubCallBack_success:"+stream.getUserid());
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //2、3平台
                        if (stream != null && stream.getSurfaceView() != null) {
                            videoStreamView.setSurfaceViewList(stream.getSurfaceView());
                        }

                        //是不是全体关麦
                        if (videoStreamView.getStream().getUserRole() != PRESENTER) {
                            if (mCCAtlasClient.getInteractBean() != null && !mCCAtlasClient.getInteractBean().isAllAllowAudio()) {
                                videoStreamView.getStream().setAllowAudio(mCCAtlasClient.getInteractBean().isAllAllowAudio());
                            }
                        }

                        if(videoStreamView.getStream().getUserRole() != TALKER){
                            if(mCCAtlasClient.getUserList() != null){
                                for(CCUser users: mCCAtlasClient.getUserList()){
                                    if(videoStreamView.getStream().getRemoteStream().getUserid()
                                            .equals(users.getUserId()) && !users.getUserSetting()
                                            .isAllowAudio()){
                                        videoStreamView.getStream().setAllowAudio(false);
                                    }

                                    if(videoStreamView.getStream().getRemoteStream().getUserid()
                                            .equals(users.getUserId()) && users.getUserSetting()
                                            .isAllowAudio()){
                                        videoStreamView.getStream().setAllowAudio(true);
                                    }
                                }
                            }
                        }

                        //添加流位置
                        int position;
                        if (videoStreamView.getStream().getUserRole() == PRESENTER ||
                                videoStreamView.getStream().getUserRole() == ASSISTANT) {
                            position = 0;
                        } else {
                            if (mVideoStreamViews == null) {
                                return;
                            }
                            position = mVideoStreamViews.size();
                        }
                        //显示对应视频框
                        if (mVideoStreamViews != null && videoStreamView != null) {
                            Tools.log(LogConfig.ADDVIDEOVIEW,"4.notifyItemChanged:"+stream.getUserid()+",views:"+mVideoStreamViews.size()+",position:"+position);
                            notifyItemChanged(videoStreamView, position, true);
                        }

                        //如果房间配置设置为关闭音频，那么需要关闭音频。
                        if (videoStreamView.getStream().getUserRole() == TALKER && mCCAtlasClient.getInteractBean() != null &&
                                mCCAtlasClient.getInteractBean().getTalkerOpenAudio() == 0) {
                            mCCAtlasClient.pauseAudio(videoStreamView.getStream().getRemoteStream(), null);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(int errCode, String errMsg) {
            Tools.log(LogConfig.ADDVIDEOVIEW,"SubCallBack_fail:"+errMsg);
            Tools.showToast(errMsg,false);
        }

    }

    /**
     * 添加远程流
     * @param stream
     */
    public synchronized void addStreamView(SubscribeRemoteStream stream) {
        try {
            VideoStreamView videoStreamView = new VideoStreamView();
            videoStreamView.setStream(stream);
            //订阅远程流
            mCCAtlasClient.SubscribeStream(stream.getRemoteStream(),com.bokecc.stream.config.Config.RENDER_MODE_FIT,new SubCallBack(videoStreamView));
        } catch (StreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除远程流
     * @param stream
     */
    public synchronized void removeStreamView(SubscribeRemoteStream stream) {
        try {
            VideoStreamView tempView = null;
            int position = -1;
            if (mVideoStreamViews.size() != 0) {
                for (int i = 0; i < mVideoStreamViews.size(); i++) {
                    VideoStreamView streamView = mVideoStreamViews.get(i);
                    if (streamView.getStream().getUserId().equals(stream.getUserId())) {
                        tempView = streamView;
                        position = i;
                        break;
                    }
                }
            }

            if (tempView == null) {//移除非列表流
                removeVideoView(stream.getUserId());
            }

            notifyItemChanged(tempView, position, false);

            //取消订阅
            mCCAtlasClient.unSubscribeStream(stream.getRemoteStream(), null);
        } catch (StreamException e) {
            e.printStackTrace();
        }
    }

    /***
     * 添加本地流
     * @param videoStreamView
     */
    public void addVideoView(VideoStreamView videoStreamView){
        notifyItemChanged(videoStreamView,mVideoStreamViews.size(),true);
    }

    /**
     * 删除本地流
     * @param videoStreamView
     */
    public void removeVideoView(VideoStreamView videoStreamView){
        int position = mVideoStreamViews.indexOf(videoStreamView);
        if(position < 0){//移除非列表流
            if(videoStreamView != null && videoStreamView.getStream() != null){
                removeVideoView(videoStreamView.getStream().getUserId());
            }
        }
        notifyItemChanged(videoStreamView,position,false);
    }

    /**
     * 刷新列表视图
     * @param videoStreamView
     * @param position
     * @param isAdd
     */
    protected abstract void notifyItemChanged(VideoStreamView videoStreamView, int position, boolean isAdd);

    /**
     * 清除资源
     */
    public void onClearDatas(){
        if(mVideoStreamViews != null){
            mVideoStreamViews.clear();
        }
    }

    /**
     * 设置当前视频是否显示
     * 只有在视频播放中才会起作用
     * @param visibility
     */
    public void setRecyclerViewVisibility(int visibility){
        if(visibility == View.GONE || visibility == View.INVISIBLE){
            dismissScreen();
        }else if(visibility == View.VISIBLE){
            showScreen();
        }
    }

    /**
     * 隐藏视图
     */
    protected void dismissScreen(){
        mVideoAdapter.bindDatas(new ArrayList<VideoStreamView>());
        mVideoAdapter.notifyDataSetChanged();
    }

    /**
     * 显示视图
     */
    protected void showScreen(){
        mVideoAdapter.bindDatas(mVideoStreamViews);
        mVideoAdapter.notifyDataSetChanged();
    }

    /**
     * 隐藏视图
     * @param visibility
     */
    public void setVisibility(int visibility){}

    /**
     * 将视频显示到文档区域
     * @param userid
     * @param mode
     */
    public void setVideoToDoc(String userid, String mode){}

    /**
     * 切换主视频
     * @param userid
     */
    public void switchMainVideo(String userid){}

}
