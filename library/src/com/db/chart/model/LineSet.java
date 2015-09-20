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

import java.lang.IllegalArgumentException;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import android.graphics.drawable.Drawable;

import com.db.chart.Tools;


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


	/** Shadow variables */
	protected float mShadowRadius;
	protected float mShadowDx;
	protected float mShadowDy;
	private int[] mShadowColor;



	/**
	 * Constructor.
	 *
	 */
	public LineSet(){
		super();
        init();
	}


	/**
	 * Constructor.
	 *
	 * @param labels
	 * @param values
	 */
    public LineSet(@NonNull String[] labels, @NonNull float[] values){
        super();
        init();

		if(labels == null || values == null)
			throw new IllegalArgumentException("Labels or/and values can't be null.");
        if(labels.length != values.length)
			throw new IllegalArgumentException("Arrays size doesn't match.");

        int nEntries = labels.length;
        for(int i = 0; i < nEntries; i++)
            addPoint(labels[i], values[i]);
    }


	/**
	 * Set defaults.
	 *
	 */
    private void init(){

        mThickness = Tools.fromDpToPx(LINE_THICKNESS);
        mColor = DEFAULT_COLOR;

        mIsDashed = false;
		mDashedIntervals = null;
		mDashedPhase = 0;

        mIsSmooth = false;

        mHasFill = false;
        mFillColor = DEFAULT_COLOR;

        mHasGradientFill = false;
        mGradientColors = null;
        mGradientPositions = null;

        mBegin = 0;
        mEnd = 0;

		mShadowRadius = 0;
		mShadowDx = 0;
		mShadowDy = 0;
		mShadowColor = new int[4];
    }


	/**
	 * Add new {@link com.db.chart.model.Point} from a string and a float.
	 *
	 * @param label   new {@link com.db.chart.model.Point}'s label
	 * @param value   new {@link com.db.chart.model.Point}'s value
	 */
	public void addPoint(String label, float value){
		this.addPoint(new Point(label, value));
	}


	/**
	 * Add new {@link com.db.chart.model.Point}.
	 *
	 * @param point   new {@link com.db.chart.model.Point}
	 */
	public void addPoint(@NonNull Point point){
		this.addEntry(point);
	}


	/**
	 * If line dashed.
	 *
	 * @return true if dashed property defined.
	 */
	public boolean isDashed() {
		return mIsDashed;
	}


	/**
	 * If line smooth.
	 *
	 * @return true if smooth property defined.
	 */
	public boolean isSmooth() {
		return mIsSmooth;
	}


	/**
	 * If line has fill color defined.
	 *
	 * @return true if fill property defined.
	 */
    public boolean hasFill() {
        return mHasFill;
    }


	/**
	 * If line has gradient fill color defined.
	 *
	 * @return true if gradient fill property defined.
	 */
    public boolean hasGradientFill(){
        return mHasGradientFill;
    }


	public boolean hasShadow(){
		return mShadowRadius != 0;
	}


	
	/*
	 * --------
	 * Getters
	 * --------
	 */


	/**
	 * Retrieve line's thickness.
	 *
	 * @return Line's thickness.
	 */
	public float getThickness() {
		return mThickness;
	}


	/**
	 * Retrieve line's color.
	 *
	 * @return Line's color.
	 */
	public int getColor() {
		return mColor;
	}


	/**
	 * Retrieve color defined for line's fill.
	 * Fill property must have been previously defined.
	 *
	 * @return Line's fill color.
	 */
	public int getFillColor() {
		return mFillColor;
	}


	/**
	 * Retrieve set of colors defining the gradient of line's fill.
	 * Gradient fill property must have been previously defined.
	 *
	 * @return Gradient colors array.
	 */
	public int[] getGradientColors(){
		return mGradientColors;
	}


	/**
	 * Retrieve set of positions to define the gradient of line's fill.
	 * Gradient fill property must have been previously defined.
	 *
	 * @return Gradient positions.
	 */
	public float[] getGradientPositions(){
		return mGradientPositions;
	}


	/**
	 * Retrieve first {@link com.db.chart.model.Point} that will be displayed for this set.
	 *
	 * @return first displayed {@link com.db.chart.model.Point}.
	 */
	public int getBegin() {
		return mBegin;
  	}


	/**
	 * Retrieve last {@link com.db.chart.model.Point} that will be displayed for this set.
	 *
	 * @return last displayed {@link com.db.chart.model.Point}.
	 */
	public int getEnd() {

		if(mEnd == 0)
			return size();
		return mEnd;
	}


	/**
	 * Retrieve set of intervals defining line's dash.
	 * Dashed property must have been previously defined.
	 *
	 * @return Line dashed intervals. Dashed property must have been previously defined.
	 */
    public float[] getDashedIntervals(){
        return mDashedIntervals;
    }


	/**
	 *
	 * @return Line dashed phase. Dashed property must have been previously defined.
	 */
	public int getDashedPhase(){
		return mDashedPhase;
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
	 *
	 * @param phase
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDashedPhase(int phase){
		mDashedPhase = phase;
		return this;
	}


    /**
     * Define a dashed effect to the line.
     *
     * @param intervals Array of ON and OFF distances
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet setDashed(@NonNull float[] intervals) {

		if(intervals == null)
			throw new IllegalArgumentException("Argument can't be null.");

        mIsDashed = true;
        mDashedIntervals = intervals;
        return this;
    }


	/**
	 * Define a smooth effect to the line.
	 *
	 * @param bool   True if line smooth
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
    public LineSet setSmooth(boolean bool) {

		mIsSmooth = bool;
		return this;
	}

	
	/**
	 * Define the thickness to be used when drawing the line.
     *
	 * @param thickness   Line thickness. Can't be equal or less than 0
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setThickness(@FloatRange(from=0.f) float thickness) {
		
		if(thickness < 0)
			throw new IllegalArgumentException("Line thickness can't be <= 0.");

		mThickness = thickness;
		return this;
	}


	/**
	 * Define the color to be used when drawing the line.
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setColor(@ColorInt int color){

		mColor = color;
		return this;
	}


	/**
	 * Define the color to fill up the line area.
	 * If no color has been previously defined to the line it will automatically be set to the
	 * same color fill color.
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
    public LineSet setFill(@ColorInt int color){

        mHasFill = true;
        mFillColor = color;

		if(mColor == DEFAULT_COLOR)
			mColor = color;

        return this;
    }


    /**
	 * Define the gradient colors to fill up the line area.
	 * If no color has been previously defined to the line it will automatically be set to the
	 * first color defined in gradient.
	 *
     * @param colors   The colors to be distributed among gradient
     * @param positions   Position/order from which the colors will be place
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet setGradientFill(@NonNull int colors[], float[] positions){

		if(colors == null || colors.length == 0)
			throw new IllegalArgumentException("Colors argument can't be null or empty.");

        mHasGradientFill = true;
        mGradientColors = colors;
        mGradientPositions = positions;

		if(mColor == DEFAULT_COLOR)
			mColor = colors[0];

        return this;
    }


    /**
     * Define at which {@link com.db.chart.model.Point} should the dataset begin.
     *
     * @param index   Index where the set begins. Argument mustn't be negative or greater than set's size.
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet beginAt(@IntRange(from=0) int index) {

        if(index < 0 || index > size())
			throw new IllegalArgumentException("Index is negative or greater than set's size.");

        mBegin = index;
        return this;
    }


    /**
     * Define at which {@link com.db.chart.model.Point} should the dataset end.
     *
     * @param index   Where the set ends. Argument mustn't be negative, greater than set's size, or
	 *                   lesser than the first point to be displayed (defined by beginAt() method)..
	 * @return {@link com.db.chart.model.LineSet} self-reference.
     */
    public LineSet endAt(@IntRange(from=0) int index) {

		if(index < 0 || index > size())
			throw new IllegalArgumentException("Index is negative or greater than set's size.");
		if(index < mBegin)
			throw new IllegalArgumentException("Index cannot be lesser than the start entry " +
					"defined in beginAt(index).");

        mEnd = index;
        return this;
    }


	/**
	 * Define the color to be used when drawing the dots.
	 * Color will be assigned to all {@link com.db.chart.model.Point}s in the set.
	 * Will override previous defined values for any {@link com.db.chart.model.Point}.
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsColor(@ColorInt int color){

        for(ChartEntry e : getEntries())
            e.setColor(color);
        return this;
	}


	/**
	 * Define the radius to be used when drawing the dots.
	 * Radius will be assigned to all {@link com.db.chart.model.Point}s in the set.
	 * Will override previous defined values for any {@link com.db.chart.model.Point}.
	 *
	 * @param radius
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsRadius(@FloatRange(from=0.f) float radius){

		if(radius < 0.f)
			throw new IllegalArgumentException("Dots radius can't be < 0.");

        for(ChartEntry e : getEntries())
            ((Point) e).setRadius(radius);
		return this;
	}


	/**
	 * Define the stroke thickness to be used when drawing the dots.
	 * Thickness will override previous defined values for any {@link com.db.chart.model.Point}.
	 * Will override previous defined values for any {@link com.db.chart.model.Point}s.
	 *
	 * @param thickness   Grid thickness. Can't be equal or less than 0
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsStrokeThickness(@FloatRange(from=0.f) float thickness){

		if(thickness < 0.f)
			throw new IllegalArgumentException("Dots thickness can't be < 0.");

        for(ChartEntry e : getEntries())
            ((Point) e).setStrokeThickness(thickness);
        return this;
	}


	/**
	 * Define the stroke color to be used when drawing the dots.
	 * Color will override previous defined values for any {@link com.db.chart.model.Point}.
	 * Will override previous defined values for any {@link com.db.chart.model.Point}s.
	 *
	 * @param color
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsStrokeColor(@ColorInt int color){

        for(ChartEntry e : getEntries())
            ((Point) e).setStrokeColor(color);
        return this;
	}
	
	
	/**
	 * Define a background drawable to each of the dataset points to be
	 * drawn instead of the usual dot.
     *
	 * @param drawable
	 * @return {@link com.db.chart.model.LineSet} self-reference.
	 */
	public LineSet setDotsDrawable(@NonNull Drawable drawable){

		if(drawable == null)
			throw new IllegalArgumentException("Drawable argument can't be null.");

        for(ChartEntry e : getEntries())
            ((Point) e).setDrawable(drawable);
        return this;
	}


	@Override
	public void setShadow(float radius, float dx, float dy, int color) {
		super.setShadow(radius, dx, dy, color);

		mShadowRadius = radius;
		mShadowDx = dx;
		mShadowDy = dy;

		mShadowColor[0] = Color.alpha(color);
		mShadowColor[1] = Color.red(color);
		mShadowColor[2] = Color.blue(color);
		mShadowColor[3] = Color.green(color);
	}
	
}
