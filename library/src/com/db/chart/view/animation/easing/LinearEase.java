package com.db.chart.view.animation.easing;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class LinearEase extends BaseEasingMethod {

    @Override
    protected float easeOut(float time) {
        return time;
    }

    @Override
    protected float easeIn(float time) {
        return time;
    }
	
}
