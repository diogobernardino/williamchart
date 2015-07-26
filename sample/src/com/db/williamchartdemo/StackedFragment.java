package com.db.williamchartdemo;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ExpoEase;

import java.text.DecimalFormat;


public class StackedFragment extends Fragment {

    private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
    private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();


    /** First chart */
    private StackBarChartView mChartOne;
    private ImageButton mPlayOne;
    private boolean mUpdateOne;
    private final String[] mLabelsOne= {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
    private final float [][] mValuesOne = { {30f, 40f, 25f, 25f, 40f, 25f, 25f, 30f, 30f, 25f, 40f, 25f},
            {30f, 30f, 25f, 40f, 25f, 30f, 40f, 30f, 30f, 25f, 25f, 25f},
            {30f, 30f, 25f, 25f, 25f, 25f, 25f, 30f, 40f, 25, 25, 40f} };
    private TextView mLegendOneRed;
    private TextView mLegendOneYellow;
    private TextView mLegendOneGreen;


    /** Second chart */
    private HorizontalStackBarChartView mChartTwo;
    private ImageButton mPlayTwo;
    private boolean mUpdateTwo;
    private final String[] mLabelsTwo= {"0-20", "20-40", "40-60", "60-80", "80-100", "100+"};
    private final float [][] mValuesTwo = { {1.8f, 2f, 2.4f, 2.2f, 3.3f, 3.45f},
            {-1.8f, -2.1f, -2.55f, -2.40f, -3.40f, -3.5f}};


    /** Third chart */
    private HorizontalStackBarChartView mChartThree;
    private ImageButton mPlayThree;
    private boolean mUpdateThree;
    private TextView mTooltipThree;
    private final String[] mLabelsThree= {"", "", "", ""};
    private final float[][] mValuesThree = {{30f, 60f, 50f, 80f},{-70f, -40f, -50f, -20f}};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.stacked, container, false);

