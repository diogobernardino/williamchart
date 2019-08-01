package com.db.williamchart.view

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import androidx.annotation.Size
import com.db.williamchart.ChartContract
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.toRect
import com.db.williamchart.renderer.ChartRenderer

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr), ChartContract.View {

    /**
     * API
     */

    @Suppress("MemberVisibilityCanBePrivate")
    var smooth: Boolean = false

    @Suppress("MemberVisibilityCanBePrivate")
    var strokeWidth: Float = 4F

    @Suppress("MemberVisibilityCanBePrivate")
    var fillColor: Int = 0

    @Suppress("MemberVisibilityCanBePrivate")
    var lineColor: Int = -0x1000000 // Black as default

    @Size(min = 2, max = 2)
    @Suppress("MemberVisibilityCanBePrivate")
    var gradientFillColors: IntArray = intArrayOf(0, 0)

    init {
        renderer = ChartRenderer(this, painter, NoAnimation())
    }

    override fun drawData(
        innerFrame: Frame,
        entries: List<DataPoint>
    ) {

        val linePath =
            if (!smooth) createLinePath(entries)
            else createSmoothLinePath(entries)

        if (fillColor != 0 || gradientFillColors.isNotEmpty()) { // Draw background

            if (fillColor != 0)
                painter.prepare(color = fillColor, style = Paint.Style.FILL)
            else painter.prepare(
                shader = LinearGradient(
                    innerFrame.left,
                    innerFrame.top,
                    innerFrame.left,
                    innerFrame.bottom,
                    gradientFillColors[0],
                    gradientFillColors[1],
                    Shader.TileMode.MIRROR
                ),
                style = Paint.Style.FILL
            )

            canvas.drawPath(
                createBackgroundPath(linePath, entries, innerFrame.bottom),
                painter.paint
            )
        }

        // Draw line
        painter.prepare(color = lineColor, style = Paint.Style.STROKE, strokeWidth = strokeWidth)
        canvas.drawPath(linePath, painter.paint)
    }

    override fun drawLabels(xLabels: List<Label>) {

        painter.prepare(
            textSize = labelsSize,
            color = labelsColor,
            font = labelsFont
        )
        xLabels.forEach { canvas.drawText(it.label, it.screenPositionX, it.screenPositionY, painter.paint) }
    }

    override fun drawDebugFrame(outerFrame: Frame, innerFrame: Frame, labelsFrame: List<Frame>) {
        painter.prepare(color = -0x1000000, style = Paint.Style.STROKE)
        canvas.drawRect(outerFrame.toRect(), painter.paint)
        canvas.drawRect(innerFrame.toRect(), painter.paint)
        labelsFrame.forEach { canvas.drawRect(it.toRect(), painter.paint) }
    }

    private fun createLinePath(points: List<DataPoint>): Path {

        val res = Path()

        res.moveTo(points.first().screenPositionX, points.first().screenPositionY)
        for (i in 1 until points.size)
            res.lineTo(points[i].screenPositionX, points[i].screenPositionY)
        return res
    }

    /**
     * Credits: http://www.jayway.com/author/andersericsson/
     */
    private fun createSmoothLinePath(points: List<DataPoint>): Path {

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
        res.moveTo(points.first().screenPositionX, points.first().screenPositionY)

        for (i in 0 until points.size - 1) {

            thisPointX = points[i].screenPositionX
            thisPointY = points[i].screenPositionY

            nextPointX = points[i + 1].screenPositionX
            nextPointY = points[i + 1].screenPositionY

            startDiffX = nextPointX - points[si(points.size, i - 1)].screenPositionX
            startDiffY = nextPointY - points[si(points.size, i - 1)].screenPositionY

            endDiffX = points[si(points.size, i + 2)].screenPositionX - thisPointX
            endDiffY = points[si(points.size, i + 2)].screenPositionY - thisPointY

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
        points: List<DataPoint>,
        innerFrameBottom: Float
    ): Path {

        val res = Path(path)

        res.lineTo(points.last().screenPositionX, innerFrameBottom)
        res.lineTo(points.first().screenPositionX, innerFrameBottom)
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