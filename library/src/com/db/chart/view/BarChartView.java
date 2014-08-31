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

import com.db.williamchart.R;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

/**
 * Implements a bar chart extending {@link ChartView}
 */
public class BarChartView extends ChartView {
	
	
	/** Style applied to Graph */
	protected Style style;

	
	/** Bar width */
	protected float mBarWidth;

	
	/** 
	 * Offset to control bar positions
	 *  Added due to multiset charts
	 */
	private float mDrawingOffset;
	
	
	
	public BarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		style = new Style(context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
	}
	
	
	public BarChartView(Context context) {
		super(context);
		style = new Style();
	}
	
	
	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		style.init();
	}
	
	
	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		style.clean();
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
			
			// Set first offset to draw a group of bars
			float drawingOffset = data.get(0).getEntry(i).getX() - mDrawingOffset;
			
			for(int j = 0; j < data.size(); j++){
				
				final BarSet barSet = (BarSet) data.get(j);
				style.barPaint.setColor(barSet.getColor());
				Bar bar = (Bar) barSet.getEntry(i);
				style.barPaint.setColor(bar.getColor());
				
				// If bar needs background
				if(style.hasBarBackground)
					drawBarBackground(canvas, drawingOffset);			
				
				// Draw bar
				canvas.drawRoundRect(new RectF((int) drawingOffset, 
						(int) bar.getY(), 
							(int) (drawingOffset + mBarWidth),
								(int) this.getInnerChartBottom()), 
							style.cornerRadius,
								style.cornerRadius,
									style.barPaint);

				drawingOffset += mBarWidth;
				
				// If last bar of group no set spacing is necessary
				if(j != data.size()-1)
					drawingOffset += style.mSetSpacing;
			}	
		}
	}
	
	
	
	protected void drawBarBackground(Canvas canvas, float horizontalOffset) {
		canvas.drawRoundRect(new RectF((int) horizontalOffset, 
				(int) this.innerchartTop, 
					(int) (horizontalOffset + mBarWidth),
						(int) this.getInnerChartBottom()),
					style.cornerRadius,
						style.cornerRadius,
							style.barBackgroundPaint);	
	}
	


	/**
	 * Calculates Bar width based on the distance of two horizontal labels
	 * @param number of sets
	 * @param x0
	 * @param x1
	 */
	private void calculateBarsWidth(int n, float x0, float x1) {
		mBarWidth = ((x1 - x0) - style.barSpacing/2 - style.mSetSpacing*(n-1))/n;
	}

	
	
	/**
	 * Having calculated previously bar width it gives the offset to know 
	 * where to start drawing the first bar of each group.
	 * @param n - Number of sets
	 */
	private void calculatePositionOffset(int n){
		if(n % 2 == 0){
			mDrawingOffset = n*mBarWidth/2 + (n-1)*(style.mSetSpacing/2);
		}else{
			mDrawingOffset = n*mBarWidth/2 + ((n-1)/2)*style.mSetSpacing;
		}
	}
	
	
	
	@Override
	public void onPreDrawChart(ArrayList<ChartSet> data){
		
		// Doing calculations here to avoid doing several times while drawing
		// in case of animation
		calculateBarsWidth(data.size(), data.get(0).getEntry(0).getX(), 
							data.get(0).getEntry(1).getX());
		calculatePositionOffset(data.size());
	}
	
	
	
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		// Define regions
		
		final ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>();
		for(int i = 0; i < data.size(); i++)
			result.add(new ArrayList<Region>());
		
		for (int i = 0; i < data.get(0).size(); i++) {
			
			// Set first offset to draw a group of bars
			float drawingOffset = data.get(0).getEntry(i).getX() - mDrawingOffset;
			
			for(int j = 0; j < data.size(); j++){
				
				final BarSet barSet = (BarSet) data.get(j);
				Bar bar = (Bar) barSet.getEntry(i);
				
				result.get(j).add(new Region((int) drawingOffset, 
									(int) bar.getY(), 
										(int) (drawingOffset += mBarWidth), 
											(int)(this.getInnerChartBottom())));
				// If last bar of group no set spacing is necessary
				if(j != data.size()-1)
					drawingOffset += style.mSetSpacing;
			}	
		}
		return result;
	}
	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	/**
	 * Define the space to use between bars.
	 * @param spacing
	 */
	public void setBarSpacing(float spacing){
		style.barSpacing = spacing;
	}
	
	/**
	 * When multiset, it defines the space to use set.
	 * @param spacing
	 */
	public void setSetSpacing(float spacing){
		style.mSetSpacing = spacing;
	}
	
	/**
	 * Background in bars place
	 * @param bool
	 */
	public void setBarBackground(boolean bool){
		style.hasBarBackground = bool;
	}
	
	/**
	 * Color to use in bars background.
	 * @param color
	 */
	public void setBarBackgroundColor(int color){
		style.mBarBackgroundColor = color;
	}
	
	/**
	 * Round corners of bars
	 * @param radius - radius applied to the corners.
	 */
	public void setRoundCorners(float radius){
		style.cornerRadius = radius;
	}
	
	/** 
	 * Keeps the style to be applied to the BarChart.
	 *
	 */
	public class Style{
		
		private int DEFAULT_COLOR = -16777216;
		
		/** Bars fill variables */
		protected Paint barPaint;
		
		/** Bar background variables */
		private Paint barBackgroundPaint;
		private int mBarBackgroundColor;
		protected boolean hasBarBackground;
		
		/** Spacing between bars */
		protected float barSpacing;
		private float mSetSpacing;
		
		/** Radius to round corners **/
		protected float cornerRadius;
		
		/** Shadow related variables */
		private final float mShadowRadius;
		private final float mShadowDx;
		private final float mShadowDy;
		private final int mShadowColor;
		
		
	    protected Style() {
	    	
	    	mBarBackgroundColor = DEFAULT_COLOR;
	    	hasBarBackground = false;
	    	
	    	barSpacing = (float) getResources().getDimension(R.dimen.bar_spacing);
	    	mSetSpacing = (float) getResources().getDimension(R.dimen.set_spacing);
	    	
	    	mShadowRadius = getResources().getDimension(R.dimen.shadow_radius);
	    	mShadowDx = getResources().getDimension(R.dimen.shadow_dx);
	    	mShadowDy = getResources().getDimension(R.dimen.shadow_dy);
	    	mShadowColor = DEFAULT_COLOR;
	    }
	    
	    protected Style(TypedArray attrs) {
	    	
	    	mBarBackgroundColor = DEFAULT_COLOR;
	    	hasBarBackground = false;
	    	
	    	barSpacing = attrs.getDimension(
	    			R.styleable.BarChartAttrs_chart_barSpacing, 
	    				getResources().getDimension(R.dimen.bar_spacing));
	    	mSetSpacing = attrs.getDimension(
	    			R.styleable.BarChartAttrs_chart_barSpacing, 
	    				getResources().getDimension(R.dimen.set_spacing));
	    	
	    	mShadowRadius = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowRadius, 
	    				getResources().getDimension(R.dimen.shadow_radius));
	    	mShadowDx = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowDx, 
	    				getResources().getDimension(R.dimen.shadow_dx));
	    	mShadowDy = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowDy, 
	    				getResources().getDimension(R.dimen.shadow_dy));
	    	mShadowColor = attrs.getColor(
	    			R.styleable.ChartAttrs_chart_shadowColor, 0);
	    }	
	    
	    
		private void init(){
	    	
	    	cornerRadius = 0;
	    	
	    	barPaint = new Paint();
	    	barPaint.setStyle(Paint.Style.FILL);
	    	barPaint.setShadowLayer(mShadowRadius, mShadowDx, 
	    								mShadowDy, mShadowColor);
	    	
	    	barBackgroundPaint = new Paint();
	    	barBackgroundPaint.setColor(mBarBackgroundColor);
	    	barBackgroundPaint.setStyle(Paint.Style.FILL);
	    }
	    
		
	    private void clean(){
	    	
	    	barPaint = null;
	    	barBackgroundPaint = null;
	    }
	    
	}

}
