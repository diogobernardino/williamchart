package com.db.williamchart

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

    private lateinit var renderer: ChartRenderer

    @Before fun setup() {

        MockitoAnnotations.initMocks(this)
        renderer = ChartRenderer(view, painter)
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

}

/**
 * Returns ArgumentCaptor.capture() as nullable type to avoid java.lang.IllegalStateException
 * when null is returned.
 */
fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
