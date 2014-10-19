package com.db.chartviewdemo;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.Point;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.YController;
import com.db.chart.view.XController;
import com.db.williamchartdemo.R;

import android.os.Build;
import android.os.Bundle;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.db.chartviewdemo.DataRetriever;

public class MainActivity extends ActionBarActivity {

	
	private final static String[] mLabels = {"ANT", "GNU", "OWL", "APE", "COD","YAK", "RAM", "JAY"};
	private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
	private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();
	
	private final static float LINE_MAX = 10;
	private final static float LINE_MIN = 1;
	private static LineChartView mLineChart;
	private TextView mLineTooltip;
	private static int mCurrLineEntriesSize;
	private static int mCurrLineSetSize;

	private final static float BAR_MAX = 10;
	private final static float BAR_MIN = 2;
	private static BarChartView mBarChart;
	private TextView mBarTooltip;
	private static int mCurrBarEntriesSize;
	private static int mCurrBarSetSize;

	private final static float STACKBAR_MAX = 10;
	private final static float STACKBAR_MIN = 4.8f;
	private static StackBarChartView mStackBarChart;
	private TextView mStackBarTooltip;
	private static int mCurrStackBarEntriesSize;
	private static int mCurrStackBarSetSize;
	
	
	private static Button mButton;


	private static Runnable mEndAction = new Runnable() {
        @Override
        public void run() {
        	mButton.setEnabled(true);
        	mButton.setText("PLAY ME");
        }
	};

	

	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     
        
		mButton = (Button) findViewById(R.id.button);
		mButton.setTypeface(Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf"));
		
		mButton.setOnClickListener(new OnClickListener(){
			private int updateIndex = 1;
			private boolean newInstance = true;
			@Override
			public void onClick(View v) {
				
				mLineChart.dismissAllTooltips();
				mLineTooltip = null;
				mBarChart.dismissAllTooltips();
				mBarTooltip = null;
				mStackBarChart.dismissAllTooltips();
				mStackBarTooltip = null;
				
				mButton.setEnabled(false);
				mButton.setText("I'M PLAYING...");
				
				switch(updateIndex){
					case 1: 
						if(newInstance)
							updateLineChart( mCurrLineSetSize = DataRetriever.randNumber(1, 3), 
												mCurrLineEntriesSize = DataRetriever.randNumber(5, 8));
						else
							updateValues(mLineChart, mCurrLineSetSize, mCurrLineEntriesSize);
						break;
					case 2: 
						if(newInstance)
							updateBarChart( mCurrBarSetSize = DataRetriever.randNumber(1, 3), 
												mCurrBarEntriesSize = DataRetriever.randNumber(4, 5));
						else
							updateValues(mBarChart, mCurrBarSetSize, mCurrBarEntriesSize);
						break;
					case 3: 
						if(newInstance)
							updateStackBarChart( mCurrStackBarSetSize = DataRetriever.randNumber(3, 4), 
													mCurrStackBarEntriesSize = DataRetriever.randNumber(4, 6));
						else
							updateValues(mStackBarChart, mCurrStackBarSetSize, mCurrStackBarEntriesSize);
						updateIndex = 0;
						break;
				}
				newInstance = !newInstance;
				updateIndex++;
			}
		});

		
		initLineChart();
		initBarChart();
		initStackBarChart();
		
		updateLineChart( mCurrLineSetSize = DataRetriever.randNumber(1, 3), 
							mCurrLineEntriesSize = DataRetriever.randNumber(5, 8));
		updateBarChart( mCurrBarSetSize = DataRetriever.randNumber(1, 3), 
							mCurrBarEntriesSize = DataRetriever.randNumber(4, 5));
		updateStackBarChart( mCurrStackBarSetSize = DataRetriever.randNumber(3, 4), 
							mCurrStackBarEntriesSize = DataRetriever.randNumber(4, 6));
		
	}
	
	
	
	
	
	
	/*------------------------------------*
	 *              LINECHART             *
	 *------------------------------------*/
	
