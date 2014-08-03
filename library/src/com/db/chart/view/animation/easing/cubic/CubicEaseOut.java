package com.db.chart.view.animation.easing.cubic;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class CubicEaseOut implements BaseEasingMethod {

    public CubicEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return (float) Math.pow( time - 1, 3) + 1;
    }
}
