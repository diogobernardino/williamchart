package com.db.williamchart.renderer.executor

import com.db.williamchart.data.AxisType
import org.junit.Assert
import org.junit.Test

class MeasureHorizontalBarChartPaddingsTest {

    private val measureHorizontalBarChartPaddings by lazy {
        MeasureHorizontalBarChartPaddings()
    }

    @Test
    fun `no paddings when no axis is displayed at all`() {
        // Act
        val paddings = measureHorizontalBarChartPaddings.invoke(
            axisType = AxisType.NONE,
            labelsHeight = 1f,
            xLastLabelWidth = 1f,
            yLongestLabelWidth = 1f,
            labelsPaddingToInnerChart = 1f
        )

        // Assert
        Assert.assertEquals(0f, paddings.left)
        Assert.assertEquals(0f, paddings.top)
        Assert.assertEquals(0f, paddings.right)
        Assert.assertEquals(0f, paddings.bottom)
    }

    @Test
    fun `paddings take into account label height`() {
        // Arrange
        val labelsHeight = 999f

        // Act
        val paddings = measureHorizontalBarChartPaddings.invoke(
            axisType = AxisType.XY,
            labelsHeight = labelsHeight,
            xLastLabelWidth = 0f,
            yLongestLabelWidth = 0f,
            labelsPaddingToInnerChart = 0f
        )

        // Assert
        Assert.assertEquals(0f, paddings.left)
        Assert.assertEquals(0f, paddings.top)
        Assert.assertEquals(0f, paddings.right)
        Assert.assertEquals(labelsHeight, paddings.bottom)
    }

    @Test
    fun `consider last label width placed at the end of the scale`() {
        // Arrange
        val xLastLabelWidth = 999f
        val xLastLabelHalfWidth = xLastLabelWidth / 2

        // Act
        val paddings = measureHorizontalBarChartPaddings.invoke(
            axisType = AxisType.XY,
            labelsHeight = 0f,
            xLastLabelWidth = xLastLabelWidth,
            yLongestLabelWidth = 0f,
            labelsPaddingToInnerChart = 0f

        )

        // Assert
        Assert.assertEquals(0f, paddings.left)
        Assert.assertEquals(0f, paddings.top)
        Assert.assertEquals(xLastLabelHalfWidth, paddings.right)
        Assert.assertEquals(0f, paddings.bottom)
    }
}
