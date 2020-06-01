package com.bokecc.room.ui.view.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bokecc.ccdocview.DensityUtil;
import com.bokecc.room.ui.R;
import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur;

import eightbitlab.com.blurview.BlurView;

/**
 * @author swh
 * @Description 老师页面查看更多菜单
 */
public class MenuMoreTeacherView extends RelativeLayout implements View.OnClickListener {

    private ImageButton mBlurSetting,mBlurDoc,mBlurUpdateImg,mBlurNamed;
    private Activity activity;
    private BlurView mBlurView;
    /**
     * 0 没显示，1 正在显示或者隐藏 2 已经显示完成
     */
    private int showStatus;
    private Animation mBlurOutAnim;
    private Animation mBlurInAnim;
    private Activity mContext;
    private ImageButton[] imageButtons;
    private MenuMoreListener mListener;

    public MenuMoreTeacherView(Context context) {
        super(context);
        initView(context);
    }

    public MenuMoreTeacherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MenuMoreTeacherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }
    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.menu_more_teacher_layout, this, true);
        mBlurView = findViewById(R.id.id_teacher_blur_view);
        mBlurDoc = findViewById(R.id.id_teacher_blur_doc);
        mBlurDoc.setOnClickListener(this);
        mBlurUpdateImg = findViewById(R.id.id_teacher_blur_update_img);
        mBlurUpdateImg.setOnClickListener(this);
        mBlurNamed = findViewById(R.id.id_teacher_blur_named);
        mBlurNamed.setOnClickListener(this);
        mBlurSetting = findViewById(R.id.id_teacher_blur_setting);
        mBlurSetting.setOnClickListener(this);
        findViewById(R.id.id_teacher_blur_clickable).setOnClickListener(this);


        initBlurAnim(context);
    }
    private void initBlurAnim(Context context) {
        mBlurInAnim = AnimationUtils.loadAnimation(context, R.anim.blur_in);
        mBlurOutAnim = AnimationUtils.loadAnimation(context, R.anim.blur_out);
    }
    @Override
    public void onClick(View v) {
        if(showStatus==1)return;
        int id = v.getId();
        dismissBlurAtOnce();
        if(id == R.id.id_teacher_blur_setting){
            if(mListener!=null)
                mListener.clickSetting();

        }else if(id ==R.id.id_teacher_blur_doc){
            if(mListener!=null)
                mListener.clickDoc();
        }else if(id ==R.id.id_teacher_blur_update_img){
            if(mListener!=null)
                mListener.updateImage();
        }else if(id ==R.id.id_teacher_blur_named){
            if(mListener!=null)
                mListener.rollCall();
        }/*else if(id ==R.id.id_teacher_blur_clickable){
            childExitAnim();
        }*/


    }

    public boolean isShow(){
        return showStatus!=0;
    }

    public void show() {
        if(showStatus==1)return;
        showStatus=1;
        setVisibility(VISIBLE);
        childEnterAnim();
    }
    public void dismiss(){
        dismissBlurAtOnce();
    }

    private void dismissBlurWithAnim() {

        mBlurOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                setVisibility(GONE);
                showStatus = 0;
                mBlurView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBlurView.startAnimation(mBlurOutAnim);
    }
   /* private void childExitAnim() {

        for (int i = imageButtons.length - 1; i >= 0; i--) {
            final int finalI = i;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation translateAnim = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_PARENT, 1f
                    );
                    translateAnim.setDuration(150);
                    if(finalI==0)
                    translateAnim.setAnimationListener(new MyAnimListener());
                    imageButtons[finalI].setAnimation(translateAnim);
                    imageButtons[finalI].startAnimation(translateAnim);
                }
            }, (imageButtons.length - 1 - i) * 50);
        }
    }*/


    private void childEnterAnim() {

        for (int i = 0; i < imageButtons.length; i++) {
            final int finalI = i;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation translateAnim = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_SELF, 0f
                    );
                    translateAnim.setDuration(200);

                    mBlurView.setVisibility(View.VISIBLE);
                    mBlurView.startAnimation(mBlurInAnim);
                    ImageButton imageButton = imageButtons[finalI];
                    imageButton.setVisibility(View.VISIBLE);
                    imageButton.setAnimation(translateAnim);
                    if(finalI==imageButtons.length-1){
                        translateAnim.setAnimationListener(new EnterAnimListener());
                    }
                    imageButton.startAnimation(translateAnim);

                }
            }, 200 + i * 50);
        }
    }

    private void setTopMargin(ImageButton item, int value) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item.getLayoutParams();
        params.topMargin = DensityUtil.dp2px(mContext, value);
        item.setLayoutParams(params);
    }

    private void setStartMargin(ImageButton item, int value) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item.getLayoutParams();
        params.leftMargin = DensityUtil.dp2px(mContext, value);
        item.setLayoutParams(params);
    }

    private void setEndMargin(ImageButton item, int value) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item.getLayoutParams();
        params.rightMargin = DensityUtil.dp2px(mContext, value);
        item.setLayoutParams(params);
    }

    public void initData(Activity activity, boolean isMoreItemHasDoc, int sClassDirection,MenuMoreListener listener) {
        this.mContext = activity;
        this.mListener = listener;
        if (isMoreItemHasDoc) {
            if (sClassDirection == 0) {
                setTopMargin(mBlurDoc, 120);
                setTopMargin(mBlurUpdateImg, 120);
                setTopMargin(mBlurNamed, 250);
                setTopMargin(mBlurSetting, 250);
                setStartMargin(mBlurDoc, 60);
                setEndMargin(mBlurUpdateImg, 60);
                setStartMargin(mBlurNamed, 60);
                setEndMargin(mBlurSetting, 60);
            } else {
                setTopMargin(mBlurDoc, 10);
                setTopMargin(mBlurUpdateImg, 10);
                setTopMargin(mBlurNamed, 125);
                setTopMargin(mBlurSetting, 125);
                setStartMargin(mBlurDoc, 160);
                setEndMargin(mBlurUpdateImg, 160);
                setStartMargin(mBlurNamed, 160);
                setEndMargin(mBlurSetting, 160);
            }
        } else {
            mBlurDoc.setVisibility(View.GONE);
            mBlurUpdateImg.setVisibility(View.GONE);
            if (sClassDirection == 0) {
                setTopMargin(mBlurNamed, 120);
                setTopMargin(mBlurSetting, 120);
                setStartMargin(mBlurNamed, 60);
                setEndMargin(mBlurSetting, 60);
            } else {
                setTopMargin(mBlurNamed, 60);
                setTopMargin(mBlurSetting, 60);
                setStartMargin(mBlurNamed, 160);
                setEndMargin(mBlurSetting, 160);
            }
        }
        final float radius = 20;
        final View decorView = mContext.getWindow().getDecorView();
        //Activity's root View. Can also be root View of your layout (preferably)
        final ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //set background, if your root layout doesn't have one
        final Drawable windowBackground = decorView.getBackground();
        mBlurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new SupportRenderScriptBlur(mContext))
                .blurRadius(radius);

        if (isMoreItemHasDoc) {
            imageButtons = new ImageButton[]{
                    mBlurDoc, mBlurUpdateImg, mBlurNamed, mBlurSetting};
        } else {
            imageButtons = new ImageButton[]{
                    mBlurNamed, mBlurSetting};
        }

    }

   /* private class MyAnimListener implements
            Animation.AnimationListener {


        MyAnimListener() {
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dismissBlurWithAnim();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }*/
    protected void go(Class clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivityForResult(intent, requestCode);
    }
    private void dismissBlurAtOnce() {
        setVisibility(GONE);
        mBlurView.setVisibility(View.GONE);
        for (ImageButton view :
                imageButtons) {
            view.setVisibility(View.GONE);
        }
        showStatus = 0;
    }


    /**
     * 菜单监听
     */
    public interface MenuMoreListener {

        void rollCall();

        void updateImage();

        void clickDoc();

        void clickSetting();

    }

    private class EnterAnimListener extends Animation implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            showStatus = 2;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
