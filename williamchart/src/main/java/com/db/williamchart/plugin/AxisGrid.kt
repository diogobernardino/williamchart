package com.db.williamchart.plugin

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.db.williamchart.Grid
import com.db.williamchart.data.Frame
import com.db.williamchart.renderer.RendererConstants

private const val DEFAULT_STROKE_WIDTH = 5f
private const val DEFAULT_DASH_INTERVAL = 10f

class AxisGrid : Grid {

    var gridType = GridType.FULL
    var gridEffect = GridEffect.SOLID
    var color = -0x1000000
    var strokeWidth = DEFAULT_STROKE_WIDTH

    private val paint by lazy {
        Paint().apply {

            color = this@AxisGrid.color
            strokeWidth = this@AxisGrid.strokeWidth

            if (gridEffect != GridEffect.SOLID) {
                val interval =
                    if (gridEffect == GridEffect.DASHED) DEFAULT_STROKE_WIDTH * 2
                    else DEFAULT_STROKE_WIDTH
                pathEffect =
                    DashPathEffect(
                        floatArrayOf(interval, interval),
                        0f
                    )
            }
        }
    }

    override fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        if (gridType == GridType.FULL || gridType == GridType.VERTICAL) {
            xLabelsPositions.forEach {
                canvas.drawLine(it, innerFrame.bottom, it, innerFrame.top, paint)
            }
        }

        if (gridType == GridType.FULL || gridType == GridType.HORIZONTAL) {
            yLabelsPositions.forEach {
                canvas.drawLine(innerFrame.left, it - RendererConstants.labelsPaddingToInnerChart, innerFrame.right, it - RendererConstants.labelsPaddingToInnerChart, paint)
            }
        }
    }
}

enum class GridType {
    FULL,
    VERTICAL,
    HORIZONTAL
}

enum class GridEffect {
    SOLID,
    DASHED,
    DOTTED
}
