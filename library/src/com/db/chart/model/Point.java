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



	public Point(String label, float value){
		super(label, value);

        isVisible = false;

        mRadius = Tools.fromDpToPx(DOTS_THICKNESS);

        mHasStroke = false;
        mStrokeThickness = Tools.fromDpToPx(DOTS_RADIUS);
        mStrokeColor = DEFAULT_COLOR;

        mDrawable = null;
	}



    public boolean hasStroke() {
        return mHasStroke;
    }



    /*
	 * --------
	 * Getters
	 * --------
	 */


    public float getStrokeThickness() {
        return mStrokeThickness;
    }


    public float getRadius() {
        return mRadius;
    }


    public int getStrokeColor() {
        return mStrokeColor;
    }


    public Drawable getDrawable(){
        return mDrawable;
    }



    /*
	 * --------
	 * Setters
	 * --------
	 */


    public Point setRadius(float radius){

        isVisible = true;
        mRadius = radius;
        return this;
    }


    /**
     *
     * @param thickness   Grid thickness. Can't be equal or less than 0
     */
    public Point setStrokeThickness(float thickness){

        isVisible = true;
        if(thickness <= 0)
            Log.e(TAG, "Grid thickness <= 0.", new IllegalArgumentException());
        mHasStroke = true;
        mStrokeThickness = thickness;
        return this;
    }


    public Point setStrokeColor(int color){

        isVisible = true;
        mHasStroke = true;
        mStrokeColor = color;
        return this;
    }


    public Point setDrawable(Drawable drawable){

        isVisible = true;
        mDrawable = drawable;
        return this;
    }

}
