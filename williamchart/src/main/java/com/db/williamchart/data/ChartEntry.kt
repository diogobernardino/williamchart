package com.db.williamchart.data

data class ChartEntry(
    val label: String,
    val value: Float
) {
    var x: Float = 0f
    var y: Float = 0f
}