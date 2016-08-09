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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.williamchartdemo.barchart.BarCardOne;
import com.db.williamchartdemo.barchart.BarCardThree;
import com.db.williamchartdemo.barchart.BarCardTwo;
import com.db.williamchartdemo.linechart.LineCardOne;
import com.db.williamchartdemo.linechart.LineCardThree;
import com.db.williamchartdemo.linechart.LineCardTwo;
import com.db.williamchartdemo.stackedchart.StackedCardOne;
import com.db.williamchartdemo.stackedchart.StackedCardThree;
import com.db.williamchartdemo.stackedchart.StackedCardTwo;


public class ChartsFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.charts, container, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        (new LineCardOne((CardView) layout.findViewById(R.id.card1), getContext())).init();
        (new LineCardThree((CardView) layout.findViewById(R.id.card2), getContext())).init();
        (new BarCardOne((CardView) layout.findViewById(R.id.card3), getContext())).init();
        (new StackedCardThree((CardView) layout.findViewById(R.id.card4), getContext())).init();
        (new StackedCardOne((CardView) layout.findViewById(R.id.card5))).init();
        (new BarCardThree((CardView) layout.findViewById(R.id.card6), getContext())).init();
        (new BarCardTwo((CardView) layout.findViewById(R.id.card7), getContext())).init();
        (new StackedCardTwo((CardView) layout.findViewById(R.id.card8))).init();
        (new LineCardTwo((CardView) layout.findViewById(R.id.card9))).init();

        return layout;
    }


}
