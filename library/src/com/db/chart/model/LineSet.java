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

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.db.chart.Tools;

import java.lang.IllegalArgumentException;

/**
 * Data model containing a set of {@link Point} to be used by {@link com.db.chart.view.LineChartView}.
 */
public class LineSet extends ChartSet{

	
	private static final String TAG = "chart.model.LineSet";

	
	/** Defaults */
    private static final int DEFAULT_COLOR = -16777216;
    private static final float LINE_THICKNESS = 4;

	
	/** Line variables */
	private float mThickness;
	private int mColor;
	
	
	/** Line type */
	private boolean mIsDashed;
	private boolean mIsSmooth;


	/** Background fill variables */
	private boolean mHasFill;
	private int mFillColor;
	
	
	/** Gradient background fill variables */
	private boolean mHasGradientFill;
	private int[] mGradientColors;
	private float[] mGradientPositions;
	
	
	/** Index where set begins/ends */
	private int mBegin;
	private int mEnd;
	

    /** Intervals to apply in dashness */
    private float[] mDashedIntervals;
    /** Phase of the line (useful for animations) */
	private int mDashedPhase;

	
	public LineSet(){
		super();
        init();
	}

    public LineSet(String[] labels, float[] values){
        super();
        init();

        if(labels.length != values.length)
            Log.e(TAG, "Arrays size doesn't match.", new IllegalArgumentException());

        int nEntries = labels.length;
        for(int i = 0; i < nEntries; i++)
            addPoint(labels[i], values[i]);
    }


    private void init(){

        //Set defaults
        mThickness = Tools.fromDpToPx(LINE_THICKNESS);
        mColor = DEFAULT_COLOR;

        mIsDashed = false;
        mIsSmooth = false;

        mHasFill = false;
        mFillColor = DEFAULT_COLOR;

        mHasGradientFill = false;
        mGradientColors = null;
        mGradientPositions = null;

        mBegin = 0;
        mEnd = 0;
    }


	/**
	 *
	 * @param label
	 * @param value
	 */
	public void addPoint(String label, float value){
		this.addPoint(new Point(label, value));
	}


	/**
	 *
	 * @param point
	 */
	public void addPoint(Point point){
		this.addEntry(point);
	}


	/**
	 *
	 * @return true if dashed property defined.
	 */
	public boolean isDashed() {
		return mIsDashed;
	}


	/**
	 *
	 * @return true if smooth property defined.
	 */
	public boolean isSmooth() {
		return mIsSmooth;
	}


	/**
	 *
	 * @return true if fill property defined.
	 */
    public boolean hasFill() {
        return mHasFill;
    }


	/**
	 *
	 * @return true if gradient fill property defined.
	 */
    public boolean hasGradientFill(){
        return mHasGradientFill;
    }


	
	/*
	 * --------
	 * Getters
	 * --------
	 */


	/**
	 *
	 * @return line thickness.
	 */
	public float getThickness() {
		return mThickness;
	}


	/**
	 *
	 * @return line color.
	 */
	public int getColor() {
		return mColor;
	}


	/**
	 *
	 * @return fill color. Fill property must have been previously defined.
	 */
	public int getFillColor() {
		return mFillColor;
	}


	/**
	 *
	 * @return gradient colors. Gradient fill property must have been previously defined.
	 */
	public int[] getGradientColors(){
		return mGradientColors;
	}


	/**
	 *
	 * @return gradient positions. Gradient fill must have been previously defined.
	 */
	public float[] getGradientPositions(){
		return mGradientPositions;
	}


	/**
	 *
	 * @return first displayed {@link com.db.chart.model.Point}.
	 */
	public int getBegin() {
		return mBegin;
  	}


	/**
	 *
	 * @return last displayed {@link com.db.chart.model.Point}.
	 */
	public int getEnd() {
		if(mEnd == 0)
			return size();
		return mEnd;
	}


	/**
	 *
	 * @return line dashed intervals. Dashed property must have been previously defined.
	 */
    public float[] getDashedIntervals(){
        return mDashedIntervals;
    }


	/**
	 *
	 * @return line dashed phase. Dashed property must have been previously defined.
	 */
	public int getDashedPhase(){
		return mDashedPhase;
	}
	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */


	/**
	 *
	 * @param phase
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDashedPhase(int phase){
		mDashedPhase = phase;
		return this;
	}


    /**
     * Set a dashed effect to the line.
     *
     * @param intervals array of ON and OFF distances
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet setDashed(float[] intervals) {

        mIsDashed = true;
        mDashedIntervals = intervals;
        mDashedPhase = 0;
        return this;
    }


	/**
	 *
	 * @param bool
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
    public LineSet setSmooth(boolean bool) {

		mIsSmooth = bool;
		return this;
	}

	
	/**
	 * Defines the thickness to be used when drawing the line.
     *
	 * @param thickness   Line thickness. Can't be equal or less than 0
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setThickness(float thickness) {
		
		if(thickness <= 0)
			Log.e(TAG, "Line thickness <= 0.", new IllegalArgumentException());

		mThickness = thickness;
		return this;
	}


	/**
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setColor(int color){

		mColor = color;
		return this;
	}


	/**
	 * Set color to fill up the line area against the axis.
	 * If no color has been previously defined to the line it will automatically be set to the
	 * same color fill color.
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
    public LineSet setFill(int color){

        mHasFill = true;
        mFillColor = color;

		if(mColor == DEFAULT_COLOR)
			mColor = color;

        return this;
    }


    /**
     *
     * @param colors   The colors to be distributed among gradient
     * @param positions
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet setGradientFill(int colors[], float[] positions){

        mHasGradientFill = true;
        mGradientColors = colors;
        mGradientPositions = positions;
        return this;
    }


    /**
     * Define at which index should the dataset begin.
     *
     * @param index   Index where the set begins
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet beginAt(int index) {

        if(index < 0)
            Log.e(TAG, "Index can't be negative.", new IllegalArgumentException());

        mBegin = index;
        return this;
    }


    /**
     * Define at which index should the dataset end.
     *
     * @param index   Where the set ends
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet endAt(int index) {

        if(index > size())
            Log.e(TAG, "Index cannot be greater than the set's size.", new IllegalArgumentException());

        mEnd = index;
        return this;
    }


	/**
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsColor(int color){

        for(ChartEntry e : getEntries())
            e.setColor(color);
        return this;
	}


	/**
	 *
	 * @param radius
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsRadius(float radius){

        for(ChartEntry e : getEntries())
            ((Point) e).setRadius(radius);
		return this;
	}


	/**
     *
	 * @param thickness   Grid thickness. Can't be equal or less than 0
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsStrokeThickness(float thickness){

        for(ChartEntry e : getEntries())
            ((Point) e).setStrokeThickness(thickness);
        return this;
	}


	/**
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsStrokeColor(int color){

        for(ChartEntry e : getEntries())
            ((Point) e).setStrokeColor(color);
        return this;
	}
	
	
	/**
	 * Set a background drawable to each of the dataset's points.
     *
	 * @param drawable
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsDrawable(Drawable drawable){

        for(ChartEntry e : getEntries())
            ((Point) e).setDrawable(drawable);
        return this;
	}
	
}
