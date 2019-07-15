package com.db.williamchart.view

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartSet
import com.db.williamchart.data.Line

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr) {

    private val smoothFactor = 0.15f

    override fun drawData(
        innerFrameLeft: Float,
        innerFrameTop: Float,
        innerFrameRight: Float,
        innerFrameBottom: Float,
        data: ChartSet
    ) {

        if (canvas == null) return

        val line: Line = data as Line
        val linePath: Path

        linePath = if (!line.smooth) createLinePath(line.entries) else createSmoothLinePath(line.entries)

        if (line.hasFill() || line.hasGradientFillColors()) { // Draw background

            if (line.hasFill()) painter.prepare(color = line.fillColor, style = Paint.Style.FILL)
            else painter.prepare(
                    shader = LinearGradient(innerFrameLeft,
                            innerFrameTop,
                            innerFrameLeft,
                            innerFrameBottom,
                            line.gradientFillColors[0],
                            line.gradientFillColors[1],
                            Shader.TileMode.MIRROR),
                    style = Paint.Style.FILL)

            canvas!!.drawPath(
                    createBackgroundPath(linePath, line.entries, innerFrameBottom),
                    painter.paint)
        }

        // Draw line
        painter.prepare(color = line.color, style = Paint.Style.STROKE, strokeWidth = line.strokeWidth)
        canvas!!.drawPath(linePath, painter.paint)
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

    private fun createBackgroundPath(
        path: Path,
        points: MutableList<ChartEntry>,
        innerFrameBottom: Float
    ): Path {

        val res = Path(path)

        res.lineTo(points.last().x, innerFrameBottom)
        res.lineTo(points.first().x, innerFrameBottom)
        res.close()

        return res
    }

    /**
     * Credits: http://www.jayway.com/author/andersericsson/
     */
    private fun si(setSize: Int, i: Int): Int {
        return when {
            i > setSize - 1 -> setSize - 1
            i < 0 -> 0
            else -> i
        }
    }
}