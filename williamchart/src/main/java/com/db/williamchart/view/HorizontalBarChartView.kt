package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.db.williamchart.ChartContract
import com.db.williamchart.R
import com.db.williamchart.animation.DefaultHorizontalAnimation
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.BarChartConfiguration
import com.db.williamchart.data.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.toRect
import com.db.williamchart.extensions.obtainStyledAttributes
import com.db.williamchart.renderer.HorizontalBarChartRenderer

class HorizontalBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr), ChartContract.BarView {

    @Suppress("MemberVisibilityCanBePrivate")
    var spacing = defaultSpacing

    @ColorInt
    @Suppress("MemberVisibilityCanBePrivate")
    var barsColor: Int = defaultBarsColor

    @Suppress("MemberVisibilityCanBePrivate")
    var barRadius: Float = defaultBarsRadius

    @Suppress("MemberVisibilityCanBePrivate")
    var barsBackgroundColor: Int = -1

    override val chartConfiguration: ChartConfiguration
        get() = BarChartConfiguration(
            measuredWidth,
            measuredHeight,
            Paddings(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                paddingRight.toFloat(),
                paddingBottom.toFloat()
            ),
            axis,
            labelsSize,
            barsBackgroundColor
        )

    init {
        renderer = HorizontalBarChartRenderer(this, painter, NoAnimation())
        animation = DefaultHorizontalAnimation()
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.BarChartAttrs))
        showEditMode()
    }

    override fun drawBars(
        points: List<DataPoint>,
        innerFrame: Frame
    ) {

        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (points.size + 1) * spacing) / points.size / 2

        painter.prepare(color = barsColor, style = Paint.Style.FILL)
        points.forEach {
            canvas.drawRoundRect(
                RectF(
                    innerFrame.left,
                    it.screenPositionY - halfBarWidth,
                    it.screenPositionX,
                    it.screenPositionY + halfBarWidth
                ),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }

    override fun drawBarsBackground(points: List<DataPoint>, innerFrame: Frame) {

        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (points.size + 1) * spacing) / points.size / 2

        painter.prepare(color = barsBackgroundColor, style = Paint.Style.FILL)
        points.forEach {
            canvas.drawRoundRect(
                RectF(
                    innerFrame.left,
                    it.screenPositionY - halfBarWidth,
                    innerFrame.right,
                    it.screenPositionY + halfBarWidth
                ),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }

    override fun drawLabels(xLabels: List<Label>) {

        painter.prepare(
            textSize = labelsSize,
            color = labelsColor,
            font = labelsFont
        )

        xLabels.forEach {
            canvas.drawText(
                it.label,
                it.screenPositionX,
                it.screenPositionY,
                painter.paint
            )
        }
    }

    override fun drawDebugFrame(outerFrame: Frame, innerFrame: Frame, labelsFrame: List<Frame>) {
        painter.prepare(color = -0x1000000, style = Paint.Style.STROKE)
        canvas.drawRect(outerFrame.toRect(), painter.paint)
        canvas.drawRect(innerFrame.toRect(), painter.paint)
        labelsFrame.forEach { canvas.drawRect(it.toRect(), painter.paint) }
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            spacing = getDimension(R.styleable.BarChartAttrs_chart_spacing, spacing)
            barsColor = getColor(R.styleable.BarChartAttrs_chart_barsColor, barsColor)
            barRadius = getDimension(R.styleable.BarChartAttrs_chart_barsRadius, barRadius)
            barsBackgroundColor =
                getColor(R.styleable.BarChartAttrs_chart_barsBackgroundColor, barsBackgroundColor)
            recycle()
        }
    }

    companion object {
        private const val defaultSpacing = 10f
        private const val defaultBarsColor = -0x1000000
        private const val defaultBarsRadius = 0F
    }
}
