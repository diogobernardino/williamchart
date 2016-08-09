package com.db.williamchartdemo.stackedchart;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.BarSet;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class StackedCardOne extends CardController {


    private final TextView mLegendOneRed;
    private final TextView mLegendOneYellow;
    private final TextView mLegendOneGreen;


    private final StackBarChartView mChart;

    private final String[] mLabels= {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
    private final float [][] mValuesOne = {
            {30f, 40f, 25f, 25f, 40f, 25f, 25f, 30f, 30f, 25f, 40f, 25f},
            {30f, 30f, 25f, 40f, 25f, 30f, 40f, 30f, 30f, 25f, 25f, 25f},
            {30f, 30f, 25f, 25f, 25f, 25f, 25f, 30f, 40f, 25, 25, 40f} };


    public StackedCardOne(CardView card){
        super(card);

        mChart = (StackBarChartView) card.findViewById(R.id.chart5);
        mLegendOneRed = (TextView) card.findViewById(R.id.state_one);
        mLegendOneYellow = (TextView) card.findViewById(R.id.state_two);
        mLegendOneGreen = (TextView) card.findViewById(R.id.state_three);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mChart.setOnEntryClickListener(new OnEntryClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(int setIndex, int entryIndex, Rect rect) {
                    if(setIndex == 2)
                        mLegendOneRed.animate()
                                .scaleY(1.3f)
                                .scaleX(1.3f)
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
                                .scaleY(1.3f)
                                .scaleX(1.3f)
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
                                .scaleY(1.3f)
                                .scaleX(1.3f)
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

        BarSet stackBarSet = new BarSet(mLabels, mValuesOne[0]);
        stackBarSet.setColor(Color.parseColor("#a1d949"));
        mChart.addData(stackBarSet);

        stackBarSet = new BarSet(mLabels, mValuesOne[1]);
        stackBarSet.setColor(Color.parseColor("#ffcc6a"));
        mChart.addData(stackBarSet);

        stackBarSet = new BarSet(mLabels, mValuesOne[2]);
        stackBarSet.setColor(Color.parseColor("#ff7a57"));
        mChart.addData(stackBarSet);

        mChart.setBarSpacing(Tools.fromDpToPx(15));
        mChart.setRoundCorners(Tools.fromDpToPx(1));

        mChart.setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setValueThreshold(89.f, 89.f, thresPaint);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        mChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }


    @Override
    public void update() {
        super.update();

        if (firstStage) {
            mChart.updateValues(0, mValuesOne[2]);
            mChart.updateValues(1, mValuesOne[0]);
            mChart.updateValues(2, mValuesOne[1]);
        }else {
            mChart.updateValues(0, mValuesOne[0]);
            mChart.updateValues(1, mValuesOne[1]);
            mChart.updateValues(2, mValuesOne[2]);
        }
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        mChart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }

}
