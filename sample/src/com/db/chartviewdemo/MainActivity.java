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
	
	
	private final static float MAX = 10;
	private final static float MIN = 1;
	private final static String[] mLabels = {"ANT", "GNU", "OWL", "APE", "COD","YAK", "RAM", "JAY"};
	
	
	private static LineChartView mLineChart;
	private static BarChartView mBarChart;
	private static StackBarChartView mStackBarChart;
	
	private static Button mButton;
	
	
	private static int mCurrLineEntriesSize;
	private static int mCurrBarEntriesSize;
	private static int mCurrStackBarEntriesSize;
	
	private static int mCurrLineSetSize;
	private static int mCurrBarSetSize;
	private static int mCurrStackBarSetSize;
	
	
	private static Runnable mEndAction = new Runnable() {
        @Override
        public void run() {
        	mButton.setEnabled(true);
        	mButton.setText("PLAY ME");
        }
	};

	
	private TextView mTooltip;
	private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
	private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();
	
	
	
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
				mBarChart.dismissAllTooltips();
				mStackBarChart.dismissAllTooltips();
				mTooltip = null;
				
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
								mCurrBarEntriesSize = DataRetriever.randNumber(4, 6));
						else
							updateValues(mBarChart, mCurrBarSetSize, mCurrBarEntriesSize);
						break;
					case 3: 
						if(newInstance)
							updateStackBarChart( mCurrStackBarSetSize = DataRetriever.randNumber(2, 3), 
								mCurrStackBarEntriesSize = DataRetriever.randNumber(4, 7));
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
							mCurrBarEntriesSize = DataRetriever.randNumber(4, 6));
		updateStackBarChart( mCurrStackBarSetSize = DataRetriever.randNumber(2, 4), 
							mCurrStackBarEntriesSize = DataRetriever.randNumber(4, 7));
		
	}
	
	
	
	
	
	
	/*------------------------------------*
	 *              LINECHART             *
	 *------------------------------------*/
	
	private void initLineChart(){
		
		final OnEntryClickListener lineEntryListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				if(mTooltip == null)
					showLineTooltip(mLineChart, entryIndex, rect);
				else
					dismissLineTooltip(mLineChart);
			}
		};
		
		final OnClickListener lineClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mTooltip != null)
					dismissLineTooltip(mLineChart);
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
				data.addPoint(new Point(mLabels[j], DataRetriever.randValue(MIN, MAX)));

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
			//.setVerticalGrid(randPaint())
			.setHorizontalGrid(DataRetriever.randPaint())
			//.setThresholdLine(2, randPaint())
			.setLabels(YController.NONE)
			.setMaxAxisValue(10, 2)
			.animate(DataRetriever.randAnimation(mEndAction))
			//.show()
			;
	}
	
	
	@SuppressLint("NewApi")
	private void showLineTooltip(ChartView chartView, int index, Rect rect){
		
		mTooltip = (TextView) getLayoutInflater().inflate(R.layout.circular_tooltip, null);
		mTooltip.setText(mLabels[index]);
		
        LayoutParams layoutParams = new LayoutParams((int)Tools.fromDpToPx(40), (int)Tools.fromDpToPx(40));
        layoutParams.leftMargin = rect.centerX() - layoutParams.width/2;
        layoutParams.topMargin = rect.centerY() - layoutParams.height/2;
        mTooltip.setLayoutParams(layoutParams);
        
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
        	mTooltip.setPivotX(layoutParams.width/2);
        	mTooltip.setPivotY(layoutParams.height/2);
        	mTooltip.setAlpha(0);
        	mTooltip.setScaleX(0);
        	mTooltip.setScaleY(0);
        	mTooltip.animate()
	        	.setDuration(150)
	        	.alpha(1)
	        	.scaleX(1).scaleY(1)
	        	.rotation(360)
	        	.setInterpolator(enterInterpolator);
        }
        
        chartView.showTooltip(mTooltip);
	}
	
	

	@SuppressLint("NewApi")
	private void dismissLineTooltip(final ChartView chartView){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mTooltip.animate()
			.setDuration(100)
	    	.scaleX(0).scaleY(0)
	    	.alpha(0)
	    	.rotation(-360)
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					chartView.removeView(mTooltip);
					mTooltip = null;
				}
	    	});
		}else{
			chartView.dismissTooltip(mTooltip);
			mTooltip = null;
		}
	}
	
	
	
	
	
	/*------------------------------------*
	 *              BARCHART              *
	 *------------------------------------*/
	
	private void initBarChart(){
		
		final OnEntryClickListener barEntryListener = new OnEntryClickListener(){
			
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				
				if(mTooltip == null)
					showBarTooltip(mBarChart, entryIndex, rect);
				else
					dismissBarTooltip(mBarChart);
			}
		};
		
		final OnClickListener barClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mTooltip != null)
					dismissBarTooltip(mBarChart);
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
			for(int j = 0; j <nPoints; j++){
				
				bar = new Bar(mLabels[j], DataRetriever.randValue(MIN, MAX));
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
			.setAxisX(DataRetriever.randBoolean())
			//.setThresholdLine(2, randPaint())
			.setLabels(YController.NONE)
			.setMaxAxisValue(10, 2)
			.animate(DataRetriever.randAnimation(mEndAction))
			//.show()
			;	
	}
	
	
	
	
	
	
	/*------------------------------------*
	 *           STACKBARCHART            *
	 *------------------------------------*/
	
	private void initStackBarChart(){
		
		final OnEntryClickListener stackBarEntryListener = new OnEntryClickListener(){
			
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				
				if(mTooltip == null)
					showBarTooltip(mStackBarChart, entryIndex, rect);
				else
					dismissBarTooltip(mStackBarChart);
			}
		};
		
		final OnClickListener stackBarClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mTooltip != null)
					dismissBarTooltip(mStackBarChart);
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
				bar = new Bar(mLabels[j], DataRetriever.randValue(MIN, MAX));
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
			.setAxisX(DataRetriever.randBoolean())
			//.setThresholdLine(2, randPaint())
			.setLabels(YController.NONE)
			.setMaxAxisValue(10*nSets, 5)
			.animate(DataRetriever.randAnimation(mEndAction))
			//.show()
			;
	}
	
	
	
	
	@SuppressLint("NewApi")
	private void showBarTooltip(ChartView chartView, int index, Rect rect){
		
		mTooltip = (TextView) getLayoutInflater().inflate(R.layout.tooltip, null);
		mTooltip.setText(mLabels[index]);
		
		LayoutParams layoutParams = new LayoutParams((int)Tools.fromDpToPx(50), (int)Tools.fromDpToPx(20));	
		layoutParams.leftMargin = rect.centerX();
		layoutParams.topMargin = rect.centerY();
		mTooltip.setLayoutParams(layoutParams);
        
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
        	mTooltip.setPivotX(0);
        	mTooltip.setPivotY(layoutParams.height/2);
        	mTooltip.setRotation(-90);
        	mTooltip.setAlpha(0);
        	mTooltip.setScaleX(0);
        	mTooltip.animate()
	        	.setDuration(150)
	        	.alpha(1)
	        	.scaleX(1)
	        	.translationX(0)
	        	.setInterpolator(enterInterpolator);
        }
        
        chartView.showTooltip(mTooltip, false);
	}
	

	@SuppressLint("NewApi")
	private void dismissBarTooltip(final ChartView chartView){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mTooltip.animate()
			.setDuration(100)
	    	.scaleX(0)
	    	.alpha(0)
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					chartView.removeView(mTooltip);
					mTooltip = null;
				}
	    	});
		}else{
			chartView.dismissTooltip(mTooltip);
			mTooltip = null;
		}
	}
	
	
	
	
	private void updateValues(ChartView chartView, int setsSize, int entriesSize){
		
		mButton.setEnabled(false);
		float[] newValues;
		for(int i = 0; i < setsSize; i++){
			
			newValues = new float[entriesSize];
			for(int j = 0; j < entriesSize; j++)
				newValues[j] = DataRetriever.randValue(MIN, MAX);
			
			chartView.updateValues(i, newValues);
		}
		
		chartView.notifyDataUpdate();
	}

	
}
