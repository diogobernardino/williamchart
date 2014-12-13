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

import java.util.ArrayList;


/**
 * Data model containing {@link ChartEntry} elements to be used by {@link ChartView}.
 */
public class ChartSet {
	
	
	/** Set with entries */
	private ArrayList<ChartEntry> mEntries;

	
	/** Paint alpha value from 0 to 1 */
	private float mAlpha;
	
	
	
	public ChartSet(){
		mEntries = new ArrayList<ChartEntry>();
		mAlpha = 1;
	}
	
	
	
	
	protected void addEntry(String label, float value){
		mEntries.add(new ChartEntry(label, value));
	}

	
	
	protected void addEntry(ChartEntry e){
		mEntries.add(e);
	}

	
	
	/**
	 * Updates set values.
	 * @param newValues
	 * @return float[] with X and Y coordinates of old values
	 */
	public float[][] updateValues(float[] newValues){
		
		float[][] result = new float[size()][2];
		for(int i = 0; i < size(); i++){
			result[i][0] = getEntry(i).getX();
			result[i][1] = getEntry(i).getY();
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
	 * @param i - entry's index
	 */
	public ChartEntry getEntry(int i) {
		return mEntries.get(i);
	}
	
	
	/**
	 * Get {@link ChartEntry} value from specific index.
	 * @param i - value's index
	 */
	public float getValue(int i){
		return mEntries.get(i).getValue();
	}
	
	
	/**
	 * Get {@link ChartEntry} label from specific index.
	 * @param i - label's index
	 */
	public String getLabel(int i) {
		return mEntries.get(i).getLabel();
	}
	
	
	/**
	 * Get screen points.
	 */
	public float[][] getScreenPoints(){
		
		float[][] result = new float[size()][2];
		for(int i = 0; i < size(); i++){
			result[i][0] = getEntry(i).getX();
			result[i][1] = getEntry(i).getY();
		}
		
		return result;
	}
	
	
	/**
	 * Get current set's alpha.
	 */
	public float getAlpha(){
		return mAlpha;
	}
	
	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	
	/**
	 * Set {@link ChartEntry} value at specific index position.
	 * @param i - value's index where value will be placed.
	 */
	private void setValue(int i, float value){
		mEntries.get(i).setValue(value);
	}
	
	
	/**
	 * Get set's alpha.
	 */
	public void setAlpha(float alpha){
		mAlpha = (alpha < 1) ? alpha : 1;
	}
	
	
	
	public String toString(){
		return mEntries.toString();
	}
	
	
	public int size() {
		return mEntries.size();
	}
	
}
