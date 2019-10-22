package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultDonutAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.DonutDataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.extensions.toDonutDataPoint

class DonutChartRenderer(
    val view: ChartContract.DonutView,
    private var animation: DefaultDonutAnimation
) : ChartContract.DonutRenderer {

    private var innerFrameWithStroke: Frame = Frame(0f, 0f, 0f, 0f)

    private var datapoint: DonutDataPoint = DonutDataPoint(0f, 0f)

    private lateinit var chartConfiguration: DonutChartConfiguration

    override fun preDraw(configuration: DonutChartConfiguration): Boolean {

        chartConfiguration = configuration

        val left =
            configuration.paddings.left + configuration.thickness / 2
        val top =
            configuration.paddings.top + configuration.thickness / 2
        val right =
            configuration.width - configuration.paddings.right - configuration.thickness / 2
        val bottom =
            configuration.height - configuration.paddings.bottom - configuration.thickness / 2
        innerFrameWithStroke = Frame(left, top, right, bottom)

        datapoint.screenDegrees = datapoint.value * fullDegrees / chartConfiguration.total

        animation.animateFrom(listOf(datapoint)) {
            view.postInvalidate()
        }

        return true
    }

    override fun draw() {
        view.drawBackground(innerFrameWithStroke)
        view.drawArc(datapoint.screenDegrees, innerFrameWithStroke)
    }

    override fun render(value: Float) {
        datapoint = value.toDonutDataPoint()
        view.postInvalidate()
    }

    override fun anim(value: Float, animation: ChartAnimation) {
        datapoint = value.toDonutDataPoint()
        view.postInvalidate()
    }

    companion object {
        private const val fullDegrees = 360
    }
}
