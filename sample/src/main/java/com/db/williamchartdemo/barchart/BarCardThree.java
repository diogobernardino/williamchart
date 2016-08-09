package com.db.williamchartdemo.barchart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ElasticEase;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class BarCardThree extends CardController {


    private final Context mContext;


    private final BarChartView mChart;

    private final String[] mLabels= {"", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", ""};
    private final float[][] mValues = {
            {2.5f, 3.7f, 4f, 8f, 4.5f, 4f, 5f, 7f, 10f, 14f,
            12f, 6f, 7f, 8f, 9f, 8f, 9f, 8f, 7f, 6f,
            5f, 4f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 11f,
            12f, 14, 13f, 10f ,9f, 8f, 7f, 5f, 4f, 6f},
            {3.5f, 4.7f, 5f, 9f, 5.5f, 5f, 6f, 8f, 11f, 13f,
            11f, 7f, 6f, 7f, 10f, 11f, 12f, 9f, 8f, 7f,
            6f, 5f, 4f, 3f, 6f, 7f, 8f, 9f, 10f, 12f,
            13f, 11, 13f, 10f ,8f, 7f, 5f, 4f, 3f, 7f}};


    public BarCardThree(CardView card, Context context){
        super(card);

        mContext = context;
        mChart = (BarChartView) card.findViewById(R.id.chart6);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

        Tooltip tip = new Tooltip(mContext);
        tip.setBackgroundColor(Color.parseColor("#CC7B1F"));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
            tip.setEnterAnimation(alpha).setDuration(150);

            alpha = PropertyValuesHolder.ofFloat(View.ALPHA,0);
            tip.setExitAnimation(alpha).setDuration(150);
        }

        mChart.setTooltips(tip);

        BarSet dataset = new BarSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#eb993b"));
        mChart.addData(dataset);

        mChart.setBarSpacing(Tools.fromDpToPx(3));

        mChart.setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false);

        Animation anim = new Animation()
                .setEasing(new ElasticEase())
                .setEndAction(action);

        mChart.show(anim);
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
        mChart.dismiss(new Animation()
                .setEasing(new ElasticEase())
                .setEndAction(action));
    }

}
