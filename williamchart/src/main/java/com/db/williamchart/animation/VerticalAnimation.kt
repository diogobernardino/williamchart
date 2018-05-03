package com.db.williamchart.animation

import android.animation.ObjectAnimator
import com.db.williamchart.data.ChartEntry


class VerticalAnimation(
        override val entries: MutableList<ChartEntry>,
        var start: Float) : ChartAnimation(entries) {

    override fun animate() : ObjectAnimator{
        val animator: ObjectAnimator =
                ObjectAnimator.ofFloat(entries[1], "y", start, entries[1].y)
        animator.addUpdateListener { println(entries[1].y) }
        animator.start()
        return animator
    }
}