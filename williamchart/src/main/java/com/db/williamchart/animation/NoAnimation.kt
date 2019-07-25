package com.db.williamchart.animation

import com.db.williamchart.data.DataPoint

class NoAnimation : ChartAnimation() {

    override fun animateFrom(
        y: Float,
        entries: List<DataPoint>,
        callback: () -> Unit
    ): ChartAnimation {
        callback()
        return this
    }
}