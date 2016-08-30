/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.williamchartwear;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;


public class LineFragment extends Fragment implements View.OnClickListener {

	private final String[] mLabels =
			  {"", "", "", "", "START", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "FINISH", "", "", "",
						 ""};

	private final float[][] mValues =
			  {{35f, 37f, 47f, 49f, 43f, 46f, 80f, 83f, 65f, 68f, 28f, 55f, 58f, 50f, 53f, 53f, 57f,
						 48f, 50f, 53f, 54f, 25f, 27f, 35f, 37f, 35f, 80f, 82f, 55f, 59f, 85f, 82f, 60f,
						 55f, 63f, 65f, 58f, 60f, 63f, 60f},
						 {85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
									85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f,
									85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f, 85f}};

	private LineChartView mChart;

	private boolean mFirstStage;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			  Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_line, container, false);
		mChart = (LineChartView) layout.findViewById(R.id.chart);
		mFirstStage = true;

		layout.setOnClickListener(this);
		mChart.setOnClickListener(this);

		LineSet dataset = new LineSet(mLabels, mValues[0]);
		dataset.setColor(Color.parseColor("#004f7f"))
				  .setThickness(Tools.fromDpToPx(3))
				  .setSmooth(true)
				  .beginAt(4)
				  .endAt(36);
		for (int i = 0; i < mLabels.length; i += 5) {
			Point point = (Point) dataset.getEntry(i);
			point.setColor(Color.parseColor("#ffffff"));
			point.setStrokeColor(Color.parseColor("#0290c3"));
			if (i == 30 || i == 10) point.setRadius(Tools.fromDpToPx(6));
		}
		mChart.addData(dataset);

		Paint thresPaint = new Paint();
		thresPaint.setColor(Color.parseColor("#0079ae"));
		thresPaint.setStyle(Paint.Style.STROKE);
		thresPaint.setAntiAlias(true);
		thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
		thresPaint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));

		Paint gridPaint = new Paint();
		gridPaint.setColor(Color.parseColor("#ffffff"));
		gridPaint.setStyle(Paint.Style.STROKE);
		gridPaint.setAntiAlias(true);
		gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

		mChart.setBorderSpacing(Tools.fromDpToPx(0))
				  .setXLabels(AxisRenderer.LabelPosition.OUTSIDE)
				  .setLabelsColor(Color.parseColor("#304a00"))
				  .setYLabels(AxisRenderer.LabelPosition.NONE)
				  .setXAxis(false)
				  .setYAxis(false)
				  .setGrid(ChartView.GridType.VERTICAL, 1, 7, gridPaint)
				  .setValueThreshold(80f, 80f, thresPaint)
				  .setAxisBorderValues(0, 110);

		mChart.show(new Animation().setStartPoint(0, .5f));

		return layout;
	}


	@Override
	public void onClick(View view) {

		if (mFirstStage) mChart.updateValues(0, mValues[1]);
		else mChart.updateValues(0, mValues[0]);

		mFirstStage = !mFirstStage;
		mChart.notifyDataUpdate();
	}
}
