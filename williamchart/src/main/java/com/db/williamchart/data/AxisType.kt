package com.db.williamchart.data

enum class AxisType {
    NONE, // No axis
    X, // Only axis X
    Y, // Only axis Y
    XY // All axis
}

fun AxisType.shouldDisplayAxisX(): Boolean {
    return this == AxisType.XY || this == AxisType.X
}

fun AxisType.shouldDisplayAxisY(): Boolean {
    return this == AxisType.XY || this == AxisType.Y
}
