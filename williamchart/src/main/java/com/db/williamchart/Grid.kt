package com.db.williamchart

import android.graphics.Canvas
import com.db.williamchart.data.Frame

interface Grid {
    fun preDraw()
    fun draw(
        canvas: Canvas,
        innerFrame: Frame,
        xLabelsPositions: List<Float>,
        yLabelsPositions: List<Float>
    )
}
