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

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;


/**
 * Class responsible to control horizontal measures, positions, yadda yadda.  
 * If the drawing is requested it will also take care of it.
 */
public class XController extends AxisController{


	/** Vertical coordinate where label will be drawn */
	private int mLabelVerCoord;

	
	/** Width of last label of sets */
	private float mLastLabelWidth;



	public XController(ChartView chartView) {
		super(chartView);
	}


	public XController(ChartView chartView, TypedArray attrs) {
		super(chartView);
	}



    /*
     * IMPORTANT: Method's order is crucial. Change it (or not) carefully.
     */
	protected void init() {

        // Set the vertical display coordinate
        mLabelVerCoord = chartView.chartBottom;
        if(labelsPositioning == LabelPosition.INSIDE)
            mLabelVerCoord -= distLabelToAxis;

        defineLabels();

        // To manage horizontal width of the last axis label
        mLastLabelWidth = chartView.style.labelPaint.measureText(labels.get(nLabels - 1));

        defineMandatoryBorderSpacing(chartView.getInnerChartLeft(), getInnerChartRight());
        defineLabelsPos(chartView.getInnerChartLeft(), getInnerChartRight());
    }
	
	
	
	/*
	 * ---------
	 *  Getters
	 * ---------
	 */
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
     *
	 * @return Position of the inner right side of the chart
	 */
	public float getInnerChartRight(){
		
		float rightBorder = 0;
		if(borderSpacing + mandatoryBorderSpacing < mLastLabelWidth / 2)
			rightBorder = mLastLabelWidth/2 - (borderSpacing + mandatoryBorderSpacing);
	
		return chartView.chartRight - rightBorder;
	}

	
	/**
	 * Get the vertical position of axis.
	 */
	protected float getAxisVerticalPosition(){
		
		if(labelsPositioning != LabelPosition.OUTSIDE)
			return chartView.chartBottom;
		
		return chartView.chartBottom - getLabelHeight() - distLabelToAxis;
	}



    /**
     * Based in a (real) value returns the associated screen point.
     *
     * @param value   Value to be parsed in display coordinate
     * @return Display's coordinate
     */
    protected float parsePos(int index, double value){

        if(handleValues)
            return (float) (chartView.getInnerChartLeft() +
                (((value - minLabelValue) * screenStep) / (labelsValues.get(1) - minLabelValue)));
        else
            return labelsPos.get(index);
    }



    /*
     * -----------------
     * Abstract Methods
     * -----------------
     */

    /**
     * Method called from onDraw method to draw XController data.
     *
     * @param canvas   {@link android.graphics.Canvas} object to use while drawing the data.
     */
    @Override
    protected void draw(Canvas canvas){

        // Draw axis
        if(hasAxis)
            canvas.drawLine(chartView.getInnerChartLeft(),
                    getAxisVerticalPosition(),
                    getInnerChartRight(),
                    getAxisVerticalPosition(),
                    chartView.style.chartPaint);

        // Draw labels
        if(labelsPositioning != LabelPosition.NONE){

            chartView.style.labelPaint.setTextAlign(Align.CENTER);
            for(int i = 0; i < nLabels; i++){
                canvas.drawText(labels.get(i),
                                    labelsPos.get(i),
                                        mLabelVerCoord,
                                            chartView.style.labelPaint);

            }
        }
    }

}