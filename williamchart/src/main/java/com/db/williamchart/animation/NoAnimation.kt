package com.db.williamchart.animation

import com.db.williamchart.data.DataPoint

class NoAnimation : ChartAnimation<DataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DataPoint> = this
}
