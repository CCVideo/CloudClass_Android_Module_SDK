package com.bokecc.room.ui.view.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.VideoStreamView;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;

import com.bokecc.common.utils.Tools;
import com.bokecc.common.utils.DensityUtil;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.common.util.LogUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作者 ${CC视频}.<br/>
 * 视频列表适配器
 * @modify wy
 */
public class VideoAdapter extends BaseRecycleAdapter<VideoAdapter.LittleVideoViewHolder, VideoStreamView> {

    /***/
    private ConcurrentHashMap<SurfaceView, RelativeLayout> views = new ConcurrentHashMap<>();
    /***/
    private ConcurrentHashMap<RelativeLayout, SurfaceView> mRootRenderers = new ConcurrentHashMap<>();
    /***/
    private ConcurrentHashMap<SurfaceView, RelativeLayout> SurfaceViews = new ConcurrentHashMap<>();
    /***/
    private ConcurrentHashMap<RelativeLayout, SurfaceView> mRootSurfaceRenderers = new ConcurrentHashMap<>();

    public int getmType() {
        return mType;
    }

    /**布局类型，1-文档视频，2-主视频，4-平铺*/
    private int mType;
    /***/
    private boolean isRefresh = false;

    /**横竖屏，0横屏，1竖屏*/
    private int sClassDirection = 0;

    private static final String TAG = "VideoAdapter";
    /**
     * init
     * @param context
     * @param sClassDirection
     */
    public VideoAdapter(Context context,int sClassDirection) {
        super(context);
        this.sClassDirection = sClassDirection;
    }

    /**设置显示类型，1-文档视频，2-主视频，4-平铺*/
    public void setType(int type) {
        mType = type;
    }

    /**设置刷新*/
    public void refresh() {
        isRefresh = true;
    }

