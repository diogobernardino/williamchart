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



    public XController(ChartView chartView) {
		super(chartView);
	}


	public XController(ChartView chartView, TypedArray attrs) {
		super(chartView, attrs);
	}



    void measure(){

        chartView.setInnerChartLeft(measureInnerChartLeft());
        chartView.setInnerChartRight(measureInnerChartRight());
        chartView.setInnerChartBottom(measureInnerChartBottom());
    }


    /*
     * IMPORTANT: Method's order is crucial. Change it (or not) carefully.
     */
    @Override
	void dispose() {
        super.dispose();

        defineMandatoryBorderSpacing(chartView.getInnerChartLeft(), chartView.getChartRight());
        defineLabelsPosition(chartView.getInnerChartLeft(), chartView.getInnerChartRight());
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



    /**
     * Measure the necessary padding from the chart left border defining the
     * coordinate of the inner chart left border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @return Coordinate of the inner left side of the chart
     */
    public float measureInnerChartLeft() {

        if(labelsPositioning != LabelPosition.NONE)
            return chartView.style.labelsPaint.measureText(labels.get(0)) / 2;
        else
            return 0;
    }


    /**
     * Measure the necessary padding from the chart right border defining the
     * coordinate of the inner chart right border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @return Coordinate of the inner left side of the chart
     */
    public float measureInnerChartRight(){

        // To manage horizontal width of the last axis label
        float mLastLabelWidth = 0;
        if (nLabels > 0) // to fix possible crash on trying to access label by index -1.
            mLastLabelWidth = chartView.style.labelsPaint.measureText(labels.get(nLabels - 1));

        float rightBorder = 0;
        if(labelsPositioning != LabelPosition.NONE && borderSpacing + mandatoryBorderSpacing < mLastLabelWidth / 2)
            rightBorder = mLastLabelWidth/2 - (borderSpacing + mandatoryBorderSpacing);

        return chartView.getChartRight() - rightBorder;
    }


    /**
     * Measure the necessary padding from the chart bottom border defining the
     * coordinate of the inner chart bottom border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @return Coordinate of the inner bottom side of the chart
     */
    private float measureInnerChartBottom(){

        float result = chartView.getChartBottom();

        if (hasAxis)
            result -= chartView.style.axisThickness;

        if (labelsPositioning == LabelPosition.OUTSIDE)
            result -= getLabelsMaxHeight() + distLabelToAxis;

        return result;
    }



    /**
     * Get the vertical position of axis based on the chart inner bottom.
     *
     */
    @Override
    protected void defineAxisPosition(){

        axisPosition = chartView.getInnerChartBottom();
        if (hasAxis)
            axisPosition += chartView.style.axisThickness / 2;
    }


    /**
     * Get the vertical position of labels based on the axis position.
     *
     */
    @Override
    protected void defineStaticLabelsPosition(){

        labelsStaticPos = axisPosition;

        if(labelsPositioning == LabelPosition.INSIDE) { // Labels sit inside of chart
            labelsStaticPos -= distLabelToAxis;
            labelsStaticPos -= chartView.style.labelsPaint.descent();
            if(hasAxis)
                labelsStaticPos -=  chartView.style.axisThickness/2;

        }else if (labelsPositioning == LabelPosition.OUTSIDE){ // Labels sit outside of chart
            labelsStaticPos += distLabelToAxis;
            labelsStaticPos += getLabelsMaxHeight() - chartView.style.labelsPaint.descent();
            if(hasAxis)
                labelsStaticPos +=  chartView.style.axisThickness/2;
        }
    }



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
                    axisPosition,
                    chartView.getInnerChartRight(),
                    axisPosition,
                    chartView.style.chartPaint);

        // Draw labels
        if(labelsPositioning != LabelPosition.NONE){
            chartView.style.labelsPaint.setTextAlign(Align.CENTER);

            for(int i = 0; i < nLabels; i++){
                canvas.drawText(labels.get(i),
                        labelsPos.get(i),
                        labelsStaticPos,
                        chartView.style.labelsPaint);

            }
        }
    }

}
