package com.bokecc.room.ui.view.menu;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bokecc.ccdocview.SPUtil;
import com.bokecc.common.dialog.BottomMenuDialog;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.example.ccbarleylibrary.CCBarLeyCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.bokecc.sskt.base.CCAtlasClient.MEDIA_MODE_BOTH;

/**
 * 老师底部菜单控件
 * @author swh
 */
public class MenuBottomTeacherView extends RelativeLayout implements View.OnClickListener {


    /***/
    private Activity activity;
    /**监听*/
    private MenuBottomTeacherListener listener;
    /**
     *  开始直播外层布局
     */
    private View teacher_start_layout;
    /**
     * 结束直播外层 root
     */
    private View teacher_stop_layout;

    private ImageButton teacher_stop,teacher_mic,teacher_camera;


    public MenuBottomTeacherView(Context context) {
        super(context);
        initView(context);
    }

    public MenuBottomTeacherView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView(context);
    }

    public MenuBottomTeacherView(Context context, AttributeSet attrs, int deStyleAttr){
        super(context,attrs,deStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_menu_bottom_teacher_layout,this,true);

        View teacher_chat = findViewById(R.id.id_teacher_chat);
        teacher_chat.setOnClickListener(this);
        //老师端开始直播
        teacher_start_layout = findViewById(R.id.id_teacher_start_layout);
        findViewById(R.id.id_teacher_start).setOnClickListener(this);

        teacher_stop_layout = findViewById(R.id.id_teacher_stop_layout);
        //摄像头
        teacher_camera = findViewById(R.id.id_teacher_camera);
        teacher_stop = findViewById(R.id.id_teacher_stop);
        teacher_mic = findViewById(R.id.id_teacher_mic);
        View teacher_more = findViewById(R.id.id_teacher_more);
        teacher_more.setOnClickListener(this);
        teacher_stop.setOnClickListener(this);
        teacher_camera.setOnClickListener(this);
        teacher_mic.setOnClickListener(this);
        initAnimation(context);
    }

    private boolean isPerformingAnim = false;
    private HiddenRunnable hiddenRunnable;
    private Animation showAnimation;
    private Animation hideAnimation;

    private void initAnimation(Context context) {
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.doc_bottom_down);
        showAnimation = AnimationUtils.loadAnimation(context, R.anim.doc_bottom_up);
        hiddenRunnable = new HiddenRunnable();
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPerformingAnim = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPerformingAnim = false;
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPerformingAnim = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPerformingAnim = false;
                postDelayed(hiddenRunnable, Config.MENU_SHOW_TIME);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void removeAnimation() {
        removeCallbacks(hiddenRunnable);
        if(showAnimation!=null)showAnimation.cancel();
        if(hideAnimation!=null)hideAnimation.cancel();
        clearAnimation();
    }

    private class HiddenRunnable implements Runnable{

        @Override
        public void run() {
            clearAnimation();//向下隐藏
            startAnimation(hideAnimation);
        }
    }
    public void startShowAnim(){
        if(isPerformingAnim)return;
        setVisibility(VISIBLE);
        clearAnimation();//向下隐藏
        removeCallbacks(hiddenRunnable);
        startAnimation(showAnimation);
    }
    public void startHideAnim(){
        if(isPerformingAnim)return;
        clearAnimation();//向下隐藏
        removeCallbacks(hiddenRunnable);
        startAnimation(hideAnimation);
    }

    @Override
    public void onClick(View v) {
        if(listener==null)return;
        int id = v.getId();
        if (id == R.id.id_teacher_chat) {
            listener.menuOpenChat();
        }else if(id == R.id.id_teacher_start){
            listener.startLive();
        }else if(id ==R.id.id_teacher_more){
                listener.clickMore();
        }else if(id ==R.id.id_teacher_mic){
            listener.clickMic();
        }else if(id ==R.id.id_teacher_stop){
                listener.clickStopLive();
        }else if(id ==R.id.id_teacher_camera){
                listener.clickCamera();
        }
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setListener(Activity activity, MenuBottomTeacherListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    /**
     * 摄像头图标控制
     * @param flag 开关
     */
    public void setCameraEnable(boolean flag) {
        teacher_camera.setBackgroundResource(flag?R.drawable.camera_selector:R.drawable.camera_close_selector);
    }
    /**
     * 麦克风图标控制
     * @param flag 开关
     */
    public void setMicEnable(boolean flag) {
        teacher_mic.setBackgroundResource(flag?R.drawable.mic_selector:R.drawable.mic_close_selector);
    }

    public void setLiveStatus(boolean status) {
        if(status){
            teacher_start_layout.setVisibility(GONE);
            teacher_stop_layout.setVisibility(VISIBLE);
            setMicEnable(true);
            setCameraEnable(true);
        }else {
            teacher_start_layout.setVisibility(VISIBLE);
            teacher_stop_layout.setVisibility(GONE);
        }
    }


    /**
     * 菜单监听
     * @author swh
     */
    public interface MenuBottomTeacherListener {

        void menuOpenChat();


        void startLive();
        void clickMore();

        void clickCamera();

        void clickStopLive();

        void clickMic();
    }


}
