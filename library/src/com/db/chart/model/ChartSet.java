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
	
	
	
	
	public void addEntry(String label, float value){
		mEntries.add(new ChartEntry(label, value));
	}

	
	
	public void addEntry(ChartEntry e){
		mEntries.add(e);
	}

	
	
	/**
	 * Updates set values.
	 * @param newValues
	 * @return float[] with Y coordinates of old values
	 */
	public float[] updateValues(float[] newValues){
		float[] result = new float[size()];
		for(int i = 0; i < size(); i++){
			result[i] = getEntry(i).getY();
			setValue(i, newValues[i]);
		}
		return result;
	}
	
	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */
	
	
	public ArrayList<ChartEntry> getEntries(){
		return mEntries;
	}
	
	
	public ChartEntry getEntry(int i) {
		return mEntries.get(i);
	}
	
	
	public float getValue(int i){
		return mEntries.get(i).getValue();
	}
	
	
	public String getLabel(int i) {
		return mEntries.get(i).getLabel();
	}
	
	
	public float[] getXCoordinates(){
		
		float[] result = new float[size()];
		for(int i = 0; i < result.length; i++)
			result[i] = getEntry(i).getX();
		
		return result;
	}
	
	
	public float[] getYCoordinates(){
		
		float[] result = new float[size()];
		for(int i = 0; i < result.length; i++)
			result[i] = getEntry(i).getY();
		
		return result;
	}
	
	
	public float getAlpha(){
		return mAlpha;
	}
	
	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	
	private void setValue(int i, float value){
		mEntries.get(i).setValue(value);
	}
	
	
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
