package com.db.chart.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.listener.OnTooltipEventListener;


/**
 * Class representing chart's tooltips. It works basically as a wrapper.
 */
public class Tooltip extends RelativeLayout{

    private TextView mTooltipValue;

    private OnTooltipEventListener mTooltipEventListener;

    private ObjectAnimator mEnterAnimator;
    private ObjectAnimator mExitAnimator;

    private boolean mOn;


    public Tooltip(Context context, int layoutId) {
        super(context);
        init(layoutId);
    }

    public Tooltip(Context context, int layoutId, int valueId) {
        super(context);
        init(layoutId);
        mTooltipValue = (TextView) findViewById(valueId);
    }


    private void init(int layoutId){
        addView(inflate(getContext(), layoutId, null));
        mOn = false;
    }



    /**
     *
     * @param listener
     */
    public void setEventListener(OnTooltipEventListener listener){
        mTooltipEventListener = listener;
    }



    /**
     *
     * @param rect
     * @param value
     */
    public void prepare(Rect rect, float value){

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(rect.width(), rect.height());
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        setLayoutParams(layoutParams);

        if (mTooltipValue != null)
            mTooltipValue.setText(Float.toString(value));
    }



    /**
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void correctPosition(int left, int top, int right, int bottom){

        final LayoutParams layoutParams = (LayoutParams) getLayoutParams();

        if (layoutParams.leftMargin < left)
            layoutParams.leftMargin = left;
        if (layoutParams.topMargin < top)
            layoutParams.topMargin = top;
        if (layoutParams.leftMargin + layoutParams.width > right)
            layoutParams.leftMargin -= layoutParams.width - (right - layoutParams.leftMargin);
        if (layoutParams.topMargin + layoutParams.height > bottom)
            layoutParams.topMargin -= layoutParams.height - (bottom - layoutParams.topMargin);
        setLayoutParams(layoutParams);
    }



    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void animateEnter(){
        mEnterAnimator.start();
    }

    /**
     *
     * @param endAction
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void animateExit(final Runnable endAction){

        mExitAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                endAction.run();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        mExitAnimator.start();
    }


    /**
     *
     * @return
     */
    protected boolean hasEnterAnimation(){
        if(mEnterAnimator != null)
            return true;
        return false;
    }

    /**
     *
     * @return
     */
    protected boolean hasExitAnimation(){
        if(mExitAnimator != null)
            return true;
        return false;
    }

    /**
     *
     * @return
     */
    protected boolean on(){
        return mOn;
    }


    /**
     *
     * @param on
     */
    protected void setOn(boolean on){
        mOn = on;
    }

    /**
     *
     * @param values
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ObjectAnimator setEnterAnimation(PropertyValuesHolder... values){

        for(PropertyValuesHolder value: values){

            if(value.getPropertyName() == "alpha")
                setAlpha(0);
            if(value.getPropertyName() == "rotation")
                setRotation(0);
            if(value.getPropertyName() == "rotationX")
                setRotationX(0);
            if(value.getPropertyName() == "rotationY")
                setRotationY(0);
            if(value.getPropertyName() == "translationX")
                setTranslationX(0);
            if(value.getPropertyName() == "translationY")
                setTranslationY(0);
            if(value.getPropertyName() == "scaleX")
                setScaleX(0);
            if(value.getPropertyName() == "scaleY")
                setScaleY(0);
        }
        return mEnterAnimator =  ObjectAnimator.ofPropertyValuesHolder(this, values);
    }

    /**
     *
     * @param values
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ObjectAnimator setExitAnimation(PropertyValuesHolder... values){
        return mExitAnimator = ObjectAnimator.ofPropertyValuesHolder(this, values);
    }

}
