/*
 * Copyright 2015 Diogo Bernardino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.chart.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.view.animation.DecelerateInterpolator;

import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;
import com.db.chart.view.ChartView;

import java.util.ArrayList;


/**
 * Controls the whole animation process.
 */
public class Animation {

    private static final String TAG = "animation.Animation";

    /**
     * Default animation duration
     */
    private final static int DEFAULT_DURATION = 1000;

    /**
     * Task that handles with animation updates
     */
    private Runnable mEndAction;

    /**
     * Animation global duration
     */
    private long mDuration;

    /**
     * Controls interpolation of the animation
     */
    private TimeInterpolator mInterpolator;

    private ArrayList<ChartSet> mData;

    /**
     * Factor from 0 to 1 to specifying where animation starts according innerchart area
     */
    private float mStartXFactor;

    private float mStartYFactor;

    /**
     * Alpha to include in animation
     */
    private int mAlpha;

    /**
     * Color from which entries should animate from
     */
    private int mColor;

    /**
     * Animation order
     */
    private int[] mOrder;

    /**
     * Flag if animation is entering
     */
    private boolean mIsEntering;

    /**
     * Chart callback to call in every chart data update
     */
    private ChartAnimationListener mCallback;

    /**
     * Overlap factor between entries while animating in sequence
     */
    private float mAnimateOverlapFactor;

    /**
     * List of {@link ValueAnimator} objects for a given on going animation
     */
    private ArrayList<ValueAnimator> mAnimators;

