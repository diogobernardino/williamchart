package com.db.williamchart.view

import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.db.williamchart.data.BarSet
import com.db.williamchart.data.ChartSet

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr) {

    var spacing = 10

    init {
        renderer.xPacked = true
        renderer.yAtZero = true
    }

    override fun drawData(
        innerFrameLeft: Float,
        innerFrameTop: Float,
        innerFrameRight: Float,
        innerFrameBottom: Float,
        data: ChartSet
    ) {

        if (canvas == null) return

        val set: BarSet = data as BarSet
        val halfBarWidth =
                (innerFrameRight - innerFrameLeft - (data.entries.size + 1) * spacing) /
                data.entries.size / 2

        painter.prepare(color = set.color, style = Paint.Style.FILL)
        set.entries.forEach {
            canvas!!.drawRoundRect(
                    RectF(it.x - halfBarWidth, it.y, it.x + halfBarWidth, innerFrameBottom),
                    set.radius, set.radius,
                    painter.paint)
        }
    }
}