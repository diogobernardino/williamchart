package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.Scale
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY
import com.db.williamchart.extensions.limits
import com.db.williamchart.extensions.toDataPoints

class ChartRenderer(
    private val view: ChartContract.View,
    private val painter: Painter,
    private var animation: ChartAnimation
) : ChartContract.Renderer {

    private lateinit var data: List<DataPoint>

    private lateinit var axis: AxisType

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private var labelsSize: Float = notInitialized

    internal var xPacked = false

    internal var yAtZero = false

    private val xLabels: List<Label> by lazy {
        data.map {
            Label(
                label = it.label,
                screenPositionX = 0f,
                screenPositionY = 0f
            )
        }
    }

    private val yLabels by lazy {
        val scale = findBorderValues(data, yAtZero)
        val scaleStep = (scale.max - scale.min) / defaultScaleNumberOfSteps

        List(defaultScaleNumberOfSteps + 1) {
            val scaleValue = scale.min + scaleStep * it
            Label(
                label = scaleValue.toString(),
                screenPositionX = 0F,
                screenPositionY = 0F
            )
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

        if (this.labelsSize != notInitialized) // Data already processed, proceed with drawing
            return true

        if (data.size <= 1)
            throw IllegalArgumentException("A chart needs more than one entry.")

        this.axis = axis
        this.labelsSize = labelsSize

        outerFrame = Frame(
            left = paddingLeft.toFloat(),
            top = paddingTop.toFloat(),
            right = width - paddingRight.toFloat(),
            bottom = height - paddingBottom.toFloat()
        )

        val longestChartLabel = yLabels.maxBy { painter.measureLabelWidth(it.label, labelsSize) }
            ?: throw IllegalArgumentException("A chart needs more than one entry.")

        val paddings = MeasurePaddingsNeeded().invoke(
            axisType = axis,
            labelsHeight = painter.measureLabelHeight(labelsSize),
            longestLabelWidth = painter.measureLabelWidth(longestChartLabel.label, labelsSize)
        )

        innerFrame = Frame(
            left = outerFrame.left + paddings.left,
            top = outerFrame.top + paddings.top,
            right = outerFrame.right - paddings.right,
            bottom = outerFrame.bottom - paddings.bottom
        )

        placeLabelsX(innerFrame.left, innerFrame.top, innerFrame.right, innerFrame.bottom)
        placeLabelsY(innerFrame.left, innerFrame.top, innerFrame.right, innerFrame.bottom)

        placeDataPoints(innerFrame.top, innerFrame.bottom)

        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (axis.shouldDisplayAxisX())
            view.drawLabels(xLabels)

        if (axis.shouldDisplayAxisY())
            view.drawLabels(yLabels)

        view.drawData(innerFrame, data)

        if (inDebug) {
            val labelsFrame = getLabelsFrameForDebug()
            view.drawDebugFrame(outerFrame, innerFrame, labelsFrame)
        }
    }

    private fun getLabelsFrameForDebug(): List<Frame> {
        val labelHeight = painter.measureLabelHeight(labelsSize)
        return xLabels.map {
            val labelHalftWidth = painter.measureLabelWidth(it.label, labelsSize) / 2
            Frame(
                left = it.screenPositionX - labelHalftWidth,
                top = it.screenPositionY - labelHeight,
                right = it.screenPositionX + labelHalftWidth,
                bottom = it.screenPositionY
            )
        } + yLabels.map {
            val labelHalftWidth = painter.measureLabelWidth(it.label, labelsSize) / 2
            Frame(
                left = it.screenPositionX - labelHalftWidth,
                top = it.screenPositionY - labelHeight,
                right = it.screenPositionX + labelHalftWidth,
                bottom = it.screenPositionY
            )
        }
    }

    override fun render(entries: HashMap<String, Float>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(entries: HashMap<String, Float>, animation: ChartAnimation) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
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
            label.screenPositionX = labelsStartPosition + stepWidth * index
            label.screenPositionY = xLabelsVerticalPosition
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
            it.screenPositionX = chartLeft - painter.measureLabelWidth(it.label, labelsSize) / 2
            it.screenPositionY = screenCursor
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
            entry.screenPositionX = xLabels[index].screenPositionX
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
        private const val notInitialized = -1f
        private const val inDebug = false
    }
}
