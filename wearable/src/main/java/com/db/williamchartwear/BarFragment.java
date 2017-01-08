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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;

import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.model.BarSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.BarChartView;


public class BarFragment extends Fragment implements View.OnClickListener {

	private final String[] mLabels =
			  {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

	private final float[][] mValues =
			  {{3.5f, 4.7f, 5f, 9f, 5.5f, 5f, 6f, 8f, 11f, 13f, 11f, 7f, 6f, 7f, 10f, 11f, 12f, 9f, 8f,
						 7f, 6f, 5f, 4f, 3f, 6f, 7f, 8f, 9f, 10f, 12f, 13f, 11, 13f, 10f, 8f, 7f, 5f, 4f,
						 3f, 7f},
						 {2.5f, 3.7f, 4f, 8f, 4.5f, 4f, 5f, 7f, 10f, 14f, 12f, 6f, 7f, 8f, 9f, 8f, 9f, 8f,
									7f, 6f, 5f, 4f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 11f, 12f, 14, 13f, 10f, 9f,
									8f, 7f, 5f, 4f, 6f}};

	private BarChartView mChart;

	private boolean mFirstStage;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			  Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_bar, container, false);
		mChart = (BarChartView) layout.findViewById(R.id.chart);
		mFirstStage = true;

		layout.setOnClickListener(this);
		mChart.setOnClickListener(this);

		BarSet dataset = new BarSet(mLabels, mValues[0]);
		dataset.setColor(Color.parseColor("#eb993b"));
		mChart.addData(dataset);

		mChart.setBarSpacing(Tools.fromDpToPx(3));

		mChart.setXLabels(AxisRenderer.LabelPosition.NONE)
				  .setYLabels(AxisRenderer.LabelPosition.NONE)
				  .setXAxis(false)
				  .setYAxis(false);
		mChart.show(new Animation().setEasing(new BounceInterpolator()));

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
