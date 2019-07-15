package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.R
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.ChartLabel

import com.db.williamchart.data.ChartSet
import com.db.williamchart.renderer.ChartRenderer

abstract class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), ChartContract.View {

    enum class Axis { NONE, X, Y, XY }

    private val defFrameWidth = 200

    private val defFrameHeight = 100

    var labelsSize: Float = 60F

    var labelsColor: Int = -0x1000000

    var labelsFont: Typeface? = null

    var axis: Axis = Axis.XY

    var animation: ChartAnimation = DefaultAnimation()

    private val drawListener = ViewTreeObserver.OnPreDrawListener {
        renderer.preDraw(measuredWidth, measuredHeight,
                paddingLeft, paddingTop, paddingRight, paddingBottom,
                axis, labelsSize)
    }

    protected var canvas: Canvas? = null

    protected val painter: Painter = Painter()

    protected val renderer: ChartRenderer = ChartRenderer(this, painter, NoAnimation())

    init {
        viewTreeObserver.addOnPreDrawListener(drawListener)

        val arr = context.theme.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0)

        axis = when (arr.getString(R.styleable.ChartAttrs_chart_axis)) {
            "0" -> Axis.NONE
            "1" -> Axis.X
            "2" -> Axis.Y
            else -> Axis.XY
        }
        labelsSize = arr.getDimension(R.styleable.ChartAttrs_chart_labelsSize, labelsSize)
        labelsColor = arr.getColor(R.styleable.ChartAttrs_chart_labelsColor, labelsColor)
        if (arr.hasValue(R.styleable.ChartAttrs_chart_labelsFont))
            labelsFont =
                    ResourcesCompat.getFont(context,
                            arr.getResourceId(R.styleable.ChartAttrs_chart_labelsFont, -1))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setWillNotDraw(false)
        // style.init()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // style.clean()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(
                if (widthMode == View.MeasureSpec.AT_MOST) defFrameWidth else widthMeasureSpec,
                if (heightMode == View.MeasureSpec.AT_MOST) defFrameHeight else heightMeasureSpec)
    }

    /**
     * The method listens for chart clicks and checks whether it intercepts
     * a known Region. It will then use the registered Listener.onClick
     * to return the region's index.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    fun add(set: ChartSet) {
        renderer.add(set)
    }

    fun render() {
        renderer.render()
    }

    fun anim() {
        renderer.anim(animation)
    }

    override fun drawLabels(xLabels: List<ChartLabel>) {

        if (canvas == null) return

        painter.prepare(
                textSize = labelsSize,
                color = labelsColor,
                font = labelsFont)
        xLabels.forEach { canvas!!.drawText(it.label, it.x, it.y, painter.paint) }
    }
}