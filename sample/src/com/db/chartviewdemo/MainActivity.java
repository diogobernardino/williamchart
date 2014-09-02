package com.db.chartviewdemo;

import java.util.Random;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.Point;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.bounce.BounceEaseOut;
import com.db.chart.view.animation.easing.elastic.ElasticEaseOut;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.db.williamchartdemo.R;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	
	private final static float MAX_VALUE = 9;
	private final static float MIN_VALUE = 1;
	
	private LineChartView mLineChart;
	private BarChartView mBarChart;
	private StackBarChartView mStackBarChart;
	
	private TextView mTextView;
	private Button mButton;
	
	private String[] mColors = {"#f36c60","#7986cb", "#4db6ac", "#aed581", "#ffb74d"};
	private String[] mLabels = {"ANT", "GNU", "OWL", "APE", "COD","YAK", "RAM", "JAY"};
	
	private int mCurrLineSetSize;
	private int mCurrBarSetSize;
	private int mCurrStackBarSetSize;
	
	private Runnable mEndAction = new Runnable() {
        @Override
        public void run() {
        	mButton.setEnabled(true);
        	mTextView.setText("onPointClick");
        }
	};
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setTypeface(Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf"));
        
        
		mButton = (Button) findViewById(R.id.button);
		mButton.setTypeface(Typeface.createFromAsset(getAssets(),"Roboto-Regular.ttf"));
		
		mButton.setOnClickListener(new OnClickListener(){
			private int updateIndex = 1;
			@Override
			public void onClick(View v) {
				mButton.setEnabled(false);
				mTextView.setText("PLAYING");
				switch(updateIndex){
					case 1: updateLineChart(randNumber(1, 3), mCurrLineSetSize = randNumber(5, 8));break;
					case 2: updateBarChart(randNumber(1, 3), mCurrBarSetSize = randNumber(4, 6));break;
					case 3: updateStackBarChart(randNumber(3, 5), mCurrStackBarSetSize = randNumber(4, 6));updateIndex = 0;break;
				}
				updateIndex++;
			}
		});

		
		initLineChart();
		initBarChart();
		initStackBarChart();
		
		updateLineChart(randNumber(1, 3), mCurrLineSetSize = randNumber(5, 8));
		updateBarChart(randNumber(1, 3), mCurrBarSetSize = randNumber(4, 6));
		updateStackBarChart(randNumber(3, 5), mCurrStackBarSetSize = randNumber(4, 5));
	}
	
	
	
	
	/*------------------------------------*
	 *              LINECHART             *
	 *------------------------------------*/
	
	public void updateLineChart(int nSets, int nPoints){
		
		mLineChart.reset();
		
		for(int i = 0; i < nSets; i++){
			
			LineSet data = new LineSet();
			for(int j = 0; j < nPoints; j++)
				data.addPoint(new Point(mLabels[j], randValue()));

			data.setDots(randBoolean())
				.setDotsColor(Color.parseColor(getColor(randNumber(0,2))))
				.setDotsRadius(randDimen(4,8))
				.setLineThickness(randDimen(3,9))
				.setLineColor(Color.parseColor(getColor(i)))
				.setFill(hasFill(i))
				.setFillColor(Color.parseColor("#3388c6c3"))
				.setDashed(randBoolean())
				.setSmooth(randBoolean());
			if(randBoolean())
				data.setDotsStrokeThickness(randDimen(1,4))
				.setDotsStrokeColor(Color.parseColor(getColor(randNumber(0,2))));

			mLineChart.addData(data);
		}
		
		mLineChart.setGrid(randPaint())
			.setMaxAxisValue(10, 2)
			//.setVerticalGrid(randPaint())
			.setHorizontalGrid(randPaint())
			//.setThresholdLine(2, randPaint())
			//.setLabels(true)
			.animate(randAnimation())
			//.show()
			;
	}
	
	
	
	
	/*------------------------------------*
	 *              BARCHART              *
	 *------------------------------------*/
	
	public void updateBarChart(int nSets, int nPoints){
		
		mBarChart.reset();
		
		for(int i = 0; i < nSets; i++){
			BarSet data = new BarSet();
			for(int j = 0; j <nPoints; j++){
				Bar bar = new Bar(mLabels[j], randValue());
				data.addBar(bar);
			}
			data.setColor(Color.parseColor(getColor(i)));
			mBarChart.addData(data);
		}
		
		mBarChart.setBarSpacing(randDimen(13, 28));
		mBarChart.setSetSpacing(randDimen(2, 7));
		mBarChart.setBarBackground(randBoolean());
		mBarChart.setBarBackgroundColor(Color.parseColor("#37474f"));
		mBarChart.setRoundCorners(randDimen(0,6));
		mBarChart.setGrid(randPaint())
			.setHorizontalGrid(randPaint())
			.setVerticalGrid(randPaint())
			.setAxisX(randBoolean())
			//.setThresholdLine(2, randPaint())
			//.setLabels(randBoolean())
			.setMaxAxisValue(10, 1)
			.animate(randAnimation())
			//.show()
			;
	}
	
	
	
	
	/*------------------------------------*
	 *           STACKBARCHART            *
	 *------------------------------------*/
	
	public void updateStackBarChart(int nSets, int nPoints){
		
		mStackBarChart.reset();
		
		for(int i = 0; i < nSets; i++){
			BarSet data = new BarSet();
			for(int j = 0; j <nPoints; j++){
				Bar bar = new Bar(mLabels[j], randValue());
				data.addBar(bar);
			}
			data.setColor(Color.parseColor(getColor(i)));
			mStackBarChart.addData(data);
		}
		
		mStackBarChart.setBarSpacing(randDimen(12, 27));
		mStackBarChart.setBarBackground(randBoolean());
		mStackBarChart.setBarBackgroundColor(Color.parseColor("#37474f"));
		mStackBarChart.setRoundCorners(randDimen(0,3));
		
		mStackBarChart.setGrid(randPaint())
			.setHorizontalGrid(randPaint())
			.setVerticalGrid(randPaint())
			.setAxisX(randBoolean())
			//.setLabels(true)
			.setMaxAxisValue(30, 2)
			.animate(randAnimation())
			//.show()
			;
	}
	
	
	
	
	/*---------------------*
	 *       INITs         *       
	 ----------------------*/
	
	private void initLineChart(){
		
		OnEntryClickListener lineListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				mTextView.setText(mLabels[entryIndex]);
				mButton.setEnabled(false);
				
				float[] newValues = new float[mCurrLineSetSize];
				for(int i = 0; i < mCurrLineSetSize; i++)
					newValues[i] = randValue();
				
				mLineChart.updateValues(setIndex, newValues);
			}
		};
		
		mLineChart = (LineChartView) findViewById(R.id.linechart);
		mLineChart//.setStep(2)
			.setOnEntryClickListener(lineListener);
	}

	
	private void initBarChart(){
		
		OnEntryClickListener barListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				mTextView.setText(mLabels[entryIndex]);
				mButton.setEnabled(false);
				
				float[] newValues = new float[mCurrBarSetSize];
				for(int i = 0; i < mCurrBarSetSize; i++)
					newValues[i] = randValue();
				
				mBarChart.updateValues(setIndex, newValues);
			}
		};
		
		mBarChart = (BarChartView) findViewById(R.id.barchart);
		mBarChart.setBorderSpacing(Tools.fromDpToPx(40))
			.setOnEntryClickListener(barListener);
	}

	
	private void initStackBarChart(){
		
		OnEntryClickListener stackBarListener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				mTextView.setText(mLabels[entryIndex]);
				mButton.setEnabled(false);
				
				float[] newValues = new float[mCurrStackBarSetSize];
				for(int i = 0; i < mCurrStackBarSetSize; i++)
					newValues[i] = randValue();
				
				mStackBarChart.updateValues(setIndex, newValues);
			}
		};
		
		mStackBarChart = (StackBarChartView) findViewById(R.id.stackbarchart);
		mStackBarChart.setStep(4)
			.setBorderSpacing(Tools.fromDpToPx(40))
			.setOnEntryClickListener(stackBarListener);
	}
	
	
	
	/*------------------------*
	 *						  * 
	 * Random data generation *
	 * 						  *
	 -------------------------*/
	
	
	private static boolean randBoolean(){
		return Math.random() < 0.5;
	}
	
	
	private static int randNumber(int min, int max) {
	    return new Random().nextInt((max - min) + 1) + min;
	} 
	
	
	private static float randDimen(float min, float max){
		float ya = (new Random().nextFloat() * (max - min)) + min;
	    return  Tools.fromDpToPx(ya);
	}
	
	
	private static float randValue() {
		return  (new Random().nextFloat() * (MAX_VALUE - MIN_VALUE)) + MIN_VALUE;
	} 
	
	
	private static Paint randPaint() {
		
		if(randBoolean()){
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#b0bec5"));
			paint.setStyle(Paint.Style.STROKE);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(Tools.fromDpToPx(1));
			if(randBoolean())
				paint.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
			return paint;
		}
		return null;
	}
	
	
	private static boolean hasFill(int index){
		return (index == 2) ? true : false;
	}
	
	
	private Animation randAnimation(){
		
		switch (new Random().nextInt(3)){
			case 0:
				return new Animation()
					.setEasing(new QuintEaseOut())
						.setEndAction(mEndAction);
			case 1:
				return new Animation()
				.setEasing(new BounceEaseOut())
					.setEndAction(mEndAction);
			default:
				return new Animation()
				.setAnimateInSequence(randBoolean())
				.setEasing(new ElasticEaseOut())
					.setEndAction(mEndAction);
		}
	}

	
	private String getColor(int index){
		
		switch (index){
			case 0:
				return mColors[0];
			case 1:
				return mColors[1];
			case 2:
				return mColors[2];
			case 3:
				return mColors[0];
			case 4:
				return mColors[1];
			default:
				return mColors[2];
		}
	}
	

	
}
