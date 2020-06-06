package com.db.williamchart.renderer.executor

import com.db.williamchart.data.Frame

class DefineDataPointsClickableFrames {

    operator fun invoke(
        innerFrame: Frame,
        datapointsCoordinates: List<Pair<Float, Float>>,
        clickableRadius: Int
    ): List<Frame> {
        val halfWidthBetweenLabels =
            ((innerFrame.right - innerFrame.left) / (datapointsCoordinates.size - 1)) / 2

        return List(datapointsCoordinates.size) {

            val left =
                if (clickableRadius > halfWidthBetweenLabels)
                    datapointsCoordinates[it].first - halfWidthBetweenLabels
                else datapointsCoordinates[it].first - clickableRadius

            val top = datapointsCoordinates[it].second - clickableRadius

            val right =
                if (clickableRadius > halfWidthBetweenLabels)
                    datapointsCoordinates[it].first + halfWidthBetweenLabels
                else datapointsCoordinates[it].first + clickableRadius

            val bottom = datapointsCoordinates[it].second + clickableRadius

            Frame(left, top, right, bottom)
        }
    }
}