    /**
     * Animation listener set to executed animation end action
     */
    private final AnimatorListener mAnimatorListener = new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            cancel();
            mAnimators.clear();
            if (mEndAction != null) mEndAction.run();
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };


    public Animation() {

        init(DEFAULT_DURATION);
    }


    public Animation(int duration) {

        init(duration);
    }


    private void init(int duration) {

        mAnimators = new ArrayList<>();
        mDuration = duration;
        mAlpha = 1;
        mColor = -1;
        mInterpolator = new DecelerateInterpolator();
        mStartXFactor = -1f;
        mStartYFactor = -1f;
        mIsEntering = true;
        mAnimateOverlapFactor = 1;
    }


    /**
     * Method that prepares the animation. Defines starting points, targets,
     * distance, yadda, as well as the first set of points to be drawn.
     *
     * @param chartView {@link ChartView}
     */
    private ArrayList<ChartSet> prepareAnimation(ChartView chartView) {

        mData = chartView.getData();

        int nSets = mData.size();
        int nEntries = mData.get(0).size();

        ArrayList<float[][]> startValues = new ArrayList<>(nSets);
        ArrayList<float[][]> endValues = new ArrayList<>(nSets);
        float[][] startCoords;
        float[][] endCoords;
        for (int i = 0; i < nSets; i++) {

            startCoords = new float[nEntries][2];
            endCoords = mData.get(i).getScreenPoints();
            for (int j = 0; j < nEntries; j++) {

                startCoords[j][0] = chartView.getOrientation() == ChartView.Orientation.VERTICAL ?
                        mData.get(i).getEntry(j).getX() : chartView.getZeroPosition();

                startCoords[j][1] = chartView.getOrientation() == ChartView.Orientation.HORIZONTAL ?
                        mData.get(i).getEntry(j).getY() : chartView.getZeroPosition();
            }

            startValues.add(startCoords);
            endValues.add(endCoords);
        }

        startValues = applyStartingPosition(startValues,
                new Rect((int) chartView.getInnerChartLeft(), (int) chartView.getInnerChartTop(),
                        (int) chartView.getInnerChartRight(), (int) chartView.getInnerChartBottom()),
                mStartXFactor, mStartYFactor);

        if (mIsEntering) return animate(startValues, endValues);
        else return animate(endValues, startValues);

    }


    /**
     * Method that prepares the enter animation. Defines starting points, targets,
     * distance, yadda, as well as the first set of points to be drawn.
     *
     * @param chartView {@link ChartView}
     * @return Initial chart data state before starting animation
     */
    public ArrayList<ChartSet> prepareEnterAnimation(ChartView chartView) {

        mIsEntering = true;
        return prepareAnimation(chartView);
    }


    /**
     * Method that prepares the enter animation. Defines starting points, targets,
     * distance, yadda, as well as the first set of points to be drawn.
     * <p>
     * values and to get the {@link ChartSet} containing the target values
     *
     * @return Initial chart data state before starting animation
     */
    public ArrayList<ChartSet> prepareUpdateAnimation(ArrayList<float[][]> start,
                                                      ArrayList<float[][]> end) {

        return animate(start, end);
    }


    /**
     * Method that prepares the enter animation. Defines starting points, targets,
     * distance, yadda, as well as the first set of points to be drawn.
     *
     * @param chartView {@link ChartView} to be invalidate each time the animation wants to
     *                  update values and to get the {@link ChartSet} containing the target values
     * @return Initial chart data state before starting animation
     */
    public ArrayList<ChartSet> prepareExitAnimation(ChartView chartView) {

        mIsEntering = false;
        return prepareAnimation(chartView);
    }


    /**
     * Set a specific starting position for the animation.
     *
     * @param values       Values containing current start position
     * @param area         Chart's inner area
     * @param xStartFactor Factor from 0 to 1 specifying the X chart coordinate where animation
     *                     should start
     * @param yStartFactor Factor from 0 to 1 specifying the Y chart coordinate where animation
     *                     should start
     * @return Given values modified with new starting position.
     */
    ArrayList<float[][]> applyStartingPosition(ArrayList<float[][]> values, Rect area,
                                                         float xStartFactor, float yStartFactor) {

        for (int i = 0; i < values.size(); i++) {
            for (int j = 0; j < values.get(i).length; j++) {
                if (xStartFactor != -1)
                    values.get(i)[j][0] = area.left + (area.right - area.left) * xStartFactor;
                if (yStartFactor != -1)
                    values.get(i)[j][1] = area.bottom - (area.bottom - area.top) * yStartFactor;
            }
        }
        return values;
    }


    /**
     * Method that prepares the animation. Defines starting points, targets,
     * distance, yadda, as well as the first set of points to be drawn.
     *
     * @param start X and Y start coordinates
     * @param end   X and Y end coordinates
     * @return Array of {@link ChartSet} containing the first values to be drawn.
     */
    private ArrayList<ChartSet> animate(ArrayList<float[][]> start, ArrayList<float[][]> end) {

        ValueAnimator animator;
        animator = ValueAnimator.ofInt(0, 1); // Fuehrer
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCallback.onAnimationUpdate(mData);
            }
        });
        animator.addListener(mAnimatorListener);
        animator.setDuration(mDuration);
        mAnimators.add(animator);

        mAnimators.addAll(animateEntries(start, end));

        for (ChartSet set : mData){ // Animate alpha
            animator = set.animateAlpha(mAlpha, set.getAlpha());
            animator.setDuration(mDuration);
            animator.setInterpolator(mInterpolator);
            mAnimators.add(animator);
        }

        if (mColor != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // Animate color
            for (ChartSet set : mData)
                for (ChartEntry entry : set.getEntries()) {
                    animator = entry.animateColor(mColor, entry.getColor());
                    animator.setDuration(mDuration);
                    animator.setInterpolator(mInterpolator);
                    mAnimators.add(animator);
                }

        for (ValueAnimator e : mAnimators)
            e.start();

        return mData;
    }


    /**
     * Animate entries in sequence. In this case a {@link ValueAnimator} will be created for each chart entry.
     *
     * @param start Animation start values
     * @param end   Animation end values
     * @return List of animators to animate each entry.
     */
    private ArrayList<ValueAnimator> animateEntries(ArrayList<float[][]> start, ArrayList<float[][]> end) {

        final int nSets = start.size();
        final int nEntries = start.get(0).length;

        ArrayList<ValueAnimator> result = new ArrayList<>(nSets * nEntries);

        long duration = calculateEntriesDuration(nEntries, mDuration, mAnimateOverlapFactor);
        long[] delays = calculateEntriesInitTime(nEntries, mDuration, mAnimateOverlapFactor, mOrder);
        ValueAnimator animator;
        for (int i = 0; i < nSets; i++)
            for (int j = 0; j < nEntries; j++) {

                animator = mData.get(i).getEntry(j).animateXY(start.get(i)[j][0], start.get(i)[j][1],
                        end.get(i)[j][0], end.get(i)[j][1]);
                animator.setStartDelay(delays[j]);
                animator.setDuration(duration);
                animator.setInterpolator(mInterpolator);
                result.add(animator);
            }
        return result;
    }


    /**
     * Define the delay for each entry.
     *
     * @param size          Number of entries
     * @param duration      Complete animation duration
     * @param overlapFactor Overlap factor between entries
     * @return Array containing the animation delays for each entry.
     */
    long[] calculateEntriesInitTime(int size, long duration, float overlapFactor, int[] order){

        if (overlapFactor != 1)
            duration = (long) (duration + duration * overlapFactor);

        if (order == null) {
            order = new int[size];
            for (int i = 0; i < size; i++)
                order[i] = i;
        }

        long[] result = new long[size];
        long noOverlapInitTime;
        for (int i = 0; i < size; i++) {
            // Calculates the expected init time as there was with no overlap (factor = 0)
            noOverlapInitTime = i * (duration / size);
            // Adjust the init time to overlap
            result[order[i]] = (noOverlapInitTime - ((long) (overlapFactor * noOverlapInitTime)));
        }
        return result;
    }


    /**
     *  Calculate each individual entry duration.
     *
     * @param size          Number of entries
     * @param duration      Complete animation duration
     * @param overlapFactor Overlap factor between entries
     * @return Array containing the animation duration for each entry.
     */
    long calculateEntriesDuration(int size, long duration, float overlapFactor){

        // Calculates the expected duration as there was with no overlap (factor = 0)
        final float noOverlapDuration = duration / size;
        // Adjust the duration to the overlap
        return (long) (noOverlapDuration + (duration - noOverlapDuration) * overlapFactor);
    }


    /**
     * Information on animation is still on going on not.
     *
     * @return True if animation is running, False otherwise.
     */
    public boolean isPlaying() {

        for (ValueAnimator animator : mAnimators)
            if (animator.isRunning())
                return true;
        return false;
    }


    /**
     * Cancel every running animator.
     */
    public void cancel() {

        for (ValueAnimator animator : mAnimators)
            animator.cancel();
    }


    /**
     * Get current action to be executed once animation finishes.
     *
     * @return Current action to be executed once animation finishes
     */
    public Runnable getEndAction() {

        return mEndAction;
    }


    /**
     * Animation easing will be managed differently depending on which {@link TimeInterpolator}
     * object is defined.
     *
     * @param interpolator {@link TimeInterpolator} object responsible to interpolate animation
     *                     values
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setInterpolator(TimeInterpolator interpolator) {

        mInterpolator = interpolator;
        return this;
    }


    /**
     * Set animation duration.
     *
     * @param duration Animation duration
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setDuration(int duration) {

        mDuration = duration;
        return this;
    }


    /**
     * Entries will animate sequentially instead of all at the same time,
     * but also in a specific order.
     *
     * @param order  Sequentiality order
     * @param factor In case animation should show an overlap between entries
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setInSequence(float factor, int[] order) {

        mOrder = order;
        setInSequence(factor);
        return this;
    }


    /**
     * Entries will animate sequentially instead of all at the same time.
     *
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setInSequence(float factor) {

        mAnimateOverlapFactor = factor;
        return this;
    }


    /**
     * Sets an action to be executed once the animation finishes.
     *
     * @param endAction to be executed once the animation finishes
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setEndAction(Runnable endAction) {

        mEndAction = endAction;
        return this;
    }


    /**
     * Sets the starting point for the animation.
     * Eg. xFactor=0; yFactor=0; starts the animation on the bottom left
     * corner of the inner chart area.
     *
     * @param xFactor horizontal factor between 0 and 1. If not applied then -1 can be set.
     * @param yFactor vertical factor between 0 and 1. If not applied then -1 can be set.
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setStartPoint(@FloatRange(from = -1.f, to = 1.f) float xFactor,
                                   @FloatRange(from = -1.f, to = 1.f) float yFactor) {

        mStartXFactor = xFactor;
        mStartYFactor = yFactor;
        return this;
    }


    /**
     * Sets an alpha value to animate from. To disable set it to -1.
     *
     * @param alpha alpha value from where chart will animate from.
     * @return {@link com.db.chart.animation.Animation} self-reference.
     */
    public Animation setAlpha(int alpha) {

        mAlpha = alpha;
        return this;
    }


    /**
     * Callback to use in every chart data update.
     *
     * @param callback Callback to be called in every data update
     */
    public Animation setAnimationListener(ChartAnimationListener callback) {

        mCallback = callback;
        return this;
    }


    /**
     * Animate from a specific color until the already set entry's color.
     *
     * @param color Color to animate from
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Animation fromColor(int color) {

        mColor = color;
        return this;
    }

}