    @Override
    public void onBindViewHolder(LittleVideoViewHolder holder, final int position) {
        try {
            LogUtil.e(TAG,"LittleVideoViewHolder holder, final int position开始跟新："+position);
            final VideoStreamView videoStreamView = mDatas.get(position);
            SurfaceView renderer1 = null;
            //renderer是1平台和2平台
            SurfaceView renderer = videoStreamView.getSurfaceViewList();
            if(renderer == null){
                renderer1 = videoStreamView.getRenderer();
            }

            RelativeLayout.LayoutParams params;
            if (mType == CCAtlasClient.TEMPLATE_TILE) {
                if (isRefresh) {
                    calItemHeight(holder.itemView);
                    isRefresh = false;
                }
                if (mDatas.size() == 1) {
                    holder.mLittleUsername.setVisibility(View.INVISIBLE);
                } else {
                    holder.mLittleUsername.setVisibility(View.VISIBLE);
                }
                if (sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
            } else {
                holder.mLittleUsername.setVisibility(View.VISIBLE);
                if (sClassDirection == 0) {
                    params = new RelativeLayout.LayoutParams(DensityUtil.dp2px(mContext, 80), ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(mContext, 80));
                }
            }
            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mLittleVideoOne.setVisibility(View.VISIBLE);
                holder.mLittleVideoOper.setVisibility(View.GONE);
            } else {
                holder.mLittleVideoOne.setVisibility(View.GONE);
                holder.mLittleVideoOper.setVisibility(View.VISIBLE);
            }
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            if(renderer != null){
                renderer.setLayoutParams(params);
                if (mType == CCAtlasClient.TEMPLATE_SINGLE ||
                        mType == CCAtlasClient.TEMPLATE_DOUBLE_TEACHER) {
                    renderer.setZOrderOnTop(true);
                    renderer.setZOrderMediaOverlay(true);
                    renderer.bringToFront();
                } else {
                    if(mType == CCAtlasClient.TEMPLATE_SPEAK && sClassDirection == 1){
                        renderer.setZOrderOnTop(true);
                        renderer.setZOrderMediaOverlay(true);
                    } else {
                        renderer.setZOrderOnTop(false);
                        renderer.setZOrderMediaOverlay(false);
                    }
                }
                if (views.get(renderer) != null) { // 判断当前需要被添加的子布局是否有父布局
                    views.get(renderer).removeView(renderer); // 找到该子布局的父布局，从父布局移除
                }
                if (mRootRenderers.get(holder.mLittleItemRoot) != null) { // 如果跟布局下面有渲染布局 移除该渲染布局
                    holder.mLittleItemRoot.removeView(mRootRenderers.get(holder.mLittleItemRoot));
                }
                holder.mLittleItemRoot.addView(renderer, -1);

                Tools.log(TAG,"[" + Tools.getCurrentTime()+ "]" + ": VideoAdapter-StreamID:  " + videoStreamView.getStream().getStreamId()+  ":VideoAdapter-renderer:  " + renderer);


                views.put(renderer, holder.mLittleItemRoot);
                mRootRenderers.put(holder.mLittleItemRoot, renderer); // 存放根布局和渲染布局
            } else {
                renderer1.setLayoutParams(params);
                if (mType == CCAtlasClient.TEMPLATE_SINGLE ||
                        mType == CCAtlasClient.TEMPLATE_DOUBLE_TEACHER) {
                    renderer1.setZOrderOnTop(true);
                    renderer1.setZOrderMediaOverlay(true);
                    renderer1.bringToFront();
                } else {
                    if(mType == CCAtlasClient.TEMPLATE_SPEAK && sClassDirection == 1){
                        renderer1.setZOrderOnTop(true);
                        renderer1.setZOrderMediaOverlay(true);
                    } else {
                        renderer1.setZOrderOnTop(false);
                        renderer1.setZOrderMediaOverlay(false);
                    }
                }
                if (SurfaceViews.get(renderer1) != null) { // 判断当前需要被添加的子布局是否有父布局
                    SurfaceViews.get(renderer1).removeView(renderer1); // 找到该子布局的父布局，从父布局移除
                }
                if (mRootSurfaceRenderers.get(holder.mLittleItemRoot) != null) { // 如果跟布局下面有渲染布局 移除该渲染布局
                    holder.mLittleItemRoot.removeView(mRootSurfaceRenderers.get(holder.mLittleItemRoot));
                }
                holder.mLittleItemRoot.addView(renderer1, -1);
                SurfaceViews.put(renderer1, holder.mLittleItemRoot);
                mRootSurfaceRenderers.put(holder.mLittleItemRoot, renderer1); // 存放根布局和渲染布局
            }
            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                holder.mLittleOneUsername.setText(videoStreamView.getStream().getUserName());
            } else {
                holder.mLittleUsername.setText(videoStreamView.getStream().getUserName());
            }
            holder.mMicIcon.setVisibility(View.VISIBLE);
            if (videoStreamView.getStream().isAllowAudio()) {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneMicIcon.setImageResource(R.mipmap.mic_open_icon);
                } else {
                    holder.mMicIcon.setImageResource(R.mipmap.mic_open_icon);
                }
            } else {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneMicIcon.setImageResource(R.mipmap.mic_close_icon);
                } else {
                    holder.mMicIcon.setImageResource(R.mipmap.mic_close_icon);
                }
            }
            if (videoStreamView.getStream().isAllowDraw()) {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneDrawIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.mDrawIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneDrawIcon.setVisibility(View.GONE);
                } else {
                    holder.mDrawIcon.setVisibility(View.GONE);
                }
            }
            if (videoStreamView.getStream().isSetupTeacher()) {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneSetupTheacher.setVisibility(View.VISIBLE);
                } else {
                    holder.mSetupTheacherIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mOneSetupTheacher.setVisibility(View.GONE);
                } else {
                    holder.mSetupTheacherIcon.setVisibility(View.GONE);
                }
            }
            if (CCAtlasClient.getInstance().getInteractBean().getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_AUTO) {
                if (videoStreamView.getStream().isLock()) {
                    if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                        holder.mOneLockIcon.setVisibility(View.VISIBLE);
                    } else {
                        holder.mLockIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                        holder.mOneLockIcon.setVisibility(View.GONE);
                    } else {
                        holder.mLockIcon.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.mLockIcon.setVisibility(View.GONE);
                holder.mOneLockIcon.setVisibility(View.GONE);
            }
            // 学生仅音频模式
            if (videoStreamView.getStream().getUserRole() != CCAtlasClient.PRESENTER &&
                    videoStreamView.getStream().getUserRole() != CCAtlasClient.ASSISTANT &&
                    CCAtlasClient.getInstance().getMediaMode() == CCAtlasClient.MEDIA_MODE_AUDIO) {
                if (videoStreamView.getStream().getUserId().equals(CCAtlasClient.SHARE_SCREEN_STREAM_ID)) {
                    holder.mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
                } else {
                    holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                    if (sClassDirection == 0) {
                        holder.mOtherIcon.setImageResource(R.mipmap.only_mic_bg);
                    } else {
                        holder.mOtherIcon.setImageResource(R.mipmap.only_mic_bg_land);
                    }
                }
            } else {
                if (!videoStreamView.getStream().isAllowVideo()) { // 关闭视频
                    holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                    if (sClassDirection == 0) {
                        holder.mOtherIcon.setImageResource(R.mipmap.camera_close_bg);
                    } else {
                        holder.mOtherIcon.setImageResource(R.mipmap.camera_close_bg_land);
                    }
                } else {
                    holder.mOtherLayout.setVisibility(View.GONE);
                    if (videoStreamView.getStream().getRemoteStream() != null) {
                        if (!videoStreamView.getStream().getRemoteStream().hasAudio()) {
                            holder.mMicIcon.setVisibility(View.VISIBLE);
                            holder.mMicIcon.setImageResource(R.mipmap.no_mic_icon);
                        }
                        if (!videoStreamView.getStream().getRemoteStream().hasVideo()) {
                            holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                            if (sClassDirection == 0) {
                                holder.mOtherIcon.setImageResource(R.mipmap.no_camera_icon);
                            } else {
                                holder.mOtherIcon.setImageResource(R.mipmap.no_camera_icon_land);
                            }
                        }
                    }
                }
            }
            holder.mVideoRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickVideoListening != null) {
                        mOnClickVideoListening.onClick(position);
                    }
                }
            });

            holder.mAudioCut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSwitchAVListening != null) {
                        mOnSwitchAVListening.onClick(videoStreamView, position);
                    }
                }
            });

            holder.mProgressButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongClickProListening != null) {
                        mOnLongClickProListening.onClick(position);
                    }
                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(LittleVideoViewHolder holder, int position, List<Object> payloads) {
        try{
            LogUtil.e(TAG,"onBindViewHolder开始跟新："+position);
            if (payloads == null || payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                    holder.mLittleVideoOne.setVisibility(View.VISIBLE);
                    holder.mLittleVideoOper.setVisibility(View.GONE);
                } else {
                    holder.mLittleVideoOne.setVisibility(View.GONE);
                    holder.mLittleVideoOper.setVisibility(View.VISIBLE);
                }
                VideoStreamView videoStreamView = mDatas.get(position);
                for(int i = 0; i < payloads.size(); i++){
                    if ((int) payloads.get(i) == 0) {
                        if (videoStreamView.getStream().isAllowAudio()) {
                            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                holder.mOneMicIcon.setImageResource(R.mipmap.mic_open_icon);
                            } else {
                                holder.mMicIcon.setImageResource(R.mipmap.mic_open_icon);
                            }
                        } else {
                            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                holder.mOneMicIcon.setImageResource(R.mipmap.mic_close_icon);
                            } else {
                                holder.mMicIcon.setImageResource(R.mipmap.mic_close_icon);
                            }
                        }
                    } else if ((int) payloads.get(i) == 1) {
                        // 学生仅音频模式
                        if (videoStreamView.getStream().getUserRole() != CCAtlasClient.PRESENTER &&
                                videoStreamView.getStream().getUserRole() != CCAtlasClient.ASSISTANT &&
                                CCAtlasClient.getInstance().getMediaMode() == CCAtlasClient.MEDIA_MODE_AUDIO) {
                            if (videoStreamView.getStream().getUserId().equals(CCAtlasClient.SHARE_SCREEN_STREAM_ID)) {
                                holder.mOtherLayout.setVisibility(View.GONE); // 显示音频贴图
                            } else {
                                holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示音频贴图
                                if (sClassDirection == 0) {
                                    holder.mOtherIcon.setImageResource(R.mipmap.only_mic_bg);
                                } else {
                                    holder.mOtherIcon.setImageResource(R.mipmap.only_mic_bg_land);
                                }
                            }
                        } else {
                            if (!videoStreamView.getStream().isAllowVideo()) { // 关闭视频
                                holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                                if (sClassDirection == 0) {
                                    holder.mOtherIcon.setImageResource(R.mipmap.camera_close_bg);
                                } else {
                                    holder.mOtherIcon.setImageResource(R.mipmap.camera_close_bg_land);
                                }
                            } else {
                                holder.mOtherLayout.setVisibility(View.GONE);
                                if (videoStreamView.getStream().getRemoteStream() != null) {
                                    if (!videoStreamView.getStream().getRemoteStream().hasAudio()) {
                                        holder.mMicIcon.setVisibility(View.VISIBLE);
                                        holder.mMicIcon.setImageResource(R.mipmap.no_mic_icon);
                                    }
                                    if (!videoStreamView.getStream().getRemoteStream().hasVideo()) {
                                        holder.mOtherLayout.setVisibility(View.VISIBLE); // 显示摄像头被关闭贴图
                                        if (sClassDirection == 0) {
                                            holder.mOtherIcon.setImageResource(R.mipmap.no_camera_icon);
                                        } else {
                                            holder.mOtherIcon.setImageResource(R.mipmap.no_camera_icon_land);
                                        }
                                    }
                                }
                            }
                        }
                        // 共享桌面流不显示麦克风图标
                        if (videoStreamView.getStream().getUserName().equals(CCAtlasClient.SHARE_SCREEN_STREAM_NAME)) {
                            holder.mMicIcon.setVisibility(View.GONE);
                        }
                    } else if ((int) payloads.get(i) == 2) {
                        if (videoStreamView.getStream().isAllowDraw()) {
                            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                holder.mOneDrawIcon.setVisibility(View.VISIBLE);
                            } else {
                                holder.mDrawIcon.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                holder.mOneDrawIcon.setVisibility(View.GONE);
                            } else {
                                holder.mDrawIcon.setVisibility(View.GONE);
                            }
                        }
                    } else if ((int) payloads.get(i) == 3) {
                        if (CCAtlasClient.getInstance().getInteractBean().getLianmaiMode() == CCAtlasClient.LIANMAI_MODE_AUTO) {
                            if (videoStreamView.getStream().isLock()) {
                                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                    holder.mOneLockIcon.setVisibility(View.VISIBLE);
                                } else {
                                    holder.mLockIcon.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                    holder.mOneLockIcon.setVisibility(View.GONE);
                                } else {
                                    holder.mLockIcon.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            holder.mLockIcon.setVisibility(View.GONE);
                            holder.mOneLockIcon.setVisibility(View.GONE);
                        }
                    } else if ((int) payloads.get(i) == 4) {
                        if (videoStreamView.getStream().isSetupTeacher()) {
                            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                holder.mOneSetupTheacher.setVisibility(View.VISIBLE);
                            } else {
                                holder.mSetupTheacherIcon.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (mType == CCAtlasClient.TEMPLATE_TILE && mDatas.size() == 1) {
                                holder.mOneSetupTheacher.setVisibility(View.GONE);
                            } else {
                                holder.mSetupTheacherIcon.setVisibility(View.GONE);
                            }
                        }
                    } else if ((int) payloads.get(i) == 5) {//出现加载的样式
                        if (videoStreamView.getBlackStream()) {
                            holder.mProgressOtherLayout.setVisibility(View.VISIBLE);
                        } else {
                            holder.mProgressOtherLayout.setVisibility(View.GONE);
                        }
                        if (videoStreamView.getType() == 0) {
                            holder.mBandwidth.setText("↑" + videoStreamView.getBandwidth() / 1000 + "kb/s");
                        } else {
                            holder.mBandwidth.setText("↓" + videoStreamView.getBandwidth() / 1000 + "kb/s");
                        }
                    } else if ((int) payloads.get(i) == 6) {//显示上行下行数据
                        if (videoStreamView.getAudio()) {//如果切换音频只听显示
                            holder.mAudioOtherLayout.setVisibility(View.VISIBLE);
                            holder.mProgressOtherLayout.setVisibility(View.GONE);
                        } else {//如果切换视频后隐藏
                            holder.mAudioOtherLayout.setVisibility(View.GONE);
                            holder.mProgressOtherLayout.setVisibility(View.GONE);
                        }

                    } else {
                        onBindViewHolder(holder, position);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.little_item_video_layout;
    }

    @Override
    public VideoAdapter.LittleVideoViewHolder getViewHolder(View itemView, int viewType) {
        if (mType == CCAtlasClient.TEMPLATE_TILE) {
            calItemHeight(itemView);
        } else {
            if (sClassDirection == 1) { // 横屏
                itemView.getLayoutParams().width = DensityUtil.dp2px(mContext, 143.3f);
                itemView.getLayoutParams().height = DensityUtil.dp2px(mContext, 81);
            }
        }
        return new LittleVideoViewHolder(itemView);
    }

    /**
     * holder
     */
    protected class LittleVideoViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        RelativeLayout mVideoRelativeLayout;
        RelativeLayout mLittleItemRoot;
        TextView mLittleUsername;
        ImageView mMicIcon;
        ImageView mDrawIcon;
        ImageView mSetupTheacherIcon;
        ImageView mLockIcon;
        RelativeLayout mOtherLayout;
        ImageView mOtherIcon;
        RelativeLayout mLittleVideoOper;
        LinearLayout mLittleVideoOne;
        TextView mLittleOneUsername;
        TextView mBandwidth;
        ImageView mOneMicIcon;
        ImageView mOneDrawIcon;
        ImageView mOneSetupTheacher;
        ImageView mOneLockIcon;

        RelativeLayout mProgressOtherLayout;
        ProgressBar mIdProgressBar;
        TextView mProgressButton;

        RelativeLayout mAudioOtherLayout;
        ImageView mAudioOtherIcon;
        TextView mAudioCut;


        LittleVideoViewHolder(View itemView) {
            super(itemView);

            mVideoRelativeLayout = itemView.findViewById(R.id.id_video_relative_layout);
            mLittleItemRoot = itemView.findViewById(R.id.id_little_video_item_root);
            mLittleUsername = itemView.findViewById(R.id.id_little_video_item_username);
            mMicIcon = itemView.findViewById(R.id.id_little_video_item_mic);
            mDrawIcon = itemView.findViewById(R.id.id_little_video_item_draw);
            mSetupTheacherIcon = itemView.findViewById(R.id.id_little_video_item_setup_theacher);
            mLockIcon = itemView.findViewById(R.id.id_little_video_item_lock);
            mOtherLayout = itemView.findViewById(R.id.id_little_video_item_other_layout);
            mOtherIcon = itemView.findViewById(R.id.id_little_video_item_other_icon);
            mLittleVideoOper = itemView.findViewById(R.id.id_little_video_oper);
            mLittleVideoOne = itemView.findViewById(R.id.id_little_video_one_item);
            mLittleOneUsername = itemView.findViewById(R.id.id_little_video_one_item_name);
            mBandwidth = itemView.findViewById(R.id.id_bandwidth);
            mOneMicIcon = itemView.findViewById(R.id.id_little_video_one_item_mic_close);
            mOneDrawIcon = itemView.findViewById(R.id.id_little_video_one_item_video_draw);
            mOneSetupTheacher = itemView.findViewById(R.id.id_little_video_one_item_video_setup_theacher);
            mOneLockIcon = itemView.findViewById(R.id.id_little_video_one_item_video_lock);

            mProgressOtherLayout = itemView.findViewById(R.id.id_little_progressnbar_item_other_layout);
            mIdProgressBar = itemView.findViewById(R.id.id_progressbar);
            mProgressButton = itemView.findViewById(R.id.id_little_progress_item_button);

            mAudioOtherLayout = itemView.findViewById(R.id.id_little_audio_item_other_layout);
            mAudioOtherIcon = itemView.findViewById(R.id.id_little_audio_item_other_icon);
            mAudioCut = itemView.findViewById(R.id.id_little_audio_item_cut_button);
        }
    }


    /**
     *
     * @param itemView
     */
    private void calItemHeight(View itemView) {
        Rect outRect = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int height = outRect.bottom - outRect.top;
        int width = outRect.right-outRect.left;
        int size = mDatas.size();

        int spanCount = (int) Math.ceil(Math.sqrt(size));
        itemView.getLayoutParams().height = height / spanCount;
        itemView.getLayoutParams().width = width / spanCount;
    }



    //adapter item 点击事件
    private OnClickVideoListening mOnClickVideoListening;
    public void setOnClickVideoListening(OnClickVideoListening mOnClickVideoListening) {
        this.mOnClickVideoListening = mOnClickVideoListening;
    }
    public interface OnClickVideoListening {
        void onClick(int position);
    }

    //切换音视频
    private OnSwitchAVListening mOnSwitchAVListening;
    public void setOnSwitchAVListening(OnSwitchAVListening mOnSwitchAVListening) {
        this.mOnSwitchAVListening = mOnSwitchAVListening;
    }
    public interface OnSwitchAVListening {
        void onClick(VideoStreamView videoStreamView, int position);
    }

    //加载按钮 item 点击事件
    private OnLongClickProListening mOnLongClickProListening;
    public void setOnLongClickProListening(OnLongClickProListening mOnLongClickProListening) {
        this.mOnLongClickProListening = mOnLongClickProListening;
    }
    public interface OnLongClickProListening {
        void onClick(int position);
    }

}
