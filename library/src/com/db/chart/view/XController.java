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

package com.db.chart.view;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;


/**
 * Class responsible to control horizontal measures, positions, yadda yadda.  
 * If the drawing is requested it will also take care of it.
 */
public class XController extends AxisController{


	/** Width of last label of sets */
	private float mLastLabelWidth;



	public XController(ChartView chartView) {
		super(chartView);
	}


	public XController(ChartView chartView, TypedArray attrs) {
		super(chartView, attrs);
	}



    /*
     * IMPORTANT: Method's order is crucial. Change it (or not) carefully.
     */
	void init() {

        defineLabels();

        // To manage horizontal width of the last axis label
        if (nLabels > 0){ // to fix possible crash on trying to access label by index -1.
        	mLastLabelWidth = chartView.style.labelsPaint.measureText(labels.get(nLabels - 1));
        } else {
        	mLastLabelWidth = 0;
        }

        defineMandatoryBorderSpacing(chartView.getInnerChartLeft(), chartView.getChartRight());
        defineLabelsPos(chartView.getInnerChartLeft(), getInnerChartRight());
    }



    /**
     * Based in a (real) value returns the associated screen point.
     *
     * @param value   Value to be parsed in display coordinate
     * @return Display's coordinate
     */
    float parsePos(int index, double value){

        if(handleValues)
            return (float) (chartView.getInnerChartLeft() +
                (((value - minLabelValue) * screenStep) / (labelsValues.get(1) - minLabelValue)));
        else
            return labelsPos.get(index);
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
        if(labelsPositioning != LabelPosition.NONE && borderSpacing + mandatoryBorderSpacing < mLastLabelWidth / 2)
            rightBorder = mLastLabelWidth/2 - (borderSpacing + mandatoryBorderSpacing);

        return chartView.getChartRight() - rightBorder;
    }


    /**
     * Get the vertical position of axis.
     *
     * @return
     */
    float getAxisVerticalPosition(){

        if(axisPosition == 0) {

            axisPosition = chartView.getChartBottom();

            if (hasAxis)
                axisPosition -= chartView.style.axisThickness / 2;

            if (labelsPositioning == LabelPosition.OUTSIDE)
                axisPosition -= getLabelHeight() + distLabelToAxis;
        }
        return axisPosition;
    }


    /**
     * Get the vertical position of labels.
     *
     * @return vertical labels coordinate
     */
    private float getLabelsVerticalPosition(){

        float result = chartView.getChartBottom();
        if(labelsPositioning == LabelPosition.INSIDE) { // Labels sit inside of chart
            result -= distLabelToAxis;
            if(hasAxis)
                result -=  chartView.style.axisThickness;
        }if (labelsPositioning == LabelPosition.OUTSIDE){ // Labels sit outside of chart
            result -= chartView.style.labelsPaint.descent();
        }
        return result;
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
            chartView.style.labelsPaint.setTextAlign(Align.CENTER);

            final float labelsVerticalCoord = getLabelsVerticalPosition();
            for(int i = 0; i < nLabels; i++){
                canvas.drawText(labels.get(i),
                        labelsPos.get(i),
                        labelsVerticalCoord,
                        chartView.style.labelsPaint);

            }
        }
    }

}