        // Init first chart
        mUpdateOne = true;
        mChartOne = (StackBarChartView) layout.findViewById(R.id.stackedchart1);
        mPlayOne = (ImageButton) layout.findViewById(R.id.play1);
        mPlayOne.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUpdateOne)
                    updateChart(0, mChartOne, mPlayOne);
                else {
                    dismissChart(0, mChartOne, mPlayOne);
                }
                mUpdateOne = !mUpdateOne;
            }
        });
        mLegendOneRed = (TextView) layout.findViewById(R.id.state_one);
        mLegendOneYellow = (TextView) layout.findViewById(R.id.state_two);
        mLegendOneGreen = (TextView) layout.findViewById(R.id.state_three);

        // Init second chart
        mUpdateTwo = true;
        mChartTwo = (HorizontalStackBarChartView) layout.findViewById(R.id.stackedchart2);
        mPlayTwo = (ImageButton) layout.findViewById(R.id.play2);
        mPlayTwo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUpdateTwo)
                    updateChart(1, mChartTwo, mPlayTwo);
                else
                    dismissChart(1, mChartTwo, mPlayTwo);
                mUpdateTwo = !mUpdateTwo;
            }
        });

        // Init third chart
        mUpdateThree = true;
        mChartThree = (HorizontalStackBarChartView) layout.findViewById(R.id.stackedchart3);
        mPlayThree = (ImageButton) layout.findViewById(R.id.play3);
        mPlayThree.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUpdateThree)
                    updateChart(2, mChartThree, mPlayThree);
                else
                    dismissChart(2, mChartThree, mPlayThree);
                mUpdateThree = !mUpdateThree;
            }
        });

        showChart(0, mChartOne, mPlayOne);
        showChart(1, mChartTwo, mPlayTwo);
        showChart(2, mChartThree, mPlayThree);
        return layout;
    }


    /**
     * Show a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void showChart(final int tag, final ChartView chart, final ImageButton btn){
        dismissPlay(btn);
        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showPlay(btn);
                    }
                }, 500);
            }
        };

        switch(tag){
            case 0:
                produceOne(chart, action); break;
            case 1:
                produceTwo(chart, action); break;
            case 2:
                produceThree(chart, action); break;
            default:
        }
    }


    /**
     * Update the values of a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void updateChart(final int tag, final ChartView chart, ImageButton btn){

        dismissPlay(btn);

        switch(tag){
            case 0:
                updateOne(chart); break;
            case 1:
                updateTwo(chart); break;
            case 2:
                updateThree(chart); break;
            default:
        }
    }


    /**
     * Dismiss a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void dismissChart(final int tag, final ChartView chart, final ImageButton btn){

        dismissPlay(btn);

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showPlay(btn);
                        showChart(tag, chart, btn);
                    }
                }, 500);
            }
        };

        switch(tag){
            case 0:
                dismissOne(chart, action); break;
            case 1:
                dismissTwo(chart, action); break;
            case 2:
                dismissThree(chart, action); break;
            default:
        }
    }


    /**
     * Show CardView play button
     * @param btn    Play button
     */
    private void showPlay(ImageButton btn){
        btn.setEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            btn.animate().alpha(1).scaleX(1).scaleY(1);
        else
            btn.setVisibility(View.VISIBLE);
    }


    /**
     * Dismiss CardView play button
     * @param btn    Play button
     */
    private void dismissPlay(ImageButton btn){
        btn.setEnabled(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            btn.animate().alpha(0).scaleX(0).scaleY(0);
        else
            btn.setVisibility(View.INVISIBLE);
    }



    /**
     *
     * Chart 1
     *
     */

    private void produceOne(ChartView chart, Runnable action){
        StackBarChartView stackedChart = (StackBarChartView) chart;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            stackedChart.setOnEntryClickListener(new OnEntryClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(int setIndex, int entryIndex, Rect rect) {
                    if(setIndex == 2)
                        mLegendOneRed.animate()
                                .scaleY(1.1f)
                                .scaleX(1.1f)
                                .setDuration(100)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLegendOneRed.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    else if(setIndex == 1){
                        mLegendOneYellow.animate()
                                .scaleY(1.1f)
                                .scaleX(1.1f)
                                .setDuration(100)
                                .withEndAction(new Runnable(){
                                    @Override
                                    public void run() {
                                        mLegendOneYellow.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    }else{
                        mLegendOneGreen.animate()
                                .scaleY(1.1f)
                                .scaleX(1.1f)
                                .setDuration(100)
                                .withEndAction(new Runnable(){
                                    @Override
                                    public void run() {
                                        mLegendOneGreen.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    }
                }
            });

        Paint thresPaint = new Paint();
        thresPaint.setColor(Color.parseColor("#dad8d6"));
        thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        thresPaint.setStyle(Paint.Style.STROKE);
        thresPaint.setAntiAlias(true);
        thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        BarSet stackBarSet = new BarSet(mLabelsOne, mValuesOne[0]);
        stackBarSet.setColor(Color.parseColor("#a1d949"));
        stackedChart.addData(stackBarSet);

        stackBarSet = new BarSet(mLabelsOne, mValuesOne[1]);
        stackBarSet.setColor(Color.parseColor("#ffcc6a"));
        stackedChart.addData(stackBarSet);

        stackBarSet = new BarSet(mLabelsOne, mValuesOne[2]);
        stackBarSet.setColor(Color.parseColor("#ff7a57"));
        stackedChart.addData(stackBarSet);

        stackedChart.setBarSpacing(Tools.fromDpToPx(15));
        stackedChart.setRoundCorners(Tools.fromDpToPx(1));

        stackedChart.setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setThresholdLine(89, thresPaint);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        stackedChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action))
        //.show()
        ;
    }

    private void updateOne(ChartView chart){

        chart.updateValues(0, mValuesOne[2]);
        chart.updateValues(1, mValuesOne[0]);
        chart.updateValues(2, mValuesOne[1]);
        chart.notifyDataUpdate();
    }

    private static void dismissOne(ChartView chart, Runnable action){
        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        chart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }



    /**
     *
     * Chart 2
     *
     */

    private void produceTwo(ChartView chart, Runnable action){
        HorizontalStackBarChartView horChart = (HorizontalStackBarChartView) chart;

        BarSet barSet = new BarSet(mLabelsTwo, mValuesTwo[0]);
        barSet.setColor(Color.parseColor("#90ee7e"));
        horChart.addData(barSet);

        barSet = new BarSet(mLabelsTwo, mValuesTwo[1]);
        barSet.setColor(Color.parseColor("#2b908f"));
        horChart.addData(barSet);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#e7e7e7"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.7f));

        horChart.setBarSpacing(Tools.fromDpToPx(8));

        horChart.setBorderSpacing(0)
                .setGrid(ChartView.GridType.VERTICAL, gridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setLabelsFormat(new DecimalFormat("##'M'"))
                .show(new Animation()
                        .setDuration(2500)
                        .setEasing(new ExpoEase())
                        .setEndAction(action))
        ;
    }

    private void updateTwo(ChartView chart){

        float [][] values = { {-1.8f, -2f, -2.4f, -2.2f, -3.3f, -3.45f},
                {1.8f, 2.1f, 2.55f, 2.40f, 3.40f, 3.5f}};
        chart.updateValues(0, values[1]);
        chart.updateValues(1, values[0]);
        chart.notifyDataUpdate();
    }

    private static void dismissTwo(ChartView chart, Runnable action){
        chart.dismiss(new Animation()
                .setDuration(2500)
                .setEasing(new ExpoEase())
                .setEndAction(action));
    }



    /**
     *
     * Chart 3
     *
     */

    private void produceThree(ChartView chart, Runnable action){
        HorizontalStackBarChartView barChart = (HorizontalStackBarChartView) chart;

        chart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                if (mTooltipThree == null)
                    showTooltipThree(entryIndex, rect);
                else
                    dismissTooltipThree(entryIndex, rect);
            }
        });

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTooltipThree != null)
                    dismissTooltipThree(-1, null);
            }
        });

        BarSet dataset = new BarSet(mLabelsThree, mValuesThree[0]);
        dataset.setColor(Color.parseColor("#687E8E"));
        barChart.addData(dataset);

        dataset = new BarSet(mLabelsThree, mValuesThree[1]);
        dataset.setColor(Color.parseColor("#FF5C8E67"));
        barChart.addData(dataset);

        barChart.setRoundCorners(Tools.fromDpToPx(5));
        barChart.setBarSpacing(Tools.fromDpToPx(8));

        barChart.setBorderSpacing(Tools.fromDpToPx(5))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(-80, 80, 10);

        Animation anim = new Animation()
                .setEndAction(action);

        chart.show(anim);
    }

    private void updateThree(ChartView chart){

        final float[][] values= {{50f, 70f, 10f, 30f},{-40f, -70f, -60f, -50f}};
        chart.updateValues(0, values[0]);
        chart.updateValues(1, values[1]);
        chart.notifyDataUpdate();
    }

    private static void dismissThree(ChartView chart, Runnable action){

        chart.dismiss(chart.getChartAnimation().setEndAction(action));
    }

    @SuppressLint("NewApi")
    private void showTooltipThree(int entryIndex, Rect rect){

        mTooltipThree = (TextView) getActivity().getLayoutInflater().inflate(R.layout.stacked_three_tooltip, null);
        mTooltipThree.setText(Integer.toString((int) mValuesThree[0][entryIndex]));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(rect.width(), rect.height());
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        mTooltipThree.setLayoutParams(layoutParams);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
            mTooltipThree.setAlpha(0);
            mTooltipThree.setScaleY(0);
            mTooltipThree.animate()
                    .setDuration(200)
                    .alpha(1)
                    .scaleY(1)
                    .setInterpolator(enterInterpolator);
        }
    }


    @SuppressLint("NewApi")
    private void dismissTooltipThree(final int entryIndex, final Rect rect){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mTooltipThree.animate()
                    .setDuration(100)
                    .scaleY(0)
                    .alpha(0)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable(){
                @Override
                public void run() {
                    mChartThree.removeView(mTooltipThree);
                    mTooltipThree = null;
                    if(entryIndex != -1)
                        showTooltipThree(entryIndex, rect);
                }
            });
        }else{
            mTooltipThree = null;
            if(entryIndex != -1)
                showTooltipThree(entryIndex, rect);
        }
    }
}
