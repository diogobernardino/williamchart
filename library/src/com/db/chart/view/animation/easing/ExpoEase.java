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

public class ExpoEase extends BaseEasingMethod {

    public ExpoEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (time == 1) ? 1 : ( -(float) Math.pow( 2, -10 * time) + 1);
    }

    @Override
    protected float easeInOut(float time) {

        if (time == 0)
            return 0.f;

        if (time == 1f)
            return 1.f;

        float p = time / 0.5f;
        if (p < 1.f)
            return 0.5f * (float) Math.pow(2.f, 10.f * (p - 1.f));

        return 0.5f * ( -(float) Math.pow(2.f, -10.f * --p) + 2.f);
    }

    @Override
    protected float easeIn(float time) {
        return (time == 0.f) ? 0.f : (float) Math.pow( 2, 10.f * (time - 1.f));
    }

}
