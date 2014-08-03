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

import java.util.ArrayList;

/**
 * Data model containing {@link ChartEntry} elements to be used by {@link ChartView}.
 */
public class ChartSet {

	
	/** Set with entries */
	private ArrayList<ChartEntry> mEntries;

	
	public ChartSet(){
		mEntries = new ArrayList<ChartEntry>();
	}
	
	
	public void addEntry(String label, float value){
		mEntries.add(new ChartEntry(label, value));
	}

	public void addEntry(ChartEntry e){
		mEntries.add(e);
	}
	
	

	public int size() {
		return mEntries.size();
	}
	
	
	public String toString(){
		return mEntries.toString();
	}
	
	
	/*
	 *Getters
	 *
	 */
	
	public ArrayList<ChartEntry> getEntries(){
		return mEntries;
	}
	
	public ChartEntry getEntry(int i) {
		return mEntries.get(i);
	}
	
	public float getValue(int i){
		return mEntries.get(i).getValue();
	}
	
	public String getLabel(int i) {
		return mEntries.get(i).getLabel();
	}

}
