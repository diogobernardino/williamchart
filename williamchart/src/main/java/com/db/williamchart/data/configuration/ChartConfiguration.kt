package com.db.williamchart.data.configuration

import com.db.williamchart.data.AxisType
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.Scale

open class ChartConfiguration(
    open val width: Int,
    open val height: Int,
    open val paddings: Paddings,
    open val axis: AxisType,
    open val labelsSize: Float,
    open val scale: Scale,
    open val labelsFormatter: (Float) -> String
)

internal fun ChartConfiguration.toOuterFrame(): Frame {
    return Frame(
        left = paddings.left,
        top = paddings.top,
        right = width - paddings.right,
        bottom = height - paddings.bottom
    )
}
