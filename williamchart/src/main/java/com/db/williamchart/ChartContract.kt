package com.db.williamchart

import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import com.db.williamchart.view.ChartView.Axis

interface ChartContract {

    interface View {

        fun drawLabels(xLabels: List<ChartLabel>)

        fun drawData(
            innerFrameLeft: Float,
            innerFrameTop: Float,
            innerFrameRight: Float,
            innerFrameBottom: Float,
            data: ChartSet
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

        fun render()

        fun anim(animation: ChartAnimation)

        fun add(set: ChartSet)
    }
}