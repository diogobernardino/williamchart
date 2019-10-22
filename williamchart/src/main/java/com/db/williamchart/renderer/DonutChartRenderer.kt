package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.animation.DefaultDonutAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.DonutDataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.extensions.toDonutDataPoints

class DonutChartRenderer(
    val view: ChartContract.DonutView,
    private var animation: DefaultDonutAnimation
) : ChartContract.DonutRenderer {

    private var innerFrameWithStroke: Frame = Frame(0f, 0f, 0f, 0f)

    private var data = emptyList<DonutDataPoint>()

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

        data.forEach {
            it.screenDegrees = it.value * fullDegrees / chartConfiguration.total
        }

        animation.animateFrom(data) {
            view.postInvalidate()
        }

        return true
    }

    override fun draw() {
        view.drawBackground(innerFrameWithStroke)
        view.drawArc(data.map { it.screenDegrees }.first(), innerFrameWithStroke)
    }

    override fun render(datapoints: List<Float>) {
        data = datapoints.toDonutDataPoints()
        view.postInvalidate()
    }

    override fun anim(datapoints: List<Float>, animation: ChartAnimation) {
        data = datapoints.toDonutDataPoints()
        view.postInvalidate()
    }

    companion object {
        private const val fullDegrees = 360
    }
}
