package com.bokecc.room.ui.view.doc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.ccdocview.DocView;
import com.bokecc.ccdocview.DocWebView;
import com.bokecc.ccdocview.gestureview.GestureViewBinder;
import com.bokecc.common.utils.NetUtils;
import com.bokecc.room.ui.listener.DragTouchListener;
import com.bokecc.room.ui.listener.ImageUpdateListener;
import com.bokecc.room.ui.utils.ImageUtil;
import com.bokecc.room.ui.view.activity.StudentRoomActivity;
import com.bokecc.room.ui.view.activity.TeacherRoomActivity;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.widget.CCDocRelativeLayout;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.common.util.LogUtil;
import com.bokecc.ccdocview.model.DocInfo;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.model.ColorStatus;
import com.bokecc.common.utils.DensityUtil;
import com.bokecc.room.ui.utils.SPUtil;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleRelativeLayout;
import com.cpiz.android.bubbleview.BubbleStyle;
import java.util.ArrayList;

import static com.bokecc.room.ui.config.Config.MENU_SHOW_TIME;
import static com.bokecc.room.ui.view.base.CCRoomActivity.ORIENTATION_LANDSCAPE;


/**
 * author cc
 * 文档组合视图（包含文档画笔显示、翻页按钮，文档工具条，全屏、退出全屏按钮、跳页按钮等）, 可以将文档整体
 * 当作一个控件使用，参考{@link TeacherRoomActivity} 和 {@link StudentRoomActivity}中 主要方法调用
 *
 */
