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

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import com.db.chart.Tools;


/**
 * Data model that represents a point in {@link com.db.chart.view.LineChartView}
 */
public class Point extends ChartEntry{

    private static final String TAG = "chart.model.Point";


    /** Defaults **/
    private static final int DEFAULT_COLOR = -16777216;
    private static final float DOTS_THICKNESS = 4;
    private static final float DOTS_RADIUS = 3;


    /** Dot variables */
    private boolean mHasStroke;
    private float mStrokeThickness;
    private int mStrokeColor;


    /** Radius */
    private float mRadius;


    /** Dots drawable background */
    private Drawable mDrawable;


    /**
     * Constructor.
     *
     * @param label
     * @param value
     */
	public Point(String label, float value){
		super(label, value);

        isVisible = false;

        mRadius = Tools.fromDpToPx(DOTS_THICKNESS);

        mHasStroke = false;
        mStrokeThickness = Tools.fromDpToPx(DOTS_RADIUS);
        mStrokeColor = DEFAULT_COLOR;

        mDrawable = null;
	}


    /**
     * Whether the Point has stroke defined or not.
     *
     * @return true if point has stroke define.
     */
    public boolean hasStroke() {
        return mHasStroke;
    }



    /*
	 * --------
	 * Getters
	 * --------
	 */


    /**
     * Retrieve the point's stroke thickness.
     *
     * @return point's stroke thickness.
     */
    public float getStrokeThickness() {
        return mStrokeThickness;
    }


    /**
     * Retrieve the point's radius.
     *
     * @return point radius.
     */
    public float getRadius() {
        return mRadius;
    }


    /**
     * Retrieve point's stroke color.
     *
     * @return point's stroke color.
     */
    public int getStrokeColor() {
        return mStrokeColor;
    }


    /**
     * Retrieve point's drawable.
     *
     * @return {@link android.graphics.drawable.Drawable} to be displayed.
     */
    public Drawable getDrawable(){
        return mDrawable;
    }



    /*
	 * --------
	 * Setters
	 * --------
	 */


    /**
     * Define specific radius to point.
     *
     * @param radius   value of radius starting from 0.
     * @return {@link com.db.chart.model.Point} self-reference.
     */
    public Point setRadius(@FloatRange(from=0.f) float radius){

        if(radius < 0.f)
            throw new IllegalArgumentException("Dot radius can't be < 0.");

        isVisible = true;
        mRadius = radius;
        return this;
    }


    /**
     * Define specific thickness to point's stroke.
     *
     * @param thickness   Grid thickness. Can't be equal or less than 0
     * @return {@link com.db.chart.model.Point} self-reference.
     */
    public Point setStrokeThickness(@FloatRange(from=0.f) float thickness){

        if(thickness < 0)
            throw new IllegalArgumentException("Grid thickness < 0.");

        isVisible = true;
        mHasStroke = true;
        mStrokeThickness = thickness;
        return this;
    }


    /**
     * Define point's stroke color.
     *
     * @param color   The color.
     * @return {@link com.db.chart.model.Point} self-reference.
     */
    public Point setStrokeColor(@ColorInt int color){

        isVisible = true;
        mHasStroke = true;
        mStrokeColor = color;
        return this;
    }


    /**
     * Define a drawable to be drawn instead of the usual dot.
     *
     * @param drawable   The drawable.
     * @return {@link com.db.chart.model.Point} self-reference.
     */
    public Point setDrawable(@NonNull Drawable drawable){

        if(drawable == null)
            throw new IllegalArgumentException("Drawable argument can't be null.");

        isVisible = true;
        mDrawable = drawable;
        return this;
    }

}
