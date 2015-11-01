package com.db.williamchartdemo.linechart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.williamchartdemo.CardController;
import com.db.williamchartdemo.R;


public class LineCardThree extends CardController {


    private final Context mContext;


    private final LineChartView mChart;

    private final String[] mLabelsThree= {"00", "04", "08", "12", "16", "20", "24"};
    private final float[][] mValuesThree = {{4.5f, 5.7f, 4f, 8f, 2.5f, 3f, 6.5f},
            {1.5f, 2.5f, 1.5f, 5f, 5.5f, 5.5f, 3f},
            {8f, 7.5f, 7.8f, 1.5f, 8f, 8f, .5f}};


    public LineCardThree(CardView card, Context context){
        super(card);

        mContext = context;
        mChart = (LineChartView) card.findViewById(R.id.chart3);
    }


    @Override
    public void show(Runnable action){
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

        LineSet dataset = new LineSet(mLabelsThree, mValuesThree[0]);
        dataset.setColor(Color.parseColor("#FF58C674"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF58C674"))
                .setDotsColor(Color.parseColor("#e3e7ec"));
        mChart.addData(dataset);

        dataset = new LineSet(mLabelsThree, mValuesThree[1]);
        dataset.setColor(Color.parseColor("#FFA03436"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FFA03436"))
                .setDotsColor(Color.parseColor("#e3e7ec"));
        mChart.addData(dataset);

        dataset = new LineSet(mLabelsThree, mValuesThree[2]);
        dataset.setColor(Color.parseColor("#FF365EAF"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF365EAF"))
                .setDotsColor(Color.parseColor("#e3e7ec"));
        mChart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        mChart.setBorderSpacing(1)
                .setAxisBorderValues(0, 10, 2)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FF8E9196"))
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.VERTICAL, gridPaint);

        Animation anim = new Animation().setEndAction(action);

        mChart.show(anim);
    }

    @Override
    public void update(){
        super.update();

        mChart.dismissAllTooltips();
        if(firstStage) {
            mChart.updateValues(0, mValuesThree[2]);
            mChart.updateValues(1, mValuesThree[0]);
            mChart.updateValues(2, mValuesThree[1]);
        }else{
            mChart.updateValues(0, mValuesThree[0]);
            mChart.updateValues(1, mValuesThree[1]);
            mChart.updateValues(2, mValuesThree[2]);
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
