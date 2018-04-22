package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

import com.db.williamchart.data.ChartSet

abstract class ChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        //Init attrs
    }

    private var data: ChartSet? = null

    fun add(set: ChartSet) {
        data = set
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
        onDrawChart(canvas, data)
    }

    /**
     * Method responsible to draw bars with the parsed screen points.
     *
     * @param canvas The canvas to draw on
     * @param data   [java.util.ArrayList] of [com.db.chart.model.ChartSet]
     * to use while drawing the Chart
     */
    protected abstract fun onDrawChart(canvas: Canvas, data: ChartSet?)

}