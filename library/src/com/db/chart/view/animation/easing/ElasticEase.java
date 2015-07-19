package com.db.chart.view.animation.easing;

public class ElasticEase extends BaseEasingMethod {

    public ElasticEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {

        if (time==0) return 0;  if (time==1) return 1;
        float p=.3f;
        float a=1;
        float s=p/4;
        return (a*(float)Math.pow(2,-10*time) * (float)Math.sin( (time*1-s)*(2*(float)Math.PI)/p ) + 1);
    }

    @Override
    protected float easeInOut(float time) {

        if (time == 0)
            return 0.f;

        float pos = time / 0.5f;
        if (pos == 2)
            return 1.f;

        float p = (.3f * 1.5f);
        float s = p / (2.f * (float) Math.PI) * (float) Math.asin(1.f);
        if (pos < 1.f)
            return -.5f * ((float) Math.pow(2.f, 10.f * (pos -= 1.f))
                    * (float) Math.sin((pos * 1f - s)
                    * (2.f * Math.PI) / p));

        return (float) Math.pow(2.f, -10.f * (pos -= 1.f))
                * (float) Math.sin((pos * 1f - s) * (2.f * Math.PI) / p) * .5f
                + 1.f;
    }

    @Override
    protected float easeIn(float time) {

        if (time == 0) return 0.f;
        if (time == 1) return 1.f;
        float p=.3f;
        float s=p/4;
        return - ((float) Math.pow(2, 10.f * (time -= 1.f))
                * (float) Math.sin( (time - s) * (2.f * Math.PI)/p ));
    }

}
