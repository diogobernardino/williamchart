package com.db.williamchartdemo.linechart;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.style.DashAnimation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardTwo extends CardController {


    private final LineChartView mChart;

    private final String[] mLabels= {"", "", "", "", "START", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "FINISH", "", "", "", ""};
    private final float[][] mValues = {
            {35f, 37f, 47f, 49f, 43f,46f, 80f, 83f, 65f, 68f,
            100f,68f, 70f, 73f, 83f, 85f, 70f, 73f, 73f, 77f,
            33f, 15f, 18f, 25f, 28f, 25f, 28f, 40f, 43f, 25f,
            28f, 55f, 58f, 50f, 53f, 53f, 57f, 48f, 50f, 53f,
            54f,25f, 27f, 35f, 37f, 35f, 80f, 82f, 55f, 59f,
            85f, 82f, 60f, 55f, 63f, 65f, 58f, 60f, 63f, 60f},
            {85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f}};


    public LineCardTwo(CardView card){
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.chart2);
    }


    @Override
    public void show(Runnable action){
        super.show(action);

        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.WHITE)
                .setThickness(Tools.fromDpToPx(3))
                .beginAt(4).endAt(55);
        mChart.addData(dataset);

        dataset = new LineSet(mLabels, mValues[1]);
        dataset.setColor(Color.parseColor("#97b867"))
                .setThickness(Tools.fromDpToPx(3))
                .setDashed(new float[]{10, 10});
        mChart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#7F97B867"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        mChart.setBorderSpacing(Tools.fromDpToPx(0))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#304a00"))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(0, 125, 25)
                .setGrid(ChartView.GridType.HORIZONTAL, gridPaint);

        Animation anim = new Animation().setStartPoint(0, .5f).setEndAction(action);

        mChart.show(anim);
        mChart.animateSet(1, new DashAnimation());
    }

    @Override
    public void update(){
        super.update();

        if(firstStage) {
            mChart.updateValues(0, mValues[1]);
            mChart.updateValues(1, mValues[0]);
        }else {
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[1]);
        }
        mChart.notifyDataUpdate();
    }

    @Override
    public void dismiss(Runnable action){
        super.dismiss(action);

        mChart.dismiss(new Animation().setStartPoint(1, .5f).setEndAction(action));
    }

}
