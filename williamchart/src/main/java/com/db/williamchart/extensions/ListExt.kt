package com.db.williamchart.extensions

import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Label
import com.db.williamchart.data.Scale

fun List<DataPoint>.limits(): Pair<Float, Float> {

    if (isEmpty())
        Pair(0F, 1F)

    val values = map { it.value }
    return values.floatLimits()
}

fun List<DataPoint>.toScale(): Scale {
    val limits = limits()
    return Scale(min = limits.first, max = limits.second)
}

fun List<DataPoint>.toLabels(): List<Label> {
    return map {
        Label(
            label = it.label,
            screenPositionX = 0f,
            screenPositionY = 0f
        )
    }
}

private fun List<Float>.floatLimits(): Pair<Float, Float> {

    val min = min() ?: 0F
    var max = max() ?: 1F

    if (min == max)
        max += 1F

    return Pair(min, max)
}