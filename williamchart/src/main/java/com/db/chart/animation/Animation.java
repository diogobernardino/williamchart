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

import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.FloatRange;
import android.view.animation.DecelerateInterpolator;

import com.db.chart.model.ChartSet;
import com.db.chart.view.ChartView;

import java.util.ArrayList;


/**
 * Controls the whole animation process.
 */
public class Animation {

	private static final String TAG = "animation.Animation";


	/** The delay between data updates to be drawn in the screen */
	private final static long DELAY_BETWEEN_UPDATES = 20;


	/** Default animation duration */
	private final static int DEFAULT_DURATION = 1000;


	/** Default animation overlap */
	private final static float DEFAULT_OVERLAP_FACTOR = 1f;


	/** Default animation alpha */
	private final static int DEFAULT_ALPHA_OFF = -1;


	/** Task that handles with animation updates */
	private Runnable mRunnable;

	/** Maintains path measures to get position updates **/
	private PathMeasure[][] mPathMeasures;

	/** Keeps the global initial time of the animation */
	private long mInitTime;

	/** Animation global duration */
	private long mDuration;

	/** Keeps the current global duration */
	private long mCurrDuration;

	/** Keeps the initial time of the animation for each of the points */
	private long[] mEntryInitTime;

	/** Animation duration for each of the entries */
	private int mEntryDuration;
	
	/** Keeps the current duration of the animation in each of the entries */
	private long[] mCurrEntryDuration;

	/** Controls interpolation of the animation */
	private TimeInterpolator mInterpolator;

	/** {@link ChartView} element to request draw updates */
	private ChartView mChartView;

	/** Keeps information if animation is ongoing or not */
	private boolean mPlaying;

	/** Flags the cancellation of the on-going animation */
	private boolean mCancelled;

	/** Animation overlap between entries (from 0 to 1) */
	private float mOverlapingFactor;

	/** Factor from 0 to 1 to specifying where animation starts according innerchart area */
	private float mStartXFactor;

	private float mStartYFactor;

	/** Alpha speed to include in animation */
	private int mAlphaSpeed;

	/** Sets alpha value to be preserved */
	private float[] mSetsAlpha;

	/** Animation order */
	private int[] mOrder;

	/** Flag if animation is entering */
	private boolean mIsEntering;

	/** Control animation updates */
	final private Runnable mAnimator = new Runnable() {
		@Override
		public void run() {

			if (mChartView.canIPleaseAskYouToDraw()) {
				mChartView.addData(getUpdate(mChartView.getData()));
				mChartView.postInvalidate();
			}
		}
	};


	public Animation() {

		init(DEFAULT_DURATION);
	}


	public Animation(int duration) {

		init(duration);
	}


	private void init(int duration) {

		mDuration = duration;
		mOverlapingFactor = DEFAULT_OVERLAP_FACTOR;
		mAlphaSpeed = DEFAULT_ALPHA_OFF;
		mInterpolator = new DecelerateInterpolator();
		mStartXFactor = -1f;
		mStartYFactor = -1f;

		mPlaying = false;
		mIsEntering = true;
	}


	/**
	 * Method that prepares the animation. Defines starting points, targets,
	 * distance, yadda, as well as the first set of points to be drawn.
	 *
	 * @param chartView {@link ChartView} to be invalidated each time the
	 * animation wants to update values
	 * @param start X and Y start coordinates
	 * @param end X and Y end coordinates
	 *
	 * @return Array of {@link ChartSet} containing the first values to be drawn.
	 */
	private ArrayList<ChartSet> prepareAnimation(ChartView chartView, ArrayList<float[][]> start,
			  ArrayList<float[][]> end) {

		final int nSets = start.size();
		final int nEntries = start.get(0).length;

		mChartView = chartView;
		mCurrEntryDuration = new long[nEntries];

		// Set the animation order if not defined already
		if (mOrder == null) {
			mOrder = new int[nEntries];
			for (int i = 0; i < mOrder.length; i++)
				mOrder[i] = i;
		} else {
			// In case the animation order has been defined,
			// check if size equal than set's entries size
			if (mOrder.length != nEntries) throw new IllegalArgumentException(
					  "Size of overlap order different than set's entries size.");
		}

		// Define animation paths for each entry
		Path path;
		mPathMeasures = new PathMeasure[nSets][nEntries];
		for (int i = 0; i < nSets; i++) {
			for (int j = 0; j < nEntries; j++) {

				path = new Path();
				path.moveTo(start.get(i)[j][0], start.get(i)[j][1]);
				path.lineTo(end.get(i)[j][0], end.get(i)[j][1]);

				mPathMeasures[i][j] = new PathMeasure(path, false);
			}
		}

		// Calculates the expected duration as there was with no overlap (factor = 0)
		float noOverlapDuration = mDuration / nEntries;
		// Adjust the duration to the overlap
		mEntryDuration =
				  (int) (noOverlapDuration + (mDuration - noOverlapDuration) * mOverlapingFactor);

		// Define initial time for each entry
		mEntryInitTime = new long[nEntries];
		mInitTime = System.currentTimeMillis();
		long noOverlapInitTime;
		for (int i = 0; i < nEntries; i++) {
			// Calculates the expected init time as there was with no overlap (factor = 0)
			noOverlapInitTime = mInitTime + (i * (mDuration / nEntries));
			// Adjust the init time to overlap
			mEntryInitTime[mOrder[i]] = (noOverlapInitTime -
					  ((long) (mOverlapingFactor * (noOverlapInitTime - mInitTime))));
		}

		mPlaying = true;
		return getUpdate(mChartView.getData());
	}


