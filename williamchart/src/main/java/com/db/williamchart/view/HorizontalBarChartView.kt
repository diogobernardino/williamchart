package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.view.doOnPreDraw
import com.db.williamchart.ChartContract
import com.db.williamchart.R
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.toRect
import com.db.williamchart.renderer.HorizontalBarChartRenderer

class HorizontalBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr), ChartContract.View {

    /**
     * API
     */

    @Suppress("MemberVisibilityCanBePrivate")
    var spacing = 10f

    @ColorInt
    @Suppress("MemberVisibilityCanBePrivate")
    var barsColor: Int = -0x1000000

    @Suppress("MemberVisibilityCanBePrivate")
    var barRadius: Float = 0F

    init {
        doOnPreDraw {
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

        renderer = HorizontalBarChartRenderer(this, painter, NoAnimation())

        val styledAttributes =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.BarChartAttrs,
                0,
                0
            )
        handleAttributes(styledAttributes)
    }

    override fun drawData(
        innerFrame: Frame,
        entries: List<DataPoint>
    ) {

        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (entries.size + 1) * spacing) /
                entries.size /
                2

        painter.prepare(
            color = barsColor,
            style = Paint.Style.FILL
        )

        entries.forEach {
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
        spacing = typedArray.getDimension(R.styleable.BarChartAttrs_chart_spacing, spacing)
        barsColor = typedArray.getColor(R.styleable.BarChartAttrs_chart_barsColor, barsColor)
        typedArray.recycle()
    }
}