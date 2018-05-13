package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultAnimation
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import java.lang.IllegalArgumentException


class ChartRenderer(private val view: ChartContract.View,
                    private val painter: Painter,
                    private var animation: ChartAnimation) : ChartContract.Renderer{

    private val defStepNumY = 3

    private var data : ChartSet? = null

    private var xLabels : List<ChartLabel> = arrayListOf()

    private var yLabels : List<ChartLabel> = arrayListOf()

    private var innerFrameLeft : Float = 0F

    private var innerFrameTop : Float = 0F

    private var innerFrameRight : Float = 0F

    private var innerFrameBottom : Float = 0F

    private var isProcessed : Boolean = false

    private var hasLabels : Boolean = true

    private var labelsSize : Float = 60F


    override fun preDraw(width: Int,
                         height: Int,
                         paddingLeft: Int,
                         paddingTop: Int,
                         paddingRight: Int,
                         paddingBottom: Int,
                         hasLabels: Boolean,
                         labelsSize: Float): Boolean {

        if (isProcessed) return true  // Data already processed, proceed with drawing

        if (data == null) return false  // No data, cancel drawing
        if (data!!.entries.size <= 1) throw IllegalArgumentException("A chart needs more than one entry.")

        val frameLeft = paddingLeft.toFloat()
        val frameTop = paddingTop.toFloat()
        val frameRight = width - paddingRight.toFloat()
        val frameBottom = height - paddingBottom.toFloat()

        this.hasLabels = hasLabels
        this.labelsSize = labelsSize

        xLabels = defineX()
        yLabels = defineY()

        val (pLeft, pTop, pRight, pBottom) = // Measure and negotiate padding needs of each axis
                negotiatePaddingsXY(measurePaddingsX(), measurePaddingsY())

        innerFrameLeft = frameLeft + pLeft
        innerFrameTop = frameTop + pTop
        innerFrameRight = frameRight - pRight
        innerFrameBottom = frameBottom - pBottom

        disposeX(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom)
        disposeY(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom)

        processEntries(innerFrameTop, innerFrameBottom)

        animation.animateFrom(innerFrameBottom, data!!.entries) { view.postInvalidate() }

        isProcessed = true

        return false
    }

    override fun draw() {

        if (data == null) return

        if (hasLabels) {
            view.drawLabels(xLabels)
            view.drawLabels(yLabels)
        }

        view.drawData(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom, data!!)
    }

    override fun show() {
        view.postInvalidate()
    }

    override fun animate(animation: ChartAnimation) {
        this.animation = animation
        view.postInvalidate()
    }

    override fun add(set: ChartSet) {
        data = set
    }


    private fun measurePaddingsX() : FloatArray {

        if (!hasLabels) return floatArrayOf(0F, 0F, 0F, 0F)

        return floatArrayOf(
                painter.measureLabelWidth(xLabels.first().label, labelsSize) / 2,
                0F,
                painter.measureLabelWidth(xLabels.last().label, labelsSize) / 2,
                painter.measureLabelHeight(labelsSize))
    }

    private fun measurePaddingsY() : FloatArray {

        if (!hasLabels) return floatArrayOf(0F, 0F, 0F, 0F)

        val longestChartLabel = yLabels.maxBy { painter.measureLabelWidth(it.label, labelsSize) }

        return floatArrayOf(
                if (longestChartLabel != null) painter.measureLabelWidth(longestChartLabel.label, labelsSize) else 0F,
                painter.measureLabelHeight(labelsSize) / 2,
                0F,
                painter.measureLabelHeight(labelsSize) / 2)
    }

    private fun defineX(): List<ChartLabel> {
        return data!!.entries.map{ ChartLabel(it.label, 0F, 0F) }
    }

    private fun defineY(): List<ChartLabel> {

        val tmp : MutableList<ChartLabel> = mutableListOf()

        val (min, max) = findBorderValues(data!!.entries)
        val valuesStep = (max - min) / defStepNumY
        var valuesCursor = min

        for (n in 0..defStepNumY) {
            tmp.add(ChartLabel(valuesCursor.toString(), 0F, 0F))
            valuesCursor += valuesStep
        }

        return tmp.toList()
    }

    private fun disposeX(
            chartLeft: Float,
            chartTop: Float,
            chartRight: Float,
            chartBottom: Float) {

        val stepX = (chartRight - chartLeft) / (xLabels.size - 1)

        xLabels.forEachIndexed { index, label ->
            label.x = chartLeft + stepX * index
            label.y = chartBottom + painter.measureLabelHeight(labelsSize)
        }
    }

    private fun disposeY(
            chartLeft: Float,
            chartTop: Float,
            chartRight: Float,
            chartBottom: Float) {

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
            frameBottom: Float) {

        val (min, max) = findBorderValues(data!!.entries)

        data!!.entries.forEachIndexed { index, entry ->
            entry.x = xLabels[index].x
            entry.y = frameBottom - ((frameBottom - frameTop) * (entry.value - min) / (max - min))
        }
    }

    private fun findBorderValues(entries: MutableList<ChartEntry>): FloatArray {

        var max = Integer.MIN_VALUE.toFloat()
        var min = Integer.MAX_VALUE.toFloat()

        entries.forEach {
            if (it.value >= max) max = it.value
            if (it.value <= min) min = it.value
        }

        if (min == max) max += 1f  // All given values are equal

        return floatArrayOf(min, max)
    }

    private fun negotiatePaddingsXY(paddingsX: FloatArray, paddingsY: FloatArray): FloatArray {
        return floatArrayOf(
                maxOf(paddingsX[0], paddingsY[0]),
                maxOf(paddingsX[1], paddingsY[1]),
                maxOf(paddingsX[2], paddingsY[2]),
                maxOf(paddingsX[3], paddingsY[3]))
    }

}