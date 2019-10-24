package com.db.williamchart.data

data class BarChartConfiguration(
    override val width: Int,
    override val height: Int,
    override val paddings: Paddings,
    override val axis: AxisType,
    override val labelsSize: Float,
    val barsBackgroundColor: Int,
    val xScaleLabel: (String) -> String = { it },
    val yScaleLabel: (Float) -> String = { it.toString() }
) : ChartConfiguration(
    width = width,
    height = height,
    paddings = paddings,
    axis = axis,
    labelsSize = labelsSize
)
