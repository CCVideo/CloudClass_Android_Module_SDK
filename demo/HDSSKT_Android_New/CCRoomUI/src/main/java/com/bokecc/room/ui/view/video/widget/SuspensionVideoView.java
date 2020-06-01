package com.bokecc.room.ui.view.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bokecc.sskt.base.bean.SubscribeRemoteStream;
import com.bokecc.sskt.base.common.exception.StreamException;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.stream.bean.CCStream;

/**
 * 悬浮视频控件
 * （1）悬浮于整个窗口最上方；
 * （2）支持手势拖动；
 * （3）双击放大，全屏显示；
 * @author wy
 */
public class SuspensionVideoView extends FrameLayout {

    private final String TAG = "SuspensionVideoView";

    /**跟视图*/
    private FrameLayout mShareScreenContainer;
    /**视频视图*/
    private SurfaceView mShareScreen;
    /**退出全屏按钮*/
    private ImageView mShareScreenExit;

    /**是否展示视频中*/
    private boolean isShowVideo = false;

    public boolean isFullScreen() {
        return isFullScreen;
    }

    /**是否全屏*/
    private boolean isFullScreen = false;

    /**当前的SurfaceView*/
    private volatile SurfaceView mSurfaceView;

    /**事件回调*/
    private SuspensionVideoViewListener listener = null;


    public SuspensionVideoView(Context context) {
        super(context);
        initView(context);
    }

    public SuspensionVideoView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView(context);
    }

    public SuspensionVideoView(Context context, AttributeSet attrs, int deStyleAttr){
        super(context,attrs,deStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_video_suspension_layout,this,true);

        mShareScreenContainer = findViewById(R.id.id_student_share_screen_container);
        mShareScreen = findViewById(R.id.id_student_share_screen);
        mShareScreenExit = findViewById(R.id.id_student_share_screen_exit);
        mShareScreenExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFullScreen();
            }
        });

        //注册手势事件
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mShareScreenContainer.getLayoutParams();
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                fullScreen(params);
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

                if (params.topMargin > (Tools.getScreenHeight() - mShareScreenContainer.getHeight())) {
                    params.topMargin = Tools.getScreenHeight() - mShareScreenContainer.getHeight();
                }
                if (params.leftMargin > (Tools.getScreenWidth() - mShareScreenContainer.getWidth())) {
                    params.leftMargin = Tools.getScreenWidth() - mShareScreenContainer.getWidth();
                }

                mShareScreenContainer.setLayoutParams(params);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        mShareScreenContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isFullScreen && gestureDetector.onTouchEvent(event);
            }
        });
    }

    public void setListener(SuspensionVideoViewListener listener){
        this.listener = listener;
    }

    /**
     * 展示屏幕共享流
     * @param stream
     * @param ccstream
     */
    public synchronized void showScreen(final SubscribeRemoteStream stream, final CCStream ccstream) {
        isShowVideo = true;
        isFullScreen = false;
        if (ccstream != null && ccstream.getSurfaceView() != null) {
            mSurfaceView = ccstream.getSurfaceView();
            ViewGroup parent = (ViewGroup) mSurfaceView.getParent();
            if (parent != null) {
                parent.removeView(mSurfaceView);
            }
            mSurfaceView.setVisibility(View.VISIBLE);
            mSurfaceView.setZOrderOnTop(true);
            mSurfaceView.setZOrderMediaOverlay(true);
            FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            videoParams.gravity = Gravity.CENTER;
            if (mSurfaceView != null) {
                mSurfaceView.setLayoutParams(videoParams);
            }
            mShareScreenContainer.addView(mSurfaceView);
            mShareScreenContainer.setVisibility(View.VISIBLE);

            setVisibility(View.VISIBLE);
        } else {
//            try {
//                stream.getRemoteStream().attach(mShareScreen);
//            } catch (StreamException e) {
//                Tools.showToast(e.getMessage());
//            }

            mShareScreen.setVisibility(View.VISIBLE);
            setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏视频
     *
     * @param stream
     */
    public void dismissScreen(SubscribeRemoteStream stream) {
        try {
            stream.detach();
        } catch (StreamException ignored) {
            ignored.printStackTrace();
        } finally {
            isShowVideo = false;

            FrameLayout.LayoutParams temp = new FrameLayout.LayoutParams(Tools.dipToPixel(160),Tools.dipToPixel(90));
            temp.leftMargin = 0;
            temp.topMargin = 0;
            mShareScreenContainer.setLayoutParams(temp);

            FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            videoParams.gravity = Gravity.CENTER;
            if (mSurfaceView != null) {
                mSurfaceView.setLayoutParams(videoParams);
                mSurfaceView.setVisibility(View.GONE);
                mShareScreenContainer.setVisibility(View.GONE);
            } else {
//                mShareScreen.cleanFrame();
                mShareScreen.setLayoutParams(videoParams);
                mShareScreen.setVisibility(View.GONE);
                mShareScreenContainer.setVisibility(View.GONE);
            }

            if(listener != null){
                listener.exitFullScreen();
            }
        }
    }

    /**
     * 设置当前视频是否显示
     * 只有在视频播放中才会起作用
     * @param visibility
     */
    public void setSVVVisibility(int visibility){
        if(isShowVideo){
            mShareScreenContainer.setVisibility(visibility);
            mShareScreen.setVisibility(visibility);
            if (mSurfaceView != null) {
                mSurfaceView.setVisibility(visibility);
            }
        }
    }

    /**放大前的起始坐标*/
    private int mScreenLeft = 0, mScreenTop = 0;

    /**
     * 全屏显示
     * @param params
     */
    private void fullScreen(FrameLayout.LayoutParams params){
        if (mSurfaceView != null) {
            mShareScreenContainer.removeView(mSurfaceView);
        }

        mScreenLeft = params.leftMargin;
        mScreenTop = params.topMargin;

        FrameLayout.LayoutParams temp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        temp.leftMargin = 0;
        temp.topMargin = 0;
        mShareScreenContainer.setLayoutParams(temp);

        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        videoParams.gravity = Gravity.CENTER;
        mShareScreen.setLayoutParams(videoParams);

        if (mSurfaceView != null) {
            mSurfaceView.setLayoutParams(videoParams);
            mSurfaceView.setZOrderOnTop(true);
            mSurfaceView.setZOrderMediaOverlay(true);
            mShareScreenContainer.addView(mSurfaceView, 1);
        }

        mShareScreenExit.setVisibility(View.VISIBLE);
        isFullScreen = true;

        if(listener != null){
            listener.fullScreen();
        }
    }

    /**
     * 关闭全屏
     */
    public void exitFullScreen() {
        isFullScreen = false;

        //设置外层布局
        FrameLayout.LayoutParams temp = new FrameLayout.LayoutParams(Tools.dipToPixel(160), Tools.dipToPixel(90));
        temp.leftMargin = mScreenLeft;
        temp.topMargin = mScreenTop;
        mShareScreenContainer.setLayoutParams(temp);

        //设置FV布局
//        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(Tools.dipToPixel(160), Tools.dipToPixel(90));
//        videoParams.gravity = Gravity.CENTER;
//        mShareScreen.setLayoutParams(videoParams);
        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        videoParams.gravity = Gravity.CENTER;
        mShareScreen.setLayoutParams(videoParams);
        if (mSurfaceView != null) {
            mSurfaceView.setLayoutParams(videoParams);
        }

        mShareScreenExit.setVisibility(View.GONE);

        if(listener != null){
            listener.exitFullScreen();
        }
    }


    public interface SuspensionVideoViewListener{

        void fullScreen();

        void exitFullScreen();

    }
}
