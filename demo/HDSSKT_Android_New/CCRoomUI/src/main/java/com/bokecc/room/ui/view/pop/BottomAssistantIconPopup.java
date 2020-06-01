package com.bokecc.room.ui.view.pop;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;


import com.bokecc.room.ui.R;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.listener.OnClickListener;
import com.bokecc.room.ui.model.IconEntity;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.room.ui.utils.PopupAnimUtil;
import com.bokecc.room.ui.view.adapter.IconAdapter;

import java.util.ArrayList;

/**
 * 作者 ${wdh}.<br/>
 */

public class BottomAssistantIconPopup extends BasePopupWindow {

    private final static String TAG = BottomAssistantIconPopup.class.getSimpleName();
    private IconAdapter mIconAdapter;
    private int mPosition = -1;
    private OnChooseClickListener mOnChooseClickListener;

    private VideoStreamView mVideoStreamView;
    private int mPopupStreamPosition;

    private final int resIds[] = new int[]{
            R.mipmap.close_mic_normal, R.mipmap.close_camera_normal
//            , R.drawable.kickout_normal
    };
    private static final String resDess[] = new String[]{
            "关麦", "关闭视频"
//            , "踢下麦"
    };

    private ArrayList<IconEntity> mIconEntities;
    private boolean haveLoop = false, haveVideo = true;

    public BottomAssistantIconPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {

        mIconEntities = new ArrayList<>();
        for (int i = 0; i < resIds.length; i++) {
            IconEntity iconEntity = new IconEntity(resIds[i], resDess[i]);
            mIconEntities.add(iconEntity);
        }

        findViewById(R.id.id_bottom_icon_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final RecyclerView icons = findViewById(R.id.id_bottom_icons);
        icons.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        icons.addOnItemTouchListener(new BaseOnItemTouch(icons, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                dismiss();
                mPosition = icons.getChildAdapterPosition(viewHolder.itemView);
            }
        }));
        mIconAdapter = new IconAdapter(mContext);
        mIconAdapter.bindDatas(mIconEntities);
        icons.setAdapter(mIconAdapter);

        setOnPopupDismissListener(new OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnChooseClickListener != null && mPosition != -1) {
                    mOnChooseClickListener.onClick(mPosition, mIconEntities.get(mPosition).getResId(),
                            mVideoStreamView);
                    mPosition = -1;
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.bottom_icon_layout_ui;
    }

    public int getPopupStreamPosition() {
        return mPopupStreamPosition;
    }

    public void show(View view, int position, VideoStreamView videoStreamView) {
        if (videoStreamView == null) {
            return;
        }
        mPopupStreamPosition = position;
        mVideoStreamView = videoStreamView;

        if (videoStreamView.getStream().isAllowAudio()) {
            update(0, new IconEntity(R.mipmap.close_mic_normal, "关麦"));
        } else {
            update(0, new IconEntity(R.mipmap.open_mic_normal, "开麦"));
        }

        if (videoStreamView.getStream().isAllowVideo()) {
            update(1, new IconEntity(R.mipmap.close_camera_normal, "关闭视频"));
        } else {
            update(1, new IconEntity(R.mipmap.open_camera_normal, "开放视频"));
        }

        super.show(view);
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefTranslateEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefTranslateExitAnim();
    }

    public void setOnChooseClickListener(OnChooseClickListener onChooseClickListener) {
        mOnChooseClickListener = onChooseClickListener;
    }

    public void update(int index, IconEntity iconEntity) {
        mIconAdapter.update(index, iconEntity);
    }

    public void remove(int index) {
        mIconAdapter.remove(index);
    }

    public void add(int index, IconEntity iconEntity) {
        mIconAdapter.add(index, iconEntity);
    }

    public interface OnChooseClickListener {
        void onClick(int index, int tag, VideoStreamView videoStreamView);
    }

}
