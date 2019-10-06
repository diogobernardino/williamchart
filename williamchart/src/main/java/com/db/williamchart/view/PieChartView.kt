package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.data.Frame
import com.db.williamchart.data.toRect

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ChartContract.DonutView {

    private val paint: Paint = Paint()

    private lateinit var innerFrame: Frame

    init {
        doOnPreDraw {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = STROKE_WIDTH
            val left = paddingLeft + STROKE_WIDTH / 2
            val top = paddingTop + STROKE_WIDTH / 2
            val right = width - paddingRight - STROKE_WIDTH / 2
            val bottom = height - paddingBottom - STROKE_WIDTH / 2
            innerFrame = Frame(left, top, right, bottom)
        }
    }

    override fun drawArc(value: Float) {
    }

    override fun drawDebugFrame(innerFrame: Frame) {
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawArc(innerFrame.toRect().toRectF(), START_ANGLE, CURRENT_ANGLE, false, paint)
    }

    companion object {
        private const val STROKE_WIDTH = 20f
        private const val START_ANGLE = 90f
        private const val CURRENT_ANGLE = 120f
    }
}
