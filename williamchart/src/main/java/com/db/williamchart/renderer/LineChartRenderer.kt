package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY
import com.db.williamchart.extensions.toDataPoints
import com.db.williamchart.extensions.toLabels
import com.db.williamchart.extensions.toScale

class LineChartRenderer(
    private val view: ChartContract.View,
    private val painter: Painter,
    private var animation: ChartAnimation
) : ChartContract.Renderer {

    private lateinit var data: List<DataPoint>

    private lateinit var axis: AxisType

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private var labelsSize: Float = notInitialized

    private val xLabels: List<Label> by lazy {
        data.toLabels()
    }

    private val yLabels by lazy {
        val scale = data.toScale()
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

        val longestChartLabel =
            yLabels.maxBy { painter.measureLabelWidth(it.label, labelsSize) }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasurePaddingsNeeded()(
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

        placeLabelsX(innerFrame)
        placeLabelsY(innerFrame)

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
            view.drawDebugFrame(
                outerFrame,
                innerFrame,
                DebugWithLabelsFrame()(
                    painter = painter,
                    xLabels = xLabels,
                    yLabels = yLabels,
                    labelsSize = labelsSize
                )
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

    private fun placeLabelsX(chartFrame: Frame) {

        val labelsStartPosition: Float
        val labelsEndPosition: Float

        when {
            axis.shouldDisplayAxisX() -> {
                labelsStartPosition = chartFrame.left + painter.measureLabelWidth(xLabels.first().label, labelsSize) / 2
                labelsEndPosition = chartFrame.right - painter.measureLabelWidth(xLabels.last().label, labelsSize) / 2
            }
            else -> { // No axis
                labelsStartPosition = chartFrame.left
                labelsEndPosition = chartFrame.right
            }
        }

        val stepWidth = (labelsEndPosition - labelsStartPosition) / (xLabels.size - 1)
        val xLabelsVerticalPosition = chartFrame.bottom + painter.measureLabelHeight(labelsSize)

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = labelsStartPosition + stepWidth * index
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(chartFrame: Frame) {

        val screenStep = (chartFrame.bottom - chartFrame.top) / defaultScaleNumberOfSteps
        var screenCursor = chartFrame.bottom + painter.measureLabelHeight(labelsSize) / 2

        yLabels.forEach {
            it.screenPositionX = chartFrame.left - painter.measureLabelWidth(it.label, labelsSize) / 2
            it.screenPositionY = screenCursor
            screenCursor -= screenStep
        }
    }

    private fun placeDataPoints(
        frameTop: Float,
        frameBottom: Float
    ) {

        val scale = data.toScale()
        val scaleSize = scale.max - scale.min
        val frameHeight = frameBottom - frameTop

        data.forEachIndexed { index, entry ->
            entry.screenPositionX = xLabels[index].screenPositionX
            entry.screenPositionY = frameBottom - (frameHeight * (entry.value - scale.min) / scaleSize)
        }
    }

    companion object {
        private const val defaultScaleNumberOfSteps = 3
        private const val notInitialized = -1f
        private const val inDebug = false
    }
}
