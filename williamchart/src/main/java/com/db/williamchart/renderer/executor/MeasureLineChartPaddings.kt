package com.db.williamchart.renderer.executor

import com.db.williamchart.data.AxisType
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.mergeWith
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY

class MeasureLineChartPaddings {

    operator fun invoke(
        axisType: AxisType,
        labelsHeight: Float,
        longestLabelWidth: Float,
        labelsPaddingToInnerChart: Float,
        lineThickness: Float
    ): Paddings {
        val paddings = measurePaddingsX(axisType, labelsHeight)
            .mergeWith(
                measurePaddingsY(axisType, labelsHeight, longestLabelWidth, labelsPaddingToInnerChart)
            )
        return paddings.copy(
            top = paddings.top + lineThickness,
            bottom = paddings.bottom + lineThickness
        )
    }

    private fun measurePaddingsX(axisType: AxisType, labelsHeight: Float): Paddings {
        return Paddings(
            left = 0F,
            top = 0f,
            right = 0f,
            bottom = if (axisType.shouldDisplayAxisX()) labelsHeight else 0F
        )
    }

    private fun measurePaddingsY(
        axisType: AxisType,
        labelsHeight: Float,
        longestLabelWidth: Float,
        labelsPaddingToInnerChart: Float
    ): Paddings {

        if (!axisType.shouldDisplayAxisY())
            return Paddings(0F, 0f, 0F, 0F)

        return Paddings(
            left = longestLabelWidth + labelsPaddingToInnerChart,
            top = labelsHeight / 2,
            right = 0F,
            bottom = labelsHeight / 2
        )
    }
}