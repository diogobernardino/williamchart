package com.db.chartviewdemo;

import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.Point;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.StackBarChartView;
import com.db.williamchartdemo.R;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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

	

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
		mButton = (Button) findViewById(R.id.button);
		mButton.setTypeface(Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf"));
		
		mButton.setOnClickListener(new OnClickListener(){
			private int updateIndex = 1;
			@Override
			public void onClick(View v) {

				mButton.setEnabled(false);
				mButton.setText("I'M PLAYING...");
				
				switch(updateIndex){
					case 1: updateLineChart( mCurrLineSetSize = DataRetriever.randNumber(1, 3), 
								mCurrLineEntriesSize = DataRetriever.randNumber(5, 8));
							break;
					case 2: updateBarChart( mCurrBarSetSize = DataRetriever.randNumber(1, 3), 
								mCurrBarEntriesSize = DataRetriever.randNumber(4, 6));
							break;
					case 3: updateStackBarChart( mCurrStackBarSetSize = DataRetriever.randNumber(2, 3), 
								mCurrStackBarEntriesSize = DataRetriever.randNumber(4, 7));
							updateIndex = 0;
							break;
				}
				
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
		
		OnEntryClickListener lineListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				abstractOnClick(mLineChart, entryIndex, mCurrLineSetSize, mCurrLineEntriesSize);
			}
		};
		
		mLineChart = (LineChartView) findViewById(R.id.linechart);
		mLineChart//.setStep(2)
			.setOnEntryClickListener(lineListener)
			;
	}
	
	
	
	public void updateLineChart(int nSets, int nPoints){
		
		mLineChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			LineSet data = new LineSet();
			for(int j = 0; j < nPoints; j++)
				data.addPoint(new Point(mLabels[j], DataRetriever.randValue(MIN, MAX)));

			data.setDots(DataRetriever.randBoolean())
				.setDotsColor(Color.parseColor(DataRetriever.getColor(DataRetriever.randNumber(0,2))))
				.setDotsRadius(DataRetriever.randDimen(4,7))
				.setLineThickness(DataRetriever.randDimen(3,8))
				.setLineColor(Color.parseColor(DataRetriever.getColor(i)))
				.setFill(DataRetriever.hasFill(i))
				.setFillColor(Color.parseColor("#3388c6c3"))
				.setDashed(DataRetriever.randBoolean())
				.setSmooth(DataRetriever.randBoolean())
				;
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
			//.setLabels(DataRetriever.randBoolean())
			.setMaxAxisValue(10, 2)
			.animate(DataRetriever.randAnimation(mEndAction))
			//.show()
			;
		
	}
	
	
	
	
	
	
	/*------------------------------------*
	 *              BARCHART              *
	 *------------------------------------*/
	
	private void initBarChart(){
		
		OnEntryClickListener barListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				abstractOnClick(mBarChart, entryIndex, mCurrBarSetSize, mCurrBarEntriesSize);
			}
		};
		
		mBarChart = (BarChartView) findViewById(R.id.barchart);
		mBarChart.setOnEntryClickListener(barListener);
	}
	
	
	
	public void updateBarChart(int nSets, int nPoints){
		
		mBarChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			BarSet data = new BarSet();
			for(int j = 0; j <nPoints; j++){
				
				Bar bar = new Bar(mLabels[j], DataRetriever.randValue(MIN, MAX));
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
			//.setLabels(DataRetriever.randBoolean())
			.setMaxAxisValue(10, 2)
			.animate(DataRetriever.randAnimation(mEndAction))
			//.show()
			;
		
	}
	
	
	
	
	
	
	/*------------------------------------*
	 *           STACKBARCHART            *
	 *------------------------------------*/
	
	private void initStackBarChart(){
		
		OnEntryClickListener stackBarListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				abstractOnClick(mStackBarChart, entryIndex, mCurrStackBarSetSize, mCurrStackBarEntriesSize);
			}
		};
		
		mStackBarChart = (StackBarChartView) findViewById(R.id.stackbarchart);
		mStackBarChart.setStep(4)
			.setOnEntryClickListener(stackBarListener);
	}
	
	
	
	public void updateStackBarChart(int nSets, int nPoints){
		
		mStackBarChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			BarSet data = new BarSet();
			for(int j = 0; j <nPoints; j++){
				Bar bar = new Bar(mLabels[j], DataRetriever.randValue(MIN, MAX));
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
			//.setLabels(DataRetriever.randBoolean())
			.setMaxAxisValue(30, 5)
			.animate(DataRetriever.randAnimation(mEndAction))
			//.show()
			;
	}
	
	
	
	
	
	
	
	private void abstractOnClick(ChartView chartView, int clickedEntry, int setsSize, int entriesSize){
		
		mButton.setText(mLabels[clickedEntry]);
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
