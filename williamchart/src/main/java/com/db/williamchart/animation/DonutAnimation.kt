package com.db.williamchart.animation

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import com.db.williamchart.data.DonutDataPoint

abstract class DonutAnimation {

    var duration: Long = DEFAULT_DURATION

    var interpolator: Interpolator = DecelerateInterpolator()

    abstract fun animateFrom(
        entries: List<DonutDataPoint>,
        callback: () -> Unit
    ): DonutAnimation

    companion object {
        private const val DEFAULT_DURATION = 1000L
    }
}
