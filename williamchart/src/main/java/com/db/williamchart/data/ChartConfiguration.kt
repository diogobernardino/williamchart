package com.db.williamchart.data

data class ChartConfiguration(
    val width: Int,
    val height: Int,
    val paddingsPaddings: Paddings,
    val axis: AxisType,
    val labelsSize: Float
)

internal fun ChartConfiguration.toOuterFrame(): Frame {
    return Frame(
        left = paddingsPaddings.left,
        top = paddingsPaddings.top,
        right = width - paddingsPaddings.right,
        bottom = height - paddingsPaddings.bottom
    )
}