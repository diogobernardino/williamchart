package com.db.williamchartdemo.stackedchart;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ExpoEase;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;

import java.text.DecimalFormat;


public class StackedCardTwo extends CardController {


    private final HorizontalStackBarChartView mChart;

    private final String[] mLabels= {"0-20", "20-40", "40-60", "60-80", "80-100", "100+"};
    private final float [][] mValues = {{1.8f, 2f, 2.4f, 2.2f, 3.3f, 3.45f},
            {-1.8f, -2.1f, -2.55f, -2.40f, -3.40f, -3.5f},
            {1.8f, 2.1f, 2.55f, 2.40f, 3.40f, 3.5f},
            {-1.8f, -2f, -2.4f, -2.2f, -3.3f, -3.45f}};


    public StackedCardTwo(CardView card){
        super(card);

        mChart = (HorizontalStackBarChartView) card.findViewById(R.id.chart8);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet.setColor(Color.parseColor("#90ee7e"));
        mChart.addData(barSet);

        barSet = new BarSet(mLabels, mValues[1]);
        barSet.setColor(Color.parseColor("#2b908f"));
        mChart.addData(barSet);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#e7e7e7"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.7f));

        mChart.setBarSpacing(Tools.fromDpToPx(10));

        mChart.setBorderSpacing(0)
                .setGrid(ChartView.GridType.VERTICAL, 1, 10, gridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setLabelsFormat(new DecimalFormat("##'M'"))
                .show(new Animation()
                        .setDuration(2500)
                        .setEasing(new ExpoEase())
                        .setEndAction(action));
    }


    @Override
    public void update() {
        super.update();

        if (firstStage) {
            mChart.updateValues(0, mValues[2]);
            mChart.updateValues(1, mValues[3]);
        }else{
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[1]);
        }
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismiss(new Animation()
                .setDuration(2500)
                .setEasing(new ExpoEase())
                .setEndAction(action));
    }

}
