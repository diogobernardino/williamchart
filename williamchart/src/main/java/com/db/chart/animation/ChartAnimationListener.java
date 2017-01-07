package com.db.chart.animation;

import com.db.chart.model.ChartSet;

import java.util.ArrayList;


public interface ChartAnimationListener {

	boolean onAnimationUpdate(ArrayList<ChartSet> data);
}
