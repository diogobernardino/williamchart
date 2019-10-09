package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.R
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.toRect
import com.db.williamchart.extensions.obtainStyledAttributes
import com.db.williamchart.renderer.DonutChartRenderer

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ChartContract.DonutView {

    @Suppress("MemberVisibilityCanBePrivate")
    var thickness = defaultDonutThickness

    @Suppress("MemberVisibilityCanBePrivate")
    var color = defaultColor

    private val paint: Paint = Paint()

    private lateinit var canvas: Canvas

    private var renderer: ChartContract.DonutRenderer = DonutChartRenderer(this)

    private val configuration: DonutChartConfiguration
        get() =
            DonutChartConfiguration(
                width = measuredWidth,
                height = measuredHeight,
                paddings = Paddings(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    paddingRight.toFloat(),
                    paddingBottom.toFloat()
                ),
                thickness = thickness
            )

    init {
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.DonutChartAttrs))
        show(listOf())
    }

    override fun drawArc(value: Float, innerFrame: Frame) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = thickness
        paint.color = color
        canvas.drawArc(
            innerFrame.toRect().toRectF(),
            START_ANGLE,
            CURRENT_ANGLE,
            false,
            paint
        )
    }

    override fun drawDebugFrame(innerFrame: Frame) {
        canvas.drawRect(innerFrame.toRect(), paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    fun show(values: List<Float>) {
        doOnPreDraw { renderer.preDraw(configuration) }
        renderer.render(values)
    }

    fun animate(values: List<Float>) {
        doOnPreDraw { renderer.preDraw(configuration) }
        renderer.anim(values, DefaultAnimation())
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            thickness = getDimension(R.styleable.DonutChartAttrs_chart_thickness, thickness)
            color = getColor(R.styleable.DonutChartAttrs_chart_donutColor, color)
            recycle()
        }
    }

    companion object {
        private const val defaultDonutThickness = 50f
        private const val defaultColor = 0
        private const val START_ANGLE = 90f
        private const val CURRENT_ANGLE = 120f
    }
}
