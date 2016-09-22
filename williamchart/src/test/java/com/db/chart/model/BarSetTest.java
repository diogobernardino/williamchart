package com.db.chart.model;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;


@RunWith(JUnit4.class)
@SmallTest
public class BarSetTest {

	BarSet set;

	Bar bar;


	@Before
	public void setUp() {

		set = new BarSet();
		for (int i = 0; i < 3; i++) {
			bar = new Bar("test" + Integer.toString(i), (float) i);
			set.addBar(bar);
		}
	}


	/**
	 * No null point argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void pointNullException() {

		set.addBar(null);
	}


	/**
	 * Test values update.
	 */
	@Test
	public void updateValues() {

		set.updateValues(new float[] {4f, 5f, 6f});
		assertEquals(4f, set.getValue(0), 0f);
		assertEquals(5f, set.getValue(1), 0f);
		assertEquals(6f, set.getValue(2), 0f);
	}


	/**
	 * No different size when updating values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateValuesSizeException() {

		set.updateValues(new float[] {1f, 2f});
	}


	/**
	 * Gradient colors can't be null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void barGradientNullException() {

		bar.setGradientColor(null, new float[] {1.f, 2.f});
	}

}
