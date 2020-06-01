package com.bokecc.room.ui.view.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.room.ui.R;

/**
 * @author swh
 * @Description java类作用
 */
public class CupView extends LinearLayout {

    /**奖杯视图*/
    private TextView id_student_reward_text;
    /**声明一个SoundPool*/
    private SoundPool spPool;
    /**声明一个变量 || 可以理解成用来储存歌曲的变量*/
    private int music;
    private Context mContext;

    public CupView(Context context) {
        super(context);
        init(context);
    }

    public CupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_cup_view_layout, this,true);
        id_student_reward_text = findViewById(R.id.id_student_reward_text);
        if(spPool==null){
            //第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            spPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
            //所要加载的music文件 ,(第2个参数即为资源文件，第3个为音乐的优先级), 其中raw是res文件夹里的 ,较低版本的android可能没有,需要手动创建,并在'R'文件中声明
            music = spPool.load(mContext, R.raw.cup_audio, 1);

        }
    }
    /**
     * 开启奖杯动画
     */
    public void startRewardAnim(String username) {
        setVisibility(VISIBLE);

        //开启音频,(对音频文件播放的设置 例如左右声道等)
        spPool.play(music, 1, 1, 0, 0, 1);

        if(username != null && username.length() > 0){
            id_student_reward_text.setText("恭喜“"+username+"”获得一个大大的奖励！");
        }else{
            id_student_reward_text.setText("恭喜你获得一个大大的奖励！");
        }

        Animation loadAnimation = AnimationUtils.loadAnimation(mContext, R.anim.animation_show_alpha);
        loadAnimation.setFillAfter(true);
        startAnimation(loadAnimation);

        Animation hideAnimation = AnimationUtils.loadAnimation(mContext, R.anim.animation_hide_alpha);
        hideAnimation.setFillAfter(true);
        startAnimation(hideAnimation);

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
