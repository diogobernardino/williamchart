package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.Scale
import com.db.williamchart.data.LineChartConfiguration
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY
import com.db.williamchart.data.toOuterFrame
import com.db.williamchart.data.withPaddings
import com.db.williamchart.extensions.maxValueBy
import com.db.williamchart.extensions.toDataPoints
import com.db.williamchart.extensions.toLabels
import com.db.williamchart.extensions.toScale
import com.db.williamchart.renderer.executor.DebugWithLabelsFrame
import com.db.williamchart.renderer.executor.MeasureLineChartPaddings

class LineChartRenderer(
    private val view: ChartContract.LineView,
    private val painter: Painter,
    private var animation: ChartAnimation
) : ChartContract.Renderer {

    private var isAlreadyRendered = false

    private lateinit var data: List<DataPoint>

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: LineChartConfiguration

    internal var scale: Scale? = null

    private val xLabels: List<Label> by lazy {
        data.toLabels()
    }

    private val yLabels by lazy {
        val scale = scale ?: data.toScale()
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

    override fun preDraw(configuration: ChartConfiguration): Boolean {
        require(data.size > 1) { "A chart needs more than one entry." }

        if (isAlreadyRendered) // Data already processed, proceed with drawing
            return true

        this.chartConfiguration = configuration as LineChartConfiguration

        val longestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureLineChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            longestLabelWidth = longestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart,
            lineThickness = chartConfiguration.lineThickness,
            pointsDrawableWidth = chartConfiguration.pointsDrawableWidth,
            pointsDrawableHeight = chartConfiguration.pointsDrawableHeight
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        if (chartConfiguration.axis.shouldDisplayAxisX())
            placeLabelsX(innerFrame)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            placeLabelsY(innerFrame)

        placeDataPoints(innerFrame)

        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

        isAlreadyRendered = true
        return false
    }

    override fun draw() {

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabels(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabels(yLabels)

        if (chartConfiguration.fillColor != 0 ||
            chartConfiguration.gradientFillColors.isNotEmpty()
        )
            view.drawLineBackground(innerFrame, data)

        view.drawLine(data)
        view.drawPoints(data)

        if (RendererConstants.inDebug) {
            view.drawDebugFrame(
                outerFrame,
                innerFrame,
                DebugWithLabelsFrame()(
                    painter = painter,
                    axisType = chartConfiguration.axis,
                    xLabels = xLabels,
                    yLabels = yLabels,
                    labelsSize = chartConfiguration.labelsSize
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

        val labelsLeftPosition =
            innerFrame.left +
                painter.measureLabelWidth(xLabels.first().label, chartConfiguration.labelsSize) / 2
        val labelsRightPosition =
            innerFrame.right -
                painter.measureLabelWidth(xLabels.last().label, chartConfiguration.labelsSize) / 2
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                painter.measureLabelAscent(chartConfiguration.labelsSize) +
                RendererConstants.labelsPaddingToInnerChart

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(innerFrame: Frame) {

        val heightBetweenLabels =
            (innerFrame.bottom - innerFrame.top) / RendererConstants.defaultScaleNumberOfSteps
        val labelsBottomPosition =
            innerFrame.bottom + painter.measureLabelHeight(chartConfiguration.labelsSize) / 2

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                innerFrame.left -
                    RendererConstants.labelsPaddingToInnerChart -
                    painter.measureLabelWidth(label.label, chartConfiguration.labelsSize) / 2
            label.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scale = scale ?: data.toScale()
        val scaleSize = scale.max - scale.min
        val chartHeight = innerFrame.bottom - innerFrame.top
        val widthBetweenLabels = (innerFrame.right - innerFrame.left) / (xLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = innerFrame.left + (widthBetweenLabels * index)
            dataPoint.screenPositionY =
                innerFrame.bottom -
                    (chartHeight * (dataPoint.value - scale.min) / scaleSize)
        }
    }
}
