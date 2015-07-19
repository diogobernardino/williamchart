package com.db.chart.view.animation.easing;

public class SineEase extends BaseEasingMethod {

    public SineEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (float)Math.sin(time * (Math.PI/2));
    }

    @Override
    protected float easeIn(float time) {
        return -(float) Math.cos(time * (Math.PI / 2.f)) + 1.f;
    }

}
