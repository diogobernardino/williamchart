package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.DonutChartConfiguration
import com.db.williamchart.data.Frame

class DonutChartRenderer(val view: ChartContract.DonutView) : ChartContract.DonutRenderer {

    private lateinit var innerFrame: Frame

    override fun preDraw(configuration: DonutChartConfiguration): Boolean {

        val left = configuration.paddings.left
        val top = configuration.paddings.top
        val right = configuration.width - configuration.paddings.right
        val bottom = configuration.height - configuration.paddings.bottom
        innerFrame = Frame(left, top, right, bottom)
        return true
    }

    override fun draw() {
        view.drawArc(120f, innerFrame)
    }

    override fun render(datapoints: List<Float>) {
    }

    override fun anim(datapoints: List<Float>, animation: ChartAnimation) {
    }
}
