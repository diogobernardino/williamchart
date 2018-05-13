package com.db.williamchart.animation

import com.db.williamchart.data.ChartEntry

abstract class ChartAnimation {

    var duration: Long = 1000

    abstract fun animateFrom(y: Float, entries: MutableList<ChartEntry>, callback: () -> Unit) : ChartAnimation
}