package com.db.williamchart.renderer

import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartSet
import java.lang.IllegalArgumentException

class ChartRenderer {

    var frameLeft : Int = 0

    var frameTop : Int = 0

    var frameRight : Int = 0

    var frameBottom : Int = 0

    var data: ChartSet? = null


    fun draw(width: Int, height: Int): ChartSet? {

        if (data == null) return null

        if (data!!.entries.size <= 1) throw IllegalArgumentException("A chart needs more than one entry.")

        frameLeft = 0
        frameTop = 0
        frameRight = width
        frameBottom = height

        processScreenCoordinates()

        return data
    }

    private fun processScreenCoordinates() {

        val nX = data!!.entries.size
        val stepX = (frameRight - frameLeft) / (nX - 1)
        var cursorX = frameLeft

        val yBorders = findBorderValues(data!!.entries)

        for (entry in data?.entries!!) {

            entry.x = cursorX.toFloat()
            cursorX += stepX

            entry.y = frameBottom -
                    ((frameBottom - frameTop) * (entry.value - yBorders[0])
                            / (yBorders[1] - yBorders[0]))
        }
    }

    private fun findBorderValues(entries: MutableList<ChartEntry>): FloatArray {

        var max = Integer.MIN_VALUE.toFloat()
        var min = Integer.MAX_VALUE.toFloat()

        for (e in entries) {
            if (e.value >= max) max = e.value
            if (e.value <= min) min = e.value
        }

        if (min == max) max += 1f  // All given values are equal

        return floatArrayOf(min, max)
    }

}