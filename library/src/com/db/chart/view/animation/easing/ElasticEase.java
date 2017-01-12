package com.db.chart.view.animation.easing;

import com.db.chart.view.animation.easing.BaseEasingMethod;

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
    protected float easeIn(float time) {
        return easeOut(1.f - time);
    }

}
