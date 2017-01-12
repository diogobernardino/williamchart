package com.db.chart.view.animation.easing;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class QuartEase extends BaseEasingMethod {

	public QuartEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (float) -(Math.pow( time - 1, 4) - 1);
    }

    @Override
    protected float easeIn(float time) {
        return easeOut(1.f - time);
    }

}
