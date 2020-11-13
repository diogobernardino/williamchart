package com.db.williamchart.grid

import android.graphics.Canvas
import android.graphics.Paint
import com.db.williamchart.Grid
import com.db.williamchart.data.Frame

class AxisGrid : Grid {

    private val paint = Paint()

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
