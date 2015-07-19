package com.db.chart.view.animation.easing;

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
        return time * time * time * time * time;
    }

}
