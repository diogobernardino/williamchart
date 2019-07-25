package com.db.williamchart.view

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import androidx.annotation.Size
import com.db.williamchart.data.ChartEntry

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr) {

    var smooth: Boolean = false

    var strokeWidth: Float = 4F

    var fillColor: Int = 0

    var lineColor: Int = -0x1000000 // Black as default

    @Size(min = 2, max = 2)
    var gradientFillColors: IntArray = intArrayOf(0, 0)

    override fun drawData(
        innerFrameLeft: Float,
        innerFrameTop: Float,
        innerFrameRight: Float,
        innerFrameBottom: Float,
        entries: List<ChartEntry>
    ) {

        val linePath =
            if (!smooth) createLinePath(entries)
            else createSmoothLinePath(entries)

        if (fillColor != 0 || gradientFillColors.isNotEmpty()) { // Draw background

            if (fillColor != 0)
                painter.prepare(color = fillColor, style = Paint.Style.FILL)
            else painter.prepare(
                shader = LinearGradient(
                    innerFrameLeft,
                    innerFrameTop,
                    innerFrameLeft,
                    innerFrameBottom,
                    gradientFillColors[0],
                    gradientFillColors[1],
                    Shader.TileMode.MIRROR
                ),
                style = Paint.Style.FILL
            )

            canvas.drawPath(
                createBackgroundPath(linePath, entries, innerFrameBottom),
                painter.paint
            )
        }

        // Draw line
        painter.prepare(color = lineColor, style = Paint.Style.STROKE, strokeWidth = strokeWidth)
        canvas.drawPath(linePath, painter.paint)
    }

    private fun createLinePath(points: List<ChartEntry>): Path {

        val res = Path()

        res.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size)
            res.lineTo(points[i].x, points[i].y)
        return res
    }

    /**
     * Credits: http://www.jayway.com/author/andersericsson/
     */
    private fun createSmoothLinePath(points: List<ChartEntry>): Path {

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
            endDiffY = points[si(points.size, i + 2)].y - thisPointY

            firstControlX = thisPointX + SMOOTH_FACTOR * startDiffX
            firstControlY = thisPointY + SMOOTH_FACTOR * startDiffY

            secondControlX = nextPointX - SMOOTH_FACTOR * endDiffX
            secondControlY = nextPointY - SMOOTH_FACTOR * endDiffY

            res.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, nextPointX, nextPointY)
        }

        return res
    }

    private fun createBackgroundPath(
        path: Path,
        points: List<ChartEntry>,
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

    companion object {
        private const val SMOOTH_FACTOR = 0.20f
    }
}