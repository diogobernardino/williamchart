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

import com.db.williamchart.R;

import java.util.ArrayList;


/**
 * Class responsible to control horizontal measures, positions, yadda yadda.  
 * If the drawing is requested it will also take care of it.
 */
public class XController extends AxisController{


	/** Vertical coordinate where label will be drawn */
	private int mLabelVerCoord;

	
	/** Horizontal border spacing for labels */
	protected float borderSpacing;


	/** Mandatory horizontal border when necessary (ex: BarCharts) */
	protected float mandatoryBorderSpacing;

	
	/** Width of last label of sets */
	private float mLastLabelWidth;

	
	
	public XController(ChartView chartView) {
		super(chartView);

		mandatoryBorderSpacing = 0;
		borderSpacing = chartView.getResources()
									.getDimension(R.dimen.axis_border_spacing);
	}

	
	public XController(ChartView chartView, TypedArray attrs) {
		super(chartView);

        mandatoryBorderSpacing = 0;
		borderSpacing = attrs.getDimension( R.styleable.ChartAttrs_chart_axisBorderSpacing,
                borderSpacing);
	}



    /*
     * IMPORTANT: Method's calls order is crucial. Change it (or not) carefully.
     */
    @Override
	protected void init() {

        // Set the vertical display coordinate
        mLabelVerCoord = chartView.chartBottom;
        if(labelsPositioning == LabelPosition.INSIDE)
            mLabelVerCoord -= distLabelToAxis;

        super.init();

        mLastLabelWidth = chartView.style.labelPaint.measureText(labels.get(nLabels - 1));

        // In case of mandatory border spacing (ex. BarChart)
        if(mandatoryBorderSpacing == 1)
            mandatoryBorderSpacing = (getInnerChartRight() - chartView.getInnerChartLeft()
                    - borderSpacing * 2) / nLabels / 2;

        labelsPos = calcLabelsPos();
    }
	
	
	
	/*
	 * ---------
	 *  Getters
	 * ---------
	 */
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner right side of the chart
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



    /*
     * ----------------
     * Abstract Methods
     * ----------------
     */

    @Override
    protected ArrayList<Float> calcLabelsPos() {

        ArrayList<Float> result = new ArrayList<Float>(nLabels);

        if(nLabels == 1)
            result.add(chartView.getInnerChartLeft() + (getInnerChartRight() - chartView.getInnerChartLeft())/2);
        else{
            final float screenStep =
                    (getInnerChartRight()
                            - chartView.getInnerChartLeft()
                            - chartView.style.axisThickness/2
                            //if 0 first label will be right at the beginning of the axis
                            - borderSpacing * 2
                            - mandatoryBorderSpacing * 2 )
                            / (nLabels - 1);

            float pos = chartView.getInnerChartLeft() + borderSpacing + mandatoryBorderSpacing;
            while(pos <= chartView.chartRight - borderSpacing - mandatoryBorderSpacing){
                result.add(pos);
                pos += screenStep;
            }
        }

        return result;
    }


    /**
     * Method called from onDraw method to draw XController data
     * @param canvas - Canvas to use while drawing the data.
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