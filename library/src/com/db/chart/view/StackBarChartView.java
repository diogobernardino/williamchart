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

import java.util.ArrayList;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
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
		float currBottomY;

        float negVerticalOffset;
        float negCurrBottomY;

        float x0;
        float x1;
        float y1;
        float barSize;
        int bottomSetIndex;
        int topSetIndex;
        float cornersPatch;
        BarSet barSet;
		Bar bar;
		int dataSize = data.size();
		int setSize = data.get(0).size();
        float zeroPosition = this.getZeroPosition();

		for (int i = 0; i < setSize; i++) {
			
			// If bar needs background
			if(style.hasBarBackground) {
                drawBarBackground(canvas,
                        (int) (data.get(0).getEntry(i).getX() - barWidth / 2),
                        (int) this.getInnerChartTop(),
                        (int) (data.get(0).getEntry(i).getX() + barWidth / 2),
                        (int) this.getInnerChartBottom());
            }
			
			// Vertical offset to keep drawing bars on top of the others
			verticalOffset = 0;
            negVerticalOffset = 0;

			// Bottom of the next bar to be drawn
			currBottomY = zeroPosition;
            negCurrBottomY = zeroPosition;

			// Unfortunately necessary to discover which set is the bottom and top in case there
			// are entries with value 0. To better understand check one of the methods.
			bottomSetIndex = discoverBottomSet(i, data);
			topSetIndex = discoverTopSet(i, data);
			
			for(int j = 0; j < dataSize; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);

                barSize = Math.abs(zeroPosition - bar.getY());

				// If:
				// Bar not visible OR
				// Bar value equal to 0 OR
                // Size of bar < 2 (Due to the loss of precision)
                // Then no need to draw
				if(!barSet.isVisible() || bar.getValue() == 0 || barSize < 2)
					continue;

				style.barPaint.setColor(bar.getColor());
                style.barPaint.setAlpha((int)(barSet.getAlpha() * 255));
                applyShadow(style.barPaint, barSet.getAlpha(), bar);

                x0 = (bar.getX() - barWidth / 2);
                x1 = (bar.getX() + barWidth / 2);


                if(bar.getValue() > 0) {

                    y1 = zeroPosition - (barSize + verticalOffset);

                    // Draw bar
                    if (j == bottomSetIndex) {
                        drawBar(canvas, (int) x0, (int) y1, (int) x1, (int) currBottomY);
                        if (bottomSetIndex != topSetIndex && style.cornerRadius != 0) {
                            // Patch top corners of bar
                            cornersPatch = (currBottomY - y1) / 2;
                            canvas.drawRect(new Rect((int) x0, (int) y1,
                                                        (int) x1, (int) (y1 + cornersPatch)),
                                                style.barPaint);
                        }

                    } else if (j == topSetIndex) {
                        drawBar(canvas, (int) x0, (int) y1, (int) x1, (int) currBottomY);
                        // Patch bottom corners of bar
                        cornersPatch = (currBottomY - y1) / 2;
                        canvas.drawRect(new Rect((int) x0, (int) (currBottomY - cornersPatch),
                                                    (int) x1, (int) currBottomY),
                                            style.barPaint);

                    } else {// if(j != bottomSetIndex && j != topSetIndex){ // Middle sets
                        canvas.drawRect(new Rect((int) x0, (int) y1,
                                                    (int) x1, (int) currBottomY),
                                            style.barPaint);
                    }

                    currBottomY = y1;

                    // Increase the vertical offset to be used by the next bar
                    if (barSize != 0)
                        // Sum 1 to compensate the loss of precision in float
                        verticalOffset += barSize + 2;


                }else{ // if(bar.getValue() < 0)

                    y1 = zeroPosition + (barSize - negVerticalOffset);

                    if (j == bottomSetIndex) {
                        drawBar(canvas, (int) x0, (int) negCurrBottomY, (int) x1, (int) y1);
                        if (bottomSetIndex != topSetIndex && style.cornerRadius != 0) {
                            // Patch top corners of bar
                            cornersPatch = (y1 - negCurrBottomY) / 2;
                            canvas.drawRect(new Rect((int) x0, (int) negCurrBottomY,
                                                        (int) x1, (int) (negCurrBottomY + cornersPatch)),
                                                style.barPaint);
                        }

                    } else if (j == topSetIndex) {
                        drawBar(canvas, (int) x0, (int) negCurrBottomY, (int) x1, (int) y1);
                        // Patch bottom corners of bar
                        cornersPatch = (y1 - negCurrBottomY) / 2;
                        canvas.drawRect(new Rect((int) x0, (int) (y1 - cornersPatch),
                                                    (int) x1, (int) y1),
                                            style.barPaint);

                    } else {// if(j != bottomSetIndex && j != topSetIndex){ // Middle sets
                        canvas.drawRect(new Rect((int) x0, (int) negCurrBottomY,
                                                    (int) x1, (int) y1),
                                            style.barPaint);
                    }

                    negCurrBottomY = y1;

                    // Increase the vertical offset to be used by the next bar
                    if (barSize != 0)
                        negVerticalOffset -= barSize;

                }
			}
		}
	}



    /**
     * (Optional) To be overridden in case the view needs to execute some code before
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
                    - this.getBorderSpacing() * 2);
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
		ArrayList<ArrayList<Region>> result = new ArrayList<>(dataSize);
		for(int i = 0; i < dataSize; i++)
			result.add(new ArrayList<Region>(setSize));

        float verticalOffset;
        float currBottomY;

        float negVerticalOffset;
        float negCurrBottomY;

        float y1;
        float barSize;
        BarSet barSet;
        Bar bar;
        float zeroPosition = this.getZeroPosition();

		for (int i = 0; i < setSize; i++) {
			
			// Vertical offset to keep drawing bars on top of the others
			verticalOffset = 0;
            negVerticalOffset = 0;
			// Bottom of the next bar to be drawn
			currBottomY = zeroPosition;
            negCurrBottomY = zeroPosition;
			
			for(int j = 0; j < dataSize; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);

                barSize = Math.abs(zeroPosition - bar.getY());

                // If:
                // Bar not visible OR
                // Bar value equal to 0 OR
                // Size of bar < 2 (Due to the loss of precision)
                // Then no need to have region
                if(!barSet.isVisible() || bar.getValue() == 0 || barSize < 2)
                    continue;

                if(bar.getValue() > 0) {

                    y1 = zeroPosition - (barSize + verticalOffset);
                    result.get(j).add(new Region(
                            (int) (bar.getX() - barWidth / 2),
                            (int) y1,
                            (int) (bar.getX() + barWidth / 2),
                            (int) currBottomY));

                    currBottomY = y1;
                    verticalOffset += barSize + 2;
                }else{

                    y1 = zeroPosition + (barSize - negVerticalOffset);
                    result.get(j).add(new Region(
                            (int) (bar.getX() - barWidth / 2),
                            (int) negCurrBottomY,
                            (int) (bar.getX() + barWidth / 2),
                            (int) y1));

                    negCurrBottomY = y1;
                    negVerticalOffset -= barSize;
                }
			}
		}
		
		return result;
	}

}