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

package com.db.chart.model;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;


/**
 * Data model containing a set of {@link Bar} to be used by {@link com.db.chart.view.BaseBarChartView}.
 */
public class BarSet extends ChartSet{
	
	
	private static final String TAG = "chart.model.BarSet";


	/**
	 * Constructor.
	 */
	public BarSet(){
		super();
	}


	/**
	 * Constructor.
	 *
	 * @param labels
	 * @param values
	 */
    public BarSet(@NonNull String[] labels, @NonNull float[] values){
        super();

		if(labels == null || values == null)
			throw new IllegalArgumentException("Labels or/and values can't be null.");
		if(labels.length != values.length)
			throw new IllegalArgumentException("Arrays size doesn't match.");

        int nEntries = labels.length;
        for(int i = 0; i < nEntries; i++)
            addBar(labels[i], values[i]);
    }


	/**
	 * Add new {@link com.db.chart.model.Bar} from a string and a float.
	 *
	 * @param label   new {@link com.db.chart.model.Bar}'s label
	 * @param value   new {@link com.db.chart.model.Bar}'s value
	 */
	public void addBar(String label, float value){
		this.addBar(new Bar(label, value));
	}


	/**
	 * Add new {@link com.db.chart.model.Bar}.
	 *
	 * @param bar   new nonnull {@link com.db.chart.model.Bar}
	 */
	public void addBar(@NonNull Bar bar){
		this.addEntry(bar);
	}
	
	
	
	/*
	 * --------
	 * Getters
	 * --------
	 */

	/**
	 * Retrieve line's color.
	 *
	 * @return {@link com.db.chart.model.BarSet} color.
	 */
	public int getColor(){
		return this.getEntry(0).getColor();
	}

	
	
	/*
	 * -------------
	 * Setters
	 * -------------
	 */

	/**
	 * Define the color of bars. Previously defined colors will be overridden.
	 *
	 * @param color
	 * @return {@link com.db.chart.model.BarSet} self-reference.
	 */
	public BarSet setColor(@ColorInt int color){
		
		for(ChartEntry e : getEntries())
			e.setColor(color);
		return this;
	}


	/**
	 * Define a gradient color to the bars. Previously defined colors will be overridden.
	 *
	 * @param colors   The colors to be distributed among gradient
	 * @param positions   Position/order from which the colors will be place
	 * @return {@link com.db.chart.model.BarSet} self-reference.
	 */
    public BarSet setGradientColor(@NonNull int colors[], float[] positions){

		if(colors == null || colors.length == 0)
			throw new IllegalArgumentException("Colors argument can't be null or empty.");

        for(ChartEntry e : getEntries())
            ((Bar) e).setGradientColor(colors, positions);
        return this;
    }
	
}
