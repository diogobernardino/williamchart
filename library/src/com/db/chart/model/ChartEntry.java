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

/**
 * Generic Data model of a {@link com.db.chart.view.ChartView} entry
 */
public abstract class ChartEntry {

    /** Default bar color */
    private static final int DEFAULT_COLOR = -16777216;

	
	/** Input from user */
	final private String mLabel;
	private float mValue;

	
	/** Display coordinates */
	private float mX;
	private float mY;


    /** Bar color */
    private int mColor;


    /** Defines if entry is visible */
    boolean isVisible;

	
	ChartEntry(String label, float value){
		mLabel = label;
		mValue = value;

        mColor = DEFAULT_COLOR;
	}


    public boolean isVisible(){
        return isVisible;
    }
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */


	public String getLabel() {
		return mLabel;
	}


	public float getValue() {
		return mValue;
	}


	public float getX() {
		return mX;
	}


	public float getY() {
		return mY;
	}


    public int getColor() {
        return mColor;
    }
	

	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	
	/**
	 * Set new entry value.
     *
	 * @param value   New value
	 */
	public void setValue(float value){
		mValue = value;
	}
	
	
	/**
	 * Set the parsed display coordinates.
     *
	 * @param x
	 * @param y
	 */
	public void setCoordinates(float x, float y){

		mX = x;
		mY = y;
	}


    public void setColor(int mColor) {

        isVisible = true;
        this.mColor = mColor;
    }


	public String toString(){
		return "Label="+mLabel+" \n" + "Value="+mValue+"\n" +
				"X = "+mX+"\n" + "Y = "+mY;
	}

}
