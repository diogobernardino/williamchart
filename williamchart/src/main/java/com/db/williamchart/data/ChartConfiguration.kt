package com.db.williamchart.data

open class ChartConfiguration(
    open val width: Int,
    open val height: Int,
    open val paddings: Paddings,
    open val axis: AxisType,
    open val labelsSize: Float,
    open val xScaleLabel: (String) -> String,
    open val yScaleLabel: (Float) -> String
)

internal fun ChartConfiguration.toOuterFrame(): Frame {
    return Frame(
        left = paddings.left,
        top = paddings.top,
        right = width - paddings.right,
        bottom = height - paddings.bottom
    )
}