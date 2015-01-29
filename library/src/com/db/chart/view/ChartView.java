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
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.style.BaseStyleAnimation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.RelativeLayout;


/**
 * Abstract class to be extend to define any chart that implies axis.
 */
public abstract class ChartView extends RelativeLayout{
	
	
	private static final String TAG = "com.db.chart.view.ChartView";
	
	
	public static enum GridType {
		FULL, VERTICAL, HORIZONTAL
    }
	
	
	/** Chart borders */
	protected int chartTop;
	protected int chartBottom;
	protected int chartLeft;
	protected int chartRight;
	
	
	/** Horizontal and Vertical position controllers */
	protected XController horController;
	protected YController verController;
	
	
	/** Chart data to be displayed */
	protected ArrayList<ChartSet> data;
	
	
	/** Style applied to chart */
	protected Style style;
	
	
	
	/** Threshold limit line value */
	private float mThresholdValue;
	
	
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


	/** Chart animation */
	private Animation mAnim;
	
	
	/** Keep record of data updates to be done */
	private ArrayList<Pair<Integer, float []>> mToUpdateValues;
	
	
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
			chartTop = getPaddingTop() + verController.getLabelHeight()/2;
			chartBottom = getMeasuredHeight() - getPaddingBottom();
			chartLeft = getPaddingLeft();
			chartRight = getMeasuredWidth() - getPaddingRight();
	
			// Initialize controllers now that we have the measures
			verController.init();	
			mThresholdValue = verController.parseYPos(mThresholdValue);
			// Mandatory: X axis after Y axis!
			horController.init();
			
			// Process data to define screen positions
			digestData();
			
			// Tells view to execute code before starting drawing
			onPreDrawChart(data);
			
			// Sets listener if needed
			if(mEntryListener != null)
				mRegions = defineRegions(data);
			
			// Prepares the animation if needed and gets the first dump 
			// of data to be drawn
			if(mAnim != null)
				data = mAnim.prepareEnterAnimation(ChartView.this);
			
			if (android.os.Build.VERSION.SDK_INT >= 
					android.os.Build.VERSION_CODES.HONEYCOMB)
				ChartView.this.setLayerType(LAYER_TYPE_SOFTWARE, null);
				
