package com.db.chart.view.animation.easing.sine;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class SineEaseOut implements BaseEasingMethod{
    public SineEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return (float)Math.sin(time * (Math.PI/2));
    }
}
