package com.db.williamchart.renderer.executor

import com.db.williamchart.data.AxisType
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.mergeWith
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY

class MeasureHorizontalBarChartPaddings {

    operator fun invoke(
        axisType: AxisType,
        labelsHeight: Float,
        xLastLabelWidth: Float,
        yLongestLabelWidth: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {

        return measurePaddingsX(axisType, labelsHeight, xLastLabelWidth)
            .mergeWith(
                measurePaddingsY(axisType, yLongestLabelWidth, labelsPaddingToInnerChart)
            )
    }

    private fun measurePaddingsX(
        axisType: AxisType,
        labelsHeight: Float,
        xLastLabelWidth: Float
    ): Paddings {

        if (!axisType.shouldDisplayAxisX())
            return Paddings(0F, 0F, 0F, 0F)

        return Paddings(
            left = 0F,
            top = 0F,
            right = xLastLabelWidth / 2,
            bottom = labelsHeight
        )
    }

    private fun measurePaddingsY(
        axisType: AxisType,
        yLongestLabelWidth: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {

        if (!axisType.shouldDisplayAxisY())
            return Paddings(0F, 0F, 0F, 0F)

        return Paddings(
            left = yLongestLabelWidth + labelsPaddingToInnerChart,
            top = 0f,
            right = 0F,
            bottom = 0f
        )
    }
}