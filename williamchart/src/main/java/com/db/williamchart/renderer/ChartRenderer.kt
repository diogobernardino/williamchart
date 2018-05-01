package com.db.williamchart.renderer

import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.data.ChartEntry
import com.db.williamchart.data.ChartLabel
import com.db.williamchart.data.ChartSet
import java.lang.IllegalArgumentException

class ChartRenderer(private val view: ChartContract.View,
                    private val painter: Painter) : ChartContract.Renderer{

    private var frameLeft : Int = 0

    private var frameTop : Int = 0

    private var frameRight : Int = 0

    private var frameBottom : Int = 0

    private var data : ChartSet? = null

    private var xLabels : List<ChartLabel>? = null

    var labelSize : Float = 60F

    override fun preDraw(width: Int,
                height: Int,
                paddingLeft: Int,
                paddingTop: Int,
                paddingRight: Int,
                paddingBottom: Int) {

        if (data == null) return
        if (data!!.entries.size <= 1) throw IllegalArgumentException("A chart needs more than one entry.")

        frameLeft = paddingLeft
        frameTop = paddingTop
        frameRight = width - paddingRight
        frameBottom = height - paddingBottom

        val firstLabelCenter = painter.measureLabel(data!!.entries.first().label, labelSize)
        val lastLabelCenter = painter.measureLabel(data!!.entries.last().label, labelSize)
        val stepX = (frameRight - frameLeft - firstLabelCenter - lastLabelCenter)/
                (data!!.entries.size - 1)

        xLabels = data!!.entries.mapIndexed{index, entry ->
            ChartLabel(entry.label,
                    frameLeft + firstLabelCenter + stepX * index,
                    frameBottom.toFloat()) }

        processEntries()
    }

    override fun draw() {

        if (xLabels == null || data == null) return

        view.drawLabels(xLabels!!)
        view.drawData(data!!)
    }

    override fun add(set: ChartSet) {
        data = set
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