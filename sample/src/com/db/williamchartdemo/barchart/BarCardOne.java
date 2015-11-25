package com.db.williamchartdemo.barchart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class BarCardOne extends CardController {


    private final Context mContext;

    private final BarChartView mChart;

    private final String[] mLabels= {"A", "B", "C", "D"};
    private final float [][] mValues = {{6.5f, 8.5f, 2.5f, 10f}, {7.5f, 3.5f, 5.5f, 4f}};


    public BarCardOne(CardView card, Context context){
        super(card);

        mChart = (BarChartView) card.findViewById(R.id.chart3);
        mContext = context;
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        // Data
        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet.setColor(Color.parseColor("#fc2a53"));
        mChart.addData(barSet);

        mChart.setBarSpacing(Tools.fromDpToPx(40));
        mChart.setRoundCorners(Tools.fromDpToPx(2));
        mChart.setBarBackgroundColor(Color.parseColor("#592932"));

        // Chart
        mChart.setXAxis(false)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#86705c"))
                .setAxisColor(Color.parseColor("#86705c"));

        int[] order = {1, 0, 2, 3};
        final Runnable auxAction = action;
        Runnable chartOneAction = new Runnable() {
            @Override
            public void run() {
                auxAction.run();
                showTooltip();
            }
        };
        mChart.show(new Animation()
                .setOverlap(.7f, order)
                .setEndAction(chartOneAction));
    }


    @Override
    public void update() {
        super.update();

        mChart.dismissAllTooltips();
        if (firstStage)
            mChart.updateValues(0, mValues[1]);
        else
            mChart.updateValues(0, mValues[0]);
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismissAllTooltips();
        int[] order = {0, 2, 1, 3};
        mChart.dismiss(new Animation()
                .setOverlap(.7f, order)
                .setEndAction(action));
    }



    private void showTooltip() {

        // Tooltip
        Tooltip tip = new Tooltip(mContext, R.layout.square_tooltip);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)).setDuration(200);

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f)).setDuration(200);
        }
        tip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        tip.setDimensions((int) Tools.fromDpToPx(25), (int) Tools.fromDpToPx(25));
        tip.setMargins(0, 0, 0, (int) Tools.fromDpToPx(10));
        tip.prepare(mChart.getEntriesArea(0).get(2), 0);

        mChart.showTooltip(tip, true);
    }

}
