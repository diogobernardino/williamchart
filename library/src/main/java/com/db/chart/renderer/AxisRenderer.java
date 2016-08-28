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

package com.db.chart.renderer;

import android.graphics.Canvas;

import com.db.chart.Tools;
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;
import com.db.chart.view.ChartView.Style;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Class responsible to control vertical measures, positions, yadda yadda.
 * If the drawing is requested it will also take care of it.
 */
public abstract class AxisRenderer {


	/** Distance between axis and label */
	int distLabelToAxis;

	/** Label's values formatted */
	ArrayList<String> labels;

	/** Label's values */
	ArrayList<Integer> labelsValues;

	/** Labels position */
	ArrayList<Float> labelsPos;

	/** Refers to the coordinate X in case of Axis Y and coordinate Y in case of Axis X */
	float labelsStaticPos;

	/** Number of labels */
	int nLabels;

	/** none/inside/outside */
	LabelPosition labelsPositioning;

	/** Labels Metric to draw together with labels */
	DecimalFormat labelFormat;

	/** Maximum value of labels */
	int maxLabelValue;

	/** Minimum value of labels */
	int minLabelValue;

	/** Step between labels */
	int step;

	/** Screen step between labels */
	float screenStep;

	/** Starting X point of the axis */
	float axisPosition;

	/** Spacing for top label */
	float topSpacing;

	/** Horizontal border spacing for labels */
	float borderSpacing;

	/** Mandatory horizontal border when necessary (ex: BarCharts) */
	float mandatoryBorderSpacing;

	/** Define whether labels must be taken from data or calculated from values */
	boolean handleValues;

	/** Inner chart borders (inner chart means the chart's area where datasets are drawn) */
	float mInnerChartLeft;

	float mInnerChartTop;

	float mInnerChartRight;

	float mInnerChartBottom;

	/** Object containing style attributes of chart */
	Style style;


	AxisRenderer() {

		reset();
	}


	/**
	 * Define labels/values of axis.
	 */
	public void init(ArrayList<ChartSet> data, Style style) {

		if (handleValues) {
			if (minLabelValue == 0 && maxLabelValue == 0) { // If no pre-defined borders
				int[] borders;
				if(hasStep()) borders = findBorders(data, step);
				else borders = findBorders(data, 1);
				minLabelValue = borders[0];
				maxLabelValue = borders[1];
			}
			if (!hasStep()) // If no pre-defined step
				setBorderValues(minLabelValue, maxLabelValue);
			labelsValues = calculateValues(minLabelValue, maxLabelValue, step);
			labels = convertToLabelsFormat(labelsValues, labelFormat);
		} else {
			labels = extractLabels(data);
		}
		nLabels = labels.size();
		this.style = style;
	}


	/**
	 * Dispose the various axis elements in their positions.
	 */
	void dispose() {

		axisPosition = defineAxisPosition();
		labelsStaticPos = defineStaticLabelsPosition(axisPosition, distLabelToAxis);
	}


	/**
	 * Measure inner bounds required in order to have enough space
	 * to display all axis elements based.
	 *
	 * @param left left position of chart
	 * @param top top position of chart
	 * @param right right position of chart
	 * @param bottom bottom position of chart
	 */
	protected abstract void measure(int left, int top, int right, int bottom);


	/**
	 * Define position of axis.
	 */
	protected abstract float defineAxisPosition();


	/**
	 * Define static position of labels.
	 * If X axis, static position means vertical labels position,
	 * otherwise, if Y it means horizontal labels position.
	 */
	protected abstract float defineStaticLabelsPosition(float axisCoordinate, int distanceToAxis);


	/**
	 * Method called from onDraw method to draw AxisController data.
	 *
	 * @param canvas {@link android.graphics.Canvas} to use while drawing the data
	 */
	abstract protected void draw(Canvas canvas);


	/**
	 * Based in a (real) value returns the associated screen point.
	 *
	 * @param index Index of label.
	 * @param value Value to be parsed in display coordinate.
	 *
	 * @return Display's coordinate
	 */
	public abstract float parsePos(int index, double value);


	/**
	 * Reset renderer attributes to defaults.
	 */
	public void reset() {

		distLabelToAxis = (int) Tools.fromDpToPx(5.f);
		mandatoryBorderSpacing = 0;
		borderSpacing = 0;
		topSpacing = 0;
		step = -1;
		labelsStaticPos = 0;
		labelsPositioning = LabelPosition.OUTSIDE;
		labelFormat = new DecimalFormat();
		axisPosition = 0;
		minLabelValue = 0;
		maxLabelValue = 0;
		handleValues = false;
	}


	/**
	 * In case of a Chart that requires a mandatory border spacing (ex. BarChart).
	 *
	 * @param innerStart Inner chart start
	 * @param innerEnd Inner chart end
	 */
	void defineMandatoryBorderSpacing(float innerStart, float innerEnd) {

		if (mandatoryBorderSpacing == 1)
			mandatoryBorderSpacing = (innerEnd - innerStart - borderSpacing * 2) / nLabels / 2;
	}


