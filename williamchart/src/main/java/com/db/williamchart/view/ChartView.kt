package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.R
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.Label
import com.db.williamchart.renderer.ChartRenderer

abstract class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), ChartContract.View {

    enum class Axis { NONE, X, Y, XY }

    private val defaultFrameWidth = 200

    private val defaultFrameHeight = 100

    var labelsSize: Float = 60F

    var labelsColor: Int = -0x1000000

    var labelsFont: Typeface? = null

    var axis: Axis = Axis.XY

    var animation: ChartAnimation = DefaultAnimation()

    private val drawListener = ViewTreeObserver.OnPreDrawListener {
        renderer.preDraw(
            measuredWidth,
            measuredHeight,
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom,
            axis,
            labelsSize
        )
    }

    protected lateinit var canvas: Canvas

    protected val painter: Painter = Painter()

    protected val renderer: ChartRenderer = ChartRenderer(this, painter, NoAnimation())

    init {
        viewTreeObserver.addOnPreDrawListener(drawListener)

        val styledAttributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0)
        handleAttributes(styledAttributes)
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

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(
            if (widthMode == MeasureSpec.AT_MOST) defaultFrameWidth else widthMeasureSpec,
            if (heightMode == MeasureSpec.AT_MOST) defaultFrameHeight else heightMeasureSpec
        )
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

    override fun drawLabels(xLabels: List<Label>) {

        painter.prepare(
            textSize = labelsSize,
            color = labelsColor,
            font = labelsFont
        )
        xLabels.forEach { canvas.drawText(it.label, it.x, it.y, painter.paint) }
    }

    fun show(entries: HashMap<String, Float>) {
        renderer.render(entries)
    }

    fun anim(entries: HashMap<String, Float>) {
        renderer.anim(entries, animation)
    }

    private fun handleAttributes(typedArray: TypedArray) {

        axis = when (typedArray.getString(R.styleable.ChartAttrs_chart_axis)) {
            "0" -> Axis.NONE
            "1" -> Axis.X
            "2" -> Axis.Y
            else -> Axis.XY
        }
        labelsSize = typedArray.getDimension(R.styleable.ChartAttrs_chart_labelsSize, labelsSize)
        labelsColor = typedArray.getColor(R.styleable.ChartAttrs_chart_labelsColor, labelsColor)
        if (typedArray.hasValue(R.styleable.ChartAttrs_chart_labelsFont))
            labelsFont =
                ResourcesCompat.getFont(
                    context,
                    typedArray.getResourceId(R.styleable.ChartAttrs_chart_labelsFont, -1)
                )

        typedArray.recycle()
    }
}