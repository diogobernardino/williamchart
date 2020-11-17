package com.db.williamchartdemo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.db.williamchart.Grid
import com.db.williamchart.data.Frame

class HorizontalDotGrid : Grid {

    private val paint by lazy {
        Paint().apply {
            color = Color.parseColor("#64386B")
            strokeWidth = 5f
            pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
        }
    }

    override fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    ) {
        yLabelsPositions.forEach {
            canvas.drawLine(innerFrame.left, it, innerFrame.right, it, paint)
        }
    }
}
