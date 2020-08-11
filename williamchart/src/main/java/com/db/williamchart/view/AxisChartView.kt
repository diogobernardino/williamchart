package com.db.williamchart.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.ExperimentalFeature
import com.db.williamchart.Painter
import com.db.williamchart.R
import com.db.williamchart.Tooltip
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.configuration.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Scale
import com.db.williamchart.extensions.obtainStyledAttributes
import com.db.williamchart.renderer.RendererConstants.Companion.NOT_INITIALIZED

@OptIn(ExperimentalFeature::class)
abstract class AxisChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var labelsSize: Float = DEFAULT_LABEL_SIZE

    var labelsColor: Int = -0x1000000

    var labelsFont: Typeface? = null

    var axis: AxisType = AxisType.XY

    var scale: Scale = Scale(NOT_INITIALIZED, NOT_INITIALIZED)

    var labelsFormatter: (Float) -> String = { it.toString() }

    var animation: ChartAnimation<DataPoint> = DefaultAnimation()

    var tooltip: Tooltip = object : Tooltip {
        override fun onCreateTooltip(parentView: ViewGroup) {}
        override fun onDataPointTouch(x: Float, y: Float) {}
        override fun onDataPointClick(x: Float, y: Float) {}
    }

    @ExperimentalFeature
    var onDataPointClickListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    @ExperimentalFeature
    var onDataPointTouchListener: (index: Int, x: Float, y: Float) -> Unit = { _, _, _ -> }

    protected lateinit var canvas: Canvas

    protected val painter: Painter = Painter(labelsFont = labelsFont)

    // Initialized in init() by chart views extending `AxisChartView` (e.g. LineChartView)
    protected lateinit var renderer: ChartContract.Renderer

    private var gestureDetector: GestureDetectorCompat

    init {
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.ChartAttrs))
        gestureDetector =
            GestureDetectorCompat(
                this.context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent?): Boolean = true
                    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                        val (index, x, y) = renderer.processClick(e?.x, e?.y)
                        return if (index != -1) {
                            onDataPointClickListener(index, x, y)
                            tooltip.onDataPointClick(x, y)
                            true
                        } else super.onSingleTapConfirmed(e)
                    }
                }
            )
        doOnPreDraw { tooltip.onCreateTooltip(this) }
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
            if (widthMode == MeasureSpec.AT_MOST) DEFAULT_FRAME_WIDTH else widthMeasureSpec,
            if (heightMode == MeasureSpec.AT_MOST) DEFAULT_FRAME_HEIGHT else heightMeasureSpec
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val (index, x, y) = renderer.processTouch(event?.x, event?.y)
        if (index != -1) {
            onDataPointTouchListener(index, x, y)
            tooltip.onDataPointTouch(x, y)
        }
        return if (gestureDetector.onTouchEvent(event)) true
        else super.onTouchEvent(event)
    }

    abstract val chartConfiguration: ChartConfiguration

    fun show(entries: LinkedHashMap<String, Float>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.render(entries)
    }

    fun animate(entries: LinkedHashMap<String, Float>) {
        doOnPreDraw { renderer.preDraw(chartConfiguration) }
        renderer.anim(entries, animation)
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {

            axis = when (getString(R.styleable.ChartAttrs_chart_axis)) {
                "0" -> AxisType.NONE
                "1" -> AxisType.X
                "2" -> AxisType.Y
                else -> AxisType.XY
            }

            labelsSize = getDimension(R.styleable.ChartAttrs_chart_labelsSize, labelsSize)

            labelsColor = getColor(R.styleable.ChartAttrs_chart_labelsColor, labelsColor)

            if (hasValue(R.styleable.ChartAttrs_chart_labelsFont) && !isInEditMode) {
                labelsFont =
                    ResourcesCompat.getFont(
                        context,
                        getResourceId(R.styleable.ChartAttrs_chart_labelsFont, -1)
                    )
                painter.labelsFont = labelsFont
            }

            recycle()
        }
    }

    protected fun handleEditMode() {
        if (isInEditMode) {
            show(EDIT_MODE_SAMPLE_DATA)
        }
    }

    companion object {
        private const val DEFAULT_FRAME_WIDTH = 200
        private const val DEFAULT_FRAME_HEIGHT = 100
        private const val DEFAULT_LABEL_SIZE = 60F
        private val EDIT_MODE_SAMPLE_DATA =
            linkedMapOf(
                "Label1" to 1f,
                "Label2" to 7.5f,
                "Label3" to 4.7f,
                "Label4" to 3.5f
            )
    }
}
