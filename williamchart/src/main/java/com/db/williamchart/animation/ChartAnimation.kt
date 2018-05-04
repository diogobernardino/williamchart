package com.db.williamchart.animation

import com.db.williamchart.data.ChartEntry

abstract class ChartAnimation(open val entries: MutableList<ChartEntry>) {

    val duration: Long = 5000

    abstract fun animate(callback: () -> Unit) : ChartAnimation
}