package com.db.chart.view.animation.easing;

public class QuartEase extends BaseEasingMethod {

	public QuartEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (float) -(Math.pow( time - 1, 4) - 1);
    }

    @Override
    protected float easeInOut(float time) {

        float p = time / 0.5f;
        if (p < 1.f)
            return 0.5f * p * p * p * p;

        p -= 2.f;
        return -0.5f * (p * p * p * p - 2.f);
    }

    @Override
    protected float easeIn(float time) {
        return time * time * time * time;
    }

}
