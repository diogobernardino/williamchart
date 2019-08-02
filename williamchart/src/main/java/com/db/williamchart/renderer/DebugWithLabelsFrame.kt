package com.db.williamchart.renderer

import com.db.williamchart.Painter
import com.db.williamchart.data.Frame
import com.db.williamchart.data.Label

class DebugWithLabelsFrame {

    operator fun invoke(
        painter: Painter,
        xLabels: List<Label>,
        yLabels: List<Label>,
        labelsSize: Float
    ): List<Frame> {
        val labelHeight = painter.measureLabelHeight(labelsSize)
        return xLabels.map {
            val labelHalftWidth = painter.measureLabelWidth(it.label, labelsSize) / 2
            Frame(
                left = it.screenPositionX - labelHalftWidth,
                top = it.screenPositionY - labelHeight,
                right = it.screenPositionX + labelHalftWidth,
                bottom = it.screenPositionY
            )
        } + yLabels.map {
            val labelHalftWidth = painter.measureLabelWidth(it.label, labelsSize) / 2
            Frame(
                left = it.screenPositionX - labelHalftWidth,
                top = it.screenPositionY - labelHeight,
                right = it.screenPositionX + labelHalftWidth,
                bottom = it.screenPositionY
            )
        }
    }
}