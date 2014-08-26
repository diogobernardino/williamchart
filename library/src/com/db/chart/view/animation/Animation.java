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
	private boolean mInSequence;
	
	
	
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
		mEasing = new QuintEaseOut();
		mInSequence = false;
		mPlaying = false;
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
	public ArrayList<ChartSet> prepareEnter(ChartView chartView, 
			float startY, ArrayList<ChartSet> sets){
		
		mChartView = chartView;
		mSets = sets;
		
		mPathMeasures = new PathMeasure[mSets.size()][mSets.get(0).size()];
		mInitTime = new long[mSets.get(0).size()];
		mCurrentDuration = new long[mSets.get(0).size()];
		
		if(mInSequence) // Divide the duration between the points
			mDuration = (int) mGlobalDuration / mSets.get(0).size();
		else // Duration applied to all
			mDuration = (int) mGlobalDuration;
		
		for(int i = 0; i < mSets.size(); i++){
			for(int j = 0; j < mSets.get(i).size(); j++){
				Path path = new Path();
				path.moveTo(mSets.get(i).getEntry(j).getX(), startY);
				path.lineTo(mSets.get(i).getEntry(j).getX(), mSets.get(i).getEntry(j).getY());
				mPathMeasures[i][j] = new PathMeasure(path, false);
			}
		}
		mPlaying = true;
		
		// Save initial time. Only executed the first time this method gets called.
		if(mGlobalInitTime == 0){
			mGlobalInitTime = System.currentTimeMillis();
			for(int i = 0; i < mInitTime.length; i++){
				if(mInSequence){
					mInitTime[i] = mGlobalInitTime + i * mDuration;
				}else
					mInitTime[i] = mGlobalInitTime;
			}
		}	
		
		return getUpdate();
	}
	
	
	
	/**
	 * Updates values, with the next interpolation, to be drawn next.
	 * @return return the next interpolated values.
	 */
	private ArrayList<ChartSet> getUpdate(){
		
		// Process current animation duration
		long currentTime = System.currentTimeMillis();
		mCurrentGlobalDuration = currentTime - mGlobalInitTime;
		for(int i = 0; i < mCurrentDuration.length; i++){
			long diff = currentTime - mInitTime[i];
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
		for(int i = 0; i < mSets.size(); i++)
			for(int j = 0; j < mSets.get(i).size(); j++){
				mSets.get(i).getEntry(j)
					.setY(getEntryUpdate(i, j, normalizeTime(j)));
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
	 * Gets the next position value of a point
	 * @param i - set index
	 * @param j - point index
	 * @param normalizedTime
	 * @return x display value where point will be drawn
	 */
	private float getEntryUpdate(int i, int j, float normalizedTime){
		float[] pos = new float[2];
		if(mPathMeasures[i][j].getPosTan(mPathMeasures[i][j].getLength() * mEasing.next(normalizedTime), pos, null))
			return pos[1];
		return 0;
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
	
	
	public Animation setAnimateInSequence(boolean bool){
		mInSequence = bool;
		return this;
	}
	
	
	/**
	 * @param runnable to be executed once the animation finishes.
	 * @return this
	 */
	public Animation setEndAction(Runnable runnable){
		mRunnable = runnable;
		return this;
	}
	
	
	public boolean isPlaying(){
		return mPlaying;
	}
	
}
