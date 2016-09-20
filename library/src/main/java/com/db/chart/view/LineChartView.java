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

package com.db.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Shader;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;

import com.db.chart.Tools;
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.williamchart.R;

import java.util.ArrayList;


/**
 * Implements a line chart extending {@link ChartView}
 */
public class LineChartView extends ChartView {


	/** Style applied to line chart */
	final private Style mStyle;

	/** Radius clickable region */
	private float mClickableRadius;


	public LineChartView(Context context, AttributeSet attrs) {

		super(context, attrs);

		setOrientation(Orientation.VERTICAL);
		mStyle = new Style(
				  context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
		mClickableRadius = context.getResources().getDimension(R.dimen.dot_region_radius);
	}


	public LineChartView(Context context) {

		super(context);

		setOrientation(Orientation.VERTICAL);
		mStyle = new Style();
		mClickableRadius = context.getResources().getDimension(R.dimen.dot_region_radius);
	}


	/**
	 * Credits: http://www.jayway.com/author/andersericsson/
	 * Given an index in points, it will make sure the the returned index is
	 * within the array.
	 */
	private static int si(int setSize, int i) {

		if (i > setSize - 1) return setSize - 1;
		else if (i < 0) return 0;
		return i;
	}


	@Override
	public void onAttachedToWindow() {

		super.onAttachedToWindow();
		mStyle.init();
	}


	@Override
	public void onDetachedFromWindow() {

		super.onDetachedFromWindow();
		mStyle.clean();
	}


	/**
	 * Method responsible to draw a line with the parsed screen points.
	 *
	 * @param canvas The canvas to draw on.
	 */
	@Override
	public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {

		LineSet lineSet;
		Path linePath;

		for (ChartSet set : data) {

			lineSet = (LineSet) set;

			if (lineSet.isVisible()) {

				mStyle.mLinePaint.setColor(lineSet.getColor());
				mStyle.mLinePaint.setStrokeWidth(lineSet.getThickness());
				applyShadow(mStyle.mLinePaint, lineSet.getAlpha(), lineSet.getShadowDx(), lineSet
						  .getShadowDy(), lineSet.getShadowRadius(), lineSet.getShadowColor());

				if (lineSet.isDashed()) mStyle.mLinePaint.setPathEffect(
						  new DashPathEffect(lineSet.getDashedIntervals(), lineSet.getDashedPhase()));
				else mStyle.mLinePaint.setPathEffect(null);

				if (!lineSet.isSmooth()) linePath = createLinePath(lineSet);
				else linePath = createSmoothLinePath(lineSet);

				//Draw background
				if (lineSet.hasFill() || lineSet.hasGradientFill())
					canvas.drawPath(createBackgroundPath(new Path(linePath), lineSet), mStyle.mFillPaint);

				//Draw line
				canvas.drawPath(linePath, mStyle.mLinePaint);

				//Draw points
				drawPoints(canvas, lineSet);
			}
		}

	}


	/**
	 * (Optional) To be overridden in order for each chart to define its own clickable regions.
	 * This way, classes extending ChartView will only define their clickable regions.
	 * <p>
	 * Important: the returned vector must match the order of the data passed
	 * by the user. This ensures that onTouchEvent will return the correct index.
	 *
	 * @param data {@link java.util.ArrayList} of {@link com.db.chart.model.ChartSet}
	 * to use while defining each region of a {@link com.db.chart.view.BarChartView}
	 *
	 * @return {@link java.util.ArrayList} of {@link android.graphics.Region} with regions
	 * where click will be detected
	 */
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data) {

		ArrayList<ArrayList<Region>> result = new ArrayList<>(data.size());

		ArrayList<Region> regionSet;
		float x;
		float y;
		for (ChartSet set : data) {

			regionSet = new ArrayList<>(set.size());
			for (ChartEntry e : set.getEntries()) {

				x = e.getX();
				y = e.getY();
				regionSet.add(new Region((int) (x - mClickableRadius), (int) (y - mClickableRadius),
						  (int) (x + mClickableRadius), (int) (y + mClickableRadius)));
			}
			result.add(regionSet);
		}

		return result;
	}


	/**
	 * Responsible for drawing points
	 */
	private void drawPoints(Canvas canvas, LineSet set) {

		int begin = set.getBegin();
		int end = set.getEnd();
		Point dot;
		for (int i = begin; i < end; i++) {

			dot = (Point) set.getEntry(i);

			if (dot.isVisible()) {

				// Style dot
				mStyle.mDotsPaint.setColor(dot.getColor());
				mStyle.mDotsPaint.setAlpha((int) (set.getAlpha() * 255));
				applyShadow(mStyle.mDotsPaint, set.getAlpha(), dot.getShadowDx(), dot
						  .getShadowDy(), dot.getShadowRadius(), dot.getShadowColor());

				// Draw dot
				canvas.drawCircle(dot.getX(), dot.getY(), dot.getRadius(), mStyle.mDotsPaint);

				//Draw dots stroke
				if (dot.hasStroke()) {

					// Style stroke
					mStyle.mDotsStrokePaint.setStrokeWidth(dot.getStrokeThickness());
					mStyle.mDotsStrokePaint.setColor(dot.getStrokeColor());
					mStyle.mDotsStrokePaint.setAlpha((int) (set.getAlpha() * 255));
					applyShadow(mStyle.mDotsStrokePaint, set.getAlpha(), dot.getShadowDx(), dot
							  .getShadowDy(), dot.getShadowRadius(), dot.getShadowColor());

					canvas.drawCircle(dot.getX(), dot.getY(), dot.getRadius(), mStyle.mDotsStrokePaint);
				}

				// Draw drawable
				if (dot.getDrawable() != null) {
					Bitmap dotsBitmap = Tools.drawableToBitmap(dot.getDrawable());
					canvas.drawBitmap(dotsBitmap, dot.getX() - dotsBitmap.getWidth() / 2,
							  dot.getY() - dotsBitmap.getHeight() / 2, mStyle.mDotsPaint);
				}
			}
		}

	}


