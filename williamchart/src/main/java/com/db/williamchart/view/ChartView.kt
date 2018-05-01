package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.data.ChartLabel

import com.db.williamchart.data.ChartSet
import com.db.williamchart.renderer.ChartRenderer

abstract class ChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr), ChartContract.View {

    private val defFrameWidth = 200

    private val defFrameHeight = 100

    private val drawListener = object : ViewTreeObserver.OnPreDrawListener {

        override fun onPreDraw(): Boolean {
            this@ChartView.viewTreeObserver.removeOnPreDrawListener(this)

            renderer.preDraw(
                    measuredWidth,
                    measuredHeight,
                    paddingLeft,
                    paddingTop,
                    paddingRight,
                    paddingBottom)

            return true
        }
    }

    protected var canvas: Canvas? = null

    protected val painter : Painter = Painter()

    private val renderer : ChartRenderer = ChartRenderer(this, painter)

    fun add(set: ChartSet) {
        renderer.add(set)
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

    fun show() {
        viewTreeObserver.addOnPreDrawListener(drawListener)
        postInvalidate()
    }

    override fun drawLabels(xLabels : List<ChartLabel>) {

        if (canvas == null) return

        painter.prepare(textSize = renderer.labelSize)
        xLabels.forEach { canvas!!.drawText(it.label, it.x, it.y, painter.paint) }
    }
}