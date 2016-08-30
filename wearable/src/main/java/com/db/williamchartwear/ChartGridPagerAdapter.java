/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.williamchartwear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;


public class ChartGridPagerAdapter extends FragmentGridPagerAdapter {

	public ChartGridPagerAdapter(Context ctx, FragmentManager fm) {

		super(fm);
	}


	@Override
	public Fragment getFragment(int row, int col) {

		switch (row) {
			case 0:
				return new LineFragment();
			case 1:
				return new BarFragment();
			case 2:
				return new StackedFragment();
			default:
				return new LineFragment();
		}
	}


	@Override
	public int getRowCount() {

		return 3;
	}


	@Override
	public int getColumnCount(int i) {

		return 1;
	}
}
