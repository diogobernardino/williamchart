package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import com.db.williamchart.view.ChartView.Axis
import java.lang.IllegalArgumentException

class ChartRenderer(
    private val view: ChartContract.View,
    private val painter: Painter,
    private var animation: ChartAnimation
) : ChartContract.Renderer {

    private val defStepNumY = 3

    private var data: ChartSet? = null

    private var xLabels: List<ChartLabel> = arrayListOf()

    private var yLabels: List<ChartLabel> = arrayListOf()

    private var innerFrameLeft: Float = 0F

    private var innerFrameTop: Float = 0F

    private var innerFrameRight: Float = 0F

    private var innerFrameBottom: Float = 0F

    private var isProcessed: Boolean = false

    private var axis: Axis = Axis.XY

    private var labelsSize: Float = 60F

    internal var xPacked = false

    internal var yAtZero = false

    override fun preDraw(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        axis: Axis,
        labelsSize: Float
    ): Boolean {

        if (isProcessed) return true // Data already processed, proceed with drawing

        if (data == null) return false // No data, cancel drawing
        if (data!!.entries.size <= 1) throw IllegalArgumentException("A chart needs more than one entry.")

        val frameLeft = paddingLeft.toFloat()
        val frameTop = paddingTop.toFloat()
        val frameRight = width - paddingRight.toFloat()
        val frameBottom = height - paddingBottom.toFloat()

        this.axis = axis
        this.labelsSize = labelsSize

        xLabels = defineX()
        yLabels = defineY()

        val paddings = negotiatePaddingsXY(measurePaddingsX(), measurePaddingsY())

        innerFrameLeft = frameLeft + paddings.left
        innerFrameTop = frameTop + paddings.top
        innerFrameRight = frameRight - paddings.right
        innerFrameBottom = frameBottom - paddings.bottom

        processX(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom)
        processY(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom)

        processEntries(innerFrameTop, innerFrameBottom)

        animation.animateFrom(innerFrameBottom, data!!.entries) { view.postInvalidate() }

        isProcessed = true

        return false
    }

    override fun draw() {

        if (data == null) return

        if (axis == Axis.XY || axis == Axis.X) view.drawLabels(xLabels)
        if (axis == Axis.XY || axis == Axis.Y) view.drawLabels(yLabels)

        view.drawData(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom, data!!)
    }

    override fun render() {
        view.postInvalidate()
    }

    override fun anim(animation: ChartAnimation) {
        this.animation = animation
        view.postInvalidate()
    }

    override fun add(set: ChartSet) {
        data = set
    }

    private fun measurePaddingsX(): Paddings {
        return if (axis != Axis.XY && axis != Axis.X) Paddings(0F, 0F, 0F, 0F)
        else Paddings(0F, 0F, 0f, painter.measureLabelHeight(labelsSize))
    }

    private fun measurePaddingsY(): Paddings {

        return if (axis != Axis.XY && axis != Axis.Y) return Paddings(0F, 0F, 0F, 0F)
        else {
            val longestChartLabel = yLabels.maxBy { painter.measureLabelWidth(it.label, labelsSize) }
            Paddings(
                    if (longestChartLabel != null) painter.measureLabelWidth(longestChartLabel.label, labelsSize) else 0F,
                    painter.measureLabelHeight(labelsSize) / 2,
                    0F,
                    painter.measureLabelHeight(labelsSize) / 2)
        }
    }

    private fun defineX(): List<ChartLabel> {
        return data!!.entries.map { ChartLabel(it.label, 0F, 0F) }
    }

    private fun defineY(): List<ChartLabel> {

        val borders = findBorderValues(data!!.entries)
        val valuesStep = (borders.max - borders.min) / defStepNumY

        return List(defStepNumY + 1, {
            val aux = borders.min + valuesStep * it
            ChartLabel(aux.toString(), 0F, 0F)
        })
    }

    private fun processX(
        chartLeft: Float,
        chartTop: Float,
        chartRight: Float,
        chartBottom: Float
    ) {

        val auxLeft: Float
        val auxRight: Float

        if (xPacked) { // Pack labels
            val entryWidth = (chartRight - chartLeft) / (xLabels.size)
            auxLeft = chartLeft + entryWidth / 2
            auxRight = chartRight - entryWidth / 2
        } else if (axis == Axis.XY || axis == Axis.X) {
            auxLeft = chartLeft + painter.measureLabelWidth(xLabels.first().label, labelsSize) / 2
            auxRight = chartRight - painter.measureLabelWidth(xLabels.last().label, labelsSize) / 2
        } else { // X not displayed
            auxLeft = chartLeft
            auxRight = chartRight
        }

        val stepX = (auxRight - auxLeft) / (xLabels.size - 1)
        xLabels.forEachIndexed { index, label ->
            label.x = auxLeft + stepX * index
            label.y = chartBottom + painter.measureLabelHeight(labelsSize)
        }
    }

    private fun processY(
        chartLeft: Float,
        chartTop: Float,
        chartRight: Float,
        chartBottom: Float
    ) {

        val screenStep = (chartBottom - chartTop) / defStepNumY
        var screenCursor = chartBottom + painter.measureLabelHeight(labelsSize) / 2

        yLabels.forEach {
            it.x = chartLeft - painter.measureLabelWidth(it.label, labelsSize) / 2
            it.y = screenCursor
            screenCursor -= screenStep
        }
    }

    private fun processEntries(
        frameTop: Float,
        frameBottom: Float
    ) {

        val borders = findBorderValues(data!!.entries)

        data!!.entries.forEachIndexed { index, entry ->
            entry.x = xLabels[index].x
            entry.y = frameBottom -
                    ((frameBottom - frameTop) * (entry.value - borders.min) /
                            (borders.max - borders.min))
        }
    }

    private fun findBorderValues(entries: MutableList<ChartEntry>): Borders {

        if (entries.isEmpty()) return Borders(0F, 1F)

        val values = entries.map { it.value }
        val min: Float = if (yAtZero) 0F else values.min()!!
        var max: Float = values.max()!!

        if (min == max) max += 1f // All given values are equal

        return Borders(min, max)
    }

    private fun negotiatePaddingsXY(paddingsX: Paddings, paddingsY: Paddings): Paddings {
        return Paddings(
                maxOf(paddingsX.left, paddingsY.left),
                maxOf(paddingsX.top, paddingsY.top),
                maxOf(paddingsX.right, paddingsY.right),
                maxOf(paddingsX.bottom, paddingsY.bottom))
    }
}

class Borders(val min: Float, val max: Float)

class Paddings(val left: Float, val top: Float, val right: Float, val bottom: Float)