			return mReadyToDraw = true;
		}
	};
	
	
	
	
	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		horController = new XController(this, 
				context.getTheme().obtainStyledAttributes(attrs, 
						R.styleable.ChartAttrs, 0, 0));
		verController = new YController(this, 
				context.getTheme().obtainStyledAttributes(attrs, 
						R.styleable.ChartAttrs, 0, 0));
		
		style = new Style(context.getTheme().obtainStyledAttributes(attrs, 
				R.styleable.ChartAttrs, 0, 0));
		
		init();
	}
	
	
	public ChartView(Context context) {
		super(context);
		
		horController = new XController(this);
		verController = new YController(this);
		
		style = new Style();
		
		init();
	}
	
	
	
	
	private void init(){
		
		mReadyToDraw = false;
		mSetClicked = -1;
		mIndexClicked = -1;
		mThresholdValue = 0;
		mIsDrawing = false;
		data = new ArrayList<ChartSet>();
		mRegions = new ArrayList<ArrayList<Region>>();
		mToUpdateValues = new ArrayList<Pair<Integer,float[]>>();
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
	 * -----------------------
	 * Methods to be overriden
	 * -----------------------
	 */
	
	
	/**
	 * Convert {@link ChartEntry} values into screen points.
	 */
	private void digestData() {
		int nEntries = data.get(0).size();
		for(ChartSet set: data){
			for(int i = 0; i < nEntries; i++){
				set.getEntry(i)
					.setCoordinates(horController.labelsPos.get(i), 
									verController.parseYPos(set.getValue(i)));
			}
		}
	}
	
	
	
	/**
	 * (Optional) To be overriden in case the view needs to execute some code before 
	 * starting the drawing.
	 * @param data - An array of {@link ChartSet}
	 */
	public void onPreDrawChart(ArrayList<ChartSet> data){}
	
	
	
	/**
	 * (Optional) To be overridden in order for each chart to define 
	 * its own clickable regions.
	 * This way, classes extending ChartView will only define their 
	 * clickable regions.
	 * @param data - An array of {@link ChartSet}
	 * @return ArrayList with regions where click will be detected.
	 * 	
	 * Important: the returned vector must match the order of the data passed 
	 * by the user. This ensures that onTouchEvent will return the correct index.
	 */
	protected ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data){
		return mRegions;
	};
	
	
	
	/**
	 * To be overridden in order for each chart to customize visualization.
	 * @param data - An array of {@link ChartSet}
	 */
	abstract public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data);
	

	
	
	
	/**
	 * Set new data to the chart and invalidates the view to be then drawn.
	 * @param set - A {@link ChartSet} object.
	 */
	public void addData(ChartSet set){
		
		if(!data.isEmpty() && set.size() != data.get(0).size())
			Log.e(TAG, "The number of labels between sets doesn't match.", 
					new IllegalArgumentException());
		
		data.add(set);
	}
	
	
	/**
	 * Add full chart data.
	 * @param data - An array of {@link ChartSet}
	 */
	public void addData(ArrayList<ChartSet> data){
		this.data = data;
	}
	
	
	/**
	 * Base method when a show chart occurs
	 */
	private void display(){
			
		this.getViewTreeObserver().addOnPreDrawListener(drawListener);
		postInvalidate();
	}
	
	/**
	 * Show chart data
	 */
	public void show(){
		
		for(ChartSet set : data)
			set.setVisible(true);
		display();
	}
	
	/**
	 * Show only a specific chart dataset
	 * @param setIndex - dataset's index to be displayed
	 */
	public void show(int setIndex){

		data.get(setIndex).setVisible(true);
		display();
	}
	
	/**
	 * Starts the animation given as parameter.
	 * @param anim - Animation used while showing and updating sets.
	 */
	public void show(Animation anim){
		
		mAnim = anim;
		show();
	}
	
	
	/**
	 * Dismiss chart data.
	 */
	public void dismiss(){
	
		data.clear();
		invalidate();
	}
	
	/**
	 * Dismiss a specific chart dataset
	 * @param setIndex - dataset's index to be dismissed
	 */
	public void dismiss(int setIndex){
		
		data.get(setIndex).setVisible(false);
		invalidate();
	}
	
	/**
	 * Dismiss chart data with animation.
	 * @param anim - animation used to exit.
	 */
	public void dismiss(Animation anim){
		
		mAnim = anim;
		
		final Runnable endAction = mAnim.getEndAction();
		mAnim.setEndAction(new Runnable() {
		        @Override
		        public void run() {
		        	if(endAction != null)
		        		endAction.run();
		            dismiss();
		        }
			});
		
		data = mAnim.prepareExitAnimation(this);
		invalidate();
	}
	
	
	
	/**
	 * Method not expected to be used often. More for testing.
	 * Resets chart state to insert new configuration.
	 */
	public void reset(){
		
		data.clear();
		mRegions.clear();
		mToUpdateValues.clear();
		verController.minLabelValue = 0;
		verController.maxLabelValue = 0;
		if(horController.mandatoryBorderSpacing != 0)
			horController.mandatoryBorderSpacing = 1;
		style.thresholdPaint = null;
		style.gridPaint = null;
		style.hasHorizontalGrid = false;
		style.hasVerticalGrid = false;
	}
	
	
	/**
	 * Update set values. Animation support in case previously added.
	 * @param setIndex - Index of set to be updated
	 * @param values - Array of new values. Array length must match current data.
	 */	
	public ChartView updateValues(int setIndex, float[] values){
		
		if(values.length != data.get(setIndex).size())
			Log.e(TAG, "New values size doesn't match current dataset size.", new IllegalArgumentException());

		data.get(setIndex).updateValues(values);
		return this;
	}

	/**
	 * Update set values. Animation support in case previously added.
	 * @param setIndex - Index of set to be updated
	 * @param values - Array of new values. Array length must match current data.
	 * @param xIndices - Array of relative positions of entries on the X axis
	 */
	public ChartView updateValues(int setIndex, float[] values, int[] xIndices){

		if(values.length != data.get(setIndex).size())
			Log.e(TAG, "New values size doesn't match current dataset size.", new IllegalArgumentException());

		data.get(setIndex).updateValues(values, xIndices);
		return this;
	}
	
	/**
	 * Notify ChartView about updated values. ChartView will be validated.
	 */
	public void notifyDataUpdate(){
		
		ArrayList<float[][]> oldCoords = new ArrayList<float[][]>(data.size());
		ArrayList<float[][]> newCoords = new ArrayList<float[][]>(data.size());
		
		for(ChartSet set : data)
			oldCoords.add(set.getScreenPoints());
		
		digestData();
		for(ChartSet set : data)
			newCoords.add(set.getScreenPoints());
		
		mRegions = defineRegions(data);
		if(mAnim != null)
			data = mAnim.prepareAnimation(this, oldCoords, newCoords);
		
		mToUpdateValues.clear();
		
		invalidate();
	}
	
	
	
	/**
	 * Adds a tooltip to ChartView. If is not the case already, 
	 * the whole tooltip is forced to be inside ChartView bounds.
	 * @param tooltip - tooltip view to be added.
	 * @param boolean - false if the tooltip should not be forced to be inside 
	 * ChartView. You may want to take care of it.
	 */
	public void showTooltip(View tooltip, boolean bool){
		
		if(bool){
			final LayoutParams layoutParams = (LayoutParams) tooltip.getLayoutParams();
			
			if(layoutParams.leftMargin < chartLeft - getPaddingLeft())
				layoutParams.leftMargin = (int) chartLeft - getPaddingLeft();
			if(layoutParams.topMargin < chartTop - getPaddingTop())
				layoutParams.topMargin = (int) chartTop - getPaddingTop();
			if(layoutParams.leftMargin + layoutParams.width 
					> chartRight - getPaddingRight())
				layoutParams.leftMargin -= layoutParams.width 
						- (chartRight - getPaddingRight() 
								- layoutParams.leftMargin);
			if(layoutParams.topMargin + layoutParams.height 
					> getInnerChartBottom() - getPaddingBottom())
				layoutParams.topMargin -= layoutParams.height 
						- (getInnerChartBottom() - getPaddingBottom() 
								- layoutParams.topMargin);
			
			tooltip.setLayoutParams(layoutParams);
		}
		
		this.addView(tooltip);
	}
	
	
	/**
	 * Adds a tooltip to ChartView. If is not the case already, 
	 * the whole tooltip is forced to be inside ChartView bounds.
	 * @param tooltip - tooltip view to be added.
	 */
	public void showTooltip(View tooltip){
		showTooltip(tooltip, true);
	}
	
	
	/**
	 * Removes tooltip from ChartView.
	 * @param tooltip - view to be removed.
	 */
	public void dismissTooltip(View tooltip){
		this.removeView(tooltip);
	}
	

	/**
	 * Removes all tooltips from ChartView.
	 */
	public void dismissAllTooltips(){
		this.removeAllViews();
	}
	
	
	/**
	 * Animate {@link ChartSet}
	 * @param index - position of {@link ChartSet}
	 * @param anim - animation extending {@link BaseStyleAnimation}
	 */
    public void animateSet(int index, BaseStyleAnimation anim){
    	anim.play(this, this.data.get(index));
    }
	
	
	/**
	 * Asks the view if it is able to draw now.
	 */
	public boolean canIPleaseAskYouToDraw(){
		return !mIsDrawing;
	}
	
	
	
	
	
	/*
	 * -------------
	 * Draw Methods
	 * -------------
	 */

	
	@Override
	protected void onDraw(Canvas canvas) {
		
		mIsDrawing = true;
		super.onDraw(canvas);
		
		if(mReadyToDraw){
			
			//long time = System.currentTimeMillis();
			
			// Draw grid
			if(style.hasVerticalGrid)
				drawVerticalGrid(canvas);
			if(style.hasHorizontalGrid)
				drawHorizontalGrid(canvas);
			
			// Draw Axis Y
			verController.draw(canvas);
						
			// Draw data
			if(!data.isEmpty())
				onDrawChart(canvas, data);
			
			// Draw axis X
			horController.draw(canvas);
			
			if(style.thresholdPaint != null)
				drawThresholdLine(canvas);
			
			//System.out.println("Time drawing "+(System.currentTimeMillis() - time));
		}
		
		mIsDrawing = false;
	}
	
	
	
	private void drawThresholdLine(Canvas canvas) {
		
		canvas.drawLine(getInnerChartLeft(), 
							mThresholdValue, 
								getInnerChartRight(), 
									mThresholdValue, 
										style.thresholdPaint);
	}


	
	private void drawVerticalGrid(Canvas canvas){
		
		// Draw vertical grid lines
		for(Float pos : horController.labelsPos){
			canvas.drawLine(pos, 
								getInnerChartBottom(), 
									pos, 
										getInnerChartTop(), 
											style.gridPaint);
		}
		
		// If border diff than 0 inner chart sides must have lines
		if(horController.borderSpacing != 0 || horController.mandatoryBorderSpacing != 0){
			if(verController.labelsPositioning == YController.LabelPosition.NONE)
				canvas.drawLine(getInnerChartLeft(), 
									getInnerChartBottom(), 
										getInnerChartLeft(), 
											getInnerChartTop(), 
												style.gridPaint);
			canvas.drawLine(getInnerChartRight(), 
								getInnerChartBottom(), 
									getInnerChartRight(), 
										getInnerChartTop(), 
											style.gridPaint);
		}
	}
	
	
	
	private void drawHorizontalGrid(Canvas canvas){
		
		// Draw horizontal grid lines
		for(Float pos : verController.labelsPos){
			canvas.drawLine(getInnerChartLeft(), 
								pos, 
									getInnerChartRight(),
										pos, 
											style.gridPaint);
		}
		
		// If there's no axis
		if(!horController.hasAxis)
			canvas.drawLine(getInnerChartLeft(), 
								getInnerChartBottom(), 
									getInnerChartRight(), 
										getInnerChartBottom(), 
											style.gridPaint);
	}
	
	
	
	
	
	/*
	 * --------------
	 * Click Handler
	 * --------------
	 */
	
	
	/**
	 * The method listens chart clicks and checks whether it intercepts
	 * a known Region. It will then use the registered Listener.onClick 
	 * to return the region's index. 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(mAnim == null || !mAnim.isPlaying())
			
			if(event.getAction() == MotionEvent.ACTION_DOWN &&
					mEntryListener != null && mRegions != null){
				
				//Check if ACTION_DOWN over any ScreenPoint region.
				int nSets = mRegions.size();
				int nEntries = mRegions.get(0).size();
				for(int i = 0; i < nSets ; i++){
					for(int j = 0; j < nEntries; j++){
								
						if(mRegions.get(i).get(j)
								.contains((int) event.getX(), 
									(int) event.getY())){
							mSetClicked = i;
							mIndexClicked = j;
						}
					}
				}
			}else if(event.getAction() == MotionEvent.ACTION_UP){
				
				if(mEntryListener != null && 
						mSetClicked != -1 && 
							mIndexClicked != -1){
					if(mRegions.get(mSetClicked).get(mIndexClicked)
								.contains((int)event.getX(), 
											(int)event.getY())){
					
						mEntryListener.onClick(mSetClicked, 
								mIndexClicked, 
									new Rect(mRegions.get(mSetClicked)
												.get(mIndexClicked)
													.getBounds().left - getPaddingLeft(),
											mRegions.get(mSetClicked)
												.get(mIndexClicked)
													.getBounds().top - getPaddingTop(),
											mRegions.get(mSetClicked)
												.get(mIndexClicked)
													.getBounds().right - getPaddingLeft(),
											mRegions.get(mSetClicked)
												.get(mIndexClicked)
													.getBounds().bottom - getPaddingTop()));
					}
					mSetClicked = -1;
					mIndexClicked = -1;
				}else if(mChartListener != null){
					mChartListener.onClick(this);
				}
			}
		
		return true;
	}
	
	
	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner bottom side of the chart
	 */
	public float getInnerChartBottom(){
		return verController.getInnerChartBottom();
	}

	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner left side of the chart
	 */
	public float getInnerChartLeft(){
		return verController.getInnerChartLeft();
	}
	
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner right side of the chart
	 */
	public float getInnerChartRight(){
		return horController.getInnerChartRight();
	}
	
	
	
	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 * @return position of the inner top side of the chart
	 */
	public float getInnerChartTop(){
		return chartTop;
	}
	
	
	
	/**
	 * Returns the position of 0 value on chart.
	 * @return position of 0 value on chart
	 */
	public float getZeroPosition(){
		return verController.parseYPos(0);
	}
	
	
	
	/**
	 * Get the step used between Y values
	 * @return step
	 */
	protected int getStep(){
		return verController.step;
	}
	
	
	
	/**
	 * @return Border between left/right of the chart and the first/last label
	 */
	public float getLabelBorderSpacing(){
		return horController.borderSpacing;
	}
	
	
	public ArrayList<ChartSet> getData(){
		return data;
	}
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	
	/**
	 * Show/Hide Y labels and respective axis
	 * @param YController.LabelPosition
	 * 	NONE - No labels
	 *  OUTSIDE - Labels will be positioned outside the chart
	 *  INSIDE - Labels will be positioned inside the chart
	 */
	public ChartView setYLabels(YController.LabelPosition position){
		verController.labelsPositioning = position;
		return this;
	}
	
	
	/**
	 * Show/Hide X labels and respective axis
	 * @param XController.LabelPosition
	 * 	NONE - No labels
	 *  OUTSIDE - Labels will be positioned outside the chart
	 *  INSIDE - Labels will be positioned inside the chart
	 */
	public ChartView setXLabels(XController.LabelPosition position){
		horController.labelsPositioning = position;
		return this;
	}
	
	
	/**
	 * Show/Hide X axis
	 * @param bool - if true axis won't be visible
	 */
	public ChartView setXAxis(boolean bool){
		horController.hasAxis = bool;
		return this;
	}
	
	
	/**
	 * Show/Hide Y axis
	 * @param bool - if true axis won't be visible
	 */
	public ChartView setYAxis(boolean bool){
		verController.hasAxis = bool;
		return this;
	}
	
	
	
	/**
	 * A step is seen as the step to be defined between 2 labels. As an 
	 * example a step of 2 with a maxAxisValue of 6 will end up with 
	 * {0, 2, 4, 6} as labels.
	 * @param maxAxisValue - the maximum value that Y axis will have as a label
	 * @param step - step - (real) value distance from every label
	 */
	public ChartView setAxisBorderValues(int minValue, int maxValue, int step){
		
		if((maxValue - minValue) % step != 0)
			Log.e(TAG, "Step value must be a divisor of distance between " +
					"minValue and maxValue", new IllegalArgumentException());
	
		verController.maxLabelValue = maxValue;
		verController.minLabelValue = minValue;
		verController.step = step;
		
		return this;
	}
	
	
	
	/**
	 * A step is seen as the step to be defined between 2 labels. 
	 * As an example a step of 2 with a max label value of 6 will end 
	 * up with {0, 2, 4, 6} as labels.
	 * @param step - (real) value distance from every label
	 */
	public ChartView setStep(int step){
		
		if(step <= 0)
			Log.e(TAG, "Step can't be lower or equal to 0", new IllegalArgumentException());
		
		verController.step = step;
		
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
	@Override
	public void setOnClickListener(OnClickListener listener){
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
	  
    
    
	/**
	 * @param spacing - Spacing between left/right of the chart and the 
	 * first/last label
	 */
	public ChartView setBorderSpacing(float spacing){
		horController.borderSpacing = spacing;
		return this;
	}
	
	
	
	/**
	 * @param spacing - Spacing between top of the chart and the first label
	 */
	public ChartView setTopSpacing(float spacing){
		verController.topSpacing = spacing;
		return this;
	}
	
	

	/**
	 * Apply grid to chart.
	 * @param type - {@link GridType} for grid.
	 * @param paint - The Paint instance that will be used to draw the grid. 
	 * If null the grid won't be drawn.
	 */
	public ChartView setGrid(GridType type, Paint paint){
		
		if(type.compareTo(GridType.FULL) == 0){
			style.hasVerticalGrid = true;
			style.hasHorizontalGrid = true;
		}else if(type.compareTo(GridType.VERTICAL) == 0){
			style.hasVerticalGrid = true;
		}else{
			style.hasHorizontalGrid = true;
		}
		style.gridPaint = paint;
		
		return this;
	}
	
	
	
	/**
	 * To set a threshold line to the chart.
	 * @param value - Threshold value.
	 * @param paint - The Paint instance that will be used to draw the grid. 
	 * If null the grid won't be drawn.
	 */
	public ChartView setThresholdLine(float value, Paint paint){
		
		mThresholdValue = value;
		style.thresholdPaint = paint;
		return this;
	}
	
	
	
	protected ChartView setMandatoryBorderSpacing(){
		horController.mandatoryBorderSpacing = 1;
		return this;
	}
	
	
	
	/**
	 * Set the metric to be added to Y labels.
	 * @param metric to be used.
	 */
	public ChartView setLabelsMetric(String metric){
		verController.labelMetric = metric;
		return this;
	}
	
	
	/*
	 * -------------------------
	 *          Style
	 * -------------------------
	 */

	
	/**
	 * Class responsible to style the Graph!
	 * Can be instantiated with or without attributes.
	 */
	class Style {
		
		private final static int DEFAULT_COLOR = -16777216;
		
		
		/** Chart */
		protected Paint chartPaint;
		protected float axisThickness;
		protected int axisColor;
		
		
		/** Grid */
		protected Paint gridPaint;
		protected boolean hasHorizontalGrid;
		protected boolean hasVerticalGrid;
		
		
		/** Threshold Line **/
		private Paint thresholdPaint;
		
		
		/** Font */
		protected Paint labelPaint;
		protected int labelColor;
		protected float fontSize;
		protected Typeface typeface;
		
		
		protected Style() {
			
			hasHorizontalGrid = false;
			hasVerticalGrid = false;
			
			axisColor = DEFAULT_COLOR;
			axisThickness = (float) getResources().getDimension(R.dimen.grid_thickness);
			
			labelColor = DEFAULT_COLOR;
			fontSize = getResources().getDimension(R.dimen.font_size);
		}

		
		protected Style(TypedArray attrs) {
			
			hasHorizontalGrid = false;
			hasVerticalGrid = false;
			
			axisColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_axisColor, 
						DEFAULT_COLOR);
			axisThickness = attrs.getDimension(
					R.styleable.ChartAttrs_chart_axisThickness, 
						getResources().getDimension(R.dimen.axis_thickness));
			
			labelColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_labelColor, DEFAULT_COLOR);
	    	fontSize = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_fontSize, 
	    				getResources().getDimension(R.dimen.font_size));
			
	    	String typefaceName = attrs.getString(R.styleable.ChartAttrs_chart_typeface);
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
		}

		
		public void clean() {
			
			chartPaint = null;
			labelPaint = null;
			gridPaint = null;
			thresholdPaint = null;
		}
	
		
		protected int getTextHeightBounds(String character){
			if(character != ""){
				Rect bounds = new Rect();
				style.labelPaint
					.getTextBounds(character, 
							0, 
								1, 
									bounds);
				return bounds.height();
			}
			return 0;
		}
		
	}
	

}
