package com.bokecc.room.ui.model;

import android.view.SurfaceView;

import com.bokecc.sskt.base.bean.SubscribeRemoteStream;

/**
 * 作者 ${CC视频}.<br/>
 */
public class VideoStreamView {

    private SubscribeRemoteStream mStream;
    private SurfaceView mRenderer;
    private boolean isBlackStream = false;
    private int bandwidth = 0;
    private int type = 1;//0是推流1是拉流
    private boolean isAudio =false;//false不是音频
    private SurfaceView mUidsList;//视频视图
    private int isBig  = 0;//1是big操作 0是点击放大操作

    public boolean getAudio() {
        return isAudio;
    }

    public void setAudio(boolean audio) {
        isAudio = audio;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public boolean getBlackStream() {
        return isBlackStream;
    }

    public void setBlackStream(boolean blackStream) {
        isBlackStream = blackStream;
    }

    public SubscribeRemoteStream getStream() {
        return mStream;
    }

    public void setStream(SubscribeRemoteStream stream) {
        mStream = stream;
    }

    @Deprecated
    public SurfaceView getRenderer() {
        return mRenderer;
    }

    @Deprecated
    public void setRenderer(SurfaceView renderer) {
        mRenderer = renderer;
    }

    public SurfaceView getSurfaceViewList(){
        return mUidsList;
    }

    public void setSurfaceViewList(SurfaceView uid){
        mUidsList = uid;
    }

    public int getIsBig() {
        return isBig;
    }

    public void setIsBig(int isBig) {
        this.isBig = isBig;
    }
}