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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;

import java.util.ArrayList;


/**
 * Implements a {@link com.db.chart.view.HorizontalBarChartView} extending {@link com.db.chart.view.ChartView}
 */
public class HorizontalBarChartView extends BaseBarChartView {



	public HorizontalBarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

        setOrientation(Orientation.HORIZONTAL);
        setMandatoryBorderSpacing();
	}


	public HorizontalBarChartView(Context context) {
		super(context);

        setOrientation(Orientation.HORIZONTAL);
        setMandatoryBorderSpacing();
	}



    /**
     * Method responsible to draw bars with the parsed screen points.
     *
     * @param canvas   The canvas to draw on
     * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet}
     *             to use while drawing the Chart
     */
	@Override
	public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {

		final int nSets = data.size();
		final int nEntries = data.get(0).size();
		
		float offset;
		BarSet barSet;
		Bar bar;
		
		for (int i = nEntries - 1; i >= 0; i--) {
			
			// Set first offset to draw a group of bars
            offset = data.get(0).getEntry(i).getY() - drawingOffset;
			
			for(int j = 0; j < nSets; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				// If entry value is 0 it won't be drawn
				if(!barSet.isVisible() || bar.getValue() == 0)
					continue;

				style.barPaint.setColor(bar.getColor());
				style.barPaint.setAlpha((int)(barSet.getAlpha() * 255));
				applyShadow(style.barPaint, barSet.getAlpha(), bar);
				
				// If bar needs background
				if(style.hasBarBackground)
                    drawBarBackground(canvas,
                            this.getInnerChartLeft(), offset,
                            this.getInnerChartRight(), (offset + barWidth));

				
				// Draw bar
				if(bar.getValue() > 0)
					// Draw positive bar
                    drawBar(canvas,
							this.getZeroPosition(), offset,
                            bar.getX(), offset + barWidth);
				else
					// Draw negative bar
                    drawBar(canvas,
                            bar.getX(), offset,
							this.getZeroPosition(), offset + barWidth);

                offset += barWidth;
				
				// If last bar of group no set spacing is necessary
				if(j != nSets - 1)
                    offset += style.setSpacing;
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
	protected void onPreDrawChart(ArrayList<ChartSet> data){

        // In case of only on entry
		if(data.get(0).size() == 1){
			style.barSpacing = 0;
			calculateBarsWidth(data.size(), 0, this.getInnerChartBottom()
                    - this.getInnerChartTop()
                    - this.getBorderSpacing() * 2);
        // In case of more than one entry
        }else
			calculateBarsWidth(data.size(), data.get(0).getEntry(1).getY(),
                    data.get(0).getEntry(0).getY());

		calculatePositionOffset(data.size());
	}



    /**
     * (Optional) To be overridden in order for each chart to define its own clickable regions.
     * This way, classes extending ChartView will only define their clickable regions.
     *
     * Important: the returned vector must match the order of the data passed
     * by the user. This ensures that onTouchEvent will return the correct index.
     *
     * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet} to use
     *               while defining each region of a {@link com.db.chart.view.HorizontalBarChartView}
     * @return   {@link java.util.ArrayList} of {@link android.graphics.Region} with regions
     *           where click will be detected
     */
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		
		int nSets = data.size();
		int nEntries = data.get(0).size();
		
		final ArrayList<ArrayList<Region>> result = new ArrayList<>(nSets);
		
		for(int i = 0; i < nSets; i++)
			result.add(new ArrayList<Region>(nEntries));
		
		float offset;
		BarSet barSet;
		Bar bar;
		
		for (int i = nEntries - 1; i >= 0; i--) {
			
			// Set first offset to draw a group of bars
            offset = data.get(0).getEntry(i).getY() - drawingOffset;

			for(int j = 0; j < nSets; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				if(bar.getValue() > 0)
					result.get(j).add(new Region(
							(int) this.getZeroPosition(),
                            (int) offset,
                            (int) bar.getX(),
                            (int) (offset + barWidth)));
				else
					result.get(j).add(new Region(
                            (int) bar.getX(),
                            (int) offset,
							(int) this.getZeroPosition(),
                            (int) (offset + barWidth)));
				
				// If last bar of group no set spacing is necessary
				if(j != nSets - 1)
                    offset += style.setSpacing;
			}
		}
		return result;
	}

}