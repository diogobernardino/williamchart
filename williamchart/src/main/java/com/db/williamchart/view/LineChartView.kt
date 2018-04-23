package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.db.williamchart.data.ChartSet
import com.db.williamchart.data.Line

class LineChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ChartView(context, attrs, defStyleAttr) {

    override fun onDrawChart(canvas: Canvas, data: ChartSet?) {

        if (data == null) return

        val line : Line = data as Line
        val paint = Paint()
        val path: Path

        paint.color = line.color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = line.strokeWidth

        path = createLinePath(line)

        canvas.drawPath(path, paint)
    }

    /**
     * Responsible for drawing a (non smooth) line.
     *
     * @param set [LineSet] object
     * @return [Path] object containing line
     */
    private fun createLinePath(line: Line): Path {

        val res = Path()

        res.moveTo(line.entries.first().x, line.entries.first().y)
        for (i in 1 until line.entries.size)
            res.lineTo(line.entries[i].x, line.entries[i].y)
        return res
    }

}