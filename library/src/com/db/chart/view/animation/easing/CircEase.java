package com.db.chart.view.animation.easing;

public class CircEase extends BaseEasingMethod {

    public CircEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return 1 * (float) Math.sqrt( 1 - ( time = time - 1) * time);
    }

    @Override
    protected float easeInOut(float time) {

        float p = time / 0.5f;
        if (p < 1.f)
            return -0.5f * ((float) Math.sqrt(1.f - p * p) - 1.f);
        return 0.5f * ((float) Math.sqrt(1.f - (p -= 2.f) * p) + 1.f);
    }

    @Override
    protected float easeIn(float time) {
        return -((float) Math.sqrt(1.f - time * time) - 1.f);
    }

}
