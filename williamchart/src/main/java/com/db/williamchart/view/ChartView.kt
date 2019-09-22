package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.R
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.ChartConfiguration

abstract class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * API
     */

    var labelsSize: Float = defaultLabelsSize

    var labelsColor: Int = -0x1000000

    var labelsFont: Typeface? = null

    var axis: AxisType = AxisType.XY

    var animation: ChartAnimation = DefaultAnimation()

    /**
     * Members to be used by the different charts extending `ChartView`
     */

    protected lateinit var canvas: Canvas

    protected val painter: Painter = Painter(labelsFont = labelsFont)

    // Initialized in init() by chart views extending `ChartView` (e.g. LineChartView)
    protected lateinit var renderer: ChartContract.Renderer

    init {
        val styledAttributes =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ChartAttrs,
                0,
                0
            )
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        renderer.draw()
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

    protected fun showEditMode() {
        if (isInEditMode) {
            show(
                linkedMapOf(
                    "PORRO" to 5F,
                    "FUSCE" to 6.4F,
                    "EGET" to 3F
                )
            )
        }
    }

    companion object {
        private const val defaultFrameWidth = 200
        private const val defaultFrameHeight = 100
        private const val defaultLabelsSize = 60F
    }
}
