package com.db.williamchart.data

data class DonutConfiguration(
    override val width: Int,
    override val height: Int,
    override val paddings: Paddings,
    override val axis: AxisType,
    override val labelsSize: Float
) : ChartConfiguration(width, height, paddings, axis, labelsSize)
