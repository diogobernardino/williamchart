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

import java.text.DecimalFormat;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.RelativeLayout;


/**
 * Abstract class to be extend to define any chart that implies axis.
 */
public abstract class ChartView extends RelativeLayout{


	private static final String TAG = "chart.view.ChartView";

	private static final int DEFAULT_GRID_ROWS = 5;
	private static final int DEFAULT_GRID_COLUMNS = 5;


	public enum GridType {
		FULL, VERTICAL, HORIZONTAL, NONE
	}

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}


	/** Chart orientation */
	private Orientation mOrientation;


	/** Chart borders */
	private int mChartTop;
	private int mChartBottom;
	private int mChartLeft;
	private int mChartRight;


	/** Horizontal and Vertical position controllers */
	final XController horController;
	final YController verController;


	/** Chart data to be displayed */
	ArrayList<ChartSet> data;


	/** Style applied to chart */
	final Style style;


	/** Threshold limit line value */
	private boolean mHasThresholdValue;
	private float mThresholdStartValue;
	private float mThresholdEndValue;
	private boolean mHasThresholdLabel;
	private int mThresholdStartLabel;
	private int mThresholdEndLabel;


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


	private GridType mGridType;
	private int mGridNRows;
	private int mGridNColumns;


	private Tooltip mTooltip;


	/**
	 * Executed only before the chart is drawn for the first time.
	 * . borders are defined
	 * . digestData(data), to process the data to be drawn
	 * . defineRegions(), if listener has been registered 
	 * 	 this will define the chart regions to handle by onTouchEvent
	 */
	final private OnPreDrawListener drawListener = new OnPreDrawListener(){
		@SuppressLint("NewApi")
		@Override
		public boolean onPreDraw() {
			ChartView.this.getViewTreeObserver().removeOnPreDrawListener(this);

			style.init();

			// Define chart frame
			mChartTop = getPaddingTop() + verController.getLabelHeight()/2;
			mChartBottom = getMeasuredHeight() - getPaddingBottom();
			mChartLeft = getPaddingLeft();
			mChartRight = getMeasuredWidth() - getPaddingRight();

			// Initialize controllers now that we have the measures
			verController.init();

			if(mHasThresholdValue) {
				mThresholdStartValue = verController.parsePos(0, mThresholdStartValue);
				mThresholdEndValue = verController.parsePos(0, mThresholdEndValue);
			}

			// Mandatory: X axis after Y axis!
			horController.init();

			// Process data to define screen positions
			digestData();

			// Tells view to execute code before starting drawing
			onPreDrawChart(data);

			// Define regions
			mRegions = defineRegions(data);

			// Prepares the animation if needed and gets the first dump 
			// of data to be drawn
			if(mAnim != null)
				data = mAnim.prepareEnterAnimation(ChartView.this);

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
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
		mHasThresholdValue = false;
		mHasThresholdLabel = false;
		mIsDrawing = false;
		data = new ArrayList<>();
		mRegions = new ArrayList<>();
		mGridType = GridType.NONE;
		mGridNRows = DEFAULT_GRID_ROWS;
		mGridNColumns = DEFAULT_GRID_COLUMNS;
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


	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int tmpWidth = widthMeasureSpec;
		int tmpHeight = heightMeasureSpec;

		if (widthMode == MeasureSpec.AT_MOST)
			tmpWidth = 200;

		if (heightMode == MeasureSpec.AT_MOST)
			tmpHeight = 100;

		setMeasuredDimension(tmpWidth, tmpHeight);
	}
	

	
	
	
	/*
	 * -------------------------
	 * Methods to be overridden
	 * -------------------------
	 */


	/**
	 * Convert {@link ChartEntry} values into screen points.
	 */
	private void digestData() {

		int nEntries = data.get(0).size();
		for(ChartSet set: data){
			for(int i = 0; i < nEntries; i++){
				set.getEntry(i)
						.setCoordinates(horController.parsePos(i, set.getValue(i)),
								verController.parsePos(i, set.getValue(i)));
			}
		}
	}



	/**
	 * (Optional) To be overridden in case the view needs to execute some code before
	 * starting the drawing.
	 *
	 * @param data   Array of {@link ChartSet} to do the necessary preparation just before onDraw
	 */
	void onPreDrawChart(ArrayList<ChartSet> data){}



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
	ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data){
		return mRegions;
	}



	/**
	 * Method responsible to draw bars with the parsed screen points.
	 *
	 * @param canvas   The canvas to draw on
	 * @param data   {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet}
	 *             to use while drawing the Chart
	 */
	abstract protected void onDrawChart(Canvas canvas, ArrayList<ChartSet> data);




	/**
	 * Set new data to the chart and invalidates the view to be then drawn.
	 *
	 * @param set   {@link ChartSet} object.
	 */
	public void addData(ChartSet set){

		if(!data.isEmpty() && set.size() != data.get(0).size())
			Log.e(TAG, "The number of entries between sets doesn't match.",
					new IllegalArgumentException());
		if(set == null)
			Log.e(TAG, "Chart data set can't be null",
					new IllegalArgumentException());

		data.add(set);
	}


	/**
	 * Add full chart data.
	 * @param data   An array of {@link ChartSet}
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
	 * Show only a specific chart dataset.
	 *
	 * @param setIndex   Dataset index to be displayed
	 */
	public void show(int setIndex){

		data.get(setIndex).setVisible(true);
		display();
	}

	/**
	 * Starts the animation given as parameter.
	 *
	 * @param anim   Animation used while showing and updating sets
	 */
	public void show(Animation anim){

		mAnim = anim;
		show();
	}


	/**
	 * Dismiss chart data.
	 */
	public void dismiss(){

		dismiss(mAnim);
	}

	/**
	 * Dismiss a specific chart dataset.
	 *
	 * @param setIndex   Dataset index to be dismissed
	 */
	public void dismiss(int setIndex){

		data.get(setIndex).setVisible(false);
		invalidate();
	}

	/**
	 * Dismiss chart data with animation.
	 *
	 * @param anim   Animation used to exit
	 */
	public void dismiss(Animation anim){

		if(anim != null) {
			mAnim = anim;

			final Runnable endAction = mAnim.getEndAction();
			mAnim.setEndAction(new Runnable() {
				@Override
				public void run() {
					if (endAction != null)
						endAction.run();
					data.clear();
					invalidate();
				}
			});

			data = mAnim.prepareExitAnimation(this);
		}else{
			data.clear();
		}
		invalidate();
	}



	/**
	 * Method not expected to be used often. More for testing.
	 * Resets chart state to insert new configuration.
	 */
	public void reset(){

		if(mAnim != null && mAnim.isPlaying())
			mAnim.cancel();

		init();
		if(horController.mandatoryBorderSpacing != 0) {
			horController.reset();
		}

		if(verController.mandatoryBorderSpacing != 0) {
			verController.reset();
		}

		mHasThresholdLabel = false;
		mHasThresholdValue = false;
		style.thresholdPaint = null;

		style.gridPaint = null;
	}


	/**
	 * Update set values. Animation support in case previously added.
	 *
	 * @param setIndex   Index of set to be updated
	 * @param values   Array of new values. Array length must match current data
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView updateValues(int setIndex, float[] values){

		if(values.length != data.get(setIndex).size())
			Log.e(TAG, "New values size doesn't match current dataset size.", new IllegalArgumentException());

		data.get(setIndex).updateValues(values);
		return this;
	}


	/**
	 * Notify {@link ChartView} about updated values. {@link ChartView} will be validated.
	 */
	public void notifyDataUpdate(){

		ArrayList<float[][]> oldCoords = new ArrayList<>(data.size());
		ArrayList<float[][]> newCoords = new ArrayList<>(data.size());

		for(ChartSet set : data)
			oldCoords.add(set.getScreenPoints());

		digestData();
		for(ChartSet set : data)
			newCoords.add(set.getScreenPoints());

		mRegions = defineRegions(data);
		if(mAnim != null)
			data = mAnim.prepareUpdateAnimation(this, oldCoords, newCoords);

		invalidate();
	}


	/**
	 * Set the {@link Tooltip} object which will be used to create chart tooltips.
	 *
	 * @param tip   {@link Tooltip} object in order to produce chart tooltips
	 */
	public void setTooltips(Tooltip tip){
		mTooltip = tip;
	}


	/**
	 * Toggles {@link Tooltip} between show and dismiss.
	 *
	 * @param rect   {@link Rect} containing the bounds of last clicked entry
	 * @param value   Value of the last entry clicked
	 */
	private void toggleTooltip(Rect rect, float value){

		if(!mTooltip.on()) {
			mTooltip.prepare(rect, value);
			showTooltip(mTooltip, true);
		}else {
			dismissTooltip(mTooltip, rect, value);
		}
	}


	/**
	 * Adds a tooltip to {@link ChartView}. If is not the case already,
	 * the whole tooltip is forced to be inside {@link ChartView} bounds.
	 *
	 * @param tooltip   {@link Tooltip} view to be added
	 * @param correctPos   False if tooltip should not be forced to be inside ChartView.
	 *               You may want to take care of it
	 */
	public void showTooltip(Tooltip tooltip, boolean correctPos) {

		if (correctPos) {
			tooltip.correctPosition(mChartLeft - getPaddingLeft(),
					mChartTop - getPaddingTop(),
					mChartRight - getPaddingRight(),
					(int) (getInnerChartBottom() - getPaddingBottom()));
		}

		if(tooltip.hasEnterAnimation())
			tooltip.animateEnter();

		this.addTooltip(tooltip);

	}


	/**
	 * Add {@link Tooltip}/{@link View}. to chart/parent view.
	 *
	 * @param tip   tooltip to be added to chart
	 */
	private void addTooltip(Tooltip tip){

		this.addView(tip);
		tip.setOn(true);
	}


	/**
	 * Remove {@link Tooltip}/{@link View} to chart/parent view.
	 *
	 * @param tip   tooltip to be removed to chart
	 */
	private void removeTooltip(Tooltip tip){
		this.removeView(tip);
		tip.setOn(false);
	}


	/**
	 * Dismiss tooltip from {@link ChartView}.
	 *
	 * @param tooltip   View to be dismissed
	 */
	private void dismissTooltip(Tooltip tooltip){
		dismissTooltip(tooltip, null, 0);
	}


	/**
	 * Dismiss tooltip from {@link ChartView}.
	 *
	 * @param tooltip   View to be dismissed
	 */
	private void dismissTooltip(final Tooltip tooltip, final Rect rect, final float value){

		if(tooltip.hasExitAnimation()) {
			tooltip.animateExit( new Runnable(){
				@Override
				public void run() {

					removeTooltip(tooltip);
					if(rect != null)
						toggleTooltip(rect, value);
				}
			});
		}else{
			this.removeTooltip(tooltip);
			if(rect != null)
				this.toggleTooltip(rect, value);
		}



	}


	/**
	 * Removes all tooltips currently presented in the chart.
	 */
	public void dismissAllTooltips(){
		this.removeAllViews();
		if(mTooltip != null)
			mTooltip.setOn(false);
	}



	/**
	 * Animate {@link ChartSet}.
	 *
	 * @param index   Position of {@link ChartSet}
	 * @param anim   Animation extending {@link BaseStyleAnimation}
	 */
	public void animateSet(int index, BaseStyleAnimation anim){
		anim.play(this, this.data.get(index));
	}


	/**
	 * Asks the view if it is able to draw now.
	 *
	 * @return {@link com.db.chart.view.ChartView} self-reference.
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
			if(mGridType == GridType.FULL || mGridType == GridType.VERTICAL)
				drawVerticalGrid(canvas);
			if(mGridType == GridType.FULL || mGridType == GridType.HORIZONTAL)
				drawHorizontalGrid(canvas);

			// Draw Axis Y
			verController.draw(canvas);

			// Draw threshold
			if (mHasThresholdValue)
				drawThreshold(canvas,
						getInnerChartLeft(),
						mThresholdStartValue,
						getInnerChartRight(),
						mThresholdEndValue);
			if (mHasThresholdLabel)
				drawThreshold(canvas,
						data.get(0).getEntry(mThresholdStartLabel).getX(),
						getInnerChartTop(),
						data.get(0).getEntry(mThresholdEndLabel).getX(),
						getInnerChartBottom());

			// Draw data
			if(!data.isEmpty())
				onDrawChart(canvas, data);

			// Draw axis X
			horController.draw(canvas);

			//System.out.println("Time drawing "+(System.currentTimeMillis() - time));
		}

		mIsDrawing = false;
	}



	/**
	 * Draw a threshold line or band on the labels or values axis. If same values or same label
	 * index have been given then a line will be drawn rather than a band.
	 *
	 * @param canvas   Canvas to draw line/band on.
	 * @param left   The left side of the line/band to be drawn
	 * @param top    The top side of the line/band to be drawn
	 * @param right  The right side of the line/band to be drawn
	 * @param bottom The bottom side of the line/band to be drawn
	 */
	private void drawThreshold(Canvas canvas, float left, float top, float right, float bottom) {

		if(left == right || top == bottom)
			canvas.drawLine(left, top, right, bottom, style.thresholdPaint);
		else
			canvas.drawRect(left, top, right, bottom, style.thresholdPaint);
	}



	/**
	 * Draw vertical lines of Grid.
	 *
	 * @param canvas   Canvas to draw on.
	 */
	private void drawVerticalGrid(Canvas canvas){

		final float offset = (getInnerChartRight() - getInnerChartLeft()) / mGridNColumns;
		float marker = getInnerChartLeft();

		if(verController.hasAxis)
			marker += offset;

		while (marker < getInnerChartRight()){
			canvas.drawLine(marker, getInnerChartTop(), marker, getInnerChartBottom(), style.gridPaint);
			marker += offset;
		}

		canvas.drawLine(getInnerChartRight(),
				getInnerChartTop(),
				getInnerChartRight(),
				getInnerChartBottom(),
				style.gridPaint);
	}



	/**
	 * Draw horizontal lines of Grid.
	 *
	 * @param canvas   Canvas to draw on.
	 */
	private void drawHorizontalGrid(Canvas canvas){

		final float offset = (getInnerChartBottom() - getInnerChartTop()) / mGridNRows;
		float marker = getInnerChartTop();
		while (marker < getInnerChartBottom()){
			canvas.drawLine(getInnerChartLeft(), marker, getInnerChartRight(), marker, style.gridPaint);
			marker += offset;
		}

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
	public boolean onTouchEvent(@NonNull MotionEvent event) {

		if(mAnim == null || !mAnim.isPlaying())

			if(event.getAction() == MotionEvent.ACTION_DOWN &&
					(mTooltip != null || mEntryListener != null) &&
					mRegions != null){

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

				if(mSetClicked != -1 &&
						mIndexClicked != -1){
					if(mRegions.get(mSetClicked).get(mIndexClicked)
							.contains((int) event.getX(),
									(int) event.getY())){

						if(mEntryListener != null){
							mEntryListener.onClick(mSetClicked,
									mIndexClicked,
									new Rect(getEntryRect(mRegions.get(mSetClicked).get(mIndexClicked))));
						}

						if(mTooltip != null){
							toggleTooltip(getEntryRect(mRegions.get(mSetClicked).get(mIndexClicked)),
									data.get(mSetClicked).getValue(mIndexClicked));
						}

					}
					mSetClicked = -1;
					mIndexClicked = -1;

				}else{

					if(mChartListener != null)
						mChartListener.onClick(this);

					if(mTooltip != null && mTooltip.on())
						dismissTooltip(mTooltip);
				}
			}

		return true;
	}

	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */


	public Orientation getOrientation(){
		return mOrientation;
	}


	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 *
	 * @return Position of the inner bottom side of the chart
	 */
	public float getInnerChartBottom(){
		return verController.getInnerChartBottom();
	}



	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 *
	 * @return Position of the inner left side of the chart
	 */
	public float getInnerChartLeft(){
		return verController.getInnerChartLeft();
	}



	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 *
	 * @return Position of the inner right side of the chart
	 */
	public float getInnerChartRight(){
		return horController.getInnerChartRight();
	}



	/**
	 * Inner Chart refers only to the area where chart data will be draw, 
	 * excluding labels, axis, etc.
	 *
	 * @return Position of the inner top side of the chart
	 */
	public float getInnerChartTop(){
		return mChartTop;
	}



	/**
	 * Returns the position of 0 value on chart.
	 *
	 * @return Position of 0 value on chart
	 */
	public float getZeroPosition(){

		if(mOrientation == Orientation.VERTICAL)
			return verController.parsePos(0, 0);
		else
			return horController.parsePos(0, 0);
	}



	/**
	 * Get the step used between Y values.
	 *
	 * @return step
	 */
	int getStep(){

		if(mOrientation == Orientation.VERTICAL)
			return verController.step;
		else
			return horController.step;
	}


	/**
	 * Get chart's border spacing.
	 *
	 * @return spacing
	 */
	float getBorderSpacing(){

		if(mOrientation == Orientation.VERTICAL)
			return horController.borderSpacing;
		else
			return verController.borderSpacing;
	}



	/**
	 * Get the whole data owned by the chart.
	 *
	 * @return   List of {@link com.db.chart.model.ChartSet} owned by the chart
	 */
	public ArrayList<ChartSet> getData(){
		return data;
	}



	/**
	 * Get the list of {@link android.graphics.Rect} associated to each entry of a ChartSet.
	 *
	 * @param index   {@link com.db.chart.model.ChartSet} index
	 * @return The list of {@link android.graphics.Rect} for the specified dataset
	 */
	public ArrayList<Rect> getEntriesArea(int index){

		ArrayList<Rect> result = new ArrayList<>(mRegions.get(index).size());
		for(Region r: mRegions.get(index))
			result.add(getEntryRect(r));

		return result;
	}



	/**
	 * Get the area, {@link android.graphics.Rect}, of an entry from the entry's {@link android.graphics.Region}
	 *
	 * @param region
	 * @return   {@link android.graphics.Rect} specifying the area of an Entry
	 */
	private Rect getEntryRect(Region region){
		return new Rect(region.getBounds().left - getPaddingLeft(),
				region.getBounds().top - getPaddingTop(),
				region.getBounds().right - getPaddingLeft(),
				region.getBounds().bottom - getPaddingTop());
	}



	/**
	 * Get the current {@link com.db.chart.view.animation.Animation}
	 * held by {@link com.db.chart.view.ChartView}.
	 * Useful, for instance, to define another endAction.
	 *
	 * @return   Current {@link com.db.chart.view.animation.Animation}
	 */
	public Animation getChartAnimation(){
		return mAnim;
	}



	int getChartTop(){
		return mChartTop;
	}

	int getChartBottom(){
		return mChartBottom;
	}

	int getChartLeft(){
		return mChartLeft;
	}

	int getChartRight(){
		return mChartRight;
	}

	/*
	 * --------
	 * Setters
	 * --------
	 */


	/**
	 * Sets the chart's orientation.
	 *
	 * @param orien   Orientation.HORIZONTAL | Orientation.VERTICAL
	 */
	void setOrientation(Orientation orien){

		mOrientation = orien;
		if(mOrientation == Orientation.VERTICAL) {
			verController.handleValues = true;
		}else{
			horController.handleValues = true;
		}
	}



	/**
	 * Show/Hide Y labels and respective axis.
	 *
	 * @param position   NONE - No labels
	 *                   OUTSIDE - Labels will be positioned outside the chart
	 *                   INSIDE - Labels will be positioned inside the chart
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setYLabels(YController.LabelPosition position){
		verController.labelsPositioning = position;
		return this;
	}



	/**
	 * Show/Hide X labels and respective axis.
	 *
	 * @param position   NONE - No labels
	 *                   OUTSIDE - Labels will be positioned outside the chart
	 *                   INSIDE - Labels will be positioned inside the chart
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setXLabels(XController.LabelPosition position){
		horController.labelsPositioning = position;
		return this;
	}



	/**
	 * Set the format to be added to Y labels.
	 *
	 * @param format   Format to be applied
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setLabelsFormat(DecimalFormat format){

		if(mOrientation == Orientation.VERTICAL)
			verController.labelFormat = format;
		else
			horController.labelFormat = format;

		return this;
	}


	/**
	 *
	 * @param color
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setLabelsColor(@ColorInt int color) {
		style.labelsColor = color;
		return this;
	}


	/**
	 *
	 * @param size
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setFontSize(@IntRange(from=0) int size) {
		style.fontSize = size;
		return this;
	}


	/**
	 *
	 * @param typeface
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setTypeface(Typeface typeface) {
		style.typeface = typeface;
		return this;
	}



	/**
	 * Show/Hide X axis.
	 *
	 * @param bool   If true axis won't be visible
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setXAxis(boolean bool){
		horController.hasAxis = bool;
		return this;
	}



	/**
	 * Show/Hide Y axis.
	 *
	 * @param bool   If true axis won't be visible
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setYAxis(boolean bool){
		verController.hasAxis = bool;
		return this;
	}



	/**
	 * A step is seen as the step to be defined between 2 labels. As an 
	 * example a step of 2 with a maxAxisValue of 6 will end up with 
	 * {0, 2, 4, 6} as labels.
	 *
	 * @param minValue   The minimum value that Y axis will have as a label
	 * @param maxValue   The maximum value that Y axis will have as a label
	 * @param step   (real) value distance from every label
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setAxisBorderValues(int minValue, int maxValue, int step){

		if(mOrientation == Orientation.VERTICAL)
			verController.setBorderValues(minValue, maxValue, step);
		else
			horController.setBorderValues(minValue, maxValue, step);

		return this;
	}



	/**
	 *
	 * @param minValue   The minimum value that Y axis will have as a label
	 * @param maxValue   The maximum value that Y axis will have as a label
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setAxisBorderValues(int minValue, int maxValue){

		if(mOrientation == Orientation.VERTICAL)
			verController.setBorderValues(minValue, maxValue);
		else
			horController.setBorderValues(minValue, maxValue);

		return this;
	}


	/**
	 * Define the thickness of the axis.
	 *
	 * @param thickness   size of the thickness
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setAxisThickness(@FloatRange(from=0.f) float thickness){
		style.axisThickness = thickness;
		return this;
	}


	/**
	 * Define the color of the axis.
	 *
	 * @param color   color of the axis
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setAxisColor(@ColorInt int color){
		style.axisColor = color;
		return this;
	}


	/**
	 * A step is seen as the step to be defined between 2 labels. 
	 * As an example a step of 2 with a max label value of 6 will end 
	 * up with {0, 2, 4, 6} as labels.
	 *
	 * @param step   (real) value distance from every label
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setStep(int step){

		if(step <= 0)
			throw new IllegalArgumentException("Step can't be lower or equal to 0");

		if(mOrientation == Orientation.VERTICAL)
			verController.step = step;
		else
			horController.step = step;

		return this;
	}



	/**
	 * Register a listener to be called when the chart is clicked.
	 *
	 * @param listener
	 */
	public void setOnEntryClickListener(OnEntryClickListener listener){
		this.mEntryListener = listener;
	}



	/**
	 * Register a listener to be called when the chart is clicked.
	 *
	 * @param listener
	 */
	@Override
	public void setOnClickListener(OnClickListener listener){
		this.mChartListener = listener;
	}



	/**
	 *
	 * @param spacing   Spacing between left/right of the chart and the first/last label
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setBorderSpacing(float spacing){

		if(mOrientation == Orientation.VERTICAL)
			horController.borderSpacing = spacing;
		else
			verController.borderSpacing = spacing;

		return this;
	}



	/**
	 *
	 * @param spacing   Spacing between top of the chart and the first label
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setTopSpacing(float spacing){

		if(mOrientation == Orientation.VERTICAL)
			verController.topSpacing = spacing;
		else
			horController.borderSpacing = spacing;

		return this;
	}



	/**
	 * Apply grid to chart.
	 *
	 * @param type   {@link GridType} for grid
	 * @param paint   The Paint instance that will be used to draw the grid
	 *                If null the grid won't be drawn
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setGrid(GridType type, Paint paint){

		mGridType = type;
		style.gridPaint = paint;
		return this;
	}


	/**
	 * Apply grid to chart.
	 *
	 * @param type   {@link GridType} for grid
	 * @param rows   Grid's number of rows
	 * @param columns Grid's number of columns
	 * @param paint   The Paint instance that will be used to draw the grid
	 *                If null the grid won't be drawn
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setGrid(GridType type,
							 @IntRange(from=1) int rows, @IntRange(from=1) int columns,
							 Paint paint){

		if(rows < 1 || columns < 1)
			throw new IllegalArgumentException("Number of rows/columns can't be lesser than 1.");

		mGridType = type;
		mGridNRows = rows;
		mGridNColumns = columns;
		style.gridPaint = paint;
		return this;
	}



	/**
	 * Display a value threshold either in a form of line or band.
	 * In order to produce a line, the start and end value will be equal.
	 *
	 * @param startValue   Threshold value.
	 * @param endValue   Threshold value.
	 * @param paint   The Paint instance that will be used to draw the grid
	 *                If null the grid won't be drawn
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setValueThreshold(float startValue, float endValue, Paint paint){

		mHasThresholdValue = true;
		mThresholdStartValue = startValue;
		mThresholdEndValue = endValue;
		style.thresholdPaint = paint;
		return this;
	}


	/**
	 * Display a label threshold either in a form of line or band.
	 * In order to produce a line, the start and end label will be equal.
	 *
	 * @param startLabel   Threshold start label index.
	 * @param endLabel   Threshold end label index.
	 * @param paint   The Paint instance that will be used to draw the grid
	 *                If null the grid won't be drawn
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setLabelThreshold(int startLabel, int endLabel, Paint paint){

		mHasThresholdLabel = true;
		mThresholdStartLabel = startLabel;
		mThresholdEndLabel = endLabel;
		style.thresholdPaint = paint;
		return this;
	}


	/**
	 * Set spacing between Labels and Axis. Will be applied to both X and Y.
	 *
	 * @param spacing   Spacing between labels and axis
	 * @return {@link com.db.chart.view.ChartView} self-reference.
	 */
	public ChartView setAxisLabelsSpacing(float spacing){

		horController.setAxisLabelsSpacing(spacing);
		verController.setAxisLabelsSpacing(spacing);
		return this;
	}


	/**
	 * Mandatory horizontal border when necessary (ex: BarCharts)
	 * Sets the attribute depending on the chart's orientation.
	 * e.g. If orientation is VERTICAL it means that this attribute must be handled
	 * by horizontal axis and not the vertical axis.
	 */
	void setMandatoryBorderSpacing(){

		if(mOrientation == Orientation.VERTICAL)
			horController.mandatoryBorderSpacing = 1;
		else
			verController.mandatoryBorderSpacing = 1;
	}



	/**
	 * Applies an alpha to the paint object.
	 *
	 * @param paint   {@link android.graphics.Paint} object to apply alpha.
	 * @param alpha   Alpha value (opacity).
	 * @param entry   Entry containing shadow customization.
	 */
	protected void applyShadow(Paint paint, float alpha, ChartEntry entry){

		paint.setShadowLayer(entry.getShadowRadius(), entry.getShadowDx(), entry.getShadowDy(),
				Color.argb(((int)(alpha * 255) < entry.getShadowColor()[0])
								? (int)(alpha * 255)
								: entry.getShadowColor()[0],
						entry.getShadowColor()[1],
						entry.getShadowColor()[2],
						entry.getShadowColor()[3]));
	}


	
	/*
	 * ----------
	 *    Style
	 * ----------
	 */

	/**
	 * Class responsible to style the Graph!
	 * Can be instantiated with or without attributes.
	 */
	class Style {

		private final static int DEFAULT_COLOR = Color.BLACK;


		/** Chart */
		Paint chartPaint;
		float axisThickness;
		int axisColor;


		/** Grid */
		Paint gridPaint;


		/** Threshold **/
		Paint thresholdPaint;

		/** Font */
		Paint labelsPaint;
		int labelsColor;
		float fontSize;
		Typeface typeface;


		Style() {

			axisColor = DEFAULT_COLOR;
			axisThickness = getResources().getDimension(R.dimen.grid_thickness);

			labelsColor = DEFAULT_COLOR;
			fontSize = getResources().getDimension(R.dimen.font_size);
		}


		Style(TypedArray attrs) {

			axisColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_axisColor,
					DEFAULT_COLOR);
			axisThickness = attrs.getDimension(
					R.styleable.ChartAttrs_chart_axisThickness,
					getResources().getDimension(R.dimen.axis_thickness));

			labelsColor = attrs.getColor(
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

			labelsPaint = new Paint();
			labelsPaint.setColor(labelsColor);
			labelsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			labelsPaint.setAntiAlias(true);
			labelsPaint.setTextSize(fontSize);
			labelsPaint.setTypeface(typeface);
		}


		public void clean() {

			chartPaint = null;
			labelsPaint = null;
			gridPaint = null;
			thresholdPaint = null;
		}

	}


}
