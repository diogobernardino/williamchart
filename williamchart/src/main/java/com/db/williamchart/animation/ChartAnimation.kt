package com.db.williamchart.animation

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import com.db.williamchart.data.DataPoint

abstract class ChartAnimation {

    var duration: Long = DEFAULT_DURATION

    var interpolator: Interpolator = DecelerateInterpolator()

    abstract fun animateFrom(
        y: Float,
        entries: List<DataPoint>,
        callback: () -> Unit
    ): ChartAnimation

    companion object {
        private const val DEFAULT_DURATION = 1000L
    }
}
