package com.db.williamchart.renderer

import com.db.williamchart.data.AxisType
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.mergeWith
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY

class MeasurePaddingsNeeded {

    operator fun invoke(axisType: AxisType, labelsHeight: Float, longestLabelWidth: Float): Paddings {
        return measurePaddingsX(axisType, labelsHeight)
            .mergeWith(measurePaddingsY(axisType, labelsHeight, longestLabelWidth))
    }

    private fun measurePaddingsX(axisType: AxisType, labelsHeight: Float): Paddings {
        return Paddings(
            left = 0F,
            top = 0F,
            right = 0f,
            bottom = if (axisType.shouldDisplayAxisX()) labelsHeight else 0F
        )
    }

    private fun measurePaddingsY(axisType: AxisType, labelsHeight: Float, longestLabelWidth: Float): Paddings {

        if (!axisType.shouldDisplayAxisY())
            return Paddings(0F, 0F, 0F, 0F)

        return Paddings(
            left = longestLabelWidth,
            top = labelsHeight / 2,
            right = 0F,
            bottom = labelsHeight / 2
        )
    }
}