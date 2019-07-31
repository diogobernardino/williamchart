package com.db.williamchart.data

data class Paddings(val left: Float, val top: Float, val right: Float, val bottom: Float)

fun Paddings.mergeWith(paddings: Paddings): Paddings {
    return Paddings(
        maxOf(this.left, paddings.left),
        maxOf(this.top, paddings.top),
        maxOf(this.right, paddings.right),
        maxOf(this.bottom, paddings.bottom)
    )
}