	/**
	 * Calculates the position of each label along the axis.
	 *
	 * @param innerStart Start inner position the chart
	 * @param innerEnd End inned position of chart
	 */
	void defineLabelsPosition(float innerStart, float innerEnd) {

		screenStep = (innerEnd -
				  innerStart -
				  topSpacing -
				  borderSpacing * 2 -
				  mandatoryBorderSpacing * 2) / (nLabels - 1);

		labelsPos = new ArrayList<>(nLabels);
		float currPos = innerStart + borderSpacing + mandatoryBorderSpacing;
		for (int i = 0; i < nLabels; i++) {
			labelsPos.add(currPos);
			currPos += screenStep;
		}
	}


	/**
	 * Generate and format strings out of axis values.
	 *
	 * @param values Axis values
	 * @param format Format to be applied to string results
	 *
	 * @return An {@link ArrayList} containing the set of strings generated
	 * from axis values and to be displayed along the axis.
	 */
	ArrayList<String> convertToLabelsFormat(ArrayList<Integer> values, DecimalFormat format) {

		int size = values.size();
		ArrayList<String> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			result.add(format.format(values.get(i)));
		return result;
	}


	/**
	 * Extract labels from chart data.
	 *
	 * @param sets {@link ArrayList} containing all {@link ChartSet} elements of chart
	 *
	 * @return Extracted labels which are common among all {@link ChartSet} elements.
	 */
	ArrayList<String> extractLabels(ArrayList<ChartSet> sets) {

		int size = sets.get(0).size();
		ArrayList<String> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			result.add(sets.get(0).getLabel(i));
		return result;
	}


	/**
	 * Find out what are the minimum and maximum values of the
	 * axis based on {@link ChartSet} values.
	 *
	 * @param sets {@link ArrayList} containing {@link ChartSet} elements of chart
	 * @param step Step to be used between axis values
	 *
	 * @return Int vector containing both minimum and maximum value to be used.
	 */
	int[] findBorders(ArrayList<ChartSet> sets, int step) {

		float max = Integer.MIN_VALUE;
		float min = Integer.MAX_VALUE;

		// Find minimum and maximum value out of all chart entries
		for (ChartSet set : sets) {
			for (ChartEntry e : set.getEntries()) {
				if (e.getValue() >= max) max = e.getValue();
				if (e.getValue() <= min) min = e.getValue();
			}
		}

		max = (max < 0) ? 0 : (int) Math.ceil(max);
		min = (min > 0) ? 0 : (int) Math.floor(min);
		while ((max - min) % step != 0) max += 1;

		// All given set values are 0
		if (min == max) max += step;

		return new int[] {(int) min, (int) max};
	}


	/**
	 * Calculate labels based on the minimum and maximum value displayed
	 * as well as the step used to defined both of them.
	 *
	 * @param min Minimum axis value
	 * @param max Maximum axis value
	 * @param step Step to be used between axis values
	 *
	 * @return {@link ArrayList} containing all values to be displayed along the axis.
	 */
	ArrayList<Integer> calculateValues(int min, int max, int step) {

		ArrayList<Integer> result = new ArrayList<>();
		int pos = min;
		while (pos <= max) {
			result.add(pos);
			pos += step;
		}

		// Set max Y axis label in case isn't already there
		if (result.get(result.size() - 1) < max) result.add(pos);

		return result;
	}


	/**
	 * Get left inner chart border (inner chart means the chart's area where datasets are drawn).
	 *
	 * @return Inner left coordinate position.
	 */
	public float getInnerChartLeft() {

		return mInnerChartLeft;
	}


	/**
	 * Get top inner chart border (inner chart means the chart's area where datasets are drawn).
	 *
	 * @return Inner top coordinate position.
	 */
	public float getInnerChartTop() {

		return mInnerChartTop;
	}


	/**
	 * Get right inner chart border (inner chart means the chart's area where datasets are drawn).
	 *
	 * @return Inner left coordinate position.
	 */
	public float getInnerChartRight() {

		return mInnerChartRight;
	}


	/**
	 * Get bottom inner chart border (inner chart means the chart's area where datasets are drawn).
	 *
	 * @return Inner bottom coordinate position.
	 */
	public float getInnerChartBottom() {

		return mInnerChartBottom;
	}


	/**
	 * Get inner chart bounds.
	 * Inner chart means the chart's area where datasets are drawn,
	 * chart's area excluding axis area.
	 *
	 * @return Inner left coordinate position.
	 */
	public float[] getInnerChartBounds() {

		return new float[] {mInnerChartLeft, mInnerChartTop, mInnerChartRight, mInnerChartBottom};
	}


	/**
	 * Get step between axis values.
	 *
	 * @return Step used between axis values.
	 */
	public int getStep() {

		return step;
	}


