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
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;


/**
 * Implements a line chart extending {@link ChartView}
 */
public class LineChartView extends ChartView {

	
	/** Radius clickable region */
	private static float sRegionRadius;
	
	
	/** Style applied to line chart */
	private Style mStyle;

	
	
	public LineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mStyle = new Style(context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
		sRegionRadius = (float) getResources()
									.getDimension(R.dimen.dot_region_radius);
	}
	
	
	public LineChartView(Context context) {
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
	 * Method responsible to draw a line with the parsed screen points.
	 * @param canvas
	 *   The canvas to draw on.
	 * @param screenPoints
	 *   The parsed screen points ready to be used/drawn.
	 */
	@Override
	public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {

		for(ChartSet set: data){
			
			final LineSet lineSet = (LineSet) set;
			
			mStyle.mLinePaint.setColor(lineSet.getLineColor());
			mStyle.mLinePaint.setStrokeWidth(lineSet.getLineThickness());
			mStyle.mFillPaint.setColor(lineSet.getFillColor());
			
			if(lineSet.isDashed())
				mStyle.mLinePaint
					.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
			else
				mStyle.mLinePaint.setPathEffect(null);
			
			//Draw line
			if (!lineSet.isSmooth())
				drawLine(canvas, lineSet);
			else
				drawSmoothLine(canvas, lineSet);
			
			//Draw points
			if(lineSet.hasDots())
				drawPoints(canvas, lineSet);
		}

	}

	
	/**
	 * Responsible for drawing points
	 */
	private void drawPoints(Canvas canvas, LineSet set) {
		
		mStyle.mDotsPaint.setColor(set.getDotsColor());
		mStyle.mDotsStrokePaint.setStrokeWidth(set.getDotsStrokeThickness());
		mStyle.mDotsStrokePaint.setColor(set.getDotsStrokeColor());
		
		Path path = new Path();
		for(int i = 0; i < set.size(); i++){
			path.addCircle(set.getEntry(i).getX(), 
							set.getEntry(i).getY(), 
								set.getDotsRadius(), Path.Direction.CW);
		}
		
		//Draw dots fill
		canvas.drawPath(path, mStyle.mDotsPaint);
		
		//Draw dots stroke
		if(set.hasDotsStroke())
			canvas.drawPath(path, mStyle.mDotsStrokePaint);
	}



	/**
	 * Responsible for drawing a (not smooth) line
	 */
	public void drawLine(Canvas canvas, LineSet set) {
		Path path = new Path();
		Path bgPath = new Path();
		for (int i = 0; i < set.size(); i++) {
			if (i == 0){
				//Defining outline
				path.moveTo(set.getEntry(i).getX(), set.getEntry(i).getY());
				//Defining background
				bgPath.moveTo(set.getEntry(i).getX(), set.getEntry(i).getY());	
			}else{
				//Defining outline
				path.lineTo(set.getEntry(i).getX(), set.getEntry(i).getY());
				//Defining background
				bgPath.lineTo(set.getEntry(i).getX(), set.getEntry(i).getY());	
			}
		}
		
		//Draw background
		if(set.hasFill()){
			bgPath.lineTo(set.getEntry(set.size() - 1).getX(), 
							this.getInnerChartBottom());
			bgPath.lineTo(set.getEntry(0).getX(), 
							this.getInnerChartBottom());
			bgPath.close();
			canvas.drawPath(bgPath, mStyle.mFillPaint);
		}
		
		//Draw line
		canvas.drawPath(path, mStyle.mLinePaint);
	}

	

	/**
	 * Credits: http://www.jayway.com/author/andersericsson/
	 * Method responsible to draw a smooth line with the parsed screen points.
	 */
	private void drawSmoothLine(Canvas canvas, LineSet set) {
		
		Path path = new Path();
		path.moveTo(set.getEntry(0).getX(),set.getEntry(0).getY());
		
		Path bgPath= new Path();
		bgPath.moveTo(set.getEntry(0).getX(), set.getEntry(0).getY());
			
		for (int i = 0; i < set.size() - 1; i++) {
            
			float thisPointX = set.getEntry(i).getX();
            float thisPointY = set.getEntry(i).getY();
            
            float nextPointX = set.getEntry(i+1).getX();
            float nextPointY = set.getEntry(i+1).getY();
	
            float startdiffX = (nextPointX - set.getEntry(si(set.getEntries(), 
            													i - 1)).getX());
            float startdiffY = (nextPointY - set.getEntry(si(set.getEntries(), 
            													i - 1)).getY());
	            
            float endDiffX = (set.getEntry(si(set.getEntries(), i + 2))
            											.getX() - thisPointX);
            float endDiffY = (set.getEntry(si(set.getEntries(), i + 2))
            											.getY() - thisPointY);
	
            float firstControlX = thisPointX + (0.15f * startdiffX);
            float firstControlY = thisPointY + (0.15f * startdiffY);
	            
            float secondControlX = nextPointX - (0.15f * endDiffX);
            float secondControlY = nextPointY - (0.15f * endDiffY);
	
            //Define outline
            path.cubicTo(firstControlX, firstControlY, 
            				secondControlX, secondControlY, 
            					nextPointX, nextPointY);
	        
            //Define background
            bgPath.cubicTo(firstControlX, firstControlY, 
            				secondControlX, secondControlY, 
            					nextPointX, nextPointY);
				
		}
		
		//Draw background
		if(set.hasFill()){
			bgPath.lineTo(set.getEntry(set.size()-1).getX(), this.getInnerChartBottom());
			bgPath.lineTo(set.getEntry(0).getX(), this.getInnerChartBottom());
			bgPath.close();
			canvas.drawPath(bgPath, mStyle.mFillPaint);
		}
		
		//Draw outline
		canvas.drawPath(path, mStyle.mLinePaint);

	}
	
	
	
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data){
		final ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>();
		for(ChartSet set: data){
			final ArrayList<Region> regionSet = new ArrayList<Region>(set.size());
			for(ChartEntry e : set.getEntries())
				regionSet.add(new Region((int)(e.getX() - sRegionRadius), 
										(int)(e.getY() - sRegionRadius), 
											(int)(e.getX() + sRegionRadius), 
												(int)(e.getY() + sRegionRadius)));
			result.add(regionSet);
		}
		
		return result;
	}
	
	
	
