package com.db.chart.view.animation.easing;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class ExpoEase extends BaseEasingMethod {

    public ExpoEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (time == 1) ? 1 : ( -(float) Math.pow( 2, -10 * time) + 1);
    }

    @Override
    protected float easeIn(float time) {
        return easeOut(1.f - time);
    }

}
