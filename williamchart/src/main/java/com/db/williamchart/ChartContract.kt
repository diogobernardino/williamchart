package com.db.williamchart

import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.configuration.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.configuration.DonutChartConfiguration
import com.db.williamchart.data.DonutDataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label

interface ChartContract {

    interface LineView {
        fun postInvalidate()
        fun drawLabels(xLabels: List<Label>)
        fun drawLine(points: List<DataPoint>)
        fun drawLineBackground(innerFrame: Frame, points: List<DataPoint>)
        fun drawPoints(points: List<DataPoint>)
        fun drawDebugFrame(frames: List<Frame>)
    }

    interface BarView {
        fun postInvalidate()
        fun drawLabels(xLabels: List<Label>)
        fun drawBars(frames: List<Frame>)
        fun drawBarsBackground(frames: List<Frame>)
        fun drawDebugFrame(frames: List<Frame>)
    }

    interface DonutView {
        fun postInvalidate()
        fun drawArc(degrees: List<Float>, innerFrame: Frame)
        fun drawBackground(innerFrame: Frame)
        fun drawDebugFrame(innerFrame: Frame)
    }

    interface Renderer {
        fun preDraw(configuration: ChartConfiguration): Boolean
        fun draw()
        fun render(entries: LinkedHashMap<String, Float>)
        fun anim(entries: LinkedHashMap<String, Float>, animation: ChartAnimation<DataPoint>)
        fun processClick(x: Float?, y: Float?): Int
        fun processTouch(x: Float?, y: Float?): Int
    }

    interface DonutRenderer {
        fun preDraw(configuration: DonutChartConfiguration): Boolean
        fun draw()
        fun render(values: List<Float>)
        fun anim(values: List<Float>, animation: ChartAnimation<DonutDataPoint>)
    }
}
