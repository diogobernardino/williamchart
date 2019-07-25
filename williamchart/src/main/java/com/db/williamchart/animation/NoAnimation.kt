package com.db.williamchart.animation

import com.db.williamchart.data.ChartEntry

class NoAnimation : ChartAnimation() {

    override fun animateFrom(
        y: Float,
        entries: List<ChartEntry>,
        callback: () -> Unit
    ): ChartAnimation {
        callback()
        return this
    }
}