package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout

import com.db.williamchart.data.ChartSet
import com.db.williamchart.renderer.ChartRenderer

abstract class ChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        // Init
    }

    private val DEFAULT_WIDTH = 200

    private val DEFAULT_HEIGHT = 100

    private val drawListener = object : ViewTreeObserver.OnPreDrawListener {

        override fun onPreDraw(): Boolean {
            this@ChartView.viewTreeObserver.removeOnPreDrawListener(this)
            renderer.preDraw(measuredWidth, measuredHeight)
            return true
        }
    }

    private var renderer : ChartRenderer = ChartRenderer()

    fun add(set: ChartSet) {
        renderer.data = set
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
                if (widthMode == View.MeasureSpec.AT_MOST) DEFAULT_WIDTH else widthMeasureSpec,
                if (heightMode == View.MeasureSpec.AT_MOST) DEFAULT_HEIGHT else heightMeasureSpec)
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
        renderer.xLabels!!.forEach { canvas.drawText(it.label, it.x, it.y, Paint()) } // Draw X
        onDrawChart(canvas, renderer.data)
    }

    /**
     * Method responsible to draw bars with the parsed screen points.
     *
     * @param canvas The canvas to draw on
     * @param data   [java.util.ArrayList] of [com.db.chart.model.ChartSet]
     * to use while drawing the Chart
     */
    protected abstract fun onDrawChart(canvas: Canvas, data: ChartSet?)

    /**
     * Base method when a show chart occurs
     */
    fun show() {
        viewTreeObserver.addOnPreDrawListener(drawListener)
        postInvalidate()
    }
}