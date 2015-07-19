package com.db.chart.view.animation.easing;

public class QuadEase extends BaseEasingMethod {
    
	public QuadEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return -time * (time - 2);
    }

    @Override
    protected float easeIn(float time) {
        return time * time;
    }

}
