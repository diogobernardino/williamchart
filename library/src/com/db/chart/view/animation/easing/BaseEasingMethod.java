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

/**
 * Interface that gives the abstract methods to any possible
 * interpolator/easing function
 */
public abstract class BaseEasingMethod {

    public final static int ENTER = 0;
    public final static int UPDATE = 1;
    public final static int EXIT = 2;

    private static int mState;


    protected abstract float easeOut(float time);
    protected abstract float easeInOut(float time);
    protected abstract float easeIn(float time);


    /**
     * Method that gives the next interpolated value to be processed by
     * the {@link com.db.chart.view.animation.Animation} object.
     *
     * @param time - time normalized between 0 and 1
     * @return the next interpolation.
     */
    public float next(float time){

        if(mState == BaseEasingMethod.ENTER)
            return easeOut(time);
        else if(mState == BaseEasingMethod.UPDATE)
            return easeInOut(time);
        else if(mState == BaseEasingMethod.EXIT)
            return easeIn(time);
        return 1;
    }

    public int getState(){
        return mState;
    }


    /**
     * Whether interpolation should comply with ENTER, UPDATE, or EXIT animation.
     *
     * @param state
     */
    public void setState(int state){
        mState = state;
    }

}