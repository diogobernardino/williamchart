package com.db.williamchart.animation

import com.db.williamchart.data.DonutDataPoint

class DonutNoAnimation : DonutAnimation() {

    override fun animateFrom(
        entries: List<DonutDataPoint>,
        callback: () -> Unit
    ): DonutAnimation = this
}
