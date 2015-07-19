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
    protected float easeInOut(float time) {

        if (time == 0)
            return 0.f;

        if (time == 1f)
            return 1.f;

        float p = time / 0.5f;
        if (p < 1.f)
            return 0.5f * (float) Math.pow(2.f, 10.f * (p - 1.f));

        return 0.5f * ( -(float) Math.pow(2.f, -10.f * --p) + 2.f);
    }

    @Override
    protected float easeIn(float time) {
        return (time == 0.f) ? 0.f : (float) Math.pow( 2, 10.f * (time - 1.f));
    }

}
