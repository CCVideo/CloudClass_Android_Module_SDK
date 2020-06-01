package com.bokecc.room.ui.view.chat;

import java.io.File;

/**
 * 聊天视图监听类
 * @author wy
 */
public interface ChatViewListener {

    /**发送信息*/
    void sendMsg(String msg);
    /**发送图片*/
    void sendPic(File file);

}
