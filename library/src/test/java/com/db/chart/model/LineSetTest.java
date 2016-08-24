package com.db.chart.model;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
@SmallTest
public class LineSetTest {


	LineSet set;

	Point point;


	@Before
	public void setUp() {

		set = new LineSet();
		for (int i = 0; i < 3; i++) {
			point = new Point("test" + Integer.toString(i), (float) i);
			set.addPoint(point);
		}
	}


	/**
	 * No null point argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void pointNullException() {

		set.addPoint(null);
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
	 * Test color customization.
	 * Line's fill color assigned to line's color when not previously defined.
	 */
	@Test
	public void colorCustomization() {

		set.setFill(1);
		assertTrue(set.hasFill());
		assertEquals(1, set.getColor());
		assertEquals(1, set.getFillColor());

		set.setColor(3);
		set.setFill(2);
		assertEquals(3, set.getColor());

		set.setGradientFill(new int[] {1, 2}, new float[] {1.f, 2.f});
		assertTrue(set.hasGradientFill());
	}


	/**
	 * Test gradient color customization.
	 * Line's gradient fill color assigned to line's color when not previously defined.
	 */
	@Test
	public void colorGradientCustomization() {

		set.setGradientFill(new int[] {1, 2}, new float[] {1.f, 2.f});
		assertTrue(set.hasGradientFill());
		assertEquals(new int[] {1, 2}[0], set.getColor());
		assertArrayEquals(new int[] {1, 2}, set.getGradientColors());

		set.setColor(3);
		set.setGradientFill(new int[] {1, 2}, new float[] {1.f, 2.f});
		assertEquals(3, set.getColor());
	}


	/**
	 * No negative thickness.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void thicknessNegativeException() {

		set.setThickness(-1);
	}


	/**
	 * No negative dots stroke thickness.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void thicknessDotsNegativeException() {

		set.setDotsStrokeThickness(-1);
	}


	/**
	 * No negative dots radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void radiusDotsNegativeException() {

		set.setDotsRadius(-1);
	}


	/**
	 * Non null argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void dashedNullException() {

		set.setDashed(null);
	}


	/**
	 * End point should be equal to set's size until a different value is defined.
	 */
	@Test
	public void endBorder() {

		assertEquals(set.size(), set.getEnd());
		set.endAt(1);
		assertEquals(1, set.getEnd());
	}


	/**
	 * No negative index assigned to begin.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void beginNegativeException() {

		set.beginAt(-1);
	}


	/**
	 * No index greater than set's size assigned to begin.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void beginGreaterException() {

		set.beginAt(set.size() + 1);
	}


	/**
	 * No negative index assigned to end.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void endNegativeException() {

		set.endAt(-1);
	}


	/**
	 * No index greater than set's size assigned to end.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void endGreaterException() {

		set.endAt(set.size() + 1);
	}


	/**
	 * No index lesser than begin assigned to end.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void endLesserException() {

		set.beginAt(2);
		set.endAt(1);
	}


	/**
	 * No null dots drawable argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void drawableNullException() {

		set.setDotsDrawable(null);
	}


	/**
	 * No null drawable argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void drawableDotNullException() {

		point.setDrawable(null);
	}


	/**
	 * No negative dot stroke thickness.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void thicknessDotNegativeException() {

		point.setStrokeThickness(-1);
	}


	/**
	 * No negative dots radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void radiusDotNegativeException() {

		point.setRadius(-1);
	}

}
