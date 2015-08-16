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

package com.db.chart.listener;

import android.graphics.Rect;

/**
 * Interface to define a listener when an chart entry has been clicked
 */
public interface OnEntryClickListener{
	
	/**
	 * Abstract method to define the code when an entry has been clicked
	 * @param setIndex - index of the entry set clicked
	 * @param entryIndex - index of the entry within set(setIndex) 
	 * @param rect - a Rect covering the entry area.
	 */
	void onClick(int setIndex, int entryIndex, Rect rect);
	
}
