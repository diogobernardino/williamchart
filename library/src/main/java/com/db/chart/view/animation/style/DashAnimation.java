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

package com.db.chart.view.animation.style;

import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;

public class DashAnimation extends BaseStyleAnimation {
	
	
	@Override
	public void nextUpdate(ChartSet set) {
		LineSet line = (LineSet) set;
			line.setDashedPhase(line.getDashedPhase() - 4);
	}
	
}
