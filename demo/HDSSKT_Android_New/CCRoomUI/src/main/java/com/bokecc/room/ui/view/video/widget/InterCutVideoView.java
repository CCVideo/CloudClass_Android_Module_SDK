package com.bokecc.room.ui.view.video.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 插播视频悬浮控件
 * （1）悬浮于整个窗口最上方；
 * （2）支持手势拖动；
 * （3）双击放大，全屏显示；
 * 注意：
 * 和悬浮视频控件的区别在于视频的播放方式：插播视频使用B站ijk播放器
 * @author wy
 */
public class InterCutVideoView extends FrameLayout {

    private final String TAG = "InterCutVideoView";

    /**跟视图*/
    private FrameLayout mRemoteVideoContainer;
    /**视频视图*/
    private SurfaceView mSurfaceView;
    /**暂停按钮*/
    private ImageView mRemoteVideoPause;
    /**退出全屏按钮*/
    private ImageView mRemoteVideoExit;

    /**回调事件监听*/
    private VideoEventListener videoEventListener;

    /**???*/
//    private boolean needRestore = false;
    /**???*/
    private boolean needInitVideoPlayer = false;

    /**是否因退到后台而暂停*/
    private boolean isOnPause = false;


    public InterCutVideoView(Context context) {
        super(context);
        initView(context);
    }

