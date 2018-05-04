package com.db.williamchart.animation

import android.animation.ValueAnimator
import com.db.williamchart.data.ChartEntry

abstract class ChartAnimation(open val entries: MutableList<ChartEntry>) {

    abstract fun animate(callback: () -> Unit) : ValueAnimator
}