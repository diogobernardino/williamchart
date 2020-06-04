package com.db.williamchart.renderer.executor

import com.db.williamchart.data.Frame

class DefineDataPointsClickableFrames {

    operator fun invoke(
        xCoordinates: List<Float>,
        yCoordinates: List<Float>,
        clickableRadius: Int
    ): List<Frame> =
        List(xCoordinates.size) {
            val left = xCoordinates[it] - clickableRadius
            val right = xCoordinates[it] + clickableRadius
            val top = yCoordinates[it] - clickableRadius
            val bottom = yCoordinates[it] + clickableRadius
            Frame(left, top, right, bottom)
        }
}
