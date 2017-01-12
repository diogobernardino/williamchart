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
 * Data model that represents a bar in {@link com.db.chart.view.BaseBarChartView}
 */
public class Bar extends ChartEntry{


    private boolean mHasGradientColor;
    private int[] mGradientColors;
    private float[] mGradientPositions;


    public Bar(String label, float value){
		super(label, value);

        isVisible = true;
        mHasGradientColor = false;
	}


    /**
     *
     * @return true if gradient color define.
     */
    public boolean hasGradientColor(){
        return mHasGradientColor;
    }


    /**
     *
     * @return gradient colors. Gradient color must have been previously defined.
     */
    public int[] getGradientColors(){
        return mGradientColors;
    }


    /**
     *
     * @return gradient positions. Gradient color must have been previously defined.
     */
    public float[] getGradientPositions(){
        return mGradientPositions;
    }



    /**
     * Set gradient colors to the fill of the {@link com.db.chart.model.Bar}.
     *
     * @param colors   The colors to be distributed among gradient
     * @param positions
     * @return {@link com.db.chart.model.Bar} self-reference.
     */
    public Bar setGradientColor(int colors[], float[] positions){

        mHasGradientColor = true;
        mGradientColors = colors;
        mGradientPositions = positions;
        return this;
    }
	
}
