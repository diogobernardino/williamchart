package com.db.chart.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.MotionEvent;
import android.view.View;

import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import com.db.chart.renderer.XRenderer;
import com.db.chart.renderer.YRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@MediumTest
public class ChartViewTest {

	@Mock
	private XRenderer mMockXRndr;

	@Mock
	private YRenderer mMockYRndr;

	@Mock
	private Context mMockContext;

	@Mock
	private Resources mMockResources;

	@Mock
	private MotionEvent mMockMotionEvent;

	@Mock
	private Region mMockRegion;

	@Mock
	private OnEntryClickListener mMockEntryListener;

	@Mock
	private View.OnClickListener mMockChartListener;

	private LineChartView mChartView;

	private ArrayList<ChartSet> mData;


	@Before
	public void setUp() {

		when(mMockContext.getResources()).thenReturn(mMockResources);
		when(mMockResources.getDimension(0)).thenReturn(6.f);

		mChartView = new LineChartView(mMockContext);
		BarSet set = new BarSet();
		for (int i = 0; i < 2; i++)
			set.addBar(new Bar(Integer.toString(i), (float) i));
		mData = new ArrayList<>();
		mData.add(set);
	}


	@Test(expected = IllegalArgumentException.class)
	public void addData_NullSet_ThrowIllegalArgumentException() {

		BarSet set = null;
		mChartView.addData(set);
	}


	@Test(expected = IllegalArgumentException.class)
	public void addData_LesserEntriesSize_ThrowIllegalArgumentException() {

		mChartView.addData(mData);
		BarSet set1 = new BarSet();
		set1.addBar(new Bar("3", 1.f));
		mChartView.addData(set1);
	}


	@Test
	public void updateValues_Nominal_Result() {

		mChartView.addData(mData);
		mChartView.updateValues(0, new float[] {10, 20});

		assertEquals(10, mChartView.getData().get(0).getEntry(0).getValue(), 0.f);
		assertEquals(20, mChartView.getData().get(0).getEntry(1).getValue(), 0.f);
	}


	@Test(expected = IllegalArgumentException.class)
	public void updateValues_LesserValues_ThrowIllegalArgumentException() {

		mChartView.addData(mData);
		mChartView.updateValues(0, new float[] {10});
	}


	@Test
	public void negotiateInnerChartBounds_Nominal_InnerB() {

		assertArrayEquals(new float[] {2, 2, 11, 11},
				  mChartView.negotiateInnerChartBounds(new float[] {1, 1, 12, 12},
							 new float[] {2, 2, 11, 11}), 0.f);
	}


	@Test
	public void onTouchEvent_EntryClick_EntryListenerAndTooltipToggle() {

		mChartView = spy(new LineChartView(mMockContext));
		doReturn(new Rect(0, 0, 0, 0)).when(mChartView).getEntryRect(any(Region.class));
		when(mMockMotionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN)
				  .thenReturn(MotionEvent.ACTION_UP);
		when(mMockMotionEvent.getX()).thenReturn(5.f);
		when(mMockMotionEvent.getY()).thenReturn(5.f);
		when(mMockRegion.contains(5, 5)).thenReturn(true);
		when(mMockRegion.getBounds()).thenReturn(new Rect(0, 0, 0, 0));

		ArrayList<ArrayList<Region>> regions_sets = new ArrayList();
		ArrayList<Region> regions_entries = new ArrayList();
		regions_entries.add(mMockRegion);
		regions_sets.add(regions_entries);
		mChartView.setOnEntryClickListener(mMockEntryListener);
		mChartView.setOnClickListener(mMockChartListener);
		mChartView.setClickableRegions(regions_sets);

		mChartView.onTouchEvent(mMockMotionEvent);
		verify(mMockEntryListener, times(0)).onClick(anyInt(), anyInt(), any(Rect.class));

		mChartView.onTouchEvent(mMockMotionEvent);
		verify(mMockEntryListener, times(1)).onClick(anyInt(), anyInt(), any(Rect.class));

		mChartView.onTouchEvent(mMockMotionEvent);
		verify(mMockEntryListener, times(1)).onClick(anyInt(), anyInt(), any(Rect.class));
		verify(mMockChartListener, times(1)).onClick(mChartView);
	}


	@Test(expected = IllegalArgumentException.class)
	public void setGrid_RowsLesserOne_ThrowIllegalArgException() {

		mChartView.setGrid(ChartView.GridType.NONE, 0, 0, null);
	}
}
