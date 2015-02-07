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
public class StackBarChartView extends BaseStackBarChartView{
	

	public StackBarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
	}
	
	
	public StackBarChartView(Context context) {
		super(context);

        setOrientation(Orientation.VERTICAL);
        setMandatoryBorderSpacing();
	}



    /**
     * Method responsible to draw bars with the parsed screen points.
     *
     * @param canvas   The canvas to draw on
     * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet} to use
     */
	@Override
	public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {
		
		float verticalOffset;
		float nextBottomY;
		float dist;
		int bottomSetIndex;
		int topSetIndex;
		float cornersFix;
        float x;
        float y;
		BarSet barSet;
		Bar bar;
		int dataSize = data.size();
		int setSize = data.get(0).size();
		float innerChartBottom = this.getInnerChartBottom();

		for (int i = 0; i < setSize; i++) {
			
			// If bar needs background
			if(style.hasBarBackground) {
                drawBarBackground(canvas,
                        (int) (data.get(0).getEntry(i).getX() - barWidth / 2),
                        (int) this.getInnerChartTop(),
                        (int) (data.get(0).getEntry(i).getX() - barWidth / 2 + barWidth),
                        (int) this.getInnerChartBottom());
            }
			
			// Vertical offset to keep drawing bars on top of the others
			verticalOffset = 0;

			// Bottom of the next bar to be drawn
			nextBottomY = innerChartBottom;

			// Unfortunately necessary to discover which set is the bottom and top in case there
			// are entries with value 0. To better understand check one of the methods.
			bottomSetIndex = discoverBottomSet(i, data);
			topSetIndex = discoverTopSet(i, data);
			
			for(int j = 0; j < dataSize; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				// If entry value is 0 it won't be drawn
				if(!barSet.isVisible() || bar.getValue() <= 0)
					continue;
				
				x = bar.getX();
				y = bar.getY();
				
				style.barPaint.setColor(bar.getColor());
				style.applyAlpha(style.barPaint, barSet.getAlpha());
				
				// Distance from bottom to top of the bar
				dist = innerChartBottom - y;
				
				// Draw bar
				if(j == bottomSetIndex){

                    drawBar(canvas,
                            (int) (x - barWidth/2), (int) (innerChartBottom - (dist + verticalOffset)),
                            (int) (x + barWidth/2), (int) nextBottomY);
					
					if(bottomSetIndex != topSetIndex && style.cornerRadius != 0){
                        // Patch top corners of bar
                        cornersFix = (nextBottomY - (innerChartBottom - (dist + verticalOffset)))/2;
						canvas.drawRect(new Rect((int) (x - barWidth/2),
							(int) (innerChartBottom - (dist + verticalOffset)), 
								(int) (x + barWidth/2),
									(int) (innerChartBottom - (dist + verticalOffset) + cornersFix)),
								style.barPaint);
					}
					
				}else if(j == topSetIndex){

                    drawBar(canvas,
                            (int) (x - barWidth/2), (int) (innerChartBottom - (dist + verticalOffset)),
                            (int) (x + barWidth/2), (int) (nextBottomY));

					// Patch bottom corners of bar
					cornersFix = (nextBottomY - (innerChartBottom - (dist + verticalOffset))) / 2;
					canvas.drawRect(new Rect((int) (x - barWidth/2), 
							(int) (nextBottomY - cornersFix), 
								(int) (x + barWidth/2),
									(int) (nextBottomY)),
								style.barPaint);
				
				}else{// if(j != bottomSetIndex && j != topSetIndex){ // Middle sets

					canvas.drawRect(new Rect((int) (x - barWidth/2), 
							(int) (innerChartBottom - (dist + verticalOffset)), 
									(int) (x + barWidth/2),
											(int) (nextBottomY)),
											style.barPaint);
				}
					
				nextBottomY = innerChartBottom - (dist + verticalOffset);
				
				// In case bar is still hidden
				if(dist != 0)
					// Sum 2 to compensate the loss of precision in float
					verticalOffset += dist + 2;
			}
			
		}
		
	}



    /**
     * (Optional) To be overriden in case the view needs to execute some code before
     * starting the drawing.
     *
     * @param data   Array of {@link ChartSet} to do the necessary preparation just before onDraw
     */
	@Override
	public void onPreDrawChart(ArrayList<ChartSet> data){
		
		// Doing calculations here to avoid doing several times while drawing
		// in case of animation
		if(data.get(0).size() == 1)
            barWidth = (this.getInnerChartRight() - this.getInnerChartLeft()
                    - this.horController.borderSpacing * 2);
        else
			calculateBarsWidth(-1, data.get(0).getEntry(0).getX(), data.get(0).getEntry(1).getX());
	}



    /**
     * (Optional) To be overridden in order for each chart to define its own clickable regions.
     * This way, classes extending ChartView will only define their clickable regions.
     *
     * Important: the returned vector must match the order of the data passed
     * by the user. This ensures that onTouchEvent will return the correct index.
     *
     * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet}
     *             to use while defining each region of a {@link com.db.chart.view.BarChartView}
     * @return   {@link java.util.ArrayList} of {@link android.graphics.Region} with regions
     *           where click will be detected
     */
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		
		int dataSize = data.size();
		int setSize = data.get(0).size();
		float innerChartBottom = this.getInnerChartBottom();
		ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>(dataSize);

		for(int i = 0; i < dataSize; i++)
			result.add(new ArrayList<Region>(setSize));
		
		float verticalOffset;
		float nextBottomY;
		float dist;
		BarSet barSet;
		Bar bar;
		
		for (int i = 0; i < setSize; i++) {
			
			// Vertical offset to keep drawing bars on top of the others
			verticalOffset = 0;
			// Bottom of the next bar to be drawn
			nextBottomY = innerChartBottom;
			
			for(int j = 0; j < dataSize; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				// Distance from bottom to top of the bar
				dist = innerChartBottom - bar.getY();
				
				result.get(j).add(new Region(
                        (int) (bar.getX() - barWidth/2),
                        (int) (innerChartBottom - (dist + verticalOffset)),
                        (int) (bar.getX() + barWidth/2),
                        (int)(nextBottomY)));
				
				nextBottomY = innerChartBottom - (dist + verticalOffset);
				// Sum X to compensate the loss of precision in float
				verticalOffset += dist + 2;
			}
		}
		
		return result;
	}

}