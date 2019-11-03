package com.db.williamchart.data

data class BarChartConfiguration(
    override val width: Int,
    override val height: Int,
    override val paddings: Paddings,
    override val axis: AxisType,
    override val labelsSize: Float,
    override val scale: Scale?,
    override val yScaleLabel: (Float) -> String = { it.toString() },
    val barsBackgroundColor: Int
) : ChartConfiguration(
    width = width,
    height = height,
    paddings = paddings,
    axis = axis,
    labelsSize = labelsSize,
    scale = scale,
    yScaleLabel = yScaleLabel
)
