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
public class SandboxStackTest {

	@Rule
	public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

	private int mCurrTab;


	@Before
	public void setUp() {

		mCurrTab = 0;
		onView(withId(R.id.spinner)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("sandbox"))).perform(click());
		onView(withId(R.id.sandbox_chart_stacked)).perform(click());
	}


	@Test
	public void defaultStackChart() {

		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void roundCornersBar() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_bar_corner3)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void spacingBar() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_bar_spacing4)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void backgroundBar() {

		swipeLeftTo(3);
		onView(withId(R.id.sandbox_bar_background)).perform(click());
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