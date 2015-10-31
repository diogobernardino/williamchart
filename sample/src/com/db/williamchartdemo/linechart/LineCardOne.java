package com.db.williamchartdemo.linechart;

import android.graphics.Color;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardOne extends CardController {


    private final LineChartView mChart;


    private final String[] mLabels= {"", "10-15", "", "15-20", "", "20-25", "", "25-30", "", "30-35", ""};
    private float[][] mValues = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f, 7.3f, 5f},
            {2.5f, 3.5f, 3.5f, 7f, 5.5f, 8.5f, 6f, 6.3f, 5.8f, 6.3f, 4.5f},
            {1.5f, 2.5f, 2.5f, 4f, 2.5f, 5.5f, 5f, 5.3f, 4.8f, 5.3f, 3f},
            {3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f, 7.3f, 5f},
            {1f, 2f, 2f, 3.5f, 2f, 5f, 4.5f, 4.8f, 4.3f, 4.8f, 2.5f}};


    public LineCardOne(CardView card){
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.chart1);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#a34545"))
                .setFill(Color.parseColor("#a34545"))
                .setSmooth(true);
        mChart.addData(dataset);

        dataset = new LineSet(mLabels, mValues[1]);
        dataset.setColor(Color.parseColor("#e08b36"))
                .setFill(Color.parseColor("#e08b36"))
                .setSmooth(true);
        mChart.addData(dataset);

        dataset = new LineSet(mLabels, mValues[2]);
        dataset.setColor(Color.parseColor("#61263c"))
                .setFill(Color.parseColor("#61263c"))
                .setSmooth(true);
        mChart.addData(dataset);

        mChart.setBorderSpacing(Tools.fromDpToPx(0))
                .setXLabels(AxisController.LabelPosition.INSIDE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#e08b36"))
                .setXAxis(false)
                .setYAxis(false);

        Animation anim = new Animation().setStartPoint(-1, 1).setEndAction(action);

        mChart.show(anim);
    }


    @Override
    public void update() {
        super.update();

        if (firstStage) {
            mChart.updateValues(1, mValues[3]);
            mChart.updateValues(2, mValues[4]);
        }else{
            mChart.updateValues(1, mValues[1]);
            mChart.updateValues(2, mValues[2]);
        }
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismiss(new Animation().setStartPoint(-1, 0).setEndAction(action));
    }


}
