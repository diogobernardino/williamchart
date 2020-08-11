package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.R
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultDonutAnimation
import com.db.williamchart.animation.DonutNoAnimation
import com.db.williamchart.data.configuration.DonutChartConfiguration
import com.db.williamchart.data.DonutDataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.toRect
import com.db.williamchart.extensions.obtainStyledAttributes
import com.db.williamchart.renderer.DonutChartRenderer

class DonutChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ChartContract.DonutView {

    @Suppress("MemberVisibilityCanBePrivate")
    var donutThickness = DEFAULT_THICKNESS

    @Suppress("MemberVisibilityCanBePrivate")
    var donutColors = intArrayOf(DEFAULT_COLOR)

    @Suppress("MemberVisibilityCanBePrivate")
    var donutBackgroundColor = DEFAULT_BACKGROUND_COLOR

    @Suppress("MemberVisibilityCanBePrivate")
    var donutRoundCorners = false

    @Suppress("MemberVisibilityCanBePrivate")
    var donutTotal = DEFAULT_DONUT_TOTAL

    var animation: ChartAnimation<DonutDataPoint> = DefaultDonutAnimation()

    private lateinit var canvas: Canvas

    private var renderer: ChartContract.DonutRenderer =
        DonutChartRenderer(this, DonutNoAnimation())

    private val paint: Paint = Paint()

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
                thickness = donutThickness,
                total = donutTotal,
                colorsSize = donutColors.size,
                barBackgroundColor = donutBackgroundColor
            )

    init {
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.DonutChartAttrs))
    }

    override fun drawArc(degrees: List<Float>, innerFrame: Frame) {

        if (donutRoundCorners)
            paint.strokeCap = Paint.Cap.ROUND

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = donutThickness

        degrees.forEachIndexed { index, degree ->
            paint.color = donutColors[index]
            canvas.drawArc(
                innerFrame.toRect().toRectF(),
                DEFAULT_START_ANGLE,
                degree,
                false,
                paint
            )
        }
    }

    override fun drawBackground(innerFrame: Frame) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = donutThickness
        paint.color = donutBackgroundColor

        val radius = (innerFrame.bottom - innerFrame.top) / 2
        canvas.drawCircle(
            innerFrame.left + radius,
            innerFrame.top + radius,
            radius,
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
        renderer.anim(values, animation)
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            donutThickness =
                getDimension(R.styleable.DonutChartAttrs_chart_donutThickness, donutThickness)
            donutBackgroundColor = getColor(
                R.styleable.DonutChartAttrs_chart_donutBackgroundColor,
                donutBackgroundColor
            )
            donutRoundCorners =
                getBoolean(R.styleable.DonutChartAttrs_chart_donutRoundCorners, donutRoundCorners)
            donutTotal = getFloat(R.styleable.DonutChartAttrs_chart_donutTotal, donutTotal)
            recycle()
        }
    }

    companion object {
        private const val DEFAULT_THICKNESS = 50f
        private const val DEFAULT_COLOR = 0
        private const val DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT
        private const val DEFAULT_START_ANGLE = 90f
        private const val DEFAULT_DONUT_TOTAL = 100f
    }
}