	/**
	 * Method that prepares the animation. Defines starting points, targets,
	 * distance, yadda, as well as the first set of points to be drawn.
	 *
	 * @param chartView {@link ChartView} to be invalidate each time the
	 * animation wants to update values and to get the {@link ChartSet}
	 * containing the target values
	 */
	private ArrayList<ChartSet> prepareAnimation(ChartView chartView) {

		final ArrayList<ChartSet> sets = chartView.getData();

		float x;
		if (mStartXFactor != -1) x = chartView.getInnerChartLeft() +
				  (chartView.getInnerChartRight() - chartView.getInnerChartLeft()) * mStartXFactor;
		else x = chartView.getZeroPosition();

		float y;
		if (mStartYFactor != -1) y = chartView.getInnerChartBottom() -
				  (chartView.getInnerChartBottom() - chartView.getInnerChartTop()) * mStartYFactor;
		else y = chartView.getZeroPosition();

		final int nSets = sets.size();
		final int nEntries = sets.get(0).size();

		mSetsAlpha = new float[nSets];

		ArrayList<float[][]> startValues = new ArrayList<>(nSets);
		ArrayList<float[][]> endValues = new ArrayList<>(nSets);
		float[][] startSet;
		float[][] endSet;

		for (int i = 0; i < nSets; i++) {

			// Save set alpha value to be preserved
			mSetsAlpha[i] = sets.get(i).getAlpha();

			startSet = new float[nEntries][2];
			endSet = new float[nEntries][2];

			for (int j = 0; j < nEntries; j++) {

				if (mStartXFactor == -1 && chartView.getOrientation() == ChartView.Orientation.VERTICAL)
					startSet[j][0] = sets.get(i).getEntry(j).getX();
				else startSet[j][0] = x;

				if (mStartYFactor == -1 &&
						  chartView.getOrientation() == ChartView.Orientation.HORIZONTAL)
					startSet[j][1] = sets.get(i).getEntry(j).getY();
				else startSet[j][1] = y;

				endSet[j][0] = sets.get(i).getEntry(j).getX();
				endSet[j][1] = sets.get(i).getEntry(j).getY();
			}

			startValues.add(startSet);
			endValues.add(endSet);
		}

		if (mIsEntering)
			return prepareAnimation(chartView, startValues, endValues);
		else
			return prepareAnimation(chartView, endValues, startValues);

	}


	/**
	 * Method that prepares the enter animation. Defines starting points, targets,
	 * distance, yadda, as well as the first set of points to be drawn.
	 *
	 * @param chartView {@link ChartView} to be invalidate each time the animation wants to update
	 * values and to get the {@link ChartSet} containing the target values
	 *
	 * @return
	 */
	public ArrayList<ChartSet> prepareEnterAnimation(ChartView chartView) {

		mIsEntering = true;
		return prepareAnimation(chartView);
	}


	/**
	 * Method that prepares the enter animation. Defines starting points, targets,
	 * distance, yadda, as well as the first set of points to be drawn.
	 *
	 * @param chartView {@link ChartView} to be invalidate each time the animation wants to update
	 * values and to get the {@link ChartSet} containing the target values
	 *
	 * @return
	 */
	public ArrayList<ChartSet> prepareUpdateAnimation(ChartView chartView,
			  ArrayList<float[][]> start, ArrayList<float[][]> end) {

		return prepareAnimation(chartView, start, end);
	}


	/**
	 * Method that prepares the enter animation. Defines starting points, targets,
	 * distance, yadda, as well as the first set of points to be drawn.
	 *
	 * @param chartView {@link ChartView} to be invalidate each time the animation wants to
	 * update values and to get the {@link ChartSet} containing the target values
	 *
	 * @return
	 */
	public ArrayList<ChartSet> prepareExitAnimation(ChartView chartView) {

		mIsEntering = false;
		return prepareAnimation(chartView);
	}


