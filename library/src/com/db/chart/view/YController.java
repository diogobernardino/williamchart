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

import java.util.Collections;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;



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



    /*
     * IMPORTANT: Method's calls order is crucial. Change it (or not) carefully.
     */
	protected void init(){

        if(labelsPositioning == LabelPosition.INSIDE)
            distLabelToAxis *= -1;

        defineLabels();
        defineMandatoryBorderSpacing(chartView.getInnerChartTop(), getInnerChartBottom());
        defineAxisHorizontalPosition();
        defineLabelsPos(chartView.getInnerChartTop(), getInnerChartBottom());
    }



    /**
     * Calculates the starting X point of the axis
     */
    protected void defineAxisHorizontalPosition(){

        // Just in case Y labels need to be drawn
        if(labelsPositioning == LabelPosition.OUTSIDE){
            float aux;
            float maxLabelLength = 0;
            for(String label : labels)
                if((aux = chartView.style.labelPaint.measureText(label)) > maxLabelLength)
                    maxLabelLength = aux;
            axisHorPosition = chartView.chartLeft + maxLabelLength + distLabelToAxis;
        }else
            axisHorPosition = chartView.chartLeft;
    }



    @Override
    protected void defineLabelsPos(float innerStart, float innerEnd) {
        super.defineLabelsPos(innerStart, innerEnd);
        Collections.reverse(labelsPos);
    }



    /**
     * Based in a (real) value returns the associated screen point.
     *
     * @param value - Value to be parsed in display coordinate
     * @return Chart's coordinate
     */
    protected float parsePos(int index, double value){

        if(handleValues)
            return (float) ( chartView.horController.getAxisVerticalPosition() -
                (((value - minLabelValue) * screenStep) / (labelsValues.get(1) - minLabelValue)));
        else
            return labelsPos.get(index);
    }



	/*
	 * --------
	 * Getters
	 * --------
	 */

    /**
     * Differentiates the inner left side of the chart depending if axis Y is drawn or not.
     * If drawing axis give it gives half of the line thickness as margin.
     * Inner Chart refers only to the area where chart data will be draw,
     * excluding labels, axis, etc.
     *
     * @return Position of the inner left side of the chart
     */
    public float getInnerChartLeft(){

        if(hasAxis)
            return axisHorPosition + chartView.style.axisThickness/2;
        else
            return axisHorPosition;
    }


    /**
     * Inner Chart refers only to the area where chart data will be draw,
     * excluding labels, axis, etc.
     *
     * @return Position of the inner left side of the chart
     */
    public float getInnerChartBottom(){
        return chartView.horController.getAxisVerticalPosition() - chartView.style.axisThickness/2;
    }



    /*
	 * ----------------
	 * Abstract Methods
	 * ----------------
	 */

    @Override
	protected void draw(Canvas canvas){
		
		if(hasAxis)
			// Draw axis line
			canvas.drawLine(axisHorPosition,
								chartView.chartTop,
									axisHorPosition,
										chartView.horController.getAxisVerticalPosition()
                                                + chartView.style.axisThickness/2,
											chartView.style.chartPaint);


		if(labelsPositioning != LabelPosition.NONE){
			
			chartView.style.labelPaint.setTextAlign(
					(labelsPositioning == LabelPosition.OUTSIDE)
						? Align.RIGHT : Align.LEFT);

			// Draw labels
			for(int i = 0; i < nLabels; i++){
				canvas.drawText(labels.get(i),
									axisHorPosition - chartView.style.axisThickness/2 - distLabelToAxis,
										labelsPos.get(i) + getLabelHeight()/2,
											chartView.style.labelPaint);
			}
		}
	}
	
}