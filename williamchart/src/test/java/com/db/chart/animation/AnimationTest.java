package com.db.chart.animation;

import android.graphics.Rect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class AnimationTest {

    private Animation mAnimation;


    @Before
    public void setUp() {

        mAnimation = new Animation();
    }


    @Test
    public void startingPoint_BottomLeft() {

        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{{0f, 1f}});

        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues =
                mAnimation.applyStartingPosition(values, area, 0f, 0f);

        assertEquals(0, newValues.get(0)[0][0], 0f);
        assertEquals(10, newValues.get(0)[0][1], 0f);
    }


    @Test
    public void startingPoint_TopLeft() {

        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{{0f, 1f}});

        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues =
                mAnimation.applyStartingPosition(values, area, 0f, 1f);

        assertEquals(0, newValues.get(0)[0][0], 0f);
        assertEquals(0, newValues.get(0)[0][1], 0f);
    }


    @Test
    public void startingPoint_BottomRight() {

        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{{0f, 1f}});

        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues =
                mAnimation.applyStartingPosition(values, area, 1f, 0f);

        assertEquals(10, newValues.get(0)[0][0], 0f);
        assertEquals(10, newValues.get(0)[0][1], 0f);
    }


    @Test
    public void startingPoint_TopRight() {

        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{{0f, 1f}});

        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues =
                mAnimation.applyStartingPosition(values, area, 1f, 1f);

        assertEquals(10, newValues.get(0)[0][0], 0f);
        assertEquals(0, newValues.get(0)[0][1], 0f);
    }


    @Test
    public void calculateEntriesInitTime_NoOrder(){

        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, null);
        assertTrue(initTimes[0] < initTimes[1]);
        assertTrue(initTimes[0] < initTimes[2]);
        assertTrue(initTimes[1] < initTimes[2]);
    }


    @Test
    public void calculateEntriesInitTime_BackwardsOrder(){

        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, new int[]{2,1,0});
        assertTrue(initTimes[2] < initTimes[0]);
        assertTrue(initTimes[2] < initTimes[1]);
        assertTrue(initTimes[1] < initTimes[0]);
    }


    @Test
    public void calculateEntriesInitTime_MiddleOrder(){

        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, new int[]{1,0,2});
        assertTrue(initTimes[1] < initTimes[0]);
        assertTrue(initTimes[1] < initTimes[2]);
        assertTrue(initTimes[0] < initTimes[2]);
    }


    @Test
    public void calculateEntriesInitTime_NoOverlap(){

        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, null);
        assertEquals(0, initTimes[0]);
        assertEquals(30, initTimes[1]);
        assertEquals(60, initTimes[2]);
    }


    @Test
    public void calculateEntriesInitTime_FullOverlap(){

        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 1, null);
        assertEquals(0, initTimes[0]);
        assertTrue(initTimes[1] == initTimes[0]);
        assertTrue(initTimes[1] == initTimes[2]);
    }


    @Test
    public void calculateEntriesInitTime_HalfOverlap(){

        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, .5f, null);
        assertEquals(0, initTimes[0]);
        assertEquals(23, initTimes[1]);
        assertEquals(45, initTimes[2]);
    }


    @Test
    public void calculateEntriesDuration_NoOverlap(){

        assertEquals(30, mAnimation.calculateEntriesDuration(3, 90, 0));
    }


    @Test
    public void calculateEntriesDuration_FullOverlap(){

        assertEquals(90, mAnimation.calculateEntriesDuration(3, 90, 1));
    }


    @Test
    public void calculateEntriesDuration_HalfOverlap(){

        assertEquals(60, mAnimation.calculateEntriesDuration(3, 90, .5f));
    }

}
