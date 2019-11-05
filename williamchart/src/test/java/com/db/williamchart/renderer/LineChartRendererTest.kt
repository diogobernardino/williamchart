package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.LineChartConfiguration
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.Scale
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class LineChartRendererTest {

    private val view: ChartContract.LineView = mock()
    private val painter: Painter = mock()
    private val animation: ChartAnimation<DataPoint> = mock()

    private val lineChartRenderer by lazy {
        LineChartRenderer(
            view = view,
            painter = painter,
            animation = animation
        )
    }

    @Test
    fun `draw line background`() {
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
            fillColor = 20705,
            gradientFillColors = intArrayOf(),
            scale = Scale(0f, 0f)
        )

        // Act
        lineChartRenderer.render(data)
        lineChartRenderer.preDraw(chartConfiguration)
        lineChartRenderer.draw()

        // Assert
        verify(view).drawLineBackground(any(), any())
    }
}