	/**
	 * Updates values, with the next interpolation, to be drawn next.
	 *
	 * @return return the next interpolated values.
	 */
	private ArrayList<ChartSet> getUpdate(ArrayList<ChartSet> data) {

		final int nSets = data.size();
		final int nEntries = data.get(0).size();


		long currentTime = System.currentTimeMillis();

		// In case current duration slightly goes over the
		// animation duration, force it to the duration value
		mCurrDuration = currentTime - mInitTime;
		if (mCurrDuration > mDuration) mCurrDuration = mDuration;

		// Process current animation duration, global and for each entry.
		long diff;
		for (int i = 0; i < nEntries; i++) {
			diff = currentTime - mEntryInitTime[i];
			if (diff < 0)
				mCurrEntryDuration[i] = 0;
			else if (diff > mEntryDuration)
				mCurrEntryDuration[i] = mEntryDuration;
			else
				mCurrEntryDuration[i] = diff;
		}

		// Update next values to be drawn
		float[] posUpdate = new float[2];
		float timeNormalized;
		for (int i = 0; i < nSets; i++)

			for (int j = 0; j < nEntries; j++) {

				timeNormalized = (float) mCurrEntryDuration[j] / mEntryDuration;

				if (mAlphaSpeed != -1)
					data.get(i).setAlpha(mInterpolator.getInterpolation(timeNormalized) * mAlphaSpeed *
							  mSetsAlpha[i]);

				if (!getEntryUpdate(i, j, timeNormalized, posUpdate)) {
					posUpdate[0] = data.get(i).getEntry(j).getX();
					posUpdate[1] = data.get(i).getEntry(j).getY();
				}
				data.get(i).getEntry(j).setCoordinates(posUpdate[0], posUpdate[1]);
			}

		// Sets the next update or finishes the animation
		if (mCurrDuration < mDuration && !mCancelled) {
			mChartView.postDelayed(mAnimator, DELAY_BETWEEN_UPDATES);
			mCurrDuration += DELAY_BETWEEN_UPDATES;
		} else {
			mCurrDuration = 0;
			mInitTime = 0;
			if (mRunnable != null) mRunnable.run();
			mPlaying = false;
		}

		return data;
	}


	/**
	 * Cancel the on-going animation.
	 */
	public void cancel() {

		mCancelled = true;
	}


	/**
	 * Gets the next position coordinate of a point.
	 *
	 * @param i set index
	 * @param j point index
	 * @param normalizedTime normalized time from 0 to 1
	 *
	 * @return x display value where point will be drawn
	 */
	private boolean getEntryUpdate(int i, int j, float normalizedTime, float[] pos) {

		return mPathMeasures[i][j].getPosTan(
				  mPathMeasures[i][j].getLength() * mInterpolator.getInterpolation(normalizedTime), pos, null);
	}


	public boolean isPlaying() {

		return mPlaying;
	}


	public Runnable getEndAction() {

		return mRunnable;
	}


	/**
	 * Animation easing will be managed differently depending on which {@link TimeInterpolator}
	 * object is defined.
	 *
	 * @param interpolator {@link TimeInterpolator} object responsible to interpolate animation values
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	public Animation setEasing(TimeInterpolator interpolator) {

		mInterpolator = interpolator;
		return this;
	}


	/**
	 * @param duration
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	public Animation setDuration(int duration) {

		mDuration = duration;
		return this;
	}


	/**
	 * Sets whether entries should be animate in sequence or parallel.
	 *
	 * @param factor value from 0 to 1 that tells how much will be the overlap of an entry's
	 * animation according to the previous one.
	 * 0 - no overlap
	 * 1 - all entries animate in parallel (default)
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	private Animation setOverlap(@FloatRange(from = 0.f, to = 1.f) float factor) {

		if (factor > 1 || factor < 0)
			throw new IllegalArgumentException("Overlap factor must be between 0 and 1, " +
					  "the current defined is " + factor);

		mOverlapingFactor = factor;
		return this;
	}


	/**
	 * Sets whether entries should be animate in sequence or parallel.
	 *
	 * @param factor value from 0 to 1 that tells how much will be the overlap of an entry's
	 * animation according to the previous one
	 * 0 - no overlap
	 * 1 - all entries animate in parallel (default)
	 * @param order order from which the entries will be animated
	 * { 0, 1, 2, 3, ...} - default order
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	public Animation setOverlap(@FloatRange(from = 0.f, to = 1.f) float factor, int[] order) {

		setOverlap(factor);
		mOrder = order;
		return this;
	}


	/**
	 * Sets an action to be executed once the animation finishes.
	 *
	 * @param runnable to be executed once the animation finishes
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	public Animation setEndAction(Runnable runnable) {

		mRunnable = runnable;
		return this;
	}


	/**
	 * Sets the starting point for the animation.
	 * Eg. xFactor=0; yFactor=0; starts the animation on the bottom left
	 * corner of the inner chart area.
	 *
	 * @param xFactor horizontal factor between 0 and 1. If not applied then -1 can be set.
	 * @param yFactor vertical factor between 0 and 1. If not applied then -1 can be set.
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	public Animation setStartPoint(@FloatRange(from = -1.f, to = 1.f) float xFactor,
			  @FloatRange(from = -1.f, to = 1.f) float yFactor) {

		mStartXFactor = xFactor;
		mStartYFactor = yFactor;
		return this;
	}


	/**
	 * Sets an alpha speed to animation.
	 * To disable alpha set it to -1.
	 * Eg. If speed 2 alpha goes twice faster than translation.
	 *
	 * @param speed speed of alpha animation values according with translation.
	 *
	 * @return {@link com.db.chart.animation.Animation} self-reference.
	 */
	public Animation setAlpha(int speed) {

		mAlphaSpeed = speed;
		return this;
	}


}
