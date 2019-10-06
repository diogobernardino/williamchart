package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.ChartConfiguration
import com.db.williamchart.data.DonutConfiguration

class DonutChartRenderer : ChartContract.Renderer {

    override fun preDraw(configuration: ChartConfiguration): Boolean {
        val donutChartConfiguration = configuration as DonutConfiguration
        return true
    }

    override fun draw() {
    }

    override fun render(entries: LinkedHashMap<String, Float>) {
    }

    override fun anim(entries: LinkedHashMap<String, Float>, animation: ChartAnimation) {
    }
}
