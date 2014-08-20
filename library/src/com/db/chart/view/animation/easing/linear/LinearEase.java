package com.db.chart.view.animation.easing.linear;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class LinearEase implements BaseEasingMethod{

	@Override
	public float next(float normalizedTime) {
		return normalizedTime;
	}
	
}
