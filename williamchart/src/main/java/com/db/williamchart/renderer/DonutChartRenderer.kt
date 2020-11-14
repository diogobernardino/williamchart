package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.configuration.DonutChartConfiguration
import com.db.williamchart.data.DonutDataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.extensions.toDonutDataPoint

class DonutChartRenderer(
    val view: ChartContract.DonutView,
    private var animation: ChartAnimation<DonutDataPoint>
) : ChartContract.DonutRenderer {

    private var innerFrameWithStroke: Frame = Frame(0f, 0f, 0f, 0f)

    private var datapoints = emptyList<DonutDataPoint>()

    private lateinit var chartConfiguration: DonutChartConfiguration

    override fun preDraw(configuration: DonutChartConfiguration): Boolean {

        chartConfiguration = configuration

        if (chartConfiguration.colorsSize < datapoints.size)
            throw IllegalArgumentException(
                "Number of datapoints is ${datapoints.size} " +
                    "but only ${chartConfiguration.colorsSize} color(s) provided."
            )

        val left =
            configuration.paddings.left + configuration.thickness / 2
        val top =
            configuration.paddings.top + configuration.thickness / 2
        val right =
            configuration.width - configuration.paddings.right - configuration.thickness / 2
        val bottom =
            configuration.height - configuration.paddings.bottom - configuration.thickness / 2
        innerFrameWithStroke = Frame(left, top, right, bottom)

        datapoints.forEach { it.screenDegrees = it.value * fullDegrees / chartConfiguration.total }
        datapoints = datapoints.sortedByDescending { it.screenDegrees }

        animation.animateFrom(ignoreStartPosition, datapoints) {
            view.postInvalidate()
        }

        return true
    }

    override fun draw() {

        if (chartConfiguration.barBackgroundColor != -1)
            view.drawBackground(innerFrameWithStroke)

        view.drawArc(datapoints.map { it.screenDegrees }, innerFrameWithStroke)
    }

    override fun render(values: List<Float>) {
        val valuesOffset = values.generateValuesOffset()
        datapoints = values.mapIndexed { index, value ->
            value.toDonutDataPoint(valuesOffset[index])
        }
        view.postInvalidate()
    }

    override fun anim(values: List<Float>, animation: ChartAnimation<DonutDataPoint>) {
        val valuesOffset = values.generateValuesOffset()
        datapoints = values.mapIndexed { index, value ->
            value.toDonutDataPoint(valuesOffset[index])
        }
        this.animation = animation
        view.postInvalidate()
    }

    private fun List<Float>.generateValuesOffset(): List<Float> {
        val valuesOffset: MutableList<Float> = mutableListOf()
        this.forEachIndexed { index, _ ->
            val offset = if (index == 0) 0f else valuesOffset[index - 1] + this[index - 1]
            valuesOffset.add(index, offset)
        }
        return valuesOffset.toList()
    }

    companion object {
        private const val fullDegrees = 360
        private const val ignoreStartPosition = -1234f
    }
}
