package com.db.williamchart.animation

import com.db.williamchart.data.ChartEntry

abstract class ChartAnimation {

    val duration: Long = 5000

    abstract fun animateFrom(y: Float, entries: MutableList<ChartEntry>, callback: () -> Unit) : ChartAnimation
}