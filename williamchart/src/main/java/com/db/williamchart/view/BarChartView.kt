package com.db.williamchart.view

import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.db.williamchart.data.DataPoint

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr) {

    var spacing = 10

    @ColorInt
    var barColor: Int = -0x1000000
    var barRadius: Float = 0F

    init {
        renderer.xPacked = true
        renderer.yAtZero = true
    }

    override fun drawData(
        innerFrameLeft: Float,
        innerFrameTop: Float,
        innerFrameRight: Float,
        innerFrameBottom: Float,
        entries: List<DataPoint>
    ) {

        val halfBarWidth = (innerFrameRight - innerFrameLeft - (entries.size + 1) * spacing) / entries.size / 2

        painter.prepare(color = barColor, style = Paint.Style.FILL)
        entries.forEach {
            canvas.drawRoundRect(
                RectF(
                    it.screenPositionX - halfBarWidth,
                    it.screenPositionY,
                    it.screenPositionX + halfBarWidth,
                    innerFrameBottom
                ),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }
}