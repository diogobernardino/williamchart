package com.db.williamchart

import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.ChartConfiguration
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label

interface ChartContract {

    interface View {

        fun drawLabels(xLabels: List<Label>)

        fun drawData(innerFrame: Frame, entries: List<DataPoint>)

        fun postInvalidate()

        fun drawDebugFrame(
            outerFrame: Frame,
            innerFrame: Frame,
            labelsFrame: List<Frame>
        )
    }

    interface LineView {

        fun drawLabels(xLabels: List<Label>)

        fun drawLine(entries: List<DataPoint>)

        fun drawLineBackground(innerFrame: Frame, entries: List<DataPoint>)

        fun drawPoints(entries: List<DataPoint>)

        fun postInvalidate()

        fun drawDebugFrame(
            outerFrame: Frame,
            innerFrame: Frame,
            labelsFrame: List<Frame>
        )
    }

    interface Renderer {

        fun preDraw(chartConfiguration: ChartConfiguration): Boolean

        fun draw()

        fun render(entries: LinkedHashMap<String, Float>)

        fun anim(entries: LinkedHashMap<String, Float>, animation: ChartAnimation)
    }
}
