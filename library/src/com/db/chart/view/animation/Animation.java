/*
 * Copyright 2014 Diogo Bernardino
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

package com.db.chart.view.animation;

import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.db.chart.model.ChartSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;


/**
 * Controls the whole animation process.
 */
public class Animation{
	
	
	/** The delay between data updates to be drawn in the screen */
	private final static long DELAY_BETWEEN_UPDATES = 20;
	
	
	/** Default animation duration */
	private final static int DEFAULT_DURATION = 1000;
	
	
	/** Maintains the current drawing data */
	private ArrayList<ChartSet> mSets;

	
	/** Task that handles with animation updates */
	private Runnable mRunnable;
	
	
	/** Maintains path measures to get position updates **/
	private PathMeasure[][] mPathMeasures;
	
	
	/** 
	 * Animation global duration 
	 * Likely to be removed in future.
	 */
	private long mGlobalDuration;
	
	
	/** 
	 * Keeps the current global duration 
	 * Likely to be removed in future.
	 */
	private long mCurrentGlobalDuration;
	
	
	/** 
	 * Keeps the global initial time of the animation
	 * Likely to be removed in future.
	 */
	private long mGlobalInitTime;
	
	
	/** Controls interpolation of the animation */
	private BaseEasingMethod mEasing;

	
	/** Control animation updates */
    private Runnable mAnimator = new Runnable() {
        @Override
        public void run() {
        	if(mChartView.canIPleaseAskYouToDraw()){
        		mChartView.addData(getUpdate());
        		mChartView.postInvalidate();
        	}
        }
    };


    /** {@link ChartView} element to request draw updates */
	private ChartView mChartView;
	
	
	/** Keeps information if animation is ongoing or not */
	private boolean mPlaying;
	
	
	/** Keeps the initial time of the animation for each of the points */
	private long[] mInitTime;
	
	
	/** Keeps the current duration of the animation in each of the points */
	private long[] mCurrentDuration;
	
	
	/** Animation duration for each of the points */
	private int mDuration;
	
	
	/** 
	 * Keeps the information regarding whether points will be animated 
	 * in parallel or in sequence.
	 */
	private float mOverlapingFactor;
	
	
	/**
	 * Factor from 0 to 1 to specify where does the animation starts 
	 * according innerchart area.
	 */
	private float mStartXFactor;
	private float mStartYFactor;
	
	
	/** Alpha speed to include in animation */
	private int mAlphaSpeed;
	
	
	/** Animation order */
	private int[] mOrder;
	
	
	
	public Animation(){
		init(DEFAULT_DURATION);
	}
	
	
	public Animation(int duration){
		init(duration);
	}
	
	
	
	
	private void init(int duration){
		
		mGlobalDuration = duration;
		mCurrentGlobalDuration = 0;
		mGlobalInitTime = 0;
		mOverlapingFactor = 1f;
		mEasing = new QuintEaseOut();
		mPlaying = false;
		mStartXFactor = -1f;
		mStartYFactor = -1f;
		mAlphaSpeed = -1;
	}
	
	
	
	
	
