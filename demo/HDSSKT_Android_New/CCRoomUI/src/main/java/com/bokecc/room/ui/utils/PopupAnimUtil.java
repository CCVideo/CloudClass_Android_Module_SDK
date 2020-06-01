package com.bokecc.room.ui.utils;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 作者 ${CC视频}.<br/>
 */

public class PopupAnimUtil {
    private static final long ANIM_DURATION = 100L;
    private PopupAnimUtil() {
        throw new UnsupportedOperationException();
    }

    public static Animation getDefScaleEnterAnim() {
        Animation scaleAnim =
                new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(ANIM_DURATION);
        scaleAnim.setInterpolator(new AccelerateInterpolator());
        scaleAnim.setFillEnabled(true);
        scaleAnim.setFillAfter(true);
        return scaleAnim;
    }

    public static Animation getDefScaleExitAnim() {
        Animation scaleAnim =
                new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(ANIM_DURATION);
        scaleAnim.setInterpolator(new AccelerateInterpolator());
        scaleAnim.setFillEnabled(true);
        scaleAnim.setFillAfter(true);
        return scaleAnim;
    }

    public static Animation getRightScaleEnterAnim() {
        Animation scaleAnim =
                new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f);
        scaleAnim.setDuration(ANIM_DURATION);
        scaleAnim.setInterpolator(new AccelerateInterpolator());
        scaleAnim.setFillEnabled(true);
        scaleAnim.setFillAfter(true);
        return scaleAnim;
    }

    public static Animation getRightScaleExitAnim() {
        Animation scaleAnim =
                new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f);
        scaleAnim.setDuration(ANIM_DURATION);
        scaleAnim.setInterpolator(new AccelerateInterpolator());
        scaleAnim.setFillEnabled(true);
        scaleAnim.setFillAfter(true);
        return scaleAnim;
    }

    public static Animation getDefTranslateEnterAnim() {
        Animation translateAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnim.setDuration(ANIM_DURATION);
        translateAnim.setInterpolator(new AccelerateInterpolator());
        translateAnim.setFillEnabled(true);
        translateAnim.setFillAfter(true);
        return translateAnim;
    }

    public static Animation getDefTranslateExitAnim() {
        Animation translateAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
        );
        translateAnim.setDuration(ANIM_DURATION);
        translateAnim.setInterpolator(new AccelerateInterpolator());
        translateAnim.setFillEnabled(true);
        translateAnim.setFillAfter(true);
        return translateAnim;
    }

    public static Animation getDefAlphaEnterAnim() {
        Animation alphaAnim = new AlphaAnimation(0f, 1f);
        alphaAnim.setDuration(ANIM_DURATION);
        alphaAnim.setInterpolator(new AccelerateInterpolator());
        alphaAnim.setFillEnabled(true);
        alphaAnim.setFillAfter(true);
        return alphaAnim;
    }

    public static Animation getDefAlphaExitAnim() {
        Animation alphaAnim = new AlphaAnimation(1f, 0f);
        alphaAnim.setDuration(ANIM_DURATION);
        alphaAnim.setInterpolator(new AccelerateInterpolator());
        alphaAnim.setFillEnabled(true);
        alphaAnim.setFillAfter(true);
        return alphaAnim;
    }

}
