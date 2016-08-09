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

import java.util.Collections;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Rect;


/**
 * Class responsible to control vertical measures, positions, yadda yadda. 
 * If the drawing is requested it will also take care of it.
 */
public class YController extends AxisController{



    public YController(ChartView chartView) {
        super(chartView);
	}
	
	
	public YController(ChartView chartView, TypedArray attrs) {
        super(chartView, attrs);
	}



    void measure(){

        chartView.setInnerChartLeft(measureInnerChartLeft());
        chartView.setInnerChartBottom(measureInnerChartBottom());
    }


    /*
     * IMPORTANT: Method's calls order is crucial. Change it (or not) carefully.
     */
    @Override
	void dispose(){
        super.dispose();

        defineMandatoryBorderSpacing(chartView.getInnerChartTop(), chartView.getChartBottom());
        defineLabelsPosition(chartView.getInnerChartTop(), chartView.getInnerChartBottom());
    }



    /**
     * Based in a (real) value returns the associated screen point.
     *
     * @param value - Value to be parsed in display coordinate
     * @return Chart's coordinate
     */
    float parsePos(int index, double value){

        if(handleValues)
            return (float) ( chartView.horController.axisPosition -
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
    public float measureInnerChartLeft(){

        float result = 0;
        if(hasAxis)
            result += chartView.style.axisThickness/2;

        result += chartView.getChartLeft();

        if (hasAxis)
            result += chartView.style.axisThickness / 2;

        if (labelsPositioning == LabelPosition.OUTSIDE) {
            float aux;
            float maxLabelLength = 0;
            for (String label : labels)
                if ((aux = chartView.style.labelsPaint.measureText(label)) > maxLabelLength)
                    maxLabelLength = aux;
            result += maxLabelLength + distLabelToAxis;
        }
        return result;
    }


    /**
     * Measure the necessary padding from the chart bottom border defining the
     * coordinate of the inner chart bottom border. Inner Chart refers only to the
     * area where chart data will be draw, excluding labels, axis, etc.
     *
     * @return Coordinate of the inner bottom side of the chart
     */
    public float measureInnerChartBottom(){

        if (labelsPositioning != LabelPosition.NONE && borderSpacing < getLabelsMaxHeight()/2)
            return chartView.getChartBottom() - getLabelsMaxHeight()/2;
        return chartView.getChartBottom();
    }



    /**
     * Get the horizontal position of axis based on the chart left bottom.
     *
     */
    @Override
    protected void defineAxisPosition(){

        axisPosition = chartView.getInnerChartLeft();
        if(hasAxis)
            axisPosition -= chartView.style.axisThickness/2;
    }


    /**
     * Get the horizontal position of labels based on the axis position.
     *
     */
    @Override
    protected void defineStaticLabelsPosition(){

        labelsStaticPos = axisPosition;

        if(labelsPositioning == LabelPosition.INSIDE) {
            labelsStaticPos += distLabelToAxis;
            if(hasAxis)
                labelsStaticPos +=  chartView.style.axisThickness/2;

        }else if(labelsPositioning == LabelPosition.OUTSIDE) {
            labelsStaticPos -= distLabelToAxis;
            if(hasAxis)
                labelsStaticPos -=  chartView.style.axisThickness/2;
        }
    }


    @Override
    void defineLabelsPosition(float innerStart, float innerEnd) {
        super.defineLabelsPosition(innerStart, innerEnd);
        Collections.reverse(labelsPos);
    }


    /**
     * Get the height of a label.
     *
     * @param text   Label to measure
     * @return   height of label
     */
    private int getLabelHeight(String text){
        final Rect rect = new Rect();
        chartView.style.labelsPaint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


    @Override
	protected void draw(Canvas canvas){
		
		if(hasAxis) {
            // Draw axis line
            float bottom = chartView.horController.axisPosition;
            if (chartView.horController.hasAxis)
                bottom += chartView.style.axisThickness / 2;

            canvas.drawLine(axisPosition,
                    chartView.getChartTop(),
                    axisPosition,
                    bottom,
                    chartView.style.chartPaint);
        }

		if(labelsPositioning != LabelPosition.NONE){

            chartView.style.labelsPaint.setTextAlign(
                    (labelsPositioning == LabelPosition.OUTSIDE)
                            ? Align.RIGHT : Align.LEFT);

			// Draw labels
			for(int i = 0; i < nLabels; i++){
				canvas.drawText(labels.get(i),
                        labelsStaticPos,
                        labelsPos.get(i) + getLabelHeight(labels.get(i))/2,
                        chartView.style.labelsPaint);
			}
		}
	}
	
}