	/**
	 * Method that prepares the animation. Defines starting points, targets, 
	 * distance, yadda, as well as the first set of points to be drawn.
	 * @param chartView - {@link ChartView} to invalidate each time the 
	 * animation wants to update the values.
	 * @param startY - starting point of values.
	 * @param sets - Array of {@link ChartSet} containing the target values.
	 * @return Array of {@link ChartSet} containing the first values to be 
	 * drawn.
	 */
	public ArrayList<ChartSet> prepareEnter(ChartView chartView, ArrayList<float[]> startingX,
			ArrayList<float[]> startingY, ArrayList<ChartSet> sets){
		
		mChartView = chartView;
		mSets = sets;
		
		mPathMeasures = new PathMeasure[mSets.size()][mSets.get(0).size()];
		mInitTime = new long[mSets.get(0).size()];
		mCurrentDuration = new long[mSets.get(0).size()];
		
		// Set the animation order if not defined already
		if(mOrder == null){
			mOrder = new int[mSets.get(0).size()];
			for(int i = 0; i < mOrder.length; i++)
				mOrder[i] = i;
		}
		
		// Calculates the expected duration as there was with no overlap (factor = 0)
		float noOverlapDuration = mGlobalDuration / mSets.get(0).size();
		// Adjust the duration to the overlap
		mDuration = (int) (noOverlapDuration + (mGlobalDuration - noOverlapDuration) * mOverlapingFactor);
		
		// Define animation paths for each entry
		Path path;
		for(int i = 0; i < mSets.size(); i++){
			
			for(int j = 0; j < mSets.get(i).size(); j++){
				
				path = new Path();
				path.moveTo(startingX.get(i)[j], startingY.get(i)[j]);
				path.lineTo(mSets.get(i).getEntry(j).getX(), mSets.get(i).getEntry(j).getY());
				
				mPathMeasures[i][j] = new PathMeasure(path, false);
			}
		}
		
		// Define initial time for each entry
		mGlobalInitTime = System.currentTimeMillis();
		long noOverlapInitTime;
		for(int i = 0; i < mInitTime.length; i++){
			// Calculates the expected init time as there was with no overlap (factor = 0)
			noOverlapInitTime = mGlobalInitTime + (i * (mGlobalDuration / mSets.get(0).size()));
			// Adjust the init time to overlap
			mInitTime[mOrder[i]] = (noOverlapInitTime - ((long) (mOverlapingFactor * (noOverlapInitTime - mGlobalInitTime))));
		}
		
		
		
		mPlaying = true;
		return getUpdate();
	}
	
	
	
	
	
	public ArrayList<ChartSet> prepareEnter(ChartView chartView, ArrayList<ChartSet> sets){
		
		float x = 0;
		if(mStartXFactor != -1)
			x = chartView.getInnerChartLeft() 
				+ (chartView.getInnerChartRight() - chartView.getInnerChartLeft()) 
					* mStartXFactor;
		
		float y = 0;
		if(mStartYFactor != -1)
			y = chartView.getInnerChartBottom() 
				- (chartView.getInnerChartBottom() - chartView.getInnerChartTop()) 
					* mStartYFactor;
		else
			y = chartView.getInnerChartBottom();
			
		
		final ArrayList<float[]> startXValues = new ArrayList<float[]>(sets.size());
		final ArrayList<float[]> startYValues = new ArrayList<float[]>(sets.size());
		float[] Xset;
		float[] Yset;
		for(int i = 0; i < sets.size(); i++){
			
			Xset = new float[sets.get(i).size()];
			Yset = new float[sets.get(i).size()];
			
			for(int j = 0; j < sets.get(i).size(); j++){
				if(mStartXFactor == -1)
					Xset[j] = sets.get(i).getEntry(j).getX();
				else
					Xset[j] = x;
				Yset[j] = y;
			}
			
			startXValues.add(Xset);
			startYValues.add(Yset);
		}
		
		mStartXFactor = -1;
		mStartYFactor = -1;
		
		return prepareEnter(chartView, startXValues,
				startYValues, sets);
	}
	
	
	
	
	
