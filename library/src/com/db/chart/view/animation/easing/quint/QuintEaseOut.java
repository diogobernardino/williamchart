package com.db.chart.view.animation.easing.quint;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class QuintEaseOut implements BaseEasingMethod{

	public QuintEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return (float) Math.pow(time - 1, 5) + 1;
    }
}
