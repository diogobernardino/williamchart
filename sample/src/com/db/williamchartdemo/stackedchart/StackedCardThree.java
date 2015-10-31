package com.db.williamchartdemo.stackedchart;

import android.graphics.Color;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class StackedCardThree extends CardController {


    private final HorizontalStackBarChartView mChart;

    private final String[] mLabels= {"", "", "", ""};
    private final float[][] mValues = {{30f, 60f, 50f, 80f},
            {-70f, -40f, -50f, -20f},
            {50f, 70f, 10f, 30f},
            {-40f, -70f, -60f, -50f}};


    public StackedCardThree(CardView card){
        super(card);

        mChart = (HorizontalStackBarChartView) card.findViewById(R.id.chart3);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        BarSet dataset = new BarSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#687E8E"));
        mChart.addData(dataset);

        dataset = new BarSet(mLabels, mValues[1]);
        dataset.setColor(Color.parseColor("#FF5C8E67"));
        mChart.addData(dataset);

        mChart.setRoundCorners(Tools.fromDpToPx(5));
        mChart.setBarSpacing(Tools.fromDpToPx(8));

        mChart.setBorderSpacing(Tools.fromDpToPx(5))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(-80, 80, 10);

        Animation anim = new Animation()
                .setEndAction(action);

        mChart.show(anim);
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

        mChart.dismiss(mChart.getChartAnimation().setEndAction(action));
    }

}
