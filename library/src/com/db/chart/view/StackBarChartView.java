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

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

/**
 * Implements a StackBar chart extending {@link BarChartView}
 */
public class StackBarChartView extends BarChartView {
	
	
	/** Whether to calculate max value or not **/
	private boolean mCalcMaxValue;
	
	
	
	public StackBarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCalcMaxValue = true;
	}
	
	
	public StackBarChartView(Context context) {
		super(context);
		mCalcMaxValue = true;
	}
	
	
	
	/**
	 * Method responsible to draw bars with the parsed screen points.
	 * @param canvas
	 *   The canvas to draw on.
	 * @param screenPoints
	 *   The parsed screen points ready to be used/drawn.
	 */
	@Override
	public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {
		
		for (int i = 0; i < data.get(0).size(); i++) {
			
			// If bar needs background
			if(style.hasBarBackground)
				drawBarBackground(canvas, data.get(0).getEntry(i).getX() - mBarWidth/2);	
			
			// Vertical offset to keep drawing bars on top of the others
			float verticalOffset = 0;
			// Bottom of the next bar to be drawn
			float nextBottomY = this.getInnerChartBottom();
			
			for(int j = 0; j < data.size(); j++){
				
				BarSet barSet = (BarSet) data.get(j);
				style.barPaint.setColor(barSet.getColor());
				Bar bar = (Bar) barSet.getEntry(i);
				style.barPaint.setColor(bar.getColor());
				
				// Distance from bottom to top of the bar
				float dist = this.getInnerChartBottom() - bar.getY();
				
				// Draw bar
				if(j == 0){ // Bottom set
					canvas.drawRoundRect(new RectF((int) (bar.getX() - mBarWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
									(int) (bar.getX() + mBarWidth/2),
											(int) (nextBottomY)), 
									style.cornerRadius,
										style.cornerRadius,
											style.barPaint);
					
					if(data.size() != 1)
						// Fill top corners of bar
						canvas.drawRect(new Rect((int) (bar.getX() - mBarWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
								(int) (bar.getX() + mBarWidth/2),
									(int) (this.getInnerChartBottom() - (dist + verticalOffset) + style.cornerRadius)),
								style.barPaint);
					
				}else if(j == data.size()-1){ // Top set
					canvas.drawRoundRect(new RectF((int) (bar.getX() - mBarWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
									(int) (bar.getX() + mBarWidth/2),
											(int) (nextBottomY)), 
									style.cornerRadius,
										style.cornerRadius,
											style.barPaint);
					// Fill bottom corners of bar
					canvas.drawRect(new Rect((int) (bar.getX() - mBarWidth/2), 
							(int) (nextBottomY - style.cornerRadius), 
								(int) (bar.getX() + mBarWidth/2),
									(int) (nextBottomY)),
								style.barPaint);
				
				}else{ // Middle sets
					canvas.drawRect(new Rect((int) (bar.getX() - mBarWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
									(int) (bar.getX() + mBarWidth/2),
											(int) (nextBottomY)),
											style.barPaint);
				}
					
				nextBottomY = this.getInnerChartBottom() - (dist + verticalOffset);
				
				// In case bar is still hidden
				if(dist != 0)
					// Sum 2 to compensate the loss of precision in float
					verticalOffset += dist + 2;
			}
		}
	}
	
	
	
	/**
	 * Calculates Bar width based on the distance of two horizontal labels
	 * @param x0
	 * @param x1
	 */
	private void calculateBarsWidth(float x0, float x1) {
		mBarWidth = x1 - x0 - style.barSpacing;
	}

	
	
	@Override
	public void onPreDrawChart(ArrayList<ChartSet> data){
		
		// Doing calculations here to avoid doing several times while drawing
		// in case of animation
		calculateBarsWidth(data.get(0).getEntry(0).getX(), 
				data.get(0).getEntry(1).getX());
	}
	
	
	
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		
		// Define regions
		final ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>();
		for(int i = 0; i < data.size(); i++)
			result.add(new ArrayList<Region>());
		
		for (int i = 0; i < data.get(0).size(); i++) {
			
			// Vertical offset to keep drawing bars on top of the others
			float verticalOffset = 0;
			// Bottom of the next bar to be drawn
			float nextBottomY = this.getInnerChartBottom();
			
			for(int j = 0; j < data.size(); j++){
				
				BarSet barSet = (BarSet) data.get(j);
				Bar bar = (Bar) barSet.getEntry(i);
				
				// Distance from bottom to top of the bar
				float dist = this.getInnerChartBottom() - bar.getY();
				
				result.get(j).add(new Region(
						(int) (bar.getX() - mBarWidth/2), 
						(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
							(int) (bar.getX() + mBarWidth/2), 
								(int)(nextBottomY)));
				
				nextBottomY = this.getInnerChartBottom() - (dist + verticalOffset);
				// Sum X to compensate the loss of precision in float
				verticalOffset += dist + 2;
			}
		}
		
		return result;
	}
	
	
	
	private void calculateMaxStackBarValue() {
		
		int maxStackValue = 0;
		for (int i = 0; i < data.get(0).size(); i++) {
			
			float stackValue = 0;
			for(int j = 0; j < data.size(); j++){
				
				final BarSet barSet = (BarSet) data.get(j);
				Bar bar = (Bar) barSet.getEntry(i);
				stackValue += bar.getValue();
			}
			
			if(maxStackValue < (int) Math.ceil(stackValue))
				maxStackValue = (int) Math.ceil(stackValue);
		}
		
		while(maxStackValue % this.getStep() != 0)
			maxStackValue += 1;
		
		super.setMaxAxisValue(maxStackValue, this.getStep());
	}
	
	
	
	/*
	 * --------------------------------
	 * Overriden methods from ChartView
	 * --------------------------------
	 */
	
	@Override
	public ChartView setMaxAxisValue(int maxAxisValue, int step){
		mCalcMaxValue = false;
		return super.setMaxAxisValue(maxAxisValue, step);
	}

	
	@Override
	public void show(){
		
		if(mCalcMaxValue)
			calculateMaxStackBarValue();
		super.show();
	}


}