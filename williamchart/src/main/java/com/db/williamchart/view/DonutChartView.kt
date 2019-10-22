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
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.animation.DefaultDonutAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.toRect
import com.db.williamchart.extensions.obtainStyledAttributes
import com.db.williamchart.renderer.DonutChartRenderer

@Experimental
annotation class ImplementsAlphaChart

@ImplementsAlphaChart
class DonutChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ChartContract.DonutView {

    @Suppress("MemberVisibilityCanBePrivate")
    var donutThickness = defaultThickness

    @Suppress("MemberVisibilityCanBePrivate")
    var donutColor = defaultColor

    @Suppress("MemberVisibilityCanBePrivate")
    var donutBackgroundColor = defaultBackgroundColor

    @Suppress("MemberVisibilityCanBePrivate")
    var donutRoundCorners = false

    @Suppress("MemberVisibilityCanBePrivate")
    var donutTotal = defaultDonutTotal

    private lateinit var canvas: Canvas

    private var renderer: ChartContract.DonutRenderer =
        DonutChartRenderer(this, DefaultDonutAnimation())

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
                total = donutTotal
            )

    init {
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.DonutChartAttrs))
    }

    override fun drawArc(value: Float, innerFrame: Frame) {

        if (donutRoundCorners)
            paint.strokeCap = Paint.Cap.ROUND

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = donutThickness
        paint.color = donutColor

        canvas.drawArc(
            innerFrame.toRect().toRectF(),
            defaultStartAngle,
            value,
            false,
            paint
        )
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

    fun show(value: Float) {
        doOnPreDraw { renderer.preDraw(configuration) }
        renderer.render(value)
    }

    fun animate(value: Float) {
        doOnPreDraw { renderer.preDraw(configuration) }
        renderer.anim(value, DefaultAnimation())
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            donutThickness =
                getDimension(R.styleable.DonutChartAttrs_chart_donutThickness, donutThickness)
            donutColor = getColor(R.styleable.DonutChartAttrs_chart_donutColor, donutColor)
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
        private const val defaultThickness = 50f
        private const val defaultColor = 0
        private const val defaultBackgroundColor = Color.TRANSPARENT
        private const val defaultStartAngle = 90f
        private const val defaultDonutTotal = 100f
    }
}
