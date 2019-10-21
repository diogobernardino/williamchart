package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.Frame

class DonutChartRenderer(val view: ChartContract.DonutView) : ChartContract.DonutRenderer {

    private var innerFrameWithStroke: Frame = Frame(0f, 0f, 0f, 0f)

    private var data = listOf<Float>()

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
        return true
    }

    override fun draw() {
        view.drawBackground(innerFrameWithStroke)
        view.drawArc(
            data.map { it * fullDegrees / chartConfiguration.total }.first(), innerFrameWithStroke
        )
    }

    override fun render(datapoints: List<Float>) {
        data = datapoints
        view.postInvalidate()
    }

    override fun anim(datapoints: List<Float>, animation: ChartAnimation) {
        data = datapoints
        view.postInvalidate()
    }

    companion object {
        private val fullDegrees = 360f
    }
}
