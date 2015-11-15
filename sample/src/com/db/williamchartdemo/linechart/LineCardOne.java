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
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardOne extends CardController {


    private final LineChartView mChart;


    private final Context mContext;


    private final String[] mLabels= {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[][] mValues = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 10f, 7f, 8.3f, 7.0f},
            {2.5f, 3.5f, 3.5f, 7f, 5.5f, 8.5f, 6f, 6.3f, 5.8f}};


    public LineCardOne(CardView card, Context context){
        super(card);

        mContext = context;
        mChart = (LineChartView) card.findViewById(R.id.chart1);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        Tooltip tip = new Tooltip(mContext, R.layout.linechart_three_tooltip, R.id.value);

        ((TextView) tip.findViewById(R.id.value))
                .setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Semibold.ttf"));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)).setDuration(200);

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f)).setDuration(200);
        }
        mChart.setTooltips(tip);

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

        mChart.setBorderSpacing(Tools.fromDpToPx(0))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(false)
                .setYAxis(false);

        Animation anim = new Animation().setEndAction(action);

        mChart.show(anim);
    }


    @Override
    public void update() {
        super.update();

        if (firstStage) {
            mChart.updateValues(0, mValues[1]);
            mChart.updateValues(1, mValues[1]);
        }else{
            mChart.updateValues(0, mValues[0]);
            mChart.updateValues(1, mValues[0]);
        }
        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

        mChart.dismiss(new Animation().setEndAction(action));
    }


}
