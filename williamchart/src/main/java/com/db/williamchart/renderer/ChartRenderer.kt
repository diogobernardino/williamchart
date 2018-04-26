package com.db.williamchart.renderer

import com.db.williamchart.Painter
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import java.lang.IllegalArgumentException

class ChartRenderer(private val painter: Painter) {

    private var frameLeft : Int = 0

    private var frameTop : Int = 0

    private var frameRight : Int = 0

    private var frameBottom : Int = 0

    var labelSize : Float = 60F

    var data : ChartSet? = null

    var xLabels : List<ChartLabel>? = null

    fun preDraw(width: Int,
                height: Int,
                paddingLeft: Int,
                paddingTop: Int,
                paddingRight: Int,
                paddingBottom: Int) {

        if (data!!.entries.size <= 1) throw IllegalArgumentException("A chart needs more than one entry.")

        frameLeft = paddingLeft
        frameTop = paddingTop
        frameRight = width - paddingRight
        frameBottom = height - paddingBottom

        val firstLabelCenter = painter.measureTextCenter(data!!.entries.first().label, labelSize)
        val lastLabelCenter = painter.measureTextCenter(data!!.entries.last().label, labelSize)
        val stepX = (frameRight - frameLeft - firstLabelCenter - lastLabelCenter)/
                (data!!.entries.size - 1)

        xLabels = data!!.entries.mapIndexed{index, entry ->
            ChartLabel(entry.label,
                    frameLeft + firstLabelCenter + stepX * index,
                    frameBottom - 10F) }

        processEntries()
    }

    private fun processEntries() {

        val (min, max) = findBorderValues(data!!.entries)

        data!!.entries.forEachIndexed { index, entry ->
            entry.x = xLabels!![index].x
            entry.y = frameBottom - ((frameBottom - frameTop) * (entry.value - min) / (max - min))
        }
    }

    private fun findBorderValues(entries: MutableList<ChartEntry>): FloatArray {

        var max = Integer.MIN_VALUE.toFloat()
        var min = Integer.MAX_VALUE.toFloat()

        entries.forEach {
            if (it.value >= max) max = it.value
            if (it.value <= min) min = it.value
        }

        if (min == max) max += 1f  // All given values are equal

        return floatArrayOf(min, max)
    }

}