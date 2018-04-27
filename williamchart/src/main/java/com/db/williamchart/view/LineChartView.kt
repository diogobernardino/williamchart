package com.db.williamchart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartSet
import com.db.williamchart.data.Line

class LineChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ChartView(context, attrs, defStyleAttr) {

    private val smoothFactor = 0.15f

    override fun onDrawChart(canvas: Canvas, data: ChartSet?) {

        if (data == null) return

        val line : Line = data as Line
        val path: Path

        path = if (!line.smooth) createLinePath(line.entries) else createSmoothLinePath(line.entries)

        painter.prepare(color = line.color, style = Paint.Style.STROKE, strokeWidth = line.strokeWidth)
        canvas.drawPath(path, painter.paint)
    }

    private fun createLinePath(points: MutableList<ChartEntry>): Path {

        val res = Path()

        res.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size)
            res.lineTo(points[i].x, points[i].y)
        return res
    }

    /**
     * Credits: http://www.jayway.com/author/andersericsson/
     */
    private fun createSmoothLinePath(points: MutableList<ChartEntry>): Path {

        var thisPointX: Float
        var thisPointY: Float
        var nextPointX: Float
        var nextPointY: Float
        var startDiffX: Float
        var startDiffY: Float
        var endDiffX: Float
        var endDiffY: Float
        var firstControlX: Float
        var firstControlY: Float
        var secondControlX: Float
        var secondControlY: Float

        val res = Path()
        res.moveTo(points.first().x, points.first().y)

        for (i in 0 until points.size - 1) {

            thisPointX = points[i].x
            thisPointY = points[i].y

            nextPointX = points[i + 1].x
            nextPointY = points[i + 1].y

            startDiffX = nextPointX - points[si(points.size, i - 1)].x
            startDiffY = nextPointY - points[si(points.size, i - 1)].y

            endDiffX = points[si(points.size, i + 2)].x - thisPointX
            endDiffY = points[si(points.size, i + 29)].y - thisPointY

            firstControlX = thisPointX + smoothFactor * startDiffX
            firstControlY = thisPointY + smoothFactor * startDiffY

            secondControlX = nextPointX - smoothFactor * endDiffX
            secondControlY = nextPointY - smoothFactor * endDiffY

            res.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX, nextPointY)
        }

        return res

    }

    /**
     * Credits: http://www.jayway.com/author/andersericsson/
     */
    private fun si(setSize: Int, i: Int): Int {

        if (i > setSize - 1) return setSize - 1
        else if (i < 0) return 0
        return i
    }

}