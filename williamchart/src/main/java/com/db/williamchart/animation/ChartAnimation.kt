package com.db.williamchart.animation

import android.animation.ObjectAnimator
import com.db.williamchart.data.ChartEntry

abstract class ChartAnimation(open val entries: MutableList<ChartEntry>) {

    abstract fun animate() : ObjectAnimator
}