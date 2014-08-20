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

import com.db.chart.OnEntryClickListener;
import com.db.williamchart.R;
import com.db.chart.exception.ChartException;
import com.db.chart.model.ChartSet;
import com.db.chart.view.animation.Animation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;


/**
 * Abstract class to be extend to define any chart that implies axis.
 */
public abstract class ChartView extends View{
	
	
	private static final String TAG = "com.db.chart.view.ChartView";
	
	
	/** Chart borders */
	protected int chartTop;
	protected int chartBottom;
	protected int chartLeft;
	protected int chartRight;
	
	
	/** Innerchart borders */
	protected float innerchartTop;
	protected float innerchartBottom;
	protected float innerchartLeft;
	protected float innerchartRight;
	
	
	/** Horizontal and Vertical position controllers */
	private XController mHorController;
	private YController mVerController;
	
	
	/** Chart data to be displayed */
	protected ArrayList<ChartSet> data;
	
	
	/** Chart data to be displayed */	
	private ArrayList<ArrayList<Region>> mRegions;
	
	
	/** Index of last point clicked */
	private int mIndexClicked;
	private int mSetClicked;
	
	
	/** Listeners to for touch events */
	private OnEntryClickListener mEntryListener;
	private OnClickListener mChartListener;

	
	/** Drawing flag */
	private boolean mReadyToDraw;
	
	
	/** Drawing flag */
	private boolean mIsDrawing;
	
	
	/** Style applied to chart */
	protected Style style;


	/** Chart animation */
	private Animation mAnim;
	
	
	/**
	 * Executed only before the chart is drawn for the first time.
	 * . borders are defined
	 * . digestData(data), to process the data to be drawn
	 * . defineRegions(), if listener has been registered 
	 * 	 this will define the chart regions to handle by onTouchEvent
	 */
    private OnPreDrawListener drawListener = new OnPreDrawListener(){
		@SuppressLint("NewApi")
		@Override
		public boolean onPreDraw() {
			ChartView.this.getViewTreeObserver().removeOnPreDrawListener(this);
			
			// Define chart frame
			chartTop = getPaddingTop();
			chartBottom = getMeasuredHeight() - getPaddingBottom();
			chartLeft = getPaddingLeft();
			chartRight = getMeasuredWidth() - getPaddingRight();
				
			// Initialize controllers now that we have the measures
			mVerController.init();				
			mHorController.init(mVerController.getInnerChartLeft());
				
			// Define the data chart frame. Exclude axis space.
			innerchartTop = chartTop;
			innerchartBottom = mVerController.getInnerChartBottom();
			innerchartLeft = mVerController.getInnerChartLeft();
			innerchartRight = mHorController.getInnerChartRight();
				
			// Processes data to define screen positions
			digestData();
				
			// Sets listener if needed
			if(mEntryListener != null) 
				mRegions = defineRegions(data);
				
			// Prepares the animation if needed and gets the first dump 
			//of data to be drawn.
			if(mAnim != null)
				data = mAnim.prepareEnter(ChartView.this, 
								mVerController.getInnerChartBottom(), data);
			
			if (android.os.Build.VERSION.SDK_INT >= 
					android.os.Build.VERSION_CODES.HONEYCOMB)
				ChartView.this.setLayerType(LAYER_TYPE_SOFTWARE, null);
				
			return mReadyToDraw = true;
		}
		
	};
	
	
	
	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mHorController = new XController(this, 
				context.getTheme().obtainStyledAttributes(attrs, 
						R.styleable.ChartAttrs, 0, 0));
		mVerController = new YController(this, 
				context.getTheme().obtainStyledAttributes(attrs, 
						R.styleable.ChartAttrs, 0, 0));
		
		style = new Style(context.getTheme().obtainStyledAttributes(attrs, 
				R.styleable.ChartAttrs, 0, 0));
		
