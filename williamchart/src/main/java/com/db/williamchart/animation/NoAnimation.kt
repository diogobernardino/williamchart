package com.db.williamchart.animation

import com.db.williamchart.data.ChartEntry


class NoAnimation : ChartAnimation() {

    override fun animateFrom(y: Float,
                             entries: MutableList<ChartEntry>,
                             callback: () -> Unit) : ChartAnimation{
        callback.invoke()
        return this
    }
}