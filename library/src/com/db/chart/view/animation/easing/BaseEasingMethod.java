/*
 * Copyright 2014 Diogo Bernardino
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
public interface BaseEasingMethod {

	/**
	 * Method that gives the next interpolated value to be processed by 
	 * the {@link Animation} object.
	 * @param normalizedTime - time normalized between 0 and 1.
	 * @return the next interpolation.
	 */
	public abstract float next(float normalizedTime);
	
}
