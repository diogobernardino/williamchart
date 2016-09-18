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
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class SandboxLineTest {

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
	public void defaultLineChart() {

		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void smoothLine() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_line_smooth)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void dashedLine() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_line_dashed)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void thicknessLine() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_line_thickness2)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void pointsLine() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_point_size2)).perform(click());
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