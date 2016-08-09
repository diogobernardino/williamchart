package com.db.williamchartdemo.linechart;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardTwo extends CardController {


    private final LineChartView mChart;

    private final String[] mLabels= {"", "", "", "", "START", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "FINISH", "", "", "", ""};
    private final float[][] mValues = {
            {35f, 37f, 47f, 49f, 43f,46f, 80f, 83f, 65f, 68f,
            28f, 55f, 58f, 50f, 53f, 53f, 57f, 48f, 50f, 53f,
            54f,25f, 27f, 35f, 37f, 35f, 80f, 82f, 55f, 59f,
            85f, 82f, 60f, 55f, 63f, 65f, 58f, 60f, 63f, 60f},
            {85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
            85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f}};


    public LineCardTwo(CardView card){
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.chart9);
    }


    @Override
    public void show(Runnable action){
        super.show(action);

        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#004f7f"))
                .setThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .beginAt(4).endAt(36);
        for (int i = 0; i < mLabels.length; i+=5){
            Point point = (Point) dataset.getEntry(i);
            point.setColor(Color.parseColor("#ffffff"));
            point.setStrokeColor(Color.parseColor("#0290c3"));
            if (i == 30 || i == 10)
                point.setRadius(Tools.fromDpToPx(6));
        }
        mChart.addData(dataset);

        Paint thresPaint = new Paint();
        thresPaint.setColor(Color.parseColor("#0079ae"));
        thresPaint.setStyle(Paint.Style.STROKE);
        thresPaint.setAntiAlias(true);
        thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        mChart.setBorderSpacing(Tools.fromDpToPx(0))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#304a00"))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setGrid(ChartView.GridType.VERTICAL, 1, 7, gridPaint)
                .setValueThreshold(80f, 80f, thresPaint)
                .setAxisBorderValues(0, 110);

        Animation anim = new Animation().setStartPoint(0, .5f).setEndAction(action);

        mChart.show(anim);
    }

    @Override
    public void update(){
        super.update();

        if(firstStage) {
            mChart.updateValues(0, mValues[1]);
        }else {
            mChart.updateValues(0, mValues[0]);
        }
        mChart.notifyDataUpdate();
    }

    @Override
    public void dismiss(Runnable action){
        super.dismiss(action);

        mChart.dismiss(new Animation().setStartPoint(1, .5f).setEndAction(action));
    }

}
