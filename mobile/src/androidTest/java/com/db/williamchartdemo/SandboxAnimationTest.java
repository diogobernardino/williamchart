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
public class SandboxAnimationTest {

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
	public void elasticEasingAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_easing_type)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("Bounce"))).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void durationAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_duration)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("1500ms"))).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void alphaAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_alpha)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void orderFirstAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_orderf)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void orderLastAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_orderl)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void orderCenterAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_orderm)).perform(click());
		swipeLeftTo(5);
		onView(withId(R.id.sandbox_play_play)).perform(click());
	}


	@Test
	public void enterTopLeftAnimation() {

		swipeLeftTo(4);
		onView(withId(R.id.sandbox_anim_entertl)).perform(click());
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