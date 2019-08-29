package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.Scale
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY
import com.db.williamchart.data.toOuterFrame
import com.db.williamchart.data.withPaddings
import com.db.williamchart.extensions.limits
import com.db.williamchart.extensions.maxValueBy
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

    private var labelsSize: Float = RendererConstants.notInitialized

    private val xLabels: List<Label> by lazy {
        val scale = Scale(min = 0F, max = data.limits().second)
        val scaleStep = (scale.max - scale.min) / RendererConstants.defaultScaleNumberOfSteps

        List(RendererConstants.defaultScaleNumberOfSteps + 1) {
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

    override fun preDraw(chartConfiguration: ChartConfiguration): Boolean {
        require(data.size > 1) { "A chart needs more than one entry." }

        if (this.labelsSize != RendererConstants.notInitialized) // Data already processed, proceed with drawing
            return true

        this.axis = chartConfiguration.axis
        this.labelsSize = chartConfiguration.labelsSize

        val yLongestChartLabelWidth =
            yLabels.maxValueBy { painter.measureLabelWidth(it.label, labelsSize) }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureHorizontalBarChartPaddings()(
            axisType = axis,
            labelsHeight = painter.measureLabelHeight(labelsSize),
            xLastLabelWidth = painter.measureLabelWidth(xLabels.last().label, labelsSize),
            yLongestLabelWidth = yLongestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

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

        if (RendererConstants.inDebug) {
            view.drawDebugFrame(
                outerFrame,
                innerFrame,
                DebugWithLabelsFrame()(
                    painter = painter,
                    axisType = axis,
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

    private fun placeLabelsX(innerFrame: Frame) {

        val widthBetweenLabels =
            (innerFrame.right - innerFrame.left) / RendererConstants.defaultScaleNumberOfSteps
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                painter.measureLabelAscent(labelsSize) +
                RendererConstants.labelsPaddingToInnerChart

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = innerFrame.left + widthBetweenLabels * index
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(outerFrame: Frame, innerFrame: Frame) {

        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                outerFrame.left +
                    painter.measureLabelWidth(label.label, labelsSize) / 2
            label.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index +
                    painter.measureLabelDescent(labelsSize)
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scale = Scale(min = 0F, max = data.limits().second)
        val scaleSize = scale.max - scale.min
        val chartWidth = innerFrame.right - innerFrame.left
        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX =
                innerFrame.left +
                    (chartWidth * (dataPoint.value - scale.min) / scaleSize)
            dataPoint.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index
        }
    }
}
