package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertTrue
import org.junit.Test

class LineChartRendererTest {

    private val view: ChartContract.View = mock()
    private val painter: Painter = mock()
    private val animation: ChartAnimation = mock()

    private val lineChartRenderer by lazy {
        LineChartRenderer(
            view = view,
            painter = painter,
            animation = animation
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `at least 2 datapoints are needed to display chart`() {
        // Arrange
        val emptyData = linkedMapOf<String, Float>()

        // Act
        lineChartRenderer.render(emptyData)
        lineChartRenderer.preDraw(
            width = 0,
            height = 0,
            paddingBottom = 0,
            paddingLeft = 0,
            paddingRight = 0,
            paddingTop = 0,
            axis = AxisType.NONE,
            labelsSize = 0f
        )
    }

    @Test
    fun `predraw must execute only once`() {
        // Arrange
        val data = linkedMapOf(
            "this" to 999f,
            "that" to 111f
        )

        // Act
        lineChartRenderer.render(data)
        lineChartRenderer.preDraw(
            width = 0,
            height = 0,
            paddingBottom = 0,
            paddingLeft = 0,
            paddingRight = 0,
            paddingTop = 0,
            axis = AxisType.NONE,
            labelsSize = 0f
        )
        val wasPreparedAlready = lineChartRenderer.preDraw(
            width = 0,
            height = 0,
            paddingBottom = 0,
            paddingLeft = 0,
            paddingRight = 0,
            paddingTop = 0,
            axis = AxisType.NONE,
            labelsSize = 0f
        )

        // Assert
        assertTrue(wasPreparedAlready)
    }
}