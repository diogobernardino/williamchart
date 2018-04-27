package com.db.williamchart

import com.db.williamchart.data.Line
import com.db.williamchart.data.Point
import com.db.williamchart.renderer.ChartRenderer

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class ChartRendererTest {

    @Mock private lateinit var painter: Painter

    private lateinit var renderer: ChartRenderer

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        renderer = ChartRenderer(painter)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addDataWithOneEntry_ThrowIlegalArgument() {

        val set = Line()
        set.add(Point("label", 1f))

        renderer.data = set

        renderer.preDraw(0, 0, 0, 0, 0, 0)
    }

    @Test
    fun noData_DrawRetrievesNull() {
        assertEquals(null, renderer.data)
    }

    @Test
    fun addData_RetrievesProperData() {

        val set = Line()
        set.add(Point("label0", 0f))
        set.add(Point("label1", 1f))

        renderer.data = set

        renderer.preDraw(0, 1, 0, 0, 0, 0)

        val result = renderer.data
        assertEquals(1F, result!!.entries[0].y)
        assertEquals(0F, result.entries[1].y)
    }

}