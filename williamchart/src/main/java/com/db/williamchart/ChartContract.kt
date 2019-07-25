package com.db.williamchart

import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.view.ChartView.Axis

interface ChartContract {

    interface View {

        fun drawLabels(xLabels: List<ChartLabel>)

        fun drawData(
            innerFrameLeft: Float,
            innerFrameTop: Float,
            innerFrameRight: Float,
            innerFrameBottom: Float,
            entries: List<ChartEntry>
        )

        fun postInvalidate()
    }

    interface Renderer {

        fun preDraw(
            width: Int,
            height: Int,
            paddingLeft: Int,
            paddingTop: Int,
            paddingRight: Int,
            paddingBottom: Int,
            axis: Axis,
            labelsSize: Float
        ): Boolean

        fun draw()

        fun render(entries: HashMap<String, Float>)

        fun anim(entries: HashMap<String, Float>, animation: ChartAnimation)
    }
}