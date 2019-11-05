package com.db.williamchart.renderer.executor

import com.db.williamchart.data.AxisType
import org.junit.Assert
import org.junit.Test

class MeasureBarChartPaddingsTest {

    private val measureBarChartPaddings by lazy {
        MeasureBarChartPaddings()
    }

    @Test
    fun `no paddings when no axis is displayed at all`() {
        // Act
        val paddings = measureBarChartPaddings.invoke(
            axisType = AxisType.NONE,
            labelsHeight = 1f,
            longestLabelWidth = 1f,
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
        val halfLabelsHeight = labelsHeight / 2

        // Act
        val paddings = measureBarChartPaddings.invoke(
            axisType = AxisType.XY,
            labelsHeight = labelsHeight,
            longestLabelWidth = 0f,
            labelsPaddingToInnerChart = 0f
        )

        // Assert
        Assert.assertEquals(0f, paddings.left)
        Assert.assertEquals(halfLabelsHeight, paddings.top)
        Assert.assertEquals(0f, paddings.right)
        Assert.assertEquals(labelsHeight, paddings.bottom)
    }
}
