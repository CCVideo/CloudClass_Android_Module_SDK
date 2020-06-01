package com.bokecc.room.ui.view.video.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 暖场视频控件
 * @author wy
 */
public class WarmUpVideoView extends RelativeLayout {

    private final String TAG = "WarmUpVideoView";

    private IMediaPlayer mWarmVideoPlayer = null;
    private SurfaceView mWarmUpVideo;
    private ProgressBar bufferProgressBar;
    private RelativeLayout mWarmUpVideoLayout;

    private long currentPosition = 0;//播放记录

    /**当前播放路径*/
    private String mediaPath = null;


    public WarmUpVideoView(Context context) {
        super(context);
        initView(context);
    }

    public WarmUpVideoView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView(context);
    }

    public WarmUpVideoView(Context context, AttributeSet attrs, int deStyleAttr){
        super(context,attrs,deStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_video_warm_up_layout,this,true);

        mWarmUpVideo = findViewById(R.id.id_student_tv_warm_up_video);
        bufferProgressBar = findViewById(R.id.bufferProgressBar);
        mWarmUpVideoLayout = findViewById(R.id.id_student_warm_up_video_layout);
    }

    /**
     * 暖场视频播放器
     * @param mediaPath
     */
    public void initMediaPlayer(String mediaPath) {
        try {
            this.mediaPath = mediaPath;

            //暖场视频播放器
            if (mWarmVideoPlayer != null) {
                mWarmVideoPlayer.release();
                mWarmVideoPlayer = null;
            }
            mWarmVideoPlayer = new IjkMediaPlayer();
            mWarmVideoPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer mp) {
                    changeVideo(mp, mWarmUpVideoLayout, mWarmUpVideo);
                    if (mp != null) {
                        mp.setDisplay(mWarmUpVideo.getHolder());
                        mp.start();
                    }
                    bufferProgressBar.setVisibility(View.GONE);
                }

            });
            mWarmVideoPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer mp, int what, int extra) {
                    mp.reset();
                    mp.release(); // 释放播放器资源
                    return false;
                }
            });
            mWarmVideoPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(final IMediaPlayer mp) {
                    if (mp != null) {
                        mp.setDisplay(mWarmUpVideo.getHolder());
                        mp.start();
                    }
                }
            });
            boolean isPlaying = false;
            isPlaying = mWarmVideoPlayer.isPlaying();
            mWarmVideoPlayer.setLooping(true); // 设置循环播放
            mWarmVideoPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mWarmVideoPlayer.setDataSource(mediaPath);//播放地址
            mWarmVideoPlayer.prepareAsync();

            bufferProgressBar.setVisibility(View.VISIBLE);
            setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Tools.showToast("初始化播放器失败 [ " + e.toString() + " ]");
            if (mWarmVideoPlayer != null) {
                mWarmVideoPlayer.stop();
                mWarmVideoPlayer.setDisplay(null);
                mWarmVideoPlayer.release();
                return;
            }
        }
    }

    public void hideView(){
        bufferProgressBar.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }

    /**
     *
     * @param mp
     * @param relativeLayout
     * @param surfaceView
     */
    private void changeVideo(IMediaPlayer mp, RelativeLayout relativeLayout, SurfaceView surfaceView) {
        // 首先取得video的宽和高
        if (mp == null) {
            return;
        }
        int vWidth = mp.getVideoWidth();
        int vHeight = mp.getVideoHeight();

        // 该LinearLayout的父容器 android:orientation="vertical" 必须
        RelativeLayout linearLayout = (RelativeLayout) relativeLayout;
        int lw = linearLayout.getWidth();
        int lh = linearLayout.getHeight();
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
        LayoutParams lp = new LayoutParams(vWidth, vHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(lp);
    }

    /**
     * 生命周期 onResume
     */
    public void onResume() {
        if (mWarmVideoPlayer != null && !TextUtils.isEmpty(mediaPath) && currentPosition != 0) {
            mWarmVideoPlayer.seekTo(currentPosition);
        }
    }

    /**
     * 生命周期 onStop
     */
    public void onStop() {
        try {
            if (mWarmVideoPlayer != null && mWarmVideoPlayer.isPlaying()) {
                mWarmVideoPlayer.pause();
                currentPosition = mWarmVideoPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭视频
     */
    public void closeVideo(){
        if (mWarmVideoPlayer != null && mWarmVideoPlayer.isPlaying()) {
            mWarmVideoPlayer.pause();
            mWarmVideoPlayer.stop();
            mWarmVideoPlayer.release();
            mWarmVideoPlayer = null;
            mWarmUpVideo.setVisibility(View.GONE);
            mWarmUpVideoLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 是否播放中
     * @return
     */
    public boolean isVideoPlay(){
        if(mWarmVideoPlayer != null) {
            return mWarmVideoPlayer.isPlaying();
        }
        return false;
    }


    public void setPortrait(boolean portrait) {
        if(!portrait){
            RelativeLayout.LayoutParams warmLayoutParamslayout = (RelativeLayout.LayoutParams) mWarmUpVideoLayout.getLayoutParams();
            warmLayoutParamslayout.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            warmLayoutParamslayout.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mWarmUpVideoLayout.setLayoutParams(warmLayoutParamslayout);
        }
    }


}
