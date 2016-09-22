package com.db.chart.renderer;

import android.graphics.Paint;
import android.test.suitebuilder.annotation.MediumTest;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import com.db.chart.view.ChartView.Style;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@MediumTest
public class XRendererTest {

	private final static float RESULT_THRESHOLD = .5f;

	@Mock
	Style mStyleMock;

	@Mock
	Paint mMockPaint;

	private XRenderer mXRndr;

	private ArrayList<ChartSet> mData;


	@Before
	public void setUp() {

		mXRndr = new XRenderer();

		BarSet set = new BarSet();
		for (int i = 0; i < 2; i++)
			set.addBar(new Bar(Integer.toString(i), (float) i));
		mData = new ArrayList<>();
		mData.add(set);

		mXRndr.init(mData, mStyleMock);
	}


	@Test
	public void defineAxisPosition_HasAxis_Result() {

		when(mStyleMock.getAxisThickness()).thenReturn(6.f);
		when(mStyleMock.hasXAxis()).thenReturn(true);

		mXRndr.setInnerChartBounds(0.f, 0.f, 0.f, 5.f);

		assertEquals(8.f, mXRndr.defineAxisPosition(), RESULT_THRESHOLD);
	}


	@Test
	public void defineStaticLabelsPosition_LabelsInsideHasAxis_Result() {

		when(mStyleMock.hasXAxis()).thenReturn(true);
		when(mStyleMock.getAxisThickness()).thenReturn(6.f);
		when(mStyleMock.getLabelsPaint()).thenReturn(mMockPaint);
		when(mMockPaint.descent()).thenReturn(6.f);

		mXRndr.setInnerChartBounds(0, 0, 0, 18.f);
		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.INSIDE);
		float axisCoordinate = mXRndr.defineAxisPosition();
		float labelsCoordinate = mXRndr.defineStaticLabelsPosition(axisCoordinate, 6);

		assertEquals(6.f, labelsCoordinate, RESULT_THRESHOLD);
		assertTrue(labelsCoordinate < axisCoordinate);
	}


	@Test
	public void defineStaticLabelsPosition_LabelsOutsideHasAxis_Result() {

		when(mStyleMock.hasXAxis()).thenReturn(true);
		when(mStyleMock.getAxisThickness()).thenReturn(6.f);
		when(mStyleMock.getFontMaxHeight()).thenReturn(12);
		when(mStyleMock.getLabelsPaint()).thenReturn(mMockPaint);
		when(mMockPaint.descent()).thenReturn(6.f);

		mXRndr.setInnerChartBounds(0, 0, 0, 18.f);
		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.OUTSIDE);
		float axisCoordinate = mXRndr.defineAxisPosition();
		float labelsCoordinate = mXRndr.defineStaticLabelsPosition(axisCoordinate, 6);

		assertEquals(36.f, labelsCoordinate, RESULT_THRESHOLD);
		assertTrue(labelsCoordinate > axisCoordinate);
	}


	@Test
	public void measureInnerChartLeft_LabelsNone_Result() {

		when(mStyleMock.getLabelsPaint()).thenReturn(mMockPaint);
		when(mMockPaint.measureText("0")).thenReturn(6.f);

		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.NONE);

		assertEquals(5.f, mXRndr.measureInnerChartLeft(5), RESULT_THRESHOLD);
	}


	@Test
	public void measureInnerChartLeft_LabelsNotNone_Result() {

		when(mStyleMock.getLabelsPaint()).thenReturn(mMockPaint);
		when(mMockPaint.measureText("0")).thenReturn(6.f);

		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.INSIDE);

		assertEquals(3.f, mXRndr.measureInnerChartLeft(5), RESULT_THRESHOLD);
	}


	@Test
	public void measureInnerChartRight_NoLabels_Result() {

		when(mStyleMock.getLabelsPaint()).thenReturn(mMockPaint);
		when(mMockPaint.measureText("1")).thenReturn(6.f);

		mXRndr.init(mData, mStyleMock);
		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.OUTSIDE);
		mXRndr.setMandatoryBorderSpacing(true);
		mXRndr.setBorderSpacing(1);

		assertEquals(5.f, mXRndr.measureInnerChartRight(6), RESULT_THRESHOLD);
	}


	@Test
	public void measureInnerChartRight_NoData_Result() {

		when(mStyleMock.getLabelsPaint()).thenReturn(mMockPaint);
		when(mMockPaint.measureText("1")).thenReturn(6.f);

		ArrayList data = new ArrayList<BarSet>();
		data.add(new BarSet());
		mXRndr.init(data, mStyleMock);

		assertEquals(5.f, mXRndr.measureInnerChartRight(5), RESULT_THRESHOLD);
	}


	@Test
	public void measureInnerChartBottom_HasAxisLabelsOutside_Result() {

		when(mStyleMock.hasXAxis()).thenReturn(true);
		when(mStyleMock.getAxisThickness()).thenReturn(6.f);
		when(mStyleMock.getFontMaxHeight()).thenReturn(6);

		mXRndr.setAxisLabelsSpacing(6.f);
		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.OUTSIDE);

		assertEquals(6.f, mXRndr.measureInnerChartBottom(24), RESULT_THRESHOLD);
	}


	@Test
	public void parsePos_HandleValuesNoLabels_Result(){

		when(mStyleMock.hasXAxis()).thenReturn(false);

		mXRndr.setHandleValues(true);
		mXRndr.setInnerChartBounds(0, 0, 10, 10);
		mXRndr.setLabelsPositioning(AxisRenderer.LabelPosition.NONE);
		mXRndr.init(mData, mStyleMock);
		mXRndr.dispose();

		assertTrue(mXRndr.parsePos(0, 0) < mXRndr.parsePos(0, 0.1));
		assertTrue(mXRndr.parsePos(0, 1) < mXRndr.parsePos(0, 111));
		assertTrue(mXRndr.parsePos(0, -1) > mXRndr.parsePos(0, -111));
		assertTrue(mXRndr.parsePos(0, -1) < mXRndr.parsePos(0, 1));
	}

}