	private void initLineChart(){
		
		final OnEntryClickListener lineEntryListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				if(mLineTooltip == null)
					showLineTooltip(entryIndex, rect);
				else
					dismissLineTooltip(entryIndex, rect);
			}
		};
		
		final OnClickListener lineClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mLineTooltip != null)
					dismissLineTooltip(-1, null);
			}
		};
		
		mLineChart = (LineChartView) findViewById(R.id.linechart);
		//mLineChart.setStep(2)
		mLineChart.setOnEntryClickListener(lineEntryListener);
		mLineChart.setOnClickListener(lineClickListener);
	}
	
	
	
	public void updateLineChart(int nSets, int nPoints){
		
		LineSet data;
		mLineChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			data = new LineSet();
			for(int j = 0; j < nPoints; j++)
				data.addPoint(new Point(mLabels[j], DataRetriever.randValue(LINE_MIN, LINE_MAX)));

			data.setDots(DataRetriever.randBoolean())
				.setDotsColor(Color.parseColor(DataRetriever.getColor(DataRetriever.randNumber(0,2))))
				.setDotsRadius(DataRetriever.randDimen(4,7))
				.setLineThickness(DataRetriever.randDimen(3,8))
				.setLineColor(Color.parseColor(DataRetriever.getColor(i)))
				.setDashed(DataRetriever.randBoolean())
				.setSmooth(DataRetriever.randBoolean())
				;
			
			if(i == 2){
				//data.setFill(Color.parseColor("#3388c6c3"));
				int[] colors = {Color.parseColor("#3388c6c3"), Color.TRANSPARENT};
				data.setGradientFill(colors, null);
			}
			
			if(DataRetriever.randBoolean())
				data.setDotsStrokeThickness(DataRetriever.randDimen(1,4))
				.setDotsStrokeColor(Color.parseColor(DataRetriever.getColor(DataRetriever.randNumber(0,2))))
				;

			mLineChart.addData(data);
		}
		
		mLineChart.setGrid(DataRetriever.randPaint())
			.setVerticalGrid(DataRetriever.randPaint())
			.setHorizontalGrid(DataRetriever.randPaint())
			//.setThresholdLine(2, randPaint())
			.setYLabels(YController.LabelPosition.NONE)
			.setYAxis(false)
			.setXLabels(DataRetriever.getXPosition())
			.setXAxis(DataRetriever.randBoolean())
			.setMaxAxisValue(10, 2)
			.animate(DataRetriever.randAnimation(mEndAction, nPoints))
			//.show()
			;
	}
	
	
	@SuppressLint("NewApi")
	private void showLineTooltip(int index, Rect rect){
		
		mLineTooltip = (TextView) getLayoutInflater().inflate(R.layout.circular_tooltip, null);
		mLineTooltip.setText(mLabels[index]);
		
        LayoutParams layoutParams = new LayoutParams((int)Tools.fromDpToPx(40), (int)Tools.fromDpToPx(40));
        layoutParams.leftMargin = rect.centerX() - layoutParams.width/2;
        layoutParams.topMargin = rect.centerY() - layoutParams.height/2;
        mLineTooltip.setLayoutParams(layoutParams);
        
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
        	mLineTooltip.setPivotX(layoutParams.width/2);
        	mLineTooltip.setPivotY(layoutParams.height/2);
        	mLineTooltip.setAlpha(0);
        	mLineTooltip.setScaleX(0);
        	mLineTooltip.setScaleY(0);
        	mLineTooltip.animate()
	        	.setDuration(150)
	        	.alpha(1)
	        	.scaleX(1).scaleY(1)
	        	.rotation(360)
	        	.setInterpolator(enterInterpolator);
        }
        
        mLineChart.showTooltip(mLineTooltip);
	}
	
	
	
	@SuppressLint("NewApi")
	private void dismissLineTooltip(final int index, final Rect rect){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mLineTooltip.animate()
			.setDuration(100)
	    	.scaleX(0).scaleY(0)
	    	.alpha(0)
	    	.rotation(-360)
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					mLineChart.removeView(mLineTooltip);
					mLineTooltip = null;
					if(index != -1)
						showLineTooltip(index, rect);
				}
	    	});
		}else{
			mLineChart.dismissTooltip(mLineTooltip);
			mLineTooltip = null;
			if(index != -1)
				showLineTooltip(index, rect);
		}
	}
	
	
	
	
	
	
	/*------------------------------------*
	 *              BARCHART              *
	 *------------------------------------*/
	
	private void initBarChart(){
		
		final OnEntryClickListener barEntryListener = new OnEntryClickListener(){
			
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				
				if(mBarTooltip == null)
					showBarTooltip(entryIndex, rect);
				else
					dismissBarTooltip(entryIndex, rect);
			}
		};
		
		final OnClickListener barClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mBarTooltip != null)
					dismissBarTooltip(-1, null);
			}
		};
		
		mBarChart = (BarChartView) findViewById(R.id.barchart);
		mBarChart.setOnEntryClickListener(barEntryListener);
		mBarChart.setOnClickListener(barClickListener);
	}
	
	
	
	public void updateBarChart(int nSets, int nPoints){
		
		BarSet data;
		Bar bar;
		mBarChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			data = new BarSet();
			for(int j = 0; j < nPoints; j++){
				
				bar = new Bar(mLabels[j], DataRetriever.randValue(BAR_MIN, BAR_MAX));
				//bar.setColor(Color.parseColor(getColor(j)));
				data.addBar(bar);
			}
			
			data.setColor(Color.parseColor(DataRetriever.getColor(i)));
			mBarChart.addData(data);
		}
		
		mBarChart.setBarSpacing(DataRetriever.randDimen(13, 28));
		mBarChart.setSetSpacing(DataRetriever.randDimen(2, 7));
		mBarChart.setBarBackground(DataRetriever.randBoolean());
		mBarChart.setBarBackgroundColor(Color.parseColor("#37474f"));
		mBarChart.setRoundCorners(DataRetriever.randDimen(0,6));

		mBarChart.setBorderSpacing(DataRetriever.randDimen(5,15))
			.setGrid(DataRetriever.randPaint())
			.setHorizontalGrid(DataRetriever.randPaint())
			.setVerticalGrid(DataRetriever.randPaint())
			.setYLabels(YController.LabelPosition.NONE)
			.setYAxis(false)
			.setXLabels(XController.LabelPosition.OUTSIDE)
			.setXAxis(DataRetriever.randBoolean())
			//.setThresholdLine(2, randPaint())
			.setMaxAxisValue((int)BAR_MAX, 2)
			.animate(DataRetriever.randAnimation(mEndAction, nPoints))
			//.show()
			;	
	}
	
	
	@SuppressLint("NewApi")
	private void showBarTooltip(int index, Rect rect){

		mBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.tooltip, null);
		mBarTooltip.setText(""+mLabels[index].charAt(0));
		
		LayoutParams layoutParams = new LayoutParams(rect.width(), rect.height());	
		layoutParams.leftMargin = rect.left;
		layoutParams.topMargin = rect.top;
		mBarTooltip.setLayoutParams(layoutParams);
        
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
        	mBarTooltip.setAlpha(0);
        	mBarTooltip.setScaleY(0);
        	mBarTooltip.animate()
	        	.setDuration(200)
	        	.alpha(1)
	        	.scaleY(1)
	        	.setInterpolator(enterInterpolator);
        }
        
        mBarChart.showTooltip(mBarTooltip);
	}
	

	@SuppressLint("NewApi")
	private void dismissBarTooltip(final int index, final Rect rect){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mBarTooltip.animate()
			.setDuration(100)
	    	.scaleY(0)
	    	.alpha(0)
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					mBarChart.removeView(mBarTooltip);
					mBarTooltip = null;
					if(index != -1)
						showBarTooltip(index, rect);
				}
	    	});
		}else{
			mBarChart.dismissTooltip(mBarTooltip);
			mBarTooltip = null;
			if(index != -1)
				showBarTooltip(index, rect);
		}
	}
	
	
	
	
	
	/*------------------------------------*
	 *           STACKBARCHART            *
	 *------------------------------------*/
	
	private void initStackBarChart(){
		
		final OnEntryClickListener stackBarEntryListener = new OnEntryClickListener(){
			
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				
				if(mStackBarTooltip == null)
					showStackBarTooltip(entryIndex, rect);
				else
					dismissStackBarTooltip(entryIndex, rect);
			}
		};
		
		final OnClickListener stackBarClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mStackBarTooltip != null)
					dismissStackBarTooltip(-1, null);
			}
		};
		
		mStackBarChart = (StackBarChartView) findViewById(R.id.stackbarchart);
		mStackBarChart.setStep(4);
		mStackBarChart.setOnEntryClickListener(stackBarEntryListener);
		mStackBarChart.setOnClickListener(stackBarClickListener);
	}
	
	
	public void updateStackBarChart(int nSets, int nPoints){
		
		BarSet data;
		Bar bar;
		mStackBarChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			data = new BarSet();
			for(int j = 0; j <nPoints; j++){
				bar = new Bar(mLabels[j], DataRetriever.randValue(STACKBAR_MIN, STACKBAR_MAX));
				data.addBar(bar);
			}
			
			data.setColor(Color.parseColor(DataRetriever.getColor(i)));
			mStackBarChart.addData(data);
		}
		
		mStackBarChart.setBarSpacing(DataRetriever.randDimen(12, 27));
		mStackBarChart.setBarBackground(DataRetriever.randBoolean());
		mStackBarChart.setBarBackgroundColor(Color.parseColor("#37474f"));
		mStackBarChart.setRoundCorners(DataRetriever.randDimen(0,6));
		
		mStackBarChart.setBorderSpacing(DataRetriever.randDimen(5,15))
			.setGrid(DataRetriever.randPaint())
			.setHorizontalGrid(DataRetriever.randPaint())
			.setVerticalGrid(DataRetriever.randPaint())
			.setYLabels(YController.LabelPosition.NONE)
			.setYAxis(false)
			.setXLabels(DataRetriever.getXPosition())
			.setXAxis(DataRetriever.randBoolean())
			//.setThresholdLine(2, randPaint())
			.setMaxAxisValue(10*nSets, 5)
			.animate(DataRetriever.randAnimation(mEndAction, nPoints))
			//.show()
			;
	}
	
	
	
	@SuppressLint("NewApi")
	private void showStackBarTooltip(int index, Rect rect){
		
		mStackBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.tooltip, null);
		mStackBarTooltip.setText(""+mLabels[index].charAt(0));
		
		LayoutParams layoutParams = new LayoutParams(rect.width(), rect.height());	
		layoutParams.leftMargin = rect.left;
		layoutParams.topMargin = rect.top;
		mStackBarTooltip.setLayoutParams(layoutParams);
        
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
        	mStackBarTooltip.setPivotX(0);
        	mStackBarTooltip.setAlpha(0);
        	mStackBarTooltip.setScaleX(0);
        	mStackBarTooltip.animate()
	        	.setDuration(200)
	        	.alpha(1)
	        	.scaleX(1)
	        	.setInterpolator(enterInterpolator);
        }
        
        mStackBarChart.showTooltip(mStackBarTooltip);
	}
	

	@SuppressLint("NewApi")
	private void dismissStackBarTooltip(final int index, final Rect rect){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mStackBarTooltip.animate()
			.setDuration(100)
	    	.scaleX(0)
	    	.alpha(0)
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					mStackBarChart.removeView(mStackBarTooltip);
					mStackBarTooltip = null;
					if(index != -1)
						showStackBarTooltip(index, rect);
				}
	    	});
		}else{
			mStackBarChart.dismissTooltip(mStackBarTooltip);
			mStackBarTooltip = null;
			if(index != -1)
				showStackBarTooltip(index, rect);
		}
	}

	
	
	
	
	private void updateValues(ChartView chartView, int setsSize, int entriesSize){
		
		mButton.setEnabled(false);
		float[] newValues;
		for(int i = 0; i < setsSize; i++){
			
			newValues = new float[entriesSize];
			for(int j = 0; j < entriesSize; j++)
				newValues[j] = DataRetriever.randValue(STACKBAR_MIN, STACKBAR_MAX);
			
			chartView.updateValues(i, newValues);
		}
		
		chartView.notifyDataUpdate();
	}

	
}
