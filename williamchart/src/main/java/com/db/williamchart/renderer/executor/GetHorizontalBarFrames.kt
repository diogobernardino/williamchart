package com.db.williamchart.renderer.executor

import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame

class GetHorizontalBarFrames {

    operator fun invoke(
        innerFrame: Frame,
        zeroPositionX: Float,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (data.size + 1) * spacingBetweenBars) /
                data.size / 2

        return data.map {
            Frame(
                left = zeroPositionX,
                top = it.screenPositionY - halfBarWidth,
                right = it.screenPositionX,
                bottom = it.screenPositionY + halfBarWidth
            )
        }
    }
}
