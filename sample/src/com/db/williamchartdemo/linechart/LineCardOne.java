package com.db.williamchartdemo.linechart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardOne extends CardController {


    private final LineChartView mChart;


    private final Context mContext;


    private final String[] mLabels= {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[][] mValues = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f},
            {4.5f, 2.5f, 2.5f, 9f, 4.5f, 9.5f, 5f, 8.3f, 1.8f}};

    private Tooltip mTip;

    private Runnable mBaseAction;


    public LineCardOne(CardView card, Context context){
        super(card);

        mContext = context;
        mChart = (LineChartView) card.findViewById(R.id.chart1);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        // Tooltip
        mTip = new Tooltip(mContext, R.layout.linechart_three_tooltip, R.id.value);

        ((TextView) mTip.findViewById(R.id.value))
                .setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Semibold.ttf"));

        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(65), (int) Tools.fromDpToPx(25));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));
        }

        mChart.setTooltips(mTip);

        // Data
        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f,10f})
                .beginAt(5);
        mChart.addData(dataset);

        dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#b3b5bb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#ffc755"))
                .setThickness(4)
                .endAt(6);
        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(0, 20)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(false)
                .setYAxis(false);

        mBaseAction = action;
        Runnable chartAction = new Runnable() {
            @Override
            public void run() {
                mBaseAction.run();
                mTip.prepare(mChart.getEntriesArea(0).get(3), mValues[0][3]);
                mChart.showTooltip(mTip, true);
            }
        };

        Animation anim = new Animation()
                .setEasing(new BounceEase())
                .setEndAction(chartAction);

        mChart.show(anim);
    }


    @Override
    public void update() {
        super.update();

        mChart.dismissAllTooltips();
        if (firstStage) {
            mChart.updateValues(0, mValues[1]);
            mChart.updateValues(1, mValues[1]);
        }else{
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[0]);
        }
        mChart.getChartAnimation().setEndAction(mBaseAction);
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation()
                .setEasing(new BounceEase())
                .setEndAction(action));
    }

}