public class CCDocView extends RelativeLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    /**
     * 整个文档区域
     */
    private RelativeLayout mDocArea;
    /**
     * 文档底部菜单区，包含全屏按钮、跳页按钮、退出文档全屏按钮
     */
    private RelativeLayout mDocBottomLayout;
    /**
     * 显示画笔的文档控件
     */
    private DocView mDocView;
    /**
     * 显示 ppt 、图片等的文档控件
     */
    private DocWebView mDocWebView;
    /**
     * 后退按钮
     */
    private ImageButton mDocBack;
    /**
     *  前进按钮
     */
    private ImageButton mDocForward;
    /**
     * 跳页选择按钮
     */
    private ImageButton mImgGrid;
    /**
     * 文档全屏按钮
     */
    private ImageButton mFullScreen;
    /**
     * 文档加载中视图
     */
    private RelativeLayout mPrepareLayout;
    /**
     * 文档加载中视图 文本控件
     */
    private TextView mUpdateTip;
    /**
     * 文档退出全屏按钮
     */
    private ImageButton mExitFullScreen;
    /**
     * 文档工具条
     */
    private LinearLayout mDrawLayout;
    /**
     * 清除画笔按钮
     */
    private ImageButton mClear;
    /**
     * 画笔工具条中的前进、后退、页码等父布局
     */
    private LinearLayout mPageChangeLayout;
    /**
     * 画笔颜色按钮
     */
    private ImageButton mDrawPaint;
    /**
     * 画笔工具条中的页码控件
     */
    private TextView mDocIndex;
    /**
     * 实际文档控件父控件
     */
    private CCDocRelativeLayout bigContainer;
    /**
     * 手势控制按钮，可以拖动、点击，分手势模式和绘制模式
     * 按钮本身可以拖动， 手势模式，可以放大缩小文档、拖动、单击、双击等
     * 绘制模式可以使用画笔功能
     */
    private Button gestureButton;
    /**
     * 手势控制处理类
     */
    private GestureViewBinder mGestureViewBinder;
    /**
     * 当前是否支持手势
     */
    private boolean supportGesture =false;
    /**
     * 上一次的文档 id
     */
    private String lastDocId;

    /**
     * 当前是否是全屏文档，竖屏状态下
     * @return isDocFullScreen
     */
    public boolean isDocFullScreen() {
        return isDocFullScreen;
    }

    /**
     * 竖屏状态文档全屏标记
     */
    private boolean isDocFullScreen = false;
    /**
     * 画笔选择框控件
     */
    private View mPopupView;
    /**
     * 横屏控制控件
     */
    private View mControllerHorizontal;
    /**
     * 颜色控件
     */
    private RecyclerView mColors;
    /**
     * 画笔颜色封装控件
     */
    private BubblePopupWindow mPopupWindow;
    private int[] mColorResIds = new int[]{
            R.drawable.black_selector, R.drawable.orange_selector, R.drawable.green_selector,
            R.drawable.blue_selector, R.drawable.gray_selector, R.drawable.red_selector
    };

    private int[] mColorValues = new int[]{
            Color.parseColor("#000000"), Color.parseColor("#f27a1a"), Color.parseColor("#70c75e"),
            Color.parseColor("#78a7f5"), Color.parseColor("#7b797a"), Color.parseColor("#e33423")
    };
    private final String[] mColorStr = new String[]{
            "000000", "f27a1a", "70c75e", "78a7f5", "7b797a", "e33423"
    };
    /**
     * 当前画笔颜色选择索引
     */
    private int mDrawColorPosition = 0;
    /**
     * 当前页码
     */
    private int mCurPosition = -1;

    /**
     * 当前文档 ID
     */
    private String mLocalDocId;
    /**
     * 当前方向
     */
    private int orientation;
    /**
     * 当前角色
     */
    private int mRole;
    /**
     * 初始化完成
     */
    private boolean isViewInitialize;
    private Activity mActivity;
    private DocInfo mCurDocInfo, mPreDocInfo;
    //文档翻页的点击事件（返回翻页按钮）
    private int totalstep;
    private int stepnum = 0;
    private boolean isFirstChangePage = false;
    private Animation mBottomDownAni;//向下画出
    private Animation mBottomUpAni;//底部布局向上滑入
    private HiddenBottomRunnable hiddenBottomRunnable;
    private ColorAdapter colorAdapter;
    private static final String TAG = "CCDocView";

    public CCDocView(Context context) {
        super(context);
        initView(context);
    }
    public CCDocView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CCDocView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_doc_layout, this);
        mDocArea = findViewById(R.id.id_lecture_doc_area);
        mDocBottomLayout = findViewById(R.id.id_lecture_doc_bottom_layout);
        mDocView = findViewById(R.id.id_lecture_doc_display);
        mDocWebView = findViewById(R.id.id_lecture_docppt_display);
        mDocBack = findViewById(R.id.id_lecture_doc_back);
        mDocForward = findViewById(R.id.id_lecture_doc_forward);
        mImgGrid = findViewById(R.id.id_lecture_doc_img_grid);
        mFullScreen = findViewById(R.id.id_lecture_doc_fullscreen);
        mPrepareLayout = findViewById(R.id.id_lecture_prepare_layout);
        mUpdateTip = findViewById(R.id.id_lecture_doc_update_tip);
        mExitFullScreen = findViewById(R.id.id_lecture_doc_exit_fullscreen);
        mDrawLayout = findViewById(R.id.id_lecture_drag_child);
        mClear = findViewById(R.id.id_lecture_draw_clear);
        mPageChangeLayout = findViewById(R.id.id_lecture_page_change_layout);
        ImageButton mBarDocBack = findViewById(R.id.id_lecture_bar_doc_back);
        ImageButton mBarDocForward = findViewById(R.id.id_lecture_bar_doc_forward);
        mDrawPaint = findViewById(R.id.id_lecture_draw_paint);
        mDocIndex = findViewById(R.id.id_lecture_bar_doc_index);
        ImageButton mUndo = findViewById(R.id.id_lecture_draw_undo);
        mControllerHorizontal = findViewById(R.id.controller_horizontal);
        bigContainer = findViewById(R.id.bigContainer);
        gestureButton = findViewById(R.id.gestureButton);

        allSetOnClickListener(mImgGrid, mFullScreen, mExitFullScreen,
                mDrawPaint, mUndo, mClear, mBarDocBack, mBarDocForward,
                mDocBack, mDocForward, mControllerHorizontal);
    }
    /**
     * 初始化角色方向等
     * @param activity activity
     * @param mRole       用户角色
     * @param orientation 屏幕模式
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initRole(Activity activity, int mRole,final int orientation) {
        try {
            this.orientation = orientation;
            this.mRole = mRole;
            this.mActivity = activity;
            if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {//老师端// 进行横竖屏
                mImgGrid.setVisibility(View.GONE);
                mDocWebView.setVisibility(View.VISIBLE);//ppt动画类
                mDocView.setVisibility(View.VISIBLE);//画板和文档类（图片，普通ppt）
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                boolean noIntercept = (orientation == 1||(orientation==0)&&isDocFullScreen);
                mDocView.setTouchInterceptor(noIntercept, mRole);//设置画笔颜色老师为红色，学生为蓝色
                CCDocViewManager.getInstance().setDocHistory(mDocView, mDocWebView,CCDocViewManager.DOC_MODE_FILL_WIDTH);//设置文档视图样式
                mDocView.setDocWebViewSetVisibility(mDocWebView);
                mDocArea.setBackgroundColor(Color.parseColor("#ffffff"));
                if (orientation == CCRoomActivity.ORIENTATION_PORTRAIT) {//竖屏模式
                    mFullScreen.setVisibility(View.VISIBLE);
                } else {//横屏模式。
                    mDrawLayout.setVisibility(VISIBLE);
                    mFullScreen.setVisibility(View.GONE);
                }
                authTeacher = true;
            } else { // 学生 进行横竖屏
//                mDocProgress.setVisibility(View.GONE);
                mImgGrid.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                mDocWebView.setVisibility(View.VISIBLE);
                // 设置图片和文档和默认背景颜色，只能以 #FFFFFF 形式设置
                mDocWebView.setDocBackGroundColor("#FFFFFF");
                mDocView.setVisibility(View.VISIBLE);
                if (isDocFullScreen || orientation == 1) {
                    boolean isAuth = false;
                    mDocView.setTouchInterceptor(isAuth, mRole);
                }
                CCDocViewManager.getInstance().setDocHistory(mDocView, mDocWebView,CCDocViewManager.DOC_MODE_FILL_WIDTH);
                mDocView.setDocWebViewSetVisibility(mDocWebView);
                mDocArea.setBackgroundColor(Color.parseColor("#ffffff"));
                if (orientation == CCRoomActivity.ORIENTATION_PORTRAIT) {
                    mFullScreen.setVisibility(View.VISIBLE);
                } else {
                    mFullScreen.setVisibility(View.GONE);
                }
            }


            mDocWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    String errMsg = "onReceivedError,description:"+description+",errorCode"+errorCode+",failingUrl:"+failingUrl;
                    Tools.handleException(errMsg);
                    LogUtil.e(TAG,errMsg);
                }
                @Override
                public void onLoadResource(WebView view, String url) {
                    if(!NetUtils.isNetworkAvailable(mActivity)){
                        view.loadUrl("about:blank");
                    }
                    super.onLoadResource(view, url);
                }

            });
            if (orientation == 0) {
                mControllerHorizontal.setVisibility(GONE);
                initBottomAnimation();
            } else {
                mControllerHorizontal.setVisibility(VISIBLE);
            }
            bigContainer.setClickEventListener(new CCDocRelativeLayout.OnClickListener() {
                @Override
                public void onClick() {
                    if(orientation==1){
                        if (mDocHandleListener != null)
                            mDocHandleListener.showActionBar();
                    }else {
                        if(!isDocFullScreen)
                        handleClickRootView();
                    }
                }
            });
            initDrawPopup();

            initGesture();

            //初始化文档放置区域
            calDocArea();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 初始化手势相关
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initGesture() {
        //默认竖屏不支持手势
        setGestureAction(orientation != CCRoomActivity.ORIENTATION_PORTRAIT);
        gestureButton.setVisibility(orientation== ORIENTATION_LANDSCAPE&&mRole==CCAtlasClient.PRESENTER?VISIBLE:GONE);
        DragTouchListener mDragTouchListener = new DragTouchListener(getContext(), gestureButton);
        mDragTouchListener.setOnClickListener(new DragTouchListener.DragOnclickListener() {
            @Override
            public void onClick() {
                supportGesture = !supportGesture;
                bigContainer.setCanDraw(!supportGesture);
                selectGestureMode(supportGesture);
            }
        });
        gestureButton.setOnTouchListener(mDragTouchListener);
        gestureButton.setBackground(Tools.getGradientDrawable(getResources().getColor(R.color.translucent_bg), 10));
        mDrawLayout.setOnTouchListener(new DragTouchListener(getContext(),mDrawLayout));

        post(new Runnable() {
            @Override
            public void run() {
                MarginLayoutParams params = (MarginLayoutParams) mDrawLayout.getLayoutParams();
                params.leftMargin = (DensityUtil.getWidth(mActivity)-mDrawLayout.getWidth())>>1;
                mDrawLayout.setLayoutParams(params);
                MarginLayoutParams paramsGesture = (MarginLayoutParams) gestureButton.getLayoutParams();
                paramsGesture.leftMargin = (DensityUtil.getWidth(mActivity)-params.leftMargin-gestureButton.getWidth());
                gestureButton.setLayoutParams(paramsGesture);

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_lecture_doc_img_grid) {
            showDocImgGrid();
        } else if (id == R.id.id_lecture_doc_fullscreen) {
            docFullScreen();
        } else if (id == R.id.id_lecture_doc_exit_fullscreen) {
            docExitFullScreen();
        } else if (id == R.id.id_lecture_draw_paint) {
            showPaint();
        } else if (id == R.id.id_lecture_draw_undo) {
            undo();
        } else if (id == R.id.id_lecture_draw_clear) {
            clear();
        } else if (id == R.id.id_lecture_bar_doc_back || id == R.id.id_lecture_doc_back) {
            docBack();
        } else if (id == R.id.id_lecture_bar_doc_forward || id == R.id.id_lecture_doc_forward) {
            docForward();
        } else if (id == R.id.controller_horizontal) {
            if (mDocHandleListener != null)
                mDocHandleListener.showActionBar();
        } else if (id == R.id.id_lecture_doc_display) {
            handleClickRootView();
        }

    }

    private boolean isShow = true;
    private boolean isPerformingAnim = false;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        float size;
        if (checkedId == R.id.id_small_size) {
            size = 1.5f;
        } else if (checkedId == R.id.id_mid_size) {
            size = 4.5f;
        } else {
            size = 7.5f;
        }
        // 设置尺寸
        setStrokeWidth(size);
    }

    /**
     * 处理文档跳页、上传图片、选择文档等
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data == null) {
            return;
        }
        if (resultCode == Config.DOC_LIST_RESULT_CODE) {
            CCAtlasClient  mCCAtlasClient = CCAtlasClient.getInstance();
            DocInfo docInfo = (DocInfo) data.getSerializableExtra("selected_doc");
            //老师切换文档
            if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getAssistantSwitch() == 1) { //有助教功能
               setAssistDocInfo(docInfo, 0, false);
            } else {
               setDocInfo(docInfo, 0, 0);
            }
        }  else if (resultCode == Config.DOC_BOARD_RESULT_CODE) {
            CCAtlasClient  mCCAtlasClient = CCAtlasClient.getInstance();
            DocInfo docInfo = (DocInfo) data.getSerializableExtra("selected_doc_board");
            //老师切换白板
            if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getAssistantSwitch() == 1) { //有助教功能
               setAssistDocInfo(docInfo, -1, false);
            } else {
                setDocInfo(docInfo, -1, 0);
            }
        }else if (requestCode == Config.LECTURE_REQUEST_CODE && resultCode == Config.DOC_GRID_RESULT_CODE) {
            int position = data.getIntExtra("doc_img_grid_position", 0);
            isFirstChangePage = false;
            mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(position), position, mCurDocInfo.getDocId(), mCurDocInfo.getName());
            sendPageChange(position);
        }else if (requestCode == Config.REQUEST_SYSTEM_PICTURE_UPDATE) {
            String imgPath = ImageUtil.checkImg(data.getData());
            if (!TextUtils.isEmpty(imgPath)) {
                try {
                    ImageUtil.compressBitmap(imgPath, requestCode,new ImageUpdateListener(){

                        @Override
                        public void updateSuccess(String docId) {
                            dismissDocLoading(docId, true);
                        }

                        @Override
                        public void updateFailure(String msg) {
                            Tools.showToast(msg);
                            dismissDocLoading(null, false);

                        }

                        @Override
                        public void updateLoading(long progress) {
                            showDocLoading(progress);
                        }
                    });
                } catch (Exception e) {
                    Tools.showToast("图片加载失败");
                }
            } else {
                Tools.showToast("图片加载失败");
            }
        }
    }

    /**
     * 展示文档加载进度
     * @param progress progress
     */
    public void showDocLoading(final long progress) {
        post(new Runnable() {
            @Override
            public void run() {
                if(mPrepareLayout.getVisibility()!=View.VISIBLE){
                    mPrepareLayout.setVisibility(View.VISIBLE);
                }
                mUpdateTip.setText(String.format("%s%d%%", mActivity.getString(R.string.cc_lecture_doc_prepare), progress));

            }
        });
    }

    /**
     * 设置文档背景颜色，默认为白色
     * 如"#FFFFFF",默认#FFFFFF
     * @param color 默认背景颜色，默认为白色
     */
    public void setDocBackgroundColor(String color){
        mDocWebView.setDocBackGroundColor(color);
    }

    /**
     * 切换到白板的快捷方法
     */
    public void docPageToWhiteBoard(){

       DocInfo docInfo =  CCDocViewManager.getInstance().getWhiteBoardInfo();
        CCAtlasClient  mCCAtlasClient = CCAtlasClient.getInstance();
        //老师切换白板
        if (mCCAtlasClient.getInteractBean() != null && mCCAtlasClient.getInteractBean().getAssistantSwitch() == 1) { //有助教功能
            setAssistDocInfo(docInfo, -1, false);
        } else {
            setDocInfo(docInfo, -1, 0);
        }
    }


    /**
     * 房间有助教功能
     * 老师/助教文档处理
     *
     * @param docInfo docInfo
     * @param position position
     * @param isFirst isFirst
     */
    public void setAssistDocInfo(DocInfo docInfo, int position, boolean isFirst) {
        Tools.log(TAG,"setAssistDocInfo===");
        isFirstChangePage = isFirst;
        setDocInfo(docInfo, position, 0);
    }

    public void sendCurrentDocPage() {
        if (mCurDocInfo == null) {
            mCurPosition = -1;
        }
        isFirstChangePage = false;
        sendPageChange(mCurPosition);
    }

