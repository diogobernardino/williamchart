package com.db.chart.view.animation.easing;

public class LinearEase extends BaseEasingMethod {

    @Override
    protected float easeOut(float time) {
        return time;
    }

    @Override
    protected float easeInOut(float time) {
        return time;
    }

    @Override
    protected float easeIn(float time) {
        return time;
    }
	
}
