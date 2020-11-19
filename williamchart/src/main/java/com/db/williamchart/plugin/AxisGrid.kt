package com.db.williamchart.plugin

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.db.williamchart.Grid
import com.db.williamchart.data.Frame

private const val DEFAULT_STROKE_WIDTH = 5f
private const val DEFAULT_DASH_INTERVAL = 10f

class AxisGrid : Grid {

    var gridType = GridType.FULL
    var color = -0x1000000
    var strokeWidth = DEFAULT_STROKE_WIDTH
    var dashEffect = false

    private val paint by lazy {
        Paint().apply {
            color = this@AxisGrid.color
            strokeWidth = this@AxisGrid.strokeWidth
            if (dashEffect)
                pathEffect =
                    DashPathEffect(
                        floatArrayOf(DEFAULT_DASH_INTERVAL, DEFAULT_DASH_INTERVAL),
                        0f
                    )
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
                canvas.drawLine(innerFrame.left, it, innerFrame.right, it, paint)
            }
        }
    }
}

enum class GridType {
    FULL,
    VERTICAL,
    HORIZONTAL
}
