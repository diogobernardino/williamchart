package com.db.williamchartdemo;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.style.DashAnimation;


public class LineFragment extends Fragment {


    /** First chart */
    private LineChartView mChartOne;
    private ImageButton mPlayOne;
    private boolean mUpdateOne;
    private final String[] mLabelsOne= {"", "10-15", "", "15-20", "", "20-25", "", "25-30", "", "30-35", ""};
    private final float[][] mValuesOne = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f, 7.3f, 5f},
            {2.5f, 3.5f, 3.5f, 7f, 5.5f, 8.5f, 6f, 6.3f, 5.8f, 6.3f, 4.5f},
            {1.5f, 2.5f, 2.5f, 4f, 2.5f, 5.5f, 5f, 5.3f, 4.8f, 5.3f, 3f}};


    /** Second chart */
    private LineChartView mChartTwo;
    private ImageButton mPlayTwo;
    private boolean mUpdateTwo;
    private final String[] mLabelsTwo= {"", "", "", "", "START", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "FINISH", "", "", "", ""};
    private final float[][] mValuesTwo = {{35f, 37f, 47f, 49f, 43f,46f, 80f, 83f, 65f, 68f, 100f,68f, 70f, 73f, 83f, 85f, 70f, 73f, 73f, 77f,
            33f, 15f, 18f, 25f, 28f, 25f, 28f, 40f, 43f, 25f, 28f, 55f, 58f, 50f, 53f, 53f, 57f, 48f, 50f, 53f, 54f,
            25f, 27f, 35f, 37f, 35f, 80f, 82f, 55f, 59f, 85f, 82f, 60f, 55f, 63f, 65f, 58f, 60f, 63f, 60f},
            {85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
                    85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
                    85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f}};


    /** Third chart */
    private LineChartView mChartThree;
    private ImageButton mPlayThree;
    private boolean mUpdateThree;
    private final String[] mLabelsThree= {"00", "04", "08", "12", "16", "20", "24"};
    private final float[][] mValuesThree = {  {4.5f, 5.7f, 4f, 8f, 2.5f, 3f, 6.5f},
                                        {1.5f, 2.5f, 1.5f, 5f, 5.5f, 5.5f, 3f},
                                        {8f, 7.5f, 7.8f, 1.5f, 8f, 8f, .5f}};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.line, container, false);

        // Init first chart
        mUpdateOne = true;
        mChartOne = (LineChartView) layout.findViewById(R.id.linechart1);
        mPlayOne = (ImageButton) layout.findViewById(R.id.play1);
        mPlayOne.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUpdateOne)
                    updateChart(0, mChartOne, mPlayOne);
                else
                    dismissChart(0, mChartOne, mPlayOne);
                mUpdateOne = !mUpdateOne;
            }
        });

        // Init second chart
        mUpdateTwo = true;
        mChartTwo = (LineChartView) layout.findViewById(R.id.linechart2);
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
        mChartThree = (LineChartView) layout.findViewById(R.id.linechart3);
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
    private void showChart(final int tag, final LineChartView chart, final ImageButton btn){
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
    private void updateChart(final int tag, final LineChartView chart, ImageButton btn){

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
    private void dismissChart(final int tag, final LineChartView chart, final ImageButton btn){

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

    public void produceOne(LineChartView chart, Runnable action){

        LineSet dataset = new LineSet(mLabelsOne, mValuesOne[0]);
        dataset.setColor(Color.parseColor("#a34545"))
                .setFill(Color.parseColor("#a34545"))
                .setSmooth(true);
        chart.addData(dataset);

        dataset = new LineSet(mLabelsOne, mValuesOne[1]);
        dataset.setColor(Color.parseColor("#e08b36"))
                .setFill(Color.parseColor("#e08b36"))
                .setSmooth(true);
        chart.addData(dataset);

        dataset = new LineSet(mLabelsOne, mValuesOne[2]);
        dataset.setColor(Color.parseColor("#61263c"))
                .setFill(Color.parseColor("#61263c"))
                .setSmooth(true);
        chart.addData(dataset);

        chart.setTopSpacing(Tools.fromDpToPx(15))
                .setBorderSpacing(Tools.fromDpToPx(0))
                .setAxisBorderValues(0, 10, 1)
                .setXLabels(AxisController.LabelPosition.INSIDE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#e08b36"))
                .setXAxis(false)
                .setYAxis(false);

        Animation anim = new Animation().setStartPoint(-1, 1).setEndAction(action);

        chart.show(anim);
    }


    public void updateOne(LineChartView chart){
        float[][] newValues = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f, 7.3f, 5f},
                {1f, 2f, 2f, 3.5f, 2f, 5f, 4.5f, 4.8f, 4.3f, 4.8f, 2.5f}};
        chart.updateValues(1, newValues[0]);
        chart.updateValues(2, newValues[1]);
        chart.notifyDataUpdate();
    }


    public static void dismissOne(LineChartView chart, Runnable action){
        chart.dismiss(new Animation().setStartPoint(-1, 0).setEndAction(action));
    }


    /**
     *
     * Chart 2
     *
     */

    public void produceTwo(LineChartView chart, Runnable action){

        LineSet dataset = new LineSet(mLabelsTwo, mValuesTwo[0]);
        dataset.setColor(Color.WHITE)
                .setThickness(Tools.fromDpToPx(3))
                .beginAt(4).endAt(55);
        chart.addData(dataset);

        dataset = new LineSet(mLabelsTwo, mValuesTwo[1]);
        dataset.setColor(Color.parseColor("#97b867"))
                .setThickness(Tools.fromDpToPx(3))
                .setDashed(new float[]{10, 10});
        chart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#7F97B867"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        chart.setBorderSpacing(Tools.fromDpToPx(0))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#304a00"))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(0, 125, 25)
                .setGrid(ChartView.GridType.HORIZONTAL, gridPaint);

        Animation anim = new Animation().setStartPoint(0, .5f).setEndAction(action);

        chart.show(anim);
        chart.animateSet(1, new DashAnimation());
    }

    public void updateTwo(LineChartView chart){
        chart.updateValues(0, mValuesTwo[1]);
        chart.updateValues(1, mValuesTwo[0]);
        chart.notifyDataUpdate();
    }

    public static void dismissTwo(LineChartView chart, Runnable action){
        chart.dismiss(new Animation().setStartPoint(1, .5f).setEndAction(action));
    }



    /**
     *
     * Chart 3
     *
     */

    public void produceThree(LineChartView chart, Runnable action){

        Tooltip tip = new Tooltip(getActivity(), R.layout.linechart_three_tooltip, R.id.value);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f));
        }

        chart.setTooltips(tip);

        LineSet dataset = new LineSet(mLabelsThree, mValuesThree[0]);
        dataset.setColor(Color.parseColor("#FF58C674"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF58C674"))
                .setDotsColor(Color.parseColor("#eef1f6"));
        chart.addData(dataset);

        dataset = new LineSet(mLabelsThree, mValuesThree[1]);
        dataset.setColor(Color.parseColor("#FFA03436"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FFA03436"))
                .setDotsColor(Color.parseColor("#eef1f6"));
        chart.addData(dataset);

        dataset = new LineSet(mLabelsThree, mValuesThree[2]);
        dataset.setColor(Color.parseColor("#FF365EAF"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF365EAF"))
                .setDotsColor(Color.parseColor("#eef1f6"));
        chart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        chart.setBorderSpacing(1)
                .setAxisBorderValues(0, 10, 1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FF8E9196"))
                .setXAxis(false)
                .setYAxis(false)
                .setStep(2)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.VERTICAL, gridPaint);

        Animation anim = new Animation().setEndAction(action);

        chart.show(anim);
    }

    public void updateThree(LineChartView chart){
        chart.dismissAllTooltips();
        chart.updateValues(0, mValuesThree[2]);
        chart.updateValues(1, mValuesThree[0]);
        chart.updateValues(2, mValuesThree[1]);
        chart.notifyDataUpdate();
    }

    public static void dismissThree(LineChartView chart, Runnable action){
        chart.dismissAllTooltips();
        chart.dismiss(new Animation().setEndAction(action));
    }

}
