package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.db.williamchart.data.ChartSet

class LineChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ChartView(context, attrs, defStyleAttr) {

    override fun onDrawChart(canvas: Canvas, data: ChartSet?) {

        val paint = Paint()
        paint.color = Color.BLACK
        canvas.drawCircle(10F, 10F, 10F, paint)
    }

}