//    public void setDrawUtilShow(boolean isShow) {
//        mDrawLayout.setVisibility(isShow?VISIBLE:GONE);
//    }

    public void teacherSetupTeacherPage(int CurPosition) {
        mCurPosition = CurPosition;
        if (mCurDocInfo == null) {
            return;
        }
        if ( mRole == CCAtlasClient.PRESENTER ) {
            onLecture(CurPosition + 1, mCurDocInfo.getPageTotalNum());
        }
        mDocIndex.setText(CurPosition + 1 + "/" + mCurDocInfo.getPageTotalNum());
    }
    public void onLecture(int cur, int total) {
        if (mPageChangeLayout.getVisibility() != View.VISIBLE) {
            mPageChangeLayout.setVisibility(View.VISIBLE);
        }
        mDocIndex.setText(cur + "/" + total);
        if (total == 1) {
            mPageChangeLayout.setVisibility(View.GONE);
        }
    }
    private void selectGestureMode(boolean isGestureMode){
        if(isGestureMode){
            gestureButton.setText("手势模式");
            setGestureAction(true);
        }else {
            gestureButton.setText("绘制模式");
            setGestureAction(false);
        }
    }

    /**
     * 停止上课文档处理，清除画笔学生或者横屏状态默认支持手势
     */
    public void stopClass() {
        Tools.log(TAG,"stopClass:");
        clearAll();
        if(mRole!=CCAtlasClient.PRESENTER||orientation==1)
        selectGestureMode(true);

    }

    /**
     * 开始上课，文档重置，竖屏默认不支持手势
     */
    public void startClass() {
        Tools.log(TAG,"startClass:");
        restoreNormal();
        if(orientation==0)
            setGestureAction(false);
    }

    /**
     *手势控制主方法
     * @param supportGesture 是否支持手势
     */
    private void setGestureAction(boolean supportGesture) {
        this.supportGesture = supportGesture;
        mDocView.setGestureAction(supportGesture);
//        mDocView.setClickable(!supportGesture);
        bigContainer.setIntercept(!supportGesture);
        if(supportGesture){
            if(mGestureViewBinder==null){
                mGestureViewBinder = GestureViewBinder.bind(mActivity, bigContainer, mDocWebView);
                //使用了 WebView 的下上滑动，这里就不需要了
                mGestureViewBinder.setSupportVerticalScroll(false);
                bigContainer.setOnDownListener(new CCDocRelativeLayout.OnDownListener() {
                    @Override
                    public void onDown(MotionEvent motionEvent) {
                        mGestureViewBinder.setDownEvent(motionEvent);
                    }
                });

                mGestureViewBinder.setFullGroup(true);
                bigContainer.setBackgroundColor(Color.WHITE);
                CCDocViewManager.getInstance().setDocPageChangeListener(new CCDocChangeListener());
            }
            mGestureViewBinder.setGestureEnable(true);

        }else {
            if(mGestureViewBinder!=null)
                mGestureViewBinder.setGestureEnable(false);
        }

    }

    private class CCDocChangeListener implements CCDocViewManager.OnDocPageChangeListener{

        @Override
        public void onDocPageChange(String docID) {
            if((isDocFullScreen||orientation==1)&&!TextUtils.equals(docID,lastDocId)){
                if(mGestureViewBinder!=null)
                mGestureViewBinder.setBigBack();
                lastDocId = docID;
            }
        }
    }
    private class HiddenBottomRunnable implements Runnable {

        @Override
        public void run() {
            if (mDocHandleListener != null)
                mDocHandleListener.onMenuHideAnimStart();
            mDocBottomLayout.clearAnimation();//向下隐藏
            mDocBottomLayout.startAnimation(mBottomDownAni);
        }
    }

    /**
     * 处理竖屏状态下文档点击出现菜单的
     *
     */
    public void handleClickRootView() {
        if (isDocFullScreen) return;
        if (isPerformingAnim) return;
        if (mDocHandleListener != null)
            mDocHandleListener.onClickDocView(mDocView);
        if (orientation == ORIENTATION_LANDSCAPE) {
            return;
        }
        mDocBottomLayout.clearAnimation();//向下隐藏
        removeCallbacks(hiddenBottomRunnable);
        if (isShow) {
            if (mDocHandleListener != null)
                mDocHandleListener.onMenuHideAnimStart();
            if(mBottomDownAni!=null)
            mDocBottomLayout.startAnimation(mBottomDownAni);

        } else {
            if (mDocHandleListener != null)
                mDocHandleListener.onMenuShowAnimStart();
            mDocBottomLayout.setVisibility(VISIBLE);
            mDocBottomLayout.startAnimation(mBottomUpAni);


        }

    }

    public void docBack() {
        long time = System.currentTimeMillis();
        if (((time - localTime) / 1000) < 1) {
            return;
        }
        localTime = time;

        if (mCurDocInfo != null) {
            if (mCurDocInfo.getDocMode() != 0) {
                mDocWebView.evaluateJavascript("javascript:window.ANIMATIONSTEPSCOUNT", new
                        ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                if (s != null) {
                                    try {
                                        totalstep = Integer.valueOf(s).intValue();
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                mDocWebView.evaluateJavascript("javascript:window.TRIGGERED_ANIMATION_STEP", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (s != null) {
                            try {
                                stepnum = Integer.valueOf(s).intValue();
                                --stepnum;
                                if (stepnum < 0) {
                                    if (mCurPosition == 0) return;
                                    isFirstChangePage = false;
                                    mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(--mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                                    sendPageChange(mCurPosition);
                                } else {
                                    CCDocViewManager.getInstance().pptAnimationChange(mCurDocInfo.getDocId(), stepnum = 0, mCurPosition);

                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });

            } else {
                if (mCurPosition > 0) {
                    isFirstChangePage = false;
                    if (!mCurDocInfo.getDocId().equals("WhiteBorad")) {
                        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(--mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                    } else {
                        mDocView.setDocBackground("#", mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                    }
                    sendPageChange(mCurPosition);
                }
            }
        }
    }

    private long localTime = 0;

    //文档翻页的点击事件（往前翻页按钮）
    public void docForward() {

        long time = System.currentTimeMillis();
        if (((time - localTime) / 1000) < 1) {
            return;
        }
        localTime = time;


        if (mCurDocInfo != null) {
            if (mCurDocInfo.getDocMode() != 0) {
                mDocWebView.evaluateJavascript("javascript:window.ANIMATIONSTEPSCOUNT", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (s != null) {
                            try {
                                totalstep = Integer.valueOf(s);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                mDocWebView.evaluateJavascript("javascript:window.TRIGGERED_ANIMATION_STEP", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (s != null) {
                            try {
                                stepnum = Integer.valueOf(s);
                                ++stepnum;
                                if (stepnum >= totalstep) {
                                    if (mCurPosition == mCurDocInfo.getPageTotalNum() - 1) {
                                        return;
                                    } else {
                                        isFirstChangePage = false;
                                        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(++mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                                        sendPageChange(mCurPosition);
                                    }
                                    stepnum = 0;
                                } else {
                                    CCDocViewManager.getInstance().pptAnimationChange(mCurDocInfo.getDocId(), stepnum, mCurPosition);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            } else {
                isFirstChangePage = false;
                if (mCurPosition < mCurDocInfo.getPageTotalNum() - 1) {
                    if (!mCurDocInfo.getDocId().equals("WhiteBorad")) {
                        mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(++mCurPosition), mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                    } else {
                        mDocView.setDocBackground("#", mCurPosition, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                    }
                    sendPageChange(mCurPosition);
                }
            }
        }
    }

    private void showPaint() {
        if ((mDrawLayout.getY() + mDrawLayout.getHeight() + 20 + mPopupView.getHeight()) >
                DensityUtil.getHeight(mActivity)) {
            mPopupWindow.showArrowTo(mDrawPaint, BubbleStyle.ArrowDirection.Down, 20);
        } else {
            mPopupWindow.showArrowTo(mDrawPaint, BubbleStyle.ArrowDirection.Up, 20);
        }
    }

    public void allSetOnClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    private void initBottomAnimation() {
        mDocArea.setOnClickListener(this);
        hiddenBottomRunnable = new HiddenBottomRunnable();
        mBottomDownAni = AnimationUtils.loadAnimation(getContext(), R.anim.doc_bottom_down);
        mBottomDownAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFullScreen.setClickable(false);
                isPerformingAnim = true;

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShow = false;
                isPerformingAnim = false;
                mDocBottomLayout.setVisibility(GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomUpAni = AnimationUtils.loadAnimation(getContext(), R.anim.doc_bottom_up);
        mBottomUpAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPerformingAnim = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShow = true;
                isPerformingAnim = false;
                mFullScreen.setClickable(true);
                postDelayed(hiddenBottomRunnable, MENU_SHOW_TIME);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void calDocArea() {

        //横屏模式首次进入获取宽高不准确，需要延迟获取
        post(new Runnable() {
            @Override
            public void run() {

                ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
                int width = Tools.getScreenWidth();
                int height;
                if(orientation == 1){
                    height = Tools.getScreenHeight();
                }else {
                    height = width * 9 / 16;
                }
                params.width = width;
                params.height = height;
                Tools.log(TAG, "calDocArea:orientation："+orientation+"， width="+width+"  ,height"+height);
                mDocArea.setLayoutParams(params);
//                mDocView.setWhiteboard(width, height, true);
                if(orientation == 0){
                    mDocView.setDocFrame(width,height,CCDocViewManager.DOC_MODE_FIT);
                }else {
                    mDocView.setDocFrame(width,height,CCDocViewManager.DOC_MODE_FILL_WIDTH);
                }


            }
        });

    }


    private final class RetryTask implements Runnable {

        private int where;

        RetryTask(int where) {
            this.where = where;
        }

        @Override
        public void run() {
            fetchRoomDoc(where);
        }
    }

    private class RoomCallBack implements CCDocViewManager.AtlasCallBack<DocInfo> {

        private int where;

        RoomCallBack(int where) {
            this.where = where;
        }

        @Override
        public void onSuccess(DocInfo docInfo) {
            if (mCurDocInfo != null) {
                return;
            }
            if (docInfo == null) {
                // 表示当前文档正在转换格式
                postDelayed(new RetryTask(where), 1500);
                return;
            }
            isFirstChangePage = false;
            setDocInfo(docInfo, 0, where);
            if (where == 1 && isViewInitialize) {
                mDocView.setDocBackground(mCurDocInfo.getAllImgUrls().get(0), 0, mCurDocInfo.getDocId(), mCurDocInfo.getName());
                mPrepareLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(String err) {
            // 做一次数据恢复
            mCurDocInfo = mPreDocInfo;
            mPreDocInfo = null;
            // 隐藏图片区
            if (where == 1 && isViewInitialize) {
                mPrepareLayout.setVisibility(View.GONE);
            }
            if (where == 1) {
                mPrepareLayout.setVisibility(View.GONE);
            }
        }

    }

    private void fetchRoomDoc(int where) {
        CCDocViewManager.getInstance().getRoomDoc(null, mLocalDocId, new RoomCallBack(where));
    }

    //发送文档翻页事件给对方
    private void sendPageChange(int position) {
        if (position == -1) { // 发送白板
            if (mRole == CCAtlasClient.TALKER) {
                return;
            }
            if (!isFirstChangePage) {
                CCDocViewManager.getInstance().docPageChange("WhiteBorad", "WhiteBorad", 1, "#", false,
                        -1, 0, mDocView.getDocWidth(), mDocView.getDocHeight());
            }
            return;
        }
        // 通知老师更新界面
        if ((mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) && position != -1) {
            onTeacherLecture(position + 1, mCurDocInfo.getPageTotalNum());
        }
        if (position != -1) {
            mDocIndex.setText(position + 1 + "/" + mCurDocInfo.getPageTotalNum());
        }

        if (mCurDocInfo.getPageTotalNum() == 1) {
            if (isDocFullScreen) {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            } else {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            }
        } else {
            if (position == 0) {
                if (orientation == 0) {
                    if (isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                }
            } else if (position == mCurDocInfo.getPageTotalNum() - 1) {
                if (orientation == 0) {
                    if (isDocFullScreen) {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        mDocForward.setVisibility(View.VISIBLE);
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocWebView.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (orientation == 0) {
                    if (isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (mCurDocInfo.getDocId().equals("WhiteBorad")) {
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mPageChangeLayout.setVisibility(View.GONE);
            mImgGrid.setVisibility(View.GONE);
        }

        if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
            LogUtil.e(TAG,"isFirstChangePage========="+isFirstChangePage);
            if (!isFirstChangePage) {
                if (position <= mCurDocInfo.getPageTotalNum() - 1 && mCurDocInfo.getAllImgUrls()
                        .size() > 0) {
                    String url = mCurDocInfo.getAllImgUrls().get(position);
                    int width = mCurDocInfo.getWith();
                    int height = mCurDocInfo.getHeight();
                    Tools.log(TAG,"发送的宽高：width"+width+" ,height="+height);
//                    if (orientation == 0) {
                        CCDocViewManager.getInstance().docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
                                url, mCurDocInfo.isUseSDK(), position, mCurDocInfo.getDocMode(), mDocView.getDocWidth(), mDocView.getDocHeight());
//                    } else {
//                        CCDocViewManager.getInstance().docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
//                                url, mCurDocInfo.isUseSDK(), position, mCurDocInfo.getDocMode(), mDocView.getDocWidth(), mDocView.getDocHeight());
//                    }
                }
            }
        } else {
            if (!isFirstChangePage) {
                if (position <= mCurDocInfo.getPageTotalNum() - 1) {
                    if (!mCurDocInfo.getDocId().equals("WhiteBorad")) {
                        if (mCurDocInfo.getAllImgUrls().size() > 0) {
                            String url = mCurDocInfo.getAllImgUrls().get(position);
                            CCDocViewManager.getInstance().docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
                                    url, mCurDocInfo.isUseSDK(), position, mCurDocInfo.getDocMode(), mCurDocInfo.getWith(), mCurDocInfo.getHeight());
                        }
                    } else {
                        CCDocViewManager.getInstance().docPageChange(mCurDocInfo.getDocId(), mCurDocInfo.getName(), mCurDocInfo.getPageTotalNum(),
                                "#", mCurDocInfo.isUseSDK(), position, mCurDocInfo.getDocMode(), mCurDocInfo.getWith(), mCurDocInfo.getHeight());
                    }
                }
            }
        }
    }

    public void onTeacherLecture(int cur, int total) {
        if (mPageChangeLayout.getVisibility() != View.VISIBLE) {
            mPageChangeLayout.setVisibility(View.VISIBLE);
        }
        mDocIndex.setText(cur + "/" + total);
        if (total == 1) {
            mPageChangeLayout.setVisibility(View.GONE);
        }

    }

    public void onStudentLecture(int cur, int total) {
        if (total == 0 && orientation == 1) {
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mPageChangeLayout.setVisibility(View.GONE);
        }
        if (mCurDocInfo != null && mCurDocInfo.isSetupTeacher() && orientation == 1) {
            mClear.setVisibility(VISIBLE);
            if (total > 0 && mPageChangeLayout.getVisibility() != View.VISIBLE) {
                mPageChangeLayout.setVisibility(View.VISIBLE);
            }
            mDocIndex.setText(cur + "/" + total);
            if (total == 1) {
                mPageChangeLayout.setVisibility(View.GONE);
            } else if (total > 1) {
                mDocBack.setVisibility(View.VISIBLE);
                mDocForward.setVisibility(View.VISIBLE);
                mDocIndex.setVisibility(View.VISIBLE);
            }
            if (isWhitboard()) {
                mPageChangeLayout.setVisibility(View.GONE);
            }
        } else {
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mClear.setVisibility(GONE);
        }
    }

    /**
     * 初始化画笔工具箱
     */
    private void initDrawPopup() {
        mPopupView = LayoutInflater.from(getContext()).inflate(R.layout.view_doc_draw_bubble_layout, null);
        BubbleRelativeLayout mBubbleLayout = mPopupView.findViewById(R.id.id_bubble_layout);
        mPopupWindow = new BubblePopupWindow(mPopupView, mBubbleLayout);
        mPopupWindow.setCancelOnTouch(false);
        mPopupWindow.setCancelOnTouchOutside(true);
        RadioGroup colorSizeGroup = mPopupView.findViewById(R.id.id_draw_size_layout);
        colorSizeGroup.setOnCheckedChangeListener(this);
        mColors = mPopupView.findViewById(R.id.id_draw_bubble_colors);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        mColors.setLayoutManager(layoutManager);
        colorAdapter = new ColorAdapter(mActivity);
        ArrayList<ColorStatus> colorStatuses = new ArrayList<>();
        mDrawColorPosition = mRole==0?5:3;
        ColorStatus colorStatus;
        for (int i = 0; i < mColorResIds.length; i++) {
            colorStatus = new ColorStatus();
            if (i == mDrawColorPosition) {
                colorStatus.setSelected(true);
            } else {
                colorStatus.setSelected(false);
            }
            colorStatus.setResId(mColorResIds[i]);
            colorStatuses.add(colorStatus);
        }
        colorAdapter.bindDatas(colorStatuses);
        mColors.addItemDecoration(new DividerGridItemDecoration(mActivity));
        mColors.setAdapter(colorAdapter);
        mColors.addOnItemTouchListener(new BaseOnItemTouch(mColors, new com.bokecc.room.ui.listener.OnClickListener() {

            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                final int position = mColors.getChildAdapterPosition(viewHolder.itemView);
                if (mDrawColorPosition == position) {
                    return;
                }
                changeSelectColor(position);
            }
        }));
    }

    private void changeSelectColor(int position) {
        ColorStatus pre = new ColorStatus();
        pre.setSelected(false);
        pre.setResId(colorAdapter.getDatas().get(mDrawColorPosition).getResId());
        colorAdapter.update(mDrawColorPosition, pre);
        ColorStatus cur = new ColorStatus();
        cur.setSelected(true);
        cur.setResId(colorAdapter.getDatas().get(position).getResId());
        colorAdapter.update(position, cur);
        mDrawColorPosition = position;
        // 设置颜色
        setColor(mColorValues[position], Integer.parseInt(mColorStr[position], 16));
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isViewInitialize = true;
    }

    private void updatePopColor() {
        String color = mRole == 0 ? "e33423" : "78a7f5";
        int position = 3;
        for (int i = 0; i < mColorStr.length; i++) {
            if (color.equalsIgnoreCase(mColorStr[i])) {
                position = i;
                break;
            }

        }
        changeSelectColor(position);

    }

    //设置画笔实线的宽度
    public void setStrokeWidth(float width) {
        mDocView.setStrokeWidth(width);
    }

    /**
     * 设置文档信息
     * @param docInfo docInfo
     * @param position position
     * @param where where
     */
    public void setDocInfo(DocInfo docInfo, int position, int where) {
        mCurDocInfo = docInfo;
        mCurPosition = position;
        stepnum = docInfo.getStep();
        if (!isViewInitialize) {// 布局以及销毁
            return;
        }

        if (orientation == 1) {
            mFullScreen.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
        } else {
            mFullScreen.setVisibility(View.VISIBLE);
        }
        if (where == 0) {
            if (docInfo.getAllImgUrls().size() > 1) {
                if (mCurDocInfo.isSetupTeacher() && mRole == CCAtlasClient.TALKER) {
                    if (orientation == 1) {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                        mPageChangeLayout.setVisibility(View.VISIBLE);
                    } else {
                        if (isDocFullScreen) {
                            mImgGrid.setVisibility(View.GONE);
                        } else {
                            mImgGrid.setVisibility(View.VISIBLE);
                        }
                        mDocForward.setVisibility(View.VISIBLE);
                        mDocBack.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
                        if (orientation == 1) {
                            mImgGrid.setVisibility(View.GONE);
                            mDocForward.setVisibility(View.GONE);
                        } else {
                            if (isDocFullScreen) {
                                mPageChangeLayout.setVisibility(View.VISIBLE);
                                mDocForward.setVisibility(View.GONE);
                                mImgGrid.setVisibility(View.GONE);
                            } else {
                                mPageChangeLayout.setVisibility(View.GONE);
                                mDocForward.setVisibility(View.VISIBLE);
                                mImgGrid.setVisibility(View.VISIBLE);
                            }
                        }
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        mImgGrid.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    }
                }
            } else {
                mImgGrid.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
                mPageChangeLayout.setVisibility(View.GONE);
            }
            mPrepareLayout.setVisibility(View.GONE);
        } else {
            mPrepareLayout.setVisibility(View.VISIBLE);
            mImgGrid.setVisibility(View.GONE);
        }
        // 通知老师更新界面
        if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
            onTeacherLecture(mCurPosition + 1, docInfo.getAllImgUrls().size());
        }

        if (mCurDocInfo.isSetupTeacher() && mRole == CCAtlasClient.TALKER) {
            isFirstChangePage = true;
            sendPageChange(mCurPosition);
        } else if (mRole == CCAtlasClient.PRESENTER) {
            sendPageChange(mCurPosition);
        } else if (mRole == CCAtlasClient.ASSISTANT) {

            sendPageChange(mCurPosition);
        }
    }

    /**
     * 设为讲师的通知接口
     *
     * @param docInfo docInfo
     * @param position position
     */
    public void setupTeacherFlag(DocInfo docInfo, int position) {
        mCurDocInfo = docInfo;
        mCurPosition = position;
        if (mCurDocInfo.getStep() != -1) {
            stepnum = docInfo.getStep();
        }
        if (mCurDocInfo.getPageTotalNum() == 0) {
            mImgGrid.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mPageChangeLayout.setVisibility(View.GONE);
        } else {
            if (!isDocFullScreen) {
                if (orientation == 0) {
                    mImgGrid.setVisibility(View.VISIBLE);
                } else {
                    mImgGrid.setVisibility(View.GONE);
                }
            } else {
                mPageChangeLayout.setVisibility(View.VISIBLE);
            }

        }
        isFirstChangePage = true;
        if (mRole == CCAtlasClient.TALKER) {
            onStudentLecture(mCurPosition + 1, docInfo.getAllImgUrls().size());
        }
        if (mCurDocInfo.isSetupTeacher() && mRole == CCAtlasClient.TALKER) {
            setupTeacherPageChange(position);
        }
    }

    private void setupTeacherPageChange(int position) {
        // 设为讲师，通知学生更新界面，横屏界面。
        onStudentLecture(position + 1, mCurDocInfo.getPageTotalNum());
        mDocIndex.setText(position + 1 + "/" + mCurDocInfo.getPageTotalNum());

        if(orientation==0&&isDocFullScreen){
            mClear.setVisibility(VISIBLE);
        }
        if (mCurDocInfo.getPageTotalNum() == 1) {
            if (isDocFullScreen) {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            } else {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            }
        } else {
            if (position == 0) {
                if (orientation == 0) {
                    if (isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                }
            } else if (position == mCurDocInfo.getPageTotalNum() - 1) {
                if (orientation == 0) {
                    if (isDocFullScreen) {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        if (mCurDocInfo.isUseSDK()) {
                            mDocForward.setVisibility(View.VISIBLE);
                            mDocBack.setVisibility(View.VISIBLE);
                            mDocWebView.setVisibility(View.VISIBLE);
                        } else {
                            if (mCurDocInfo.getPageTotalNum() == 0) {
                                mDocForward.setVisibility(View.GONE);
                                mDocBack.setVisibility(View.GONE);
                                mDocWebView.setVisibility(View.GONE);
                            } else {
                                mDocForward.setVisibility(View.VISIBLE);
                                mDocBack.setVisibility(View.VISIBLE);
                                mDocWebView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            } else {
                if (orientation == 0) {
                    if (isDocFullScreen) {
                        mDocBack.setVisibility(View.GONE);
                        mDocForward.setVisibility(View.GONE);
                    } else {
                        mDocBack.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (mCurDocInfo.getDocId().equals("WhiteBorad")) {
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
            mPageChangeLayout.setVisibility(View.GONE);
        }

        if (orientation == 1) {
            mDocForward.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
        }

    }

    public void dismissDocLoading(final String docId, final boolean isSuccess) {
        mPreDocInfo = mCurDocInfo;
        mCurDocInfo = null;
        post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    // 进行恢复文档
                    mLocalDocId = docId;
                    fetchRoomDoc(1);
                } else {
                    mPrepareLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 初始化进入或者开课后调用此方法
     */
    public void restoreNormal() {
        mDocWebView.loadUrl("about:blank");
        mDocWebView.setDocBackground(CCDocViewManager.DOC_MODE_FILL_WIDTH);
        Tools.log(TAG,"restoreNormal");
        mDocView.clearAll();
        mDocView.reset(); // 重置绘画板 520版本新增
//        mDocArea.setEnabled(true);
    }

    /**
     * 清除全部，重置画板
     */
    public void clearAll() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mDocView.clearAll();
        mDocView.reset(); // 重置绘画板
//        mDocArea.setEnabled(true);
    }

    //设置画笔颜色
    public void setColor(int color, int colorStr) {
        mDocView.setColor(color, colorStr);
    }

    //设置画笔撤销
    public void undo() {
        if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
            mDocView.teacherUndo();
        } else {
            mDocView.undo();
        }
    }

    //设置画笔清除
    public void clear() {
        mDocView.clear();
    }

    //设置白板
    public boolean isWhitboard() {
        return mCurDocInfo == null;
    }

    /**
     * 跳转到查看当前文档所有页面的按钮
     */
    void showDocImgGrid() {
        Bundle bundle = new Bundle();
        if (mCurDocInfo == null) {
            bundle.putStringArrayList("doc_img_list", null);
        } else {
            bundle.putStringArrayList("doc_img_list", mCurDocInfo.getAllImgUrls());
        }
        Intent intent = new Intent(mActivity, DocImgGridActivity.class);
        intent.putExtras(bundle);
        mActivity.startActivityForResult(intent, Config.LECTURE_REQUEST_CODE);
    }

    /**
     * 点击放大文档按钮（在竖屏模式下才有这个按钮）
     */
    public void docFullScreen() {
        if (isDocFullScreen) {
            return;
        }
        isDocFullScreen = true;
        mFullScreen.setVisibility(View.GONE);

        removeCallbacks(hiddenBottomRunnable);
        mDocBottomLayout.clearAnimation();

        int screenHeight = DensityUtil.getRealHeight(mActivity).y;
        int screenWidth = Tools.getScreenWidth();
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        params.height = screenWidth;
        params.width = screenHeight;
        mDocArea.setLayoutParams(params);

        Tools.log("CCDocView","--docFullScreen--screenHeight--"+screenHeight+"--screenWidth=="+screenWidth);
        if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
            mImgGrid.setVisibility(View.GONE);
            mDrawLayout.setVisibility(View.VISIBLE);
            mClear.setVisibility(View.VISIBLE);
            mDocForward.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            if (isWhitboard()) {
                mPageChangeLayout.setVisibility(View.GONE);
            } else {
                if (mCurDocInfo.getPageTotalNum() == 1) {
                    mPageChangeLayout.setVisibility(View.GONE);
                } else if (mCurDocInfo.getPageTotalNum() == 0) {
                    mPageChangeLayout.setVisibility(View.GONE);
                } else {
                    mPageChangeLayout.setVisibility(View.VISIBLE);
                }
            }
            authTeacher = true;
        }
        if (mCurDocInfo != null) {
            if (mCurDocInfo.isSetupTeacher() && mRole == CCAtlasClient.TALKER) {
                mImgGrid.setVisibility(View.GONE);
                mDocView.setNoInterceptor(true);
                mDrawLayout.setVisibility(View.VISIBLE);
                mClear.setVisibility(View.VISIBLE);
                mDocForward.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
                if (mCurDocInfo.getPageTotalNum() <= 0) {
                    mPageChangeLayout.setVisibility(View.GONE);
                } else {
                    if (mCurDocInfo.getPageTotalNum() == 1) {
                        mPageChangeLayout.setVisibility(View.GONE);
                    } else {
                        mPageChangeLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (mCurDocInfo.getDocId().equals("WhiteBorad")) {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                mPageChangeLayout.setVisibility(View.GONE);
            }
        }

//        mDocView.rotate(true);
        mDocView.setDocFrame(screenHeight,screenWidth,2);
        if (authTeacher||authDraw) { // 文档全屏如果被授权的时候 设置文档view可绘制
            mDocView.setNoInterceptor(true);
            mDrawLayout.setVisibility(View.VISIBLE);
            mClear.setVisibility(authTeacher?VISIBLE:View.GONE);
        } else {
            mDocView.setNoInterceptor(false);
            mDrawLayout.setVisibility(View.GONE);
        }
        mExitFullScreen.setVisibility(VISIBLE);
        if (mDocHandleListener != null)
            mDocHandleListener.docFullScreen();

        //手势控制
        if(authTeacher||authDraw){
            gestureButton.setVisibility(VISIBLE);
            if("手势模式".equals(gestureButton.getText())){
                setGestureAction(true);
            }
        }else {
            setGestureAction(true);
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (orientation == 0 || !isDocFullScreen)
            postDelayed(hiddenBottomRunnable, MENU_SHOW_TIME);
    }

    /**
     * 点击退出文档按钮（在竖屏模式下才有这个按钮）
     */
    public void docExitFullScreen() {
        if (!isDocFullScreen) {
            return;
        }
        //手势控制
        setGestureAction(false);
        if(mGestureViewBinder!=null)
            mGestureViewBinder.setBigBack();
        gestureButton.setVisibility(GONE);

        isDocFullScreen = false;
        mExitFullScreen.setVisibility(View.GONE);
        mDocView.setNoInterceptor(false);

        mDrawLayout.setVisibility(View.GONE);
        postDelayed(hiddenBottomRunnable, MENU_SHOW_TIME);


        if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT) {
            if (!isWhitboard()) {
                if (mCurDocInfo.getPageTotalNum() == 1) {
                    mDocForward.setVisibility(View.GONE);
                    mDocBack.setVisibility(View.GONE);
                } else {
                    mImgGrid.setVisibility(View.VISIBLE);
                    mDocForward.setVisibility(View.VISIBLE);
                    mDocBack.setVisibility(View.VISIBLE);
                }
                if (mCurDocInfo.getPageTotalNum() == 0) {
                    mDocForward.setVisibility(View.GONE);
                    mDocBack.setVisibility(View.GONE);
                    mImgGrid.setVisibility(View.GONE);
                }
            }
        }
        if (mCurDocInfo != null) {
            if (mCurDocInfo.isSetupTeacher() && mRole == CCAtlasClient.TALKER) {
                if (mCurDocInfo.getPageTotalNum() > 0) {
                    mPageChangeLayout.setVisibility(View.GONE);
                    if (mCurDocInfo.getPageTotalNum() == 1) {
                        mDocForward.setVisibility(View.GONE);
                        mDocBack.setVisibility(View.GONE);
                    } else {
                        mImgGrid.setVisibility(View.VISIBLE);
                        mDocForward.setVisibility(View.VISIBLE);
                        mDocBack.setVisibility(View.VISIBLE);

                    }
                } else {
                    mDocForward.setVisibility(View.GONE);
                    mDocBack.setVisibility(View.GONE);
                    mImgGrid.setVisibility(View.GONE);
                }
            }
        }

        int width = DensityUtil.getRealHeight(mActivity).y;
        int height = width * 9 / 16;
        Tools.log("CCDocView","--docExitFullScreen--width--"+width+"--height=="+height);
        ViewGroup.LayoutParams params = mDocArea.getLayoutParams();
        params.height = height;
        params.width = width;
        mDocArea.setLayoutParams(params);
        mDocView.setDocFrame(width,height,1);
//        mDocView.rotate(false);
        if (mDocHandleListener != null)
            mDocHandleListener.exitDocFullScreen();
        mFullScreen.setVisibility(View.VISIBLE);
        mFullScreen.setClickable(true);
        if (mCurDocInfo != null) {
            if (mCurDocInfo.getDocId().equals("WhiteBorad")) {
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
                mPageChangeLayout.setVisibility(View.GONE);
                mImgGrid.setVisibility(View.GONE);
            }
        }
    }

    private boolean authTeacher = false;
    private boolean authDraw = false;

    private void justCanDraw() {
        if (isDocFullScreen || orientation == 1) {
            mDrawLayout.setVisibility(VISIBLE);
            mControllerHorizontal.setVisibility(orientation==1?VISIBLE:GONE);
            mClear.setVisibility(GONE);
            mPageChangeLayout.setVisibility(GONE);
            mDocView.setNoInterceptor(true);
        }else {
            mClear.setVisibility(GONE);
            mPageChangeLayout.setVisibility(GONE);
            mImgGrid.setVisibility(View.GONE);
            mDocBack.setVisibility(View.GONE);
            mDocForward.setVisibility(View.GONE);
        }
    }
    private void setupTeacherView() {
        if (isDocFullScreen || orientation == 1) {
            mDrawLayout.setVisibility(VISIBLE);
            mControllerHorizontal.setVisibility(orientation==1?VISIBLE:GONE);
            mClear.setVisibility(VISIBLE);
            if(mCurDocInfo.getPageTotalNum() >0&&(mRole == CCAtlasClient.TALKER||mRole == CCAtlasClient.PRESENTER)){
                mPageChangeLayout.setVisibility(VISIBLE);
            }else {
                mPageChangeLayout.setVisibility(GONE);
            }
            mDocView.setNoInterceptor(true);
        }else {
            if(mDocView.isWhiteboard()){
                mImgGrid.setVisibility(View.GONE);
                mDocBack.setVisibility(View.GONE);
                mDocForward.setVisibility(View.GONE);
            }else {
                mImgGrid.setVisibility(View.VISIBLE);
                mDocBack.setVisibility(View.VISIBLE);
                mDocForward.setVisibility(View.VISIBLE);
            }
        }
    }

    private void cancelAllAuth(){
        mDocView.setNoInterceptor(false);
        mDrawLayout.setVisibility(GONE);
        mImgGrid.setVisibility(View.GONE);
        mDocBack.setVisibility(View.GONE);
        mDocForward.setVisibility(View.GONE);
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mDocView.reset();
        gestureButton.setVisibility(GONE);
        selectGestureMode(true);
    }

    //监听授权标注和设为讲师的状态
    public void authDrawOrSetupTeacher(String userid, boolean isAuth,int flag) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

        if (CCAtlasClient.getInstance().getUserIdInPusher().equals(userid)) { // 如果是当前用户进行授权标注的处理
           if(flag==2){
               authDraw = isAuth;
               if(isAuth){
                   //之前没有设为讲师
                   if(!authTeacher){
                       justCanDraw();
                       updatePopColor();
                   }
               }else {
                   if(!authTeacher){
                       cancelAllAuth();
                   }
               }
               Tools.log(TAG,isAuth ? "您被老师授权标注" : "您被老师取消授权标注");
               Tools.showToast(isAuth ? "您被老师授权标注" : "您被老师取消授权标注");
           }else {
               authTeacher = isAuth;
               if(isAuth){
                   setupTeacherView();
                   if(!authDraw){
                       updatePopColor();
                   }
               }else {
                   if(authDraw){
                       justCanDraw();
                   }else {
                       cancelAllAuth();
                   }
               }
               Tools.showToast(isAuth ? "您被老师设为讲师" : "您被老师取消设为讲师");
               Tools.log(TAG,isAuth ? "您被老师设为讲师" : "您被老师取消设为讲师");
           }

            //手势控制
            if(mRole== CCAtlasClient.TALKER){
                if(orientation == 0){
                    if(isDocFullScreen&&isAuth&&gestureButton.getVisibility()!=VISIBLE){
                        gestureButton.setVisibility(VISIBLE);
                    }
                }else {
                    if(isAuth&&gestureButton.getVisibility()!=VISIBLE){
                        gestureButton.setVisibility(VISIBLE);
                    }
                }

            }
        }
    }

    /**
     * 设置文档相关监听
     * @param mDocHandleListener 监听器回调
     */
    public void setDocHandleListener(IDocHandleListener mDocHandleListener) {
        this.mDocHandleListener = mDocHandleListener;
    }

    private IDocHandleListener mDocHandleListener;

    /**
     * 文档去监听回调接口
     */
    public interface IDocHandleListener {

        /**
         * 点击退出文档全屏模式的方法
         */
        void exitDocFullScreen();

        /**
         * 点击文档全屏模式的方法
         */
        void docFullScreen();

        /**
         * 横屏状态点击显示上下菜单的方法
         */
        void showActionBar();

        /**
         * 竖屏非全屏状态点击文档区的回调方法
         */
        void onClickDocView(View view);

        /**
         * 竖屏非全屏状态点击文档区菜单出现动画开始
         */
        void onMenuShowAnimStart();

        /**
         * 竖屏非全屏状态点击文档区菜单隐藏动画开始
         */
        void onMenuHideAnimStart();

    }

    /**
     * 释放资源
     */
    public void release() {
        mColorResIds = null;
        mColorValues = null;
        CCDocViewManager.getInstance().release();
    }
}
