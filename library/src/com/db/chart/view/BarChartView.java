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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

/**
 * Implements a bar chart extending {@link ChartView}
 */
public class BarChartView extends ChartView {

	
	/** 
	 * Offset to control bar positions.
	 * Added due to multiset charts.
	 */
	private float mDrawingOffset;
	
	
	/** Style applied to Graph */
	protected Style style;

	
	/** Bar width */
	protected float barWidth;

	
	
	
	public BarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setMandatoryBorderSpacing();
		style = new Style(context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
	}
	
	
	public BarChartView(Context context) {
		super(context);
		setMandatoryBorderSpacing();
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
		
		final int nSets = data.size();
		final int nEntries = data.get(0).size();
		final int yZeroCoord = (int) this.verController.parseYPos(0);
		
		float drawingOffset;
		BarSet barSet;
		Bar bar;
		
		for (int i = 0; i < nEntries; i++) {
			
			// Set first offset to draw a group of bars
			drawingOffset = data.get(0).getEntry(i).getX() - mDrawingOffset;
			
			for(int j = 0; j < nSets; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				// If entry value is 0 it won't be drawn
				if(!barSet.isVisible() || bar.getValue() == 0)
					continue;

				style.barPaint.setColor(bar.getColor());
				handleAlpha(style.barPaint, barSet.getAlpha());
				
				// If bar needs background
				if(style.hasBarBackground)
					drawBarBackground(canvas, drawingOffset);			
				
				// Draw bar
				if(bar.getValue() > 0)
					// Draw positive bar
					canvas.drawRoundRect(new RectF((int) drawingOffset, 
						(int) bar.getY(), 
							(int) (drawingOffset + barWidth),
								(int) yZeroCoord), 
							style.cornerRadius,
								style.cornerRadius,
									style.barPaint);
				else
					// Draw negative bar
					canvas.drawRoundRect(new RectF((int) drawingOffset, 
							(int) yZeroCoord, 
								(int) (drawingOffset + barWidth),
									(int) bar.getY()), 
								style.cornerRadius,
									style.cornerRadius,
										style.barPaint);
				
				drawingOffset += barWidth;
				
				// If last bar of group no set spacing is necessary
				if(j != nSets - 1)
					drawingOffset += style.mSetSpacing;
			}		
		}
	}
	
	
	
	
	protected void drawBarBackground(Canvas canvas, float horizontalOffset) {
		
		canvas.drawRoundRect(new RectF((int) horizontalOffset, 
				(int) this.getInnerChartTop(), 
					(int) (horizontalOffset + barWidth),
						(int) this.getInnerChartBottom()),
					style.cornerRadius,
						style.cornerRadius,
							style.mBarBackgroundPaint);	
	}
	



	/**
	 * Calculates Bar width based on the distance of two horizontal labels
	 * @param number of sets
	 * @param number of entries
	 * @param x0
	 * @param x1
	 */
	private void calculateBarsWidth(int nSets, float x0, float x1) {
		barWidth = ((x1 - x0) - style.barSpacing/2 - style.mSetSpacing * (nSets-1)) /nSets;
	}

	
	

	/**
	 * Having calculated previously the barWidth gives the offset to know 
	 * where to start drawing the first bar of each group.
	 * @param n - Number of sets
	 */
	private void calculatePositionOffset(int n){
		
		if(n % 2 == 0)
			mDrawingOffset = n*barWidth/2 + (n-1)*(style.mSetSpacing/2);
		else
			mDrawingOffset = n*barWidth/2 + ((n-1)/2)*style.mSetSpacing;
	}
	
	
	
	
	/**
	 * Method used to run all the code that needs to be executed before chart 
	 * has been drawn.
	 * @param chart datasets
	 */
	@Override
	public void onPreDrawChart(ArrayList<ChartSet> data){
		
		// Doing calculations here to avoid doing several times while drawing
		// in case of animation
		// Bar distance based on bar spacing
		if(data.get(0).size() == 1){
			style.barSpacing = 0;
			calculateBarsWidth(data.size(), 0, 
					(this.getInnerChartRight() - super.horController.borderSpacing - data.get(0).getEntry(0).getX()) * 2);
		}else
			calculateBarsWidth(data.size(), data.get(0).getEntry(0).getX(), 
							data.get(0).getEntry(1).getX());
		// For multi datasets
		calculatePositionOffset(data.size());
	}
	
	
	
	
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		
		int nSets = data.size();
		int nEntries = data.get(0).size();
		final int yZeroCoord = (int) this.verController.parseYPos(0);
		
