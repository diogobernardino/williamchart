package com.db.chart.view.animation.easing.bounce;

import com.db.chart.view.animation.easing.BaseEasingMethod;

public class BounceEaseOut implements BaseEasingMethod{
	
	@Override
	public float next(float normalizedTime) {
		if ((normalizedTime/=1) < (1/2.75f)) {
			return (7.5625f*normalizedTime*normalizedTime);
		} else if (normalizedTime < (2/2.75f)) {
			return (7.5625f*(normalizedTime-=(1.5f/2.75f))*normalizedTime + .75f);
		} else if (normalizedTime < (2.5/2.75)) {
			return (7.5625f*(normalizedTime-=(2.25f/2.75f))*normalizedTime + .9375f);
		} else {
			return (7.5625f*(normalizedTime-=(2.625f/2.75f))*normalizedTime + .984375f);
		}
	}

	
	
}
