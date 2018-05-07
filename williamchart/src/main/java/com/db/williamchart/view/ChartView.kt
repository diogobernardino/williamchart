package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.FontRes
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.R
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.ChartLabel

import com.db.williamchart.data.ChartSet
import com.db.williamchart.renderer.ChartRenderer

abstract class ChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr), ChartContract.View {

    private val defFrameWidth = 200

    private val defFrameHeight = 100

    private val drawListener = ViewTreeObserver.OnPreDrawListener {
        renderer.preDraw(
                measuredWidth,
                measuredHeight,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom)
    }

    protected var canvas: Canvas? = null

    protected val painter : Painter = Painter()

    private val renderer : ChartRenderer = ChartRenderer(this, painter, NoAnimation())

    init {
        viewTreeObserver.addOnPreDrawListener(drawListener)

        val arr = context.theme.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0)
        renderer.hasLabels = arr.getBoolean(R.styleable.ChartAttrs_chart_labels, true)
        renderer.labelsSize = arr.getDimension(R.styleable.ChartAttrs_chart_labelsSize, renderer.labelsSize)
        renderer.labelsColor = arr.getColor(R.styleable.ChartAttrs_chart_labelsColor, renderer.labelsColor)
        if (arr.hasValue(R.styleable.ChartAttrs_chart_labelsFont))
            renderer.labelsFont =
                    ResourcesCompat.getFont(context,
                            arr.getResourceId(R.styleable.ChartAttrs_chart_labelsFont, -1))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.setWillNotDraw(false)
        //style.init()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //style.clean()
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

    fun show() {
        renderer.show()
    }

    fun showWithAnimation() {
        renderer.animate()
    }

    fun setLabels(enable: Boolean) : ChartView{
        renderer.hasLabels = enable
        return this
    }

    fun setLabelsSize(size: Float) {
        renderer.labelsSize = size
    }

    fun setLabelsColor(@ColorInt color: Int) {
        renderer.labelsColor = color
    }

    fun setLabelsFont(@FontRes resId: Int) {
        renderer.labelsFont = ResourcesCompat.getFont(context, resId)
    }

    override fun drawLabels(xLabels : List<ChartLabel>) {

        if (canvas == null) return

        painter.prepare(
                textSize = renderer.labelsSize,
                color = renderer.labelsColor,
                font = renderer.labelsFont)
        xLabels.forEach { canvas!!.drawText(it.label, it.x, it.y, painter.paint) }
    }
}