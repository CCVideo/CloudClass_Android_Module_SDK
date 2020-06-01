package com.bokecc.room.ui.listener;

/**
 * @author swh
 * @Description java类作用
 */
public interface ImageUpdateListener {
    void updateSuccess(String docId);
    void updateFailure(String msg);

    void updateLoading(long progress);
}
