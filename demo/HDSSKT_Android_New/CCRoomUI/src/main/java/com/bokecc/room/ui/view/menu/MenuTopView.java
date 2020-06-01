package com.bokecc.room.ui.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.view.userList.UserListDialog;
import com.bokecc.sskt.base.bean.CCUser;

import java.util.ArrayList;

/**
 * 聊天控件
 *
 * @author wy
 */
public class MenuTopView extends RelativeLayout {

    private final String TAG = "MenuTopView";

    /***/
    private Context context;
    /**
     * 监听
     */
    private MenuTopListener listener;

    private LinearLayout menu_top_user_ll;
    private TextView menu_top_room_name;
    private TextView menu_top_room_users;
    private ImageButton menu_top_close;
    private ImageButton video_controller;
    private View mRootView;
    private View menu_top_room_handup_flag;
    private boolean isVideoShow;
    private ImageButton teacher_follow;

    public boolean isVideoFollow() {
        return isVideoFollow;
    }

    private boolean isVideoFollow = false;

    public View getShowRootView() {
        return mRootView;
    }

    /**
     * 成员列表 Dialog
     */
    private UserListDialog userListDialog;


    public MenuTopView(Context context) {
        super(context);
        initView(context);
    }

    public MenuTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MenuTopView(Context context, AttributeSet attrs, int deStyleAttr) {
        super(context, attrs, deStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     *
     * @param con
     */
    private void initView(Context con) {
        this.context = con;
        LayoutInflater.from(context).inflate(R.layout.view_menu_top_layout, this, true);

        menu_top_user_ll = findViewById(R.id.menu_top_user_ll);
        menu_top_user_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userListDialog == null) {
                    userListDialog = new UserListDialog(context,listener);
                    mRootView = userListDialog.getWindow().getDecorView().findViewById(android.R.id.content);
                } else
                    userListDialog.initData();

            }
        });
        menu_top_room_name = findViewById(R.id.menu_top_room_name);
        menu_top_room_users = findViewById(R.id.menu_top_room_users);
        menu_top_close = findViewById(R.id.menu_top_close);
        menu_top_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.closeRoom();
            }
        });
        menu_top_room_handup_flag = findViewById(R.id.menu_top_room_handup_flag);
        teacher_follow = findViewById(R.id.id_teacher_follow);
        teacher_follow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.videoFollow();
            }
        });
        video_controller = findViewById(R.id.menu_top_video_controller);
        video_controller.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoShow = !isVideoShow;
                video_controller.setBackgroundResource(isVideoShow ? R.drawable.draw_hide : R.mipmap.draw_hide_on);
                if(listener!=null)
                listener.videoController(isVideoShow);
            }
        });

        initAnimation();


    }

    public void showHandupIcon(boolean isShow){
        if(menu_top_room_handup_flag!=null)
        menu_top_room_handup_flag.setVisibility(isShow?VISIBLE:GONE);
    }


    public boolean isPerformingAnim() {
        return isPerformingAnim;
    }

    private boolean isPerformingAnim = false;
    private HiddenTopRunnable hiddenTopRunnable;
    private Animation showAnimation;
    private Animation hideAnimation;
    private boolean isShow = true;

    public boolean isShow() {
        return isShow;
    }

    private void initAnimation() {
        showAnimation = AnimationUtils.loadAnimation(context, R.anim.doc_top_down);
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.doc_top_up);
        hiddenTopRunnable = new HiddenTopRunnable();
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPerformingAnim = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPerformingAnim = false;
                isShow = false;
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
                isShow = true;
                postDelayed(hiddenTopRunnable, Config.MENU_SHOW_TIME);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void removeAnimation() {
        removeCallbacks(hiddenTopRunnable);
        if (showAnimation != null) showAnimation.cancel();
        if (hideAnimation != null) hideAnimation.cancel();
        clearAnimation();
    }

    private class HiddenTopRunnable implements Runnable {

        @Override
        public void run() {
            clearAnimation();//向下隐藏
            startAnimation(hideAnimation);
        }
    }

    public void startShowAnim() {
        if (isPerformingAnim) return;
        setVisibility(VISIBLE);
        clearAnimation();//向下隐藏
        removeCallbacks(hiddenTopRunnable);
        startAnimation(showAnimation);
    }

    public void startHideAnim() {
        if (isPerformingAnim) return;
        clearAnimation();//向下隐藏
        removeCallbacks(hiddenTopRunnable);
        startAnimation(hideAnimation);
    }


    /**
     * 设置监听
     *
     * @param listener
     */
    public void setListener(MenuTopListener listener) {
        this.listener = listener;
    }

    /**
     * 设置房间名
     *
     * @param roomName
     */
    public void setRoomName(String roomName) {
        menu_top_room_name.setText(roomName);
    }

    /**
     * 设置用户数
     *
     * @param count
     */
    public void setUserCount(int count) {
//        if (userListDialog != null && userListDialog.isShowing() && listener != null) {
//            userListDialog.updateData(listener.getUserList());
//        }
        menu_top_room_users.setText(count + "个成员");
        if(userListDialog.isShowing()){
            userListDialog.updateData(listener.getUserList());
        }
    }

    /**
     * 设置奖杯个数
     * @param users
     */
    public void setUserCupCount(ArrayList<CCUser> users) {
        if (userListDialog != null && userListDialog.isShowing() && listener != null) {
            for (int i= 0;i<listener.getUserList().size();i++){
                for (CCUser ccUserCup: users){
                    if(listener.getUserList().get(i).getUserId().equals(ccUserCup.getUserId())){
                        listener.getUserList().get(i).setCupIndex(ccUserCup.getCupIndex());
                    }
                }
            }
            userListDialog.updateData(listener.getUserList());
        }
    }
    /**
     * 设置视频控制器按钮显示
     *
     * @param isShow isShow
     */
    public void setVideoControllerShown(boolean isShow) {
        video_controller.setVisibility(isShow?VISIBLE:GONE);
    }

    public boolean isVideoControllerShow(){
        return video_controller!=null&&video_controller.getVisibility()==VISIBLE;
    }

    /**
     * 设置视频跟随按钮显示
     *
     * @param isShow isShow
     */
    public void setVideoFollowShow(boolean isShow) {
        teacher_follow.setVisibility(isShow?VISIBLE:GONE);
    }

    public void setFollowEnable(boolean canFollow){
        isVideoFollow = canFollow;
        teacher_follow.setBackgroundResource(canFollow?R.mipmap.follow_on:R.drawable.follow_selector);
    }

    /**
     * 菜单监听
     *
     * @author wy
     */
    public interface MenuTopListener {

        ArrayList<CCUser> getUserList();

        void closeRoom();

        void videoController(boolean isVideoShow);

        void videoFollow();

        void onClickUser(CCUser user, int position);
    }


}