    public InterCutVideoView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView(context);
    }

    public InterCutVideoView(Context context, AttributeSet attrs, int deStyleAttr){
        super(context,attrs,deStyleAttr);
        initView(context);
    }

    /**
     * 设置监听
     * @param videoEventListener
     */
    public void setVideoEventListener(VideoEventListener videoEventListener){
        this.videoEventListener = videoEventListener;
    }

    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_video_inter_cut_layout,this,true);

        mRemoteVideoContainer = findViewById(R.id.view_video_suspension_fl);
        mRemoteVideoPause = findViewById(R.id.view_video_suspension_pause);
        mRemoteVideoExit = findViewById(R.id.view_video_suspension_exit_fullscreen);
        mRemoteVideoExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFullScreenVideo();
            }
        });

        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRemoteVideoContainer.getLayoutParams();
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                fullScreenVideo(params);
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //计算移动的距离
                int offX = (int) (e2.getX() - e1.getX());
                int offY = (int) (e2.getY() - e1.getY());
                params.topMargin = params.topMargin + offY;
                params.leftMargin = params.leftMargin + offX;
                if (params.topMargin < 0) {
                    params.topMargin = 0;
                }
                if (params.leftMargin < 0) {
                    params.leftMargin = 0;
                }
                if (params.topMargin > (Tools.getScreenHeight() - mSurfaceView.getHeight())) {
                    params.topMargin = Tools.getScreenHeight() - mSurfaceView.getHeight();
                }
                if (params.leftMargin > (Tools.getScreenWidth() - mSurfaceView.getWidth())) {
                    params.leftMargin = Tools.getScreenWidth() - mSurfaceView.getWidth();
                }
                mRemoteVideoContainer.setLayoutParams(params);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        mRemoteVideoContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isRemoteVideoFullScreen && gestureDetector.onTouchEvent(event);
            }
        });


        mSurfaceView = findViewById(R.id.view_video_suspension_sv);
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setZOrderMediaOverlay(true);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    Log.e(TAG, "surfaceCreated: ");
                    if (mPlayerMap != null) {
                      /*  IMediaPlayer playerAudio = mPlayerMap.get("audioMedia");

                        if (playerAudio != null) {
                            long currentPosition = playerAudio.getCurrentPosition();
                            if (isAudioPlay) {
                                playerAudio.start();
                                if(currentPosition!=0){
                                    playerAudio.seekTo(syncMediaTime);
                                }
                            }
                        }*/
                        IMediaPlayer playerVideo = mPlayerMap.get("videoMedia");
                        if (playerVideo != null) {
                            playerVideo.setDisplay(holder);
                            long currentPosition = playerVideo.getCurrentPosition();
                            if(isVideoPlay){
                                playerVideo.start();
                                if (currentPosition != 0) {
                                    playerVideo.seekTo(syncMediaTime);
                                }
                            }else {
//                                showPauseButton();
//                                if (mRemoteVideoPause.getVisibility() != View.VISIBLE) {
//                                    mRemoteVideoPause.setVisibility(View.VISIBLE);
//                                }
                            }
                        }
                    }
                    /*if (needRestore) {
                        IMediaPlayer player = mPlayerMap.get("videoMedia");
                        if (player != null) {
                            player.setDisplay(holder);
                            if ((int) player.getCurrentPosition() != 0) {
                                player.seekTo(syncMediaTime);
                            } else {
                                if (isVideoPlay) {
                                    player.start();
                                }
                            }
                        }
                        IMediaPlayer playerAudio = mPlayerMap.get("audioMedia");
                        if (playerAudio != null) {
                            playerAudio.setDisplay(holder);
                            if ((int) playerAudio.getCurrentPosition() != 0) {
                                playerAudio.seekTo(syncMediaTime);
                            } else {
                                if (isVideoPlay) {
                                    playerAudio.start();
                                }
                            }
                        }
                        needRestore = false;
                    } else if (needInitVideoPlayer) {
//                        if (mPauseMedia != null) {
//                            String type = mPauseMedia.getString("type");
//                            initMediaPlayer(type.equals("videoMedia"), mPauseMedia.getJSONObject("msg").getString("src"));
//                            mPauseMedia = null;
//                        } else {
//                            isVideoPlay = mCCAtlasClient.getInteractBean().getVideo().getString("status").equals("1");
//                            if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getMediaId() != null) {
//                                mCCAtlasClient.getPlayerVideoUrl(mCCAtlasClient.getInteractBean().getMediaId(), new CCAtlasCallBack<String>() {
//                                    @Override
//                                    public void onSuccess(String url) {
//                                        if (url != null) {
//                                            if (url.contains(".pcm")) {
//                                                String realplayUrl = "http://127.0.0.1:" + getDrmPort() + "/?url=" + URLEncoder.encode(url);
////                                            DESUtil.token = pcmToken;
//                                                drmServer.reset();
//                                                initMediaPlayer(true, realplayUrl);
//                                            } else {
//                                                try {
//                                                    initMediaPlayer(true, mCCAtlasClient.getInteractBean().getVideo().getString("src"));
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(int errCode, String errMsg) {
//
//                                    }
//                                });
//                            }
//                        }
//                        needInitVideoPlayer = false;
                    }
//                    if (mPlayerMap.get("videoMedia").isPlaying()) {//防止黑流
//                        mPlayerMap.get("videoMedia").setDisplay(holder);
//                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e(TAG, "surfaceDestroyed: ");
                if (mPlayerMap.containsKey("videoMedia")) {
                    IMediaPlayer player = mPlayerMap.get("videoMedia");
                    if (player != null) {
                        player.pause();
                    }
                }
                /*if (mPlayerMap.containsKey("audioMedia")) {
                    IMediaPlayer playerAudio = mPlayerMap.get("audioMedia");
                    if (playerAudio != null) {
                        player.setDisplay(null);
                        playerAudio.pause();

                    }
                }*/
                /*if (mPlayerMap.containsKey("videoMedia")) {
                    IMediaPlayer player = mPlayerMap.get("videoMedia");
                    if (player != null) {
                        player.pause();
                        player.setDisplay(null);
                        needRestore = true;
                    }
                }
                if (mPlayerMap.containsKey("audioMedia")) {
                    IMediaPlayer playerAudio = mPlayerMap.get("audioMedia");
                    if (playerAudio != null) {
                        playerAudio.pause();
                        playerAudio.setDisplay(null);
                        needRestore = true;
                    }
                }*/
            }
        });
    }

    /**
     * 显示暂停按钮
     */
    private void showPauseButton() {

    }

    /**
     * 同步音视频播放进度
     * @param jsonObject
     */
    public void syncMediaTime(JSONObject jsonObject){
        try {
            JSONObject dataJson = jsonObject.getJSONObject("data");
            syncMediaTime = (int) ((dataJson.getDouble("current_time")) * 1000);
            Log.i(TAG, "wdh----->OnMediaSync: " + syncMediaTime);
            String mediaTime = dataJson.getString("current_time");
            if (player != null) {
                if (player.isPlaying() && Math.abs(Float.parseFloat(mediaTime) - (float) (player.getCurrentPosition() / 1000)) >= 3) {
                    player.seekTo((long) ((dataJson.getDouble("current_time")) * 1000));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置初始状态
     * @param isVideo true播放视频/false播放音频
     * @param isPlayStatus
     * @param src
     */
    public void initStatus(boolean isVideo,boolean isPlayStatus,String src){
        if (isVideo) {
            isVideoPlay = isPlayStatus;
        }else{
            isAudioPlay = isPlayStatus;
        }
        isPause = !isPlayStatus;
        initMediaPlayer(isVideo, src);
    }

    /**
     * 设置当前视频是否显示
     * 只有在视频播放中才会起作用
     * @param visibility
     */
    public void setICVVVisibility(int visibility){
        if(visibility == View.GONE || visibility == View.INVISIBLE){
            dismissRemoteVideoByAnim();
        }else if(visibility == View.VISIBLE){
            showRemoteVideoByAnim();
        }
    }


    private HashMap<String, IMediaPlayer> mPlayerMap = new HashMap<>();
    private HashMap<IMediaPlayer, Integer> mPlayerStatus = new HashMap<>();
    private HashMap<IMediaPlayer, Boolean> mPlayerType = new HashMap<>();



    //插播音视频同步的本地时间
    private long currentTime = 0;

    /**视频开始是暂停状态*/
    private boolean isPause = false;

    /***/
    private boolean isMiss = false,isVideoPlay = false,isAudioPlay = false;

    public boolean isRemoteVideoFullScreen() {
        return isRemoteVideoFullScreen;
    }

    /**是否全屏*/
    private boolean isRemoteVideoFullScreen = false;

    private int syncMediaTime;

    /**
     * 开启插播音视频
     * @param media
     */
    public void startInterludeMedia(final JSONObject media){
        try {
            String handler = media.getString("handler");
            final String type = media.getString("type");

            String timeId = media.optString("timeId");
            if(!TextUtils.isEmpty(timeId)){
                if (currentTime >= Long.valueOf(timeId)) {
                    return;
                } else {
                    currentTime = Long.valueOf(timeId);
                }
            }
            //
            if (handler.equals("init")) {
                isMiss = false;
                if (type.equals("videoMedia")) {
                    isVideoPlay = false;
                } else {
                    isAudioPlay = false;
                }

                initMediaPlayer(type.equals("videoMedia"), media.getJSONObject("msg").getString("src"));

                //这里有对pcm的特殊处理，暂时不知道作用 todo
//                String videoId = "videoId";
//                if (!media.getJSONObject("msg").isNull(videoId)) {
//                    mCCAtlasClient.getPlayerVideoUrl(media.getJSONObject("msg").getString(videoId), new CCAtlasCallBack<String>() {
//                        @Override
//                        public void onSuccess(String url) {
//                            if (url != null) {
//                                if (url.contains(".pcm")) {
//                                    String realplayUrl = "http://127.0.0.1:" + getDrmPort() + "/?url=" + URLEncoder.encode(url);
//                                    drmServer.reset();
//                                    initMediaPlayer(type.equals("videoMedia"), realplayUrl);
//                                } else {
//                                    try {
//                                        initMediaPlayer(type.equals("videoMedia"), media.getJSONObject("msg").getString("src"));
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int errCode, String errMsg) {
//
//                        }
//                    });
//                } else {
//                    initMediaPlayer(type.equals("videoMedia"), media.getJSONObject("msg").getString("src"));
//                }

            } else {
                IMediaPlayer mediaPlayer = mPlayerMap.get(type);
                switch (handler) {
                    case "play":

                        showRemoteVideoByAnim();

                        if (type.equals("videoMedia")) {
                            isVideoPlay = true;
                            isPause = false;
                            if (getVisibility() == View.GONE) {
                                setVisibility(View.VISIBLE);
                                mSurfaceView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            isAudioPlay = true;
                            isPause = false;
                        }
                        if (mRemoteVideoPause.getVisibility() == View.VISIBLE) {
                            mRemoteVideoPause.setVisibility(View.GONE);
                        }
                        if(isOnPause){
                            return;
                        }
                        if (mediaPlayer == null) {
                            Tools.showToast("播放器未初始化");
                            return;
                        }
                        if ((mPlayerStatus.get(mediaPlayer) == 1||mPlayerStatus.get(mediaPlayer) == 0) && !mediaPlayer.isPlaying()) {
                            if (type.equals("videoMedia") && isPause) {
                                return;
                            }
                            if (type.equals("audioMedia") && isPause) {
                                return;
                            }
                            Log.e(TAG, "start: ");
                            mediaPlayer.start();


                        } else {
                            Log.e(TAG, "player: [ " + mPlayerStatus.get(mediaPlayer) + "] [ " + mediaPlayer.isPlaying() + " ]");
                        }
                        break;
                    case "pause":
                        if (type.equals("videoMedia")) {
                            isVideoPlay = false;
                        } else {
                            isAudioPlay = false;
                        }
                        if (mediaPlayer == null) {
                            Tools.showToast("播放器未初始化");
                            return;
                        }
                        if (type.equals("videoMedia")&&mRemoteVideoPause.getVisibility() != View.VISIBLE) {
                            mRemoteVideoPause.setVisibility(View.VISIBLE);
                        }
                        if ((mPlayerStatus.get(mediaPlayer) == 1||mPlayerStatus.get(mediaPlayer) == 0) && mediaPlayer.isPlaying()) {
                            if (type.equals("videoMedia") && isPause) {
                                return;
                            }
                            if (type.equals("audioMedia") && isPause) {
                                return;
                            }
//                            Log.e(TAG, "wdh------>pause: " + syncMediaTime);
                            mediaPlayer.pause();


                        } else {
                            Log.e(TAG, "player: [ " + mPlayerStatus.get(mediaPlayer) + "] [ " + mediaPlayer.isPlaying() + " ]");
//                                    showToast(mPlayerStatus.get(mediaPlayer) + "-" + mediaPlayer.isPlaying());
                        }
                        break;
                    case "timeupdate":
                        if(isOnPause){
                            return;
                        }
                        if (mediaPlayer == null) {
                            Tools.showToast("播放器未初始化");
                            return;
                        }
                        if (mPlayerStatus.get(mediaPlayer) == 1) {
                            if (type.equals("videoMedia") && isPause) {
                                return;
                            }
                            if (type.equals("audioMedia") && isPause) {
                                return;
                            }
                            Log.i(TAG, "wdh----->onInteractEvent: " + (int) (media
                                    .getJSONObject("msg").getDouble("time") * 1000));
                            mediaPlayer.seekTo((int) (media.getJSONObject("msg").getDouble("time") * 1000));
                        } else {
                            Log.e(TAG, "player: [ " + mPlayerStatus.get(mediaPlayer) + "] [ " + mediaPlayer.isPlaying() + " ]");
//                                    showToast(mPlayerStatus.get(mediaPlayer) + "-" + mediaPlayer.isPlaying());
                        }
                        break;
                    case "close":
                        mPlayerMap.remove(type); // 移除对应的播放器
                        // 视频去掉播放框
                        if (type.equals("videoMedia") && getVisibility() == View.VISIBLE) {
                            setVisibility(View.GONE);
                            mSurfaceView.setVisibility(View.GONE);
                            mRemoteVideoExit.setVisibility(View.GONE);
                            isRemoteVideoFullScreen = false;
                        }
                        if (mediaPlayer == null) {
                            Tools.showToast("播放器未初始化");
                            return;
                        }
                        if (mPlayerStatus.get(mediaPlayer) == 1) {
                            mediaPlayer.stop();
                        }
                        mPlayerStatus.remove(mediaPlayer);
                        mPlayerType.remove(mediaPlayer);
                        mediaPlayer.release();
                        if(videoEventListener != null){
                            videoEventListener.exitFullScreen();
                        }
                        break;
                    default:
                        Tools.showToast("未知操作！");
                        break;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "插播音视频数据解析异常: [ " + e.getMessage() + " ]");
            Tools.showToast("插播音视频数据解析异常 [ " + e.getMessage() + " ]");
            // TODO: 2017/11/13  是否需要释放播放器资源和处理播放框（如果显示）
        }
    }

    /**
     * 移动视图到可视区域内
     */
    private void showRemoteVideoByAnim() {
        if (getVisibility() == View.VISIBLE) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mRemoteVideoContainer, "translationX", 0);
            animator.setDuration(50);
            animator.start();
        }
    }

    /**
     * 移动视图到可视区域外
     */
    private void dismissRemoteVideoByAnim() {
        if (getVisibility() == View.VISIBLE) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRemoteVideoContainer.getLayoutParams();
            ObjectAnimator animator = ObjectAnimator.ofFloat(mRemoteVideoContainer, "translationX", -(params.leftMargin + mRemoteVideoContainer.getWidth()));
            animator.setDuration(50);
            animator.start();
        }
    }


    private IMediaPlayer player = null;
    private void initMediaPlayer(final boolean isVideo, String mediaPath) {
        player = mPlayerMap.get(isVideo ? "videoMedia" : "audioMedia");
        try {
            if (player != null) {
                mPlayerMap.remove(isVideo ? "videoMedia" : "audioMedia");
                if (isVideo) {
                    player.setDisplay(null);
                    if (getVisibility() == View.VISIBLE) { // 视频
                        //  去除上一个界面
                        setVisibility(View.GONE);
                        mSurfaceView.setVisibility(View.GONE);
                        mRemoteVideoExit.setVisibility(View.GONE);
                    }
                }


                if (mPlayerStatus.get(player) == 1) {
                    player.stop();
                }
                mPlayerType.remove(player);
                mPlayerStatus.remove(player);
                player.release();
                player = null;
            }
            if(isVideo&&!isVideoPlay&&mRemoteVideoPause.getVisibility() == View.GONE){
                mRemoteVideoPause.setVisibility(View.VISIBLE);
            }
            player = new IjkMediaPlayer();
            ((IjkMediaPlayer) player).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
            mPlayerMap.put(isVideo ? "videoMedia" : "audioMedia", player);
            mPlayerType.put(player, isVideo);
            mPlayerStatus.put(player, 0);
            if (isVideo && getVisibility() != View.VISIBLE) { // 视频
                //  显示视频区域
                setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(Tools.dipToPixel(160), ViewGroup.LayoutParams.WRAP_CONTENT);
                videoParams.gravity = Gravity.CENTER;
                mSurfaceView.setLayoutParams(videoParams);
                mSurfaceView.setVisibility(View.VISIBLE);
                isRemoteVideoFullScreen = false;
            }
            player.setLooping(true); // 设置循环播放
            player.setScreenOnWhilePlaying(true);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            ((IjkMediaPlayer)player).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            Log.e(TAG, "initMediaPlayer: ");
            player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final IMediaPlayer mp) {
                    changeFramLayoutVideo(mp, mSurfaceView, false);
//                    if (isPause) {
//                        mPlayerMap.remove(isVideo ? "videoMedia" : "audioMedia");
//                        mPlayerType.remove(mp);
//                        mPlayerStatus.remove(mp);
//                        mp.stop();
//                        mp.pause();
//                        mp.setDisplay(null);
//                        if (mRemoteVideoPause.getVisibility() == View.GONE) {
//                            mRemoteVideoPause.setVisibility(View.VISIBLE);
//                        }
//                        mp.release();
//                        return;
//                    }

                    Log.e(TAG, "onPrepared: ");
                    if (mPlayerType.get(mp)) {
                        mp.setDisplay(mSurfaceView.getHolder());
                        //  重新进行尺寸调整
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRemoteVideoContainer.getLayoutParams();
                        params.width = Tools.dipToPixel(160);
                        params.height = Tools.dipToPixel(90);
                        mRemoteVideoContainer.setLayoutParams(params);
                    }
                    mPlayerStatus.put(mp, 1);
                    if (isMiss) {
                        int msec;
                        if (mPlayerType.get(mp)) {
                            msec = videoEventListener.getVideoCurrentTime();
                        } else {
                            msec = videoEventListener.getAudioCurrentTime();
                        }
                        if (msec > 0) {
                            mp.seekTo(msec);
                        }
                    } else {
                        if (mPlayerType.get(mp)) { // 视频
                            if (isVideoPlay) {
                                mp.start();
                            }
                        } else { // 音频
                            if (isAudioPlay) {
                                mp.start();
                            }
                        }
                        if (isAudioPlay&&mRemoteVideoPause.getVisibility() == View.VISIBLE) {
                            mRemoteVideoPause.setVisibility(View.GONE);
                        }
                    }

                    //如果视频是暂停状态
                    if(isPause) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mp.pause();
                            }
                        }, 2000);
                    }

                }
            });
            player.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(final IMediaPlayer mp) {
                    if (mPlayerType.get(mp)) { // 视频
                        if (!isVideoPlay && mp.isPlaying()) {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    mp.pause();
                                }
                            }, 1000);
                            if (mRemoteVideoPause.getVisibility() == View.GONE) {
//                                mRemoteVideoPause.setVisibility(View.VISIBLE);
                            }
                        }

                        if (isVideoPlay && !mp.isPlaying()) {
                            mp.start();
                            if (mRemoteVideoPause.getVisibility() == View.VISIBLE) {
                                mRemoteVideoPause.setVisibility(View.GONE);
                            }
                        }
                    } else { // 音频
                        if (!isAudioPlay && mp.isPlaying()) {
                            mp.pause();
                        }
                        if (isAudioPlay && !mp.isPlaying()) {
                            mp.start();
                        }
                    }
                }
            });
            player.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer mp, int what, int extra) {
                    mPlayerStatus.put(mp, -1);
                    mp.reset();
                    mp.release(); // 释放播放器资源
                    Log.e(TAG, "onError: [ " + what + "-" + extra + " ]");
                    Tools.showToast("播放出错 [ " + what + "-" + extra + " ]");
                    // 移除播放器
                    if (mPlayerType.get(mp)) {
                        mPlayerMap.remove("videoMedia");
                    } else {
                        mPlayerMap.remove("audioMedia");
                    }
                    // 视频去掉播放框
                    if (mPlayerType.get(mp) && getVisibility() == View.VISIBLE) {
                        setVisibility(View.GONE);
                        mSurfaceView.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
            player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {

                }
            });
            player.setDataSource(mediaPath);
            player.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "initMediaPlayer: [ " + e.toString() + " ]");
            Tools.showToast("初始化播放器失败 [ " + e.toString() + " ]");
            if (player != null) {
                mPlayerMap.remove(isVideo ? "videoMedia" : "audioMedia");
                mPlayerType.remove(player);
                mPlayerStatus.remove(player);
                player.stop();
                player.setDisplay(null);
                player.release();
            }
        }
    }



    /**视频放大前的坐标点*/
    private int mRemoteVideoLeft = 0, mRemoteVideoTop = 0;

    /**
     * 开启全屏
     * @param params
     */
    private void fullScreenVideo(FrameLayout.LayoutParams params){
        isRemoteVideoFullScreen = true;
        mRemoteVideoExit.setVisibility(View.VISIBLE);

        mRemoteVideoLeft = params.leftMargin;
        mRemoteVideoTop = params.topMargin;

        FrameLayout.LayoutParams temp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        temp.leftMargin = 0;
        temp.topMargin = 0;
        mRemoteVideoContainer.setLayoutParams(temp);

        changeFramLayoutVideo(mPlayerMap.get("videoMedia"), mSurfaceView, true);

        if(videoEventListener != null){
            videoEventListener.fullScreen();
        }
    }

    /**
     * 退出全屏
     */
    public void exitFullScreenVideo() {
        isRemoteVideoFullScreen = false;
        mRemoteVideoExit.setVisibility(View.GONE);

        FrameLayout.LayoutParams temp = new FrameLayout.LayoutParams(Tools.dipToPixel(160), Tools.dipToPixel(90));
        temp.leftMargin = mRemoteVideoLeft;
        temp.topMargin = mRemoteVideoTop;
        mRemoteVideoContainer.setLayoutParams(temp);

        changeFramLayoutVideo(mPlayerMap.get("videoMedia"), mSurfaceView, false);

        if(videoEventListener != null){
            videoEventListener.exitFullScreen();
        }
    }

    /**
     *
     * @param mp
     * @param surfaceView
     * @param isFull
     */
    private void changeFramLayoutVideo(IMediaPlayer mp,SurfaceView surfaceView, boolean isFull) {
        // 首先取得video的宽和高
        if (mp == null) {
            return;
        }
        int vWidth = mp.getVideoWidth();
        int vHeight = mp.getVideoHeight();

        int lw;
        int lh;
        if (isFull) {
            lw = Tools.getScreenWidth();
            lh = Tools.getScreenHeight();
        } else {
            lw = Tools.dipToPixel(160);
            lh = Tools.dipToPixel(90);
        }

        float max;

        // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) vWidth / (float) lw, (float) vHeight / (float) lh);
        } else {
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) vWidth / (float) lw), (float) vHeight / (float) lh);
        }
        // 选择大的一个进行缩放

        vWidth = (int) Math.ceil((float) vWidth / max);
        vHeight = (int) Math.ceil((float) vHeight / max);

        // 设置surfaceView的布局参数
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(vWidth, vHeight);
        lp.gravity = (Gravity.CENTER);
        surfaceView.setLayoutParams(lp);
    }

    /**
     * 生命周期 onResume
     */
    public void onResume() {
        isOnPause = false;
        if (needInitVideoPlayer) {
            mRemoteVideoContainer.setVisibility(View.VISIBLE);
            mSurfaceView.setVisibility(View.VISIBLE);
        }
        if (mPlayerMap != null) {
            IMediaPlayer playerAudio = mPlayerMap.get("audioMedia");

            if (playerAudio != null) {
                long currentPosition = playerAudio.getCurrentPosition();
                if (isAudioPlay) {
                    playerAudio.start();
                    if (currentPosition != 0) {
                        playerAudio.seekTo(syncMediaTime);
                    }
                }
            }
        }
       /* if (mPlayerMap != null) {
            IMediaPlayer playerAudio = mPlayerMap.get("audioMedia");
            if (playerAudio != null && isAudioPlay) {
                if ((int) playerAudio.getCurrentPosition() != 0) {
                    playerAudio.seekTo(syncMediaTime);
                } else {
                    playerAudio.start();
                }
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    IMediaPlayer playerVideo = mPlayerMap.get("videoMedia");
                    if (playerVideo != null) {
                        if(isVideoPlay){
                            playerVideo.setDisplay(mSurfaceView.getHolder());
                            if ((int) playerVideo.getCurrentPosition() != 0) {
                                playerVideo.seekTo(syncMediaTime);
                            } else {
                                playerVideo.start();
                            }
                        }
                    }
                }
            }, 100);
        }*/
    }

    /**
     * 生命周期 onStop
     */
    public void onStop() {
        isOnPause = true;
        if (mPlayerMap != null) {
            if(isAudioPlay){
                IMediaPlayer player = mPlayerMap.get("audioMedia");
                if (player != null) {
                    player.pause();
                }
            }

         /*   if(isVideoPlay){
                IMediaPlayer player = mPlayerMap.get("videoMedia");
                if (player != null) {
                    player.pause();
                }
            }*/
        }
    }

    /**
     * 是否播放中
     * @return
     */
    public boolean isVideoPlay(){
        return isVideoPlay;
    }

    /**
     * 音频
     * @return
     */
    public boolean isAudioPlay(){
        return isAudioPlay;
    }


    /**
     * 事件监听
     */
    public interface VideoEventListener{

        int getVideoCurrentTime();

        int getAudioCurrentTime();

        void fullScreen();

        void exitFullScreen();

    }

}
