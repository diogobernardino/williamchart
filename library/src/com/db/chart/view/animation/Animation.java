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
	
	
	/** Distance between starting points and targets */
	private float[][] mDistances;
	
	
	/** Animation duration */
	private long mDuration;
	
	
	/** Keeps the current duration */
	private long mCurrentDuration;
	
	
	/** Keeps the initial time of the animantion*/
	private long mInitTime;
	
	
	/** Controls interpolation of the animation */
	private BaseEasingMethod mEasing;

	
	/**
	 * Control animation updates
	 */
    private Runnable mAnimator = new Runnable() {
        @Override
        public void run() {
        	if(mChartView.canYouDraw()){
        		mChartView.addData(getUpdate());
        		mChartView.postInvalidate();
        	}
        }
    };


    /** {@link ChartView} element to request draw updates */
	private ChartView mChartView;

	
	/** Animation starting point */
	private float mStartY;
	
	
	/** Keeps information if animation is ongoing or not */
	private boolean mPlaying;
	
	
	
	public Animation(){
		init(DEFAULT_DURATION);
	}
	
	
	public Animation(int duration){
		init(duration);
	}
	
	
	
	private void init(int duration){
		mDuration = duration;
		mCurrentDuration = 0;
		mInitTime = 0;
		mPlaying = false;
		mEasing = new QuintEaseOut();
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
		mStartY = startY;
		mDistances = new float[mSets.size()][mSets.get(0).size()];
		
		for(int i = 0; i < mSets.size(); i++){
			for(int j = 0; j < mSets.get(i).size(); j++){
				mDistances[i][j] = startY - mSets.get(i).getEntry(j).getY();
				mSets.get(i).getEntry(j).setY(startY);
			}
		}
		mPlaying = true;
		return getUpdate();
	}
	
	
	
	/**
	 * Updates values, with the next interpolation, to be drawn next.
	 * @return return the next interpolated values.
	 */
	private ArrayList<ChartSet> getUpdate(){
		
		if(mInitTime == 0)
			mInitTime = System.currentTimeMillis();
		
		mCurrentDuration = System.currentTimeMillis() - mInitTime;
		if(mCurrentDuration > mDuration)
			mCurrentDuration = mDuration;
		
		for(int i = 0; i < mSets.size(); i++)
			for(int j = 0; j < mSets.get(i).size(); j++){
				mSets.get(i).getEntry(j)
					.setY(getEntryUpdate(i, j, normalizeTime(mCurrentDuration)));
			}
		
		if(mCurrentDuration < mDuration){
			mChartView.postDelayed(mAnimator, DELAY_BETWEEN_UPDATES);
			mCurrentDuration+= DELAY_BETWEEN_UPDATES;
		}else{
			mCurrentDuration = 0;
			mInitTime = 0;
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
	private float normalizeTime(long currentTime) {
		return (float)currentTime / mDuration;
	}

	
	
	private float getEntryUpdate(int i, int j, float normalizedTime){
		return mStartY - mDistances[i][j] * mEasing.next(normalizedTime);
	}
	
	
	
	/*
	 * Setters
	 * 
	 */
	
	public Animation setEasing(BaseEasingMethod easing){
		mEasing = easing;
		return this;
	}
	
	
	public Animation setDuration(int duration){
		mDuration = duration;
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
