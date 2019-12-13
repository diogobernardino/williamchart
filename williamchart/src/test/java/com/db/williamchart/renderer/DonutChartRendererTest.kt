package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.DonutDataPoint
import com.db.williamchart.data.Paddings
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class DonutChartRendererTest {

    private val view: ChartContract.DonutView = mock()
    private val animation: ChartAnimation<DonutDataPoint> = mock()

    private val donutChartRenderer by lazy {
        DonutChartRenderer(
            view = view,
            animation = animation
        )
    }

    @Test
    fun `draw background bar`() {
        // Arrange
        val data = listOf(999f)
        val chartConfiguration = DonutChartConfiguration(
            width = 0,
            height = 0,
            paddings = Paddings(0f, 0f, 0f, 0f),
            thickness = 0f,
            total = 0f,
            colorsSize = 1,
            barBackgroundColor = 1
        )

        // Act
        donutChartRenderer.render(data)
        donutChartRenderer.preDraw(chartConfiguration)
        donutChartRenderer.draw()

        // Assert
        verify(view).drawBackground(any())
    }
}
