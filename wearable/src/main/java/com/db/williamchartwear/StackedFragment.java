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
import com.db.chart.model.BarSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.StackBarChartView;


public class StackedFragment extends Fragment implements View.OnClickListener {

	private final String[] mLabels = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL"};

	private final float[][] mValuesOne =
			  {{30f, 40f, 25f, 25f, 40f, 25f, 25f}, {30f, 30f, 25f, 40f, 25f, 30f, 40f},
						 {30f, 30f, 25f, 25f, 25f, 25f, 25f}};

	private StackBarChartView mChart;

	private boolean mFirstStage;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			  Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_stacked, container, false);
		mChart = (StackBarChartView) layout.findViewById(R.id.chart);
		mFirstStage = true;

		layout.setOnClickListener(this);
		mChart.setOnClickListener(this);

		Paint thresPaint = new Paint();
		thresPaint.setColor(Color.parseColor("#dad8d6"));
		thresPaint.setPathEffect(new DashPathEffect(new float[] {10, 20}, 0));
		thresPaint.setStyle(Paint.Style.STROKE);
		thresPaint.setAntiAlias(true);
		thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

		BarSet stackBarSet = new BarSet(mLabels, mValuesOne[0]);
		stackBarSet.setColor(Color.parseColor("#a1d949"));
		mChart.addData(stackBarSet);

		stackBarSet = new BarSet(mLabels, mValuesOne[1]);
		stackBarSet.setColor(Color.parseColor("#ffcc6a"));
		mChart.addData(stackBarSet);

		stackBarSet = new BarSet(mLabels, mValuesOne[2]);
		stackBarSet.setColor(Color.parseColor("#ff7a57"));
		mChart.addData(stackBarSet);

		mChart.setBarSpacing(Tools.fromDpToPx(15));
		mChart.setRoundCorners(Tools.fromDpToPx(1));

		mChart.setXAxis(false)
				  .setXLabels(AxisRenderer.LabelPosition.OUTSIDE)
				  .setYAxis(false)
				  .setYLabels(AxisRenderer.LabelPosition.NONE)
				  .setValueThreshold(89.f, 89.f, thresPaint);

		mChart.show(new Animation().setOverlap(.5f, new int[] {0, 1, 2, 3, 4, 5, 6}));

		return layout;
	}


	@Override
	public void onClick(View view) {

		if (mFirstStage) {
			mChart.updateValues(0, mValuesOne[2]);
			mChart.updateValues(1, mValuesOne[0]);
			mChart.updateValues(2, mValuesOne[1]);
		} else {
			mChart.updateValues(0, mValuesOne[0]);
			mChart.updateValues(1, mValuesOne[1]);
			mChart.updateValues(2, mValuesOne[2]);
		}
		mFirstStage = !mFirstStage;
		mChart.notifyDataUpdate();
	}
}
