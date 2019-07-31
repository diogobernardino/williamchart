package com.db.williamchart

import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.AxisType
import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label

interface ChartContract {

    interface View {

        fun drawLabels(xLabels: List<Label>)

        fun drawData(innerFrame: Frame, entries: List<DataPoint>)

        fun postInvalidate()

        fun drawDebugFrame(outerFrame: Frame, innerFrame: Frame)
    }

    interface Renderer {

        fun preDraw(
            width: Int,
            height: Int,
            paddingLeft: Int,
            paddingTop: Int,
            paddingRight: Int,
            paddingBottom: Int,
            axis: AxisType,
            labelsSize: Float
        ): Boolean

        fun draw()

        fun render(entries: HashMap<String, Float>)

        fun anim(entries: HashMap<String, Float>, animation: ChartAnimation)
    }
}