	/**
	 * Updates values, with the next interpolation, to be drawn next.
	 * @return return the next interpolated values.
	 */
	private ArrayList<ChartSet> getUpdate(){
		
		// Process current animation duration
		long diff;
		final long currentTime = System.currentTimeMillis();
		mCurrentGlobalDuration = currentTime - mGlobalInitTime;
		for(int i = 0; i < mCurrentDuration.length; i++){
			diff = currentTime - mInitTime[i];
			if(diff < 0)
				mCurrentDuration[i] = 0;
			else
				mCurrentDuration[i] = diff;
		}
		
		// In case current duration slightly goes over the animation duration, 
		// force it to the duration value
		if(mCurrentGlobalDuration > mGlobalDuration)
			mCurrentGlobalDuration = mGlobalDuration;
		
		// Update next values to be drawn
		float[] posUpdate;
		float timeNormalized = 1;
		for(int i = 0; i < mSets.size(); i++)
			
			for(int j = 0; j < mSets.get(i).size(); j++){
				
				timeNormalized = normalizeTime(j);
				
				if(mAlphaSpeed != -1)
					mSets.get(i).setAlpha(timeNormalized * mAlphaSpeed);
				
				posUpdate = getEntryUpdate(i, j, timeNormalized);
				mSets.get(i).getEntry(j).setCoordinates(posUpdate[0], posUpdate[1]);
			}

		// Sets the next update or finishes the animation
		if(mCurrentGlobalDuration < mGlobalDuration){
			mChartView.postDelayed(mAnimator, DELAY_BETWEEN_UPDATES);
			mCurrentGlobalDuration+= DELAY_BETWEEN_UPDATES;
		}else{
			mCurrentGlobalDuration = 0;
			mGlobalInitTime = 0;
			if(mRunnable != null)
				mRunnable.run();
			mPlaying = false;
			mAlphaSpeed = -1;
		}
		
		return mSets; 
	}
	
	
	
	
	/**
	 * Normalize time to a 0-1 relation.
	 * @param currentTime - value from 0 to 1 representing the current time 
	 * of the animation
	 * @return value from 0 to 1 telling the next step.
	 */
	private float normalizeTime(int index) {
		return (float) mCurrentDuration[index] / mDuration;
	}
	
	
	
	
	/**
	 * Gets the next position coordinate of a point
	 * @param i - set index
	 * @param j - point index
	 * @param normalizedTime
	 * @return x display value where point will be drawn
	 */
	private float[] getEntryUpdate(int i, int j, float normalizedTime){
		
		final float[] pos = new float[2];
		if(mPathMeasures[i][j].getPosTan(mPathMeasures[i][j].getLength() * mEasing.next(normalizedTime), pos, null))
			return pos;
		pos[0] = mSets.get(i).getEntry(j).getX();
		pos[1] = mSets.get(i).getEntry(j).getY();
		
		return pos;
	}
	
	
	
	
	public boolean isPlaying(){
		return mPlaying;
	}
	
	
	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	
	public Animation setEasing(BaseEasingMethod easing){
		mEasing = easing;
		return this;
	}
	
	
	public Animation setDuration(int duration){
		mGlobalDuration = duration;
		return this;
	}
	
	
	/**
	 * Sets whether entries should be animate in sequence or paralell.
	 * @param factor - value from 0 to 1 that tells how much will be the 
	 * overlap of an entry's animation according to the previous one.
	 * 0 - no overlap
	 * 1 - all entries animate in paralell (default)
	 */
	public Animation setOverlap(float factor){
		mOverlapingFactor = factor;
		return this;
	}
	
	
	/**
	 * Sets whether entries should be animate in sequence or paralell.
	 * @param factor - value from 0 to 1 that tells how much will be the 
	 * overlap of an entry's animation according to the previous one.
	 * 0 - no overlap
	 * 1 - all entries animate in paralell (default)
	 * @param order - order from which the entries will be animated.
	 * { 0, 1, 2, 3, ...} - default order
	 */
	public Animation setOverlap(float factor, int[] order){
		mOverlapingFactor = factor;
		mOrder = order;
		return this;
	}
	
	
	/**
	 * Sets an action to be executed once the animation finishes.
	 * @param runnable to be executed once the animation finishes.
	 */
	public Animation setEndAction(Runnable runnable){
		mRunnable = runnable;
		return this;
	}
	
	
	/**
	 * Sets the starting point for the animation.
	 * @param xFactor - horizontal factor between 0 and 1
	 * @param yFactor - vertical factor between 0 and 1
	 * Eg. xFactor=0; yFactor=0; starts the animation on the bottom left 
	 * corner of the inner chart area.
	 */
	public Animation setStartPoint(float xFactor, float yFactor){
		mStartXFactor = xFactor;
		mStartYFactor = yFactor;
		return this;
	}
	
	
	/**
	 * Sets an alpha speed to animation.
	 * @param speed - speed of alpha animation values according with translation.
	 * To disable alpha set it to -1.
	 * Eg. If speed 2 alpha goes twice faster than translation.
	 */
	public Animation setAlpha(int speed){
		mAlphaSpeed = speed;
		return this;
	}
	
	
}
