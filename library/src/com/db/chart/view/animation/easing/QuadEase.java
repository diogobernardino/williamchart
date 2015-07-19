package com.db.chart.view.animation.easing;

public class QuadEase extends BaseEasingMethod {
    
	public QuadEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return -time * (time - 2);
    }

    @Override
    protected float easeInOut(float time) {

        float p = time / 0.5f;
        if (p < 1.f)
            return 0.5f * p * p;

        return -0.5f * ((--p) * (p - 2.f) - 1.f);
    }

    @Override
    protected float easeIn(float time) {
        return time * time;
    }

}
