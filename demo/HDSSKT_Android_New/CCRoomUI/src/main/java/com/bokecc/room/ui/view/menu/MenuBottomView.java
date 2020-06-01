package com.bokecc.room.ui.view.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bokecc.ccdocview.SPUtil;
import com.bokecc.common.dialog.BottomMenuDialog;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.view.activity.SettingActivity;
import com.bokecc.sskt.base.CCAtlasClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天控件
 * @author wy
 */
public class MenuBottomView extends RelativeLayout {

    private final String TAG = "MenuBottomView";

    /***/
    private Activity activity;
    /**监听*/
    private MenuBottomListener listener;
    /***/
    private Button menu_bottom_chat,menu_bottom_lianmai,menu_bottom_setting,menu_bottom_handup;
    private View common_operator_area;


    public MenuBottomView(Context context) {
        super(context);
        initView(context);
    }

    public MenuBottomView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView(context);
    }

    public MenuBottomView(Context context, AttributeSet attrs, int deStyleAttr){
        super(context,attrs,deStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_menu_bottom_layout,this,true);

        common_operator_area = findViewById(R.id.common_operator_area);
        menu_bottom_chat = findViewById(R.id.menu_bottom_chat);
        menu_bottom_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuOpenChat();
            }
        });
        menu_bottom_lianmai = findViewById(R.id.menu_bottom_lianmai);
        menu_bottom_lianmai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.menuLianmai();
            }
        });
        menu_bottom_setting = findViewById(R.id.menu_bottom_setting);
        menu_bottom_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                changeResolution();
                activity.startActivity(new Intent(activity,SettingActivity.class));
            }
        });

        menu_bottom_handup = findViewById(R.id.menu_bottom_handup);
        menu_bottom_handup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.handup();
            }
        });

        initAnimation(context);
    }

    public void setOperatorAreaShow(boolean isShow){
        common_operator_area.setVisibility(isShow?VISIBLE:GONE);
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
    /**
     * 设置监听
     * @param listener
     */
    public void setListener(Activity activity, MenuBottomListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    /**分辨率key*/
//    public static final String STUDENT_RESOLUTION = "student_resolution";

//    private OptionsPickerView mPickerView;

    /**
     * 修改分辨率
     */
    /*private void changeResolution(){
        List<String> menuText = new ArrayList<>();

        final int[] resolutionValues = CCAtlasClient.getInstance().getResolution();
        for(int i=0;i<resolutionValues.length;i++){
            int resolution = resolutionValues[i];
            if(resolution == CCAtlasClient.Resolution_720P){
                menuText.add(Tools.getString(R.string.setting_bitrate_720p));
            }else if(resolution == CCAtlasClient.Resolution_480P){
                menuText.add(Tools.getString(R.string.setting_bitrate_480p));
            }else if(resolution == CCAtlasClient.Resolution_240P){
                menuText.add(Tools.getString(R.string.setting_bitrate_240p));
            }
        }

//        BottomMenuDialog dialog = new BottomMenuDialog(activity,menuText,true);
//        dialog.setOnMenuItemClickListener(new BottomMenuDialog.OnMenuItemClickListener() {
//            @Override
//            public void onClick(String text, int index) {
//                SPUtil.getIntsance().put(STUDENT_RESOLUTION, resolutionValues[index]);
//            }
//        });


        mPickerView = new OptionsPickerView.Builder(activity, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                SPUtil.getIntsance().put(STUDENT_RESOLUTION, resolutionValues[options1]);
//                if (isTeacher) {
//                    SPUtil.getIntsance().put(TEACHER_RESOLUTION, resolutionValues[options1]);
//                    mViewHolder.mTBitrate.setValue(bitOptions.get(options1));
//                    CCAtlasClient.getInstance().setResolution(SPUtil.getIntsance().getInt(TEACHER_RESOLUTION,CCAtlasClient.getInstance().getDefaultResolution()));
//                    isChangeResolution = true;
////                        commitTeacherbit(bitValues[options1]);
//                } else {
//                    commitStudentbit(bitValues[options1]);
//                }
            }
        })
                .setTitleText(Tools.getString(R.string.setting_bitrate))
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(getResources().getColor(R.color.colorPrimary))//设置分割线的颜色
                .setBgColor(Color.BLACK)
                .setTitleBgColor(Color.DKGRAY)
                .setTitleColor(Color.LTGRAY)
                .setCancelColor(getResources().getColor(R.color.colorPrimary))
                .setSubmitColor(getResources().getColor(R.color.colorPrimary))
                .setTextColorCenter(Color.LTGRAY)
                .setBackgroundId(0x66000000) //设置外部遮罩颜色
                .build();
        mPickerView.setPicker(menuText);

        //处理越界数据
        int relolution = SPUtil.getIntsance().getInt(STUDENT_RESOLUTION, CCAtlasClient.getInstance().getDefaultResolution());
        int index = 0;
        for(int i=0;i< resolutionValues.length;i++){
            if(relolution == resolutionValues[i]){
                index = i;
                break;
            }
        }
        //避免越界
        if(index > resolutionValues.length){
            index = resolutionValues.length -1;
        }

        mPickerView.setSelectOptions(index);
        mPickerView.show();

    }*/

    /**
     * 设置设置按钮状态
     */
    public void setSettingBtnVisibility(int visibility){
        menu_bottom_setting.setVisibility(visibility);
    }

    /**
     * 设置聊天按钮是否禁言
     * @param isMute
     */
    public void setChatBtnMute(boolean isMute){
        if (isMute) {
            menu_bottom_chat.setBackgroundResource(R.drawable.student_mute_selector);
        } else {
            menu_bottom_chat.setBackgroundResource(R.drawable.student_chat_selector);
        }
    }

    /**
     * 设置是否可以显示
     * @param visibility
     */
    public void setLianmaiBtnVisibility(int visibility){
        menu_bottom_lianmai.setVisibility(visibility);
    }

    /**
     * 设置连麦按钮状态
     * 0 连麦
     * 1 连麦中
     * 2 以连麦
     * 3 举手
     * 4 举手取消
     */
    public void setLianmaiStatus(int status){
        switch(status){
            case 0:
                menu_bottom_lianmai.setBackgroundResource(R.drawable.queue_mai_selector);
                break;
            case 1:
                menu_bottom_lianmai.setBackgroundResource(R.drawable.queuing_selector);
                break;
            case 2:
                menu_bottom_lianmai.setBackgroundResource(R.drawable.maiing_selector);
                break;
            case 3:
                menu_bottom_lianmai.setBackgroundResource(R.drawable.handup_selector);
                break;
            case 4:
                menu_bottom_lianmai.setBackgroundResource(R.drawable.handup_cancel_selector);
                break;
        }
    }

    /**
     * 设置连麦文字
     * @param text
     */
    public void setLianmaiText(String text){
        menu_bottom_lianmai.setText(text);
    }


    /**
     * 设置是否可以显示
     * @param visibility
     */
    public void setHandupBtnVisibility(int visibility){
        menu_bottom_handup.setVisibility(visibility);
    }

    /**
     * 设置举手
     * @param isHandup
     */
    public void setHandupBtn(boolean isHandup){
        if (isHandup) {
            menu_bottom_handup.setBackgroundResource(R.drawable.handup_cancel_selector);
        } else {
            menu_bottom_handup.setBackgroundResource(R.drawable.handup_selector);
        }
    }

    /**
     * 菜单监听
     * @author wy
     */
    public interface MenuBottomListener {

        void menuOpenChat();

        void menuLianmai();

        void handup();

    }


}
