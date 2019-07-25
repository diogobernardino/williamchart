package com.db.williamchart.animation

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import com.db.williamchart.data.DataPoint

abstract class ChartAnimation {

    var duration: Long = 1000

    var interpolator: Interpolator = DecelerateInterpolator()

    abstract fun animateFrom(y: Float, entries: List<DataPoint>, callback: () -> Unit): ChartAnimation
}