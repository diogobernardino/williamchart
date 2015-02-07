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

package com.db.chart.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.Log;

import com.db.williamchart.R;
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;

/**
 * Class responsible to control vertical measures, positions, yadda yadda. 
 * If the drawing is requested it will also take care of it.
 */
public abstract class AxisController{


    public static enum LabelPosition {
        NONE, OUTSIDE, INSIDE
    }


    /** Default step between labels */
    private static final int DEFAULT_STEP = 1;


    /** ChartView object */
    protected ChartView chartView;


    /** Distance between axis and label */
    protected int distLabelToAxis;


    /** Label's values formatted */
    protected ArrayList<String> labels;

    /** Label's values */
    protected ArrayList<Integer> labelsValues;

    /** Labels position */
    protected ArrayList<Float> labelsPos;

    /** Number of labels */
    protected int nLabels;


    /** none/inside/outside */
    protected LabelPosition labelsPositioning;

    /** Labels Metric to draw together with labels */
    protected DecimalFormat labelFormat;

    /** Maximum height that a label can have */
    protected int labelHeight;


    /** Maximum value of labels */
    protected int maxLabelValue;

    /** Minimum value of labels */
    protected int minLabelValue;


    /** Step between labels */
    protected int step;

    /** Screen step between labels */
    protected float screenStep;


    /** Whether the chart has Y Axis or not */
    protected boolean hasAxis;

    /** Starting X point of the axis */
    protected float axisHorPosition;


    /** Spacing for top label */
    protected float topSpacing;


    /** Horizontal border spacing for labels */
    protected float borderSpacing;


    /** Mandatory horizontal border when necessary (ex: BarCharts) */
    protected float mandatoryBorderSpacing;


    /** Define whether labels must be taken from data or calculated from values */
    protected boolean handleValues;



    public AxisController(ChartView view) {

        chartView = view;

        //Set DEFAULTS
        distLabelToAxis= (int) chartView.getResources().getDimension(R.dimen.axis_dist_from_label);
        mandatoryBorderSpacing = 0;
        borderSpacing = 0;
        topSpacing = 0;
        step = DEFAULT_STEP;
        labelsPositioning = LabelPosition.OUTSIDE;
        labelFormat = new DecimalFormat();
        axisHorPosition = 0;
        minLabelValue = 0;
        maxLabelValue = 0;
        labelHeight = -1;
        hasAxis = true;
        handleValues = false;
    }


    public AxisController(ChartView chartView, TypedArray attrs) {
        this(chartView);
    }


    /**
     * Defines what will be the axis labels
     */
    protected void defineLabels() {

        labelsValues = calcLabels();
        if(handleValues)
            labels = getLabelsFromValues();
        else
            labels = getLabelsFromData();
        nLabels = labels.size();
    }



    /**
     * In case of a Chart that requires a mandatory border spacing (ex. BarChart)
     */
    protected void defineMandatoryBorderSpacing(float innerStart, float innerEnd){
        if(mandatoryBorderSpacing == 1)
            mandatoryBorderSpacing = (innerEnd - innerStart - borderSpacing * 2) / nLabels / 2;
    }



    /**
     * Calculates the position of each label along the axis.
     *
     * @param innerStart   Start inner position the chart
     * @param innerEnd   End inned position of chart
     */
    protected void defineLabelsPos(float innerStart, float innerEnd) {

        labelsPos = new ArrayList<Float>(nLabels);

        screenStep = (innerEnd
                - innerStart
                - topSpacing
                - borderSpacing * 2
                - mandatoryBorderSpacing * 2 )
                / (nLabels - 1);

        float currPos = innerStart + borderSpacing + mandatoryBorderSpacing;
        for(int i = 0; i < nLabels; i++){
            labelsPos.add(currPos);
            currPos += screenStep;
        }
    }



    /**
     * Get labels from values calculated before.
     */
    private ArrayList<String> getLabelsFromValues() {

        int size = labelsValues.size();
        ArrayList<String> result = new ArrayList<String>(size);
        for(int i = 0; i < size; i++)
            result.add(labelFormat.format(labelsValues.get(i)));
        return result;
    }


    /**
     * Get labels from chart data.
     */
    protected ArrayList<String> getLabelsFromData() {

        int size = chartView.data.get(0).size();
        ArrayList<String> result = new ArrayList<String>(size);
        for(int i = 0; i < size; i++)
            result.add(chartView.data.get(0).getLabel(i));
        return result;
    }



    /**
     * Calculates the min/max value.
     *
     * @return {min, max} value
     */
    private float[]  calcBorderValues() {

        float max = Integer.MIN_VALUE;
        float min = Integer.MAX_VALUE;

        for(ChartSet set : chartView.data){
            for(ChartEntry e : set.getEntries()){
                if(e.getValue() >= max)
                    max = e.getValue();
                if(e.getValue() <= min)
                    min = e.getValue();
            }
        }

        float[] result = {min, max};
        return result;
    }



    /**
     * Get labels based on the maximum value displayed
     *
     * @return result
     */
    private ArrayList<Integer> calcLabels(){

        float[] borderValues = calcBorderValues();
        float minValue = borderValues[0];
        float maxValue = borderValues[1];

        //If not specified then calculate border labels
        if(minLabelValue == 0 && maxLabelValue == 0){

            if(maxValue < 0)
                maxLabelValue = 0;
            else
                maxLabelValue = (int) Math.ceil(maxValue);

            if(minValue > 0)
                minLabelValue = 0;
            else
                minLabelValue = (int) Math.floor(minValue);

            while((maxLabelValue - minLabelValue) % step != 0)
                maxLabelValue += 1;
        }

        ArrayList<Integer> result = new ArrayList<Integer>();
        int pos = minLabelValue;
        while(pos <= maxLabelValue){
            result.add(pos);
            pos += step;
        }

        //Set max Y axis label in case isn't already there
        if(result.get(result.size() - 1) < maxLabelValue)
            result.add(maxLabelValue);

        return result;
    }



    protected int getLabelHeight(){

        if(labelHeight == -1){

            int result = 0;
            for(ChartEntry e : chartView.data.get(0).getEntries()){
                result = chartView.style.getTextHeightBounds(e.getLabel());
                if(result != 0)
                    break;
            }
            labelHeight = result;
        }

        return labelHeight;
    }



    /**
     * Method called from onDraw method to draw AxisController data.
     *
     * @param canvas   {@link android.graphics.Canvas} to use while drawing the data
     */
    abstract protected void draw(Canvas canvas);

}