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

package com.db.williamchartdemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ChartsFragment extends Fragment {

	private final static int FULL_SPAN = 3;
	private final static int MULTI_SPAN = 2;
	private final static int SINGLE_SPAN = 1;

	private ChartAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			  Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.charts, container, false);

		((AppCompatActivity) getActivity()).setSupportActionBar(
				  (Toolbar) layout.findViewById(R.id.toolbar));
		((TextView) layout.findViewById(R.id.title)).setTypeface(
				  Typeface.createFromAsset(getContext().getAssets(), "Ponsi-Regular.otf"));

		RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);

		GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), FULL_SPAN);
		mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {

				switch(mAdapter.getItemViewType(position)){
					case 0: return FULL_SPAN;
					case 1: return MULTI_SPAN;
					case 2: return SINGLE_SPAN;
					case 3: return FULL_SPAN;
					case 4: return FULL_SPAN;
					case 5: return SINGLE_SPAN;
					case 6: return MULTI_SPAN;
					case 7: return FULL_SPAN;
					case 8: return FULL_SPAN;
					default: return SINGLE_SPAN;
				}
			}
		});

		mAdapter = new ChartAdapter(getContext());

		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(mAdapter);

		return layout;
	}

}
