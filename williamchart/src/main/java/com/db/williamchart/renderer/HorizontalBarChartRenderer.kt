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
import com.db.williamchart.renderer.executor.DefineHorizontalBarsClickableFrames
import com.db.williamchart.renderer.executor.GetHorizontalBarBackgroundFrames
import com.db.williamchart.renderer.executor.GetHorizontalBarFrames
import com.db.williamchart.renderer.executor.MeasureHorizontalBarChartPaddings

class HorizontalBarChartRenderer(
    private val view: ChartContract.BarView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var data = emptyList<DataPoint>()

    private var zeroPositionX: Float = 0.0f

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: BarChartConfiguration

    private lateinit var xLabels: List<Label>

    private lateinit var yLabels: List<Label>

    private lateinit var barsBackgroundFrames: List<Frame>

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        chartConfiguration = configuration as BarChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration = chartConfiguration.copy(scale = data.toBarScale())

        val scaleStep = chartConfiguration.scale.size / RendererConstants.defaultScaleNumberOfSteps
        xLabels = List(RendererConstants.defaultScaleNumberOfSteps + 1) {
            val scaleValue = chartConfiguration.scale.min + scaleStep * it
            Label(
                label = chartConfiguration.labelsFormatter(scaleValue),
                screenPositionX = 0F,
                screenPositionY = 0F
            )
        }
        yLabels = data.toLabels()

        val yLongestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureHorizontalBarChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            xLastLabelWidth = painter.measureLabelWidth(
                xLabels.last().label,
                chartConfiguration.labelsSize
            ),
            yLongestLabelWidth = yLongestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        zeroPositionX =
            processZeroPositionX(
                innerLeft = innerFrame.left,
                innerRight = innerFrame.right,
                scaleRange = chartConfiguration.scale.size
            )

        placeLabelsX(innerFrame)
        placeLabelsY(outerFrame, innerFrame)
        placeDataPoints(innerFrame)

        barsBackgroundFrames =
            GetHorizontalBarBackgroundFrames()(
                innerFrame,
                chartConfiguration.barsSpacing,
                data
            )

        animation.animateFrom(zeroPositionX, data) { view.postInvalidate() }

        return false
    }

    override fun draw() {

        if (data.isEmpty()) return

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabels(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabels(yLabels)

        if (chartConfiguration.barsBackgroundColor != -1)
            view.drawBarsBackground(barsBackgroundFrames)

        view.drawBars(
            GetHorizontalBarFrames()(
                innerFrame,
                zeroPositionX,
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
                    DefineHorizontalBarsClickableFrames()(
                        innerFrame,
                        data.map { Pair(it.screenPositionX, it.screenPositionY) }
                    ) +
                    Frame(zeroPositionX, innerFrame.top, zeroPositionX, innerFrame.bottom)
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
            DefineHorizontalBarsClickableFrames()(
                innerFrame,
                data.map { Pair(it.screenPositionX, it.screenPositionY) }
            ).indexOfFirst { it.contains(x, y) }

        return if (index != -1)
            Triple(index, data[index].screenPositionX, data[index].screenPositionY)
        else Triple(-1, -1f, -1f)
    }

    override fun processTouch(x: Float?, y: Float?): Triple<Int, Float, Float> = processClick(x, y)

    private fun placeLabelsX(innerFrame: Frame) {

        val widthBetweenLabels =
            (innerFrame.right - innerFrame.left) / RendererConstants.defaultScaleNumberOfSteps
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                painter.measureLabelAscent(chartConfiguration.labelsSize) +
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
                    painter.measureLabelWidth(label.label, chartConfiguration.labelsSize) / 2
            label.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index +
                    painter.measureLabelDescent(chartConfiguration.labelsSize)
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        // Chart upper part with positive points
        val positiveWidth = innerFrame.right - zeroPositionX
        val positiveScale = chartConfiguration.scale.max

        // Chart bottom part with negative points
        val negativeWidth = zeroPositionX - innerFrame.left
        val negativeScale = chartConfiguration.scale.min

        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX =
                if (dataPoint.value >= 0f)
                    zeroPositionX + (positiveWidth * dataPoint.value / positiveScale) // Positive
                else zeroPositionX - (negativeWidth * dataPoint.value / negativeScale) // Negative
            dataPoint.screenPositionY = labelsBottomPosition - heightBetweenLabels * index
        }
    }

    private fun processZeroPositionX(
        innerLeft: Float,
        innerRight: Float,
        scaleRange: Float
    ): Float {
        val chartWidth = innerRight - innerLeft
        return innerRight - (chartWidth * chartConfiguration.scale.max / scaleRange)
    }
}
