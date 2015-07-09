package com.db.williamchartdemo;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ElasticEase;
import com.db.williamchartdemo.R;

import java.util.ArrayList;


public class BarFragment extends Fragment {


    /** First chart */
    private BarChartView mChartOne;
    private ImageButton mPlayOne;
    private boolean mUpdateOne;
    private final String[] mLabelsOne= {"10-15", "15-20", "20-25", "25-30", "30-35"};
    private final float [][] mValuesOne = {{9.5f, 7.5f, 5.5f, 4.5f, 10f}, {6.5f, 3.5f, 3.5f, 2.5f, 7.5f}};


    /** Second chart */
    private HorizontalBarChartView mChartTwo;
    private ImageButton mPlayTwo;
    private boolean mUpdateTwo;
    private final String[] mLabelsTwo= {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private final float [] mValuesTwo = {23f, 34f, 55f, 71f, 98f};
    private TextView mTextViewTwo;
    private TextView mTextViewMetricTwo;


    /** Third chart */
    private BarChartView mChartThree;
    private ImageButton mPlayThree;
    private boolean mUpdateThree;
    private final String[] mLabelsThree= {"", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", ""};
    private final float[] mValuesThree = {2.5f, 3.7f, 4f, 8f, 4.5f, 4f, 5f, 7f, 10f, 14f,
            12f, 6f, 7f, 8f, 9f, 8f, 9f, 8f, 7f, 6f,
            5f, 4f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 11f,
            12f, 14, 13f, 10f ,9f, 8f, 7f, 5f, 4f, 6f};


    public BarFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.bar, container, false);

        // Init first chart
        mUpdateOne = true;
        mChartOne = (BarChartView) layout.findViewById(R.id.barchart1);
        mPlayOne = (ImageButton) layout.findViewById(R.id.play1);
        mPlayOne.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                /*if(mUpdateOne)
                    updateChart(0, mChartOne, mPlayOne);
                else {
                    dismissChart(0, mChartOne, mPlayOne);
                }
                mUpdateOne = !mUpdateOne;*/
                dismissChart(0, mChartOne, mPlayOne);
            }
        });

        // Init second chart
        mUpdateTwo = true;
        mChartTwo = (HorizontalBarChartView) layout.findViewById(R.id.barchart2);
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
        mTextViewTwo = (TextView) layout.findViewById(R.id.value);
        mTextViewMetricTwo = (TextView) layout.findViewById(R.id.metric);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mTextViewTwo.setAlpha(0);
            mTextViewMetricTwo.setAlpha(0);
        }else{
            mTextViewTwo.setVisibility(View.INVISIBLE);
            mTextViewMetricTwo.setVisibility(View.INVISIBLE);
        }

        mTextViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(0).setDuration(100);
                    mTextViewMetricTwo.animate().alpha(0).setDuration(100);
                }
            }
        });

        // Init third chart
        mUpdateThree = true;
        mChartThree = (BarChartView) layout.findViewById(R.id.barchart3);
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

        switch(tag) {
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

    public void produceOne(ChartView chart, Runnable action){
        BarChartView barChart = (BarChartView) chart;

        barChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                System.out.println("OnClick "+rect.left);
            }
        });

        Tooltip tooltip = new Tooltip(getActivity(), R.layout.barchart_one_tooltip);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
        }
        barChart.setTooltips(tooltip);

        BarSet barSet = new BarSet(mLabelsOne, mValuesOne[0]);
        barSet.setColor(Color.parseColor("#a8896c"));
        barChart.addData(barSet);

        barSet = new BarSet(mLabelsOne, mValuesOne[1]);
        barSet.setColor(Color.parseColor("#c33232"));
        barChart.addData(barSet);

        barChart.setSetSpacing(Tools.fromDpToPx(-15));
        barChart.setBarSpacing(Tools.fromDpToPx(35));
        barChart.setRoundCorners(Tools.fromDpToPx(2));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#8986705C"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        barChart.setBorderSpacing(5)
                .setAxisBorderValues(0, 10, 2)
                .setGrid(BarChartView.GridType.FULL, gridPaint)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#86705c"))
                .setAxisColor(Color.parseColor("#86705c"));

        int[] order = {2, 1, 3, 0, 4};
        final Runnable auxAction = action;
        Runnable chartOneAction = new Runnable() {
            @Override
            public void run() {
                showTooltipOne();
                auxAction.run();
            }
        };
        barChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(chartOneAction))
        //.show()
        ;
    }

    public void updateOne(ChartView chart){

        dismissTooltipOne();
        float [][]newValues = {{8.5f, 6.5f, 4.5f, 3.5f, 9f}, {5.5f, 3.0f, 3.0f, 2.5f, 7.5f}};
        chart.updateValues(0, newValues[0]);
        chart.updateValues(1, newValues[1]);
        chart.notifyDataUpdate();
    }

    public void dismissOne(ChartView chart, Runnable action){

        dismissTooltipOne();
        int[] order = {0, 4, 1, 3, 2};
        chart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }


    private void showTooltipOne(){

        ArrayList<ArrayList<Rect>> areas = new ArrayList<>();
        areas.add(mChartOne.getEntriesArea(0));
        areas.add(mChartOne.getEntriesArea(1));

        for(int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {

                Tooltip tooltip = new Tooltip(getActivity(), R.layout.barchart_one_tooltip, R.id.value);
                tooltip.prepare(areas.get(i).get(j), mValuesOne[i][j]);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                    tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
                }
                mChartOne.showTooltip(tooltip, true);
            }
        }

    }


    private void dismissTooltipOne(){
        mChartOne.dismissAllTooltips();
    }


    /**
     *
     * Chart 2
     *
     */

    public void produceTwo(ChartView chart, Runnable action){
        HorizontalBarChartView horChart = (HorizontalBarChartView) chart;

        Tooltip tip = new Tooltip(getActivity(), R.layout.barchart_two_tooltip);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0));
        }

        horChart.setTooltips(tip);


        horChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                mTextViewTwo.setText(Integer.toString((int) mValuesTwo[entryIndex]));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(1).setDuration(200);
                    mTextViewMetricTwo.animate().alpha(1).setDuration(200);
                }else{
                    mTextViewTwo.setVisibility(View.VISIBLE);
                    mTextViewMetricTwo.setVisibility(View.VISIBLE);
                }
            }
        });

        horChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(0).setDuration(100);
                    mTextViewMetricTwo.animate().alpha(0).setDuration(100);
                }else{
                    mTextViewTwo.setVisibility(View.INVISIBLE);
                    mTextViewMetricTwo.setVisibility(View.INVISIBLE);
                }
            }
        });


        BarSet barSet = new BarSet();
        Bar bar;
        for(int i = 0; i < mLabelsTwo.length; i++){
            bar = new Bar(mLabelsTwo[i], mValuesTwo[i]);
            if(i == mLabelsTwo.length - 1 )
                bar.setColor(Color.parseColor("#b26657"));
            else if (i == 0)
                bar.setColor(Color.parseColor("#998d6e"));
            else
                bar.setColor(Color.parseColor("#506a6e"));
            barSet.addBar(bar);
        }
        horChart.addData(barSet);
        horChart.setBarSpacing(Tools.fromDpToPx(5));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#aab6b2ac"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        horChart.setBorderSpacing(0)
                .setAxisBorderValues(0, 100, 5)
                .setGrid(HorizontalBarChartView.GridType.FULL, gridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setXLabels(XController.LabelPosition.NONE);

        int[] order = {4, 3, 2, 1, 0};
        horChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action))
        ;
    }

    public void updateTwo(ChartView chart){

        chart.dismissAllTooltips();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mTextViewTwo.animate().alpha(0).setDuration(100);
            mTextViewMetricTwo.animate().alpha(0).setDuration(100);
        }else{
            mTextViewTwo.setVisibility(View.INVISIBLE);
            mTextViewMetricTwo.setVisibility(View.INVISIBLE);
        }

        float[] valuesTwoOne = {17f, 26f, 48f, 63f, 94f};
        chart.updateValues(0, valuesTwoOne);
        chart.notifyDataUpdate();
    }

    public void dismissTwo(ChartView chart, Runnable action){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mTextViewTwo.animate().alpha(0).setDuration(100);
            mTextViewMetricTwo.animate().alpha(0).setDuration(100);
        }else{
            mTextViewTwo.setVisibility(View.INVISIBLE);
            mTextViewMetricTwo.setVisibility(View.INVISIBLE);
        }

        chart.dismissAllTooltips();

        int[] order = {0, 1, 2, 3, 4};
        chart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }



    /**
     *
     * Chart 3
     *
     */

    public void produceThree(ChartView chart, Runnable action){
        BarChartView barChart = (BarChartView) chart;

        Tooltip tip = new Tooltip(getActivity(), R.layout.barchart_three_tooltip);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
            tip.setEnterAnimation(alpha);

            alpha = PropertyValuesHolder.ofFloat(View.ALPHA,0);
            tip.setExitAnimation(alpha);
        }

        barChart.setTooltips(tip);

        BarSet dataset = new BarSet(mLabelsThree, mValuesThree);
        dataset.setColor(Color.parseColor("#eb993b"));
        barChart.addData(dataset);

        barChart.setBarSpacing(Tools.fromDpToPx(3));

        barChart.setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false);

        Animation anim = new Animation()
                .setEasing(new ElasticEase())
                .setEndAction(action);

        chart.show(anim);
    }

    public void updateThree(ChartView chart){

        chart.dismissAllTooltips();

        float[] values = {3.5f, 4.7f, 5f, 9f, 5.5f, 5f, 6f, 8f, 11f, 13f,
                11f, 7f, 6f, 7f, 10f, 11f, 12f, 9f, 8f, 7f,
                6f, 5f, 4f, 3f, 6f, 7f, 8f, 9f, 10f, 12f,
                13f, 11, 13f, 10f ,8f, 7f, 5f, 4f, 3f, 7f};
        chart.updateValues(0, values);
        chart.notifyDataUpdate();
    }

    public void dismissThree(ChartView chart, Runnable action){

        chart.dismissAllTooltips();
        chart.dismiss(new Animation()
                .setEasing(new ElasticEase())
                .setEndAction(action));
    }


}
