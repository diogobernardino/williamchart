package com.db.chart.view.animation.easing;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class QuintEase extends BaseEasingMethod {

	public QuintEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (float) Math.pow(time - 1, 5) + 1;
    }

    @Override
    protected float easeIn(float time) {
        return easeOut(1.f - time);
    }

}
