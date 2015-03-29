package com.db.chartviewdemo;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.YController;
import com.db.chart.view.XController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.bounce.BounceEaseOut;
import com.db.chart.view.animation.easing.cubic.CubicEaseOut;
import com.db.chart.view.animation.easing.elastic.ElasticEaseOut;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.db.chart.view.animation.style.DashAnimation;
import com.db.williamchartdemo.R;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity {

	
	private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
	private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();
	
	
	/**
	 * Play
	 */
	private static ImageButton mPlayBtn;
	
	
	/**
	 * Order
	 */
	private static ImageButton mOrderBtn;
	private final static int[] beginOrder = {0, 1, 2, 3, 4, 5, 6};
	private final static int[] middleOrder = {3, 2, 4, 1, 5, 0, 6};
	private final static int[] endOrder = {6, 5, 4, 3, 2, 1, 0};
	private static float mCurrOverlapFactor;
	private static int[] mCurrOverlapOrder;
	private static float mOldOverlapFactor;
	private static int[] mOldOverlapOrder;
	
	
	/**
	 * Ease
	 */
	private static ImageButton mEaseBtn;
	private static BaseEasingMethod mCurrEasing;
	private static BaseEasingMethod mOldEasing;
	
	
	/**
	 * Enter
	 */
	private static ImageButton mEnterBtn;
	private static float mCurrStartX;
	private static float mCurrStartY;
	private static float mOldStartX;
	private static float mOldStartY;
	
	
	/**
	 * Alpha
	 */
	private static ImageButton mAlphaBtn;
	private static int mCurrAlpha;
	private static int mOldAlpha;
	
	
	private Handler mHandler;
	
	private final Runnable mEnterEndAction = new Runnable() {
        @Override
        public void run() {
        	mPlayBtn.setEnabled(true);
        }
	};
	
	private final Runnable mExitEndAction = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(new Runnable() { 
                public void run() {
            		mOldOverlapFactor = mCurrOverlapFactor;
                    mOldOverlapOrder = mCurrOverlapOrder;
            		mOldEasing = mCurrEasing;
            		mOldStartX = mCurrStartX;
            		mOldStartY = mCurrStartY;	
            		mOldAlpha = mCurrAlpha;
                	updateLineChart();
                	updateBarChart();
                    updateHorBarChart();
                	updateStackBarChart();
                }  
           }, 500);  
        }
	};
	
	private boolean mNewInstance;
	
	
	
	/**
	 * Line
	 */
	private final static int LINE_MAX = 10;
	private final static int LINE_MIN = -10;
	private final static String[] lineLabels = {"", "ANT", "GNU", "OWL", "APE", "JAY", ""};
	private final static float[][] lineValues = { {-5f, 6f, 2f, 9f, 0f, 1f, 5f},
													{-9f, -2f, -4f, -3f, -7f, -5f, -3f}};
	private static LineChartView mLineChart;
	private Paint mLineGridPaint;
	private TextView mLineTooltip;
	
	private final OnEntryClickListener lineEntryListener = new OnEntryClickListener(){
		@Override
		public void onClick(int setIndex, int entryIndex, Rect rect) {

			if(mLineTooltip == null)
				showLineTooltip(setIndex, entryIndex, rect);
			else
				dismissLineTooltip(setIndex, entryIndex, rect);
		}
	};
	
	private final OnClickListener lineClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(mLineTooltip != null)
				dismissLineTooltip(-1, -1, null);
		}
	};
	
	
	
	/**
	 * Bar
	 */
	private final static int BAR_MAX = 10;
	private final static int BAR_MIN = 0;
	private final static String[] barLabels = {"YAK", "ANT", "GNU", "OWL", "APE", "JAY", "COD"};
	private final static float [][] barValues = { {6.5f, 7.5f, 3.5f, 3.5f, 10f, 4.5f, 5.5f},
													{9.5f, 3.5f, 5.5f, 4.5f, 8.5f, 6.5f, 5.5f} };
	private static BarChartView mBarChart;
	private Paint mBarGridPaint;
	private TextView mBarTooltip;
	
	private final OnEntryClickListener barEntryListener = new OnEntryClickListener(){
		@Override
		public void onClick(int setIndex, int entryIndex, Rect rect) {
			if(mBarTooltip == null)
				showBarTooltip(setIndex, entryIndex, rect);
			else
				dismissBarTooltip(setIndex, entryIndex, rect);
		}
	};
	
	private final OnClickListener barClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(mBarTooltip != null)
				dismissBarTooltip(-1, -1, null);
		}
	};



    /**
     * HorizontalBar
     */
    private final static int HOR_BAR_MAX = 8;
    private final static int HOR_BAR_MIN = 0;
    private final static String[] horBarLabels = {"YAK", "ANT", "GNU", "OWL", "APE", "JAY", "COD"};
    private final static float [][] horBarValues = { {6f, 7f, 2f, 4f, 3f, 2f, 5f},
            {7f, 4f, 3f, 1f, 6f, 2f, 4f} };
    private static HorizontalBarChartView mHorBarChart;
    private Paint mHorBarGridPaint;
    private TextView mHorBarTooltip;

    private final OnEntryClickListener horBarEntryListener = new OnEntryClickListener(){
        @Override
        public void onClick(int setIndex, int entryIndex, Rect rect) {
            if(mHorBarTooltip == null)
                showHorBarTooltip(setIndex, entryIndex, rect);
            else
                dismissHorBarTooltip(setIndex, entryIndex, rect);
        }
    };

    private final OnClickListener horBarClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            if(mHorBarTooltip != null)
                dismissHorBarTooltip(-1, -1, null);
        }
    };

	
	
	/**
	 * StackBar
	 */
	private final static int STACKBAR_MAX = 100;
	private final static int STACKBAR_MIN = 0;
	private final static String[] stackBarLabels = {"YAK", "ANT", "GNU", "OWL", "APE", "JAY", "COD"};
	private final static float [][] stackBarValues = {
            {30f, 40f, 25f, 25f, 40f, 25f, 25f},
            {30f, 30f, 25f, 40f, 25f, 30f, 40f},
            {30f, 30f, 25f, 25f, 25f, 25f, 25f} };
	private static StackBarChartView mStackBarChart;
	private Paint mStackBarThresholdPaint; 
	private TextView mStackBarTooltip;
	
	private final OnEntryClickListener stackBarEntryListener = new OnEntryClickListener(){
		@Override
		public void onClick(int setIndex, int entryIndex, Rect rect) {
			if(mStackBarTooltip == null)
				showStackBarTooltip(setIndex, entryIndex, rect);
			else
				dismissStackBarTooltip(setIndex, entryIndex, rect);
		}
	};
	
	private final OnClickListener stackBarClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(mStackBarTooltip != null)
				dismissStackBarTooltip(-1, -1, null);
		}
	};

	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        mNewInstance = false;
		mCurrOverlapFactor = 1;
		mCurrEasing = new QuintEaseOut();
		mCurrStartX = -1;
		mCurrStartY = 0;	
		mCurrAlpha = -1;
		
		mOldOverlapFactor = 1;
		mOldEasing = new QuintEaseOut();
		mOldStartX = -1;
		mOldStartY = 0;	
		mOldAlpha = -1;
		
		mHandler = new Handler();
		
		initMenu();
		
		initLineChart();
		initBarChart();
        initHorBarChart();
		initStackBarChart();
		
		updateLineChart();
		updateBarChart();
        updateHorBarChart();
		updateStackBarChart();

	}
	
	
	
	
	/*------------------------------------*
	 *              LINECHART             *
	 *------------------------------------*/
	
	private void initLineChart(){
		
		mLineChart = (LineChartView) findViewById(R.id.linechart);
		mLineChart.setOnEntryClickListener(lineEntryListener);
		mLineChart.setOnClickListener(lineClickListener);
		
		mLineGridPaint = new Paint();
		mLineGridPaint.setColor(this.getResources().getColor(R.color.line_grid));
		mLineGridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
		mLineGridPaint.setStyle(Paint.Style.STROKE);
		mLineGridPaint.setAntiAlias(true);
		mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
	}
	
	
	private void updateLineChart(){
		
		mLineChart.reset();
		
		LineSet dataSet = new LineSet(lineLabels, lineValues[0]);
		dataSet.setDotsColor(this.getResources().getColor(R.color.line_bg))
			.setDotsRadius(Tools.fromDpToPx(5))
			.setDotsStrokeThickness(Tools.fromDpToPx(2))
			.setDotsStrokeColor(this.getResources().getColor(R.color.line))
			.setColor(this.getResources().getColor(R.color.line))
			.setThickness(Tools.fromDpToPx(3))
			.beginAt(1).endAt(lineLabels.length - 1);
		mLineChart.addData(dataSet);
		
		dataSet = new LineSet(lineLabels, lineValues[1]);
		dataSet.setColor(this.getResources().getColor(R.color.line))
			.setThickness(Tools.fromDpToPx(3))
			.setSmooth(true)
			.setDashed(new float[]{10, 10});
		mLineChart.addData(dataSet);
		
		mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
			.setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint)
			.setXAxis(false)
			.setXLabels(XController.LabelPosition.OUTSIDE)
			.setYAxis(false)
			.setYLabels(YController.LabelPosition.OUTSIDE)
			.setAxisBorderValues(LINE_MIN, LINE_MAX, 5)
			.setLabelsFormat(new DecimalFormat("##'u'"))
			.show(getAnimation(true).setEndAction(mEnterEndAction))
			//.show()
			;
		
		mLineChart.animateSet(1, new DashAnimation());
	}
	
	
	@SuppressLint("NewApi")
	private void showLineTooltip(int setIndex, int entryIndex, Rect rect){
		
		mLineTooltip = (TextView) getLayoutInflater().inflate(R.layout.circular_tooltip, null);
		mLineTooltip.setText(Integer.toString((int)lineValues[setIndex][entryIndex]));
		
        LayoutParams layoutParams = new LayoutParams((int)Tools.fromDpToPx(35), (int)Tools.fromDpToPx(35));
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
	private void dismissLineTooltip(final int setIndex, final int entryIndex, final Rect rect){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mLineTooltip.animate()
			.setDuration(100)
	    	.scaleX(0).scaleY(0)
	    	.alpha(0)
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					mLineChart.removeView(mLineTooltip);
					mLineTooltip = null;
					if(entryIndex != -1)
						showLineTooltip(setIndex, entryIndex, rect);
				}
	    	});
		}else{
			mLineChart.dismissTooltip(mLineTooltip);
			mLineTooltip = null;
			if(entryIndex != -1)
				showLineTooltip(setIndex, entryIndex, rect);
		}
	}
	
	
	private void updateValues(LineChartView chartView){
		
		chartView.updateValues(0, lineValues[1]);
		chartView.updateValues(1, lineValues[0]);
		chartView.notifyDataUpdate();
	}
	
	
	
	
	/*------------------------------------*
	 *              BARCHART              *
	 *------------------------------------*/
	
	private void initBarChart(){
		
		mBarChart = (BarChartView) findViewById(R.id.barchart);
		mBarChart.setOnEntryClickListener(barEntryListener);
		mBarChart.setOnClickListener(barClickListener);
		
		mBarGridPaint = new Paint();
		mBarGridPaint.setColor(this.getResources().getColor(R.color.bar_grid));
		mBarGridPaint.setStyle(Paint.Style.STROKE);
		mBarGridPaint.setAntiAlias(true);
		mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
	}
	
	
	private void updateBarChart(){
		
		mBarChart.reset();
		
		BarSet barSet = new BarSet();
		Bar bar;
		for(int i = 0; i < barLabels.length; i++){
			bar = new Bar(barLabels[i], barValues[0][i]);
			if(i == 4)
				bar.setColor(this.getResources().getColor(R.color.bar_highest));
			else
				bar.setColor(this.getResources().getColor(R.color.bar_fill1));
			barSet.addBar(bar);
		}
		mBarChart.addData(barSet);
		
		barSet = new BarSet(barLabels, barValues[1]);
		barSet.setColor(this.getResources().getColor(R.color.bar_fill2));
		mBarChart.addData(barSet);
		
		mBarChart.setSetSpacing(Tools.fromDpToPx(3));
        mBarChart.setBarSpacing(Tools.fromDpToPx(14));
		
		mBarChart.setBorderSpacing(0)
			.setAxisBorderValues(BAR_MIN, BAR_MAX, 2)
			.setGrid(BarChartView.GridType.FULL, mBarGridPaint)
			.setYAxis(false)
			.setXLabels(XController.LabelPosition.OUTSIDE)
			.setYLabels(YController.LabelPosition.NONE)
			.show(getAnimation(true).setEndAction(mEnterEndAction))
			//.show()
			;	
	}
	
	
	@SuppressLint("NewApi")
	private void showBarTooltip(int setIndex, int entryIndex, Rect rect){

		mBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.bar_tooltip, null);
		mBarTooltip.setText(Integer.toString((int) barValues[setIndex][entryIndex]));
		
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
	private void dismissBarTooltip(final int setIndex, final int entryIndex, final Rect rect){
		
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
					if(entryIndex != -1)
						showBarTooltip(setIndex, entryIndex, rect);
				}
	    	});
		}else{
			mBarChart.dismissTooltip(mBarTooltip);
			mBarTooltip = null;
			if(entryIndex != -1)
				showBarTooltip(setIndex, entryIndex, rect);
		}
	}
	
	
	private void updateValues(BarChartView chartView){
		
		chartView.updateValues(0, barValues[1]);
		chartView.updateValues(1, barValues[0]);
		chartView.notifyDataUpdate();
	}




    /*------------------------------------*
	 *         HORIZONTALBARCHART         *
	 *------------------------------------*/

    private void initHorBarChart(){

        mHorBarChart = (HorizontalBarChartView) findViewById(R.id.horbarchart);
        mHorBarChart.setOnEntryClickListener(horBarEntryListener);
        mHorBarChart.setOnClickListener(horBarClickListener);

        mHorBarGridPaint = new Paint();
        mHorBarGridPaint.setColor(this.getResources().getColor(R.color.bar_grid));
        mHorBarGridPaint.setStyle(Paint.Style.STROKE);
        mHorBarGridPaint.setAntiAlias(true);
        mHorBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
    }


    private void updateHorBarChart(){

        mHorBarChart.reset();

        BarSet barSet = new BarSet();
        Bar bar;
        for(int i = 0; i < horBarLabels.length; i++){
            bar = new Bar(horBarLabels[i], horBarValues[0][i]);
            bar.setColor(this.getResources().getColor(R.color.horbar_fill));
            barSet.addBar(bar);
        }
        mHorBarChart.addData(barSet);
        mHorBarChart.setBarSpacing(Tools.fromDpToPx(3));

        mHorBarChart.setBorderSpacing(0)
                .setAxisBorderValues(HOR_BAR_MIN, HOR_BAR_MAX, 2)
                .setGrid(HorizontalBarChartView.GridType.VERTICAL, mHorBarGridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.NONE)
                .show(getAnimation(true).setEndAction(mEnterEndAction))
        //.show()
        ;
    }


    @SuppressLint("NewApi")
    private void showHorBarTooltip(int setIndex, int entryIndex, Rect rect){

        mHorBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.horbar_tooltip, null);
        mHorBarTooltip.setText(Integer.toString((int) horBarValues[setIndex][entryIndex]));
        mHorBarTooltip.setIncludeFontPadding(false);

        LayoutParams layoutParams = new LayoutParams((int) Tools.fromDpToPx(15), (int) Tools.fromDpToPx(15));
        layoutParams.leftMargin = rect.right;
        layoutParams.topMargin = rect.top - (int) (Tools.fromDpToPx(15)/2 - (rect.bottom - rect.top)/2);
        mHorBarTooltip.setLayoutParams(layoutParams);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
            mHorBarTooltip.setAlpha(0);
            mHorBarTooltip.animate()
                    .setDuration(200)
                    .alpha(1)
                    .translationX(10)
                    .setInterpolator(enterInterpolator);
        }

        mHorBarChart.showTooltip(mHorBarTooltip);
    }


    @SuppressLint("NewApi")
    private void dismissHorBarTooltip(final int setIndex, final int entryIndex, final Rect rect){

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mHorBarTooltip.animate()
                    .setDuration(100)
                    .alpha(0)
                    .translationX(-10)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable(){
                @Override
                public void run() {
                    mHorBarChart.removeView(mHorBarTooltip);
                    mHorBarTooltip = null;
                    if(entryIndex != -1)
                        showHorBarTooltip(setIndex, entryIndex, rect);
                }
            });
        }else{
            mHorBarChart.dismissTooltip(mHorBarTooltip);
            mHorBarTooltip = null;
            if(entryIndex != -1)
                showHorBarTooltip(setIndex, entryIndex, rect);
        }
    }


    private void updateValues(HorizontalBarChartView chartView){

        chartView.updateValues(0, horBarValues[1]);
        chartView.notifyDataUpdate();
    }
	
	
	
	
	/*------------------------------------*
	 *           STACKBARCHART            *
	 *------------------------------------*/
	
	private void initStackBarChart(){
		
		mStackBarChart = (StackBarChartView) findViewById(R.id.stackbarchart);
		mStackBarChart.setOnEntryClickListener(stackBarEntryListener);
		mStackBarChart.setOnClickListener(stackBarClickListener);
		
		mStackBarThresholdPaint = new Paint();
		mStackBarThresholdPaint.setColor(this.getResources().getColor(R.color.stackbar_line));
		mStackBarThresholdPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
		mStackBarThresholdPaint.setStyle(Paint.Style.STROKE);
		mStackBarThresholdPaint.setAntiAlias(true);
		mStackBarThresholdPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
	}
	
	
	private void updateStackBarChart(){
		
		mStackBarChart.reset();
		
		BarSet stackBarSet = new BarSet();
		Bar bar;
		for(int i = 0; i < stackBarLabels.length; i++){
			bar = new Bar(stackBarLabels[i], stackBarValues[0][i]);
			if(i == 2)
				bar.setColor(this.getResources().getColor(R.color.stackbar_fill1_h));
			else
				bar.setColor(this.getResources().getColor(R.color.stackbar_fill1));
			stackBarSet.addBar(bar);
		}
		mStackBarChart.addData(stackBarSet);
		
		stackBarSet = new BarSet();
		for(int i = 0; i < 7; i++){
			bar = new Bar(stackBarLabels[i], stackBarValues[1][i]);
			if(i == 2)
				bar.setColor(this.getResources().getColor(R.color.stackbar_fill2_h));
			else
				bar.setColor(this.getResources().getColor(R.color.stackbar_fill2));
			stackBarSet.addBar(bar);
		}
		mStackBarChart.addData(stackBarSet);
		
		stackBarSet = new BarSet();
		for(int i = 0; i < 7; i++){
			bar = new Bar(stackBarLabels[i], stackBarValues[2][i]);
			if(i == 2)
				bar.setColor(this.getResources().getColor(R.color.stackbar_fill3_h));
			else
				bar.setColor(this.getResources().getColor(R.color.stackbar_fill3));
			stackBarSet.addBar(bar);
		}
		mStackBarChart.addData(stackBarSet);
		
		mStackBarChart.setBarSpacing(Tools.fromDpToPx(20));
		mStackBarChart.setRoundCorners(Tools.fromDpToPx(1));
		
		mStackBarChart.setStep(4)
			.setBorderSpacing(Tools.fromDpToPx(5))
			.setAxisBorderValues(STACKBAR_MIN, STACKBAR_MAX, 50)
			.setXAxis(false)
			.setXLabels(XController.LabelPosition.OUTSIDE)
			.setYAxis(true)
			.setYLabels(YController.LabelPosition.OUTSIDE)
			.setThresholdLine(89, mStackBarThresholdPaint)
			.setLabelsFormat(new DecimalFormat("###'%'"))
			.show(getAnimation(true).setEndAction(mEnterEndAction))
			//.show()
			;
	}
	
	
	@SuppressLint("NewApi")
	private void showStackBarTooltip(int setIndex, int entryIndex, Rect rect){
		
		mStackBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.stackbar_tooltip, null);
		mStackBarTooltip.setText(""+barLabels[entryIndex].charAt(setIndex));
		
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
	private void dismissStackBarTooltip(final int setIndex, 
			final int entryIndex, final Rect rect){
		
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
					if(entryIndex != -1)
						showStackBarTooltip(setIndex, entryIndex, rect);
				}
	    	});
		}else{
			mStackBarChart.dismissTooltip(mStackBarTooltip);
			mStackBarTooltip = null;
			if(entryIndex != -1)
				showStackBarTooltip(setIndex, entryIndex, rect);
		}
	}

	
	private void updateValues(StackBarChartView chartView){

		chartView.updateValues(0, stackBarValues[2]);
		chartView.updateValues(1, stackBarValues[0]);
		chartView.updateValues(2, stackBarValues[1]);
		chartView.notifyDataUpdate();
	}
	
	
	
	
	/*------------------------------------*
	 *                MENU                *
	 *------------------------------------*/
	
	private void initMenu(){
		
        mPlayBtn = (ImageButton) findViewById(R.id.play);
        mPlayBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mPlayBtn.setImageResource(R.drawable.play);
				mPlayBtn.setBackgroundResource(R.drawable.button);
				mPlayBtn.setEnabled(false);
				
				mLineChart.dismissAllTooltips();
				mLineTooltip = null;
				mBarChart.dismissAllTooltips();
				mBarTooltip = null;
                mHorBarChart.dismissAllTooltips();
                mHorBarTooltip = null;
				mStackBarChart.dismissAllTooltips();
				mStackBarTooltip = null;
				
				if(mNewInstance){
                    mStackBarChart.getChartAnimation().setEndAction(mExitEndAction);
					mLineChart.dismiss();
					mBarChart.dismiss();
                    mHorBarChart.dismiss();
					mStackBarChart.dismiss();
				}else{
					updateValues(mLineChart);
					updateValues(mBarChart);
                    updateValues(mHorBarChart);
					updateValues(mStackBarChart);
				}
				mNewInstance = !mNewInstance;
			}
		});
		
        
        mOrderBtn = (ImageButton) findViewById(R.id.order);
        mOrderBtn.setOnClickListener(new OnClickListener(){
        	private int index = 1;
        	@Override
			public void onClick(View v) {
				setOverlap(index++);
				index = onClickChange(index, 4);
			}
		});
		
        
		mEaseBtn = (ImageButton) findViewById(R.id.ease);
		mEaseBtn.setOnClickListener(new OnClickListener(){
			private int index = 1;
			@Override
			public void onClick(View v) {
				setEasing(index++);
				index = onClickChange(index, 4);
			}
		});
		
		
		mEnterBtn = (ImageButton) findViewById(R.id.enter);
		mEnterBtn.setOnClickListener(new OnClickListener(){
			private int index = 1;
			@Override
			public void onClick(View v) {
				setEnterPosition(index++);
				index= onClickChange(index, 9);
			}
		});
		
		
		mAlphaBtn = (ImageButton) findViewById(R.id.alpha);
		mAlphaBtn.setOnClickListener(new OnClickListener(){
			private int index = 1;
			@Override
			public void onClick(View v) {
				setAlpha(index++);
				index = onClickChange(index, 3);
			}
		});
	}
	
	
	private int onClickChange(int index, int limit){
		mPlayBtn.setBackgroundResource(R.color.button_hey);
		if(index >= limit)
			index = 0;
		mNewInstance = true;
		return index;
	}
	
	
	
	/*------------------------------------*
	 *               SETTERS              *
	 *------------------------------------*/
	
	private void setOverlap(int index){
		
		switch(index){
			case 0:
				mCurrOverlapFactor = 1;
				mCurrOverlapOrder = beginOrder;
				mOrderBtn.setImageResource(R.drawable.ordere);
				break;
			case 1:
				mCurrOverlapFactor = .5f;
				mCurrOverlapOrder = beginOrder;
				mOrderBtn.setImageResource(R.drawable.orderf);
				break;
			case 2:
				mCurrOverlapFactor = .5f;
				mCurrOverlapOrder = endOrder;
				mOrderBtn.setImageResource(R.drawable.orderl);
				break;
			case 3:
				mCurrOverlapFactor = .5f;
				mCurrOverlapOrder = middleOrder;
				mOrderBtn.setImageResource(R.drawable.orderm);
				break;
			default:
				break;
		}
	}
	
	private void setEasing(int index){

		switch(index){
			case 0:
				mCurrEasing = new CubicEaseOut();
				mEaseBtn.setImageResource(R.drawable.ease_cubic);
				break;
			case 1:
				mCurrEasing = new QuintEaseOut();
				mEaseBtn.setImageResource(R.drawable.ease_quint);
				break;
			case 2:
				mCurrEasing = new BounceEaseOut();
				mEaseBtn.setImageResource(R.drawable.ease_bounce);
				break;
			case 3:
				mCurrEasing = new ElasticEaseOut();
				mEaseBtn.setImageResource(R.drawable.ease_elastic);
			default:
				break;
		}
	}
	
	private void setEnterPosition(int index){

		switch(index){
			case 0:
				mCurrStartX = -1f;
				mCurrStartY = 0f;
				mEnterBtn.setImageResource(R.drawable.enterb);
				break;
			case 1:
				mCurrStartX = 0f;
				mCurrStartY = 0f;
				mEnterBtn.setImageResource(R.drawable.enterbl);
				break;
			case 2:
				mCurrStartX = 0f;
				mCurrStartY = -1f;
				mEnterBtn.setImageResource(R.drawable.enterl);
				break;
			case 3:
				mCurrStartX = 0f;
				mCurrStartY = 1f;
				mEnterBtn.setImageResource(R.drawable.entertl);
				break;
			case 4:
				mCurrStartX = -1f;
				mCurrStartY = 1f;
				mEnterBtn.setImageResource(R.drawable.entert);
				break;
			case 5:
				mCurrStartX = 1f;
				mCurrStartY = 1f;
				mEnterBtn.setImageResource(R.drawable.entertr);
				break;
			case 6:
				mCurrStartX = 1f;
				mCurrStartY = -1f;
				mEnterBtn.setImageResource(R.drawable.enterr);
				break;
			case 7:
				mCurrStartX = 1f;
				mCurrStartY = 0f;
				mEnterBtn.setImageResource(R.drawable.enterbr);
				break;
			case 8:
				mCurrStartX = .5f;
				mCurrStartY = .5f;
				mEnterBtn.setImageResource(R.drawable.enterc);
				break;
			default:
				break;
		}
	}
	
	
	@SuppressLint("NewApi")
	private void setAlpha(int index){

		switch(index){
			case 0:
				mCurrAlpha = -1;
				if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
					mAlphaBtn.setImageAlpha(255);
				else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
					mAlphaBtn.setAlpha(1f);
				break;
			case 1:
				mCurrAlpha = 2;
				if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
					mAlphaBtn.setImageAlpha(115);
				else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
					mAlphaBtn.setAlpha(.6f);
				break;
			case 2:
				mCurrAlpha = 1;
				if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
					mAlphaBtn.setImageAlpha(55);
				else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
					mAlphaBtn.setAlpha(.3f);
				break;
			default:
				break;
		}
	}
	
	
	
	/*------------------------------------*
	 *               GETTERS              *
	 *------------------------------------*/
	
	private Animation getAnimation(boolean newAnim){
		if(newAnim)
			return new Animation()
					.setAlpha(mCurrAlpha)
					.setEasing(mCurrEasing)
					.setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
					.setStartPoint(mCurrStartX, mCurrStartY);
		else
			return new Animation()
					.setAlpha(mOldAlpha)
					.setEasing(mOldEasing)
					.setOverlap(mOldOverlapFactor, mOldOverlapOrder)
					.setStartPoint(mOldStartX, mOldStartY);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.github:
	            startActivity(new Intent("android.intent.action.VIEW", 
	            		 			Uri.parse("https://github.com/diogobernardino/WilliamChart")));
	            return true;
	        case R.id.play:
	        	try { 
	        		startActivity(new Intent(Intent.ACTION_VIEW, 
							Uri.parse("market://details?id=" 
									+ this.getApplicationContext().getPackageName())));
	        	} catch (ActivityNotFoundException e) {
	        		startActivity(new Intent(Intent.ACTION_VIEW, 
	        								Uri.parse("http://play.google.com/store/apps/details?id=" 
	        											+ this.getApplicationContext().getPackageName())));
	        	} 
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
