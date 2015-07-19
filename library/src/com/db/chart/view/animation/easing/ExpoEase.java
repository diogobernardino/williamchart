package com.db.chart.view.animation.easing;

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
        return (time == 0.f) ? 0.f : (float) Math.pow( 2, 10.f * (time - 1.f));
    }

}
