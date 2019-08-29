package com.db.williamchart.data

import android.graphics.Rect

data class Frame(val left: Float, val top: Float, val right: Float, val bottom: Float)

fun Frame.toRect(): Rect =
    Rect(this.left.toInt(), this.top.toInt(), this.right.toInt(), this.bottom.toInt())

fun Frame.withPaddings(paddings: Paddings): Frame =
    Frame(
        left = left + paddings.left,
        top = top + paddings.top,
        right = right - paddings.right,
        bottom = bottom - paddings.bottom
    )