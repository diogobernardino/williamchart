package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import com.db.williamchart.data.Frame
import com.db.williamchart.data.toRect

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight
        val bottom = height - paddingBottom
        val frame = Frame(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        canvas?.drawArc(frame.toRect().toRectF(), START_ANGLE, CURRENT_ANGLE, false, paint)
    }

    companion object {
        private const val START_ANGLE = 90f
        private const val CURRENT_ANGLE = 40f
    }
}
