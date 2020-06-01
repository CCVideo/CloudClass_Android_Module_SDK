package com.bokecc.room.ui.view.video.listener;

import com.bokecc.room.ui.model.VideoStreamView;

/**
 * 视频监听
 */
public interface VideoViewListener {

    void fullScreen();

    void exitFullScreen();

    VideoStreamView getMySelfVideoStreamView();

    void onClickDocVideo();

}
