package com.db.williamchart.renderer.executor

import com.db.williamchart.data.DataPoint
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Scale
import kotlin.math.max
import kotlin.math.min

class GetVerticalBarFrames {

    operator fun invoke(
        innerFrame: Frame,
        scale: Scale,
        spacingBetweenBars: Float,
        data: List<DataPoint>
    ): List<Frame> {
        val halfBarWidth =
            (innerFrame.right - innerFrame.left - (data.size + 1) * spacingBetweenBars) /
                    data.size / 2
        val chartHeight = innerFrame.bottom - innerFrame.top

        return data.map {
            val y1 = it.screenPositionY
            val y2 = chartHeight * scale.max / scale.size

            Frame(
                left = it.screenPositionX - halfBarWidth,
                top = min(y1, y2),
                right = it.screenPositionX + halfBarWidth,
                bottom = max(y1, y2)
            )
        }
    }
}