    /**
     * Credits: http://www.jayway.com/author/andersericsson/
     * Given an index in points, it will make sure the the returned index is
     * within the array.
     */
    private int si(ArrayList<ChartEntry> points, int i) {
    	
        if (i > points.size() - 1)
            return points.size() - 1;
        else if (i < 0)
            return 0;
        return i;
    }
    
    
    
	/**
	 * Class responsible to style the LineChart!
	 * Can be instantiated with or without attributes.
	 */
	class Style {
		
		/** Paint variables */
		private Paint mDotsPaint;
		private Paint mDotsStrokePaint;
		private Paint mLinePaint;
		private Paint mFillPaint;
		
		/** Shadow variables */
		private int mShadowColor;
		private float mShadowRadius;
		private float mShadowDx;
		private float mShadowDy;
		
		
		protected Style() {
			mShadowRadius = getResources().getDimension(R.dimen.shadow_radius);
	    	mShadowDx = getResources().getDimension(R.dimen.shadow_dx);
	    	mShadowDy = getResources().getDimension(R.dimen.shadow_dy);
			mShadowColor = 0;
		}
		
		protected Style(TypedArray attrs) {
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
	    	
			mDotsPaint = new Paint();
			mDotsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mDotsPaint.setAntiAlias(true);
			mDotsPaint.setShadowLayer(mShadowRadius, mShadowDx, 
										mShadowDy, mShadowColor);
			
			mDotsStrokePaint = new Paint();
			mDotsStrokePaint.setStyle(Paint.Style.STROKE);
			mDotsStrokePaint.setAntiAlias(true);
			mDotsStrokePaint.setShadowLayer(mShadowRadius, mShadowDx, 
												mShadowDy, mShadowColor);
			
			mLinePaint = new Paint();
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setAntiAlias(true);
			mLinePaint.setShadowLayer(mShadowRadius, mShadowDx, 
										mShadowDy, mShadowColor);
			
			mFillPaint = new Paint();
			mFillPaint.setStyle(Paint.Style.FILL);
			mFillPaint.setAlpha(128);
	    }

		
	    private void clean(){
	    	
	    	mLinePaint = null;
	    	mFillPaint = null;
	    	mDotsPaint = null;
	    }
	    
	}
    
}
