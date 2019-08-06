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
import com.db.williamchart.renderer.executor.DebugWithLabelsFrame
import com.db.williamchart.renderer.executor.MeasureHorizontalBarChartPaddings

class HorizontalBarChartRenderer(
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

    private val yLabels by lazy {
        data.toLabels()
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

        val yLongestChartLabel = yLabels.maxBy { painter.measureLabelWidth(it.label, labelsSize) }
            ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureHorizontalBarChartPaddings()(
            axisType = axis,
            labelsHeight = painter.measureLabelHeight(labelsSize),
            xLastLabelWidth = painter.measureLabelWidth(xLabels.last().label, labelsSize),
            yLongestLabelWidth = painter.measureLabelWidth(yLongestChartLabel.label, labelsSize),
            yLabelsPadding = yLabelsPadding
        )

        innerFrame = Frame(
            left = outerFrame.left + paddings.left,
            top = outerFrame.top + paddings.top,
            right = outerFrame.right - paddings.right,
            bottom = outerFrame.bottom - paddings.bottom
        )

        if (axis.shouldDisplayAxisX())
            placeLabelsX(innerFrame)

        if (axis.shouldDisplayAxisY())
            placeLabelsY(outerFrame, innerFrame)

        placeDataPoints(innerFrame)

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

    override fun render(entries: LinkedHashMap<String, Float>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(entries: LinkedHashMap<String, Float>, animation: ChartAnimation) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
    }

    private fun placeLabelsX(chartFrame: Frame) {

        val widthBetweenLabels = (chartFrame.right - chartFrame.left) / defaultScaleNumberOfSteps
        val xLabelsVerticalPosition = chartFrame.bottom + painter.measureLabelHeight(labelsSize)

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = chartFrame.left + widthBetweenLabels * index
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(outerFrame: Frame, chartFrame: Frame) {

        val halfBarWidth = (chartFrame.bottom - chartFrame.top) / yLabels.size / 2
        val halfLabelHeight = painter.measureLabelHeight(labelsSize) / 2
        val labelsTopPosition = chartFrame.top + halfBarWidth
        val labelsBottomPosition = chartFrame.bottom - halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                outerFrame.left +
                    painter.measureLabelWidth(label.label, labelsSize) / 2
            label.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index +
                    halfLabelHeight // Because text is drawn from bottom
        }
    }

    private fun placeDataPoints(chartFrame: Frame) {

        val scale = Scale(min = 0F, max = data.limits().second)
        val scaleSize = scale.max - scale.min
        val chartWidth = chartFrame.right - chartFrame.left
        val halfBarWidth = (chartFrame.bottom - chartFrame.top) / yLabels.size / 2
        val labelsBottomPosition = chartFrame.bottom - halfBarWidth
        val labelsTopPosition = chartFrame.top + halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX =
                chartFrame.left +
                    (chartWidth * (dataPoint.value - scale.min) / scaleSize)
            dataPoint.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index
        }
    }
}
