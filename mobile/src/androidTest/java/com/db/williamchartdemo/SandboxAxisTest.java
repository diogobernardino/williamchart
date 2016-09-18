package com.db.williamchartdemo;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SandboxAxisTest {

	@Rule
	public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

	private int mCurrTab;


	@Before
	public void setUp() {

		mCurrTab = 0;
		onView(withId(R.id.spinner)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("sandbox"))).perform(click());
	}


	@Test
	public void noYLabels() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_y_outside)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void insideYLabels() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_y_inside)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void noYAxis() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_y)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void noYAxisNoYLabels() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_y)).perform(click());
		onView(withId(R.id.sandbox_axis_y_outside)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void noXLabels() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_x_outside)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void insideXLabels() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_x_inside)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void noXAxis() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_x)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void noXAxisNoXLabels() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_x)).perform(click());
		onView(withId(R.id.sandbox_axis_x_outside)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void labelsWithMetric() {

		swipeLeftTo(1);
		onView(withId(R.id.sandbox_axis_label_format_value)).perform(replaceText("metric"));
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	private void swipeLeftTo(int goal) {

		for (int i = mCurrTab; i < goal; i++) {
			onView(withId(R.id.pager)).perform(swipeLeft());
			SystemClock.sleep(300);
		}
		mCurrTab = goal;
		SystemClock.sleep(300);
	}
}