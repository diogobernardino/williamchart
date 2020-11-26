package com.db.williamchart.plugin

import android.graphics.Canvas
import android.graphics.Paint
import com.db.williamchart.Labels
import com.db.williamchart.data.Label

class AxisLabels : Labels {
    override fun draw(canvas: Canvas, paint: Paint, xLabels: List<Label>) {
        xLabels.forEach {
            if (it.label.contains("\n")) {
                val labelLines = it.label.split("\n")
                labelLines.forEachIndexed { index, labelLine ->
                    canvas.drawText(
                        labelLine,
                        it.screenPositionX,
                        it.screenPositionY + index * paint.textSize,
                        paint
                    )
                }
            } else {
                canvas.drawText(
                    it.label,
                    it.screenPositionX,
                    it.screenPositionY,
                    paint
                )
            }
        }
    }
}
