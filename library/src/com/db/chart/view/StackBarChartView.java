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
		
		float verticalOffset;
		float nextBottomY;
		float dist;
		int bottomSetIndex = 0;
		int topSetIndex = 0;
		float cornersFix;
		BarSet barSet;
		Bar bar;
		
		
		for (int i = 0; i < data.get(0).size(); i++) {
			
			// If bar needs background
			if(style.hasBarBackground)
				drawBarBackground(canvas, data.get(0).getEntry(i).getX() - barWidth/2);	
			
			// Vertical offset to keep drawing bars on top of the others
			verticalOffset = 0;
			// Bottom of the next bar to be drawn
			nextBottomY = this.getInnerChartBottom();
			
			// Unfortunately necessary to discover which set is the bottom and top
			bottomSetIndex = discoverBottomSet(i, data);
			topSetIndex = discoverTopSet(i, data);
			
			for(int j = 0; j < data.size(); j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				// If entry value is 0 it won't be drawn
				if(bar.getValue() <= 0)
					continue;
				
				style.barPaint.setColor(bar.getColor());
				super.handleAlpha(style.barPaint, barSet.getAlpha());
				
				// Distance from bottom to top of the bar
				dist = super.getInnerChartBottom() - bar.getY();
				
				// Draw bar
				if(j == bottomSetIndex){
					canvas.drawRoundRect(new RectF((int) (bar.getX() - barWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
									(int) (bar.getX() + barWidth/2),
											(int) (nextBottomY)), 
									style.cornerRadius,
										style.cornerRadius,
											style.barPaint);
					
					if(bottomSetIndex != topSetIndex && style.cornerRadius != 0){
						cornersFix = (nextBottomY - (this.getInnerChartBottom() - (dist + verticalOffset)))/2;
						// Fill top corners of bar
						canvas.drawRect(new Rect((int) (bar.getX() - barWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
								(int) (bar.getX() + barWidth/2),
									(int) (this.getInnerChartBottom() - (dist + verticalOffset) + cornersFix)),
								style.barPaint);
					}
					
				}else if(j == topSetIndex){
					canvas.drawRoundRect(new RectF((int) (bar.getX() - barWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
									(int) (bar.getX() + barWidth/2),
											(int) (nextBottomY)), 
									style.cornerRadius,
										style.cornerRadius,
											style.barPaint);
					// Fill bottom corners of bar
					cornersFix = (nextBottomY - (this.getInnerChartBottom() - (dist + verticalOffset)))/2;
					canvas.drawRect(new Rect((int) (bar.getX() - barWidth/2), 
							(int) (nextBottomY - cornersFix), 
								(int) (bar.getX() + barWidth/2),
									(int) (nextBottomY)),
								style.barPaint);
				
				}else{// if(j != bottomSetIndex && j != topSetIndex){ // Middle sets
					canvas.drawRect(new Rect((int) (bar.getX() - barWidth/2), 
							(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
									(int) (bar.getX() + barWidth/2),
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
	
	
	
	
	private static int discoverBottomSet(int entryIndex, ArrayList<ChartSet> data){
		
		int index;
		for(index = 0; index < data.size(); index++){
			if(data.get(index).getEntry(entryIndex).getValue() == 0)
				continue;
			break;
		}
		return index;
	}
	
	
	
	private static int discoverTopSet(int entryIndex, ArrayList<ChartSet> data){
		
		int index;
		for(index = data.size() - 1; index >= 0; index--){
			if(data.get(index).getEntry(entryIndex).getValue() == 0)
				continue;
			break;
		}
		return index;
	}

	

	
	/**
	 * Calculates Bar width based on the distance of two horizontal labels
	 * @param x0
	 * @param x1
	 */
	private void calculateBarsWidth(float x0, float x1) {
		barWidth = x1 - x0 - style.barSpacing;
	}

	
	
	@Override
	public void onPreDrawChart(ArrayList<ChartSet> data){
		
		// Doing calculations here to avoid doing several times while drawing
		// in case of animation
		if(data.get(0).size() == 1)
			barWidth = (this.getInnerChartRight() - this.getInnerChartLeft() - this.horController.borderSpacing);
		else
			calculateBarsWidth(data.get(0).getEntry(0).getX(), 
				data.get(0).getEntry(1).getX());
	}
	
	
	
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		
		final ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>();
		for(int i = 0; i < data.size(); i++)
			result.add(new ArrayList<Region>());
		
		float verticalOffset;
		float nextBottomY;
		float dist;
		BarSet barSet;
		Bar bar;
		
		for (int i = 0; i < data.get(0).size(); i++) {
			
			// Vertical offset to keep drawing bars on top of the others
			verticalOffset = 0;
			// Bottom of the next bar to be drawn
			nextBottomY = this.getInnerChartBottom();
			
			
			for(int j = 0; j < data.size(); j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				// Distance from bottom to top of the bar
				dist = this.getInnerChartBottom() - bar.getY();
				
				result.get(j).add(new Region(
						(int) (bar.getX() - barWidth/2), 
						(int) (this.getInnerChartBottom() - (dist + verticalOffset)), 
							(int) (bar.getX() + barWidth/2), 
								(int)(nextBottomY)));
				
				nextBottomY = this.getInnerChartBottom() - (dist + verticalOffset);
				// Sum X to compensate the loss of precision in float
				verticalOffset += dist + 2;
			}
		}
		
		return result;
	}
	
	
	
	private void calculateMaxStackBarValue() {
		
		float stackValue;
		BarSet barSet;
		Bar bar;
		int maxStackValue = 0;
		
		for (int i = 0; i < data.get(0).size(); i++) {
			
			stackValue = 0;
			for(int j = 0; j < data.size(); j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
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