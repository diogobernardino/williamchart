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

package com.db.chart.model;


/**
 * Data model that represents a bar in {@link com.db.chart.view.BaseBarChartView}
 */
public class Bar extends ChartEntry{

	
	/** Default bar color */
	private static final int DEFAULT_COLOR = -16777216;
	
	
	/** Bar color */
	private int mColor;
	
	
	
	public Bar(String label, float value){
		super(label, value);
		mColor = DEFAULT_COLOR;
	}

	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */
	
	public int getColor() {
		return mColor;
	}

	
	
	
	/*
	 * --------
	 * Setters
	 * --------
	 */
	
	public void setColor(int mColor) {
		this.mColor = mColor;
	}
	
}
