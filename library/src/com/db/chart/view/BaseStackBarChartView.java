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

package com.db.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import com.db.williamchart.R;

import java.util.ArrayList;


/**
 * Implements a bar chart extending {@link com.db.chart.view.BaseStackBarChartView}
 */
public abstract class BaseStackBarChartView extends BaseBarChartView {


    /** Whether to calculate max value or not */
    protected boolean mCalcMaxValue;



	public BaseStackBarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

        mCalcMaxValue = true;
	}


	public BaseStackBarChartView(Context context) {
		super(context);

        mCalcMaxValue = true;
	}



    /**
     * Calculates Bar width based on the distance of two horizontal labels.
     *
     * @param nSets   Number of sets (not really necessary for stack bars)
     * @param x0   Coordinate(n)
     * @param x1   Coordinate(n+1)
     */
    @Override
    protected void calculateBarsWidth(int nSets, float x0, float x1) {
        barWidth = x1 - x0 - style.barSpacing;
    }


    /**
     * Finds the set that will stay in the bottom of the bar.
     *
     * @param entryIndex   Entry index
     * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet}
     *             to use while drawing the Chart
     * @return   The bottom set index
     */
    protected static int discoverBottomSet(int entryIndex, ArrayList<ChartSet> data){

        int dataSize = data.size();
        int index;
        for(index = 0; index < dataSize; index++){
            if(data.get(index).getEntry(entryIndex).getValue() == 0)
                continue;
            break;
        }
        return index;
    }


    /**
     * Finds the set that will be on top.
     *
     * @param entryIndex   Entry index
     * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet}
     *             to use while drawing the Chart
     * @return   The top set index
     */
    protected static int discoverTopSet(int entryIndex, ArrayList<ChartSet> data){

        int dataSize = data.size();
        int index;
        for(index = dataSize - 1; index >= 0; index--){
            if(data.get(index).getEntry(entryIndex).getValue() == 0)
                continue;
            break;
        }
        return index;
    }



    /**
     * This method will calculate what needs to be the max axis value that fits all the sets
     * aggregated, one on top of the other.
     */
    protected void calculateMaxStackBarValue() {

        float stackValue;
        BarSet barSet;
        Bar bar;
        int maxStackValue = 0;

        int dataSize = data.size();
        int setSize = data.get(0).size();

        for (int i = 0; i < setSize; i++) {

            stackValue = 0;
            for(int j = 0; j < dataSize; j++){

                barSet = (BarSet) data.get(j);
                bar = (Bar) barSet.getEntry(i);
                stackValue += bar.getValue();
            }

            if(maxStackValue < (int) Math.ceil(stackValue))
                maxStackValue = (int) Math.ceil(stackValue);
        }

        while(maxStackValue % this.getStep() != 0)
            maxStackValue += 1;

        super.setAxisBorderValues(0, maxStackValue, this.getStep());
    }



	/*
	 * --------------------------------
	 * Overriden methods from ChartView
	 * --------------------------------
	 */

    @Override
    public ChartView setAxisBorderValues(int minValue, int maxValue, int step){
        mCalcMaxValue = false;
        return super.setAxisBorderValues(minValue, maxValue, step);
    }


    @Override
    public void show(){

        if(mCalcMaxValue)
            calculateMaxStackBarValue();
        super.show();
    }

}