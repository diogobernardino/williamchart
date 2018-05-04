package com.db.williamchart

import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet

interface ChartContract {

    interface View {

        fun drawLabels(xLabels : List<ChartLabel>)

        fun drawData(innerFrameLeft: Float,
                     innerFrameTop: Float,
                     innerFrameRight: Float,
                     innerFrameBottom: Float,
                     data: ChartSet)

        fun postInvalidate()
    }

    interface Renderer {

        fun preDraw(width: Int,
                    height: Int,
                    paddingLeft: Int,
                    paddingTop: Int,
                    paddingRight: Int,
                    paddingBottom: Int)

        fun draw()

        fun animate()

        fun add(set: ChartSet)
    }

}