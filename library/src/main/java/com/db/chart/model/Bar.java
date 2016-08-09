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

import android.support.annotation.NonNull;


/**
 * Data model that represents a bar in {@link com.db.chart.view.BaseBarChartView}
 */
public class Bar extends ChartEntry{

    private static final String TAG = "chart.model.Bar";

    private boolean mHasGradientColor;
    private int[] mGradientColors;
    private float[] mGradientPositions;


    /**
     * Constructor.
     *
     * @param label
     * @param value
     */
    public Bar(String label, float value){
		super(label, value);

        isVisible = true;
        mHasGradientColor = false;
	}


    /**
     * If bar has gradient fill color defined.
     *
     * @return true if gradient fill property defined.
     */
    public boolean hasGradientColor(){
        return mHasGradientColor;
    }


    /*
	 * -------------
	 * Getters
	 * -------------
	 */

    /**
     * Retrieve set of colors defining the gradient of bar's fill.
     * Gradient fill property must have been previously defined.
     *
     * @return Gradient colors array.
     */
    public int[] getGradientColors(){
        return mGradientColors;
    }


    /**
     * Retrieve set of positions to define the gradient of bar's fill.
     * Gradient fill property must have been previously defined.
     *
     * @return Gradient positions.
     */
    public float[] getGradientPositions(){
        return mGradientPositions;
    }


	/*
	 * -------------
	 * Setters
	 * -------------
	 */

    /**
     * Set gradient colors to the fill of the {@link com.db.chart.model.Bar}.
     *
     * @param colors   The colors to be distributed among gradient
     * @param positions   Position/order from which the colors will be place
     * @return {@link com.db.chart.model.Bar} self-reference.
     */
    public Bar setGradientColor(@NonNull int colors[], float[] positions){

        if(colors == null || colors.length == 0)
            throw new IllegalArgumentException("Colors argument can't be null or empty.");

        mHasGradientColor = true;
        mGradientColors = colors;
        mGradientPositions = positions;
        return this;
    }
	
}
