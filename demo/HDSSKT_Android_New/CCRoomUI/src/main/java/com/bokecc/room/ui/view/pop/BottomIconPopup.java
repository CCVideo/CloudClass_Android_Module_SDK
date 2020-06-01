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
import com.bokecc.sskt.base.CCAtlasClient;

import java.util.ArrayList;

/**
 * 作者 ${CC视频}.<br/>
 */

public class BottomIconPopup extends BasePopupWindow {

    private final static String TAG = BottomIconPopup.class.getSimpleName();
    private IconAdapter mIconAdapter;
    private int mPosition = -1;
    private OnChooseClickListener mOnChooseClickListener;

    private VideoStreamView mVideoStreamView;
    private int mPopupStreamPosition;

    private static final int resIds[] = new int[]{
            R.mipmap.auth_draw_normal,  R.mipmap.close_camera_normal, R.mipmap.close_mic_normal,
            R.mipmap.kickout_normal, R.mipmap.video_fullscreen_normal,R.mipmap.setup_teacher,
            R.mipmap.send_cup_normal
    };
    private static final String resDess[] = new String[]{
            "授权标注", /*"不轮播",*/ "关闭视频", "关麦", "踢下麦", "全屏视频","设为讲师","奖励奖杯"
    };

    private ArrayList<IconEntity> mIconEntities;
    private boolean haveLoop = false, haveVideo = true;

    public BottomIconPopup(Context context) {
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
        if (videoStreamView.getStream().isAllowDraw()) {
            update(0, new IconEntity(R.mipmap.auth_draw_cancel_normal, "取消授权"));
        } else {
            update(0, new IconEntity(R.mipmap.auth_draw_normal, "授权标注"));
        }
        int index;
        if (haveLoop) {
            index = 2;
        } else {
            index = 1;
        }
        if (videoStreamView.getStream().isAllowVideo()) {
            update(index, new IconEntity(R.mipmap.close_camera_normal, "关闭视频"));
        } else {
            update(index, new IconEntity(R.mipmap.open_camera_normal, "开放视频"));
        }
        if (haveLoop) {
            index = 3;
        } else {
            index = 2;
        }
        if (videoStreamView.getStream().isAllowAudio()) {
            update(index, new IconEntity(R.mipmap.close_mic_normal, "关麦"));
        } else {
            update(index, new IconEntity(R.mipmap.open_mic_normal, "开麦"));
        }
        if (CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_TILE || position == -1) {
            if (haveVideo) {
                remove(mIconEntities.size()-3);
                haveVideo = false;
            }
            if(mIconEntities.size() == 7) {
                remove(mIconEntities.size()-3);
            }
        } else {
            if (!haveVideo) {
                if (CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SPEAK) {
                
                    add(mIconEntities.size()-2, new IconEntity(R.mipmap
                            .video_fullscreen_normal, "全屏视频"));
                } else if(CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SINGLE){
                    add(mIconEntities.size()-2, new IconEntity(R.mipmap
                            .video_fullscreen_normal, "主视频"));
                } else {

                }
            } else {
                if (CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SPEAK) {
                    update(mIconEntities.size()-3, new IconEntity(R.mipmap
                            .video_fullscreen_normal, "全屏视频"));
                } else if (CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SINGLE){
                    update(mIconEntities.size()-3, new IconEntity(R.mipmap
                            .video_fullscreen_normal, "主视频"));
                } else {
//                    remove(mIconEntities.size()-3);
                    for (int i = 0; i < mIconEntities.size(); i++) {
                        if("全屏视频".equals(mIconEntities.get(i).getResDes())){
                            remove(i);
                            break;
                        }
                    }
                }
            }
        }
        if(mIconEntities.size() > 7) {
            if (CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SPEAK) {
                remove(mIconEntities.size()-3);
            } else if(CCAtlasClient.getInstance().getInteractBean().getTemplate() == CCAtlasClient.TEMPLATE_SINGLE){
                remove(mIconEntities.size()-3);
                update(mIconEntities.size()-3, new IconEntity(R.mipmap
                        .video_fullscreen_normal, "主视频"));
            } else {
                remove(mIconEntities.size()-3);
            }
        }
        if (videoStreamView.getStream().isSetupTeacher()) {
            update(mIconEntities.size()-2, new IconEntity(R.mipmap.setup_teacher_canclenormal,
                    "撤销讲师"));
        } else {
            update(mIconEntities.size()-2, new IconEntity(R.mipmap.setup_teacher, "设为讲师"));
        }

        update(mIconEntities.size()-1, new IconEntity(R.mipmap.send_cup_normal,
                "奖励奖杯"));

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
