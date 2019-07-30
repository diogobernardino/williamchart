package com.db.williamchart.extensions

import com.db.williamchart.data.DataPoint

fun List<Float>._limits(): Pair<Float, Float> {

    val min = min() ?: 0F
    var max = max() ?: 1F

    if (min == max)
        max += 1F

    return Pair(min, max)
}

fun List<DataPoint>.limits(): Pair<Float, Float> {

    if (isEmpty())
        Pair(0F, 1F)

    val values = map { it.value }
    return values._limits()
}