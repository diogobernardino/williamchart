package com.db.williamchartdemo.barchart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;

import java.util.ArrayList;


public class BarCardOne extends CardController {


    private final Context mContext;


    private final BarChartView mChart;

    private final String[] mLabels= {"10-15", "15-20", "20-25", "25-30", "30-35"};
    private final float [][] mValues = {{9f, 7.5f, 5.5f, 4.5f, 9.5f},
            {6.5f, 3.5f, 3.5f, 2.5f, 7.5f},
            {8.5f, 6.5f, 4.5f, 3.5f, 9f},
            {5.5f, 3.0f, 3.0f, 2.5f, 7.5f}};


    public BarCardOne(CardView card, Context context){
        super(card);

        mContext = context;
        mChart = (BarChartView) card.findViewById(R.id.chart1);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        mChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                System.out.println("OnClick "+rect.left);
            }
        });

        Tooltip tooltip = new Tooltip(mContext, R.layout.barchart_one_tooltip);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
        }
        mChart.setTooltips(tooltip);

        BarSet barSet = new BarSet(mLabels, mValues[0]);
        barSet.setColor(Color.parseColor("#a8896c"));
        mChart.addData(barSet);

        barSet = new BarSet(mLabels, mValues[1]);
        barSet.setColor(Color.parseColor("#c33232"));
        mChart.addData(barSet);

        mChart.setSetSpacing(Tools.fromDpToPx(-15));
        mChart.setBarSpacing(Tools.fromDpToPx(35));
        mChart.setRoundCorners(Tools.fromDpToPx(2));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#6d86705c"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        mChart.setBorderSpacing(5)
                .setAxisBorderValues(0, 10, 2)
                .setGrid(BarChartView.GridType.FULL, 5, 10, gridPaint)
                .setXAxis(false)
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
                tooltipsOn();
                auxAction.run();
            }
        };
        mChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(chartOneAction));
    }


    @Override
    public void update() {
        super.update();

        tooltipsOff();
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

        tooltipsOff();
        int[] order = {0, 4, 1, 3, 2};
        mChart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }


    private void tooltipsOn(){

        ArrayList<ArrayList<Rect>> areas = new ArrayList<>();
        areas.add(mChart.getEntriesArea(0));
        areas.add(mChart.getEntriesArea(1));

        for(int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {

                Tooltip tooltip = new Tooltip(mContext, R.layout.barchart_one_tooltip, R.id.value);
                tooltip.prepare(areas.get(i).get(j), mValues[i][j]);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                    tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
                }
                mChart.showTooltip(tooltip, true);
            }
        }
    }


    private void tooltipsOff(){
        mChart.dismissAllTooltips();
    }
}
