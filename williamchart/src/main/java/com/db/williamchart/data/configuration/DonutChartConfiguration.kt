package com.db.williamchart.data.configuration

import com.db.williamchart.data.Paddings

data class DonutChartConfiguration(
    val width: Int,
    val height: Int,
    val paddings: Paddings,
    val thickness: Float,
    val total: Float,
    val colorsSize: Int,
    val barBackgroundColor: Int
)
