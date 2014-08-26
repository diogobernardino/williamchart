package com.db.chart.view.animation.easing.elastic;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class ElasticEaseOut implements BaseEasingMethod{

    public ElasticEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        if (time==0) return 0;  if (time==1) return 1;
        float p=.3f;
        float a=1;
        float s=p/4;
        return (a*(float)Math.pow(2,-10*time) * (float)Math.sin( (time*1-s)*(2*(float)Math.PI)/p ) + 1);
    }
}
