package com.db.chart.view.animation.easing.quart;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class QuartEaseOut implements BaseEasingMethod{

	public QuartEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return (float) Math.pow( time - 1, 4) + 1;
    }
}
