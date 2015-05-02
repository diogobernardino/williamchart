package com.db.chart.view.animation.easing;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class CubicEase extends BaseEasingMethod {

    public CubicEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (float) Math.pow( time - 1, 3) + 1;
    }

    @Override
    protected float easeIn(float time) {
        // TODO return (float) Math.pow( time, 3);
        return easeOut(1f - time);
    }

}
