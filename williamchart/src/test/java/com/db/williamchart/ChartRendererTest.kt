package com.db.williamchart

import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import com.db.williamchart.data.Line
import com.db.williamchart.data.Point
import com.db.williamchart.renderer.ChartRenderer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class ChartRendererTest {

    @Mock private lateinit var view: ChartContract.View

    @Mock private lateinit var painter: Painter

    @Captor private lateinit var setCaptor: ArgumentCaptor<ChartSet>

    @Captor private lateinit var labelsCaptor: ArgumentCaptor<List<ChartLabel>>

    private lateinit var renderer: ChartRenderer

    @Before fun setup() {

        MockitoAnnotations.initMocks(this)
        renderer = ChartRenderer(view, painter, NoAnimation())
    }

    @Test(expected = IllegalArgumentException::class)
    fun addDataWithOneEntry_ThrowIlegalArgument() {

        val set = Line()
        set.add(Point("label", 1f))

        renderer.add(set)
        renderer.preDraw(0, 0, 0, 0, 0, 0)
    }

    @Test
    fun noData_SkipPreDraw() {

        renderer.draw()

        verify(view, times(0)).drawData(any(), any(), any(), any(), any())
    }

    @Test
    fun addData_RetrievesProperData() {

        val set = Line()
        set.add(Point("label0", 0f))
        set.add(Point("label1", 1f))

        renderer.add(set)
        renderer.preDraw(0, 1, 0, 0, 0, 0)
        renderer.draw()

        verify(view).drawData(any(), any(), any(), any(), capture(setCaptor))
        assertEquals(1F, setCaptor.value.entries[0].y)
        assertEquals(0F, setCaptor.value.entries[1].y)
    }

    @Test
    fun chartWithLabels_LabelsDisplayed() {

        val set = Line()
        set.add(Point("label0", 0f))
        set.add(Point("label1", 1f))

        renderer.add(set)
        renderer.draw()

        verify(view, times(2)).drawLabels(any())
    }

    @Test
    fun chartWithLabels_XLabelsCorrectlyDisposed() {

        val set = Line()
        set.add(Point("label0", 0f))
        set.add(Point("label1", 1f))

        renderer.add(set)
        renderer.preDraw(1, 1, 0, 0, 0, 0)
        renderer.draw()

        verify(view, times(2)).drawLabels(capture(labelsCaptor))

        val labels = labelsCaptor.allValues[0]
        for (i in 0..set.entries.size - 2)
            assertTrue(labels[i].x < labels[i+1].x)
    }

    @Test
    fun chartWithLabels_YLabelsCorrectlyDisposed() {

        val set = Line()
        set.add(Point("label0", 0f))
        set.add(Point("label1", 1f))

        renderer.add(set)
        renderer.preDraw(1, 1, 0, 0, 0, 0)
        renderer.draw()

        verify(view, times(2)).drawLabels(capture(labelsCaptor))

        val labels = labelsCaptor.allValues[1]
        for (i in 0..set.entries.size - 2)
            assertTrue(labels[i].y > labels[i+1].y)
    }

    @Test
    fun noLabels_LabelsNotDisplayed() {

        val set = Line()
        set.add(Point("label0", 0f))
        set.add(Point("label1", 1f))

        renderer.add(set)
        renderer.hasLabels = false
        renderer.draw()

        verify(view, times(0)).drawLabels(any())
    }

}

/**
 * Returns ArgumentCaptor.capture() as nullable type to avoid java.lang.IllegalStateException
 * when null is returned.
 */
fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
