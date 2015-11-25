package com.db.williamchartdemo.linechart;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardThree extends CardController {


    private final LineChartView mChart;

    private final String[] mLabels = {"", "", "", "", "", "", "", "", ""};
    private final float[][] mValues = {{0f, 2f, 1.4f, 4.f, 3.5f, 4.3f, 2f, 4f, 6.f},
            {1.5f, 2.5f, 1.5f, 5f, 4f, 5f, 4.3f, 2.1f, 1.4f}};


    public LineCardThree(CardView card, Context context){
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.chart2);
    }


    @Override
    public void show(Runnable action){
        super.show(action);

        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#53c1bd"))
                .setFill(Color.parseColor("#3d6c73"))
                .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")}, null);
        mChart.addData(dataset);

        mChart.setBorderSpacing(1)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5));

        Animation anim = new Animation().setEndAction(action);

        mChart.show(anim);
    }

    @Override
    public void update(){
        super.update();

        mChart.dismissAllTooltips();
        if(firstStage) {
            mChart.updateValues(0, mValues[1]);
        }else{
            mChart.updateValues(0, mValues[0]);
        }
        mChart.notifyDataUpdate();
    }

    @Override
    public void dismiss(Runnable action){
        super.dismiss(action);

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation().setEndAction(action));
    }
}
