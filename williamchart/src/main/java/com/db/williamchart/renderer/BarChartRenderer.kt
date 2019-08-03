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
import com.db.williamchart.extensions.toLabels

class BarChartRenderer(
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
        val scale = Scale(min = 0F, max = data.limits().second)
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

        val halfBarWidth = (chartFrame.right - chartFrame.left) / xLabels.size / 2
        val labelsLeftPosition = chartFrame.left + halfBarWidth
        val labelsRightPosition = chartFrame.right - halfBarWidth
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)
        val xLabelsVerticalPosition = chartFrame.bottom + painter.measureLabelHeight(labelsSize)

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(chartFrame: Frame) {

        val heightBetweenLabels = (chartFrame.bottom - chartFrame.top) / defaultScaleNumberOfSteps
        val labelsBottomPosition = chartFrame.bottom + painter.measureLabelHeight(labelsSize) / 2

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX = chartFrame.left - painter.measureLabelWidth(label.label, labelsSize) / 2
            label.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun placeDataPoints(
        chartTop: Float,
        chartBottom: Float
    ) {

        val scale = Scale(min = 0F, max = data.limits().second)
        val scaleSize = scale.max - scale.min
        val chartHeight = chartBottom - chartTop

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = xLabels[index].screenPositionX
            dataPoint.screenPositionY =
                chartBottom -
                    (chartHeight * (dataPoint.value - scale.min) / scaleSize)
        }
    }
}
