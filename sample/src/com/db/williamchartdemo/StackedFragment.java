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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.williamchartdemo.stackedchart.StackedCardOne;
import com.db.williamchartdemo.stackedchart.StackedCardThree;
import com.db.williamchartdemo.stackedchart.StackedCardTwo;


public class StackedFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.stacked, container, false);

        (new StackedCardOne((CardView) layout.findViewById(R.id.card1))).init();
        (new StackedCardTwo((CardView) layout.findViewById(R.id.card2))).init();
        (new StackedCardThree((CardView) layout.findViewById(R.id.card3))).init();

        return layout;
    }

}
