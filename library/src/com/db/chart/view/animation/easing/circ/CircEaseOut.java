package com.db.chart.view.animation.easing.circ;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class CircEaseOut implements BaseEasingMethod{

    public CircEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return 1 * (float) Math.sqrt( 1 - ( time = time - 1) * time);
    }
}
