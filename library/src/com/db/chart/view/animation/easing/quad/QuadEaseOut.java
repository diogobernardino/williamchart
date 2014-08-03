package com.db.chart.view.animation.easing.quad;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class QuadEaseOut implements BaseEasingMethod{
    
	public QuadEaseOut() {
        super();
    }

    @Override
    public float next(float time) {
        return -time * (time - 2);
    }
}
