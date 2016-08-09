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

public class SineEase extends BaseEasingMethod {

    public SineEase() {
        super();
    }

    @Override
    protected float easeOut(float time) {
        return (float)Math.sin(time * (Math.PI/2));
    }

    @Override
    protected float easeInOut(float time) {
        return -0.5f * ((float) Math.cos(Math.PI * time) - 1.f);
    }

    @Override
    protected float easeIn(float time) {
        return -(float) Math.cos(time * (Math.PI / 2.f)) + 1.f;
    }

}
