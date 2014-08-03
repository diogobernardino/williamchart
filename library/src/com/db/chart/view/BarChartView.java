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
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;

/**
 * Implements a line chart extending {@link ChartView}
 */
public class BarChartView extends ChartView {
	
	/** Style applied to Graph */
	private Style mStyle;

	
	
	public BarChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mStyle = new Style(context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
	}
	
	
	public BarChartView(Context context) {
		super(context);
		mStyle = new Style();
	}
	
	
	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		mStyle.init();
	}
	
	
	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		mStyle.clean();
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
		
		for(ChartSet set: data){
			
			final BarSet barSet = (BarSet) set;
			mStyle.mBarPaint.setColor(barSet.getColor());
			
			for (ChartEntry entry: barSet.getEntries()) {
				Bar bar = (Bar) entry;
				mStyle.mBarPaint.setColor(bar.getColor());
				canvas.drawRect(new Rect((int) (bar.getX() - mStyle.mBarWidth/2), 
										(int) bar.getY(), 
											(int) (bar.getX() + mStyle.mBarWidth/2),
												(int) this.getInnerChartBottom()), 
									mStyle.mBarPaint);
			}
		}
	}
	
	
	
	/**
	 * Calculates Bar width based on the distance of two horizontal labels
	 * @param x0
	 * @param x1
	 */
	private void calculateBarsWidth(float x0, float x1) {
		mStyle.mBarWidth = x1-x0-mStyle.mBarSpacing;
	}

	
	
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {
		
		calculateBarsWidth(data.get(0).getEntry(0).getX(), 
							data.get(0).getEntry(1).getX());
		
		//Define regions
		final ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>();
		for(ChartSet set: data){
			
			final ArrayList<Region> regionSet = new ArrayList<Region>(set.size());
			for(ChartEntry e : set.getEntries()){

				regionSet.add(new Region((int)(e.getX() - mStyle.mBarWidth/2), 
										(int)(e.getY()), 
											(int)(e.getX() + mStyle.mBarWidth/2), 
												(int)(this.getInnerChartBottom())));
			
			}
			result.add(regionSet);
		}
		return result;
	}
	
	
	
	/*
	 * Setters
	 * 
	 */
	
	public void setBarSpacing(float spacing){
		mStyle.mBarSpacing = spacing;
	}
	
	
	
	/** 
	 * Keeps the style to be applied to the BarChart.
	 *
	 */
	public class Style{
		
		/** Bars fill variables */
		private Paint mBarPaint;
		
		
		/** Bar width */
		private float mBarWidth;
		
		
		/** Spacing between bars */
		private float mBarSpacing;
		
		
		/** Shadow related variables */
		private final float mShadowRadius;
		private final float mShadowDx;
		private final float mShadowDy;
		private final int mShadowColor;
		
		
	    protected Style() {
	    	
	    	mBarSpacing = (float) getResources().getDimension(R.dimen.bar_spacing);
	    	
	    	mShadowRadius = getResources().getDimension(R.dimen.shadow_radius);
	    	mShadowDx = getResources().getDimension(R.dimen.shadow_dx);
	    	mShadowDy = getResources().getDimension(R.dimen.shadow_dy);
	    	mShadowColor = 0;
	    }
	    
	    protected Style(TypedArray attrs) {
	    	
	    	mBarSpacing = attrs.getDimension(
	    			R.styleable.BarChartAttrs_chart_barSpacing, 
	    				getResources().getDimension(R.dimen.bar_spacing));
	    	
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
	    	
	    	mBarPaint = new Paint();
	    	mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	    	mBarPaint.setShadowLayer(mShadowRadius, mShadowDx, 
	    								mShadowDy, mShadowColor);
	    }
	    
		
	    private void clean(){
	    	
	    	mBarPaint = null;
	    }
	    
	}

}
