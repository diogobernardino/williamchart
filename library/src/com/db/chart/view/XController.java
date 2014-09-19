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
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.widget.RelativeLayout.LayoutParams;

import com.db.williamchart.R;


/**
 * Class responsible to control horizontal measures, positions, yadda yadda.  
 * If the drawing is requested it will also take care of it.
 */
class XController{
	
	
	/** Distance between label and axis. */
	private int mDistFromLabelToAxis;
	
	
	/** ChartView object */ 
	private ChartView mChartView;
	
	
	/** Distance between the top and the first vertical label */
	private float mInnerChartLeft;
	
	
	/** Default system top padding while drawing text */
	private float mTextTopPadding;


	/** Starting Y point of the axis */
	private float mAxisBottom;
	
	
	
	/** Position of labels in chart */
	protected ArrayList<Float> labelsPos;

	
	/** Horizontal border spacing for labels */
	protected float borderSpacing;


	/** Mandatory horizontal border when necessary (ex: BarCharts) */
	protected float mandatoryBorderSpacing;

	
	/** Whether the chart has X Axis or not */
	protected boolean hasAxis;
	

	
	
	public XController(ChartView chartView) {
		
		mChartView = chartView;
		
		//Initialize variables and set defaults
		labelsPos = new ArrayList<Float>();
		mandatoryBorderSpacing = 0;
		mChartView.setLayoutParams(
						new LayoutParams(LayoutParams.MATCH_PARENT, 
											LayoutParams.WRAP_CONTENT));
		borderSpacing = mChartView.getResources()
									.getDimension(R.dimen.axis_border_spacing);	
	}

	
	public XController(ChartView chartView, TypedArray attrs) {
		this(chartView);
		
		hasAxis = attrs.getBoolean( R.styleable.ChartAttrs_chart_axisX, true);
		borderSpacing = attrs.getDimension(
							R.styleable.ChartAttrs_chart_axisBorderSpacing, 
								borderSpacing);
	}




	protected void init(float innerChartLeft) {
		
		//Get system text top padding
		final Rect textBounds = new Rect();
		mChartView.style.labelPaint
							.getTextBounds(mChartView.data.get(0).getLabel(0), 
											0, 
												1, 
													textBounds);
		mTextTopPadding = (mChartView.style.fontSize - textBounds.height());
		
		mDistFromLabelToAxis = 
				(int) (mChartView.getResources()
							.getDimension(R.dimen.axis_dist_from_label));
		
		mAxisBottom = mChartView.chartBottom 
				- (mChartView.style.fontSize - mTextTopPadding) 
					- mDistFromLabelToAxis;
		
		mInnerChartLeft = innerChartLeft;

		// In case of mandatory border spacing (ex. BarChart)
		if(mandatoryBorderSpacing == 1)
			mandatoryBorderSpacing = 
				(getInnerChartRight() - mInnerChartLeft - borderSpacing * 2) 
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
			result.add(mInnerChartLeft + (getInnerChartRight() - mInnerChartLeft)/2);
		else{
			final float screenStep = 
					(getInnerChartRight()
						- mInnerChartLeft 
						- mChartView.style.axisThickness/2
						//if 0 first label will be right at the beginning of the axis
						- borderSpacing * 2
						- mandatoryBorderSpacing * 2 ) 
					/ (nLabels-1);

			float pos = mInnerChartLeft + borderSpacing + mandatoryBorderSpacing;
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
		
		mChartView.style.labelPaint.setTextAlign(Align.CENTER);
		
		// Draw axis
		if(hasAxis)
			canvas.drawLine(mInnerChartLeft, 
								mAxisBottom, 
									getInnerChartRight(), 
										mAxisBottom, 
											mChartView.style.chartPaint);
		
		// Draw labels
		for(int i = 0; i < mChartView.data.get(0).size(); i++){
			canvas.drawText(mChartView.data.get(0).getLabel(i), 
								labelsPos.get(i), 
									mChartView.chartBottom, 
										mChartView.style.labelPaint);
			
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
												getLabel(mChartView.data.get(0).
															getEntries().size()-1))/2;
	}

	
}
