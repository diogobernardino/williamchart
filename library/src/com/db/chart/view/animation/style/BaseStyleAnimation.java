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
import com.db.chart.view.ChartView;

public abstract class BaseStyleAnimation {

	private static final long DELAY_BETWEEN_UPDATES = 100;
	
	private ChartView mChartView;
	
	private ChartSet mSet;
	
	/** Control animation updates */
    final private Runnable mAnimator = new Runnable() {
        @Override
        public void run() {
        	if(mChartView.canIPleaseAskYouToDraw()){
        		mChartView.postInvalidate();
        		getUpdate(mSet);
        	}
        }
    };
	
    
    
	public void play(ChartView lineChartView, ChartSet set){
		mChartView = lineChartView;
		mSet = set;
		getUpdate(mSet);
	}
	
	
	private void getUpdate(ChartSet set){
		nextUpdate(set);
		mChartView.postDelayed(mAnimator, DELAY_BETWEEN_UPDATES);
	}
	
	
	protected abstract void nextUpdate(ChartSet set);
	
}
