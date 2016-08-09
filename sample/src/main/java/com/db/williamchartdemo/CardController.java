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

import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class CardController {


	private final ImageButton mPlayBtn;
	private final ImageButton mUpdateBtn;


	private final Runnable showAction = new Runnable() {
		@Override
		public void run() {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					show(unlockAction);
				}
			}, 500);
		}
	};

	private final Runnable unlockAction =  new Runnable() {
		@Override
		public void run() {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					unlock();
				}
			}, 500);
		}
	};


	protected boolean firstStage;


	protected CardController(CardView card){
		super();

		RelativeLayout toolbar = (RelativeLayout) card.findViewById(R.id.chart_toolbar);
		mPlayBtn = (ImageButton) toolbar.findViewById(R.id.play);
		mUpdateBtn = (ImageButton) toolbar.findViewById(R.id.update);

		mPlayBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss(showAction);
			}
		});

		mUpdateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				update();
			}
		});
	}


	public void init(){
		show(unlockAction);
	}


	protected void show(Runnable action){
		lock();
		firstStage = false;
	}

	protected void update(){
		lock();
		firstStage = !firstStage;
	}

	protected void dismiss(Runnable action){
		lock();
	}


	private void lock(){
		mPlayBtn.setEnabled(false);
		mUpdateBtn.setEnabled(false);
	}

	private void unlock(){
		mPlayBtn.setEnabled(true);
		mUpdateBtn.setEnabled(true);
	}
}
