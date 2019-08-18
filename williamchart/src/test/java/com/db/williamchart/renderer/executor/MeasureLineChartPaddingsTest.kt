package com.db.williamchart.renderer.executor

import com.db.williamchart.data.AxisType
import org.junit.Assert.assertEquals
import org.junit.Test

class MeasureLineChartPaddingsTest {

    private val measureLineChartPaddings by lazy {
        MeasureLineChartPaddings()
    }

    @Test
    fun `no paddings when no axis is displayed at all`() {
        // Act
        val paddings = measureLineChartPaddings.invoke(
            axisType = AxisType.NONE,
            labelsHeight = 1f,
            longestLabelWidth = 1f,
            labelsPaddingToInnerChart = 1f,
            lineThickness = 0f
        )

        // Assert
        assertEquals(0f, paddings.left)
        assertEquals(0f, paddings.top)
        assertEquals(0f, paddings.right)
        assertEquals(0f, paddings.bottom)
    }

    @Test
    fun `paddings take into account line thickness`() {
        // Arrange
        val lineThickness = 999f

        // Act
        val paddings = measureLineChartPaddings.invoke(
            axisType = AxisType.XY,
            labelsHeight = 0f,
            longestLabelWidth = 0f,
            labelsPaddingToInnerChart = 0f,
            lineThickness = lineThickness
        )

        // Assert
        assertEquals(0f, paddings.left)
        assertEquals(lineThickness, paddings.top)
        assertEquals(0f, paddings.right)
        assertEquals(lineThickness, paddings.bottom)
    }

    @Test
    fun `paddings take into account label height`() {
        // Arrange
        val labelsHeight = 999f
        val halfLabelsHeight = labelsHeight / 2

        // Act
        val paddings = measureLineChartPaddings.invoke(
            axisType = AxisType.XY,
            labelsHeight = labelsHeight,
            longestLabelWidth = 0f,
            labelsPaddingToInnerChart = 0f,
            lineThickness = 0f
        )

        // Assert
        assertEquals(0f, paddings.left)
        assertEquals(halfLabelsHeight, paddings.top)
        assertEquals(0f, paddings.right)
        assertEquals(labelsHeight, paddings.bottom)
    }
}