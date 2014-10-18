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

import java.util.ArrayList;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.db.williamchart.R;


/**
 * Class responsible to control horizontal measures, positions, yadda yadda.  
 * If the drawing is requested it will also take care of it.
 */
public class XController{
	
	
	private static final String TAG = "com.db.chart.view.XController";
	
	
	public static enum LabelPosition {
		NONE, OUTSIDE, INSIDE
    }
	
	
	/** Distance between label and axis. */
	private int mDistLabelToAxis;
	
	
	/** ChartView object */ 
	private ChartView mChartView;
	
	
	/** Vertical coordinate where label will be drawn */
	private int mLabelVerCoord;
	
	
	/** Position of labels in chart */
	protected ArrayList<Float> labelsPos;

	
	/** Horizontal border spacing for labels */
	protected float borderSpacing;


	/** Mandatory horizontal border when necessary (ex: BarCharts) */
	protected float mandatoryBorderSpacing;

	
	/** Whether the chart has X Axis or not */
	protected boolean hasAxis;
	
	
	/** none/inside/outside */
	protected LabelPosition labelsPositioning;
	

	
	
	public XController(ChartView chartView) {
		
		mChartView = chartView;
		
		//Initialize variables and set defaults
		mDistLabelToAxis = (int) (mChartView.getResources()
				.getDimension(R.dimen.axis_dist_from_label));
		mandatoryBorderSpacing = 0;
		hasAxis = true;
		labelsPos = new ArrayList<Float>();
		labelsPositioning = LabelPosition.OUTSIDE;
		borderSpacing = mChartView.getResources()
									.getDimension(R.dimen.axis_border_spacing);	
		
	}

	
	public XController(ChartView chartView, TypedArray attrs) {
		this(chartView);
		
		borderSpacing = attrs.getDimension(
							R.styleable.ChartAttrs_chart_axisBorderSpacing, 
								borderSpacing);
	}




	protected void init() {

		// Set the vertical display coordinate
		mLabelVerCoord = mChartView.chartBottom;
		if(labelsPositioning == LabelPosition.INSIDE)
			mLabelVerCoord -= mDistLabelToAxis;
		
		// In case of mandatory border spacing (ex. BarChart)
		if(mandatoryBorderSpacing == 1)
			mandatoryBorderSpacing = 
				(getInnerChartRight() - mChartView.getInnerChartLeft() - borderSpacing * 2) 
					/ mChartView.data.get(0).size() / 2;
		
		labelsPos = calcLabelsPos(mChartView.data.get(0).size());
	}

	
	

	/**
	 * Get labels position having into account the horizontal padding of text size.
	 * @param nLabels- number of labels to display
	 */
	private ArrayList<Float> calcLabelsPos(int nLabels) {
		
		final ArrayList<Float> result = new ArrayList<Float>();
		
		if(nLabels == 1)
			result.add(mChartView.getInnerChartLeft() + (getInnerChartRight() - mChartView.getInnerChartLeft())/2);
		else{
			final float screenStep = 
					(getInnerChartRight()
						- mChartView.getInnerChartLeft() 
						- mChartView.style.axisThickness/2
						//if 0 first label will be right at the beginning of the axis
						- borderSpacing * 2
						- mandatoryBorderSpacing * 2 ) 
					/ (nLabels-1);

			float pos = mChartView.getInnerChartLeft() + borderSpacing + mandatoryBorderSpacing;
			while(pos <= mChartView.chartRight - borderSpacing - mandatoryBorderSpacing){
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
	protected void draw(Canvas canvas){
		
		// Draw axis
		if(hasAxis)
			canvas.drawLine(mChartView.getInnerChartLeft(),
								getAxisVerticalPosition(),
									getInnerChartRight(),
										getAxisVerticalPosition(),
											mChartView.style.chartPaint);
		
		// Draw labels
		if(labelsPositioning != LabelPosition.NONE){
			
			mChartView.style.labelPaint.setTextAlign(Align.CENTER);
			for(int i = 0; i < mChartView.data.get(0).size(); i++){
				canvas.drawText(mChartView.data.get(0).getLabel(i), 
								labelsPos.get(i), 
									mLabelVerCoord, 
										mChartView.style.labelPaint);
			
			}
		}
	}	
	
	
	
	
	/*
	 * -----------------------
	 * 		  Getters
	 * -----------------------
	 */

	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner right side of the chart
	 */
	public float getInnerChartRight(){
		return mChartView.chartRight 
				- mChartView.style.labelPaint
								.measureText(mChartView.data.get(0).
												getLabel(mChartView.data.get(0)
																.size()-1))/2;
	}

	
	protected float getAxisVerticalPosition(){
		
		if(labelsPositioning != LabelPosition.OUTSIDE)
			return mChartView.chartBottom;
		
		return mChartView.chartBottom 
					- mChartView.style.getTextHeightBounds(mChartView.data.get(0).getLabel(0))
						- mDistLabelToAxis; 
	}
	
}