	/**
	 * Responsible for drawing a (non smooth) line
	 *
	 * @param set {@link LineSet} object.
	 */
	Path createLinePath(LineSet set) {

		Path res = new Path();

		int begin = set.getBegin();
		int end = set.getEnd();
		for (int i = begin; i < end; i++) {
			if (i == begin) res.moveTo(set.getEntry(i).getX(), set.getEntry(i).getY());
			else res.lineTo(set.getEntry(i).getX(), set.getEntry(i).getY());
		}

		return res;
	}


	/**
	 * Credits: http://www.jayway.com/author/andersericsson/
	 * Method responsible to draw a smooth line with the parsed screen points.
	 *
	 * @param set {@link LineSet} object.
	 */
	Path createSmoothLinePath(LineSet set) {

		float thisPointX;
		float thisPointY;
		float nextPointX;
		float nextPointY;
		float startDiffX;
		float startDiffY;
		float endDiffX;
		float endDiffY;
		float firstControlX;
		float firstControlY;
		float secondControlX;
		float secondControlY;

		Path res = new Path();
		res.moveTo(set.getEntry(set.getBegin()).getX(), set.getEntry(set.getBegin()).getY());

		int begin = set.getBegin();
		int end = set.getEnd();
		for (int i = begin; i < end - 1; i++) {

			thisPointX = set.getEntry(i).getX();
			thisPointY = set.getEntry(i).getY();

			nextPointX = set.getEntry(i + 1).getX();
			nextPointY = set.getEntry(i + 1).getY();

			startDiffX = (nextPointX - set.getEntry(si(set.size(), i - 1)).getX());
			startDiffY = (nextPointY - set.getEntry(si(set.size(), i - 1)).getY());

			endDiffX = (set.getEntry(si(set.size(), i + 2)).getX() - thisPointX);
			endDiffY = (set.getEntry(si(set.size(), i + 2)).getY() - thisPointY);

			firstControlX = thisPointX + (0.15f * startDiffX);
			firstControlY = thisPointY + (0.15f * startDiffY);

			secondControlX = nextPointX - (0.15f * endDiffX);
			secondControlY = nextPointY - (0.15f * endDiffY);

			res.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX,
					  nextPointY);
		}

		return res;

	}


	/**
	 * Responsible for drawing line background
	 *
	 * @param path {@link Path} object containing line path
	 * @param set {@link LineSet} object.
	 */
	private Path createBackgroundPath(Path path, LineSet set) {

		mStyle.mFillPaint.setAlpha((int) (set.getAlpha() * 255));

		if (set.hasFill()) mStyle.mFillPaint.setColor(set.getFillColor());
		if (set.hasGradientFill()) mStyle.mFillPaint.setShader(
				  new LinearGradient(super.getInnerChartLeft(), super.getInnerChartTop(),
							 super.getInnerChartLeft(), super.getInnerChartBottom(),
							 set.getGradientColors(), set.getGradientPositions(), Shader.TileMode.MIRROR));

		path.lineTo(set.getEntry(set.getEnd() - 1).getX(), super.getInnerChartBottom());
		path.lineTo(set.getEntry(set.getBegin()).getX(), super.getInnerChartBottom());
		path.close();

		return path;
	}


	/**
	 * @param radius Point's radius where touch event will be detected
	 *
	 * @return {@link com.db.chart.view.LineChartView} self-reference.
	 */
	public LineChartView setClickablePointRadius(@FloatRange(from = 0.f) float radius) {

		mClickableRadius = radius;
		return this;
	}


	/**
	 * Class responsible to style the LineChart!
	 * Can be instantiated with or without attributes.
	 */
	class Style {


		/** Paint variables */
		private Paint mDotsPaint;

		private Paint mDotsStrokePaint;

		private Paint mLinePaint;

		private Paint mFillPaint;


		Style() {}


		Style(TypedArray attrs) {}


		private void init() {

			mDotsPaint = new Paint();
			mDotsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mDotsPaint.setAntiAlias(true);


			mDotsStrokePaint = new Paint();
			mDotsStrokePaint.setStyle(Paint.Style.STROKE);
			mDotsStrokePaint.setAntiAlias(true);

			mLinePaint = new Paint();
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setAntiAlias(true);

			mFillPaint = new Paint();
			mFillPaint.setStyle(Paint.Style.FILL);
		}


		private void clean() {

			mLinePaint = null;
			mFillPaint = null;
			mDotsPaint = null;
		}

	}

}
