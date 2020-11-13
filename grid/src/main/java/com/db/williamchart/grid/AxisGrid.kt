package com.db.williamchart.grid

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.db.williamchart.Grid
import com.db.williamchart.data.Frame

class AxisGrid : Grid {

    var color = -0x1000000
    var strokeWidth = 5F
    var dashEffect = false

    private val paint = Paint()

    override fun preDraw() {
        paint.color = color
        paint.strokeWidth = strokeWidth
        if (dashEffect)
            paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    override fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        xLabelsPositions.forEach {
            canvas.drawLine(it, innerFrame.bottom, it, innerFrame.top, paint)
        }
        yLabelsPositions.forEach {
            canvas.drawLine(innerFrame.left, it, innerFrame.right, it, paint)
        }
    }
}
