package com.db.williamchart.animation

import com.db.williamchart.data.DonutDataPoint

class DonutNoAnimation : ChartAnimation<DonutDataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DonutDataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DonutDataPoint> = this
}
