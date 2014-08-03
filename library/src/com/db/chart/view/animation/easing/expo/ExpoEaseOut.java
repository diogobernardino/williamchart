package com.db.chart.view.animation.easing.expo;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class ExpoEaseOut implements BaseEasingMethod{
    public ExpoEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return (time == 1) ? 1 : ( -(float) Math.pow( 2, -10 * time) + 1);
    }
}
