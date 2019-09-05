package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.LineChartConfiguration
import com.db.williamchart.data.Paddings
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertTrue
import org.junit.Test

class LineChartRendererTest {

    private val view: ChartContract.LineView = mock()
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
        val chartConfiguration = LineChartConfiguration(
            width = 0,
            height = 0,
            paddings = Paddings(0f, 0f, 0f, 0f),
            axis = AxisType.NONE,
            labelsSize = 0f,
            lineThickness = 0f,
            pointsDrawableWidth = 0,
            pointsDrawableHeight = 0,
            fillColor = 0,
            gradientFillColors = intArrayOf()
        )

        // Act
        lineChartRenderer.render(emptyData)
        lineChartRenderer.preDraw(chartConfiguration)
    }

    @Test
    fun `predraw must execute only once`() {
        // Arrange
        val data = linkedMapOf(
            "this" to 999f,
            "that" to 111f
        )
        val chartConfiguration = LineChartConfiguration(
            width = 0,
            height = 0,
            paddings = Paddings(0f, 0f, 0f, 0f),
            axis = AxisType.NONE,
            labelsSize = 0f,
            lineThickness = 0f,
            pointsDrawableWidth = 0,
            pointsDrawableHeight = 0,
            fillColor = 0,
            gradientFillColors = intArrayOf()
        )

        // Act
        lineChartRenderer.render(data)
        lineChartRenderer.preDraw(chartConfiguration)
        val wasPreparedAlready = lineChartRenderer.preDraw(chartConfiguration)

        // Assert
        assertTrue(wasPreparedAlready)
    }
}