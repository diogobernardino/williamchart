package com.db.williamchart.animation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import com.db.williamchart.data.DonutDataPoint

class DefaultDonutAnimation {

    var duration: Long = DEFAULT_DURATION

    var interpolator: Interpolator = DecelerateInterpolator()

    fun animateFrom(
        entries: List<DonutDataPoint>,
        callback: () -> Unit
    ): DefaultDonutAnimation {

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

    companion object {
        private const val DEFAULT_DURATION = 1000L
    }
}
