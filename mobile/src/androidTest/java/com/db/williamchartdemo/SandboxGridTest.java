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
public class SandboxGridTest {

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
	public void horizontalGrid() {

		swipeLeftTo(2);
		onView(withId(R.id.sandbox_grid_hor)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void verticalGrid() {

		swipeLeftTo(2);
		onView(withId(R.id.sandbox_grid_ver)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void fullGrid() {

		swipeLeftTo(2);
		onView(withId(R.id.sandbox_grid_full)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void dashedGrid() {

		swipeLeftTo(2);
		onView(withId(R.id.sandbox_grid_full)).perform(click());
		onView(withId(R.id.sandbox_grid_dashed2)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void thicknessGrid() {

		swipeLeftTo(2);
		onView(withId(R.id.sandbox_grid_full)).perform(click());
		onView(withId(R.id.sandbox_grid_thickness2)).perform(click());
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