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
import android.graphics.Rect;
import android.util.Log;

import com.db.williamchart.R;
import com.db.chart.exception.ChartException;
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;

/**
 * Class responsible to control vertical measures, positions, yadda yadda. 
 * If the drawing is requested it will also take care of it.
 */
class YController{
	
	
	private static final String TAG = "com.db.chart.view.YController";
	
	
	/** Default step between labels */
	private static final int DEFAULT_STEP = 1;
	
	
	/** Distance between axis X and label X */
	private static int sDistFromLabelX;
	
	
	/** Distance between axis Y and label Y */
	private static int sDistFromLabel;
	
	
	/** Chartview object */
	private ChartView mChartView;

	
	/** Vertical labels */
	private ArrayList<Integer>mLabels;
	
	
	/** Labels position */
	private ArrayList<Float>mLabelsPos;
	
	
	/** Frame height available to draw the chart */
	private int mFrameHeight;
	
	
	/** Range of Y values from 0 to mMaxValue */
	private int mMaxLabelValue;
	
	
	/** Step between labels */
	protected int step;

	
	/** Starting X point of the axis */
	private float mAxisHorPosition;
	
	
	/** Starting Y point of the axis */
	private float mAxisBottom;
	
	
	/** Screen step between labels */
	private float mScreenStep;
	
	
	/** Spacing for top label */
	private float mVerTopSpacing;

	
	/** Default system top padding while drawing text */
	private float mTextTopPadding;

	
	/** Should the axis be drawn or only measures must be calculated */
	protected boolean isDrawing;



	public YController(ChartView chartView) {
		
		mChartView = chartView;
		
		//Set defaults
		step = DEFAULT_STEP;
		mVerTopSpacing = mChartView.getResources()
									.getDimension(R.dimen.axis_top_spacing);;
		mAxisHorPosition = 0;
		mMaxLabelValue = 0;
		isDrawing = false;
	}
	
	
	
	public YController(ChartView chartView, TypedArray attrs) {
		this(chartView);
		mVerTopSpacing = attrs.getDimension(
								R.styleable.ChartAttrs_chart_axisTopSpacing, 
									mVerTopSpacing);
	}



	protected void init() {
		
		// Get system text top padding to set the correct position when drawing labels
		final Rect textBounds = new Rect();
		mChartView.style.labelPaint
							.getTextBounds(mChartView.data.get(0).getLabel(0), 
											0, 
												1, 
													textBounds);
		mTextTopPadding = mChartView.style.fontSize - textBounds.height();
		
		sDistFromLabelX = (int) (mChartView.getResources()
									.getDimension(R.dimen.axis_dist_from_label));
		sDistFromLabel= (int) mChartView.getResources()
									.getDimension(R.dimen.axis_dist_from_label);
		
		mAxisBottom = mChartView.chartBottom 
				- textBounds.height() 
					- sDistFromLabelX; 
		mFrameHeight = (int) mAxisBottom - mChartView.chartTop;
		
		mLabels = calcLabels();
		mAxisHorPosition = calcAxisHorizontalPosition();
		mLabelsPos = calcLabelsPos(mChartView.data.get(0).size());
		
		if(mMaxLabelValue < calcMaxY()){
			try{
				throw new ChartException("MaxAxisValue defined < than current max set value");
			}catch(ChartException e){
				Log.e(TAG, "", e);
				System.exit(1);
			}	
		}
	}

	
	
	/**
	 * Calculates the max Y value.
	 * @return max Y value.
	 */
	private double  calcMaxY() {
		double max = 0;
		for(ChartSet s: mChartView.data){
			for(ChartEntry e: s.getEntries()){
				if(e.getValue() >= max)
					max = e.getValue();
			}
		}
		return max;
	}
	
	
	
	/**
	 * Get labels based on the maximum value displayed
	 * @return result
	 */
	private ArrayList<Integer> calcLabels(){
		
		final double maxY;
		if(mMaxLabelValue == 0){
			maxY = calcMaxY();
			
			//Get the highest label based in maxY and step
			mMaxLabelValue = (int) Math.ceil(maxY);
			while(mMaxLabelValue % step != 0)
				mMaxLabelValue += 1;
		}
		
		
		final ArrayList<Integer> result = new ArrayList<Integer>();
		int aux = step;
		while(aux <= mMaxLabelValue){
			result.add(aux);
			aux += step;
		}

		//Set max Y axis label in case isn't already there
		if(result.get(result.size()-1) < mMaxLabelValue)
			result.add(mMaxLabelValue);
		
		return result;
		
	}
	
	
	