		init();
	}
	
	
	public ChartView(Context context) {
		super(context);
		
		mHorController = new XController(this);
		mVerController = new YController(this);
		
		style = new Style();
		
		init();
	}
	
	

	private void init(){
		
		mReadyToDraw = false;
		mSetClicked = -1;
		mIndexClicked = -1;

		mIsDrawing = false;
		data = new ArrayList<ChartSet>();
	}


	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		
		this.setWillNotDraw(false);
		style.init();
	}
	
	
	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		
		style.clean();
	}
	

	
	/* 
	 * Methods to be overridden.
	 *
	 */
	
	/**
	 * (Optional) To be overridden in order for each chart to define 
	 * its own clickable regions.
	 * This way, classes extending ChartView will only define their 
	 * clickable regions.
	 * @return 
	 * 	Important: the returned vector must match the order of the data passed 
	 * 	by the user. This ensures that onTouchEvent will return the correct index.
	 */
	protected ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data){
		return mRegions;
	};
	
	
	/**
	 * To be Overridden in order for each chart to customize visualization
	 * @param screenPoints
	 */
	abstract public void onDrawChart(Canvas canvas, ArrayList<ChartSet> set);
	
	
	
	/**
	 * Convert chart points into screen points
	 */
	protected void digestData() {
		
		for(ChartSet s: data)
			for (int i = 0; i < s.size(); i++){
				s.getEntry(i)
					.setDisplayCoordinates(mHorController.labelsPos.get(i), 
									mVerController.parseYPos(s.getValue(i)));
		}
	}
	
	
	
	/**
	 * Set a new data to the chart and invalidates the view to be then drawn.
	 * @param data
	 */
	public void addData(ChartSet set){
		
		if(!data.isEmpty() && set.size() != data.get(0).size())
			Log.e(TAG, "", 
					new ChartException("The number of labels between " +
							"sets doesn't match."));
		data.add(set);
	}
	
	
	public void addData(ArrayList<ChartSet> data){
		this.data = data;
	}
	
	
	
	/**
	 * Method not often expected to be used. More for testing.
	 */
	public void reset(){
		data = new ArrayList<ChartSet>();
		mVerController.reset();
	}
	
	
	
	/**
	 * Starts processing the data to be drawn
	 */
	public void show(){
		
		this.getViewTreeObserver().addOnPreDrawListener(drawListener);
		postInvalidate();
	}
	
	
	
	/**
	 * Asks the view if it is able to draw now
	 */
	public boolean canYouDraw(){
		return !mIsDrawing;
	}
	
	
	
	/*
	 * Draw methods
	 * 
	 */

	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		//TODO why is this here?
		mIsDrawing = true;
		super.onDraw(canvas);
		
		//long time = System.currentTimeMillis();
		
		if(mReadyToDraw){
			
			//draw grid
			if(style.hasGrid)
				drawGrid(canvas);
			if(style.hasHorizontalGrid)
				drawHorizontalGrid(canvas);
			
			//draw data
			onDrawChart(canvas, data);
			
			//draw axis
			mVerController.draw(canvas);
			mHorController.draw(canvas);
			
		}
		//System.out.println("Time drawing "+(System.currentTimeMillis() - time));
		mIsDrawing = false;
	}
	
	
	private void drawGrid(Canvas canvas){
		
		// Draw vertical grid lines
		final ArrayList<Float> horPositions = mHorController.getLabelsPosition();
		for(int i = 0; i < horPositions.size(); i++){
			canvas.drawLine(horPositions.get(i), 
					innerchartBottom, 
						horPositions.get(i), 
							innerchartTop, 
								style.gridPaint);
		}
		
		// If border diff than 0 inner chart sides must have lines
		if(mHorController.horBorderSpacing != 0){
			canvas.drawLine(innerchartLeft, 
					innerchartBottom, 
						innerchartLeft, 
							innerchartTop, 
								style.gridPaint);
			canvas.drawLine(innerchartRight, 
					innerchartBottom, 
						innerchartRight, 
							innerchartTop, 
								style.gridPaint);
		}
			
		drawHorizontalGrid(canvas);
	}
	
	private void drawHorizontalGrid(Canvas canvas){
		//Draw horizontal grid lines
		final ArrayList<Float> verPositions = mVerController.getLabelsPosition();
		for(int i = 0; i < verPositions.size(); i++){
			canvas.drawLine(innerchartLeft, 
					verPositions.get(i), 
						innerchartRight,
							verPositions.get(i), 
								style.gridPaint);
		}
	}
	
	
	
	/*
	 * Click handler
	 * 
	 */
	
	/**
	 * The method listens chart clicks and checks whether it intercepts
	 * a known Region. It will then use the registered Listener.onClick 
	 * to return the region's index. 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mAnim != null && !mAnim.isPlaying())
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				
				if(mEntryListener != null && mRegions != null){
					 //Check if ACTION_DOWN over any ScreenPoint region.
						for(int i = 0; i < mRegions.size() ; i++){
							for(int j = 0; j < mRegions.get(i).size(); j++)
								if(mRegions.get(i).get(j)
										.contains((int) event.getX(), 
													(int) event.getY())){
									mSetClicked = i;
									mIndexClicked = j;
								}
						}
				}
				
			}else if(event.getAction() == MotionEvent.ACTION_UP){
				
				if(mChartListener != null)
					mChartListener.onClick(this);
				
				if(mEntryListener != null && 
						mSetClicked != -1 && 
							mIndexClicked != -1 &&
								mRegions.get(mSetClicked).get(mIndexClicked)
								.contains((int)event.getX(), 
											(int)event.getY())){
					mEntryListener.onClick(mSetClicked, mIndexClicked);
				}
			}
		return true;
	}
	
	
	
	/*
	 * Getters
	 * 
	 */
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner bottom side of the chart
	 */
	protected float getInnerChartBottom(){
		return innerchartBottom;
	}

	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner left side of the chart
	 */
	protected float getInnerChartLeft(){
		return innerchartLeft;
	}
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner right side of the chart
	 */
	protected float getInnerChartRight(){
		return innerchartRight;
	}
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner top side of the chart
	 */
	protected float getInnerChartTop(){
		return innerchartTop;
	}
	
	
	/**
	 * @return Border between left/right of the chart and the first/last label
	 */
	public float getLabelBorderSpacing(){
		return mHorController.getBorderSpacing();
	}
	
	
	
	/*
	 * Setters
	 * 
	 */

	
	public ChartView setLabels(boolean bool){
		mVerController.setLabels(bool);
		return this;
	}
	
	
	/**
	 * A step is seen as the step to be defined between 2 labels. As an 
	 * example a step of 2 with a maxAxisValue of 6 will end up with 
	 * {0, 2, 4, 6} as labels.
	 * @param maxAxisValue - the maximum value that Y axis will have as a label
	 * @param step - step - (real) value distance from every label
	 */
	public ChartView setMaxAxisValue(int maxAxisValue, int step){
		
		try{
			if(maxAxisValue % step != 0)
				throw new ChartException("Step value must be a divisor of maxAxisValue");
		}catch(ChartException e){
			Log.e(TAG, "", e);
			System.exit(1);
		}
		mVerController.setMaxAxisValue(maxAxisValue, step);
		return this;
	}
	
	
	/**
	 * A step is seen as the step to be defined between 2 labels. 
	 * As an example a step of 2 with a max label value of 6 will end 
	 * up with {0, 2, 4, 6} as labels.
	 * @param step - (real) value distance from every label
	 */
	public ChartView setStep(int step){
		
		try{
			if(step <= 0)
				throw new ChartException("Step less or equal to 0");
		}catch(ChartException e){
			Log.e(TAG, "", e);
			System.exit(1);
		}
		mVerController.setStep(step);
		return this;
	}

	
	public ChartView setAnimation(Animation anim){
		mAnim = anim;
		return this;
	}

	
	/**
	 * Register a listener to be called when the chart is clicked.
	 * @param listener
	 */
	public void setOnEntryClickListener(OnEntryClickListener listener){
		this.mEntryListener = listener;
	}
	
	
	/**
	 * Register a listener to be called when the chart is clicked.
	 * @param listener
	 */
	public void setOnChartClickListener(OnClickListener listener){
		this.mChartListener = listener;
	}
	
	
    public ChartView setLabelColor(int color) {
    	style.labelColor = color;
    	return this;
    }

    
    public ChartView setFontSize(int size) {
    	style.fontSize = size;
    	return this;
    }
    
    
    public ChartView setTypeface(Typeface typeface) {
    	style.typeface = typeface;
    	return this;
    }
	  
    
	public ChartView setBorderSpacing(float spacing){
		mHorController.setBorderSpacing(spacing);
		return this;
	}
	
	
	public ChartView setTopSpacing(float spacing){
		mVerController.setTopSpacing(spacing);
		return this;
	}
	
	
	public ChartView setGrid(boolean bool){
		style.hasGrid = bool;
		return this;
	}
	
	
	public ChartView setHorizontalGrid(boolean bool){
		style.hasHorizontalGrid = bool;
		return this;
	}

	
	public ChartView setGridDashed(boolean bool){
		style.hasGridDashed = bool;
		return this;
	}
	
	
	public ChartView setGridColor(int color){
		style.gridColor = color;
		return this;
	}
	
	
	public ChartView setGridStrokeThickness(int thickness){
		
		try{
			if(thickness <= 0)
				throw new ChartException("Grid thickness <= 0.");
		}catch(ChartException e){
			Log.e(TAG, "", e);
			System.exit(1);
		}
		style.gridThickness = thickness;
		return this;
	}
	
	
	
	
	/**
	 * Class responsible to style the Graph!
	 * Can be instantiated with or without attributes.
	 */
	class Style {
		
		private int DEFAULT_COLOR = -16777216;
		
		/** Chart */
		protected Paint chartPaint;
		protected float axisThickness;
		protected int axisColor;
		
		
		/** Grid */
		protected Paint gridPaint;
		protected int gridColor;
		protected float gridThickness;
		protected boolean hasGrid;
		protected boolean hasHorizontalGrid;
		protected boolean hasGridDashed;
		
		
		/** Font */
		protected Paint labelPaint;
		protected float fontSize;
		protected int labelColor;
		protected Typeface typeface;
		
		
		protected Style() {
			
			hasGrid = false;
			hasHorizontalGrid = false;
			hasGridDashed = false;
			
			gridColor = DEFAULT_COLOR;
			gridThickness = (float) getResources().getDimension(R.dimen.axis_thickness);
			
			axisColor = DEFAULT_COLOR;
			axisThickness = (float) getResources().getDimension(R.dimen.grid_thickness);
			
			labelColor = DEFAULT_COLOR;
			fontSize = getResources().getDimension(R.dimen.font_size);
		}

		protected Style(TypedArray attrs) {
			
			mVerController.setLabels(attrs.getBoolean(
										R.styleable.ChartAttrs_chart_label, 
											false));
			
			hasGrid = attrs.getBoolean(
					R.styleable.ChartAttrs_chart_grid, 
					false);
			hasHorizontalGrid = attrs.getBoolean(
					R.styleable.ChartAttrs_chart_horizontalGrid, 
					false);
			hasGridDashed = attrs.getBoolean(
					R.styleable.ChartAttrs_chart_gridDashed, 
					false);
			
			axisColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_axisColor, 
						DEFAULT_COLOR);
			axisThickness = attrs.getDimension(
					R.styleable.ChartAttrs_chart_axisThickness, 
						getResources().getDimension(R.dimen.axis_thickness));
			
			gridColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_gridColor, DEFAULT_COLOR);
			gridThickness = attrs.getDimension(
					R.styleable.ChartAttrs_chart_gridThickness, 
						getResources().getDimension(R.dimen.grid_thickness));
			
			labelColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_labelColor, DEFAULT_COLOR);
	    	fontSize = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_fontSize, 
	    				getResources().getDimension(R.dimen.font_size));
			
	    	String typefaceName = attrs.getString(
	    			R.styleable.ChartAttrs_chart_typeface);
			if (typefaceName != null)
				typeface = Typeface.createFromAsset(getResources().
												getAssets(), typefaceName);
		}
		
		
		private void init(){
			

			
			chartPaint = new Paint();
			chartPaint.setColor(axisColor);
			chartPaint.setStyle(Paint.Style.STROKE);
			chartPaint.setStrokeWidth(axisThickness);
			chartPaint.setAntiAlias(true);

			labelPaint = new Paint();
			labelPaint.setColor(labelColor);
			labelPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			labelPaint.setAntiAlias(true);
			labelPaint.setTextSize(fontSize);
			labelPaint.setTypeface(typeface);
			
			gridPaint = new Paint();
			gridPaint.setColor(gridColor);
			gridPaint.setStyle(Paint.Style.STROKE);
			gridPaint.setAntiAlias(true);
			gridPaint.setStrokeWidth(1);
			if(hasGridDashed)
				gridPaint.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
		}

		
		public void clean() {
			
			chartPaint = null;
			labelPaint = null;
			gridPaint = null;
		}
	    
	}
	
	
}
