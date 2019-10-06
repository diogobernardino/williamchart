package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.toRect
import com.db.williamchart.renderer.DonutChartRenderer

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ChartContract.DonutView {

    private val paint: Paint = Paint()

    private lateinit var canvas: Canvas

    private var renderer: ChartContract.DonutRenderer = DonutChartRenderer(this)

    init {
        doOnPreDraw {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = STROKE_WIDTH

            val configuration = DonutChartConfiguration(
                width = measuredWidth,
                height = measuredHeight,
                paddings = Paddings(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    paddingRight.toFloat(),
                    paddingBottom.toFloat()
                )
            )
            renderer.preDraw(configuration)
        }
    }

    override fun drawArc(value: Float, innerFrame: Frame) {
        val left = innerFrame.left + STROKE_WIDTH / 2
        val top = innerFrame.top + STROKE_WIDTH / 2
        val right = innerFrame.right - STROKE_WIDTH / 2
        val bottom = innerFrame.bottom - STROKE_WIDTH / 2
        val innerFrameWithStroke = Frame(left, top, right, bottom)

        canvas.drawArc(
            innerFrameWithStroke.toRect().toRectF(),
            START_ANGLE,
            CURRENT_ANGLE,
            false,
            paint
        )
    }

    override fun drawDebugFrame(innerFrame: Frame) {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    companion object {
        private const val STROKE_WIDTH = 20f
        private const val START_ANGLE = 90f
        private const val CURRENT_ANGLE = 120f
    }
}
