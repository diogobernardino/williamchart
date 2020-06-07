package com.db.williamchart.renderer.executor

import com.db.williamchart.data.Frame

class DefineVerticalBarsClickableFrames {

    operator fun invoke(
        innerFrame: Frame,
        datapointsCoordinates: List<Pair<Float, Float>>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.right - innerFrame.left - (datapointsCoordinates.size + 1)) /
                datapointsCoordinates.size / 2

        return datapointsCoordinates.map {
            Frame(
                left = it.first - halfBarWidth,
                top = it.second,
                right = it.first + halfBarWidth,
                bottom = innerFrame.bottom
            )
        }
    }
}
