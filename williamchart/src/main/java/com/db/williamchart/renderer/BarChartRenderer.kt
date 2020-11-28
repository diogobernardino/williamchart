package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.configuration.BarChartConfiguration
import com.db.williamchart.data.configuration.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label
import com.db.williamchart.data.notInitialized
import com.db.williamchart.data.shouldDisplayAxisX
import com.db.williamchart.data.shouldDisplayAxisY
import com.db.williamchart.data.configuration.toOuterFrame
import com.db.williamchart.data.contains
import com.db.williamchart.data.withPaddings
import com.db.williamchart.extensions.maxValueBy
import com.db.williamchart.extensions.toBarScale
import com.db.williamchart.extensions.toDataPoints
import com.db.williamchart.extensions.toLabels
import com.db.williamchart.renderer.executor.DebugWithLabelsFrame
import com.db.williamchart.renderer.executor.DefineVerticalBarsClickableFrames
import com.db.williamchart.renderer.executor.GetVerticalBarBackgroundFrames
import com.db.williamchart.renderer.executor.GetVerticalBarFrames
import com.db.williamchart.renderer.executor.MeasureBarChartPaddings

class BarChartRenderer(
    private val view: ChartContract.BarView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private var zeroPositionY: Float = 0.0f

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: BarChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        chartConfiguration = configuration as BarChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration = chartConfiguration.copy(scale = data.toBarScale())

        xLabels = data.toLabels()
        val scaleStep = chartConfiguration.scale.size / RendererConstants.defaultScaleNumberOfSteps
        yLabels = List(RendererConstants.defaultScaleNumberOfSteps + 1) {
            val scaleValue = chartConfiguration.scale.min + scaleStep * it
            Label(
                label = chartConfiguration.labelsFormatter(scaleValue),
                screenPositionX = 0F,
                screenPositionY = 0F
            )
        }

        val longestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureBarChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            longestLabelWidth = longestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        zeroPositionY =
            processZeroPositionY(
                innerTop = innerFrame.top,
                innerBottom = innerFrame.bottom,
                scaleRange = chartConfiguration.scale.size
            )

        placeLabelsX(innerFrame)
        placeLabelsY(innerFrame)
        placeDataPoints(innerFrame, zeroPositionY)

        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data.isEmpty()) return

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabels(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabels(yLabels)

        view.drawGrid(
            innerFrame,
            xLabels.map { it.screenPositionX },
            yLabels.map { it.screenPositionY }
        )

        if (chartConfiguration.barsBackgroundColor != -1)
            view.drawBarsBackground(
                GetVerticalBarBackgroundFrames()(
                    innerFrame,
                    chartConfiguration.barsSpacing,
                    data
                )
            )

        view.drawBars(
            GetVerticalBarFrames()(
                innerFrame,
                zeroPositionY,
                chartConfiguration.barsSpacing,
                data
            )
        )

        if (RendererConstants.inDebug) {
            view.drawDebugFrame(
                listOf(outerFrame, innerFrame) +
                    DebugWithLabelsFrame()(
                        painter = painter,
                        axisType = chartConfiguration.axis,
                        xLabels = xLabels,
                        yLabels = yLabels,
                        labelsSize = chartConfiguration.labelsSize
                    ) +
                    DefineVerticalBarsClickableFrames()(
                        innerFrame,
                        data.map { Pair(it.screenPositionX, it.screenPositionY) }
                    ) +
                    Frame(innerFrame.left, zeroPositionY, innerFrame.right, zeroPositionY)
            )
        }
    }

    override fun render(entries: List<Pair<String, Float>>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(entries: List<Pair<String, Float>>, animation: ChartAnimation<DataPoint>) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
    }

    override fun processClick(x: Float?, y: Float?): Triple<Int, Float, Float> {

        if (x == null || y == null || data.isEmpty())
            return Triple(-1, -1f, -1f)

        val index =
            DefineVerticalBarsClickableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) }
            )
                .indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> = processClick(x, y)

    private fun placeLabelsX(innerFrame: Frame) {

        val halfBarWidth = (innerFrame.right - innerFrame.left) / xLabels.size / 2
        val labelsLeftPosition = innerFrame.left + halfBarWidth
        val labelsRightPosition = innerFrame.right - halfBarWidth
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

    private fun placeDataPoints(
        innerFrame: Frame,
        zeroPositionY: Float
    ) {
        // Chart upper part with positive points
        val positiveHeight = zeroPositionY - innerFrame.top
        val positiveScale = chartConfiguration.scale.max

        // Chart bottom part with negative points
        val negativeHeight = innerFrame.bottom - zeroPositionY
        val negativeScale = chartConfiguration.scale.min

        val halfBarWidth = (innerFrame.right - innerFrame.left) / xLabels.size / 2
        val labelsLeftPosition = innerFrame.left + halfBarWidth
        val labelsRightPosition = innerFrame.right - halfBarWidth
        val widthBetweenLabels = (labelsRightPosition - labelsLeftPosition) / (xLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX = labelsLeftPosition + (widthBetweenLabels * index)
            dataPoint.screenPositionY =
                if (dataPoint.value >= 0f)
                    zeroPositionY - (positiveHeight * dataPoint.value / positiveScale) // Positive
                else zeroPositionY + (negativeHeight * dataPoint.value / negativeScale) // Negative
        }
    }

    private fun processZeroPositionY(
        innerTop: Float = innerFrame.top,
        innerBottom: Float = innerFrame.bottom,
        scaleRange: Float = chartConfiguration.scale.size
    ): Float {
        val chartHeight = innerBottom - innerTop
        return innerFrame.top + (chartHeight * chartConfiguration.scale.max / scaleRange)
    }
}
