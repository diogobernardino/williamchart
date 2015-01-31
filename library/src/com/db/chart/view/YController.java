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
 * Class responsible to control vertical measures, positions, yadda yadda. 
 * If the drawing is requested it will also take care of it.
 */
public class YController extends AxisController{


	/** Spacing for top label */
	protected float topSpacing;

	
	
	public YController(ChartView chartView) {
        super(chartView);
		
		// DEFAULTS
		topSpacing = chartView.getResources().getDimension(R.dimen.axis_top_spacing);
        isDynamic = true;
	}
	
	
	public YController(ChartView chartView, TypedArray attrs) {
        super(chartView, attrs);

        // DEFAULTS
		topSpacing = attrs.getDimension(R.styleable.ChartAttrs_chart_axisTopSpacing, topSpacing);
        isDynamic = true;
	}



    /*
     * IMPORTANT: Method's calls order is crucial. Change it (or not) carefully.
     */
    @Override
	protected void init(){

        if(labelsPositioning == LabelPosition.INSIDE)
            distLabelToAxis *= -1;

        super.init();

        axisHorPosition = calcAxisHorizontalPosition();
        labelsPos = calcLabelsPos();
    }



    /**
     * Calculates the starting X point of the axis
     */
    protected float calcAxisHorizontalPosition(){

        // Just in case Y labels need to be drawn
        if(labelsPositioning == LabelPosition.OUTSIDE){
            float aux;
            float maxLabelLength = 0;
            for(String label : labels)
                if((aux = chartView.style.labelPaint.measureText(label)) > maxLabelLength)
                    maxLabelLength = aux;
            return chartView.chartLeft + maxLabelLength + distLabelToAxis;
        }else
            return chartView.chartLeft;
    }



	/*
	 * --------
	 * Getters
	 * --------
	 */

    /**
     * Differentiates the inner left side of the chart depending
     * if axis Y is drawn or not.
     * If drawing axis give it gives half of the line thickness as margin.
     * Inner Chart refers only to the area where chart data will be draw,
     * excluding labels, axis, etc.
     * @return position of the inner left side of the chart
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
     * @return position of the inner left side of the chart
     */
    public float getInnerChartBottom(){
        return chartView.horController.getAxisVerticalPosition() - chartView.style.axisThickness/2;
    }



    /*
	 * ----------------
	 * Abstract Methods
	 * ----------------
	 */

	/**
	 * Get labels position having into account the vertical padding of text size.
     */
    @Override
	protected ArrayList<Float> calcLabelsPos() {
		
		ArrayList<Float> result = new ArrayList<Float>(nLabels);
		
		screenStep = (chartView.horController.getAxisVerticalPosition()
                - chartView.chartTop - topSpacing) / (nLabels - 1);
		
		float currPos = chartView.horController.getAxisVerticalPosition();
		for(int i = 0; i < nLabels; i++){
			result.add(currPos);
			currPos -= screenStep;
		}
		
		return result;
	}


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