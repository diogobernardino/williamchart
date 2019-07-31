package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Label
import com.db.williamchart.data.Paddings
import com.db.williamchart.data.Scale
import com.db.williamchart.data.mergeWith
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY
import com.db.williamchart.extensions.limits

class ChartRenderer(
    private val view: ChartContract.View,
    private val painter: Painter,
    private var animation: ChartAnimation
) : ChartContract.Renderer {

    private lateinit var data: List<DataPoint>

    private lateinit var axis: AxisType

    private var innerFrameLeft: Float = -1f

    private var innerFrameTop: Float = -1f

    private var innerFrameRight: Float = -1f

    private var innerFrameBottom: Float = -1f

    private var labelsSize: Float = -1f

    private var isProcessed: Boolean = false

    internal var xPacked = false

    internal var yAtZero = false

    private val xLabels: List<Label> by lazy {
        data.map { Label(it.label, -1F, -1F) }
    }

    private val yLabels by lazy {
        val scale = findBorderValues(data, yAtZero)
        val scaleStep = (scale.max - scale.min) / defaultScaleNumberOfSteps

        List(defaultScaleNumberOfSteps + 1) {
            val scaleValue = scale.min + scaleStep * it
            Label(scaleValue.toString(), -1F, -1F)
        }
    }

    override fun preDraw(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        axis: AxisType,
        labelsSize: Float
    ): Boolean {

        if (isProcessed) // Data already processed, proceed with drawing
            return true

        if (data.size <= 1)
            throw IllegalArgumentException("A chart needs more than one entry.")

        this.axis = axis
        this.labelsSize = labelsSize

        val frameLeft = paddingLeft.toFloat()
        val frameTop = paddingTop.toFloat()
        val frameRight = width - paddingRight.toFloat()
        val frameBottom = height - paddingBottom.toFloat()

        val paddings = measurePaddingsX().mergeWith(measurePaddingsY())

        innerFrameLeft = frameLeft + paddings.left
        innerFrameTop = frameTop + paddings.top
        innerFrameRight = frameRight - paddings.right
        innerFrameBottom = frameBottom - paddings.bottom

        placeLabelsX(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom)
        placeLabelsY(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom)

        placeDataPoints(innerFrameTop, innerFrameBottom)

        animation.animateFrom(innerFrameBottom, data) { view.postInvalidate() }

        isProcessed = true

        return false
    }

    override fun draw() {

        if (axis.shouldDisplayAxisX()) view.drawLabels(xLabels)
        if (axis.shouldDisplayAxisY()) view.drawLabels(yLabels)

        view.drawData(innerFrameLeft, innerFrameTop, innerFrameRight, innerFrameBottom, data)
    }

    override fun render(entries: HashMap<String, Float>) {
        add(entries)
        view.postInvalidate()
    }

    override fun anim(entries: HashMap<String, Float>, animation: ChartAnimation) {
        add(entries)
        this.animation = animation
        view.postInvalidate()
    }

    private fun add(entries: HashMap<String, Float>) {
        data = entries.map {
            DataPoint(
                label = it.key,
                value = it.value,
                screenPositionX = 0f,
                screenPositionY = 0f
            )
        }
    }

    private fun measurePaddingsX(): Paddings {
        return Paddings(
            left = 0F,
            top = 0F,
            right = 0f,
            bottom = if (axis.shouldDisplayAxisX()) painter.measureLabelHeight(labelsSize) else 0F
        )
    }

    private fun measurePaddingsY(): Paddings {

        if (!axis.shouldDisplayAxisY())
            return Paddings(0F, 0F, 0F, 0F)

        val longestChartLabel = yLabels.maxBy { painter.measureLabelWidth(it.label, labelsSize) }
        return Paddings(
            left = if (longestChartLabel != null)
                painter.measureLabelWidth(
                    longestChartLabel.label,
                    labelsSize
                ) else 0F,
            top = painter.measureLabelHeight(labelsSize) / 2,
            right = 0F,
            bottom = painter.measureLabelHeight(labelsSize) / 2
        )
    }

    private fun placeLabelsX(
        chartLeft: Float,
        chartTop: Float,
        chartRight: Float,
        chartBottom: Float
    ) {

        val labelsStartPosition: Float
        val labelsEndPosition: Float

        when {
            xPacked -> { // Pack labels - used in Bar charts
                val barWidth = (chartRight - chartLeft) / (xLabels.size)
                labelsStartPosition = chartLeft + barWidth / 2
                labelsEndPosition = chartRight - barWidth / 2
            }
            axis.shouldDisplayAxisX() -> {
                labelsStartPosition = chartLeft + painter.measureLabelWidth(xLabels.first().label, labelsSize) / 2
                labelsEndPosition = chartRight - painter.measureLabelWidth(xLabels.last().label, labelsSize) / 2
            }
            else -> { // No axis
                labelsStartPosition = chartLeft
                labelsEndPosition = chartRight
            }
        }

        val stepWidth = (labelsEndPosition - labelsStartPosition) / (xLabels.size - 1)
        val xLabelsVerticalPosition = chartBottom + painter.measureLabelHeight(labelsSize)

        xLabels.forEachIndexed { index, label ->
            label.x = labelsStartPosition + stepWidth * index
            label.y = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(
        chartLeft: Float,
        chartTop: Float,
        chartRight: Float,
        chartBottom: Float
    ) {

        val screenStep = (chartBottom - chartTop) / defaultScaleNumberOfSteps
        var screenCursor = chartBottom + painter.measureLabelHeight(labelsSize) / 2

        yLabels.forEach {
            it.x = chartLeft - painter.measureLabelWidth(it.label, labelsSize) / 2
            it.y = screenCursor
            screenCursor -= screenStep
        }
    }

    private fun placeDataPoints(
        frameTop: Float,
        frameBottom: Float
    ) {

        val scale = findBorderValues(data, yAtZero)
        val scaleSize = scale.max - scale.min
        val frameHeight = frameBottom - frameTop

        data.forEachIndexed { index, entry ->
            entry.screenPositionX = xLabels[index].x
            entry.screenPositionY = frameBottom - (frameHeight * (entry.value - scale.min) / scaleSize)
        }
    }

    private fun findBorderValues(entries: List<DataPoint>, startScaleAtZero: Boolean): Scale {
        val limits = entries.limits()
        return Scale(
            min = if (startScaleAtZero) 0F else limits.first,
            max = limits.second
        )
    }

    companion object {
        private const val defaultScaleNumberOfSteps = 3
    }
}
