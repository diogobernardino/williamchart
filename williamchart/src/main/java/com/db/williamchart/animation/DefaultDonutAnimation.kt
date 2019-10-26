package com.db.williamchart.animation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.db.williamchart.data.DonutDataPoint

class DefaultDonutAnimation : ChartAnimation<DonutDataPoint>() {

    override fun animateFrom(
        startPosition: Float,
        entries: List<DonutDataPoint>,
        callback: () -> Unit
    ): ChartAnimation<DonutDataPoint> {

        // Entries animators
        entries.forEach { dataPoint ->
            val eAnimator: ObjectAnimator =
                ObjectAnimator.ofFloat(dataPoint, "screenDegrees", 0f, dataPoint.screenDegrees)
            eAnimator.duration = duration
            eAnimator.interpolator = interpolator
            eAnimator.start()
        }

        // Global animator
        val animator: ValueAnimator = ValueAnimator.ofInt(0, 1)
        animator.addUpdateListener { callback.invoke() }
        animator.duration = duration
        animator.interpolator = interpolator
        animator.start()

        return this
    }
}
