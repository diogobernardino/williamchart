/*
 * Copyright 2015 Diogo Bernardino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.chart.view.animation.easing;

public class BounceEase extends BaseEasingMethod {

    @Override
    protected float easeOut(float time) {

        if ((time/=1) < (1/2.75f))
            return (7.5625f*time*time);
        else if (time < (2/2.75f))
            return (7.5625f*(time-=(1.5f/2.75f))*time + .75f);
        else if (time < (2.5/2.75))
            return (7.5625f*(time-=(2.25f/2.75f))*time + .9375f);
        else
            return (7.5625f*(time-=(2.625f/2.75f))*time + .984375f);
    }

    @Override
    protected float easeInOut(float time) {

        if (time < 0.5f)
            return easeIn(time * 2) * .5f;
        return easeOut(time * 2 - 1f) * .5f + .5f;
    }

    @Override
    protected float easeIn(float time) {
        return 1.f - easeOut(1.f - time);
    }
	
}
