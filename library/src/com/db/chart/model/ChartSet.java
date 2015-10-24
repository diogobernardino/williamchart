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

package com.db.chart.model;

import android.support.annotation.FloatRange;

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
			throw new IllegalArgumentException("Chart entry added can't be null object.");

		mEntries.add(e);
	}

	
	
	/**
	 * Updates set values.
     *
	 * @param newValues
	 */
	public void updateValues(float[] newValues){

		int nEntries = size();
		if(newValues.length != nEntries)
			throw new IllegalArgumentException("New set values given doesn't match previous " +
					"number of entries.");

		for(int i = 0; i < nEntries; i++)
			setValue(i, newValues[i]);
	}
	
	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */
	
	/**
	 * Get set of {@link ChartEntry}s.
	 *
	 * @return List of entries contained in the set.
	 */
	public ArrayList<ChartEntry> getEntries(){
		return mEntries;
	}
	
	
	/**
	 * Get {@link ChartEntry} from specific index.
     *
	 * @param index   Entry's index
	 * @return {@link com.db.chart.model.ChartSet} self-reference.
	 */
	public ChartEntry getEntry(int index) {
		return mEntries.get(index);
	}
	
	
	/**
	 * Get {@link ChartEntry} value from specific index.
     *
	 * @param index   Value's index
	 * @return   Value of given index.
	 */
	public float getValue(int index){
		return mEntries.get(index).getValue();
	}
	
	
	/**
	 * Get {@link ChartEntry} label from specific index.
     *
	 * @param index   Label's index
	 * @return   Label of given index.
	 */
	public String getLabel(int index) {
		return mEntries.get(index).getLabel();
	}
	
	
	/**
	 * Get screen points.
	 *
	 * @return   Display coordinates of all entries.
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
	 *
	 * @return   Set's alpha.
	 */
	public float getAlpha(){
		return mAlpha;
	}
	
	
	/**
	 * Get whether the set should be presented or not.
	 *
	 * @return   True if set visible, False if not.
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
	 *
	 * @param alpha   alpha value from 0 to 1.
	 *                   If you need to make the set invisible than consider
	 *                   using the method setVisible().
	 */
	public void setAlpha(@FloatRange(from=0.f, to=1.f) float alpha){
		mAlpha = (alpha < 1) ? alpha : 1;
	}

	
	/**
	 * Set whether the set should be visible or not.
	 *
	 * @param visible   false if set should not be visible.
	 */
	public void setVisible(boolean visible){
		mIsVisible = visible;
	}


	/**
	 *
	 * @param radius
	 * @param dx
	 * @param dy
	 * @param color
	 */
	public void setShadow(float radius, float dx, float dy, int color) {

		for(ChartEntry e : getEntries())
			e.setShadow(radius, dx, dy, color);
	}



	public int size() {
		return mEntries.size();
	}


	public String toString(){
		return mEntries.toString();
	}

	
}
