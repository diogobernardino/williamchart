package com.db.chartviewdemo;

import java.util.Random;

import com.db.chart.Tools;
import com.db.chart.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.Point;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.db.williamchartdemo.R;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	
	private static float MAX_VALUE = 9;
	private static float MIN_VALUE = 1;
	
	private LineChartView mLineChart;
	private BarChartView mBarChart;
	private TextView mTextView;
	private Button mButton;
	
	private String[] mColors = {"#009687","#FE5327","#3F51B5"};
	private String[] mLabels = {"ANT", "GNU", "OWL", "APE", "COD","YAK"};
	
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
			private boolean updateFirst = true;
			@Override
			public void onClick(View v) {
				mButton.setEnabled(false);
				mTextView.setText("PLAYING");
				if(updateFirst)
					updateLineChart(randNumber(1, 3), randNumber(4, 6));
				else
					updateBarChart(1, randNumber(4, 6));
				updateFirst = !updateFirst;
			}
		});

		
		OnEntryClickListener listener = new OnEntryClickListener(){
			@Override
			public void onClick(int setIndex, int entryIndex) {
				mTextView.setText(mLabels[entryIndex]);
			}
		};
		
		
		// Init LineChartView
		mLineChart = (LineChartView) findViewById(R.id.linechart);
		mLineChart.setStep(2)
			.setOnEntryClickListener(listener);
		
		// Init BarChartView
		mBarChart = (BarChartView) findViewById(R.id.barchart);
		mBarChart.setBorderSpacing(Tools.fromDpToPx(40))
			.setOnEntryClickListener(listener);
		
		updateLineChart(randNumber(1, 3), randNumber(4, 6));
		updateBarChart(1, randNumber(4, 6));

	}
	
	
	
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
		
		mLineChart.setGrid(randBoolean())
			.setHorizontalGrid(randBoolean())
			.setAnimation(new Animation()
							.setEasing(new QuintEaseOut())
								.setEndAction(mEndAction))
			.show();
	}
	
	
	
	public void updateBarChart(int nSets, int nPoints){
		
		mBarChart.reset();
		
		final BarSet data = new BarSet();
		for(int j = 0; j <nPoints; j++){
			Bar bar = new Bar(mLabels[j], randValue());
			bar.setColor(Color.parseColor(getColor(j)));
			data.addBar(bar);
		}
		
		mBarChart.addData(data);
		mBarChart.setBarSpacing(randDimen(15, 35));
		mBarChart.setGrid(randBoolean())
			//.setLabels(randBoolean())
			.setAnimation(new Animation()
							.setEasing(new QuintEaseOut())
								.setEndAction(mEndAction))
			.show();
	}
	
	
	
	/*
	 * Random data generation
	 * 
	 */
	
	private boolean randBoolean(){
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
		float ya = (new Random().nextFloat() * (MAX_VALUE - MIN_VALUE)) + MIN_VALUE;
	    return  ya;
	} 
	
	private boolean hasFill(int index){
		return (index == 2) ? true : false;
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