	/**
	 * Get labels position having into account the vertical padding of text size.
	 * @param nLabels
	 */
	private ArrayList<Float> calcLabelsPos(int nLabels) {
		final ArrayList<Float> result = new ArrayList<Float>();
		
		mScreenStep = (float) (mFrameHeight - mVerTopSpacing) / mLabels.size();
		float currPos = (float) (mAxisBottom - mScreenStep);
		for(int i = 0; i < mLabels.size(); i++){
			result.add(currPos);
			currPos -= mScreenStep;
		}
		return result;
	}
	
	
	
	/**
	 * Calculates the starting X point of the axis
	 */
	protected float calcAxisHorizontalPosition(){
		
		if(isDrawing){ // In case axis Y needs to be drawn
			float maxLenghtLabel = 0;
			float aux = 0;
			for(int i = 0; i < mLabels.size(); i++){
				aux = mChartView.style.labelPaint
								.measureText(Integer.toString(mLabels.get(i)));
				if(aux > maxLenghtLabel){
					maxLenghtLabel = aux;
				}
			}
			return mChartView.chartLeft + maxLenghtLabel + sDistFromLabel;
			
		}else{
			return mChartView.chartLeft 
					+ mChartView.style.labelPaint
							.measureText(mChartView.data.get(0).getLabel(0))/2;
		}
	}

	
	
	/**
	 * Based in a (real) value returns the associated screen point
	 * @param value
	 * @return point
	 */
	protected float parseYPos(double value){
		return (float) ( mAxisBottom - ( ( value * mScreenStep) / mLabels.get(0)));
	}
	
	
	
	/**
	 * Method called from onDraw method to draw YController data
	 * @param canvas - Canvas to use while drawing the data.
	 */
	protected void draw(Canvas canvas){
		if(isDrawing){
			
			//TODO isto left fica mais ou menos fixe
			mChartView.style.labelPaint.setTextAlign(Align.LEFT);

			// Draw labels
			for(int i = 0; i < mLabels.size(); i++){
				canvas.drawText(Integer.toString(mLabels.get(i)), 
								mChartView.chartLeft, 
									(float) mLabelsPos.get(i) + mTextTopPadding, 
										mChartView.style.labelPaint);
			}
			
			// Draw axis line
			canvas.drawLine(mAxisHorPosition, 
								mChartView.chartTop, 
									mAxisHorPosition, 
										mAxisBottom + mChartView.style.axisThickness/2, 
											mChartView.style.chartPaint);
		}
	}	

	
	
	/**
	 * Reset values in case new data will be added to the chart
	 */
	protected void reset() {
		mMaxLabelValue = 0;
	}
	
	
	
	/*
	 * Getters
	 * 
	 */
	
	
	public ArrayList<Float> getLabelsPosition(){
		return mLabelsPos;
	}
	
	
	/**
	 * Differentiates the inner left side of the chart depending 
	 * if axis Y is drawn or not.
	 * If drawing axis give it gives half of the line thickness as margin.  
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner left side of the chart
	 */
	public float getInnerChartLeft(){
		if(isDrawing)
			return mAxisHorPosition + mChartView.style.axisThickness/2;
		else
			return mAxisHorPosition;
	}
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner left side of the chart
	 */
	public float getInnerChartBottom(){
		return mAxisBottom - mChartView.style.axisThickness/2;
	}
	
	
	/**
	 * @return Spacing between top of the chart and the first label
	 */
	public float getTopSpacing(){
		return mVerTopSpacing;
	}
	
	
	public float getMaxAxisValue(){
		return mMaxLabelValue;
	}
	
	
	
	/*
	 * Setters
	 * 
	 */
	
	
	/**
	 * @param spacing - Spacing between top of the chart and the first label
	 */
	public void setTopSpacing(float spacing){
		mVerTopSpacing = spacing;
	}
	
	
	/**
	 * @param bool - if Y axis must be, or not, drawn
	 */
	public void setLabels(boolean bool){
		isDrawing = bool;
	}
	
	
	/**
	 * A step is seen as the step to be defined between 2 labels. 
	 * As an example a step of 2 with a max label value of 6 will end up 
	 * with {0, 2, 4, 6} as labels.
	 * @param step - (real) value distance from every label
	 */
	public void setStep(int s) {
		step = s;
	}
	
	
	/**
	 * A step is seen as the step to be defined between 2 labels. 
	 * As an example a step of 2 with a maxAxisValue of 6 will end up 
	 * with {0, 2, 4, 6} as labels.
	 * @param maxAxisValue - the maximum value that Y axis will have as a label
	 * @param step - step - (real) value distance from every label
	 */
	public void setMaxAxisValue(int maxAxisValue, int s) {
		mMaxLabelValue = maxAxisValue;
		step = s;
	}

}