		final ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>(nSets);
		
		for(int i = 0; i < nSets; i++)
			result.add(new ArrayList<Region>(nEntries));
		
		float drawingOffset;
		BarSet barSet;
		Bar bar;
		
		for (int i = 0; i < nEntries; i++) {
			
			// Set first offset to draw a group of bars
			drawingOffset = data.get(0).getEntry(i).getX() - mDrawingOffset;
			
			
			for(int j = 0; j < nSets; j++){
				
				barSet = (BarSet) data.get(j);
				bar = (Bar) barSet.getEntry(i);
				
				if(bar.getValue() > 0)
					result.get(j).add(new Region((int) drawingOffset, 
									(int) bar.getY(), 
										(int) (drawingOffset += barWidth), 
											yZeroCoord));
				else
					result.get(j).add(new Region((int) drawingOffset, 
							yZeroCoord, 
								(int) (drawingOffset += barWidth), 
									(int) bar.getY()));
				
				// If last bar of group no set spacing is necessary
				if(j != nSets - 1)
					drawingOffset += style.mSetSpacing;
			}	
			
		}
		
		return result;
	}
	
	
	
	
    protected void handleAlpha(Paint paint, float alpha){
    	
		paint.setAlpha((int)(alpha * 255));
		paint.setShadowLayer(
				style.mShadowRadius, 
					style.mShadowDx, 
						style.mShadowDy, 
							Color.argb(((int)(alpha * 255) < style.mAlpha) 
							? (int)(alpha * 255) 
							: style.mAlpha, 
								style.mRed, 
									style.mGreen, 
										style.mBlue));	
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
		
		
		private static final int DEFAULT_COLOR = -16777216;
		
		
		/** Bars fill variables */
		protected Paint barPaint;
		
		
		/** Bar background variables */
		private Paint mBarBackgroundPaint;
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
		
		
		/** Shadow color */
		private int mAlpha;
		private int mRed;
		private int mBlue;
		private int mGreen;
		
		
	    protected Style() {
	    	
	    	mBarBackgroundColor = DEFAULT_COLOR;
	    	hasBarBackground = false;
	    	
	    	barSpacing = (float) getResources().getDimension(R.dimen.bar_spacing);
	    	mSetSpacing = (float) getResources().getDimension(R.dimen.set_spacing);
	    	
	    	mShadowRadius = 0;
	    	mShadowDx = 0;
	    	mShadowDy = 0;
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
	    			R.styleable.ChartAttrs_chart_shadowRadius, 0);
	    	mShadowDx = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowDx, 0);
	    	mShadowDy = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowDy, 0);
	    	mShadowColor = attrs.getColor(
	    			R.styleable.ChartAttrs_chart_shadowColor, 0);
	    }	
	    
	    
	    
		private void init(){
	    	
			mAlpha = Color.alpha(mShadowColor);
			mRed = Color.red(mShadowColor);
			mBlue = Color.blue(mShadowColor);
			mGreen = Color.green(mShadowColor);
			
			
	    	barPaint = new Paint();
	    	barPaint.setStyle(Paint.Style.FILL);
	    	barPaint.setShadowLayer(mShadowRadius, mShadowDx, 
	    								mShadowDy, mShadowColor);
	    	
	    	mBarBackgroundPaint = new Paint();
	    	mBarBackgroundPaint.setColor(mBarBackgroundColor);
	    	mBarBackgroundPaint.setStyle(Paint.Style.FILL);
	    }
	    
		
		
	    private void clean(){
	    	
	    	barPaint = null;
	    	mBarBackgroundPaint = null;
	    }
	    
	    
	}

}