	/**
	 * Get axis border spacing, meaning space between chart area limit and side labels.
	 *
	 * @return Axis border spacing.
	 */
	public float getBorderSpacing() {

		return borderSpacing;
	}


	/**
	 * @return Axis maximum border value.
	 */
	public int getBorderMaximumValue() {

		return maxLabelValue;
	}


	/**
	 * @return Axis minimum border value.
	 */
	public int getBorderMinimumValue() {

		return minLabelValue;
	}


	/**
	 * If needs mandatory border spacing.
	 *
	 * @return True if needs mandatory border spacing, False otherwise.
	 */
	public boolean hasMandatoryBorderSpacing() {

		return (mandatoryBorderSpacing == 1);
	}


	/**
	 *
	 */
	public boolean hasStep(){
		return (step != -1);
	}


	/**
	 * Set renderer to handle {@link ChartSet} values, not labels.
	 *
	 * @param bool True to handle {@link ChartSet} values, False otherwise.
	 */
	public void setHandleValues(boolean bool) {

		handleValues = bool;
	}


	/**
	 * Set where labels should be placed in relation to axis (none/inside/outside).
	 *
	 * @param position {@link LabelPosition} value defining where should be positioned.
	 */
	public void setLabelsPositioning(LabelPosition position) {

		labelsPositioning = position;
	}


	/**
	 * Set labels format. For instance, use {@link DecimalFormat}
	 * to append the metric 'KB' to labels.
	 *
	 * @param format {@link DecimalFormat} to be used when defining axis labels.
	 */
	public void setLabelsFormat(DecimalFormat format) {

		this.labelFormat = format;
	}


	/**
	 * Set step between axis values.
	 *
	 * @param step Step to be used between axis values.
	 */
	public void setStep(int step) {

		this.step = step;
	}


	/**
	 * Set axis border spacing, meaning space between chart area limit and side labels.
	 *
	 * @param spacing Axis border spacing.
	 */
	public void setBorderSpacing(float spacing) {

		borderSpacing = spacing;
	}


	/**
	 * Set top border spacing, meaning space between top chart area limit and first top label.
	 *
	 * @param spacing Top axis border spacing.
	 */
	public void setTopSpacing(float spacing) {

		topSpacing = spacing;
	}


	/**
	 * Set if axis needs mandatory border spacing.
	 *
	 * @param bool True if needs mandatory border spacing, False otherwise.
	 */
	public void setMandatoryBorderSpacing(boolean bool) {

		mandatoryBorderSpacing = (bool) ? 1 : 0;
	}


	/**
	 * Set distance between labels and axis.
	 *
	 * @param distLabelToAxis Distance between labels and axis.
	 */
	public void setLabelToAxisDistance(int distLabelToAxis) {

		this.distLabelToAxis = distLabelToAxis;
	}


	/**
	 * Set inner chart bounds.
	 * Inner chart means the chart's area where datasets are drawn,
	 * chart's area excluding axis area.
	 *
	 * @param left Inner left coordinate position.
	 * @param top Inner top coordinate position.
	 * @param right Inner right coordinate position.
	 * @param bottom Inner bottom coordinate position.
	 */
	public void setInnerChartBounds(float left, float top, float right, float bottom) {

		mInnerChartLeft = left;
		mInnerChartTop = top;
		mInnerChartRight = right;
		mInnerChartBottom = bottom;
	}


	/**
	 * Define spacing between axis and labels.
	 *
	 * @param spacing Spacing between axis and labels
	 */
	public void setAxisLabelsSpacing(float spacing) {

		distLabelToAxis = (int) spacing;
	}


	/**
	 * Force axis range of values.
	 * A step is seen as the step to be defined between 2 labels. As an
	 * example a step of 2 with a maxAxisValue of 6 will end up with
	 * {0, 2, 4, 6} as labels.
	 *
	 * @param min The minimum value that Y axis will have as a label
	 * @param max The maximum value that Y axis will have as a label
	 * @param step (real) value distance from every label
	 */
	public void setBorderValues(int min, int max, int step) {

		if (min >= max) throw new IllegalArgumentException(
				  "Minimum border value must be greater than maximum values");
		if ((max - min) % step != 0) throw new IllegalArgumentException(
				  "Step value must be a divisor of distance between minimum " +
							 "border value and maximum border value");

		this.step = step;
		maxLabelValue = max;
		minLabelValue = min;
	}


	/**
	 * Force axis range of values. If minimum greater than 0
	 * the largest divisor between the delta maximum - minimum will be set automatically as
	 * the step.
	 *
	 * @param min The minimum value that Y axis will have as a label
	 * @param max The maximum value that Y axis will have as a label
	 */
	public void setBorderValues(int min, int max) {

		if (!hasStep())
			step = Tools.largestDivisor(max - min);
		setBorderValues(min, max, step);
	}


	public enum LabelPosition {
		NONE, OUTSIDE, INSIDE
	}

}