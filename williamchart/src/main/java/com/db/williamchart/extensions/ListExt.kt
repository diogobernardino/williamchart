package com.db.williamchart.extensions

import android.graphics.Path
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.DonutDataPoint
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

fun List<DataPoint>.toLabels(label: (String) -> String = { it }): List<Label> {
    return map {
        Label(
            label = label(it.label),
            screenPositionX = 0f,
            screenPositionY = 0f
        )
    }
}

fun List<DataPoint>.toLinePath(): Path {
    val res = Path()

    res.moveTo(this.first().screenPositionX, this.first().screenPositionY)
    for (i in 1 until this.size)
        res.lineTo(this[i].screenPositionX, this[i].screenPositionY)
    return res
}

/**
 * Credits: http://www.jayway.com/author/andersericsson/
 */
fun List<DataPoint>.toSmoothLinePath(smoothFactor: Float): Path {

    var thisPointX: Float
    var thisPointY: Float
    var nextPointX: Float
    var nextPointY: Float
    var startDiffX: Float
    var startDiffY: Float
    var endDiffX: Float
    var endDiffY: Float
    var firstControlX: Float
    var firstControlY: Float
    var secondControlX: Float
    var secondControlY: Float

    val res = Path()
    res.moveTo(this.first().screenPositionX, this.first().screenPositionY)

    for (i in 0 until this.size - 1) {

        thisPointX = this[i].screenPositionX
        thisPointY = this[i].screenPositionY

        nextPointX = this[i + 1].screenPositionX
        nextPointY = this[i + 1].screenPositionY

        startDiffX = nextPointX - this[si(this.size, i - 1)].screenPositionX
        startDiffY = nextPointY - this[si(this.size, i - 1)].screenPositionY

        endDiffX = this[si(this.size, i + 2)].screenPositionX - thisPointX
        endDiffY = this[si(this.size, i + 2)].screenPositionY - thisPointY

        firstControlX = thisPointX + smoothFactor * startDiffX
        firstControlY = thisPointY + smoothFactor * startDiffY

        secondControlX = nextPointX - smoothFactor * endDiffX
        secondControlY = nextPointY - smoothFactor * endDiffY

        res.cubicTo(
            firstControlX,
            firstControlY,
            secondControlX,
            secondControlY,
            nextPointX,
            nextPointY
        )
    }

    return res
}

fun List<Float>.toDonutDataPoints(): List<DonutDataPoint> {
    return this.map { DonutDataPoint(it) }
}

private fun List<Float>.floatLimits(): Pair<Float, Float> {

    val min = min() ?: 0F
    var max = max() ?: 1F

    if (min == max)
        max += 1F

    return Pair(min, max)
}

/**
 * Credits: http://www.jayway.com/author/andersericsson/
 */
private fun si(setSize: Int, i: Int): Int {
    return when {
        i > setSize - 1 -> setSize - 1
        i < 0 -> 0
        else -> i
    }
}
