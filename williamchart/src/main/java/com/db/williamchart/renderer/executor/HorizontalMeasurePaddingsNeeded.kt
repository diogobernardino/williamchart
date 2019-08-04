package com.db.williamchart.renderer.executor

import com.db.williamchart.data.AxisType
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.mergeWith
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY

class HorizontalMeasurePaddingsNeeded {

    operator fun invoke(
        axisType: AxisType,
        labelsHeight: Float,
        xLastLabelWidth: Float,
        yLongestLabelWidth: Float,
        yLabelsPadding: Float
    ): Paddings {

        return measurePaddingsX(axisType, labelsHeight, xLastLabelWidth)
            .mergeWith(
                measurePaddingsY(axisType, labelsHeight, yLongestLabelWidth, yLabelsPadding)
            )
    }

    private fun measurePaddingsX(
        axisType: AxisType,
        labelsHeight: Float,
        xLastLabelWidth: Float
    ): Paddings {

        return Paddings(
            left = 0F,
            top = 0F,
            right = xLastLabelWidth / 2,
            bottom = if (axisType.shouldDisplayAxisX()) labelsHeight else 0F
        )
    }

    private fun measurePaddingsY(
        axisType: AxisType,
        labelsHeight: Float,
        yLongestLabelWidth: Float,
        yLabelsPadding: Float
    ): Paddings {

        if (!axisType.shouldDisplayAxisY())
            return Paddings(0F, 0F, 0F, 0F)

        return Paddings(
            left = yLongestLabelWidth + yLabelsPadding,
            top = 0f,
            right = 0F,
            bottom = labelsHeight / 2
        )
    }
}