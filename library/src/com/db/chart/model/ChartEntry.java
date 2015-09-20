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

import android.graphics.Color;
import android.support.annotation.ColorInt;

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


	/** Shadow variables */
	protected float mShadowRadius;
	protected float mShadowDx;
	protected float mShadowDy;
	private int[] mShadowColor;



	/**
	 * Constructor.
	 *
	 * @param label
	 * @param value
	 */
	ChartEntry(String label, float value){
		mLabel = label;
		mValue = value;

        mColor = DEFAULT_COLOR;

		mShadowRadius = 0;
		mShadowDx = 0;
		mShadowDy = 0;
		mShadowColor = new int[4];
	}


    public boolean isVisible(){
        return isVisible;
    }


	public boolean hasShadow(){
		return mShadowRadius != 0;
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


	public float getShadowRadius() {
		return mShadowRadius;
	}

	public float getShadowDx() {
		return mShadowDx;
	}

	public float getShadowDy() {
		return mShadowDy;
	}

	public int[] getShadowColor() {
		return mShadowColor;
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
	 * @param x   display x coordinate.
	 * @param y   display y coordinate.
	 */
	public void setCoordinates(float x, float y){

		mX = x;
		mY = y;
	}


	/**
	 * Define the color of the entry.
	 *
	 * @param color   Color to be set.
	 */
    public void setColor(@ColorInt int color) {

        isVisible = true;
        mColor = color;
    }


	/**
	 * Define whether this entry will be drawn or not.
	 *
	 * @param visible   True if entry should be displayed.
	 */
	public void setVisible(boolean visible){

		isVisible = visible;
	}


	/**
	 *
	 * @param radius
	 * @param dx
	 * @param dy
	 * @param color
	 */
	public void setShadow(float radius, float dx, float dy, @ColorInt int color) {

		mShadowRadius = radius;
		mShadowDx = dx;
		mShadowDy = dy;
		mShadowColor[0] = Color.alpha(color);
		mShadowColor[1] = Color.red(color);
		mShadowColor[2] = Color.blue(color);
		mShadowColor[3] = Color.green(color);
	}



	public String toString(){
		return "Label="+mLabel+" \n" + "Value="+mValue+"\n" +
				"X = "+mX+"\n" + "Y = "+mY;
	}

}
