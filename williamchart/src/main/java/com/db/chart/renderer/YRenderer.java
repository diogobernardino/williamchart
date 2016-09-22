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
import android.graphics.Paint.Align;

import java.util.Collections;


/**
 * Class responsible to control vertical measures, positions, yadda yadda.
 * If the drawing is requested it will also take care of it.
 */
public class YRenderer extends AxisRenderer {


	public YRenderer() {

		super();
	}


	/*
	 * IMPORTANT: Method's calls order is crucial. Change it (or not) carefully.
	 */
	@Override
	public void dispose() {

		super.dispose();

		defineMandatoryBorderSpacing(mInnerChartTop, mInnerChartBottom);
		defineLabelsPosition(mInnerChartTop, mInnerChartBottom);
	}


	@Override
	public void measure(int left, int top, int right, int bottom) {

		mInnerChartLeft = measureInnerChartLeft(left);
		mInnerChartTop = measureInnerChartTop(top);
		mInnerChartRight = measureInnerChartRight(right);
		mInnerChartBottom = measureInnerChartBottom(bottom);
	}


	/**
	 * Get the horizontal position of axis based on the chart left bottom.
	 */
	@Override
	protected float defineAxisPosition() {

		float result = mInnerChartLeft;
		if (style.hasYAxis()) result -= style.getAxisThickness() / 2;
		return result;
	}


	/**
	 * Get the horizontal position of labels based on the axis position.
	 */
	@Override
	protected float defineStaticLabelsPosition(float axisCoordinate, int distanceToAxis) {

		float result = axisCoordinate;

		if (labelsPositioning == LabelPosition.INSIDE) {
			result += distanceToAxis;
			if (style.hasYAxis()) result += style.getAxisThickness() / 2;

		} else if (labelsPositioning == LabelPosition.OUTSIDE) {
			result -= distanceToAxis;
			if (style.hasYAxis()) result -= style.getAxisThickness() / 2;
		}
		return result;
	}


	@Override
	public void draw(Canvas canvas) {

		if (style.hasYAxis()) {
			// Draw axis line
			float bottom = mInnerChartBottom;
			if (style.hasXAxis()) bottom += style.getAxisThickness();

			canvas.drawLine(axisPosition, mInnerChartTop, axisPosition, bottom, style.getChartPaint());
		}

		if (labelsPositioning != LabelPosition.NONE) {

			style.getLabelsPaint()
					  .setTextAlign(
								 (labelsPositioning == LabelPosition.OUTSIDE) ? Align.RIGHT : Align.LEFT);

			// Draw labels
			for (int i = 0; i < nLabels; i++) {
				canvas.drawText(labels.get(i), labelsStaticPos,
						  labelsPos.get(i) + style.getLabelHeight(labels.get(i)) / 2,
						  style.getLabelsPaint());
			}
		}
	}


	@Override
	void defineLabelsPosition(float innerStart, float innerEnd) {

		super.defineLabelsPosition(innerStart, innerEnd);
		Collections.reverse(labelsPos);
	}


	/**
	 * Based in a (real) value returns the associated screen point.
	 *
	 * @param index Index of label.
	 * @param value Value to be parsed in display coordinate.
	 *
	 * @return Display's coordinate
	 */
	@Override
	public float parsePos(int index, double value) {

		if (handleValues) return (float) (mInnerChartBottom -
				  (((value - minLabelValue) * screenStep) / (labelsValues.get(1) - minLabelValue)));
		else return labelsPos.get(index);
	}


	/**
	 * Measure the necessary padding from the chart left border defining the
	 * coordinate of the inner chart left border. Inner Chart refers only to the
	 * area where chart data will be draw, excluding labels, axis, etc.
	 *
	 * @param left Left position of chart area
	 *
	 * @return Coordinate of the inner left side of the chart
	 */
	public float measureInnerChartLeft(int left) {

		float result = left;

		if (style.hasYAxis()) result += style.getAxisThickness();

		if (labelsPositioning == LabelPosition.OUTSIDE) {
			float aux;
			float maxLabelLength = 0;
			for (String label : labels)
				if ((aux = style.getLabelsPaint().measureText(label)) > maxLabelLength)
					maxLabelLength = aux;
			result += maxLabelLength + distLabelToAxis;
		}
		return result;
	}


	/**
	 * Measure the necessary padding from the chart left border defining the
	 * coordinate of the inner chart top border. Inner Chart refers only to the
	 * area where chart data will be draw, excluding labels, axis, etc.
	 *
	 * @param top Top position of chart area
	 *
	 * @return Coordinate of the inner top side of the chart
	 */
	private float measureInnerChartTop(int top) {

		return top;
	}


	/**
	 * Measure the necessary padding from the chart left border defining the
	 * coordinate of the inner chart right border. Inner Chart refers only to the
	 * area where chart data will be draw, excluding labels, axis, etc.
	 *
	 * @param right Right position of chart area
	 *
	 * @return Coordinate of the inner right side of the chart
	 */
	private float measureInnerChartRight(int right) {

		return right;
	}


	/**
	 * Measure the necessary padding from the chart left border defining the
	 * coordinate of the inner chart bottom border. Inner Chart refers only to the
	 * area where chart data will be draw, excluding labels, axis, etc.
	 *
	 * @param bottom Bottom position of chart area
	 *
	 * @return Coordinate of the inner bottom side of the chart
	 */
	public float measureInnerChartBottom(int bottom) {

		if (labelsPositioning != LabelPosition.NONE && borderSpacing < style.getFontMaxHeight() / 2)
			return bottom - style.getFontMaxHeight() / 2;
		return bottom;
	}

}