package com.bokecc.ccsskt.example.recycle;

import android.support.v7.widget.RecyclerView;

/**
 * 作者 ${CC视频}.<br/>
 */

public interface ITouchListener {
    void onClick(RecyclerView.ViewHolder viewHolder);
    void onLongPress(RecyclerView.ViewHolder viewHolder);
    void onTouchDown(RecyclerView.ViewHolder viewHolder);
    void onTouchUp(RecyclerView.ViewHolder viewHolder);
}
