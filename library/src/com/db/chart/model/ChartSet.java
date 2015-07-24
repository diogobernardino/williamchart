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

package com.db.chart.model;

import android.util.Log;

import java.util.ArrayList;


/**
 * Data model containing {@link ChartEntry} elements to be used by {@link com.db.chart.view.ChartView}.
 */
public abstract class ChartSet {


	private static final String TAG = "chart.model.ChartSet";


	/** Set with entries */
	final private ArrayList<ChartEntry> mEntries;
	
	
	/** Paint alpha value from 0 to 1 */
	private float mAlpha;
	
	
	/** Whether the set will be visible or not */
	private boolean mIsVisible;
	
	
	ChartSet(){
		mEntries = new ArrayList<>();
		mAlpha = 1;
		mIsVisible = false;
	}


	
	void addEntry(ChartEntry e){

		if(e == null)
			Log.e(TAG, "Chart entry added can't be null object.", new IllegalArgumentException());

		mEntries.add(e);
	}

	
	
	/**
	 * Updates set values.
     *
	 * @param newValues
	 * @return float[] with X and Y coordinates of old values
	 */
	public float[][] updateValues(float[] newValues){

		int nEntries = size();
		if(newValues.length != nEntries)
			Log.e(TAG, "New set values given doesn't match previous number of entries.",
					new IllegalArgumentException());

		float[][] result = new float[nEntries][2];
		for(int i = 0; i < nEntries; i++){
			result[i][0] = mEntries.get(i).getX();
			result[i][1] = mEntries.get(i).getY();
			setValue(i, newValues[i]);
		}
		return result;
	}
	
	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */
	
	/**
	 * Get set of {@link ChartEntry}s.
	 */
	public ArrayList<ChartEntry> getEntries(){
		return mEntries;
	}
	
	
	/**
	 * Get {@link ChartEntry} from specific index.
     *
	 * @param index - Entry's index
	 */
	public ChartEntry getEntry(int index) {
		return mEntries.get(index);
	}
	
	
	/**
	 * Get {@link ChartEntry} value from specific index.
     *
	 * @param index   Value's index
	 */
	public float getValue(int index){
		return mEntries.get(index).getValue();
	}
	
	
	/**
	 * Get {@link ChartEntry} label from specific index.
     *
	 * @param index   Label's index
	 */
	public String getLabel(int index) {
		return mEntries.get(index).getLabel();
	}
	
	
	/**
	 * Get screen points.
	 */
	public float[][] getScreenPoints(){
		
		int nEntries = size();
		float[][] result = new float[nEntries][2];
		for(int i = 0; i < nEntries; i++){
			result[i][0] = mEntries.get(i).getX();
			result[i][1] = mEntries.get(i).getY();
		}
		
		return result;
	}


	/**
	 * Get current set's alpha.
	 */
	public float getAlpha(){
		return mAlpha;
	}
	
	
	/**
	 * Get whether the set should be presented or not.
	 */
	public boolean isVisible(){
		return mIsVisible;
	}
	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	
	/**
	 * Set {@link ChartEntry} value at specific index position.
     *
	 * @param index   Value's index where value will be placed
	 */
	private void setValue(int index, float value){
		mEntries.get(index).setValue(value);
	}


	/**
	 * Set set's alpha.
	 */
	public void setAlpha(float alpha){
		mAlpha = (alpha < 1) ? alpha : 1;
	}

	
	/**
	 * Set whether the set should be visible or not.
	 */
	public void setVisible(boolean visible){
		mIsVisible = visible;
	}


	public int size() {
		return mEntries.size();
	}


	public String toString(){
		return mEntries.toString();
	}

	
}
